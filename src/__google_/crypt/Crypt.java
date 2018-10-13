package __google_.crypt;

import __google_.util.Coder;

import javax.crypto.Cipher;
import java.security.Key;
import java.util.Base64;

public abstract class Crypt {
    private final String algorithm;

    protected Crypt(String algorithm){
        this.algorithm = algorithm;
    }

    public abstract byte[] encodeByte(byte array[]);

    public byte[] encodeByte(String line){
        return encodeByte(Coder.toBytes(line));
    }

    public String encode(String line, boolean useBase64){
        byte[] result = encodeByte(line.getBytes());
        return useBase64 ? Base64.getEncoder().encodeToString(result) : new String(result);
    }

    public String encode(String line){
        return encode(line, true);
    }

    public abstract byte[] decodeByte(byte array[]);

    public String decode(String line, boolean usedBase64){
        return new String(decodeByte(usedBase64 ? Base64.getDecoder().decode(line) : line.getBytes()));
    }

    public String decode(String line){
        return decode(line, true);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    protected byte[] cipher(byte array[], int mode, Key key){
        try{
            Cipher cipher = Cipher.getInstance(getAlgorithm());
            cipher.init(mode, key);
            return cipher.doFinal(array);
        }catch (Exception ex){
            throw new IllegalArgumentException(ex);
        }
    }
}
