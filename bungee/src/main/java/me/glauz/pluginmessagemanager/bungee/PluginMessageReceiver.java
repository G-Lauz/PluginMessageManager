package me.glauz.pluginmessagemanager.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

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

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String groupServer = in.readUTF();

        if (plugin.getConfig().getGroup().containsKey(groupServer)) {

            String action = in.readUTF();

            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
            String data = msgin.readUTF();

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
