package com.cyocum;

import com.google.gson.Gson;
import com.cyocum.classes.Temperature;
import com.cyocum.classes.Settings;
import com.cyocum.classes.State;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.time;
import java.time.*; 

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;



public final class Util {
    // final -> makes non-access modifier for classes

    private static final String NO_RESOURCE = "The table is empty!\n";
        // static if in separate file, else private -> same file
    private static final String STATE = "state";
    private static final String TEMP = "temp";
    private static final String SETTINGS = "settings";
    //private static final String REPORT = "report";
    private static int TempNow;

    private Util() {
    }

    //perform get request
    public static NanoHTTPD.Response performGet(JDBCConnection connection, NanoHTTPD.IHTTPSession session) {
        String jsonResp = null;
        String route = getRoute(session.getUri());
        String param = getIndex(session.getUri());
        Gson gson = new Gson();
        
        if (route != null) {
            if (route.equals(TEMP)) {
                if (param != null && !param.equals("")) {
                    Temperature temp = connection.getTemp(param);
                    if (temp == null) {
                        return failedAttempt("Temperature value is null\n");
                    }
                    jsonResp = gson.toJson(temp);
                } else {
                    List<Temperature> temps = connection.getTemps(); 
                    if (temps.isEmpty()) {
                        return failedAttempt("Temperature get request is empty.\n");
                    }
                    jsonResp = gson.toJson(temps);
                }
            } else if (route.equals(STATE)) {
                State state = connection.getState();
                if (state == null) {
                    //jsonResp = "ON";
                    return failedAttempt("state get request is empty.\n");
                }
                jsonResp = gson.toJson(state);
            } else if (route.equals(SETTINGS)) {
                if (param != null && !param.equals("")) {
                    Settings setting = connection.getSetting(param);
                    if (setting == null) {
                        return failedAttempt("Settings value is null\n");
                    }
                    jsonResp = gson.toJson(setting);
                } else {
                    List<Settings> settings = connection.getSettings(); 
                    if (settings.isEmpty()) {
                        return failedAttempt("Settings get request is empty.\n");
                    }
                    jsonResp = gson.toJson(settings);
                }
            }
            return newFixedLengthResponse(jsonResp);
        }
        return failedAttempt("Invalid Get request path\n");
    }

    //perform post request
    public static NanoHTTPD.Response performPost(JDBCConnection connection, NanoHTTPD.IHTTPSession session) {
        try {
            session.parseBody(new HashMap<>());
            
            String route = session.getUri().replace("/", "");
            String result = null;
            if (route.equals(TEMP)) {
                String tempt = session.getQueryParameterString();
                CompareTimeNowToSettings(tempt);
                TempNow = Integer.parseInt(tempt);
                result = connection.addTemp(session.getQueryParameterString());
            }
            else if (route.equals(STATE)) {

                result = connection.addState(session.getQueryParameterString());
            }
            return newFixedLengthResponse(result + "\n");
        } catch (IOException | NanoHTTPD.ResponseException e) {
            return failedAttempt("Unable to perform POST request\n");
        }
    }

    //perform delete request
    public static NanoHTTPD.Response performDelete(JDBCConnection connection, NanoHTTPD.IHTTPSession session) {
        
        String route = session.getUri().replace("/", "");
        String result = null;
        if (route == TEMP) {
            result = connection.deleteTemp(getIndex(session.getUri()));
            return newFixedLengthResponse(result);
        } else if (route == STATE) {
           // result = connection.deleteState(getIndex(session.getUri()));
            return newFixedLengthResponse(result);
        }
        return newFixedLengthResponse(result + "\n");

    }

    public static NanoHTTPD.Response failedAttempt(String message) {
        return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, MIME_PLAINTEXT,
        message);
    }

    
    // VS Code lies, it's needed
    private static String getIndex(String param) {
        return param.replaceAll("[^0-9]", "");
    }

    private static String getRoute(String param) {
        if (param.contains(TEMP)) {
            return TEMP;
        } else if (param.contains(STATE)) {
            return STATE;
        }else if (param.contains(SETTINGS)) {
            return SETTINGS;
        }
        return null;
    }

 //////////////////////////////////////////////performing settings///////////////////////////////////
     private static String decodePeriod() {
        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        if (hour >= 18) {
            return "EVENING";
        } else if (hour >= 12) {
            return "AFTERNOON";
        } else {
            return "MORNING";
        }
    }

    // for system state time and 
    private static void CompareTimeNowToSettings(string Temp)
    {
        Status state = new State();
        Settings setting = new Settings();
        Instance time = Instance.now();
        int hour= getHourNow();
        {
            if (hour >= 18) {
                string ID = Integer.toString(1);
                setting = connection.getSetting(ID);
            } else if (hour >= 12) {
                string ID = Integer.toString(2);
                setting = connection.getSetting(ID);               
            }else
            {
                tring ID = Integer.toString(3);
                setting = connection.getSetting(ID);   
            }
        }

        int currentTemp = Integer.toString(Temp);
        if (currentTemp <= setting.getTemp1())
        {
            connection.addState("ON");            
        }
        else if (currentTemp > setting.getTemp2()) {
            connection.addState("OFF");            
        }
    }

    public static int getHourNow(){ 
           Instance time = Instance.now();  
            int Hour = time.getHour(); 
    }
}
