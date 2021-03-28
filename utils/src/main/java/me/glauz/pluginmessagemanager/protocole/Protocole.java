package me.glauz.pluginmessagemanager.protocole;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.*;


public class Protocole {
    public static byte[] constructPacket(Packet packet) throws InvalidPacketException, ConstructPacketErrorException {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        if (packet.serversGroup == null)
            throw new InvalidPacketException("packet.serversGroup is null");

        output.writeUTF(packet.serversGroup);

        if (packet.params != null) {
            output.writeBoolean(true);
            output.writeShort(packet.params.size());
            packet.params.forEach(param -> output.writeUTF(param));
        } else {
            output.writeBoolean(false);
        }

        if (packet.data != null) {
            output.writeBoolean(true);

            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);

            try {
                msgout.writeUTF(packet.data);

                output.writeShort(msgbytes.toByteArray().length);
                output.write(msgbytes.toByteArray());

            } catch (IOException exception) {
                throw new ConstructPacketErrorException("Unable to write packet.data:\n" + exception.getMessage());
            }
        } else {
            output.writeBoolean(false);
        }

        return output.toByteArray();
    }

    public static Packet deconstructPacket(byte[] bytes) throws DeconstructPacketErrorException{
        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);

        Packet packet = new Packet();
        packet.serversGroup = input.readUTF();

        boolean hasParameters = input.readBoolean();
        if (hasParameters) {
            short nbParameters = input.readShort();
            for(int i = 0; i < nbParameters; i++) {
                packet.params.add(input.readUTF());
            }
        }

        boolean hasDatum = input.readBoolean();
        if (hasDatum) {
            short len = input.readShort();
            byte[] msgbytes = new byte[len];
            input.readFully(msgbytes);

            DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));

            try {
                packet.data = msgin.readUTF();
            } catch (IOException exception) {
                throw new DeconstructPacketErrorException("Unable to read the data:\n" + exception.getMessage());
            }
        }

        return packet;
    }
}
