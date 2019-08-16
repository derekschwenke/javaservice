package gov.va.eva;

import java.sql.*;
//import oracle.jdbc.*;

// Start with this example
//https://docs.oracle.com/en/database/oracle/oracle-database/18/jjdev/calling-Java-from-database-triggers.html#GUID-5C498DEF-0348-484D-AA26-2A88EF348D5C
//https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html


public class JDBCService {
    private static JavaService javaService;

    public JDBCService(JavaService service) {
        this.javaService = service;
    }

    public static void insertCaseNote(int caseDcmntId) throws SQLException {
        JavaService.log("STORED PROCEDURE HAS BEEN WAS CALLED caseDcmntId = " + caseDcmntId);
        CaseNote note = new CaseNote(String.valueOf(caseDcmntId), "TYPE", "4000", "4000", "dcmntTxt for the case note.");
        javaService.log(note.toString());
        javaService.receive(note);
        if (note.hasError) {
            throw new SQLException("JavaService Reported Error");
        }
    }
}

/*

CREATE OR REPLACE PROCEDURE insert_case_note (
 emp_id NUMBER
)
AS LANGUAGE JAVA
NAME 'JDBCService.insertCaseNote(int)';

Finally, create the database trigger, which fires on any update:

CREATE OR REPLACE TRIGGER cn_trig
AFTER UPDATE ON employees
FOR EACH ROW
CALL insert_case_note(:new.employee_id);

 */