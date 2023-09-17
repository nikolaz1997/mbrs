package generator;

import java.io.FileWriter;
import java.io.IOException;

public class FileSaver {
    public static void save(final String code, final String fileName, final String extension) {
        try (FileWriter writer = new FileWriter(fileName.concat("." + extension))) {
            writer.write(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
