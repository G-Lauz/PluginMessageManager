package me.glauz.pluginmessagemanager.bungee;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;


public class PluginMessageManager extends Plugin {

    private Config config;

    @Override
    public void onEnable() {
        super.onEnable();

        config = new Config(this);
        try {
            config.loadConfigFile();

            getProxy().registerChannel(config.getChannel());
            getProxy().getPluginManager().registerListener(this, new PluginMessageReceiver(this));
        } catch (IOException err) {
            getLogger().severe("Unable to load the configuration. The plugin won't respond:");
            getLogger().severe(err.getMessage());
        }
    }

    public Config getConfig() {
        return config;
    }
}
