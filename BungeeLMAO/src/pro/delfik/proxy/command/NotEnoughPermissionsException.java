package pro.delfik.proxy.command;

import pro.delfik.proxy.permissions.Rank;

public class NotEnoughPermissionsException extends RuntimeException {
	private final Rank required;
	public NotEnoughPermissionsException(Rank required) {
		super(required.toString());
		this.required = required;
	}
	
	public Rank getRequiredRank() {
		return required;
	}
}
