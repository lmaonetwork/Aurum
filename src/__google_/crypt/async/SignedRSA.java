package __google_.crypt.async;

import __google_.crypt.hash.SHA_256;
import __google_.util.*;

import java.io.InputStream;
import java.util.Arrays;

public class SignedRSA implements Byteable{
	private final byte signedHash[];
	private final String hosts[];
	private final RSA rsa;
	private final long end;

	public SignedRSA(byte signedHash[], RSA rsa, String hosts[], long end){
		this.signedHash = signedHash;
		this.rsa = rsa;
		this.hosts = hosts;
		this.end = end;
	}

	public SignedRSA(byte signedHash[], RSA rsa, String hosts[]){
		this(signedHash, rsa, hosts, System.currentTimeMillis() + (2419200000L));
	}

	public SignedRSA(byte signedHash[], RSA rsa){
		this(signedHash, rsa, new String[]{});
	}

	public SignedRSA(ByteUnzip unzip){
		byte array[] = unzip.getBytes();
		signedHash = array.length == 0 ? null : array;
		rsa = new RSA(unzip.getBytes());
		end = unzip.getLong();
		int size = unzip.getInt();
		String strHosts[] = new String[size];
		for(int i = 0; i < size; i++)
			strHosts[i] = unzip.getString();
		this.hosts = strHosts;
	}

	public long getEnd() {
		return end;
	}

	public byte[] getSignedHash() {
		return signedHash;
	}

	public String[] getHosts() {
		return hosts;
	}

	public boolean existsHost(String host){
		for(String check : hosts)
			if(check.equals(host))return true;
		return false;
	}

	public RSA getRSA() {
		return rsa;
	}

	public boolean checkCertificate(RSA rsa){
		if(signedHash == null)return false;
		boolean last = rsa.isCertificate();
		rsa.setCertificate(true);
		try{
			if(end > System.currentTimeMillis())return false;
			byte decoded[] = rsa.decodeByte(signedHash);
			return Arrays.equals(createHash(this.rsa, hosts, end), decoded);
		}catch (IllegalArgumentException ex){
			return false;
		}finally{
			rsa.setCertificate(last);
		}
	}

	public boolean checkCertificate(){
		return checkCertificate(constant());
	}

	@Override
	public ByteZip toByteZip() {
		ByteZip zip = new ByteZip().add(signedHash == null ? new byte[]{} :
				signedHash).add(rsa.getBytePublicKey()).add(end).add(hosts.length);
		for(String host : hosts)
			zip.add(host);
		return zip;
	}

	public static SignedRSA sign(RSA constant, RSA sign, String hosts[], long end){
		return Exceptions.getThrowsEx(() -> new SignedRSA(
				constant.encodeByte(createHash(sign, hosts, end)),
				new RSA(sign.getBytePublicKey()), hosts, end), false);
	}

	private static RSA constant = null;

	public static RSA constant(){
		return constant;
	}

	static{
		InputStream in = SignedRSA.class.getClassLoader().getResourceAsStream("__google_/crypt/async/public.key");
		Exceptions.runThrowsEx(() -> {
			byte array[] = new byte[in.available()];
			in.read(array);
			constant = new RSA(array);
		});
	}

	private static byte[] createHash(RSA rsa, String hosts[], long end){
		return Coder.addBytes(Coder.addBytes(rsa.getHashPublicKey(), Coder.toBytes(end)), new SHA_256().encodeByte(String.join("", hosts)));
	}
}
