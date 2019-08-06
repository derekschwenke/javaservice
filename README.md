JDBC to SOAP Java Service
========

e-VA Integration IN Process Java Service.
Executive Virtual Assistant (e-VA) Integration Project.  This process bridges between the corp DB eva tables and the BGS Services. 

Java Service
--------------
  INPUT  JDBC PL-SQL is input from corp DB
  OUTPUT SOAP is sent to BGS


    Java Service
    INPUT JDBC PL-SQL is input from corp DB
    OUTPUT SOAP is sent to BGS
    
    
REQUIREMENTS

Java JAX?

JavaService Command Line
======================

NAME

    javaService - generates a report of from three input files

SYNOPSIS

    java javaService [...] 

OPTIONS

   see config.txt file

DESCRIPTION

    Three input files are read in CSV format in the working directory: users.txt, roles.txt and, permissions.txt.

        1. users.txt contains the User-ID, first_name, last_name
        2. roles.txt contains the Role, User-IDs,,,
        3. permissions.txt contains the Role, Permissions,,,

    The program will report consistency errors in the input files.
    Joining the input data produces six table reports that are printed.

        1. Users Permissions
        2. Permission Users
        3. User Roles
        4. Role Users
        5. Role Permissions
        6. Permission Roles
    
    These tables list all users. Reports focused on each user are also be printed.
    
    

Development plans
=================
A java implementation will explore the solution.

DJANGO DEVELOPMENT

These features are targets for the next sprint. 
- Develop the equivalent of six reports on expanded data 
- Develop a method to search (filter) the users roles and permissions
- Develop a method to reduce the reports. (For example see: "User reports" format) 

Estimates: 2 days ea. 10 days total. completed

