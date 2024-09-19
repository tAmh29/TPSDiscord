package me.tam.TPSDiscord.Command;

import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.File;
import java.io.IOException;

public class SetImage extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("set-image")) {
            event.deferReply().queue();
            var attachment = event.getOption("image").getAsAttachment();
            try {
                File file = new File("temp_image");
                attachment.getProxy().downloadToFile(file).thenAccept( downloadedFile -> {
                    try {
                        Icon icon = Icon.from(downloadedFile);
                        event.getJDA().getSelfUser().getManager().setAvatar(icon).queue(
                                success -> event.getHook().sendMessage("Profile picture updated successfully!").queue(),
                                failure -> event.getHook().sendMessage("Failed to update profile picture: " + failure.getMessage()).queue()
                        );
                    } catch (IOException e) {
                        event.getHook().sendMessage("Failed to update profile picture: " + e.getMessage()).queue();
                    }
                });
            } catch (Exception e) {
                event.getHook().sendMessage("Failed to download the file.").queue();
            }
        }
    }
}
