package com.mc_auth.bungeecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseUtils {
    private final String url;
    private final String user;
    private final String password;

    private Connection con;

    DatabaseUtils() {
        this.url = "jdbc:postgresql://" + Settings.PSQL_HOST.getValueAsString() +
                ":" + Settings.PSQL_PORT.getValueAsInt() +
                "/" + Settings.PSQL_DB.getValueAsString();

        this.user = Settings.PSQL_USER.getValueAsString();
        this.password = Settings.PSQL_PASSWORD.getValueAsString();
    }

    public void updateAccount(UUID uuid, String name) throws SQLException {
        try (PreparedStatement ps = getConnection()
                .prepareStatement("INSERT INTO accounts(id,name,last_login) VALUES(?,?,CURRENT_TIMESTAMP) " +
                        "ON CONFLICT(id) DO UPDATE SET name=?,last_login=CURRENT_TIMESTAMP;")) {
            ps.setString(1, uuid.toString().replace("-", ""));
            ps.setString(2, name);
            ps.setString(3, name);

            ps.executeUpdate();
        }
    }

    public int getCode(UUID uuid) throws SQLException {
        try (PreparedStatement ps = getConnection()
                .prepareStatement("SELECT code FROM otps WHERE account =? AND issued >= CURRENT_TIMESTAMP - INTERVAL '5 MINUTES';")) {
            ps.setString(1, uuid.toString().replace("-", ""));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("code");
            }
        }

        return -1;
    }

    public void setCode(UUID uuid, int code) throws SQLException {
        try (PreparedStatement ps = getConnection()
                .prepareStatement("INSERT INTO otps VALUES(?,?) ON CONFLICT(account) DO UPDATE SET (code,issued) = (?,CURRENT_TIMESTAMP);")) {
            ps.setString(1, uuid.toString().replace("-", ""));
            ps.setInt(2, code);
            ps.setInt(3, code);

            ps.executeUpdate();
        }
    }

    public boolean isValid() throws SQLException {
        return this.con != null && !this.con.isClosed() && this.con.isValid(3);
    }

    public Connection getConnection() throws SQLException {
        if (!isValid()) {
            this.con = null;

            this.con = DriverManager.getConnection(this.url, this.user, this.password);
        }

        return this.con;
    }
}