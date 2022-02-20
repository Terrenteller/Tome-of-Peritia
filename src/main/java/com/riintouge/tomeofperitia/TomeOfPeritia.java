package com.riintouge.tomeofperitia;

import org.bukkit.plugin.java.JavaPlugin;

public class TomeOfPeritia extends JavaPlugin
{
    public static final String BookTitle = "Tome of Peritia";
    public static final String StoredExperienceKey = "exp";

    public static JavaPlugin INSTANCE;

    public TomeOfPeritia()
    {
        INSTANCE = this;
    }

    // JavaPlugin overrides

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents( new EventHandlers() , this );
    }
}
