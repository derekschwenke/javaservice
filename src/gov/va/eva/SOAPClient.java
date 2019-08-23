package gov.va.eva;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/*  */

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
        wr.writeBytes(request); // or  note.toCaseDcmntDTO()
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
            javaService.log(note.toString());
            Files.write(Paths.get(out_fn), request.getBytes());  // Save to fake output file
            if (new File(res_fn).exists()) {
                sendToFile(note, request);
            } else {
                sendToBGS(note, request);
            }

            String status = note.getResultTag("jrnStatusTypeCd").toLowerCase();
            note.hasError = (status.equals("i") | status.equals("u")) == false;
        } catch (IOException e) {
            note.result = "Error" + e.toString();
            note.hasError = true;
            e.printStackTrace();
        }
    }

    /* This code will be a unit test someday */
    void test() {
        CaseNote note = new CaseNote("1001", "bnftClaimNoteTypeCd", "1002", "1003", "dcmntTxt");
        sendCaseNote(note);
        testCaseNotes();
    }

    // reads note from lines of file - for testing, the case notes are one per line. They can not have \n new lines.
    private void testCaseNotes() {
        try {
            Scanner scan = new Scanner(new File(in_fn));

            while (scan.hasNextLine()) {
                String[] f = scan.nextLine().split(" ", 5);
                CaseNote note = new CaseNote(f[0], f[1], f[2], f[3], f[4]);
                sendCaseNote(note);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//https://stackoverflow.com/questions/22068864/how-to-generate-soap-request-and-get-response-in-java-coding
//https://chillyfacts.com/java-send-soap-xml-request-read-response/            }
