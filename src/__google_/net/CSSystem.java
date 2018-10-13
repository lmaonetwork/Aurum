package __google_.net;

import __google_.util.Coder;
import __google_.util.Exceptions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.function.Consumer;

public abstract class CSSystem implements NetWorker{
    private Response response = null;
    private Flags flags = new Flags();
    private boolean onlyEncrypted = false;
    private Consumer<NetWorker> postWrite = null;

    private final Socket socket;
    private final BufferedInputStream in;
    private final BufferedOutputStream out;

    @Override
    public void postWrite(Consumer<NetWorker> postWrite) {
        this.postWrite = postWrite;
    }

    protected CSSystem(Socket socket) throws IOException{
        this.socket = socket;
        this.in = new BufferedInputStream(socket.getInputStream());
        this.out = new BufferedOutputStream(socket.getOutputStream());
    }

    @Override
    public void close(){
        if(socket == null)return;
        Exceptions.runThrowsEx(socket::close);
    }

    @Override
    public void write() throws IOException{
        byte write[] = Coder.toBytes(response);
        if(onlyEncrypted || (flags.isCrypt() && crypt() != null))write = crypt().encodeByte(write);
        write(Coder.toAbsoluteBytes(write.length));
        write(flags.getFlags());
        write(write);
        flush();
        if(postWrite == null)return;
        postWrite.accept(this);
        postWrite = null;
    }

    @Override
    public void read() throws IOException{
        byte input[] = read(5);
        int size = Coder.toInt(Coder.subBytes(input, 4));
        Flags flags = new Flags(input[4]);
        setFlags(flags);
        byte read[] = read(size);
        if(onlyEncrypted || (flags.isCrypt() && crypt() != null))read = crypt().decodeByte(read);
        setResponse(Coder.toObject(read, Response.class));
    }

    private byte[] read(int size) throws IOException{
        while (true) {
            try {
                byte array[] = new byte[size];
                for (int i = 0; i < array.length; i++) {
                    int local = in.read();
                    if (local == -1) throw new IOException("Can't read");
                    array[i] = (byte) local;
                }
                return array;
            }catch (SocketTimeoutException ex){}
        }
    }

    private void write(byte array[]) throws IOException{
        out.write(array);
    }

    private void write(byte b) throws IOException{
        out.write(b);
    }

    private void flush() throws IOException{
        out.flush();
    }

    @Override
    public Socket socket() {
        return socket;
    }

    @Override
    public Response response(){
        return response;
    }

    @Override
    public void setResponse(Response response) {
        this.response = response;
    }

    @Override
    public Flags flags() {
        return flags;
    }

    @Override
    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    @Override
    public boolean connected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void onlyEncrypt(boolean onlyEncrypt){
        onlyEncrypted = onlyEncrypt;
    }

    @Override
    public boolean onlyEncrypt() {
        return onlyEncrypted;
    }
}
