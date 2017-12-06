package com.myapp.vaibhav.quizz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class SplashScreenActivity extends AppCompatActivity {

    private Session session;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        session = new Session(getApplicationContext());
        if(session.loggedIn()){
            HashMap<String, String> user = session.getLoggedIn();
            email = user.get("email");
            password = user.get("password");

            if(isNetworkAvailable()) {
                SigninBackgroundWorker signinBackgroundWorker = new SigninBackgroundWorker();
                signinBackgroundWorker.execute(email, password);
            }
            else{
                Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        else{
            Thread timerThread = new Thread(){
                @Override
                public void run() {
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finally {
                        Intent intent = new Intent(SplashScreenActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            };
            timerThread.start();
        }
    }

    class SigninBackgroundWorker extends AsyncTask<String, Void, String> {

        AlertDialog alertDialog;

        @Override
        protected String doInBackground(String... params) {
            String login_url = "http://gatewaysgrp.com/mobapp/login_user.php";
            //String login_url = "http://192.168.0.11/login_user.php";
            try {
                String email = params[0];
                String password = params[1];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                String post_data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result.toString().trim();

            } catch(SocketTimeoutException e){
                return null;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (ProtocolException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(SplashScreenActivity.this).create();
            alertDialog.setTitle("Status");
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null) {
                if (result.equals("fail")) {
                    alertDialog.setMessage("Server is currently busy. Please try again later. ");
                    alertDialog.show();
                } else {
                    session.setLoggedIn(true, email, password);
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    intent.putExtra("json_data", result);
                    startActivity(intent);
                    finish();
                }
            }
            else{
                alertDialog.setMessage("Server is currently busy. Please try again later. ");
                alertDialog.show();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

