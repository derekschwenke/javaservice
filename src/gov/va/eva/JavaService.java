package gov.va.eva;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


class Configuration {
    public Configuration(String filename){
    }
}

public class JavaService {

    public static void main(String[] args) {
        System.out.println("Hello");
        try( FileReader reader = new FileReader("properties.txt")) {
            Properties p = new Properties();
            p.load(reader);
            System.out.println(p.getProperty(("greeting")));
            System.out.println("Greetings " + p.getProperty(("greeting")) );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
