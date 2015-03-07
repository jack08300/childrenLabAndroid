package data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.childrenlabandroid.MainActivity;

import org.json.JSONObject;

import util.Server;

/**
 * Created by Jay on 8/9/14.
 */
public class User {

    private String email, password, token;
    private SharedPreferences sharedData;
    private String errorMessage;
    private boolean returnUser = false;
    private Context context;

    public User(Context context){
        sharedData = context.getSharedPreferences("childrenLab", Context.MODE_PRIVATE);
        this.context = context;
        if(sharedData.getString("email", null) != null){
            email = sharedData.getString("email", null);
            password = sharedData.getString("password", null);
            token = sharedData.getString("token", null);
            returnUser = true;
        }
    }

    public boolean login(String email, String password){
        this.email = email;
        this.password = password;

        return login();
    }

    public boolean register(String email, String password, String name, String age, String gender, String height, String weight, String ethnicity){

        String params = "register=true&name=" + name + "&age=" + age + "&height=" + height + "&weight=" + weight + "&gender=" + gender + "&ethnicity=" + ethnicity + "&email=" + email + "&password=" + password;
        try{
            Server server = new Server(context);
            JSONObject resultJSON = server.execute("user/loginOrRegister/?" + params).get();

            if (resultJSON.get("success").toString().equals("true")) {

                sharedData.edit().putString("email", email).apply();
                sharedData.edit().putString("password", password).apply();

                return login();
            } else {
                return false;
            }

        }catch(Exception e){}

        return false;
    }

    public boolean login(){
        try{

            if(!hasToken()){
                Server server = new Server(context);
                JSONObject resultJSON = server.execute("api/login?" + "email=" + email + "&password=" + password).get();

                Log.d("Login Response", resultJSON.toString());

                if (resultJSON.has("access_token")) {
                    Log.d("Login result: ", resultJSON.toString());
                    sharedData.edit().putString("token", resultJSON.getString("access_token")).apply();
                    sharedData.edit().putString("email", email).apply();
                    sharedData.edit().putString("password", password).apply();

                    Log.d("TOKEN", sharedData.getString("token", null));
                    return true;
                } else {
                    errorMessage = "Please check your email and password.";
                    return false;
                }
            }else{
                return true;
            }


        }catch(Exception e){
            Log.e("Login error", "Error on login", e);
            return false;
        }
    }

    public boolean logout(){
        try{
            Server server = new Server(context);
            server.execute("api/logout").get();


            sharedData.edit().putString("token", null).apply();
            sharedData.edit().putString("email", null).apply();
            sharedData.edit().putString("password", null).apply();

            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);

            return true;

        }catch(Exception e){
            Log.e("Logout error", "Error on logout", e);
            return false;
        }
    }

    public JSONObject getUserInfo(){
        JSONObject resultJSON = null;
        try{
            Server server = new Server(context);
            resultJSON = server.execute("user/getUserInfo").get();
        }catch(Exception e){
            Log.e("Error on Getting user", e.toString());
        }

        return resultJSON;
    }

    public String getErrorMessage() {return errorMessage;}
    public Boolean getReturnUser() { return returnUser; }
    public Boolean hasToken(){ return token != null;}
}
