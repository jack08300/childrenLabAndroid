package com.childrenlabandroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.childrenlabandroid.DataActivity;
import com.childrenlabandroid.SignupActivity;

import data.User;
import util.Tools;


public class MainActivity extends ActionBarActivity {

    TextView emailField, passwordField, signinButton, signupText, errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailField = (TextView) findViewById(R.id.loginEmailField);
        passwordField = (TextView) findViewById(R.id.loginPasswordField);
        signinButton = (TextView) findViewById(R.id.signinButton);
        signupText = (TextView) findViewById(R.id.signupText);
        errorMessage = (TextView) findViewById(R.id.loginErrorMessage);

        attachEvent();
    }

    public void attachEvent(){
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(signinButton.isEnabled()){
                    displayError("");
                    signinButton.setEnabled(false);
                    login();
                }
            }
        });

        signupText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent signupIntent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(signupIntent);

            }
        });
    }

    public void login(){
        final String email = emailField.getText().toString();
        final String password = passwordField.getText().toString();

        if(checkInput(email, password)){
            final User user = new User(this);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(user.login(email, password)){
                        Intent dailyIntent = new Intent(MainActivity.this, DataActivity.class);
                        startActivity(dailyIntent);
                        finish();
                    }else{
                        displayError("Wrong Email or Password.");
                        signinButton.setEnabled(true);
                    }
                }
            });


        }else{
            displayError("Check your email and password.");
            signinButton.setEnabled(true);
        }
    }

    public void displayError(String message){
        errorMessage.setText(message);
    }

    private boolean checkInput(String email, String password) {
        if (email == null || email.length() < 5 || !Tools.checkEmailFormat(email)) {
            displayError("Please check your email format.");
            return false;
        }

        if (password == null || password.length() < 5) {
            displayError("Please check your password, at least 5 length.");
            return false;
        }

        return true;
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
