package com.cyocum;

import com.cyocum.classes.State;
import com.cyocum.classes.Temperature;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class JDBCConnection {

    private static final String DB_CONNECTION = "jdbc:mysql://127.0.0.1:3306/tempDB";
    private static final String ROOT = "root";
    private static final String PASSWORD = "Coyote1985%$";

    private JDBCConnection() {

    }

    private final static Connection setupConnection() throws SQLException {
        return DriverManager.getConnection(DB_CONNECTION, ROOT, PASSWORD);
    }

    // Temperature 
    // What is the current temperature? (find by ID)
    public final static Temperature getTemperature(String id) {

        String select = "select * from TempData where id = " + id;
        try ( Connection conn = setupConnection()) {

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            Temperature console = new Temperature();
            while (resultSet.next()) {
                console.setId(resultSet.getInt("ID"));
                console.setSetting(resultSet.getString("NAME"));
            }
            return console;
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return null;
    }

    // What is current temperature set to?
    public final static Temperature getTemperatureSetting(String setting) {
        String select = "select * from temps where setting = '" + setting + "'";
        try (Connection conn = setupConnection()) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            Temperature temp1 = new Temperature();
            while (resultSet.next()) {
                temp.setId(resultSet.getInt("ID"));
                temp.setTemp1(resultSet.getInt("TEMP1"));
                temp.setTemp2(resultSet.getInt("TEMP2"));
                temp.setSetting(resultSet.getString("SETTING"));
            }
            return temp1;

        }
        catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return null;
    }

    // Get the temperatures
    public final static List<Temperature>  getAllTemps() {
        List<Temperature> temps = new ArrayList<>();
        String select = "select * from temps";
        try (Connection conn = setupConnection()) {
            Statement statement = conn.createStatement();
            ResetSet resultSet = statement.executeQuery(select);
            while (resultSet.next()) {
                Temperature obj = new Temperature();
                obj.setId(resultSet.getInt("ID"));
                obj.setSetting(resultSet.getString("SETTING"));
                obj.setTemp1(resultSet.getInt("TEMP1"));
                obj.setTemp2(resultSet.getInt("TEMP2"));
                temps.add(obj);
            }
        }
        catch (SQLException ex) {
            Sytem.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return temps;
    }

    // update the temperature
    public final static String updateTemp(Tempurature temp) {
        String update = "update temps set temp1 = " + temp.getTemp1() + 
                        ", temp2 = " + tempgetTemp2() + 
                        " where id = " + temp.getId();
        try (Connection conn = setupConnection()) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(update);
        }
        catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Post temp failed\n";
        }
        return "Post temp successful\n";
    }

    // delete temperature record from database
    public final static String deleteTemp(String id) {
        String insert = "delete from temps where id = " + id;
        try (Connection conn = setupConnection()) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(insert);
        }
        catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessgae());
            return "Delete failed\n";
        }
        return "Delete Successful\n";
    }

    // Heat and States
    // Is the heat on?
    public final static State getState() {
        String select = "select * from state";
        try (Connection conn = setupConnection()) {
            Statement statement = conn.createConnection();
            ResultSet resultSet = statement.executQuery(select);
            State state = new State();
            while (resultSet.next()) {
                String currentState = resultSet.getString("STATE");
                if (currentState != null) {
                    state.setOn(true);      // Thermostat
                }
                else {
                    state.setOn(false);
                }
            }
            return temp1;
        }
        catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return null;
    }

    // Add State for Heat
    public final static String addState(State state) {
        String insert = null;
        if (state.isOn()) {
            insert = "insert into state (state, date) values ('', '" + state.getDate() + "')";
        }
        else {
            insert = "insert into state (state, date) values (NULL, '" + state.getDate() + "')";
        }

        try (Connection conn = setupConnection()) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(insert);
        }
        catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Post state failed\n";
        }
        return "Post state successful\n";
    }

    // Update State
    public final static String updateState(boolen value) {
        String update = null;
        if (value) {
            update = "update state set state = ''";
        }
        else {
            update = "update state set state = NULL";
        }

        try (Connection conn = setupConnection()) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(update);
        }
        catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Update state failed\n";
        }
        return "Post state failed\n";
    }

    // Reports:
    // adding reports
    public final static String addReport(Report report) {
        String insert = "insert into report (temp, date) values ('" + report.getTemp()
                        + "', '" + report.getDate() + "')";
        try (Connection conn = setupConnection()) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(insert);
        }
        catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Post state failed\n";
        }
        return "Post Report successful\n";
    }

    // recall published report
    public final static List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();
        String select = "select * from report";
        try (Connection conn = setupConnection()) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            while (resultSet.next()) {
                Report obj = new Report();
                obj.setId(resultSet.getInt("ID"));
                obj.setTemp1(resultSet.getInt("TEMP1"));
                obj.setDate(resultSet.getTimestamp("DATE"));
                reports.add(obj);
            }
        }
        catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            // no return needed, "reports" will be returned
        }
        return "reports";
    }

}
/*
    // get list of objects from table
    public List<Temperature> getConsoles() {
        List<Temperature> consoles = new ArrayList<>();
        String select = "select * from TempData";

        try ( Connection conn = setupConnection()) {

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            while (resultSet.next()) {

                Temperature obj = new Temperature();
                obj.setId(resultSet.getInt("ID"));
                obj.setSetting(resultSet.getString("NAME"));

                consoles.add(obj);

            }

        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return consoles;
    }

    //              adds ids
    public String addConsole(String name) {
        String insert = "insert into TempData (name) values ('" + name + "')";
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
    public String deleteConsole(String id) {
        String insert = "delete from TempData where id = " + id;
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


*/