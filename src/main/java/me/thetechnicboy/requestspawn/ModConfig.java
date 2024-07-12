package me.thetechnicboy.requestspawn;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final String CATEGORY_GENERAL = "general";
    public static ForgeConfigSpec CONFIG;
    public static ForgeConfigSpec.BooleanValue AUTH;
    public static ForgeConfigSpec.ConfigValue<String> USERNAME;
    public static ForgeConfigSpec.ConfigValue<String> PASSWORD;
    public static ForgeConfigSpec.IntValue PORT;

    static {
        ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        BUILDER.comment("General settings").push(CATEGORY_GENERAL);

        PORT = BUILDER
                .comment("Port number")
                .defineInRange("port", 25565, 1024, 65535);

        AUTH = BUILDER
                .comment("Enable authentication")
                .define("auth", true);

        USERNAME = BUILDER
                .comment("Username")
                .define("username", "defaultUsername");

        PASSWORD = BUILDER
                .comment("Password")
                .define("password", "defaultPassword");

        BUILDER.pop();
        CONFIG = BUILDER.build();
    }
}
