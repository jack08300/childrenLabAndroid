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
import util.Tools;


public class SignupActivity extends ActionBarActivity implements View.OnClickListener {

    EditText emailEdit, fNameEdit, lNameEdit, errorMessage, passwordEdit, phoneEdit;
    TextView signupButton;
    String email, token, password, fname, lname, phone;
    RadioGroup genderGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fNameEdit = (EditText) findViewById(R.id.fNameEdit);
        lNameEdit = (EditText) findViewById(R.id.lNameEdit);
        emailEdit = (EditText) findViewById(R.id.emailEdit);
        errorMessage = (EditText) findViewById(R.id.errorMessage);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        signupButton = (TextView) findViewById(R.id.signupButton);
        genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
        phoneEdit = (EditText) findViewById(R.id.phoneEdit);

        signupButton.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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
    public void onClick(View v){
        switch (v.getId()){
            case R.id.signupButton:
                if(checkInput()){
                    register();
                }
                break;
        }
    }

    public void register(){
        try{

            int genderId = genderGroup.getCheckedRadioButtonId();
            String gender = "";
            if(genderId == R.id.maleRadioButton){
                gender = "Male";
            }else if(genderId == R.id.femaleRadioButton){
                gender = "Female";
            }
            Server server = new Server(this);

            String url = "user/register?email=" + email + "&password=" + password + "&lastName=" + lname + "&firstName=" + fname  + "&sex=" +
                    gender + "&phoneNumber=" + phone;

            JSONObject result = server.execute(url).get();

            if(result.getBoolean("success")){
                User user = new User(this);
                user.login(email, password);

                Intent dailyIntent = new Intent(SignupActivity.this, DataActivity.class);
                startActivity(dailyIntent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),
                        "Error on register, please try again later.", Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){
            Log.e("Error on register", "register", e);
        }

    }

    private boolean checkInput() {
        email = emailEdit.getText().toString();
        password = passwordEdit.getText().toString();
        lname = lNameEdit.getText().toString();
        fname = fNameEdit.getText().toString();
        phone = phoneEdit.getText().toString();
        if (email == null || email.length() < 5 || !Tools.checkEmailFormat(email)) {
            errorMessage.setText("Please check your email format.");
            return false;
        }

        if (password == null || password.length() < 5) {
            errorMessage.setText("Please check your password, at least 5 length.");
            return false;
        }

        if(fname == null){
            errorMessage.setText("Please check your name.");
            return false;
        }

        if(lname == null){
            errorMessage.setText("Please check your name.");
            return false;
        }

        return true;
    }
}
