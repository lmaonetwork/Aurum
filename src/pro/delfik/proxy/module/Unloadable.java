package pro.delfik.proxy.module;

public interface Unloadable {
    void unload();

    static Unloadable get(Runnable runnable){
        return runnable::run;
    }
}
