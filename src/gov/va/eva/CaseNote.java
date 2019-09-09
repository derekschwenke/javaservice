package gov.va.eva;

/**
 * Case note record
 * This design uses all Strings for all Case note fields, because in SOAP (and log files) the CaseNote is a caseDcmntDTO, which is all Strings.
 */


// Case note caseDcmntDTO record
class CaseNote {
    // All data fields are strings, as shown in XML and LOG files:
    String caseDcmntId;
    String bnftClaimNoteTypeCd;
    String caseId;
    String modifdDt;
    String dcmntTxt;
    // These are response fields
    String result;
    String error;

    CaseNote(String caseDcmntId, String bnftClaimNoteTypeCd, String caseId, String modifdDt, String dcmntTxt) {
        this.caseDcmntId = caseDcmntId;
        this.bnftClaimNoteTypeCd = bnftClaimNoteTypeCd;
        this.caseId = caseId;
        this.modifdDt = modifdDt;
        this.dcmntTxt = dcmntTxt;
        this.result = null;
        this.error = null;
    }

    String clean(String s, int length) {
        if (s==null) s = "";
        if (s.length()>length) s = s.substring(0, length);
        s = s.replaceAll("\n"," ");
        return s;
    }

    public String toString() {
        String e = "";
        if (this.error != null) e = "      >>> " + this.error + " <<< ";
        return ("CaseNote "
                + this.caseDcmntId + " "
                + this.bnftClaimNoteTypeCd + " "
                + this.caseId + " "
                + this.modifdDt + " "
                + clean(this.dcmntTxt,80) + " "
                + e);
    }

    private String tag(String tag, String value) {
        return ("\n<" + tag + ">" + value + "</" + tag + ">");
    }

    public String getResultTag(String tag) {
        try {
            String s = this.result.split("<" + tag + ">")[1];
            return s.split("</" + tag + ">")[0];
        } catch (Exception e) {
            this.error = "Missing tag " + tag + " in xml result";
            return "";
        }
    }

    private boolean isValid(String s) {return ((s != null) & (s.length() > 0));}

    String toCaseDcmntDTO() {  // Note JAXB Marshaller
        String xml = "<CaseDcmntDTO>";
        if (isValid(this.caseDcmntId))
            xml += tag("caseDcmntId", this.caseDcmntId);
        xml += tag("bnftClaimNoteTypeCd", this.bnftClaimNoteTypeCd);
        xml += tag("caseId", this.caseId);
        xml += tag("modifdDt", this.modifdDt);
        xml += tag("dcmntTxt", this.dcmntTxt);
        xml += "\n</CaseDcmntDTO>";
        return xml;
    }

    /* This code is not used, but it will be needed to escape chars found in dcmntTxt for SOAP. */
    String encode(String s) {
        s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        s = s.replaceAll("\"", "&quot;");
        return s;
    }

}
