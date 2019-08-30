package gov.va.eva;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/*  BFS SOAP interface */

public class SOAPClient {
    private static JavaService javaService;
    private static final String update_fn = "template_update.xml";
    private static final String out_fn = "test/soap.xml";
    private static final String res_fn = "test/response.xml";
    private static final String in_fn = "test/case_notes.txt";
    private final Configuration config = Configuration.get();


    public SOAPClient(JavaService service) {
        javaService = service;
    }


    private void sendToBGS(CaseNote note, String request) throws IOException {
        URL url = new URL(config.getString("bgs-url"));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(request);
        wr.flush();
        wr.close();

        String responseStatus = con.getResponseMessage(); // Send
        javaService.log("BGS send status: " + responseStatus);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        note.result = response.toString();
    }

    private void sendToFile(CaseNote note, String request) throws IOException {
        Files.write(Paths.get(out_fn), request.getBytes());  // write to fake file
        note.result = new String(Files.readAllBytes(Paths.get(res_fn)));  // read from fake file
    }


    void sendCaseNote(CaseNote note) {
        try {
            String template = new String(Files.readAllBytes(Paths.get(update_fn)));
            String request = template.replace("<CaseDcmntDTO/>", note.toCaseDcmntDTO());
            Files.write(Paths.get(out_fn), request.getBytes());  // Save to fake output file
            if (new File(res_fn).exists()) {
                sendToFile(note, request);
            } else {
                sendToBGS(note, request);
            }

            String status = note.getResultTag("jrnStatusTypeCd").toLowerCase();
            if ((status.equals("i") | status.equals("u")) == false)
                note.error = "Error wong BGS status: " + status + ".";
        } catch (IOException e) {
            note.error = "Error" + e.toString();
            e.printStackTrace();
        }
        javaService.log(note.toString());
    }

    /* This code will be moved to a unit test someday */
    void tests() {
        testCaseNotes();
    }

    // reads notes from file. The case notes are one per line. They can not have \n new lines in this test.
    private void testCaseNotes() {
        try {
            Scanner scan = new Scanner(new File(in_fn));

            while (scan.hasNextLine()) {
                String[] f = scan.nextLine().split(" ", 5);
                for (int i=0;i<f.length;i++) if ("\"\"".equals(f[i])) f[i] = ""; // two quotes is the empty string.
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
