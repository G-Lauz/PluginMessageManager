package me.glauz.pluginmessagemanager.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GlobalConfig {
    private static GlobalConfig instance;
    private String channel;

    private GlobalConfig() {

    }

    public static GlobalConfig getInstance() {
        if (instance == null)
            synchronized (GlobalConfig.class) {
                if (instance == null)
                    instance = new GlobalConfig();
            }
        return instance;
    }

    public void loadConfigFile() throws IOException {
        InputStream configFile = null;
        Properties properties = null;

        try {
            configFile = GlobalConfig.class.getResourceAsStream("/config.properties");
            properties = new Properties();

            properties.load(configFile);

            this.channel = properties.getProperty("channel");

        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            configFile.close();
        }
    }

    public String getChannel() {
        return this.channel;
    }
}
