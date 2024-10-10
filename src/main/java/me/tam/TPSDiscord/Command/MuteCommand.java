package me.tam.TPSDiscord.Command;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class MuteCommand extends ListenerAdapter {

    private File configFile;
    private FileConfiguration config;
    private static final String JOIN_MESSAGE_PATH = "MinecraftPlayerJoinMessage.Enabled";
    private static final String LEAVE_MESSAGE_PATH = "MinecraftPlayerLeaveMessage.Enabled";


    public MuteCommand() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("DiscordSRV");
        if (plugin == null) {
            System.out.println("DiscordSRV not found");
            return;
        }

        configFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!configFile.exists()) {
            System.out.println("messages.yml not found");
            return;
        }
        loadConfig();
    }

    private void loadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("mute")) {
            event.deferReply().queue();

            boolean currentJoinState = config.getBoolean(JOIN_MESSAGE_PATH, true);
            boolean newJoinState = !currentJoinState;
            boolean currentLeaveState = config.getBoolean(LEAVE_MESSAGE_PATH, true);
            boolean newLeaveState = !currentLeaveState;

            boolean joinUpdated = toggleJoinAnnouncements(newJoinState);
            boolean leaveUpdated = toggleLeaveAnnouncements(newLeaveState);

            if (joinUpdated && leaveUpdated) {
                String response = newJoinState
                        ? "Join and leave announcements have been unmuted. :shaking_face:"
                        : "Join and leave announcements have been muted. :shushing_face:";
                event.getHook().sendMessage(response).queue();
            } else {
                event.getHook().sendMessage("Failed to update the configuration. Please check the server logs.").queue();
            }

        }
    }

    private boolean toggleLeaveAnnouncements(boolean enable) {
        config.set(LEAVE_MESSAGE_PATH, enable);
        try {
            config.save(configFile);

            // Attempt to reload DiscordSRV config
            Plugin discordSRV = Bukkit.getPluginManager().getPlugin("DiscordSRV");
            if (discordSRV != null) {
                discordSRV.reloadConfig();
            }
            return true;
        } catch (Exception e) {
            System.out.println("Failed to save messages.yml: " + e.getMessage());
            return false;
        }
    }

    private boolean toggleJoinAnnouncements(boolean enable) {
        config.set(JOIN_MESSAGE_PATH, enable);
        try {
            config.save(configFile);

            // Attempt to reload DiscordSRV config
            Plugin discordSRV = Bukkit.getPluginManager().getPlugin("DiscordSRV");
            if (discordSRV != null) {
                discordSRV.reloadConfig();
            }
            return true;
        } catch (Exception e) {
            System.out.println("Failed to save messages.yml: " + e.getMessage());
            return false;
        }
    }
}
