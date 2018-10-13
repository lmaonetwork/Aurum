package __google_;

import __google_.crypt.Crypt;
import __google_.crypt.async.RSA;
import __google_.crypt.async.SignedRSA;
import __google_.crypt.sync.AES;
import __google_.crypt.sync.Blowfish;
import __google_.net.Flags;
import __google_.net.Response;
import __google_.net.client.Client;
import __google_.net.server.Server;
import __google_.util.*;

import java.nio.charset.Charset;
import java.util.function.Consumer;

public class Testing {
    private static final int iterations = 100000;

    public static <T> long test(Consumer<T> consumer, T object, int number){
        long end, start = System.nanoTime();
        for(int i = 0; i < number; i++)
            consumer.accept(object);
        end = System.nanoTime();
        return end - start;
    }

    public static void AES(){
        //Key size only 16 | 24 | 32
        defCrypt(new AES("LolLolLolLolLolL"));
    }

    public static void RSA(){
        //Async crypt
        defCrypt(new RSA());
    }

    public static void Blowfish(){
        //Custom key size
        defCrypt(new Blowfish("LolLolLolLolLolLaff"));
    }

    public static void fastLowerEN(){
        String lineEN = "aAbBcCdDeEfF123456@@%";

        System.out.println(Coder.toLowerEN(lineEN));
        System.out.println("Fast -> " + Testing.test((l) -> Coder.toLowerEN(l), lineEN, iterations));
        System.out.println(lineEN.toLowerCase());
        System.out.println("Default -> " + Testing.test((l) -> l.toLowerCase(), lineEN, iterations));
    }

    public static void fastUpperEN(){
        String lineEN = "aAbBcCdDeEfF123456@@%";

        System.out.println(Coder.toUpperEN(lineEN));
        System.out.println("Fast -> " + Testing.test((l) -> Coder.toUpperEN(l), lineEN, iterations));
        System.out.println(lineEN.toUpperCase());
        System.out.println("Default -> " + Testing.test((l) -> l.toUpperCase(), lineEN, iterations));
    }

    public static void fastLowerRU(){
        String lineRU = "аАбБвВгГдДеЕёЁ123456@@%";

        System.out.println(Coder.toLowerRU(lineRU));
        System.out.println("Fast -> " + Testing.test((l) -> Coder.toLowerRU(l), lineRU, iterations));
        System.out.println(lineRU.toLowerCase());
        System.out.println("Default -> " + Testing.test((l) -> l.toLowerCase(), lineRU, iterations));
    }

    public static void fastUpperRU(){
        String lineRU = "аАбБвВгГдДеЕёЁ123456@@%";

        System.out.println(Coder.toUpperRU(lineRU));
        System.out.println("Fast -> " + Testing.test((l) -> Coder.toUpperRU(l), lineRU, iterations));
        System.out.println(lineRU.toUpperCase());
        System.out.println("Default -> " + Testing.test((l) -> l.toUpperCase(), lineRU, iterations));
    }

    public static void certificate(){
        RSA rsa = new RSA(1536);
        RSA rs = new RSA(rsa.getBytePublicKey(), rsa.getBytePrivateKey());
        String line = "LolKasgasgaqwsgawsgzagwsawsggtawqtqwtqwtqwtqwtqwtqwtqwek";
        System.out.println(rs.encodeByte(line.getBytes(Charset.forName("ASCII"))).length);
        System.out.println(line.getBytes(Charset.forName("ASCII")).length);
        System.out.println(rs.encode(line));
        System.out.println(rsa.decode(rs.encode(line)));
    }

    public static void net(){
        Server server = new Server(4000);
        server.setCertificate(Byteable.toByteable(FileIO.readBytes("lmaomc/signed.certificate"), SignedRSA.class),
                Byteable.toByteable(FileIO.readBytes("lmaomc/rsa.key"), RSA.class));
        server.addExec(1, netServer -> {});
        Client client = new Client("localhost", 4000);
        client.connect();
        client.getCertificate();
        Response response = client.apply(new Response(1, Coder.toBytes("LolKek")), new Flags(false));
        System.out.println(response.getType());
        System.out.println(Coder.toString(response.getContent()));
        System.out.println(client.connected());
        response = client.apply(new Response(1, Coder.toBytes("LolKek123")));
        System.out.println(response.getType());
        System.out.println(Coder.toString(response.getContent()));
        client.close();
        server.close();
    }

    public static void file(){
        FileIO.write("Lol", "Lol12355");
        System.out.println(FileIO.read("Lol"));
    }

    public static void bytezip(){
        ByteZip zip = new ByteZip();
        zip.add("LolKekддд").add(123).add(12512552512512L);
        ByteUnzip unzip = new ByteUnzip(zip.build());
        System.out.println(unzip.getString());
        System.out.println(unzip.getInt());
        System.out.println(unzip.getLong());
    }

    private static void defCrypt(Crypt crypt){
        String line = "Lol 12355";
        String encoded = crypt.encode(line);

        System.out.println(line);
        System.out.println(encoded);
        System.out.println(crypt.decode(encoded));
    }
}
