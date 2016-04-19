package edu.uark.ndavies.blackout;


import android.app.Application;

public class MyApplication extends Application {

    private String User;
    private boolean start;

    public String getUser() {return User;}
    public boolean getStart(){return start;}

    public void setUser(String someVariable) {
        this.User = someVariable;
    }
    public void setStart(boolean start){this.start = start;}
}
