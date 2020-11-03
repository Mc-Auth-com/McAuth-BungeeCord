package com.mc_auth.bungeecord;

import de.sprax2013.lime.configuration.Config;
import de.sprax2013.lime.configuration.ConfigEntry;
import de.sprax2013.lime.configuration.validation.IntEntryValidator;
import de.sprax2013.lime.configuration.validation.StringEntryValidator;

import java.io.File;

public class Settings {
    static final Config config = new Config(
            new File(McAuthBungee.instance.getDataFolder(), "config.yml"),
            "Mc-Auth.com (BungeeCord Plugin)\n\n" +
                    "Support: https://Sprax.me/Discord\n" +
                    "Updates and Information:\n" +
                    "Information for developers: https://github.com/Mc-Auth-com/McAuth-BungeeCord")
            .withCommentEntry("PostgreSQL", "This plugins requires access to the same database as the backend");

    public static final ConfigEntry PSQL_HOST = config.createEntry(
            "PostgreSQL.Host", "127.0.0.1")
            .setEntryValidator(StringEntryValidator.get());
    public static final ConfigEntry PSQL_PORT = config.createEntry(
            "PostgreSQL.Port", 5432)
            .setEntryValidator(IntEntryValidator.get());
    public static final ConfigEntry PSQL_DB = config.createEntry(
            "PostgreSQL.Database", "mcauth")
            .setEntryValidator(StringEntryValidator.get());

    public static final ConfigEntry PSQL_USER = config.createEntry(
            "PostgreSQL.Username", "user007")
            .setEntryValidator(StringEntryValidator.get());
    public static final ConfigEntry PSQL_PASSWORD = config.createEntry(
            "PostgreSQL.Password", "v3ryS3cr3t")
            .setEntryValidator(StringEntryValidator.get());

    public static final ConfigEntry KICK_SUCCESS = config.createEntry(
            "Kick.Success", "&3&nMc-Auth.com\n\n&6${Name}&e's authentication code\n\n&6${OTP}",
            "Available variables: ${Name}, ${UUID}, ${OTP}")
            .setEntryValidator(StringEntryValidator.get());
    public static final ConfigEntry KICK_ERROR = config.createEntry(
            "Kick.Error", "&3&nMc-Auth.com\n\n&cAn error occurred!" +
                    "\nPlease try again shortly or contact Sprax\n\n&3https://Sprax2013.de",
            "Available variables: ${Name}, ${UUID}")
            .setEntryValidator(StringEntryValidator.get());

    private Settings() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean reload() {
        return config.load() && config.save();
    }
}