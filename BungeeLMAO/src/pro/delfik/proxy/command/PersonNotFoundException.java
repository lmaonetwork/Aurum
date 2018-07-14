package pro.delfik.proxy.command;

public class PersonNotFoundException extends RuntimeException {
	private final String username;
	public PersonNotFoundException(String username) {
		super(username);
		this.username = username;
	}
	
	public String getPersonname() {
		return username;
	}
}
