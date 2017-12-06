package com.myapp.vaibhav.quizz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScoresActivity extends AppCompatActivity {

    final ArrayList<String> list = new ArrayList<>();
    ArrayAdapter aa;

    ListView l;

    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        setTitle("Scores");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        l = (ListView) findViewById(R.id.listView);

        aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        l.setAdapter(aa);

        json_string = getIntent().getExtras().getString("json_data");
        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("server_response");
            int count = 0;
            while(count < jsonArray.length()){
                JSONObject jo = jsonArray.getJSONObject(count);
                list.add(count,  jo.getString("category")+ "  " + jo.getString("date_time") + " GMT  " + jo.getString("score") + "/20");
                aa.notifyDataSetChanged();
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (list.isEmpty()) {
            Toast.makeText(this, "No past scores found.", Toast.LENGTH_SHORT).show();
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

}
