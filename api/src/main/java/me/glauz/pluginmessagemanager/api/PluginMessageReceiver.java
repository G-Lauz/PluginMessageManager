package me.glauz.pluginmessagemanager.api;

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

    private PluginMessageReceiver() {

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

        checkIfBungee();
        if (!getServer().getPluginManager().isPluginEnabled(this.plugin)) {
            return;
        }

        // load global's configuration
        GlobalConfig globalConfig = GlobalConfig.getInstance();
        globalConfig.loadConfigFile();
        this.channel = globalConfig.getChannel();

        getServer().getMessenger().registerIncomingPluginChannel(this.plugin, this.channel, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, this.channel);
    }

    private void checkIfBungee() {
        if (!getServer().spigot().getConfig().getBoolean("settings.bungeecord")) {
            getLogger().severe( "This server is not BungeeCord." );
            getLogger().severe( "If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell." );
            getLogger().severe( "Plugin disabled!" );
            getServer().getPluginManager().disablePlugin(this.plugin);
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!channel.equals(this.channel))
            return;

        try {
            Packet packet = Protocole.deconstructPacket(bytes);

            System.out.println("PACKET RECEIVED: ");
            System.out.println("Server group: " + packet.serversGroup);
            System.out.println("Params: ");
            packet.params.forEach(param -> System.out.println("\t" + param));
            System.out.println("Data: " + packet.data);

            if (packet.serversGroup.equalsIgnoreCase("MySubChannel")) {
                // TODO
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
}
