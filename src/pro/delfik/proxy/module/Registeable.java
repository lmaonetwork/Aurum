package pro.delfik.proxy.module;

public interface Registeable {
    void register();

    static Registeable get(Runnable runnable){
        return runnable::run;
    }
}
