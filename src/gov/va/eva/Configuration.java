package gov.va.eva;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/*
        This class automatically reloads the configuration after the  file changes. See also JavaWatch and nio.
        This class extends Object and not Properties to discourage unchecked direct calls that are part of the API. (style)
*/


public class Configuration {
    private static Configuration theConfiguration;
    private Properties properties = new Properties();
    private File file = null;
    private long nextCheckMS = 0;
    private long lastModified = 0;

    public Configuration(String filename){
        theConfiguration = this;
        file = new File(filename);
        check();
    }

     static Configuration get() {
        return theConfiguration;
    }

    private synchronized void load() {
        System.out.println("Configuration loads "+ file.getName());
        try( FileReader reader = new FileReader(file)) {
            properties = new Properties(); // or .clear()
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void checkNow() {
        nextCheckMS = System.currentTimeMillis() + (10 * 1000); // Check every 10 sec
        long modifiedTime = file.lastModified();
        if (lastModified != modifiedTime) {
            lastModified = modifiedTime;
            load();
        }
    }

    private void check() {
        if (System.currentTimeMillis() > nextCheckMS) {
            checkNow();
        }
    }

    // Wrong  typeS simply return Properties
    String getString(String key){
        check();
        return properties.getProperty(key);
    }

    // Not used
    Integer getInt(String key){
        check();
        return Integer.valueOf(properties.getProperty(key));
    }
}
