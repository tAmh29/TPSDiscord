package me.tam.TPSDiscord.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.SelfUser;

import java.time.Instant;

public class TPSutils {

    private static final double TPS_THRESHOLD = 19;

    public void sendTPSWarning(double currentTPS, String channelId, JDA jda) {
        SelfUser selfUser = jda.getSelfUser();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("TPS Warning");
        embed.setAuthor(selfUser.getName(), null, selfUser.getEffectiveAvatarUrl());
        embed.setDescription("TPS is below " + TPS_THRESHOLD + " (" + currentTPS + ")");
        embed.setImage("https://cdn.discordapp.com/attachments/936170474306699327/1285423823113420915/angry-kitten-angry-kitty.gif?ex=66ea37b9&is=66e8e639&hm=121dedf0906aad98cf519dac93a82e021a59011f8246d83b06c87cc325c7be07&");
        embed.setColor(0xff0000);
        embed.setTimestamp(Instant.now());
        embed.setFooter("TPSDiscord", selfUser.getEffectiveAvatarUrl());

        jda.getTextChannelById(channelId).sendMessageEmbeds(embed.build()).queue();
    }

}
