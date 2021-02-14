package me.glauz.pluginmessagemanager.api;

import me.glauz.pluginmessagemanager.config.GlobalConfig;
import me.glauz.pluginmessagemanager.protocole.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class PluginMessageReceiver implements PluginMessageListener {

    private static PluginMessageReceiver instance;
    private Plugin plugin;
    private String channel;

    private PluginMessageReceiver() {
        checkIfBungee();

        // load global's configuration
        channel = GlobalConfig.getInstance().getChannel();

        getServer().getMessenger().registerIncomingPluginChannel(this.plugin, this.channel, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, this.channel);
    }

    public static PluginMessageReceiver getInstance() {
        if (instance == null)
            synchronized (PluginMessageReceiver.class) {
                if (instance == null)
                    instance = new PluginMessageReceiver();
            }
        return instance;
    }

    public void initialize(Plugin plugin) {
        this.plugin = plugin;
    }

    private void checkIfBungee() {
        if (!getServer().spigot().getConfig().getConfigurationSection("settings").getBoolean("settings.bungeecord")) {
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

            if (packet.serversGroup.equalsIgnoreCase("MySubChannel")) {
                // TODO
            }

        } catch (DeconstructPacketErrorException deconstructPacketErrorException) {
            getLogger().severe(deconstructPacketErrorException.getStackTrace().toString());
        }
    }

    private void sendPluginMessage(Player player, String channel, Packet packet) throws SendPluginMessageErrorException {
        try {
            byte[] bytes = Protocole.constructPacket(packet);
            player.sendPluginMessage(this.plugin, channel, bytes);

        } catch (InvalidPacketException invalidPacketException) {
            throw new SendPluginMessageErrorException(invalidPacketException.getMessage());
        } catch (ConstructPacketErrorException constructPacketErrorException) {
            throw new SendPluginMessageErrorException(constructPacketErrorException.getMessage());
        }
    }

    public void broadcast(Player player, String serversGroup, String message) throws SendPluginMessageErrorException {
        Packet packet = new Packet();
        packet.serversGroup = serversGroup;
        packet.params.add("Broadcast");
        packet.data = message;

        this.sendPluginMessage(player, this.channel, packet);
    }
}
