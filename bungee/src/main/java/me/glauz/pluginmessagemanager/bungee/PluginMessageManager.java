package me.glauz.pluginmessagemanager.bungee;

import net.md_5.bungee.api.plugin.Plugin;


public class PluginMessageManager extends Plugin {

    private Config config;

    @Override
    public void onEnable() {
        super.onEnable();

        config = new Config(this);
        config.loadConfigFile();

        getProxy().registerChannel(config.getChannel());
        getProxy().getPluginManager().registerListener(this, new PluginMessageReceiver(this));
    }

    public Config getConfig() {
        return config;
    }
}
