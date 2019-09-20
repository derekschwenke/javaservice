Java Service
========

The Java Service is part of the Executive Virtual Assistant (e-VA) IN process.  
This service bridges between the IN process e-VA tables and the BGS service. 

    
Input:
- JDBC PL-SQL triggers on case note changes 
- Configuration file _eva.config_ 
- Soap template file _template.xml_
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
- Java 1.6 to Java 1.11 
- Oracle 11g 
- ojdbc (ojdbc8.jar)

Notes for version 0.7 
- Template_update.xml renamed _template.xml_
- File _config.txt_ renamed _eVA.config_
- File _eva.log_ renamed _eVA.dat_
- eVA.config names changed:
   - _log-to-console:on_  replaces log-console
   - _log-to-file:on_     replaces log-file
   - _log-dcmntTxt-length:40_  is new


- The new _test/response.xml_ file allows you to use or bypass BGS as needed.  To use BGS, rename this file
- The new _jdbc-url_ setting allows you to use or bypass JDBC
- _caseID_ field capitalization (soap) changed to _caseId_ to match BGS
- Update and Insert can be simulated in the _case_notes.xml_ file by using SINGLE quotes '' for any empty fields  

Copy these files that are not in the .jar file:
- _eVA.config_
- _test/case_nates.txt_
- _test/response.xml_ to bypass BGS
- _template.xml_ full version

To build from source:

   javac src\gov\va\eva\\*.java 


JavaService Command Line
======================

NAME

    JavaService - Send Case Notes from sql to bgs service

SYNOPSIS

    java -cp "./ojdbc6.jar:./JavaService.jar" gov.va.eva.JavaService [...] 
    
Setting Java Classpath

In the classpath string on Unix one ":" separates two paths (On Windows ";" separates two paths)  
Some shells use "./" for the current directory. Alternatively use all jars in a directory:

    java -cp "./*" gov.va.eva.JavaService

HOW TO RUN: (tbd)
1. Ask for the full template.xml from derek.
1. Start BGS server
1. Start Oracle server
1. Start JavaService 

INPUT: _template.xml_ omitted for security

INPUT: _eVA.config_

    This file controls the operation. 
    - tbd 1

INPUT: _test/case_notes.txt_

    1001 CatA 1002 29-AUG-19 The first Case Note one
    2002 CatA 2002 29-AUG-19 The second Case Note
    ''   CatA 3002 29-AUG-19 The third Case Note    

OUTPUT: _console_

    2019 08 29 21:24:48 Java Service version 0.5 starts. configuration version 0.5
    2019 08 29 21:24:48 Empty jdbc-url supplied in eVA.config file, no jdbc server.
    2019 08 29 21:24:48 CaseNote 1001 CatA 1002 29-AUG-19 The first Case Note one 
    2019 08 29 21:24:48 CaseNote 2002 CatA 2002 29-AUG-19 The second Case Note 
    2019 08 29 21:24:48 CaseNote  CatA 3002 29-AUG-19 The third Case Note 

Process finished with exit code 0
    
OUTPUT: _eVA.dat_

    2019 08 29 21:24:48 Java Service version 0.5 starts. configuration version 0.5
    2019 08 29 21:24:48 Empty jdbc-url supplied in eVA.config file, no jdbc server.
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


Setting Java Classpath
In the classpath string on Unix one ":" separates two paths (On Windows ";" separates two paths).
Some shells use "./" for the current directory. 

java -cp "./ojdbc6.jar:./JavaService.jar" gov.va.eva.JavaService

Or load all jars in a directory:

java -cp "./*" gov.va.eva.JavaService


    

Development plans
=================
The java implementation is developed in sprints that add functions.

Task List and Backlog:

        Build auto configuration code [C]
        Build Java Service logging functions [C]
        Construct CaseNotes from (test) file [C]
        Add JDBCServer initialization code and runtime code [F]
        Construct CaseNotes from JDBC calls [F]
        Rate limit the CaseNote production by JDBC server [F]
        Build soap messages from CaseNotes.[C]
        Send soap messages to file for testing, read the response.[C]
        Send soap messages to BGS server [C]
        Log PL/SQL and BGS error status [F]
        Build PL/SQL error processor [F]
        Report BGS errors to PL/SQL [F]
        Report BGS ready to PL/SQL [F]
        Write unit/module/system tests [TNF]
        Release code on GitHub and VA [CTNF]
        
        Other & dependencies
        Resolve problems in the specs.[C]
        Decide if Java Services should be stateless or have a persistent queue.[C]
        If required, build and test the persistent queue in each state.[C]
        Set up the cloud environment  [F]
        Run in development [N]
        Run in production [F]
        
        Key:
        [C] Completed/Done
        [T] This week
        [N] Next week
        [F] Future (later) (depends on more services)