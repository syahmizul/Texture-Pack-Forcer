package com.silverhawk21.texturepackforce;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public final class TexturePackForce extends JavaPlugin implements Listener{

    private final ArrayList<Player> playerBlockedChatList = new ArrayList<>();
    private final HashMap<Player, Long> playerTimeoutList = new HashMap<>();
    @Override
    public void onEnable() {
        Config.getInstance().setConfig(getConfig());
        Config.getInstance().InitializeMessage();
        saveConfig();
        getServer().getPluginManager().registerEvents(this,this);
        getCommand("texturepack").setExecutor((sender, command, label, args) -> {

            if(args.length < 1)
                return true;

            if(!(sender instanceof Player))
            {
                System.out.println(ChatColor.RED + "Only a player can execute this command!");
                return true;
            }


            Player player = (Player) sender;
            if(args[0].equals("accept"))
            {
                try{
                    player.setResourcePack(Config.resource_pack_url);
                }
                catch(Exception exception)
                {
                    exception.printStackTrace();
                    player.sendMessage(ChatColor.DARK_RED + "Unable to send you the texture pack.");
                    player.sendMessage(ChatColor.DARK_RED + "Please contact an admin and provide this timestamp :" + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                }

                playerBlockedChatList.remove(player);
                playerTimeoutList.remove(player);
            }
            else if(args[0].equals("deny"))
            {
                playerBlockedChatList.remove(player);
                playerTimeoutList.remove(player);
            }
            return true;

        });
        new BukkitRunnable(){

            @Override
            public void run() {
                //Just in case the user is afk,new,can't read or any other player limitations..
                ArrayList<Player> tempPlayerToRemove = new ArrayList<>();
                playerTimeoutList.forEach((player, aLong) -> {
                    if(System.currentTimeMillis() - aLong >= Config.chat_allow_timeout)
                        tempPlayerToRemove.add(player);
                });
                tempPlayerToRemove.forEach(playerTimeoutList::remove);
                tempPlayerToRemove.forEach(playerBlockedChatList::remove);
            }
        }.runTaskTimer(this,0L,1L);


    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void OnPlayerJoin(PlayerJoinEvent event)
    {
        new BukkitRunnable(){

            @Override
            public void run() {
                Player player = event.getPlayer();
                playerBlockedChatList.add(player);
                playerTimeoutList.put(player,System.currentTimeMillis());

                TextComponent accept_text = new TextComponent(Config.accept_text);
                accept_text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Config.accept_description)));
                accept_text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/texturepack accept"));

                TextComponent deny_text = new TextComponent(Config.deny_text);
                deny_text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Config.deny_description)));
                deny_text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/texturepack deny"));

                for(String prompt : Config.prompt_message)
                {
                    player.sendMessage(prompt);
                }
                accept_text.addExtra(" ");
                accept_text.addExtra(deny_text);
                player.sendMessage(accept_text);
            }
        }.runTaskLater(this,Config.tick_to_delay_prompt);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        playerBlockedChatList.remove(event.getPlayer());
        playerTimeoutList.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event)
    {
        if(playerBlockedChatList.contains(event.getPlayer()))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Config.blocked_chat_text);
        }

        event.viewers().removeAll(playerBlockedChatList);
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
