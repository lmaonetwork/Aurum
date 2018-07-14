package pro.delfik.proxy.data;

import net.md_5.bungee.api.plugin.Event;

public class SocketEvent extends Event {
	private final String channel;
	private final String msg;
	public SocketEvent(String channel, String msg) {
		this.channel = channel;
		this.msg = msg;
	}
	public String getChannel() {
			return this.channel;
		}
	public String getMsg() {
			return this.msg;
		}
}
