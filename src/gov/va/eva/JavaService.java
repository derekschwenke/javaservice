package gov.va.eva;  // "http://cases.services.vetsnet.vba.va.gov/"

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;


/*  Comments  */
public class JavaService {
    private static final Configuration config = new Configuration("config.txt");
    private static Path log_fn;
    private JDBCService jdbc;
    private SOAPClient soap;

    public JavaService() {
        log("Java Service version 0.5 starts. " + config.getString("version"));
        this.soap = new SOAPClient(this);
        this.jdbc = new JDBCService(this);


        soap.tests(); // Run tests, remove this line.

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

    /* Logging  may be replaced or moved - - - - - - - - - - - - - - */

    private String logFormat(String str) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return (dateFormat.format(new Date()) + " " + str +"\n");
    }

    void log(String msg) {
        if (config.getBool("log-console")) {
            System.out.print(logFormat(msg));
        }
        if (config.getBool("log-file")) {
            try {
                if (log_fn == null) {
                    log_fn = Paths.get("eVA.log");
                    Files.write(log_fn, logFormat(msg).getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                } else {
                    Files.write(log_fn, logFormat(msg).getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

