package __google_.crypt.sync;

import __google_.crypt.hash.BCrypt;
import __google_.crypt.hash.SHA_256;
import __google_.util.Coder;

import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AES extends SyncCrypt{
    public AES(int size, SecureRandom random){
        super("AES");
        byte array[] = new byte[size];
        random.nextBytes(array);
        this.key = new SecretKeySpec(array, getAlgorithm());
    }

    public AES(int size){
        this(size, new SecureRandom());
    }

    public AES(byte key[]){
        super("AES", key);
    }

    public AES(String key){
        this(new SHA_256().encodeByte(BCrypt.encode(Coder.toBytes(key), 12)));
    }
}