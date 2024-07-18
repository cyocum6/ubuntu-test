package com.cyocum;

import com.google.gson.Gson;
import com.cyocum.classes.Temperature;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;
import static com.cyocum.JDBCConnection.addReport;
import static com.cyocum.JDBCConnection.addState;
import static com.cyocum.JDBCConnection.updateTemp;


public final class Util {
    // final -> makes non-access modifier for classes

    private final static String DELIM = ",";
    private final static String DEC = ".000000";
    private final static String TYPE_DELIM = ":";
    private final static String STATE = "state";
    private final static String TEMP = "temps";
    private final static String REPORT = "report";
    private static final String NO_RESOURCE = "The table is empty!\n";
        // static if in separate file, else private -> same file

    private Util() {
    }

    // mini-functions
    private static String cleanValue(String param) {
        return param.replaceAll("[^0-9]", "");
    }

    private static cleanDecimal(String input) {
        return cleanValue(input.replace(DEC, ""));
    }

    // GET function
    public static NanoHTTPD.Response performGet(NanoHTTPD.IHTTPSession session) {
        String jsonResp = null;
        String param = cleanValue(session.getUri());
        String route = getRoute(session.getUri());
        Gson gson = new Gson();

        if (route != null) {
            if (route.equals(TEMP)) {
                if (param != null && !param.equals("")) {
                    Temperature temp = JDBCConnection.getTemperature(param);       // to JDCConnection, Temp section
                    if (temp == null) {
                        return failedAttempt("temp value was null");
                    }
                    jsonResp = gson.toJson(temp);
                }
                else {
                    List<Temperature> temps = JDBCConnection.getAllTemps();         // to JDCConnection, Temp section
                    if (temps.isEmpty()) {
                        return failedAttempt("get request has empty results");
                    }
                    jsonResp = gson.toJson(temps);
                }
            }
            else if (route.equals(STATE)) {
                State state = JDBCConnection.getState();                            // to JDCConnection, Heat section
                if (state == null) {
                    jsonResp = Boolean.toString(true);
                }
                jsonResp = Boolean.toString(state.isOn());
            }
            else if (route.equals(REPORT)) {
                List<Report> reports = JDBCConnection.getAllReports();           // to JDCConnection, Reports section 
                if (reports.isEmpty()) {
                    return failedAttempt("get request has empty results");
                }
                jsonResp = gson.toJson(reports);
            }
        return failedAttempt("improper get url path\n");
        
    }

    // POST function
    public static NanoHTTPD.Response performPost(NanoHTTPD.IHTTPSession session) {
        try {
            String result = null;
            session.parseBody(new HashMap<>());
            String route = session.getUri().replace("/", "");
            Thermostat thermostat = parseRouteParams(session.getQueryParametersString(),route);
            
            if (thermostat instanceof Temperature) {
                result = updateTemp((Temperature) thermostat);
            }
            else if (thermostat instanceof State) {
                result = addState((State) thermostat);
            }
            else if (thermostat instanceof Report) {
                handleTemperatureChange((Report) thermostat);
            }
            // but if it's null....
            if (thermostat == null) {
                return newFixedLengthResponse("temp or time values unsupported");
            }

            return newFixedLengthResponse(result + "\n");
        } catch (IOException | NanoHTTPD.ResponseException e) {
            return failedAttempt();
        }
    }

    // DELETE a temperature
    public static NanoHTTPD.Response performDelete(NanoHTTPD.IHTTPSession session) {
        String route = session.getUri().replace("/", "")
        if (route == TEMP) {
            String result = JDBCConnection.deleteTemp(cleanValue(session.getUri()));
            return newFixedLengthResponse(result);
        }
        else if (route == REPORT) {
            String result = JDBCConnection.deleteTemp(cleanValue(session.getUri()));
            return newFixedLengthResponse(result);
        }
        return failedAttempt("failed to delete object, make sure correct route\n");
    }

    public static NanoHTTPD.Response failedAttempt() {
        return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, MIME_PLAINTEXT,
                NO_RESOURCE);
    }
       
}
