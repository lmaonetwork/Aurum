package pro.delfik.proxy.command;

public class NotEnoughArgumentsException extends RuntimeException {
	
	private final String usage;
	public NotEnoughArgumentsException(String s) {
		super(s);
		this.usage = s;
	}
	
	public String getUsage() {
		return usage;
	}
}
