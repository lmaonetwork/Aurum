package __google_.net;

public class Response{
    private byte[] content;
    private byte type;

    public Response(int type, byte[] content){
        this.content = content;
        this.type = (byte)type;
    }

    public Response(int type){
        this(type, new byte[]{});
    }

    public Response(){}

    public byte[] getContent() {
        return content;
    }

    public byte getType() {
        return type;
    }
}