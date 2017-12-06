package com.myapp.vaibhav.quizz;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;


public class ResultsActivity extends AppCompatActivity {

    private int score;
    private String category;
    private String message;
    private int percent;
    TextView messageText;
    TextView scoreText;
    TextView percentText;

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        tv = (TextView)findViewById(R.id.advtText);

        tv.setText(Html.fromHtml(
                "For " +
                        "technical computer courses,"+"<br>"+
                        "student visa and immigration,"+"<br>"+
                        "futher studies & PR in foreign countries"+"<br>"+
                        "visit "+
                        "<a href=\"http://www.gatewaysgrp.com\">gatewaysgrp.com</a> "));
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        score = getIntent().getExtras().getInt("score");
        category = getIntent().getExtras().getString("category");
        setTitle("Result: "+category  +" Quiz");

        messageText = (TextView)findViewById(R.id.messageText);
        percentText = (TextView)findViewById(R.id.percentText);
        scoreText = (TextView)findViewById(R.id.scoreText);

        percent = score * 5;

        if(score <= 3){
            percentText.setTextColor(Color.parseColor("#b6576d"));
            messageText.setText("Better luck next time.");
        }
        else if(score <= 6){
            percentText.setTextColor(Color.parseColor("#b6576d"));
            messageText.setText("You can do better.");
        }
        else if(score <= 9){
            percentText.setTextColor(Color.parseColor("#8bc34a"));
            messageText.setText("Try again and again until you succeed.");
        }
        else if(score <= 12){
            percentText.setTextColor(Color.parseColor("#8bc34a"));
            messageText.setText("Lets try again. You can do better.");
        }
        else if(score <= 15){
            percentText.setTextColor(Color.parseColor("#8bc34a"));
            messageText.setText("Good Job. You can do better.");
        }
        else if(score <= 18){
            percentText.setTextColor(Color.parseColor("#8bc34a"));
            messageText.setText("Good Job. You are good in this.");
        }
        else if(score < 20){
            percentText.setTextColor(Color.parseColor("#8bc34a"));
            messageText.setText("Very good! You will master it soon.");
        }
        else {
            percentText.setTextColor(Color.parseColor("#8bc34a"));
            messageText.setText("Perfect! :)");
        }

        scoreText.setText("You scored "+ score + " out of 20. That's a");
        percentText.setText(""+percent+"%");

    }


    public void shareResultMeth(View v){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String sharingTextBody = "I scored "+score + " out of 20 in a "+category + " quiz.\n\nDownload \"Quizz: Programming Quiz App\" from Google Play Store and test yourself.\n\nhttps://play.google.com/store/apps/details?id=com.myapp.vaibhav.quizz ";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Quizz: Programming Quiz App");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharingTextBody);
        startActivity(Intent.createChooser(sharingIntent,"Share result via"));
    }

    public void homeMeth(View v){
        onBackPressed();
    }

}
