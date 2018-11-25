package pro.delfik.util;

import implario.util.Exceptions;
import pro.delfik.proxy.module.Registeable;
import pro.delfik.proxy.module.Unloadable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Logger implements Registeable, Unloadable {
    private static BufferedWriter writer;

    public static void log(String type, String message){
        Exceptions.runThrowsEx(() -> writer.write(type + ": " + message + '\n'));
        flush();
    }

    public static void flush(){
        Exceptions.runThrowsEx(writer::flush);
    }

    @Override
    public void register() {
        Exceptions.runThrowsEx(() -> {
            File file = new File("Core/log.txt");
            if (!file.exists()) file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file, true));
        });
    }

    @Override
    public void unload(){
        Exceptions.runThrowsEx(() -> {
            writer.flush();
            writer.close();
        });
    }
}
