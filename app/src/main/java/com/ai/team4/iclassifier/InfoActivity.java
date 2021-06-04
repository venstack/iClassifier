package com.ai.team4.iclassifier;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {

    private ListView confidenceList;
    ArrayList<Map<String,Object>> userClasses =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        confidenceList = (ListView)findViewById(R.id.confidence_list);
        getSupportActionBar().setTitle("More Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       String jResult =  getIntent().getStringExtra("jsonResult");
       try {
           JSONObject json = new JSONObject(jResult);
           userClasses.clear();
           userClasses.addAll((ArrayList) new Gson().fromJson(json.optString("classes"),new TypeToken<ArrayList<Map<String,Object>>>() {
           }.getType()));
           LayoutInflater inflater = getLayoutInflater();
           ViewGroup header = (ViewGroup)inflater.inflate(R.layout.listview_header,confidenceList,false);
          TextView tv = (TextView) header.findViewById(R.id.headText);
          tv.setText("Question: "+json.optString("text"));
           confidenceList.addHeaderView(header);

           SimpleAdapter adapter = new SimpleAdapter(this,userClasses,android.R.layout.simple_list_item_2,new String[] {"class_name","confidence"},new int[] {android.R.id.text1,android.R.id.text2});
           confidenceList.setAdapter(adapter);

       }
       catch (JSONException jse) {
           jse.printStackTrace();
       }


      //  Toast.makeText(getApplicationContext(),jResult,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
