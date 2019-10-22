package gov.va.eva;

import java.sql.*;


public class JDBCService {
    private static JavaService javaService;
    private static Connection conn;
    private final Configuration config = Configuration.get();


    public JDBCService(JavaService service) {
        Statement query = null;
        Statement completed = null;
        ResultSet resultSet = null;
        javaService = service;
        String jdbc_url = config.getString("jdbc-url", "-node"); // bug - This only read once. Must restart.

        if (jdbc_url.length() < 2) {
            javaService.log("Empty jdbc-url supplied in eVA.config file.  No jdbc server available.");
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
            conn = DriverManager.getConnection(jdbc_url, config.getString("jdbc-user", "-node"), config.getString("jdbc-password", "-node"));
            javaService.log("Connected With Oracle is " + (conn != null));

            if (config.isValid("jdbc-test")) {
                call_balance();
                return;
            }

            // Step 3 - Creating Oracle Statement Object
            query = conn.createStatement();

            // Step 4 - Execute SQL Query
            resultSet = query.executeQuery(config.getString("jdbc-query", "-node")); // not node

            // Step 5 - Read results
            while (resultSet.next()) {
                System.out.println("About to call resultSet.getInt() ");
                String id = String.valueOf(resultSet.getInt(1));
                System.out.println("After resultSet.getInt()  id=" + id);

                CaseNote note = new CaseNote(id, "TYPE", "5000", "5000", "dcmntTxt for the case note.");
                javaService.log("JDBC read " + note.toString());
                javaService.receive(note);

                if (completed != null) completed.close();
                completed = conn.createStatement();
                String comp = config.getString("jdbc-completed"); // Or read file.
                comp = comp.replace("<ID>", id);
                comp = comp.replace("<STATUS>", (note.error == null) ? "OK" : "ERROR");
                completed.executeUpdate(comp);

                if (note.error != null) {
                    System.out.println("About to call jdbc-error-report " + config.getString("jdbc-error-report"));
                    //throw new SQLException("JavaService Reported Error");
                }
            }

            if (config.isValid("jdbc-update")) {
                query.close();
                query = conn.createStatement();
                System.out.println("About to call jdbc-update " + config.getString("jdbc-update", "-node"));
                query.executeUpdate(config.getString("jdbc-update", "-node"));
                System.out.println("Done jdbc-update");
            }

        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                if (completed != null) {
                    completed.close();
                }
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
        javaService.log("About to prepareCall " + pro);
        CallableStatement statement = conn.prepareCall(pro);
        javaService.log("After prepareCall " + pro);
        statement.registerOutParameter(1, Types.FLOAT);
        javaService.log("About to setInt() ");
        statement.setInt(2, 10000);
        javaService.log("About to executeUpdate() ");
        statement.executeUpdate();
        javaService.log("About to getFloat() ");
        float value = statement.getFloat(1);
        javaService.log("jdbc-procedure returned the value " + value);
    }
}

    // Example from https://examples.javacodegeeks.com/core-java/sql/jdbc-oracle-thin-driver-example/
    //https://docs.oracle.com/en/database/oracle/oracle-database/18/jjdev/calling-Java-from-database-triggers.html#GUID-5C498DEF-0348-484D-AA26-2A88EF348D5C
    //https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html
    // note to add to IDE : Left click JavaService, and Open Module Settings F4, Project Structure Dialog, Projects Settings, Libraries, "+" , Java

