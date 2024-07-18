package com.cyocum;

import com.google.gson.Gson;
import com.cyocum.classes.Temperature;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;


public final class Util {
    // final -> makes non-access modifier for classes

    private static final String NO_RESOURCE = "The table is empty!\n";
        // static if in separate file, else private -> same file
    private static final String STATE = "state";
    private static final String TEMP = "temp";
    //private static final String REPORT = "report";

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
               // State state = JDBCConnection.getState();
               // if (state == null) {
                   // jsonResp = Boolean.toString(true);
              //  }
               // jsonResp = Boolean.toString(state.isOn());
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
        }
        return null;
    }
        
}
