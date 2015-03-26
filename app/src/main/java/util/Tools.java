package util;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jay on 3/7/2015.
 */
public class Tools {
    public static boolean checkEmailFormat(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public JSONObject callAPI(Context context, String path, Map<String, String> params) throws JSONException{
        JSONObject result = new JSONObject();
        try{
            Server server = new Server(context);
        }catch(Exception e){
            Log.e("Error on calling Server", path, e);
            result.put("success", false);
        }

        return result;
    }
}
