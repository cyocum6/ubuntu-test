package com.cyocum;

import com.cyocum.classes.Temperature;
import com.cyocum.classes.State;
import com.cyocum.classes.Settings;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class JDBCConnection {

    private static final String DB_CONNECTION = "jdbc:mysql://127.0.0.1:3306/finaltemp";
    private static final String ROOT = "root";
    private static final String PASSWORD = "Coyote1985%$";



    ////////////////////////////////                 GET                  /////////////////////////////
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

    // get state
    public State getState() {

        String select = "select * from state" ;
        State state = new State();
        try ( Connection conn = setupConnection()) {

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
           
            while (resultSet.next()) {               
                state.setState(resultSet.getString("STATE"));
            }
            return state;
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return state;
    }




    public Settings getSetting(String id) {

        String select = "select * from settings where id = " + id;
        try ( Connection conn = setupConnection()) {

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            Settings setting = new Settings();
            while (resultSet.next()) {
                setting.setId(resultSet.getInt("ID"));
                setting.setTemp1(resultSet.getInt("TEMP1"));
                setting.setTemp2(resultSet.getInt("TEMP2"));
            }
            return setting;
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return null;
    }

    // get list of objects from table
    public List<Settings> getSettings() {
        List<Settings> settings = new ArrayList<>();
        String select = "select * from settings";

        try ( Connection conn = setupConnection()) {

            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(select);
            while (resultSet.next()) {

                Settings obj = new Settings();
                obj.setId(resultSet.getInt("ID"));
                obj.setTemp1(resultSet.getInt("TEMP1"));
                obj.setTemp2(resultSet.getInt("TEMP2"));

                settings.add(obj);

            }

        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
        }
        return settings;
    }






    ///////////////                       ADD                   ////////////////////

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
        String insert = "insert into state (state) values ('" + state + "')";
        //String insert = "update state SET state = '"+ state + "'";
        try ( Connection conn = setupConnection()) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(insert);
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Post Failed\n";
        }
        return "Post Successful\n";
    }

    public String addSetting(Settings setting) {
        //String insert = "insert into settings (id, temp1, temp2) values ('" + setting.getId()+ "," 
            //+ setting.getTemp1() + "," + setting.getTemp2() + "')";



            String insert = "insert into settings (id, temp1, temp2) values (" + setting.getId() +
            "," + setting.getTemp1() + "," + setting.getTemp2() + ")";

        
        //String insert = "update state SET state = '"+ state + "'";
        try ( Connection conn = setupConnection()) {
            Statement statement = (Statement) conn.createStatement();
            statement.execute(insert);
        } catch (SQLException ex) {
            System.err.format("SQL State: %s\n%s", ex.getSQLState(), ex.getMessage());
            return "Post Failed\n";
        }
        return "Post Successful\n";
    }

   
    //////////////////////                      DELETE                //////////////////////

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
