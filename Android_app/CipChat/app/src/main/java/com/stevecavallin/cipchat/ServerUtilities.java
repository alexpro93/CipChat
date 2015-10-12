package com.stevecavallin.cipchat;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Created by Steve on 07/07/14.
 */
public class ServerUtilities {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static final String TAG = "ServerUtilities";


    public static String send(final Context context,final String msg, String to, String from) throws IOException {
        Log.i(TAG, "sending message (msg = " + msg + ")");
        String serverUrl = context.getString(R.string.server_addr);
        Map<String, String> params = new HashMap<String, String>();
        params.put(DataContentProvider.COL_TESTO, msg);
        params.put(DataContentProvider.COL_DEST, to.replace("+","%2B"));
        params.put("Mittente",from);
        return post(serverUrl, params, MAX_ATTEMPTS);
    }

    /** Issue a POST with exponential backoff */
    private static String post(String endpoint, Map<String, String> params, int maxAttempts) throws IOException {
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        for (int i = 1; i <= maxAttempts; i++) {
            Log.d(TAG, "Attempt #" + i + " to connect");
            try {
                return executePost(endpoint, params);
            } catch (IOException e) {
                Log.e(TAG, "Failed on attempt " + i + ":" + e);
                if (i == maxAttempts) {
                    throw e;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    return null;
                }
                backoff *= 2;
            } catch (IllegalArgumentException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
        return null;
    }

    private static String executePost(String endpoint, Map<String, String> params) throws IOException {
        URL url;
        StringBuilder response = new StringBuilder();
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=').append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        URLEncoder.encode(body,"UTF-8");
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();


            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);

            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                Log.i(TAG,"POSTED!");
                in.close();
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return response.toString();
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use
     * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
     * since the device sends upstream messages to a server that echoes back the
     * message using the 'from' address in the message.
     */
    public static String sendRegistrationIdToBackend(Context context, String regId, String phonenumb, String nickname,String email) throws IOException {
        String serverUrl = context.getString(R.string.server_addr_utenti); // aggiungi su string l'indirizzo del server
        Map<String, String> params = new HashMap<String, String>();
        params.put("Reg_id", regId);
        params.put("Numero", phonenumb);
        params.put("Nickname",nickname);
        params.put("Email",email);

        return post(serverUrl, params, MAX_ATTEMPTS);


    }

    public static boolean isCodeRight(Context context, String code,String numero) throws IOException{
        String serverUrl = context.getString(R.string.server_addr_code);
        Map<String, String> params = new HashMap<String, String>();
        params.put("Numero",numero);
        params.put("Codice",code);
        String response = post(serverUrl,params,MAX_ATTEMPTS);
        Log.i(TAG,response);
        if(response.equals("OK")){
            return true;
        }
        else return false;
    }

    public static String checkContacts(Context context,String[] numbers,String mynumber) throws IOException{
        String serverUrl = context.getString(R.string.server_addr_check);
        String s="";
        if(numbers.length>0){
            s+=numbers[0];
            for(int i=1;i<numbers.length;i++){
                s+="&Contatti[]="+numbers[i];
            }
        }
        s=s.replace("+","%2B");
        Map<String,String> params=new HashMap<String, String>();
        params.put("Contatti[]",s);
        params.put("Numero",mynumber);
        return post(serverUrl,params,MAX_ATTEMPTS);
    }
}
