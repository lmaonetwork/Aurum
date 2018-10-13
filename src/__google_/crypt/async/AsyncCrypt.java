package __google_.crypt.async;

import __google_.crypt.Crypt;
import __google_.crypt.hash.SHA_256;
import __google_.util.Exceptions;
import sun.security.rsa.RSAPrivateCrtKeyImpl;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.X509EncodedKeySpec;

public abstract class AsyncCrypt extends Crypt {
	protected Key publicKey, privateKey;

	protected AsyncCrypt(String algorithm) {
		super(algorithm);
	}

	public Key publicKey(){
		return publicKey;
	}

	public byte[] getBytePublicKey(){
		return publicKey().getEncoded();
	}

	public byte[] getHashPublicKey(){
		return new SHA_256().encodeByte(getBytePublicKey());
	}

	public Key privateKey(){
		return privateKey;
	}

	public byte[] getBytePrivateKey(){
		return privateKey().getEncoded();
	}

	public byte[] getHashPrivateKey(){
		return new SHA_256().encodeByte(getBytePrivateKey());
	}

	@Override
	public byte[] encodeByte(byte array[]) {
		return cipher(array, Cipher.ENCRYPT_MODE, publicKey());
	}

	@Override
	public byte[] decodeByte(byte array[]) {
		return cipher(array, Cipher.DECRYPT_MODE, privateKey());
	}

	protected KeyPair generate(int size){
		return Exceptions.getThrowsEx(() -> {
				KeyPairGenerator generator = KeyPairGenerator.getInstance(getAlgorithm());
				generator.initialize(size);
				return generator.genKeyPair();
		});
	}

	protected Key decodePublic(byte publicKey[]){
		return Exceptions.getThrowsEx(() -> {
			X509EncodedKeySpec key = new X509EncodedKeySpec(publicKey);
			KeyFactory factory = KeyFactory.getInstance(getAlgorithm());
			return factory.generatePublic(key);
		});
	}

	protected Key decodePrivate(byte privateKey[]){
		return Exceptions.getThrowsEx(() -> RSAPrivateCrtKeyImpl.newKey(privateKey), false);
	}
}
