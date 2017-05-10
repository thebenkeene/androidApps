package keene.drugdatabase;

/**
 * Created by signoril on 3/31/17.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.net.*;



import java.net.*;

import android.util.Log;

public class JSONfunctions {

    public static JSONObject getJSONfromURL(String url) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL newurl = new URL(url);
            connection = (HttpURLConnection) newurl.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                Log.d("Response: ", "> " + line);//here u ll get whole response...... :-)

            }

            return new JSONObject(buffer.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new JSONObject();

    }
}