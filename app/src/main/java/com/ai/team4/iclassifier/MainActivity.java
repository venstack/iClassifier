package com.ai.team4.iclassifier;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.icu.text.UnicodeSetSpanner;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.assistant.v1.Assistant;
import com.ibm.watson.developer_cloud.assistant.v1.model.WorkspaceCollection;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.NaturalLanguageClassifier;
import com.ibm.watson.developer_cloud.natural_language_classifier.v1.model.*;

import com.ibm.watson.developer_cloud.service.exception.RequestTooLargeException;
import com.ibm.watson.developer_cloud.service.exception.ServiceResponseException;
import com.ipaulpro.afilechooser.utils.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

import okhttp3.OkHttpClient;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int REQUEST_CHOOSER = 6384;
    private Button classifyButton;
    private EditText questionText;
    private TextView statusText;
    private String jsonResult = "";
    private Button infoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        classifyButton = (Button)findViewById(R.id.btnClassify);
        questionText = (EditText) findViewById(R.id.txtQuestion);
        statusText = (TextView)findViewById(R.id.txtStatus);
        infoButton = (Button)findViewById(R.id.infoButton);
        if (!checkPermission()) {
            requestPermission();
        }
        classifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = questionText.getText().toString();
                HttpStuff httpStuff = new HttpStuff(MainActivity.this);
                if (question .equals("")  || question.length() <=0) {
                    jsonResult = "";
                    statusText.setTextColor(Color.RED);
                    statusText.setText("Please enter a question");
                }
                else {
                    AsyncTask doasync = httpStuff.execute(question);
                }
            }
        });
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jsonResult == "" || jsonResult.length() <= 0) {
                    Toast.makeText(getApplicationContext(),"No Data to Show", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent nextIntent = new Intent(MainActivity.this,InfoActivity.class);
                    nextIntent.putExtra("jsonResult",jsonResult);
                    startActivity(nextIntent);
                }
            }
        });
    }

    // Requesting Permissions

    private boolean checkPermission() {
        int readStorageResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int writeStorageResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int cameraResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return readStorageResult == PackageManager.PERMISSION_GRANTED && writeStorageResult == PackageManager.PERMISSION_GRANTED  && cameraResult == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE, CAMERA}, PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (readStorageAccepted && writeStorageAccepted && cameraAccepted) {
                        System.out.println("Permission Granted");
                    }else {
                        System.out.println("Permission Not Granted");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE, CAMERA},PERMISSION_REQUEST_CODE);
                        }
                    }

                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void updateResult(final String result)
    {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                jsonResult = result;
                String res = "";
                String topClass = "";
        try {
            JSONObject answer = new JSONObject(result);
            topClass = answer.get("top_class").toString();
            res = "Your question relates to: "+topClass;
        }
        catch (JSONException ex)
        {
            //probably not a JSON
            res = "Sorry "+result;
        }
                statusText.setTextColor(Color.BLUE);
                statusText.setText(res);
            }
        });

    }

    public void updateRequest(final String result)
    {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusText.setTextColor(Color.BLACK);
                statusText.setText(result);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent aboutIntent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(aboutIntent);

        }

        return super.onOptionsItemSelected(item);
    }

}
