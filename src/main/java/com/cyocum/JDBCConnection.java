package com.cyocum;

import com.cyocum.domain.Console;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class JDBCConnection {

    private static final String DB_CONNECTION = "jdbc:mysql://127.0.0.1:3306/tempDB";
    private static final String ROOT = "root";
    private static final String PASSWORD = "Coyote1985%$";

    // get request based on ID
    public IConsole getConsole(String id) {

        String select = "select * from TempData where id = " + id;
        try ( Connection conn = setupConnection()) {

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            Console console = new Info();
            while (resultSet.next()) {
                console.setId(resultSet.getLong("ID"));
                console.setName(resultSet.getString("NAME"));
            }
            return console;
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return null;
    }

    // get list of objects to fill a table
    public List<Console> getConsoles() {
        List<Console> consoles = new ArrayList<>();
        String select = "select * from TempData";

        try ( Connection conn = setupConnection()) {

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            while (resultSet.next()) {

                Console obj = new Info();
                obj.setId(resultSet.getLong("ID"));
                obj.setName(resultSet.getString("NAME"));

                consoles.add(obj);

            }

        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return consoles;
    }

    // add a console to the database
    public String addConsole(String name) {
        String insert = "insert into TempData (name) values ('" + name + "')\n";
        try ( Connection conn = setupConnection()) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(insert);
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Post Failed\n";
        }
        return "Post Successful\n";
    }

    // delete console from database
    public String deleteConsole(String id) {
        String insert = "delete from console where id = " + id;
        try ( Connection conn = setupConnection()) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(insert);
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Delete Failed\n";
        }
        return "Delete Successful\n";
    }

    private Connection setupConnection() throws SQLException {
        return DriverManager.getConnection(DB_CONNECTION, ROOT, PASSWORD);
    }

}
