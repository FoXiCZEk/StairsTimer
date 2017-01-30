package com.foxiczek.stairstimer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Hashtable;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {
    public Hashtable results;
    Button button_Start, button_Stop, button_Patro, button_Reset, button_Result;
    TextView textView_cas, textView_patro;
    private int patro = 1;
    long starttime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedtime = 0L;
    int running,reset = 0;
    int secs = 0;
    int mins = 0;
    int milliseconds = 0;
    Handler handler = new Handler();
    String zero = "0:00:000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        results = new Hashtable();
        textView_cas = (TextView) findViewById(R.id.textView_cas);
        textView_patro = (TextView) findViewById(R.id.textView_patro);
        textView_patro.setText("Patro : " + ( patro - 1 ));
        textView_cas.setText("0:00:000");
        button_Start = (Button) findViewById(R.id.button_start);
        button_Reset = (Button) findViewById(R.id.button_reset);
        button_Result = (Button) findViewById(R.id.button_result);

        button_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running == 0) {
                    button_Stop.setVisibility(View.VISIBLE);
                    button_Patro.setVisibility(View.VISIBLE);
                    button_Start.setVisibility(View.INVISIBLE);
                    button_Reset.setVisibility(View.INVISIBLE);
                    button_Start.setEnabled(FALSE);
                    button_Patro.setEnabled(TRUE);
                    textView_patro.setText("Patro : " + ( patro - 1 ));
                    running = 1;
                    starttime = SystemClock.uptimeMillis();
                    handler.postDelayed(updateTimer, 0);

                }

                if (patro == 1) {
                    System.err.println("writing first entry");
                    writeTime(0, "0:00:000");
                }
            }
        });

        button_Stop = (Button) findViewById(R.id.button_stop);
        button_Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_Start.setVisibility(View.VISIBLE);
                button_Patro.setVisibility(View.INVISIBLE);
                button_Stop.setVisibility(View.INVISIBLE);
                button_Start.setEnabled(TRUE);
                button_Patro.setEnabled(FALSE);
                timeSwapBuff += timeInMilliseconds;
                handler.removeCallbacks(updateTimer);
                System.err.println("DEBUG : " + patro);
                writeTime(patro, textView_cas.getText().toString());

                running = 0;
                if( zero != textView_cas.getText().toString()){
                    button_Result.setVisibility(View.VISIBLE);
                    button_Result.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] output;
                            output = getResults();
                            if(output.length > 0) {
                                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                                intent.putExtra("results", output);
                                startActivity(intent);
                            }
                        }
                    });

                    button_Reset.setVisibility(View.VISIBLE);
                    button_Reset.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            patro = 1;
                            textView_patro.setText("Patro : " + ( patro - 1 ));
                            textView_cas.setText(zero);
                            timeInMilliseconds = 0L;
                            reset = 1;
                            results.clear();
                            button_Result.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

        button_Patro = (Button) findViewById(R.id.button_patro);
        button_Patro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (running == 1) {
                    writeTime(patro, textView_cas.getText().toString());
                    patro++;
                }
            }
        });
    }

    private void writeTime(int patro, String time) {
        results.put(patro, time);
    }

    public String[] getResults() {

        String output = "";
        if (results.size() > 0) {
            String[] test = new String[results.size()];
            for (int a = 0; a < results.size(); a++) {
                test[a] = String.valueOf(results.get(a));
                output = output + a + "\t" + results.get(a).toString() + "\n";

            }
            return test;
        } else {
            return null;
        }
    }

    public Runnable updateTimer = new Runnable() {
        public void run() {
            if(reset == 1) {
                starttime = SystemClock.uptimeMillis();
                timeSwapBuff = 0L;
                reset = 0;
            }
            timeInMilliseconds = SystemClock.uptimeMillis() - starttime;
            updatedtime = timeSwapBuff + timeInMilliseconds;
            secs = (int) (updatedtime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            milliseconds = (int) (updatedtime % 1000);
            textView_patro.setText("Patro : " + String.valueOf(patro - 1));
            textView_cas.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            textView_cas.setTextColor(Color.RED);
            handler.postDelayed(this, 0);

        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                    return true;

            case R.id.action_vysledky:
                Intent intent = new Intent(MainActivity.this, VysledkyActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_about:
                    Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent2);
                    return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }












}
