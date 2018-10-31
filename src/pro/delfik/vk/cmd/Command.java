package pro.delfik.vk.cmd;

@FunctionalInterface
public interface Command {
    String exec(String args[], int id);
}
