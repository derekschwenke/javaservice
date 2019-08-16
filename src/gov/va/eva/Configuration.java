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
    static Configuration theConfiguration;
    Properties properties = new Properties();
    File file = null;
    long nextCheckMS = 0;
    long lastModified = 0;

    public Configuration(String filename){
        theConfiguration = this;
        file = new File(filename);
        check();
    }

    public static Configuration get() {
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
    public String getString(String key){
        check();
        return properties.getProperty(key);
    }

    public String getInt(String key){
        check();
        return properties.getProperty(key);
    }
}
