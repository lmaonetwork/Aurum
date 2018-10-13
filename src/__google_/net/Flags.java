package __google_.net;

import __google_.util.Coder;

public class Flags {
	private final boolean crypt;

	public Flags(byte flags){
		crypt = (flags & 1) == 1;
	}

	public Flags(boolean crypt){
		this.crypt = crypt;
	}

	public Flags(){
		this(true);
	}

	public boolean isCrypt() {
		return crypt;
	}

	public byte getFlags(){
		return Coder.toByte(crypt);
	}
}
