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
public class ScheduleDetailDialog extends Dialog implements View.OnClickListener {

    private Activity activity;
    private Dialog dialog;
    private TextView startDateText, endDateText, nameText, noteText;
    private EditText schedulePerHourEdit, scheduleNoteEdit;
    private Spinner scheduleType;
    private ScheduleInterface callback;
    private String startDate, endDate, name, note;

    public ScheduleDetailDialog(Activity a, String startDate, String endDate, String name, String note){
        super(a);

        this.callback = callback;
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.note = note;
        this.activity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_schedule_detail);

        nameText = (TextView) findViewById(R.id.scheduleDetailName);
        noteText = (TextView) findViewById(R.id.scheduleDetailNote);
        startDateText = (TextView) findViewById(R.id.scheduleDetailFrom);
        endDateText = (TextView) findViewById(R.id.scheduleDetailTo);

        startDateText.setText(startDate);
        endDateText.setText(endDate);
        nameText.setText(name);
        noteText.setText(note);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
        dismiss();
    }
}
