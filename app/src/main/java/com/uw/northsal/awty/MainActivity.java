package com.uw.northsal.awty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    PendingIntent alarmIntent = null;
    AlarmManager am;
    boolean firing = false;

    BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override public void onReceive(final Context c, Intent i) {
            String message = i.getStringExtra("Message");
            String number = i.getStringExtra("Number");
            Log.i("MyApp", number);

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null, message, null, null);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "SMS failed, please try again later", Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = (Button) findViewById(R.id.btnStart);

        startButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Grab all the text/int fields
                String message = ((EditText) findViewById(R.id.userMessage)).getText().toString().trim();
                String phone = ((EditText) findViewById(R.id.userPhone)).getText().toString().trim();
                String interval = ((EditText) findViewById(R.id.userTime)).getText().toString().trim();
                int time = 0;
                //Cast to integer if possible
                if(!interval.equals("")) {
                    time = Integer.valueOf(interval);
                }

                //If valid format, proceed. Otherwise show error message.
                if (!message.equals("") && !phone.equals("") && !interval.equals("") && time > 0) {
                    //If alarms are off, turn on and switch button
                    if (!firing) {
                        ((Button) view).setText("Cancel!");
                        firing = true;
                        registerReceiver(alarmReceiver, new IntentFilter("setupAlarm"));

                        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent i = new Intent();
                        i.setAction("setupAlarm");
                        i.putExtra("Message", message);
                        i.putExtra("Number", phone);
                        alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, i, 0);

                        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + (time * 60000), time * 60000, alarmIntent);
                    } else { //If alarms are on, turn off and switch button
                        ((Button) view).setText("Start!");
                        firing = false;
                        am.cancel(alarmIntent);
                        alarmIntent.cancel();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please make sure the form is filled correctly!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
