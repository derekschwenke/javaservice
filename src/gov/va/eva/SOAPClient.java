package gov.va.eva;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Scanner;

/*  BGS SOAP client interface */

public class SOAPClient {
    private static JavaService javaService;
    private static final String update_fn = "template.xml";
    private static final String out_fn = "test/soap.xml";
    private static final String res_fn = "test/response.xml";
    private static final String in_fn = "test/case_notes.txt";
    private final Configuration config = Configuration.get();

    private boolean useBGS() { return(config.getString("bgs-url").length() > 1); }

    public SOAPClient(JavaService service) {
        javaService = service;
        if (!useBGS()) {
            javaService.log("Empty bgs-url supplied in eVA.config file. No bgs server available.");
        }
    }


    private void sendToBGS(CaseNote note, String request) throws IOException {
        URL url = new URL(config.getString("bgs-url"));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(request);
        wr.flush();
        wr.close();

        String responseStatus = con.getResponseMessage(); // "OK" or not
        javaService.log("BGS send status: " + responseStatus);  // Delete this line

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        note.result = response.toString();
    }

    private String readFile(String fn) throws IOException {
        Scanner in = new Scanner(new File(fn));
        in.useDelimiter("\\Z");
        String text = in.next();
        in.close();
        return text;
    }

    private void writeFile(String fn, String data) throws IOException {
        FileWriter out = new FileWriter(new File(fn));
        out.write(data);
        out.close();
    }

    private void sendToFile(CaseNote note, String request) throws IOException {
        writeFile(out_fn,request);      // write to fake output file
        note.result = readFile(res_fn); // read from fake input file  // if (new File(res_fn).exists())
    }


    void sendCaseNote(CaseNote note) {
        note.validate();

        try {
            String template = readFile(update_fn);
            String request = template.replace("<CaseDcmntDTO/>", note.toCaseDcmntDTO());

            if (useBGS()) {
                sendToBGS(note, request);
            } else {
                sendToFile(note, request);
            }

            // Status for Update and Insert
            String status = note.getResultTag("jrnStatusTypeCd").toLowerCase();
            if ((status.equals("i") | status.equals("u")) == false)
                note.setError("BGS status wrong: " + status + ".");
        } catch (IOException e) {
            note.setError("Error" + e.toString());
            e.printStackTrace();
        }
        javaService.log(note.toString());
    }

    /* This code will be moved to a unit test someday */
    /* Read notes from file. The case notes are one per line. They can not have \n new lines in this test. */
    public void testCaseNotes() {
        try {
            if (!(new File(in_fn).exists())) return;
            Scanner scan = new Scanner(new File(in_fn));

            while (scan.hasNextLine()) {
                String[] f = scan.nextLine().split(" ", 5);
                for (int i = 0; i < f.length; i++) if ("''".equals(f[i])) f[i] = ""; // two single quotes is the empty string
                CaseNote note = new CaseNote(f[0], f[1], f[2], f[3], f[4]);
                javaService.receive(note);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//https://stackoverflow.com/questions/22068864/how-to-generate-soap-request-and-get-response-in-java-coding
//https://chillyfacts.com/java-send-soap-xml-request-read-response/            }
