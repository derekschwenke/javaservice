JDBC to SOAP Java Service
========

The e-VA Integration IN Process Java Service of the
Executive Virtual Assistant (e-VA) Integration Project.  This Java process bridges between the corp DB e-VA tables and the BGS SOAP Service. 

This README and code is under development.

See Specs:
    TBD


JavaService
--------------

Input:
- JDBC PL-SQL is input (triggered) from corp DB
- Configuration file _eva.config_
      
Output:
- SOAP is sent to BGS Services
- LOG messages written to a file

Structure:
- Configuration.java keeps the configuration values up to date
- JDBCService.java registers callbacks and waits for events
- SOAPClient.java sends updates and bla bla bla.

Requirements: TBD

JavaService Command Line
======================

NAME

    JavaService - Start serving sql to bgs!

SYNOPSIS

    java javaService [...] 

OPTIONS

   see eva.config file

HOW TO RUN: (tbd)
1. Clean up old .log and .soap files
1. Ask for the full template_update.xml from derek.
2. Start BGS server
3. Start Oracle server
4. Start JavaService 


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