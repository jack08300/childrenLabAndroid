package com.childrenlabandroid;

import android.app.Dialog;
import android.content.Intent;
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


import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import util.Server;
import util.customInterface.ScheduleInterface;


public class ScheduleActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener, ScheduleInterface  {

    private static final String TIME_PATTERN = "HH:mm";
    private static final String SERVER_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    private TextView fromDatePicker, fromTimePicker, toDatePicker, toTimePicker;
    private Calendar fromCalendar, toCalendar;
    private DateFormat dateFormat;
    private SimpleDateFormat timeFormat, serverDateTime;
    private boolean isFromDate = false, isToDate = false, isTime = false;
    private CreateScheduleDialog dialog;
    private LinearLayout scheduleSearchResultList;

    private TextView createButton, searchButton, myScheduleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        fromCalendar = Calendar.getInstance();
        fromCalendar.set( fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.DAY_OF_MONTH));
        toCalendar = Calendar.getInstance();
        toCalendar.set( fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.DAY_OF_MONTH) + 1);

        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());
        serverDateTime = new SimpleDateFormat(SERVER_DATE_TIME_FORMAT,  Locale.getDefault());
        fromDatePicker = (TextView) findViewById(R.id.fromDatePicker);
        fromTimePicker = (TextView) findViewById(R.id.fromTimePicker);
        toDatePicker = (TextView) findViewById(R.id.toDatePicker);
        toTimePicker = (TextView) findViewById(R.id.toTimePicker);


        createButton = (TextView) findViewById(R.id.createButton);
        searchButton = (TextView) findViewById(R.id.searchButton);
        myScheduleButton = (TextView) findViewById(R.id.myScheduleButton);
        scheduleSearchResultList = (LinearLayout) findViewById(R.id.scheduleSearchResultList);

        createButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        myScheduleButton.setOnClickListener(this);

        fromDatePicker.setOnClickListener(this);
        fromTimePicker.setOnClickListener(this);
        toDatePicker.setOnClickListener(this);
        toTimePicker.setOnClickListener(this);

        updateAll();
    }


    private void updateAll() {
            fromDatePicker.setText(dateFormat.format(fromCalendar.getTime()));
            fromTimePicker.setText(timeFormat.format(fromCalendar.getTime()));

            toDatePicker.setText(dateFormat.format(toCalendar.getTime()));
            toTimePicker.setText(timeFormat.format(toCalendar.getTime()));
    }

    private void updateFromDateTime(){
        isFromDate = false;
        fromDatePicker.setText(dateFormat.format(fromCalendar.getTime()));
        if(isTime){
            fromTimePicker.setText(timeFormat.format(fromCalendar.getTime()));
            isTime = false;
        }else{
            fromTimePicker.performClick();
        }


    }

    private void updateToDateTime(){
        toDatePicker.setText(dateFormat.format(toCalendar.getTime()));
        isToDate = false;
        if(isTime){
            toTimePicker.setText(timeFormat.format(toCalendar.getTime()));
            isTime = false;
        }else{
            toTimePicker.performClick();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fromDatePicker:
                isFromDate = true;
                DatePickerDialog.newInstance(this, fromCalendar.get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
                break;
            case R.id.fromTimePicker:
                isFromDate = true;
                TimePickerDialog.newInstance(this, fromCalendar.get(Calendar.HOUR_OF_DAY), fromCalendar.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
                break;
            case R.id.toDatePicker:
                isToDate = true;
                DatePickerDialog.newInstance(this, toCalendar.get(Calendar.YEAR), toCalendar.get(Calendar.MONTH), toCalendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
                break;
            case R.id.toTimePicker:
                isToDate = true;
                TimePickerDialog.newInstance(this, toCalendar.get(Calendar.HOUR_OF_DAY), toCalendar.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
                break;

            case R.id.createButton:
                dialog = new CreateScheduleDialog(ScheduleActivity.this, fromDatePicker.getText().toString(), toDatePicker.getText().toString(), ScheduleActivity.this);
                dialog.show();
                break;

            case R.id.searchButton:
                getSearchResult();
                break;

            case R.id.myScheduleButton:
                Intent myScheduleIntent = new Intent(ScheduleActivity.this, MyScheduleActivity.class);
                startActivity(myScheduleIntent);
                break;
        }
    }
    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        Log.d("is to dTE", String.valueOf(isToDate));
        if(isFromDate){
            fromCalendar.set(year, monthOfYear, dayOfMonth);
            updateFromDateTime();
        }else if(isToDate){
            toCalendar.set(year, monthOfYear, dayOfMonth);
            updateToDateTime();
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        isTime = true;
        Log.d("is from date time", String.valueOf(isFromDate));
        Log.d("is from date time time", String.valueOf(isTime));
        if(isFromDate){
            fromCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            fromCalendar.set(Calendar.MINUTE, minute);
            updateFromDateTime();
        }else if(isToDate){
            toCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            toCalendar.set(Calendar.MINUTE, minute);
            updateToDateTime();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
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

    @Override
    public void createSchedule(String type, String note, int perHour){
        String fromDate = serverDateTime.format(fromCalendar.getTime());
        String toDate = serverDateTime.format(toCalendar.getTime());

        Log.d("Schedule Type", type);
        Log.d("Schedule Note", note);
        Log.d("Schedule PerHour", String.valueOf(perHour));
        Log.d("Schedule From Date", fromDate);
        Log.d("Schedule to Date", toDate);

        if(type.equals("JOB")){
            type = "PARENT";
        }else if(type.equals("Nanny")){
            type = "NANNY";
        }

        try{
            Server server = new Server(this);
            JSONObject resultJSON = server.execute("schedule/create/?startDate=" + fromDate + "&endDate=" + toDate + "&paymentPerHour=" + perHour + "&type=" + type + "&note=" + note).get();
            if(resultJSON.getBoolean("success")){
                Toast.makeText(getApplicationContext(),
                        "Success create schedule.", Toast.LENGTH_LONG).show();

                dialog.dismiss();
            }else{
                String message = resultJSON.getString("message");
                if(message == null || message.length() < 2){
                    message = "Error on schedule creation, please try again later.";
                }
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            Log.e("Error on calling server", "schedule creation", e);

            Toast.makeText(getApplicationContext(),
                    "Error on schedule creation, please try again later..", Toast.LENGTH_LONG).show();

        }
    }

    public void getSearchResult(){
        scheduleSearchResultList.removeAllViews();
        String fromDate = serverDateTime.format(fromCalendar.getTime());
        String toDate = serverDateTime.format(toCalendar.getTime());

        try{
            Server server = new Server(this);
            JSONObject resultJSON = server.execute("schedule/search/?startDate=" + fromDate + "&endDate=" + toDate).get();


            if(resultJSON.getBoolean("success")){
                showScheduleSearchResult(resultJSON.getJSONArray("scheduleList"));

            }else{
                String message = resultJSON.getString("message");
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){
            Log.e("Server error", "calling server", e);
            Toast.makeText(getApplicationContext(),
                    "Error on schedule creation, please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    public void showScheduleSearchResult(final JSONArray list){
        try{


            for(int i=0; i<list.length() ;i++){
                final String fromDate = list.getJSONObject(i).getString("startDate");
                final String toDate = list.getJSONObject(i).getString("endDate");
                final String name = list.getJSONObject(i).getString("user");
                final String id = list.getJSONObject(i).getString("id");

                View view = LayoutInflater.from(this).inflate(R.layout.schedule_item, null);

                TextView fromDateText = (TextView) view.findViewById(R.id.scheduleSearchResultFromDate);
                fromDateText.setText(fromDate);
                TextView toDateText = (TextView) view.findViewById(R.id.scheduleSearchResultToDate);
                toDateText.setText(toDate);

                TextView userName = (TextView) view.findViewById(R.id.scheduleSearchResultName);
                userName.setText(name);

                scheduleSearchResultList.addView(view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getScheduleDetail(id);
                    }
                });

            }
        }catch(Exception e){
            Log.e("Error on search result", "JSON", e);
        }

    }

    public void getScheduleDetail(String id){
        try{
            Server server = new Server(this);
            JSONObject jsonResult = server.execute("schedule/retrieveUserSchedule/?scheduleId=" + id).get();
            JSONObject schedule = jsonResult.getJSONObject("schedule");

            if(jsonResult.getBoolean("success")){

                Dialog detailDialog = new ScheduleDetailDialog(ScheduleActivity.this,
                        schedule.getString("startDate"),
                        schedule.getString("endDate"),
                        schedule.getString("user"),
                        schedule.getString("note"));
                detailDialog.show();
            }


        }catch(Exception e){
            Log.e("Error on Schedul Detail", "Schedule detail", e);
            Toast.makeText(getApplicationContext(), "Couldn't get the detail, please try it again later.", Toast.LENGTH_LONG).show();
        }

    }
}
