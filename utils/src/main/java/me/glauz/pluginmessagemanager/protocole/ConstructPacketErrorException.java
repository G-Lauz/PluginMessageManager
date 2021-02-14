package me.glauz.pluginmessagemanager.protocole;

public class ConstructPacketErrorException extends Exception{
    public ConstructPacketErrorException(String message) {
        super("ConstructPacketErrorException: " + message);
    }
}
