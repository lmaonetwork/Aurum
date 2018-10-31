package pro.delfik.vk.cmd;

public abstract class Cmd implements Command{
    //add support for future
    public String exec(String args[], int id){
        return execute(args, id);
    }

    public abstract String execute(String args[], int id);
}
