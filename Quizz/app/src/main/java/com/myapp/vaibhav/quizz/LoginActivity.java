package com.myapp.vaibhav.quizz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {
    EditText emailText;
    EditText pwdText;

    String email;
    String password;

    private Session session;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new Session(getApplicationContext());

        progressDialog = new ProgressDialog(this);
        emailText = (EditText) findViewById(R.id.emailText);
        pwdText = (EditText) findViewById(R.id.pwdText);

    }

    public void signInMeth(View v) {

        email = emailText.getText().toString();
        password = pwdText.getText().toString();

        if (emailText.getText().length() == 0) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
        } else if (pwdText.getText().length() == 0) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        } else {
            if(isNetworkAvailable()) {
                progressDialog.setMessage("Signing in...");
                progressDialog.show();

                SigninBackgroundWorker signinBackgroundWorker = new SigninBackgroundWorker();
                signinBackgroundWorker.execute(email, password);
            }
            else{
                Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }


    class SigninBackgroundWorker extends AsyncTask<String, Void, String> {

        AlertDialog alertDialog;
        @Override
        protected String doInBackground(String... params) {
            String login_url = "http://gatewaysgrp.com/mobapp/login_user.php";

            try {
                String email = params[0];
                String password = params[1];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
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

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
            alertDialog.setTitle("Status");
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.cancel();
            if(result != null) {
                if (result.equals("fail")) {
                    alertDialog.setMessage("Account does not exist.");
                    alertDialog.show();
                } else {
                    session.setLoggedIn(true, email, password);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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


    public void signUpMeth(View v) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
