package pro.delfik.proxy.stats;

import implario.util.Byteable;

public interface Stats extends Byteable {
    String toString();

    default String[] toReadableString(){
        return new String[]{toString()};
    }
}
