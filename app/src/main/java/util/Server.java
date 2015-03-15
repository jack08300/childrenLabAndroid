package util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.childrenlabandroid.MainActivity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by Jay on 4/29/14.
 */
public class Server extends AsyncTask<String, Integer, JSONObject> {
    private SharedPreferences sharedData;
    private Context context;

    protected final String host = "http://www.childrenLab.com/";
//    protected final String host = "http://10.0.2.2:8070/childrenLab/";

    public Server(Context context){
        this.context = context;
        sharedData = context.getSharedPreferences("childrenLab", Context.MODE_PRIVATE);
    }

    protected JSONObject doInBackground(String... urls){
        JSONObject json = null;
        try{
            String token = sharedData.getString("token", null);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost request = new HttpPost();

            String url = (host + urls[0]).replace(" ", "%20");

            Log.d("Calling to Server:", url);

            URI serverAPI = new URI(url);
            request.addHeader("Accept", "application/json");
            if(token != null){
                request.addHeader("X-Auth-Token", token);
            }

            request.setURI(serverAPI);
            HttpResponse response = httpclient.execute(request);

            Log.d("Response Status", String.valueOf(response.getStatusLine().getStatusCode()));
            if(response.getStatusLine().getStatusCode() == 403){
                if(urls[0].contains("api/login") || urls[0].contains("test/token")){
                    JSONObject obj = new JSONObject();
                    obj.put("success", false);
                    sharedData.edit().putString("token", null).apply();
                    return obj;
                }else{
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    JSONObject obj = new JSONObject();
                    obj.put("success", false);
                    return obj;
                }

            }

            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String result = in.readLine();

            if(result == null || result.equals("")){
                JSONObject obj = new JSONObject();
                obj.put("success", false);
                sharedData.edit().putString("token", null).apply();
                return obj;
            }

            Log.d("Return from Server", result);

            json = new JSONObject(result);

        }catch(Exception e){
            Log.e("Http Request ", "Exception", e);
        }

        return json;
    }

}
