package com.silverhawk21.texturepackforce;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Config {
    private volatile static Config instance = null;

    public static String resource_pack_url;
    public static String accept_text;
    public static String accept_description;
    public static String deny_text;
    public static String deny_description;
    public static ArrayList<String> prompt_message;
    public static String blocked_chat_text;
    public static Long chat_allow_timeout;
    public static Long tick_to_delay_prompt;

    private FileConfiguration config;

    public static Config getInstance()
    {
        if(instance == null)
            instance = new Config();
        return instance;
    }

    public void InitializeMessage()
    {
        config.addDefault("resource-pack-url", "https://inserturlhere.com");
        config.addDefault("accept-text",ChatColor.GREEN + "" + ChatColor.BOLD + "[ACCEPT]");
        config.addDefault("accept-description",ChatColor.GREEN + "Accept this resource pack.");
        config.addDefault("deny-text",ChatColor.DARK_RED + "" + ChatColor.BOLD + "[DENY]");
        config.addDefault("deny-description",ChatColor.DARK_RED + "Deny this resource pack.");
        config.addDefault("prompt-message",new ArrayList<String>
                            (
                                Arrays.asList
                                (
                                    ChatColor.GOLD + "Do you want to use our resource pack?",
                                    ChatColor.GOLD + "Click the text below to confirm."
                                )
                            )
                        );
        config.addDefault("blocked-chat-text",ChatColor.DARK_RED + "Please accept or deny the texture pack first.");
        config.addDefault("chat-allow-timeout", 10000L);
        config.addDefault("tick-to-delay-prompt",40L);
        config.options().copyDefaults(true);
        SetMessages();
    }
    
    private Config()
    {

    }

    private void SetMessages() {
        resource_pack_url = config.getString("resource-pack-url");
        accept_text = config.getString("accept-text");
        accept_description = config.getString("accept-description");
        deny_text= config.getString("deny-text");
        deny_description= config.getString("deny-description");
        prompt_message = (ArrayList<String>) config.getList("prompt-message");
        blocked_chat_text = config.getString("blocked-chat-text");
        chat_allow_timeout = config.getLong("chat-allow-timeout");
        tick_to_delay_prompt = config.getLong("tick-to-delay-prompt");
    }

    public void setConfig(FileConfiguration config)
    {
        this.config = config;
    }

    
}
