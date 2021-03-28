package me.glauz.pluginmessagemanager.bungee;

import com.google.common.io.ByteStreams;
import me.glauz.pluginmessagemanager.config.GlobalConfig;
import me.glauz.pluginmessagemanager.config.LoadConfigFileException;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.*;

public class Config {
    private final PluginMessageManager plugin;
    private String channel;
    private Hashtable group;

    public Config(PluginMessageManager plugin) {
        this.plugin = plugin;
    }

    public void loadConfigFile() throws IOException, LoadConfigFileException {
        // Load global's configuration
        try {
            GlobalConfig globalConfig = GlobalConfig.getInstance();
            globalConfig.loadConfigFile();
            this.channel = globalConfig.getChannel();
        } catch (LoadConfigFileException lcfe) {
            throw lcfe;
        } catch (IOException ioe) {
            throw ioe;
        }

        // Load plugin's configuration
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();

        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile = generateDefaultConfigFile(configFile);
        }

        Configuration config;
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            List<?> serverGroup =  config.getList("group");
            group = new Hashtable();
            serverGroup.stream().forEach(item ->
                    ((LinkedHashMap)item).entrySet().forEach(entry ->
                            group.put(
                                    ((Map.Entry<String, ArrayList>)entry).getKey(),
                                    ((Map.Entry<String, ArrayList>)entry).getValue())));

        } catch (IOException e) {
            throw new RuntimeException("Unable to load configuration file");
        }
    }

    private File generateDefaultConfigFile(File file) {
        try {
            file.createNewFile();

            try (InputStream is = plugin.getResourceAsStream("config.yml");
                 OutputStream os = new FileOutputStream(file)) {
                ByteStreams.copy(is, os);
            }
            return file;

        } catch (IOException e) {
            throw new RuntimeException("Unable to create configuration file");
        }
    }

    public String getChannel() {
        return channel;
    }

    public Hashtable getGroup() {
        return group;
    }
}
