package pro.delfik.util;

import implario.util.Exceptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Logger {
    private static BufferedWriter writer;

    static{
        Exceptions.runThrowsEx(() -> {
            File file = new File("Core/log.txt");
            if (!file.exists()) file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file, true));
        });
    }

    public static void log(String type, String message){
        Exceptions.runThrowsEx(() -> writer.write(type + ": " + message + '\n'));
    }

    public static void close(){
        Exceptions.runThrowsEx(() -> {
            writer.flush();
            writer.close();
        });
    }
}
