package me.glauz.pluginmessagemanager.bungee;

import me.glauz.pluginmessagemanager.protocole.Packet;
import me.glauz.pluginmessagemanager.protocole.Protocole;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;


public class PluginMessageReceiver implements Listener {

    private final PluginMessageManager plugin;

    public PluginMessageReceiver(PluginMessageManager plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle the incoming message according to his parameters following this Packet format:
     * <ol type="1">
     *   <li>Group server : String</li>
     *   <li>Action : String</li>
     *   <li>Data length : Short</li>
     *   <li>The actual data : byte[]</li>
     * </ol>
     * Called when their is an incoming Plugin Channel message over the channel parameter configure in the config.properties.
     *
     * Possible action:
     * <ul>
     *     <li>Broadcast</li>
     * </ul>
     *
     * @param event
     */
    @EventHandler
    public void on(PluginMessageEvent event) throws Exception{
        if (!event.getTag().equalsIgnoreCase(plugin.getConfig().getChannel()))
            return;

        Packet packet = Protocole.deconstructPacket(event.getData());

        if (plugin.getConfig().getGroup().containsKey(packet.serversGroup)) {

            String action = packet.params.get(0);

            // The receiver is a ProxiedPlayer when a server talks to the proxy
            if (event.getReceiver() instanceof ProxiedPlayer) {
                ProxiedPlayer receiver = (ProxiedPlayer) event.getReceiver();

                // TODO
                // Handle the action
                switch (action) {
                    case "Broadcast":
                        // TODO
                        break;

                    default:
                        throw new Exception("Non-existent action");
                }
            }

            // The receiver is a server when the proxy talks to a server
            if (event.getReceiver() instanceof Server) {
                Server receiver = (Server) event.getReceiver();

                // TODO
                // Handle the action
            }
        }
    }
}
