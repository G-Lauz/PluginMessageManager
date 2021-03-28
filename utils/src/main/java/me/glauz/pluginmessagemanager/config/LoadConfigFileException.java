package me.glauz.pluginmessagemanager.config;

public class LoadConfigFileException extends Exception {
    public LoadConfigFileException(String message) {
        super("LoadConfigFileException: " + message);
    }
}
