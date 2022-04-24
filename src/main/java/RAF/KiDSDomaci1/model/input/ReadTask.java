package RAF.KiDSDomaci1.model.input;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class ReadTask implements Callable<String> {

    private final File file;

    public ReadTask(File file) {
        this.file = file;
    }

    @Override
    public String call() throws Exception {

        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] chars = new byte[(int) file.length()];
        fileInputStream.read(chars);
        fileInputStream.close();
        String text = new String(chars, StandardCharsets.US_ASCII);

        return text;
    }
}

