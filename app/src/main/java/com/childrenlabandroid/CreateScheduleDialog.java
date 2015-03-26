package com.childrenlabandroid;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import util.customInterface.ScheduleInterface;

/**
 * Created by Jay on 3/19/2015.
 */
public class CreateScheduleDialog extends Dialog implements View.OnClickListener {

    private Activity activity;
    private Dialog dialog;
    private TextView confirmButton, cancelButton, startDateText, endDateText;
    private EditText schedulePerHourEdit, scheduleNoteEdit;
    private Spinner scheduleType;
    private ScheduleInterface callback;
    private String startDate, endDate;

    public CreateScheduleDialog(Activity a, String startDate, String endDate, ScheduleInterface callback){
        super(a);

        this.callback = callback;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_create_schedule);

        scheduleType = (Spinner) findViewById(R.id.scheduleType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                R.array.schedule_type, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scheduleType.setAdapter(adapter);

        confirmButton = (TextView) findViewById(R.id.scheduleCreateConfirm);
        cancelButton = (TextView) findViewById(R.id.scheduleCancelButton);
        schedulePerHourEdit = (EditText) findViewById(R.id.schedulePerHourEdit);
        scheduleNoteEdit = (EditText) findViewById(R.id.scheduleNoteEdit);


        startDateText = (TextView) findViewById(R.id.startDateText);
        endDateText = (TextView) findViewById(R.id.endDateText);

        startDateText.setText(startDate);
        endDateText.setText(endDate);

        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scheduleCreateConfirm:
                String type = scheduleType.getSelectedItem().toString();
                String note = scheduleNoteEdit.getText().toString();
                int perHour = Integer.parseInt(schedulePerHourEdit.getText().toString());

                callback.createSchedule(type, note, perHour);

                break;
            case R.id.scheduleCancelButton:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
