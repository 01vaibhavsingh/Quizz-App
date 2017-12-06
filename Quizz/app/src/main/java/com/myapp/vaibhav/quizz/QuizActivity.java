package com.myapp.vaibhav.quizz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Date;
import java.text.SimpleDateFormat;

public class QuizActivity extends AppCompatActivity {

    private int TOTAL_COUNT = 0;
    private int CORRECT_COUNT = 0;
    private int c = 0;
    private String category;
    private String email;
    private String date_time;


    TextView questNoTextView;
    TextView questTextView;
    RadioGroup radioGroup;
    RadioButton option1;
    RadioButton option2;
    RadioButton option3;
    RadioButton option4;
    TextView ansTextView;
    TextView rightAnsTextView;

    Button button;
    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;


    String[] question = new String[20];
    String[] choice1 = new String[20];
    String[] choice2 = new String[20];
    String[] choice3 = new String[20];
    String[] choice4 = new String[20];
    String[] correct_choice = new String[20];

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        email = getIntent().getExtras().getString("email");
        category = getIntent().getExtras().getString("category");
        setTitle(category +" Quiz");

        progressDialog = new ProgressDialog(this);

        questNoTextView = (TextView) findViewById(R.id.qnoTextView);
        questTextView = (TextView) findViewById(R.id.questTextView);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        option1 = (RadioButton) findViewById(R.id.option1);
        option2 = (RadioButton) findViewById(R.id.option2);
        option3 = (RadioButton) findViewById(R.id.option3);
        option4 = (RadioButton) findViewById(R.id.option4);
        button = (Button) findViewById(R.id.button11);
        ansTextView = (TextView) findViewById(R.id.ansTextView);
        rightAnsTextView = (TextView) findViewById(R.id.rightAnsTextView);

        json_string = getIntent().getExtras().getString("json_data");
        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            int count = 0;
            while(count < jsonArray.length()){
                JSONObject jo = jsonArray.getJSONObject(count);
                question[count] = jo.getString("question");
                choice1[count] = jo.getString("option1");
                choice2[count] = jo.getString("option2");
                choice3[count] = jo.getString("option3");
                choice4[count] = jo.getString("option4");
                correct_choice[count] = jo.getString("correct_option");
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        questNoTextView.setText("" + (++TOTAL_COUNT) + "/20");
        questTextView.setText(question[c]);
        option1.setText(choice1[c]);
        option2.setText(choice2[c]);
        option3.setText(choice3[c]);
        option4.setText(choice4[c]);
        button.setText("CHECK");

    }

    public void nextQuestMeth(View v) {
        try {

            if (button.getText().toString().equals("CHECK")) {
                checkAnswer();
                return;
            }

            if (TOTAL_COUNT == 20) {
                endQuiz();
                return;
            }
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                radioGroup.getChildAt(i).setEnabled(true);
            }


            c++;
            radioGroup.clearCheck();
            ansTextView.setVisibility(View.GONE);
            rightAnsTextView.setVisibility(View.GONE);
            questNoTextView.setText("" + (++TOTAL_COUNT) + "/20");
            questTextView.setText(question[c]);
            option1.setText(choice1[c]);
            option2.setText(choice2[c]);
            option3.setText(choice3[c]);
            option4.setText(choice4[c]);
            button.setText("CHECK");

            if (TOTAL_COUNT == 20) {
                Button nextButton = (Button) findViewById(R.id.button11);
                nextButton.setText("CHECK");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Choose an option", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAnswer() {
        try {
            int id = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = (RadioButton) findViewById(id);
            if (radioButton.getText().toString().equals(correct_choice[c])) {
                CORRECT_COUNT++;
                ansTextView.setVisibility(View.VISIBLE);
                ansTextView.setTextColor(getResources().getColor(R.color.rightAnswer));
                ansTextView.setText("Correct!");
                if (TOTAL_COUNT == 20) {
                    button.setText("END");
                } else {
                    button.setText("NEXT");
                }
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    radioGroup.getChildAt(i).setEnabled(false);
                }
            } else {
                ansTextView.setVisibility(View.VISIBLE);
                rightAnsTextView.setVisibility(View.VISIBLE);
                ansTextView.setTextColor(getResources().getColor(R.color.wrongAnswer));
                ansTextView.setText("Wrong");
                rightAnsTextView.setText("Correct answer: "+ correct_choice[c]);
                if (TOTAL_COUNT == 20) {
                    button.setText("END");
                } else {
                    button.setText("NEXT");
                }
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    radioGroup.getChildAt(i).setEnabled(false);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Choose an option", Toast.LENGTH_SHORT).show();
        }
    }

    public void endQuizMeth(View v) {
        endQuiz();
    }

    private void endQuiz() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        date_time = dateFormat.format(date);

        if(isNetworkAvailable()) {
            progressDialog.setMessage("submitting score...");
            progressDialog.show();

            ScoreSubmitBackgroundWorker backgroundWorker = new ScoreSubmitBackgroundWorker();
            backgroundWorker.execute(email, date_time, category, CORRECT_COUNT + "");
        }
        else{
            Toast.makeText(this, "No internet. Press back to exit.", Toast.LENGTH_SHORT).show();
        }

    }

    class ScoreSubmitBackgroundWorker extends AsyncTask<String, Void, String> {

        AlertDialog alertDialog;
        @Override
        protected String doInBackground(String... params) {
            String register_url = "http://gatewaysgrp.com/mobapp/score_submit.php";

            try {
                String email = params[0];
                String date_time = params[1];
                String category = params[2];
                String score = params[3];
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                String post_data =
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&"
                                + URLEncoder.encode("date_time", "UTF-8") + "=" + URLEncoder.encode(date_time, "UTF-8") + "&"
                                + URLEncoder.encode("category", "UTF-8") + "=" + URLEncoder.encode(category, "UTF-8") + "&"
                                + URLEncoder.encode("score", "UTF-8") + "=" + URLEncoder.encode(score, "UTF-8");
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
            alertDialog = new AlertDialog.Builder(QuizActivity.this).create();
            alertDialog.setTitle("Status");
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.cancel();
            if(result != null) {
                if (result.equals("success")) {
                    Intent intent = new Intent(QuizActivity.this, ResultsActivity.class);
                    intent.putExtra("score", CORRECT_COUNT);
                    intent.putExtra("category", category);
                    startActivity(intent);
                    finish();
                } else {
                    alertDialog.setMessage("Server is currently busy. Please try again later. ");
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



    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
        builder.setTitle(category + " Quiz");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Do you want to quit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void shareMeth(View v){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String sharingTextBody = "Can you answer this \"" + category +"\" question?\n\n" + question[c] +"\n\n1. "+choice1[c] +"\n2. "+choice2[c] +"\n3. "+ choice3[c] +"\n4. "+choice4[c] +"\n"+
                "\nDownload \"Quizz: Programming Quiz App\" from Google Play Store and test yourself.\n\nhttps://play.google.com/store/apps/details?id=com.myapp.vaibhav.quizz";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Quizz: Programming Quiz App");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharingTextBody);
        startActivity(Intent.createChooser(sharingIntent,"Share this question via"));
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
