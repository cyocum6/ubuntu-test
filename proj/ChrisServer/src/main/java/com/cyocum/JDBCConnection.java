package com.cyocum;

import com.cyocum.classes.Temperature;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class JDBCConnection {

    private static final String DB_CONNECTION = "jdbc:mysql://127.0.0.1:3306/finaltemp";
    private static final String ROOT = "root";
    private static final String PASSWORD = "Coyote1985%$";

    // get id requested 
    public Temperature getTemp(String id) {

        String select = "select * from temp where id = " + id;
        try ( Connection conn = setupConnection()) {

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            Temperature console = new Temperature();
            while (resultSet.next()) {
                console.setId(resultSet.getInt("ID"));
                console.setTemp(resultSet.getString("TEMP"));
            }
            return console;
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return null;
    }

    // get list of objects from table
    public List<Temperature> getTemps() {
        List<Temperature> consoles = new ArrayList<>();
        String select = "select * from temp";

        try ( Connection conn = setupConnection()) {

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            while (resultSet.next()) {

                Temperature obj = new Temperature();
                obj.setId(resultSet.getInt("ID"));
                obj.setTemp(resultSet.getString("TEMP"));

                consoles.add(obj);

            }

        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return consoles;
    }

    //              adds ids
    public String addTemp(String temp) {
        String insert = "insert into temp (temp) values ('" + temp + "')";
        try ( Connection conn = setupConnection()) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(insert);
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Post Failed\n";
        }
        return "Post Successful\n";
    }
   
    public String addState(String state) {
        //String insert = "insert into state (name) values ('" + name + "')";
        String insert = "update state SET state = '"+ state + "'";
        try ( Connection conn = setupConnection()) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(insert);
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Post Failed\n";
        }
        return "Post Successful\n";
    }

    //             delete ids 
    public String deleteTemp(String id) {
        String insert = "delete from temp where id = " + id;
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
