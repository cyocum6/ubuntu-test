package com.cyocum.classes;


public class Settings{

    private int id;
    private int temp1;
    private int temp2;

    public Settings()
   {}

    public Settings(int id, int temp1, int temp2)
    {
        this.id = id;
        this.temp1 = temp1;
        this.temp2 = temp2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemp1() {
        return temp1;
    }

    public void setTemp1(int temp1) {
        this.temp1 = temp1;
    }

    public int getTemp2() {
        return temp2;
    }

    public void setTemp2(int temp2) {
        this.temp2 = temp2;
    }

}