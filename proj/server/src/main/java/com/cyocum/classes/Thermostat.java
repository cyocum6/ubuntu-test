package com.cyocum.classes;

import java.sql.Timestamp;
import java.time.Instant;

// public interfacing
public interface Thermostat {
    
}

// Temperature operations
public class Temperature implements Thermostat{

    private int id;
    private int temp1;
    private int temp2;
    private String setting;

    // ID
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // Setting
    public String getSetting() {
        return setting;
    }
    public void setSetting(String name) {
        this.setting = name;
    }

    // Temp 1
    public int getTemp1() {
        return temp1;
    }
    public void setTemp1(int temp1) {
        this.temp1 = temp1;
    }

    // Temp 2
    public int getTemp2() {
        return temp2;
    }
    public void setTemp2() {
        this.setting = temp2; 
    }

}

// Current State
public class State implements Thermostat {
    
    // need to set on/off for heat/no-heat
    private boolean on = true;      // = heat
    private Timestamp date;

    // function to change heat state
    public static State setState(boolean setHeat) {
        State state = new State();
        state.setOn(setHeat);
        return state;
    }

    // return for setting state request
    public boolean isOn() {
        return on;
    }

    // return for change state request
    public setOn(boolean on) {
        this.on = on;
    }

    // needed for morning, noon, night
    public Timestamp getDate() {
        return date;
    }
    public void setDate(Timestamp date) {
        this.date = date;
    }

}

public class Report implements Thermostat {

    private int id;
    private int temp1;
    private Timestamp date;

    public final static Report publishReport(int temp1) {
        Report report = new Report();
        report.setTemp(temp1);
        report.setDate(Timestamp.from(Instant.now()));
        return report;
    }

    // ID
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // Temp 1
    public int getTemp1() {
        return temp1;
    }
    public void setTemp(int temp1) {
        this.temp1 = temp1;
    }

    // Timestamp
    public Timestamp getDate() {
        return date;
    }
    public void setDate(Timestamp date) {
        this.date = date;
    }
}































/*
// example
public class xxxx extends yyyy implements zzzz {
    Workerclass workerclass;

    @Override
    protected void onCreate(Bundle savedIntanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workerclass = new Workerclass(this);
    }

    @Override
    public void callBack(Workerclass newWorkerclass) {
        Log.i("random message", newWorkerclass.testMessage);
    }
}

public interface CallBack Interface {
    public void callBack(WorkerClass newWorkerclass);
}

public class Workerclass {
    private CallBackInterface callBackInterface;
    public String testMessage = "";

    public Workerclass(CallBackInterface mainActivityInstance) {
        callBackInterface = mainActivityInstance;
        testMessage = "try it now";
        callBackInterface.callBack(this);
    }
}
    */