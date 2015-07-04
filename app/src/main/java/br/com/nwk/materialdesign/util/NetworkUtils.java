package br.com.nwk.materialdesign.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;

/**
 * Created by rma19_000 on 03/07/2015.
 */
public class NetworkUtils {
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public String doGetRequest (String protocol, String host, String path){
        String responseStr = null;
        try{
            URI uri = URIUtils.createURI(protocol, host, 0, path, null, null);
            HttpGet get = new HttpGet(uri);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            responseStr = EntityUtils.toString(entity);

        }
        catch (Exception e){}
        return responseStr;
    }
}
