package RAF.KiDSDomaci1.model.input;

public class InputToCruncher {
    private final String name;
    private final String data;
    private final String path;

    public InputToCruncher(String name, String path, String data) {
        this.name = name;
        this.path = path;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public String getPath() {
        return path;
    }
}
