package keene.drugdatabase;

/**
 * Created by BigBen on 5/3/17.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;



import cz.msebera.android.httpclient.*;
import cz.msebera.android.httpclient.client.*;
import cz.msebera.android.httpclient.impl.client.*;
import cz.msebera.android.httpclient.client.methods.*;



public class getImage {

    public static Bitmap getImage(String IMG_URL) {
        InputStream in = null;
        int resCode = -1;
        Bitmap bitmap;

        HttpURLConnection httpConn = null;
        try {

            //two ways to do this:

            //method 1
            try {
                //HttpClient httpclient = new DefaultHttpClient();
                HttpClient httpclient = HttpClientBuilder.create().build();
                HttpGet httpget = new HttpGet(IMG_URL);
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                in = entity.getContent();

            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
            }

            //method 2
            /*
            URL url = new URL(IMG_URL);
            URLConnection urlConn = url.openConnection();
            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }
            httpConn = (HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
            */

            bitmap = BitmapFactory.decodeStream(in);
            return bitmap;

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Throwable t) {
            }
            try {
                httpConn.disconnect();
            } catch (Throwable t) {
            }
        }
        return null;
    }
}
