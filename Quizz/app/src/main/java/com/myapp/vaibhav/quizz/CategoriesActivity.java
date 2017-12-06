package com.myapp.vaibhav.quizz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

public class CategoriesActivity extends AppCompatActivity {

    String email;
    String category;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        email = getIntent().getExtras().getString("email");
        setTitle("Choose category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);

    }

    public void cQuizMeth(View v){
        category = "C";
        if(isNetworkAvailable()) {
            progressDialog.setMessage("loading...");
            progressDialog.show();

            FetchQuizBackgroundWorker fetchQuizBackgroundWorker = new FetchQuizBackgroundWorker();
            fetchQuizBackgroundWorker.execute(category);
        }
        else{
            Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void cppQuizMeth(View v){
        category = "C++";
        if(isNetworkAvailable()) {
            progressDialog.setMessage("loading...");
            progressDialog.show();

            FetchQuizBackgroundWorker fetchQuizBackgroundWorker = new FetchQuizBackgroundWorker();
            fetchQuizBackgroundWorker.execute(category);
        }
        else{
            Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void javaQuizMeth(View v){
        category = "Java";
        if(isNetworkAvailable()) {
            progressDialog.setMessage("loading...");
            progressDialog.show();

            FetchQuizBackgroundWorker fetchQuizBackgroundWorker = new FetchQuizBackgroundWorker();
            fetchQuizBackgroundWorker.execute(category);
        }
        else{
            Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void pythonQuizMeth(View v){
        category = "Python";
        if(isNetworkAvailable()) {
            progressDialog.setMessage("loading...");
            progressDialog.show();

            FetchQuizBackgroundWorker fetchQuizBackgroundWorker = new FetchQuizBackgroundWorker();
            fetchQuizBackgroundWorker.execute(category);
        }
        else{
            Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void htmlQuizMeth(View v){
        category = "Html";
        if(isNetworkAvailable()) {
            progressDialog.setMessage("loading...");
            progressDialog.show();

            FetchQuizBackgroundWorker fetchQuizBackgroundWorker = new FetchQuizBackgroundWorker();
            fetchQuizBackgroundWorker.execute(category);
        }
        else{
            Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void androidQuizMeth(View v){
        category = "Android";
        if(isNetworkAvailable()) {
            progressDialog.setMessage("loading...");
            progressDialog.show();

            FetchQuizBackgroundWorker fetchQuizBackgroundWorker = new FetchQuizBackgroundWorker();
            fetchQuizBackgroundWorker.execute(category);
        }
        else{
            Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    class FetchQuizBackgroundWorker extends AsyncTask<String, Void, String> {
        AlertDialog alertDialog;

        @Override
        protected String doInBackground(String... params) {
            String login_url = "http://gatewaysgrp.com/mobapp/fetch_quiz.php";

            try {
                String category = params[0];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                String post_data = URLEncoder.encode("category", "UTF-8") + "=" + URLEncoder.encode(category, "UTF-8");
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
            alertDialog = new AlertDialog.Builder(CategoriesActivity.this).create();
            alertDialog.setTitle("Status");
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.cancel();
            if(result != null) {
                if (result.equals("fail")) {
                    alertDialog.setMessage("Server is currently busy. Please try again later. ");
                    alertDialog.show();
                } else {

                    Intent intent = new Intent(CategoriesActivity.this, QuizActivity.class);
                    intent.putExtra("json_data", result);
                    intent.putExtra("email", email);
                    intent.putExtra("category", category);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}