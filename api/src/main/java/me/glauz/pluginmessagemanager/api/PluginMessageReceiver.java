package me.glauz.pluginmessagemanager.api;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.glauz.pluginmessagemanager.config.GlobalConfig;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PluginMessageReceiver implements PluginMessageListener {

    private static PluginMessageReceiver instance;
    private Plugin plugin;
    private String channel;

    private PluginMessageReceiver() {
        // load global's configuration
        channel = GlobalConfig.getInstance().getChannel();
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

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(this.channel))
            return;
    }

    private void sendPluginMessage(Player player, String channel, ArrayList<String> specification, String message) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        specification.forEach(params -> output.writeUTF(params));

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);

        try {
            msgout.writeUTF(message);

            output.writeShort(msgbytes.toByteArray().length);
            output.write(msgbytes.toByteArray());

            player.sendPluginMessage(this.plugin, channel, output.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
