package me.tam.TPSDiscord;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import me.tam.TPSDiscord.utils.TPSutils;
import me.tam.TPSDiscord.Command.TPSCommand;


public final class TPSDiscord extends JavaPlugin {

    private static final double TPS_THRESHOLD = 19;
    private String botToken;
    private String channelId;
    private JDA jda;
    final private TPSutils TPSutils = new TPSutils();
    private boolean isTPSWarningSent = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Load the bot token and channel ID from the config
        botToken = getConfig().getString("bot-token");
        channelId = getConfig().getString("channel-id");

        // Plugin startup logic
        getLogger().info("TPSDiscord plugin has started!");

        try {
            jda = JDABuilder.createDefault(botToken)
                    .addEventListeners(new TPSCommand())
                    .build();
            jda.awaitReady();
        } catch (Exception e) {
            getLogger().severe("Error during bot initialization: " + e.getMessage());
            e.printStackTrace();
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            double currentTPS = Bukkit.getServer().getTPS()[0];
            if (!isTPSWarningSent) {
                if (currentTPS < TPS_THRESHOLD) {
                    if (channelId != null) {
                        TPSutils.sendTPSWarning(currentTPS, channelId, jda);
                        isTPSWarningSent = true;
                    }
                }
            }
        }, 0L, 1200L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("TPSDiscord plugin has stopped!");
        jda.getTextChannelById(channelId).sendMessage("TPSDiscord has stopped!").queue();
        jda.shutdown();
    }
}





