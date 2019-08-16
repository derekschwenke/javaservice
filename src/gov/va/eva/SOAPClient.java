package gov.va.eva;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/*

 */

public class SOAPClient {
    private static JavaService javaService;
    static final String update_fn = "template_update.xml";
    static final String out_fn = "test/soap.xml";
    static final String in_fn = "test/case_notes.txt";
    Configuration config = Configuration.get();


    public SOAPClient(JavaService service) {
        this.javaService = service;
    }


    private void send(CaseNote note) throws IOException {
        URL url = new URL(config.getString("bgs-url"));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes( note.toCaseDcmntDTO() );
        wr.flush();
        wr.close();
        String responseStatus = ""; // con.getResponseMessage();
        System.out.println(responseStatus);

        /*
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
        }
        in.close();
        System.out.println("response:" + response.toString());
        } catch (Exception e) {
        System.out.println(e);
https://stackoverflow.com/questions/22068864/how-to-generate-soap-request-and-get-response-in-java-coding
https://chillyfacts.com/java-send-soap-xml-request-read-response/            }
         */
    }


    public void sendCaseNote(CaseNote note) {
        try {
            String template = new String(Files.readAllBytes(Paths.get(update_fn)));
            String request = template.replace("<CaseDcmntDTO/>",note.toCaseDcmntDTO());
            JavaService.log(note.toString());
            Files.write(Paths.get(out_fn), request.getBytes());
            send(note);
        } catch (IOException e) {
            note.result = "Error" + e.toString();
            note.hasError = true;
            e.printStackTrace();
        }
    }

    public void test() {
        CaseNote note = new CaseNote("1001","bnftClaimNoteTypeCd","1002","1003","dcmntTxt");
        //System.out.println(note.toString());
        sendCaseNote(note);
        testCaseNotes();
    }

    // reads note from lines of file - for testing only the txt can not have \n.
    public void testCaseNotes() {
        try {
            Scanner scan = new Scanner( new File(in_fn) );

            while (scan.hasNextLine()) {
                String[] f = scan.nextLine().split(" ", 5);
                CaseNote cn = new CaseNote(f[0],f[1],f[2],f[3],f[4]);

                //System.out.println(cn.toString());
                //JavaService.log( cn.toString() );
                // do something more  send()
                sendCaseNote(cn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
