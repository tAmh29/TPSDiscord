package me.tam.TPSDiscord;

import me.tam.TPSDiscord.utils.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import me.tam.TPSDiscord.utils.TPSutils;

public final class TPSDiscord extends JavaPlugin {

    private static final double TPS_THRESHOLD = 17;
    private String botToken;
    private String channelId;
    private String watchUrl;
    private JDA jda;
    final private TPSutils TPSutils = new TPSutils();
    private boolean isTPSWarningSent = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        if (botToken == null || botToken.isEmpty() || channelId == null || channelId.isEmpty()) {
            getLogger().severe("Bot token or channel ID is missing in the config!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("TPSDiscord plugin is starting...");

        try {
            CommandHandler commandHandler = new CommandHandler();
            jda = JDABuilder.createDefault(botToken)
                    .addEventListeners(commandHandler)
                    .build();
            commandHandler.registerCommands(jda);
            jda.awaitReady();

            getLogger().info("Discord bot successfully connected!");
        } catch (Exception e) {
            getLogger().severe("Error during bot initialization: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            double currentTPS = Bukkit.getServer().getTPS()[0];
            if (currentTPS < TPS_THRESHOLD) {
                if (!isTPSWarningSent) {
                    TPSutils.sendTPSWarning(currentTPS, channelId, jda);
                    isTPSWarningSent = true;
                }
            } else {
                // Reset the warning flag if TPS is back to normal
                isTPSWarningSent = false;
            }
        }, 0L, 1200L);

        getLogger().info("TPSDiscord plugin has started successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TPSDiscord plugin is shutting down...");
        if (jda != null) {
            try {
                jda.shutdown();
            } catch (Exception e) {
                getLogger().warning("Failed to send shutdown message: " + e.getMessage());
            }
        }
        getLogger().info("TPSDiscord plugin has stopped!");
    }

    private void loadConfig() {
        botToken = getConfig().getString("bot-token");
        channelId = getConfig().getString("channel-id");
    }

}