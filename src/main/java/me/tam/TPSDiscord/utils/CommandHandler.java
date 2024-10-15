package me.tam.TPSDiscord.utils;

import me.tam.TPSDiscord.Command.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommandHandler extends ListenerAdapter {

    private final TPSCommand tpsCommand = new TPSCommand();
    private final SetImage setImage = new SetImage();
    private final MuteCommand muteCommand = new MuteCommand();
    private final StatusCommand statusCommand = new StatusCommand();
    private final InvCommand invCommand = new InvCommand();


    public void registerCommands(JDA jda) {
        List<CommandData> commands = new ArrayList<>();

        List<String> allPlayers = Arrays.stream(Bukkit.getOfflinePlayers()).filter(Objects::nonNull).map(OfflinePlayer::getName).collect(Collectors.toList());
        OptionData playerOption = new OptionData(OptionType.STRING, "player", "The player to check", true)
                .addChoices(allPlayers.stream()
                        .map(player -> new Command.Choice(player, player))
                        .collect(Collectors.toList()));

        commands.add(Commands.slash("tps", "Get the TPS of the server")
                .addOption(OptionType.STRING, "tps", "Get the TPS of the server", false));
        commands.add(Commands.slash("set-image", "Set the profile picture of the bot")
                .addOption(OptionType.ATTACHMENT, "image", "Set the profile picture of the bot", true));
        commands.add(Commands.slash("mute", "Mute the join message"));
        commands.add(Commands.slash("setstatus", "Set the bot's status")
                .addOption(OptionType.STRING, "type", "The activity type (playing, streaming, listening, watching, competing, custom)", true)
                .addOption(OptionType.STRING, "text", "The status text", true)
                .addOption(OptionType.STRING, "url", "The streaming URL (only for streaming type)", false));
        commands.add(Commands.slash("inv", "Check a player's Minecraft inventory")
                .addOptions(playerOption));


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
            case "mute":
                muteCommand.onSlashCommandInteraction(event);
                break;
            case "setstatus":
                statusCommand.onSlashCommandInteraction(event);
                break;
            case "inv":
                invCommand.onSlashCommandInteraction(event);
                break;
            default:
                event.reply("Invalid command").queue();
                break;
        }
    }
}