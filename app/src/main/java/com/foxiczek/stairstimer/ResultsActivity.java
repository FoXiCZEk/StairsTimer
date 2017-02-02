package com.foxiczek.stairstimer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class ResultsActivity extends AppCompatActivity {
    TextView textView_result;
    Intent input;
    String[] output;
    String webOutputFields = "";
    String webOutputValues = "";
    Integer[] casNaPatro;
    Button button_save, button_saveLocal;
    File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "Stairs2");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    FileOutputStream fis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String vystup = " ";
        double temp, temp1, temp2;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        output = getIntent().getStringArrayExtra("results");

        textView_result = (TextView) findViewById(R.id.textView_result);
        final String tempOutput = countResults(output).replaceAll("-", " ");
        textView_result.setText(tempOutput.replaceAll("=", " "));
        button_save = (Button) findViewById(R.id.button_save);

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.err.println(tempOutput);
                String webInput = tempOutput.replaceAll(" : ", "");
                webInput = webInput.toLowerCase();
                webInput = webInput.replaceAll(" ", "");
                webInput = webInput.replaceAll("cas", "");
                System.err.println(webInput);
                String[] lines = webInput.split(";");
                System.err.println("LENGTH : " + lines.length);
                for (int b = 0; b < lines.length; b++) {
                    String temporary = lines[b];
                    String[] data = temporary.split("=");
                    System.err.println("data0 " + data[0] + " data1 " + data[1]);
                    webOutputFields = webOutputFields + data[0] + ",";
                    webOutputValues = webOutputValues + "'" + data[1] + "',";
                }
                webOutputFields = webOutputFields.substring(0, webOutputFields.length() - 1);
                webOutputFields = webOutputFields.replaceAll("\\n", "");
                webOutputValues = webOutputValues.substring(0, webOutputValues.length() - 1);
                System.err.println("webOutputFields : " + webOutputFields + ";webOutputValues : " + webOutputValues);
                writeData(webOutputFields, webOutputValues);
            }
        });


        button_saveLocal = (Button) findViewById(R.id.button_save_local);
        button_saveLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeLocalData(tempOutput.replaceAll("=", " "));
            }
        });
    }


    public String countResults(String[] output) {
        this.output = output;
        String[] vystup;
        String out = " ";
        int min1 = 0, sec1 = 0, msec1 = 0, min2 = 0, sec2 = 0, msec2 = 0;
        int temp = 0, temp1 = 0, temp2 = 0;
        if (Integer.valueOf(output.length) < 1) {
            out = "NO RESULTS YET";
        } else {
            for (int i = 0; i < output.length; i++) {
                if (i < (output.length - 1)) {
                    String[] vystup1 = output[i].split(":");
                    String[] vystup2 = output[i + 1].split(":");
                    min1 = Integer.valueOf(vystup1[0]);
                    sec1 = Integer.valueOf(vystup1[1]);
                    msec1 = Integer.valueOf(vystup1[2]);
                    min2 = Integer.valueOf(vystup2[0]);
                    sec2 = Integer.valueOf(vystup2[1]);
                    msec2 = Integer.valueOf(vystup2[2]);
                    temp1 = ((min1 * 60) * 1000) + (sec1 * 1000) + msec1;
                    temp2 = ((min2 * 60) * 1000) + (sec2 * 1000) + msec2;
                    temp = temp2 - temp1;
                    sec1 = temp / 1000;
                    min1 = sec1 / 60;
                    sec1 = sec1 % 60;
                    msec1 = temp % 1000;
                    String cas = String.format("%02d", min1) + ":" + String.format("%02d", sec1) + ":" + String.format("%03d", msec1);
                    out = out + "\nPatro-:-" + (i + 1) + "=-Cas-:-" + cas + ";";
                }
            }
            out = out + "\nTOTAL-=-" + output[output.length - 1];
        }

        return out;

    }


    public void writeData(String webOutputFields, String webOutputValues) {
        String fields = "";
        String values = "";
        System.err.println(fields);
        System.err.println(values);
        int HTTP_Status = 0;
        AsyncHttpClient client = new AsyncHttpClient();
        String URL = "http://mysql.foxiczek.cz/INSERT_TEXT.php?fields=" + webOutputFields + "&values=" + webOutputValues ;
        client.get(URL, new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("HTTP STATUS CODE : " + String.valueOf(statusCode));
                String output = null;
                try {
                    output = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (output.contains("OK")) {
                    Toast.makeText(ResultsActivity.this, String.valueOf(" DATA SENT TO DB "), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResultsActivity.this, String.valueOf(" ERROR : problem with data insert into DB "), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.err.println("ERROR : UNABLE TO CONNECT TO WEBSERVER");
                Toast.makeText(ResultsActivity.this, String.valueOf(" ERROR : UNABLE TO CONNECT TO WEBSERVER "), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        });
    }

    public void writeLocalData(String input){
        if(checkExtStorage()){
            String currentDateandTime = sdf.format(new Date());
            File filename = new File(folder, currentDateandTime + ".txt");
            try {
                if(!filename.exists()) {
                    filename.createNewFile();
                }
                fis = new FileOutputStream(filename);
                OutputStreamWriter output = new OutputStreamWriter(fis);
                output.write(input);
                output.close();

                fis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if(filename.exists()){
                Toast.makeText(ResultsActivity.this, String.valueOf("DATA STORED LOCALLY"), Toast.LENGTH_SHORT).show();
            }

        }


    }

    private boolean checkExtStorage(){
        if(folder.exists()){
            System.err.println(folder.toString() + " IS OK");
            return true;
        }else{
            System.err.println(folder.toString() + " ERROR");
            folder.mkdir();
            if(folder.exists()){
                return true;
            }else{
                System.err.println(folder.toString() + " ERROR");
                return false;
            }

        }

    }


}
