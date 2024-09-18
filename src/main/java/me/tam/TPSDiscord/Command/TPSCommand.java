package me.tam.TPSDiscord.Command;

import me.tam.TPSDiscord.utils.TPSutils;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import org.bukkit.Bukkit;

import me.tam.TPSDiscord.utils.TPSutils;


public class TPSCommand extends ListenerAdapter {

    TPSutils TPSutils = new TPSutils();


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("tps")) {
            // make sure the bot can respond and also it will say "thinking" while it's fetching
            event.deferReply().queue();
            // Fetch the TPS from the server
            double[] currentTPS = Bukkit.getServer().getTPS();
            // Use event.getHook().sendMessage() to send the actual reply
            TPSutils.sendTPSRequest(event.getHook(), currentTPS);
        }
    }
}
