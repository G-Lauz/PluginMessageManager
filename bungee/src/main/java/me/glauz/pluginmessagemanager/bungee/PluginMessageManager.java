package me.glauz.pluginmessagemanager.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class PluginMessageManager extends Plugin {

    private Config config;

    @Override
    public void onEnable() {
        super.onEnable();

        config = new Config(this);
        try {
            config.loadConfigFile();

            System.out.println("REGISTER CHANNEL: " + config.getChannel());
            getProxy().getPluginManager().registerListener(this, new PluginMessageReceiver(this));
            getProxy().registerChannel(config.getChannel());
            getProxy().registerChannel("BungeeCord");
        } catch (Exception err) {
            getLogger().severe("Unable to load the configuration. The plugin won't respond:");
            getLogger().severe(err.getMessage());
        }
    }

    public Config getConfig() {
        return config;
    }
}
