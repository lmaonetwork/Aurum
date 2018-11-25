package pro.delfik.proxy.module;

public class Obj implements Registeable, Unloadable{
    private final Registeable registeable;
    private final Unloadable unloadable;

    public Obj(Registeable registeable, Unloadable unloadable){
        this.registeable = registeable;
        this.unloadable = unloadable;
    }

    @Override
    public void register() {
        registeable.register();
    }

    @Override
    public void unload() {
        unloadable.unload();
    }
}
