package gov.va.eva;

/**
 * Case note record
 * This design uses all Strings for all Case note fields, but this is not pure java.
 * However, in SOAP (and log files) the CaseNote is a caseDcmntDTO, which is all Strings.
 */



// Case note record
class CaseNote implements java.io.Serializable {
    // All data fields are strings, as shown in XML and LOG files:
    String caseDcmntId;
    String bnftClaimNoteTypeCd;
    String caseID;
    String modifdDt;
    String dcmntTxt;
    // These are responce fields
    String result;
    boolean hasError;

    CaseNote(String caseDcmntId, String bnftClaimNoteTypeCd, String caseID, String modifdDt, String dcmntTxt){
        this.caseDcmntId = caseDcmntId;
        this.bnftClaimNoteTypeCd = bnftClaimNoteTypeCd;
        this.caseID = caseID;
        this.modifdDt = modifdDt;
        this.dcmntTxt = dcmntTxt;
        this.result = null;
        this.hasError = false;
    }

    String trim(String s, int length) {
        if (s != null && s.length() > length)
            s = s.replaceAll("\n","").substring(0, length);
        return s;
    }

    public String toString() {
        return ("CaseNote "
                + this.caseDcmntId + " "
                + this.bnftClaimNoteTypeCd + " "
                + this.caseID + " "
                + this.modifdDt + " "
                + this.dcmntTxt);
    }

    private String tag(String tag, String value){
        return ("\n<"+tag+">"+value+"</"+tag+">");
    }

    String toCaseDcmntDTO() {  // Note JAXB Marshaller
        String xml = "<CaseDcmntDTO>";
        xml += tag("caseDcmntId",this.caseDcmntId);
        xml += tag("bnftClaimNoteTypeCd",this.bnftClaimNoteTypeCd);
        xml += tag("caseID",this.caseID);
        xml += tag("modifdDt",this.modifdDt);
        xml += tag("dcmntTxt",this.dcmntTxt);
        xml += "\n</CaseDcmntDTO>";
        return xml;
    }

    /* This code is not used, but I think we will need to encode chars of dcmntTxt. */
     String encode(String s){
        s = s.replaceAll("&","&amp;");
        s = s.replaceAll("<","&lt;");
        s = s.replaceAll(">","&gt;");
        s = s.replaceAll("\"","&quot;");
        return s;
    }

}
