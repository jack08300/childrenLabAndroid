package com.childrenlabandroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import data.User;
import util.Server;


public class AddKidsActivity extends ActionBarActivity {

    EditText FNameEdit, LNameEdit, NNameEdit, BirthdayEdit, SREdit;
    TextView EnterButton, ErrorMessage;
    String email, token, password, fname, lname, phone;
    RadioGroup GenderGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kids);

        FNameEdit = (EditText) findViewById(R.id.FNameEdit);
        LNameEdit = (EditText) findViewById(R.id.LNameEdit);
        NNameEdit = (EditText) findViewById(R.id.NNameEdit);
        BirthdayEdit = (EditText) findViewById(R.id.BirthdayEdit);
        SREdit = (EditText) findViewById(R.id.SREdit);
        EnterButton.setOnClickListener();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_kids, menu);
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
    public void onClick(View v){
        switch (v.getId()){
            case R.id.EnterButton:
                if(checkInput()){
                    register();
                }
                break;
        }
    }

    private void register() {
        try{
            int genderId = GenderGroup.getCheckedRadioButtonId();
            String gender = "";
            if(genderId == R.id.boyRadioButton){
                gender = "Boy";
            }else if(genderId == R.id.girlRadioButton){
                gender = "Girl";
            }
            Server server = new Server(this);

/*            String url = "user/register?email=" + email + "&password=" + password + "&lastName=" + lname + "&firstName=" + fname  + "&sex=" +
                    gender + "&phoneNumber=" + phone;

            JSONObject result = server.execute(url).get();

            if(result.getBoolean("success")){
                User user = new User(this);
                user.login(email, password);

                Intent dailyIntent = new Intent(AddKidsActivity.this, DataActivity.class);
                startActivity(dailyIntent);
*/                finish();
            }else{
                Toast.makeText(getApplicationContext(),
                        "Error on register, please try again later.", Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){
            Log.e("Error on register", "register", e);
        }
    }

    private boolean checkInput() {
    return true;
    }

}
