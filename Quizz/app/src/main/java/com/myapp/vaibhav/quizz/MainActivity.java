package com.myapp.vaibhav.quizz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import java.net.URLConnection;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView nameText;
    TextView emailText;
    ImageView iv;

    String json_string;
    String name;
    String email;
    String photo_path;

    JSONObject jsonObject;
    JSONArray jsonArray;

    private Session session;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameText = (TextView)findViewById(R.id.textView16);
        emailText = (TextView)findViewById(R.id.textView24);
        iv = (ImageView)findViewById(R.id.imageView2);

        progressDialog = new ProgressDialog(this);

        session = new Session(getApplicationContext());
        if(!session.loggedIn()){
            signOut();
        }


        if(getIntent().getExtras() != null) {
            json_string = getIntent().getExtras().getString("json_data");
            try {
                jsonObject = new JSONObject(json_string);
                jsonArray = jsonObject.getJSONArray("server_response");
                int count = 0;
                while (count < jsonArray.length()) {
                    JSONObject jo = jsonArray.getJSONObject(count);
                    name = jo.getString("name");
                    email = jo.getString("email");
                    photo_path = jo.getString("photo_path");
                    count++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        nameText.setText(name);
        emailText.setText(email);


        if(isNetworkAvailable()) {
            DownloadImageBackgroundTask downloadImageBackgroundTask = new DownloadImageBackgroundTask();
            downloadImageBackgroundTask.execute();
        }

    }


    class DownloadImageBackgroundTask extends AsyncTask<Void, Void, Bitmap>{
        String url = "http://gatewaysgrp.com/mobapp/" + photo_path;
        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);

                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null){
                iv.setImageBitmap(getCircleBitmap(bitmap));
            }
        }
    }

    public void playQuizMeth(View v){
        Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
        intent.putExtra("email",email);
        startActivity(intent);
    }

    public void viewScoresMeth(View v){
        progressDialog.setMessage("getting scores...");
        progressDialog.show();
        if(isNetworkAvailable()) {
            FetchScoresBackgroundWorker fetchScoresBackgroundWorker = new FetchScoresBackgroundWorker();
            fetchScoresBackgroundWorker.execute(email);
        }
        else{
            Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    class FetchScoresBackgroundWorker extends AsyncTask<String, Void, String> {

        AlertDialog alertDialog;

        @Override
        protected String doInBackground(String... params) {
            String login_url = "http://gatewaysgrp.com/mobapp/fetch_scores.php";

            try {
                String email = params[0];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                String post_data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
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
            alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Status");
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.cancel();
            if(result != null) {
                if (result.equals("fail")) {
                    alertDialog.setMessage("No past scores found ");
                    alertDialog.show();
                } else {
                    Intent intent = new Intent(MainActivity.this, ScoresActivity.class);
                    intent.putExtra("json_data", result);
                    startActivity(intent);
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



    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_about: {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            }

            case R.id.menu_share: {
                shareApp();
                break;
            }

            case R.id.menu_help_feedback: {
                helpFeedback();
                break;
            }

            case R.id.menu_rate_us: {
                rateUs();
                break;
            }

            case R.id.menu_logout: {
                signOut();
                break;
            }

            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void rateUs(){
        Toast.makeText(this, "If you liked our app. Please rate us at Google Play Store", Toast.LENGTH_SHORT).show();
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.myapp.vaibhav.quizz");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void shareApp(){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String sharingTextBody = "Download \"Quizz: Programming Quiz App\" from Google Play Store and test yourself.\n\nhttps://play.google.com/store/apps/details?id=com.myapp.vaibhav.quizz ";
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Quizz: Programming Quiz App");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharingTextBody);
        startActivity(Intent.createChooser(sharingIntent,"Share App via"));
    }

    private void helpFeedback(){
        String to = "01vaibhavsingh@gmail.com";
        String subject = "Quizz: Programming Quiz App - Feedback";
        String message = "Feedback: \n";

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent,"Provide Feedback:"));
    }


    private void signOut(){
        session.setLoggedIn(false,"","");
        finish();
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
