package __google_.util;

import java.io.*;

public class FileIO {
    //Can be set something, for libraries
    public static String prefix = "AppData/";

    public static void writeBytes(String strFile, byte[] array){
        File file = getFile(strFile);
        if(!file.exists())create(strFile);
        close(Exceptions.getThrowsEx(() -> {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(array);
            return out;
        }));
    }

    public static void write(String strFile, String write){
        File file = getFile(strFile);
        if(!file.exists())create(strFile);
        close(Exceptions.getThrowsEx(() -> {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(write);
            return out;
        }));
    }

    public static byte[] readBytes(String strFile){
        File file = getFile(strFile);
        if(!file.exists())return null;
        byte array[] = new byte[(int)file.length()];
        close(Exceptions.getThrowsEx(() -> {
            InputStream stream = new BufferedInputStream(new FileInputStream(file));
            stream.read(array);
            return stream;
        }));
        return array;
    }

    public static String read(String strFile){
        File file = getFile(strFile);
        if(!file.exists())return null;
        StringBuilder buffer = new StringBuilder((int)file.length());
        close(Exceptions.getThrowsEx(() -> {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (true){
                int i = reader.read();
                if(i == -1)break;
                buffer.append((char)i);
            }
            return reader;
        }));
        return buffer.toString();
    }

    public static void create(String strFile){
        File file = getFile(strFile);
        Exceptions.runThrowsEx(() -> {
            file.getParentFile().mkdirs();
            file.getParentFile().mkdir();
            file.createNewFile();
        });
    }

    public static void remove(String strFile){
        getFile(strFile).delete();
    }

    public static File[] getFiles(String strFile){
        File file = getFile(strFile);
        if(!file.exists() || file.isDirectory())return null;
        return file.listFiles();
    }

    public static File getFile(String file){
        return new File(System.getProperty("user.dir") + '/' + prefix + file);
    }

    private static void close(InputStream in){
        if(in == null)return;
        Exceptions.runThrowsEx(in::close);
    }

    private static void close(Reader in){
        if(in == null)return;
        Exceptions.runThrowsEx(in::close);
    }

    private static void close(OutputStream out){
        if(out == null)return;
        Exceptions.runThrowsEx(() -> {
            out.flush();
            out.close();
        });
    }

    private static void close(Writer out){
        if(out == null)return;
        Exceptions.runThrowsEx(() -> {
            out.flush();
            out.close();
        });
    }
}
