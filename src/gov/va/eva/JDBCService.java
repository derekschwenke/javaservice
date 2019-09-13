package gov.va.eva;

import java.sql.*;
// note to Aad to IDE : Left click JavaService, and Open Module Settings F4, Project Structure Dialog, Projects Settings, Libraries, "+" , Java


/* Example from https://examples.javacodegeeks.com/core-java/sql/jdbc-oracle-thin-driver-example/*/

public class JDBCService {
    private static JavaService javaService;
    private static Connection conn;
    private final Configuration config = Configuration.get();


    public JDBCService(JavaService service) {
        Statement query = null;
        ResultSet resultSet = null;
        javaService = service;
        String jdbc_url = config.getString("jdbc-url");

        if ( jdbc_url.length() < 2) {
            javaService.log( "Empty jdbc-url supplied in eVA.config file, no jdbc server." );
            return;
        }
        try {
            // Step 1 - Register Oracle JDBC Driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Step 2 - Creating Oracle Connection Object  jdbc:oracle:thin:@//<host>:<port>/<service_name> see https://razorsql.com/articles/oracle_jdbc_connect.html
            conn = DriverManager.getConnection(jdbc_url, config.getString("jdbc-user"), config.getString("jdbc-password"));
            System.out.println("Connected With Oracle is" + (conn != null));

            // Step 3 - Creating Oracle Statement Object
            query = conn.createStatement();

            // Step 4 - Execute SQL Query
            resultSet = query.executeQuery(config.getString("jdbc-query"));

            // Step 5 - Read results
            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1) + ", " + resultSet.getString(2) + ", " + resultSet.getFloat(3) + "$");
                int id = resultSet.getInt(1);
                CaseNote note = new CaseNote(String.valueOf(id), "TYPE", "5000", "5000", "dcmntTxt for the case note.");
                javaService.log("JDBC read "+ note.toString());
                javaService.receive(note);
                // check note.error here
            }
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (query != null) {
                    query.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

    /*  Poll the database case note table for changes. */
    public void poll() {
        // TBD System.out.println("JDBCService poll" );

    }

    /*  This stored procedure is never called */
    public static void insertCaseNote(int caseDcmntId) throws SQLException {
        javaService.log("STORED PROCEDURE HAS BEEN WAS CALLED caseDcmntId = " + caseDcmntId);
        CaseNote note = new CaseNote(String.valueOf(caseDcmntId), "TYPE", "4000", "4000", "dcmntTxt for the case note.");
        javaService.log(note.toString());
        javaService.receive(note);
        if (note.error != null) {
            throw new SQLException("JavaService Reported Error");
        }
    }


    /* Start with this oracle 18 example
//https://docs.oracle.com/en/database/oracle/oracle-database/18/jjdev/calling-Java-from-database-triggers.html#GUID-5C498DEF-0348-484D-AA26-2A88EF348D5C
//https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html


class JDBCService_18 {
    private static JavaService javaService;

    public JDBCService_18(JavaService service) {
        javaService = service;
    }

    public static void insertCaseNote(int caseDcmntId) throws SQLException {
        javaService.log("STORED PROCEDURE HAS BEEN WAS CALLED caseDcmntId = " + caseDcmntId);
        CaseNote note = new CaseNote(String.valueOf(caseDcmntId), "TYPE", "4000", "4000", "dcmntTxt for the case note.");
        javaService.log(note.toString());
        javaService.receive(note);
        if (note.hasError) {
            throw new SQLException("JavaService Reported Error");
        }
    }
}


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


}

