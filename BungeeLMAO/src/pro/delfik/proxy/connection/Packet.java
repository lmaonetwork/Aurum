package pro.delfik.proxy.connection;

import java.util.function.Function;

public class Packet {
	
	private final String[] args;
	private final Type type;
	
	public Packet(String packet) {
		String unsplittedArgs = packet.substring(3);
		String[] args = unsplittedArgs.split("&");
		String id = packet.substring(0, 3);
		this.type = Type.byID(id);
		this.args = args;
	}
	
	public String process() {
		return type.getProcessor().apply(this);
	}
	
	public boolean hasArguments() {
		return args.length == 0;
	}
	
	public String[] getArguments() {
		return args;
	}
	
	public Type getType() {
		return type;
	}
	
	public enum Type {
		
		HANDSHAKE("000", PacketProcessor::handshake),
		READ_FILE("001", PacketProcessor::readFile),
		
		BAN("151", PacketProcessor::punish),
		MUTE("152", PacketProcessor::punish),
		KICK("153", PacketProcessor::punish);
		
		private final String id;
		private final Function<Packet, String> processor;
		
		Type(String id, Function<Packet, String> processor) {
			this.id = id;
			this.processor = processor;
		}
		
		public Function<Packet, String> getProcessor() {
			return processor;
		}
		
		@Override
		public String toString() {
			return id + "?";
		}
		
		public static Type byID(String id) {
			try {
				return valueOf(id);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public class CorruptedPacketException extends RuntimeException {
		public CorruptedPacketException(String corruptedAt) {
			super("Невозможно пропарсить строку: " + corruptedAt);
		}
	}
	
}