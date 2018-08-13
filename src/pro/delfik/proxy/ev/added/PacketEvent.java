package pro.delfik.proxy.ev.added;

import net.md_5.bungee.api.plugin.Event;
import pro.delfik.net.Packet;

public class PacketEvent extends Event{
	private final String server;

	private final Packet packet;

	public PacketEvent(String server, Packet packet) {
		this.server = server;
		this.packet = packet;
	}

	public String getServer() {
		return server;
	}

	public Packet getPacket() {
		return packet;
	}
}
