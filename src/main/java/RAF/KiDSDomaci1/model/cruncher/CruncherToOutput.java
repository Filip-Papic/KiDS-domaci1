package RAF.KiDSDomaci1.model.cruncher;

import java.util.*;
import java.util.concurrent.Future;

public class CruncherToOutput {
    private final Future<Map<String,Integer>> map;
    private final String name;

    public CruncherToOutput(Future<Map<String, Integer>> map, String name) {
        this.map = map;
        this.name = name;
    }

    public Future<Map<String, Integer>> getData() {
        return map;
    }

    public String getName() {
        return name;
    }
}
