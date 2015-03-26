package com.childrenlabandroid;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import util.Server;


public class MyScheduleActivity extends ActionBarActivity {

    LinearLayout myScheduleListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schedule);

        myScheduleListLayout = (LinearLayout) findViewById(R.id.myScheduleListLayout);

        loadMySchedule();
    }

    public void loadMySchedule(){
        try{

            Server server = new Server(this);
            JSONObject resultJSON = server.execute("schedule/retrieveUserSchedule").get();
            if(resultJSON.getBoolean("success")){
                displayMySchedule(resultJSON.getJSONArray("schedule"));
            }else{
                Toast.makeText(getApplicationContext(), "Something wrong when load Schedule", Toast.LENGTH_LONG).show();
                finish();
            }

        }catch(Exception e){
            Log.e("My Schedule", "load My Schedule", e);
        }
    }

    public void displayMySchedule(JSONArray list){
        try{
            myScheduleListLayout.removeAllViews();

            for(int i=0; i<list.length() ;i++){
                View view = LayoutInflater.from(this).inflate(R.layout.schedule_item, null);

                TextView fromDateText = (TextView) view.findViewById(R.id.scheduleSearchResultFromDate);
                fromDateText.setText(list.getJSONObject(i).getString("startDate"));
                TextView toDateText = (TextView) view.findViewById(R.id.scheduleSearchResultToDate);
                toDateText.setText(list.getJSONObject(i).getString("endDate"));
                TextView statusText = (TextView) view.findViewById(R.id.scheduleSearchStatus);
                statusText.setText(list.getJSONObject(i).getString("status"));

//                TextView userName = (TextView) view.findViewById(R.id.scheduleSearchResultName);
//                userName.setText(list.getJSONObject(i).getString("user"));



                myScheduleListLayout.addView(view);

                Log.d("user", list.getJSONObject(i).getString("user"));
            }
        }catch(Exception e){
            Log.e("Error on search result", "JSON", e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_schedule, menu);
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
