package userfeedbacknlp.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author fitsum
 *
 */

public class ReFeedProperties {
	/**
	 * 
	 */
	private static ReFeedProperties instance = null;
	private Properties properties = null;
	
	private ReFeedProperties() {
		properties = new Properties();
		String configFile = "config.properties";
		try {
			properties.load(new FileInputStream(configFile ));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static ReFeedProperties getInstance() {
		if (instance == null) {
			instance = new ReFeedProperties();
		}
		return instance;
	}
	
	public String getProperty (String key) {
		return this.properties.getProperty(key);
	}
}
