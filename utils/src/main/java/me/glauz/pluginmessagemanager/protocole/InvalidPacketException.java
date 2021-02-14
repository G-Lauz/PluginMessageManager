package me.glauz.pluginmessagemanager.protocole;

public class InvalidPacketException extends Exception{
    public InvalidPacketException(String message) {
        super("InvalidPacketException: " + message);
    }
}
