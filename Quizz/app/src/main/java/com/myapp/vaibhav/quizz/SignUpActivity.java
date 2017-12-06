package com.myapp.vaibhav.quizz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class SignUpActivity extends AppCompatActivity {

    EditText nameText;
    EditText emailText;
    EditText passText;
    EditText passText2;
    EditText dobText;
    EditText mobileText;
    EditText secAnsText;

    Spinner spinner;
    ImageView imageView;
    Bitmap photo;

    private ProgressDialog progressDialog;


    private static final int CAMERA_REQUEST = 1888;
    String[] questions = {"What was the name of your first school?",
            "What is your pet’s name?",
            "What is your favorite food?",
            "What is your favorite place?",
            "What time of the day were you born?",
            "In what year was your father born?"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameText = (EditText) findViewById(R.id.editText);
        emailText = (EditText) findViewById(R.id.editText2);
        passText = (EditText) findViewById(R.id.editText3);
        passText2 = (EditText) findViewById(R.id.editText4);
        dobText = (EditText) findViewById(R.id.editText5);
        mobileText = (EditText) findViewById(R.id.editText6);
        secAnsText = (EditText) findViewById(R.id.editText7);

        imageView = (ImageView) findViewById(R.id.imageView);

        photo = BitmapFactory.decodeResource(this.getResources(), R.drawable.default_pic);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, questions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        progressDialog = new ProgressDialog(this);

    }

    public void createUserMeth(View v) {

        String name, email, pass, pass2, dob, phone_no, encoded_photo, secQues, secAns;
        name = nameText.getText().toString();
        email = emailText.getText().toString();
        pass = passText.getText().toString();
        pass2 = passText2.getText().toString();
        dob = dobText.getText().toString();
        phone_no = mobileText.getText().toString();
        encoded_photo = Base64.encodeToString(Utility.getBytes(photo),0);
        secQues = spinner.getSelectedItem().toString();
        secAns = secAnsText.getText().toString();

        if (name.equals("") || email.equals("") || pass.equals("") || pass2.equals("") || dob.equals("") || phone_no.equals("") || secAns.equals("")) {
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isValidEmail(email)){
            Toast.makeText(this, "Invalid Email id", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(pass2)) {
            Toast.makeText(this, "Your password doesn't match.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(isNetworkAvailable()) {
            progressDialog.setMessage("Creating account...");
            progressDialog.show();

            SignupBackgroundWorker backgroundWorker = new SignupBackgroundWorker();
            backgroundWorker.execute(name, email, pass, dob, phone_no, encoded_photo, secQues, secAns);
        }
        else{
            Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


    public void captureImage(View v) {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }

    class SignupBackgroundWorker extends AsyncTask<String, Void, String> {

        AlertDialog alertDialog;

        @Override
        protected String doInBackground(String... params) {
            String register_url = "http://gatewaysgrp.com/mobapp/signup_user.php";

            try {
                String name = params[0];
                String email = params[1];
                String pass = params[2];
                String dob = params[3];
                String phone_no = params[4];
                String encoded_photo = params[5];
                String secQues = params[6];
                String secAns = params[7];
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                String post_data =
                        URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&"
                                + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                                + URLEncoder.encode("pass", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8") + "&"
                                + URLEncoder.encode("dob", "UTF-8") + "=" + URLEncoder.encode(dob, "UTF-8") + "&"
                                + URLEncoder.encode("phone_no", "UTF-8") + "=" + URLEncoder.encode(phone_no, "UTF-8") + "&"
                                + URLEncoder.encode("encoded_photo", "UTF-8") + "=" + URLEncoder.encode(encoded_photo, "UTF-8") + "&"
                                + URLEncoder.encode("secQues", "UTF-8") + "=" + URLEncoder.encode(secQues, "UTF-8") + "&"
                                + URLEncoder.encode("secAns", "UTF-8") + "=" + URLEncoder.encode(secAns, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String result = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return result;

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
            alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
            alertDialog.setTitle("Status");
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.cancel();
            if(result != null) {
                if (result.equals("success")) {
                    alertDialog.setMessage("Account registered successfully.");
                    alertDialog.show();

                    nameText.setText("");
                    emailText.setText("");
                    passText.setText("");
                    passText2.setText("");
                    dobText.setText("");
                    mobileText.setText("");
                    secAnsText.setText("");
                    imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_pic));
                } else {
                    alertDialog.setMessage("Account cannot be created. Possible reason- Your email id is already registered. If you're having issues signing in or signing up, email at 01vaibhavsingh@gmail.com");
                    alertDialog.show();
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

    public void backToLoginMeth(View v) {
        onBackPressed();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

}

