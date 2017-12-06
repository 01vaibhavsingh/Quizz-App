package com.myapp.vaibhav.quizz;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Session {
    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;
    public Context ctx;

    public Session(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("myapp", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedIn(boolean logggedin, String email, String password){
        editor.putBoolean("loggedInMode",logggedin);
        editor.putString("email",email);
        editor.putString("password",password);
        editor.commit();
    }

    public HashMap<String, String> getLoggedIn(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put("email", prefs.getString("email", null));
        user.put("password", prefs.getString("password", null));
        return user;
    }

    public boolean loggedIn(){
        return prefs.getBoolean("loggedInMode", false);
    }
}