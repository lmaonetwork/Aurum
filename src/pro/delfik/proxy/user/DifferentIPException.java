package pro.delfik.proxy.user;

public class DifferentIPException extends RuntimeException {
	public DifferentIPException(String s) {
		super(s);
	}
}
