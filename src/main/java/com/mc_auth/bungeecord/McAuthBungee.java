package com.mc_auth.bungeecord;

import com.google.common.io.BaseEncoding;
import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;
import de.sprax2013.lime.bungeecord.LimeDevUtilityBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class McAuthBungee extends Plugin implements Listener {
    static McAuthBungee instance;

    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final String secretSalt = TimeBasedOneTimePasswordUtil.generateBase32Secret();

    private MessageDigest sha256;
    private DatabaseUtils dbUtils;

    @Override
    public void onEnable() {
        instance = this;

        // Init LimeDevUtility
        LimeDevUtilityBungee.init(this);

        // Load config.yml
        if (!Settings.reload()) {
            getLogger().warning("Could not create '" + Objects.requireNonNull(Settings.config.getFile()).getPath() + "'");
        }

        /* Init database and test connection */
        this.dbUtils = new DatabaseUtils();

        boolean validDbConnection = false;
        try {
            this.dbUtils.getConnection();
            validDbConnection = this.dbUtils.isValid();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (!validDbConnection) {
                getLogger().warning("Could not connect to database! Check '" +
                        Objects.requireNonNull(Settings.config.getFile()).getPath() +
                        "' and restart BungeeCord (If this is a temporary issue, users trying to connect, will cause a database reconnect)");
            }
        }

        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onLogin(LoginEvent e) {
        e.setCancelled(true);
        e.registerIntent(this);

        this.pool.execute(() -> {
            try {
                this.dbUtils.updateAccount(e.getConnection().getUniqueId(), e.getConnection().getName());

                boolean isInAlternateOtpMode = e.getConnection().getVirtualHost() != null &&
                        e.getConnection().getVirtualHost().getHostName().equalsIgnoreCase("mc-alt.mc-auth.com");
                String codeStr;
                if (isInAlternateOtpMode) {
                    codeStr = determineAlternateOtp(e.getConnection().getUniqueId());
                } else {
                    codeStr = determineOtp(e.getConnection().getUniqueId());
                }

                e.setReason(TextComponent.fromLegacy(
                        ChatColor.translateAlternateColorCodes('&',
                                Objects.requireNonNull(Settings.KICK_SUCCESS.getValueAsString())
                                        .replace("${OTP}", codeStr)
                                        .replace("${Name}", e.getConnection().getName())
                                        .replace("${UUID}", e.getConnection().getUniqueId().toString())
                        )
                ));

                getLogger().info(e.getConnection().getName() + " successfully requested an One-Time-Password");
            } catch (GeneralSecurityException | SQLException ex) {
                ex.printStackTrace();
                getLogger().warning("Could not generate an One-Time-Password for " + e.getConnection().getName() +
                        " (" + e.getConnection().getUniqueId().toString() + ")");

                // Notify the client about an error
                e.setReason(TextComponent.fromLegacy(
                        ChatColor.translateAlternateColorCodes('&',
                                Objects.requireNonNull(Settings.KICK_ERROR.getValueAsString())
                                        .replace("${Name}", e.getConnection().getName())
                                        .replace("${UUID}", e.getConnection().getUniqueId().toString())
                        )
                ));
            }

            e.completeIntent(instance);
        });
    }

    private String determineOtp(UUID uuid) throws SQLException, GeneralSecurityException {
        int code = this.dbUtils.getCode(uuid);
        if (code == -1) {
            code = TimeBasedOneTimePasswordUtil.generateCurrentNumber(generateSecret(uuid), 6);
            this.dbUtils.setCode(uuid, code);
        }

        return formatOTP(code);
    }

    private String determineAlternateOtp(UUID uuid) throws SQLException, GeneralSecurityException {
        String codePrefix = generateCodePrefix();
        int code = TimeBasedOneTimePasswordUtil.generateCurrentNumber(generateSecret(uuid), 6);
        this.dbUtils.setAlternateCode(uuid, codePrefix, code);

        return codePrefix + " " + formatOTP(code);
    }

    private String generateCodePrefix() {
        String letters = "ABCDEFGHJKMNOPQRSTUVWXYZ";
        return String.valueOf(letters.charAt(new SecureRandom().nextInt(letters.length())));
    }

    /**
     * Takes a 1 to 6 digit long number and formats it
     * into {@code "### ###"} with leading zeros if needed
     *
     * @param num The number to format
     * @return The formatted number as a {@link String}, never null
     */
    private static @NotNull String formatOTP(long num) {
        String numStr = Long.toString(num);

        if (numStr.length() > 6) {
            throw new IllegalArgumentException("Argument num may not consist of more than 6 digits");
        }

        StringBuilder sb = new StringBuilder(7);

        if (numStr.length() != 6) {
            int zeroCount = 6 - numStr.length();
            sb.append("000000", 0, zeroCount);
        }

        sb.append(numStr);
        sb.insert(3, ' ');

        return sb.toString();
    }

    private String generateSecret(UUID uuid) throws NoSuchAlgorithmException {
        if (this.sha256 == null) {
            this.sha256 = MessageDigest.getInstance("SHA-256");
        }

        return BaseEncoding.base32().encode(this.sha256.digest((uuid.toString() + this.secretSalt).getBytes()));
    }
}
