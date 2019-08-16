JDBC to SOAP Java Service
========

The e-VA Integration IN Process Java Service of the
Executive Virtual Assistant (e-VA) Integration Project.  This Java process bridges between the corp DB e-VA tables and the BGS SOAP Service. 

See Specs:
    TBD


JavaService
--------------

Input:
- JDBC PL-SQL triggers on changes
- Configuration file _eva.config_
- Soap template file _templaste_update.xml_

      
Output:
- SOAP is sent to BGS Services
- LOG messages written to _eVA.log_ file

Code structure:
- JavaService.java manages the service and log files.
- CaseNote.java contains the data being sent
- Configuration.java keeps the configuration values up to date
- JDBCService.java registers callbacks and waits for events
- SOAPClient.java sends updates and bla bla bla.

Requirements: ... Java 1.8

JavaService Command Line
======================

NAME

    JavaService - transform sql to bgs soap protocol

SYNOPSIS

    java javaService [...] 


HOW TO RUN: (tbd)
1. Clean up old .log and .soap files
1. Ask for the full template_update.xml from derek.
2. Start BGS server
3. Start Oracle server
4. Start JavaService 

INPUT: _template_update.xml_ omitted

INPUT: _test/case_notes.txt_

    1001 bnftClaimNoteTypeCd 1002 1003 This is the first CaseNote.
    2001 bnftClaimNoteTypeCd 2002 2003 This is the second CaseNote.
    3001 bnftClaimNoteTypeCd 3002 3003 This is the third CaseNote Running nose.    
    
    
OUTPUT: _eVA.log_

    Aug 16, 2019 1:45:01 PM gov.va.eva.JavaService log
    INFO: Java Service version 0.1 starts. configuration version 0.1
    Aug 16, 2019 1:45:01 PM gov.va.eva.JavaService log
    INFO: CaseNote 1001 bnftClaimNoteTypeCd 1002 1003 dcmntTxt
    Aug 16, 2019 1:45:01 PM gov.va.eva.JavaService log
    INFO: CaseNote 1001 bnftClaimNoteTypeCd 1002 1003 This is the first CaseNote.       
    Aug 16, 2019 1:45:01 PM gov.va.eva.JavaService log
    INFO: CaseNote 2001 bnftClaimNoteTypeCd 2002 2003 This is the second CaseNote.
    Aug 16, 2019 1:45:01 PM gov.va.eva.JavaService log
    INFO: CaseNote 3001 bnftClaimNoteTypeCd 3002 3003 This is the third CaseNote Running nose.
    
    Hit ^C to exit.

OUTPUT: _soap.xml_ omitted 

DESCRIPTION

    The Java Servicd flow of control is as follows:

        1. Load config
        2. Init JDBC
        3. Init SOAP
        4. send BGS Ready to JDBC
        5. Loop wait for JDBC updates to send to BGS

    

Development plans
=================
The java implementation will be developed in sprints that add functions.
TBD

Task Backlog:

        Build auto configuration code [C]
        Build Java Service logging functions [T]
        Construct CaseNotes from (test) file [T]
        Add JDBCServer initialization code and runtime code [F]
        Construct CaseNotes from JDBC calls [F]
        Rate limit the CaseNote production by JDBC server [F]
        Build soap messages from CaseNotes.[T]
        Send soap messages to file for testing, read the response.[T]
        Send soap messages to mocked BGS server using WSDL [N]
        Send soap messages to BGS server [F]
        Log PL/SQL and BGS error status [F]
        Build PL/SQL error processor [F]
        Report BGS errors to PL/SQL [F]
        Write unit/module/system tests [TNF]
        Release code on GitHub and VA [TNF]
        
        Other & dependencies
        Resolve problems in the specs.[N]
        Decide if Java Services should be stateless or have a persistent queue.[N]
        If required, build and test the persistent queue in each state.[F]
        Get PIV-GFE network access [F]
        Set up the cloud environment  [F]
        
        Key:
        [C] Completed/Done
        [T] This week
        [N] Next week
        [F] Future (later) (depends on more services)