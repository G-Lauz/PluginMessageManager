package me.glauz.pluginmessagemanager.api;

import me.glauz.pluginmessagemanager.actions.ActionsHandler;
import me.glauz.pluginmessagemanager.actions.PluginMessageManagerActions;
import me.glauz.pluginmessagemanager.config.GlobalConfig;
import me.glauz.pluginmessagemanager.config.LoadConfigFileException;
import me.glauz.pluginmessagemanager.protocole.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.IOException;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;


public class PluginMessageReceiver implements PluginMessageListener {

    private static PluginMessageReceiver instance;

    private Plugin plugin;
    private String channel;

    private ActionsHandler actionsHandler;

    private PluginMessageReceiver() {
        this.actionsHandler = null;
    }

    public static PluginMessageReceiver getInstance() {
        if (instance == null)
            synchronized (PluginMessageReceiver.class) {
                if (instance == null)
                    instance = new PluginMessageReceiver();
            }
        return instance;
    }

    public void initialize(Plugin plugin) throws IOException, LoadConfigFileException{
        this.plugin = plugin;

        if (!checkIfBungee()) return;
        loadConfig();
        registerChannel();
    }

    public void initialize(Plugin plugin, ActionsHandler actionsHandler) throws IOException, LoadConfigFileException{
        this.plugin = plugin;
        this.actionsHandler = actionsHandler;

        if (!checkIfBungee()) return;
        loadConfig();
        registerChannel();
    }

    private boolean checkIfBungee() {
        if (!getServer().spigot().getConfig().getBoolean("settings.bungeecord")) {
            getLogger().severe( "This server is not BungeeCord." );
            getLogger().severe( "If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin(this.plugin);
            return false;
        }
        return true;
    }

    private void loadConfig() throws IOException, LoadConfigFileException {
        // load global's configuration
        GlobalConfig globalConfig = GlobalConfig.getInstance();
        globalConfig.loadConfigFile();
        this.channel = globalConfig.getChannel();
    }

    private void registerChannel() {
        getServer().getMessenger().registerIncomingPluginChannel(this.plugin, this.channel, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, this.channel);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!channel.equals(this.channel))
            return;

        try {
            Packet packet = Protocole.deconstructPacket(bytes);

            String action = packet.params.get(0);
            switch (PluginMessageManagerActions.valueOf(action)) {
                case BROADCAST:
                    if (this.actionsHandler != null) {
                        this.actionsHandler.onBroadcastReceived(packet.data);
                    }
                    break;

                default:
                    getLogger().warning("Received unhandle action: " + action);
                    getLogger().warning("Be sure that the API and the PluginMessageManager on BungeeCord are up to date.");
            }

        } catch (DeconstructPacketErrorException deconstructPacketErrorException) {
            getLogger().severe(deconstructPacketErrorException.getStackTrace().toString());
        }
    }

    private void sendPluginMessage(Player player, Packet packet) throws SendPluginMessageErrorException {
        try {
            byte[] bytes = Protocole.constructPacket(packet);

            player.sendPluginMessage(this.plugin, this.channel, bytes);

        } catch (InvalidPacketException invalidPacketException) {
            throw new SendPluginMessageErrorException(invalidPacketException.getMessage());
        } catch (ConstructPacketErrorException constructPacketErrorException) {
            throw new SendPluginMessageErrorException(constructPacketErrorException.getMessage());
        }
    }

    public void broadcast(Player player, String serversGroup, String message) throws SendPluginMessageErrorException {
        Packet packet = new Packet();
        packet.serversGroup = serversGroup;
        packet.params.add(PluginMessageManagerActions.BROADCAST.toString());
        packet.data = message;

        this.sendPluginMessage(player, packet);
    }

    public void setActionsHandler(ActionsHandler actionsHandler) {
        this.actionsHandler = actionsHandler;
    }
}
