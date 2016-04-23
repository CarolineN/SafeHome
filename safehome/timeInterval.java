package com.example.caroline.safehome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

public class timeInterval extends AppCompatActivity {
    AlertDialog levelDialog;
    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_interval);
        getSupportActionBar().setTitle("Pick a Time Interval");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#184e58")));
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);
        final CharSequence[] items = {" 1 minute"," 5 minutes"," 10 minutes "};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select the update frequency");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                long interval=0;
                long otherInterval=0;

                switch (item) {
                    case 0:
                        interval = 1000 * 60 * 2;
                        otherInterval = 1000 * 60 * 1;
                        Intent appInfo = new Intent(timeInterval.this, Journey.class);
                        appInfo.putExtra("in", interval);
                        appInfo.putExtra("in1", otherInterval);
                        startActivity(appInfo);


                        break;
                    case 1:
                        interval= 1000 * 60 * 6;
                        otherInterval = 1000 * 60 * 5;
                        Intent appInfo1 = new Intent(timeInterval.this, Journey.class);
                        appInfo1.putExtra("in", interval);
                        appInfo1.putExtra("in1", otherInterval);
                        startActivity(appInfo1);



                        break;
                    case 2:
                        interval = 1000 * 60 * 10;
                        otherInterval = 1000 * 60 * 10;
                        Intent appInfo2 = new Intent(timeInterval.this, Journey.class);
                        appInfo2.putExtra("in", interval);
                        appInfo2.putExtra("in1", otherInterval);
                        startActivity(appInfo2);


                        break;


                }
                levelDialog.dismiss();
            }
        });
        levelDialog = builder.create();
        levelDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_interval, menu);
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
