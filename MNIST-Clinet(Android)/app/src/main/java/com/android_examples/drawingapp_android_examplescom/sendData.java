package com.android_examples.drawingapp_android_examplescom;


import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;


public class sendData extends AsyncTask<Void, Void, String> {
    public String picture = "";  public  String server_response ="";
    @Override
    public String doInBackground(Void... params) {
        try {
            HttpClient client = new DefaultHttpClient();
            String url = "http://YOUR URL/test";
            HttpPost post= new HttpPost(url);
            post.addHeader("content-type", "application/json");
            StringEntity entity = new StringEntity("{\"picture\":\"" + picture+ "\"}", HTTP.UTF_8);
            entity.setContentType("application/json");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            if(response.getStatusLine().getStatusCode()==200){
                server_response = EntityUtils.toString(response.getEntity());
            } else {
                Log.i("Server response", "Failed to get server response" );
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        return server_response;
    }

}



