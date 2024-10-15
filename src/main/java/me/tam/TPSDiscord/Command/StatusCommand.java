package me.tam.TPSDiscord.Command;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.entities.Activity;

public class StatusCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("setstatus")) {
            String type = event.getOption("type").getAsString();
            String text = event.getOption("text").getAsString();
            String url = event.getOption("url") != null ? event.getOption("url").getAsString() : null;

            Activity activity;
            switch (type.toLowerCase()) {
                case "playing":
                    activity = Activity.playing(text);
                    break;
                case "streaming":
                    if (url == null) {
                        event.reply("You must provide a URL for the streaming activity").queue();
                        return;
                    }
                    activity = Activity.streaming(text, url);
                    break;
                case "listening":
                    activity = Activity.listening(text);
                    break;
                case "watching":
                    activity = Activity.watching(text);
                    break;
                case "competing":
                    activity = Activity.competing(text);
                    break;
                default:
                    event.reply("Invalid activity type").queue();
                    return;
            }

            event.getJDA().getPresence().setActivity(activity);
            event.reply("Status is updated successfully").queue();
        }
    }
}