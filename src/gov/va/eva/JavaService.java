package gov.va.eva;  // "http://cases.services.vetsnet.vba.va.gov/"

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;


/*  Comments  */
public class JavaService {
    private static final Configuration config = new Configuration("eVA.config");
    private static FileWriter log_fr;
    private JDBCService jdbc;
    private SOAPClient soap;

    public JavaService() {
        log("Java Service version 0.8 starts. " + config.getString("version"));
        this.soap = new SOAPClient(this);
        this.jdbc = new JDBCService(this);


        soap.testCaseNotes(); // Run tests, remove this line.

        // Poll for case notes.
        if (config.getBool("loop")) {
            System.out.println("Hit ^C to exit.");
            while (true) {
                try {
                    sleep(config.getInt("wait")); // note to Use better Rate limit, calculation later
                } catch (InterruptedException e) {
                    ;
                }
                jdbc.poll();
            }
        }
    }

    public static void main(String[] args) {
        new JavaService();
    }

    public synchronized void receive(CaseNote note) {
        soap.sendCaseNote(note);
    }

    /* Logging */

    private String logFormat(String str) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        return (dateFormat.format(new Date()) + " IN " + str +"\n");
    }

    void log(String msg) {
        if (config.getBool("log-to-console")) {
            System.out.print(logFormat(msg));
        }
        if (config.getBool("log-to-file")) {
            try {
                if (log_fr == null) {
                    log_fr = new FileWriter(new File("eVA.dat"));
                }
                log_fr.write(logFormat(msg));
            } catch (IOException e) {
                e.printStackTrace();
                // log_fr.close();
                log_fr = null;
            }
        }
    }
}
