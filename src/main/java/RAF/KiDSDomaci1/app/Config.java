package RAF.KiDSDomaci1.app;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	public static Properties config;

	static {
		config = new Properties();
		try {
			config.load(new FileInputStream("app.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		return config.getProperty(key);
	}
}
