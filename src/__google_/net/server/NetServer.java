package __google_.net.server;

import __google_.net.NetWorker;
import __google_.util.Exceptions;

import java.io.IOException;

public interface NetServer extends Runnable, NetWorker {
    default void run() {
        Exceptions.runThrowsEx(this::execute);
    }

    void execute() throws IOException;
}
