package __google_.crypt.sync;

import __google_.util.Coder;

public class Blowfish extends SyncCrypt{
    public Blowfish(byte key[]){
        super("Blowfish", key);
    }

    public Blowfish(String key){
        this(Coder.toBytes(key));
    }
}