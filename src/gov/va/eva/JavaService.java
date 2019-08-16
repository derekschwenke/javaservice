package gov.va.eva;  // "http://cases.services.vetsnet.vba.va.gov/"

import java.io.IOException;
import java.util.logging.*;
import static java.lang.Thread.sleep;


/*  Comments soon
 */
public class JavaService {
    private static final Configuration config = new Configuration("config.txt");
    private static final Logger logger = Logger.getLogger("eVA.log");
    private JDBCService jdbc;
    private SOAPClient soap;

    public JavaService() {
        logInit();
        log("Java Service version 0.1 starts. " + config.getString("greeting"));
        this.soap = new SOAPClient(this);
        this.jdbc = new JDBCService(this);

        // Run Optional tests
        soap.test();

        // Loop until ^C Exit
        System.out.println("Hit ^C to exit.");
        try {
            while (true) {
                sleep(1000);
            }
        } catch (InterruptedException e) {;}
    }

    public static void main(String[] args) {
        new JavaService();
    }

    public synchronized void receive(CaseNote note) {
        soap.sendCaseNote(note);
    }

    /* Logging */
    private static void logInit() {
        try {
            Handler fh = new FileHandler("eVA.log", false);  // append is true  %t/ temp %h ?
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setLevel(Level.FINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean canLog() {
        return (config.getString("log").equalsIgnoreCase("on"));
    }

    public static void log(String msg) {
        if (canLog()) {
            logger.info(msg);
        }
    }
}
