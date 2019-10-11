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
        String jdbc_url = config.getString("jdbc-url"); // bug - This only checks once, must restart to change

        if ( jdbc_url.length() < 2) {
            javaService.log( "Empty jdbc-url supplied in eVA.config file. No jdbc server available." );
            return;
        }

        // Get record
        // Validate - correct
        // Check corpDB
        // insert or update


        try {
            // Step 1 - Register Oracle JDBC Driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Step 2 - Creating Oracle Connection Object  jdbc:oracle:thin:@//<host>:<port>/<service_name> see https://razorsql.com/articles/oracle_jdbc_connect.html
            conn = DriverManager.getConnection(jdbc_url, config.getString("jdbc-user"), config.getString("jdbc-password"));
            javaService.log("Connected With Oracle is " + (conn != null));

            // if (config.isValid("jdbc-test")) { call_balance(); return; }

            // Step 3 - Creating Oracle Statement Object
            query = conn.createStatement();

            // Step 4 - Execute SQL Query
            resultSet = query.executeQuery(config.getString("jdbc-query"));

            // Step 5 - Read results
            while (resultSet.next()) {
                System.out.println("About to call resultSet.getInt() ");
                int id = resultSet.getInt(1);
                System.out.println("After resultSet.getInt()  " + id);

                CaseNote note = new CaseNote(String.valueOf(id), "TYPE", "5000", "5000", "dcmntTxt for the case note.");
                javaService.log("JDBC read "+ note.toString());
                javaService.receive(note);
                // check note.error here
            }

            if (config.isValid("jdbc-update")) {
                query.close();
                query = conn.createStatement();
                query.executeUpdate(config.getString("jdbc-update"));
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

    /*  This could be called by Oracle someday. gov.va.eva.JDBCService.insertCaseNote( int )  */
    public static void insertCaseNote(int caseDcmntId) throws SQLException {
        javaService.log("INSERTCASENOTE PROCEDURE HAS BEEN WAS CALLED caseDcmntId = " + caseDcmntId);
        CaseNote note = new CaseNote(String.valueOf(caseDcmntId), "TYPE", "4000", "4000", "dcmntTxt for gov.va.eva.JDBCService.insertCaseNote( int ) ");
        javaService.log(note.toString());
        javaService.receive(note);
        if (note.error != null) {
            throw new SQLException("JavaService Reported Error");
        }
    }

    /*  This could call PLSQL someday.  */
    public void call_balance() throws SQLException {
        String pro = config.getString("jdbc-procedure");
        javaService.log("About to prepareCall " + pro );
        CallableStatement cstmt = conn.prepareCall(pro);
        javaService.log("After prepareCall " + pro );
        cstmt.registerOutParameter(1, Types.FLOAT );
        javaService.log("About to setInt() " );
        cstmt.setInt(2, 10000 );
        javaService.log("About to executeUpdate() " );
        cstmt.executeUpdate();
        javaService.log("About to getFloat() " );
        float value = cstmt.getFloat(1);
        javaService.log("jdbc-procedure returned the value "+ value );
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

