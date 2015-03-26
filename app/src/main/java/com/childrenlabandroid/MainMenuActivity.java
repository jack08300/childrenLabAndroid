package com.childrenlabandroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import data.User;


public class MainMenuActivity extends ActionBarActivity implements View.OnClickListener {

    TextView scheduleButton, kidsButton, deviceButton;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        scheduleButton = (TextView) findViewById(R.id.scheduleButton);
        kidsButton = (TextView) findViewById(R.id.kidsButton);
        deviceButton = (TextView) findViewById(R.id.deviceButton);

        scheduleButton.setOnClickListener(this);
        kidsButton.setOnClickListener(this);
        deviceButton.setOnClickListener(this);

        user = new User(this);
    }


    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.scheduleButton:
                Intent scheduleIntent = new Intent(MainMenuActivity.this, ScheduleActivity.class);
                startActivity(scheduleIntent);
                break;
            case R.id.kidsButton:
                Intent kidsIntent = new Intent(MainMenuActivity.this, KidsActivity.class);
                startActivity(kidsIntent);
                break;
            case R.id.deviceButton:
                Intent deviceIntent = new Intent(MainMenuActivity.this, DeviceActivity.class);
                startActivity(deviceIntent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
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

        switch(id){
            case R.id.logout:
                user.logout();
                break;

        }



        return super.onOptionsItemSelected(item);
    }
}
