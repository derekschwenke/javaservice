package gov.va.eva;

/**
 * Case note record
 * In SOAP CaseNote is a caseDcmntDTO, and also needs to be String.
 * This design uses all Strings for the Note because XML only sends Strings.
 */

/*
<xs:complexType name="caseDcmntDTO">
<xs:sequence>
<xs:element name="bnftClaimNoteTypeCd" type="xs:string" minOccurs="0"/>
<xs:element name="caseDcmntId" type="xs:long" minOccurs="0"/>
<xs:element name="caseId" type="xs:long" minOccurs="0"/>
<xs:element name="dcmntTxt" type="xs:string" minOccurs="0"/>
<xs:element name="jrnDt" type="xs:dateTime" minOccurs="0"/>
<xs:element name="jrnLctnId" type="xs:string" minOccurs="0"/>
<xs:element name="jrnObjId" type="xs:string" minOccurs="0"/>
<xs:element name="jrnStatusTypeCd" type="xs:string" minOccurs="0"/>
<xs:element name="jrnUserId" type="xs:string" minOccurs="0"/>
<xs:element name="modifdDt" type="xs:dateTime" minOccurs="0"/>
</xs:sequence>
</xs:complexType>

Do I replace <,>," with &lt; &gt; &quot; in dcmntTxt ??

 */


// Case note record
class CaseNote implements java.io.Serializable {
    // All data fields are strings, as shown in XML and LOG files:
    public String caseDcmntId;
    public String bnftClaimNoteTypeCd;
    public String caseID;
    public String modifdDt;
    public String dcmntTxt;
    // These are responce fields
    public String result;
    public boolean hasError;

    public CaseNote(String caseDcmntId, String bnftClaimNoteTypeCd, String caseID, String modifdDt, String dcmntTxt){
        this.caseDcmntId = caseDcmntId;
        this.bnftClaimNoteTypeCd = bnftClaimNoteTypeCd;
        this.caseID = caseID;
        this.modifdDt = modifdDt;
        this.dcmntTxt = dcmntTxt;
        this.result = null;
        this.hasError = false;
    }

    public void toLog() {
        JavaService.log(this.toString());
    }

    public String toString() {
        return ("CaseNote "
                + this.caseDcmntId + " "
                + this.bnftClaimNoteTypeCd + " "
                + this.caseID + " "
                + this.modifdDt + " "
                + this.dcmntTxt);
    }


    private String tagXMLI(String tag, int value){
        return ("\n<"+tag+">"+value+"</"+tag+">");
    }
    private String tagXML(String tag, String value){
        return ("\n<"+tag+">"+value+"</"+tag+">");
    }

    private String encode(String s){
        s = s.replaceAll("&","&amp;");
        s = s.replaceAll("<","&lt;");
        s = s.replaceAll(">","&gt;");
        s = s.replaceAll("\"","&quot;");
        return s;
    }

    public String toCaseDcmntDTO() {
        String xml = "<CaseDcmntDTO>";
        xml += tagXML("caseDcmntId",this.caseDcmntId);
        xml += tagXML("bnftClaimNoteTypeCd",this.bnftClaimNoteTypeCd);
        xml += tagXML("caseID",this.caseID);
        xml += tagXML("modifdDt",this.modifdDt);
        xml += tagXML("dcmntTxt",this.dcmntTxt);
        xml += "\n</CaseDcmntDTO>";
        return xml;
    }

}
