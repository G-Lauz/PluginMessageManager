package me.glauz.pluginmessagemanager.protocole;

public class DeconstructPacketErrorException extends Exception {
    public DeconstructPacketErrorException(String message) {
        super("DeconstructPacketErrorException: " + message);
    }
}
