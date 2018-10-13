package __google_.crypt.hash;

import __google_.crypt.Crypt;
import __google_.util.Coder;
import __google_.util.Exceptions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public abstract class Hasher extends Crypt{
	public Hasher(String algorithm) {
		super(algorithm);
	}

	public byte[] encodeByte(byte array[], byte salt[]){
		return Exceptions.getThrowsEx(() -> {
			MessageDigest digest = MessageDigest.getInstance(getAlgorithm());
			digest.update(array);
			digest.update(salt);
			return digest.digest();
		}, false);

	}

	public byte[] encodeByte(String array, byte salt[]){
		return encodeByte(Coder.toBytes(array), salt);
	}

	public String encode(String array, byte salt[], boolean useBase64){
		byte result[] = encodeByte(Coder.toBytes(array), salt);
		return useBase64 ? Base64.getEncoder().encodeToString(result) : new String(result);
	}

	public String encode(String array, byte salt[]){
		return encode(array, salt, true);
	}

	public byte[] generateSalt(int rounds, SecureRandom random){
		byte array[] = new byte[rounds];
		random.nextBytes(array);
		return array;
	}

	public byte[] generateSalt(int rounds){
		return generateSalt(rounds, new SecureRandom(new byte[]{}));
	}

	public byte[] generateSalt(){
		return generateSalt(12);
	}

	@Override
	public byte[] encodeByte(byte array[]) {
		return encodeByte(array, generateSalt());
	}

	@Override
	public final byte[] decodeByte(byte array[]) {
		throw new UnsupportedOperationException("This class support only encrypt");
	}
}
