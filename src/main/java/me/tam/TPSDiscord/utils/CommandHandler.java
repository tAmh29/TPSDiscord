package me.tam.TPSDiscord.utils;

import me.tam.TPSDiscord.Command.TPSCommand;
import me.tam.TPSDiscord.Command.SetImage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler extends ListenerAdapter {

    private JDA jda;
    private final TPSCommand tpsCommand = new TPSCommand();
    private final SetImage setImage = new SetImage();


    public void registerCommands(JDA jda) {
        List<CommandData> commands = new ArrayList<>();

        commands.add(Commands.slash("tps", "Get the TPS of the server")
                .addOption(OptionType.STRING, "tps", "Get the TPS of the server", false));
        commands.add(Commands.slash("set-image", "Set the image of the server")
                .addOption(OptionType.ATTACHMENT, "image", "Set the image of the server", true));

        jda.updateCommands().addCommands(commands).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "tps":
                tpsCommand.onSlashCommandInteraction(event);
                break;
            case "set-image":
                setImage.onSlashCommandInteraction(event);
                break;
            default :
                event.reply("Invalid command").queue();
                break;
        }
    }

}
