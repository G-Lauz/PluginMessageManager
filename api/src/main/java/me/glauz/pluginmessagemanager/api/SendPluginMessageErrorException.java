package me.glauz.pluginmessagemanager.api;

public class SendPluginMessageErrorException extends Exception {
    public SendPluginMessageErrorException(String message) {
        super("SendPluginMessageErrorException: " + message);
    }
}
