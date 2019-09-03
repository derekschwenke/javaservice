Java Service
========

The Java Service is part of the Executive Virtual Assistant (e-VA) IN process.  
This service bridges between the IN process e-VA tables and the BGS service. 

    
Input:
- JDBC PL-SQL triggers on case note changes 
- Configuration file _eva.config_ 
- Soap template file _template_update.xml_
- Optional _test/response.xml_
- Optional _test/case_notes.xml_
      
Output:
- SOAP is sent to BGS Services
- Log messages written to _eVA.log_ file

Code structure:
- JavaService.java manages the service and log files.
- CaseNote.java holds the data records being transmitted.
- Configuration.java keeps the configuration values up to date.
- JDBCService.java registers callbacks and receives case notes.
- SOAPClient.java sends the case notes to BGS Server.

There are two possible designs. The first based on stored procedure methods from PL/SQL triggered by changes in the database. 
The second design is a thin JDBC client that polls a table to retrieve any case notes to send 
and mark the status in the table.
Database triggers insert changed case note records into this table.

Requirements:
- Java 1.8 or Java 1.6 
- Oracle 11g 
- ojdbc (ojdbc8.jar)

Notes for running this version 
- The new _test/response.xml_ file allows you to use or bypass BGS as needed.  To use BGS, rename this file.
- The new _jdbc-url_ setting allows you to use or bypass JDBC
- You need a full copy of _template_update.xml_ 

To Build:

   javac src\gov\va\eva\*.java 


JavaService Command Line
======================

NAME

    JavaService - Send Case Notes from sql to bgs service

SYNOPSIS

    java -cp "./src" or "(ojdbc8.jar : javaservice.jar)" gov.va.eva.JavaService [...] 


HOW TO RUN: (tbd)
1. Ask for the full template_update.xml from derek.
1. Start BGS server
1. Start Oracle server
1. Start JavaService 

INPUT: _template_update.xml_ omitted for security

INPUT: _test/case_notes.txt_

    1001 CatA 1002 29-AUG-19 The first Case Note one
    2002 CatA 2002 29-AUG-19 The second Case Note
    ""   CatA 3002 29-AUG-19 The third Case Note    

OUTPUT: _console_

    2019 08 29 21:24:48 Java Service version 0.5 starts. configuration version 0.5
    2019 08 29 21:24:48 Empty jdbc-url supplied in config.txt file, no jdbc server.
    2019 08 29 21:24:48 CaseNote 1001 CatA 1002 29-AUG-19 The first Case Note one 
    2019 08 29 21:24:48 CaseNote 2002 CatA 2002 29-AUG-19 The second Case Note 
    2019 08 29 21:24:48 CaseNote  CatA 3002 29-AUG-19 The third Case Note 

Process finished with exit code 0
    
OUTPUT: _eVA.log_

    2019 08 29 21:24:48 Java Service version 0.5 starts. configuration version 0.5
    2019 08 29 21:24:48 Empty jdbc-url supplied in config.txt file, no jdbc server.
    2019 08 29 21:24:48 CaseNote 1001 CatA 1002 29-AUG-19 The first Case Note one 
    2019 08 29 21:24:48 CaseNote 2002 CatA 2002 29-AUG-19 The second Case Note 
    2019 08 29 21:24:48 CaseNote  CatA 3002 29-AUG-19 The third Case Note 

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
        Build Java Service logging functions [C]
        Construct CaseNotes from (test) file [C]
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
        Report BGS ready to PL/SQL [F]
        Write unit/module/system tests [TNF]
        Release code on GitHub and VA [CTNF]
        
        Other & dependencies
        Resolve problems in the specs.[C]
        Decide if Java Services should be stateless or have a persistent queue.[N]
        If required, build and test the persistent queue in each state.[F]
        Get PIV-GFE network access [F]
        Set up the cloud environment  [F]
        
        Key:
        [C] Completed/Done
        [T] This week
        [N] Next week
        [F] Future (later) (depends on more services)