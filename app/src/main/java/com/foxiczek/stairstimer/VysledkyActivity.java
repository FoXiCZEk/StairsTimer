package com.foxiczek.stairstimer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.jar.Manifest;

public class VysledkyActivity extends AppCompatActivity {
    FileWorks fw = new FileWorks();
    File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "Stairs2");
    //File filename = new File(Environment.getDataDirectory());
    DateFormat datum;


    public boolean isStorageAccessible(){
        if(folder.exists()){
            System.err.println(folder.toString() + " EXISTS");
            return true;
        }
        else{
            System.err.println(folder.toString() + " FAILED");
            folder.mkdirs();
            System.err.println(folder.toString() + " RECHECK");
            if(folder.exists()){
                System.err.println(folder.toString() + " EXISTS");
                return true;
            }else{
                System.err.println(folder.toString() + " FAILED");
                return false;
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vysledky);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);




        Button button_getData = (Button) findViewById(R.id.button_getData);
        button_getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStorageAccessible()){
                    TextView textView_localData = (TextView) findViewById(R.id.textView_localData);
                    TableLayout table = (TableLayout) findViewById(R.id.table_data);
                    System.err.println("STORAGE OK");
                    String[] input = listFolder(folder);
                    System.err.println(input.length);
                    generateTable(input);
                    table.setVisibility(View.VISIBLE);
                    textView_localData.setVisibility(View.INVISIBLE);
                }
            }
        });

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(VysledkyActivity.this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_vysledky:
                return true;


            case R.id.action_about:
                Intent intent2 = new Intent(VysledkyActivity.this, AboutActivity.class);
                startActivity(intent2);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void generateTable(String[] content){
        final TableLayout table = (TableLayout) findViewById(R.id.table_data);
        if(table.getChildCount() > 0){
            table.removeAllViews();

        }



        for(int a = 0; a < content.length;a++){
            TableRow tr = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT);
            tr.setLayoutParams(lp);
            TextView textView = new TextView(this);
            textView.setText(content[a].toString());

            final String selected = content[a].toString();
            Button button_select = new Button(this);
            button_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TextView textView_localData = (TextView) findViewById(R.id.textView_localData);
                    //table.setVisibility(View.INVISIBLE);
                    //textView_localData.setText(readFile(selected));
                    //textView_localData.setVisibility(View.VISIBLE);
                    generateDataTable(readFile(selected));
                }
            });
            button_select.setText("SELECT");
            textView.setPadding(0,0,200,0);
            tr.addView(textView);
            button_select.setPadding(0,0,0,0);
            tr.addView(button_select);
            //tr.setGravity(5);
            table.addView(tr);
        }

    }

    private String readFile(String filename){
        FileInputStream fis = null;
        String output = null;
        try {
            String file = folder + File.separator + filename;
            System.err.println("OUTPUT : " + file);
            fis = new FileInputStream(new File(file));
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            fis.close();
            isr.close();
            output = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("OUTPUT : " + output);
        return output;
    }
    private String[] listFolder(File folder){
        String[] fileList = null;
        System.err.println(folder.toString());
        System.err.println(folder.list().length);
        fileList = folder.list();
        System.err.println(fileList.length);
        for(int a =0; a <fileList.length ;a++){
            System.err.println(fileList[a]);

        }
        return fileList;
    }


    private void generateDataTable(String input){
        TableLayout table = (TableLayout) findViewById(R.id.table_data);
        if(table.getChildCount() > 0){
            table.removeAllViews();
        }
        String[] data = null;

        data = input.split(";");
        for(int i = 0; i < data.length; i++){
            TableRow tr = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT);
            tr.setLayoutParams(lp);
            TextView textView = new TextView(this);
            textView.setText(data[i].toString());
            tr.addView(textView);

            table.addView(tr);
        }

    }

}
