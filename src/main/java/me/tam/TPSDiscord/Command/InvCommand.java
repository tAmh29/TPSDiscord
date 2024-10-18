package me.tam.TPSDiscord.Command;

import me.tam.TPSDiscord.TPSDiscord;
import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import net.dv8tion.jda.api.utils.FileUpload;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;


public class InvCommand extends ListenerAdapter {

    private static final Logger LOGGER = Logger.getLogger("TPSDiscord");
    private static final int MAX_FIELDS = 25;
    private static final int MAX_ATTACHMENTS = 10;
    private JDA jda;
    private TPSDiscord plugin;

    public InvCommand(JDA jda, TPSDiscord plugin) {
        this.jda = jda;
        this.plugin = plugin;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        LOGGER.info("Inventory command received for player: " + event.getOption("player").getAsString());
        event.deferReply().queue();

        Map<String, String> emojiMap = plugin.getEmojiMap();

        String playerName = Objects.requireNonNull(event.getOption("player")).getAsString();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

        String uuid = offlinePlayer.getUniqueId().toString().replace("-", "");
        //String avatarURL = "https://api.mineatar.io/head/" + uuid;
        String avatarURL = "https://cdn.discordapp.com/emojis/1296408185803898934.gif";
        String skinURL = "https://api.mineatar.io/body/full/" + uuid;


        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(offlinePlayer.getName(), null, avatarURL);
        embed.setThumbnail(skinURL);
        embed.setTitle(offlinePlayer.getName() + "'s Inventory");
        embed.setColor(Color.PINK);

        int fieldCount = 0;
        int pageCount = 1;
        List<FileUpload> attachments = new ArrayList<>();

        Inventory inventory = getPlayerInventory(offlinePlayer);
        if (inventory != null) {
            ItemStack[] items = inventory.getContents();
            for (int i = 0; i < items.length; i++) {
                ItemStack item = items[i];
                if (item != null && item.getType() != Material.AIR) {
                    String itemName = item.getType().toString().toLowerCase();
                    String slotName = i < 36 ? "Slot " + i :
                            i < 40 ? "Armor " + (i - 36) :
                                    "Off Hand";

                    String emojiMention = emojiMap.get(itemName);
                    if (emojiMention != null) {
                        embed.addField(slotName, String.format("%s %s x%d", emojiMention, itemName, item.getAmount()), true);
                    } else {
                        LOGGER.warning("No emoji found for item: " + itemName);
                        embed.addField(slotName, String.format("%s %s x%d", "❓", itemName, item.getAmount()), true);
                    }
                    fieldCount++;

                    if (fieldCount >= MAX_FIELDS || attachments.size() >= MAX_ATTACHMENTS) {
                        event.getHook().sendMessageEmbeds(embed.build()).addFiles(attachments).queue();
                        embed = new EmbedBuilder();
                        embed.setAuthor(offlinePlayer.getName(), null, avatarURL);
                        embed.setThumbnail(skinURL);
                        embed.setTitle(offlinePlayer.getName() + "'s Inventory (Page " + (++pageCount) + ")");
                        embed.setColor(Color.PINK);
                        fieldCount = 0;
                        attachments.clear();
                    }
                }
            }
        } else {
            embed.setDescription("Unable to retrieve inventory data for this player.");
        }

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }

    private Inventory getPlayerInventory(OfflinePlayer offlinePlayer) {
        if (offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();
            if (player != null) {
                return player.getInventory();
            }
        }

        // If the player is offline, use the getOfflinePlayerInventory method
        return getOfflinePlayerInventory(offlinePlayer.getUniqueId());
    }

    public Inventory getOfflinePlayerInventory(UUID playerUUID) {
        File playerDataFile = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata/" + playerUUID + ".dat");
        if (!playerDataFile.exists()) {
            return null; // Player data file does not exist
        }
        try (FileInputStream fis = new FileInputStream(playerDataFile)) {
            NBTCompound playerData = NBTReader.read(fis);

            if (playerData == null) {
                LOGGER.warning("Failed to read player data for UUID: " + playerUUID);
                return null;
            }

            // Create an empty inventory (36 slots for player inventory)
            Inventory inventory = Bukkit.createInventory(null, 54, "Offline Inventory");

            // Get the inventory list
            NBTList inventoryList = playerData.getList("Inventory");

            if (inventoryList == null) {
                LOGGER.warning("Inventory data not found for UUID: " + playerUUID);
                return inventory; // Return empty inventory
            }

            // Iterate through the inventory items
            for (int i = 0; i < inventoryList.size(); i++) {
                NBTCompound itemTag = (NBTCompound) inventoryList.get(i);
                    // Extract item data
                String itemId = itemTag.getString("id");
                int count = itemTag.getInt("count", 0);
                int slot = itemTag.getInt("Slot", 1);

                if (slot >= 54) {
                    LOGGER.warning("Skipping item with slot number greater than 54: " + itemId + " in slot " + slot);
                    continue;
                }

                    // Create ItemStack and set it in the inventory
                if (count > 0) {
                    Material material = Material.getMaterial(itemId.toUpperCase().replace("MINECRAFT:", ""));
                    if (material != null) {
                        ItemStack item = new ItemStack(material, count);
                        inventory.setItem(slot, item);
                    } else {
                        LOGGER.warning("Invalid item ID or slot number for item: " + itemId + " in slot " + slot);
                    }
                } else {
                    LOGGER.warning("Invalid count (0 or less) for item: " + itemId + " in slot " + slot);
                }
            }

            return inventory;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
