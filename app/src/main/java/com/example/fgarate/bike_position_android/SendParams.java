package com.example.fgarate.bike_position_android;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by fgarate on 25/07/2017.
 */

public class SendParams extends AsyncTask<String, Integer, String> {
    private static final String TAG = "SendParams";
    /*
      * Método que se ejecuta en la hebra principal antes de dar paso a la hebra en segundo plano.
      *
      * Inicializa el progreso en 0
      */
    protected void onPreExecute(){
        Log.i(TAG, "::: onPreExecute ");

    }

    /*
     * Método que se ejecuta en segundo plano tras onPreExcute().
     *
     * Se encarga de llenar la barra de progreso hasta el porcentaje
     * de aciertos que se ha conseguido con las respuestas
     */
    protected String doInBackground(String...params) {
        String baseUrl = params[0];
        String jsonData = params[1];
        String result = postData(baseUrl,jsonData);
        return result;
    }

    /*
     * Método que se ejecuta en la hebra principal. Su ejecución se empieza al finalizar doInBackground().
     *
     * Establece el texto con el porcentaje que se encuentra en medio de la barra de progreso
     */
    protected void onPostExecute(String result){
        Log.i(TAG , "::: onPostExecute "+ result);

    }

    /*
     * Método que se ejecuta en primer plano. Puede ser llamado desde doInBackground() en cualquier momento.
     *
     * Rellena nuestra vista de la barra de progreso en cada llamada hasta el entero que le pasan como parámetro.
     */
    protected void onProgressUpdate(Integer... view){

    }
    public String postData(String baseUrl, String jsonData ) {
        // Create a new HttpClient and Post Header
        BufferedReader br = null;
        try{

            //Send Http PUT request to: "http://some.url" with request header:


            URL url = new URL(baseUrl);

            JSONObject result = new JSONObject();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("position", jsonData);
            String query = builder.build().getEncodedQuery();

            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(query);
            wr.flush();
            StringBuilder sb = new StringBuilder();
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                Log.i(TAG, "::: "+sb.toString());
                System.out.println("" + sb.toString());
            } else {
                Log.i(TAG, "::: "+con.getResponseMessage());
                System.out.println(con.getResponseMessage());
            }

            return sb.toString();

        } catch (Exception e) {
            //  Log.i("errror" , "::: "+e.getMessage());
            return "Exception happened: " + e.getMessage();
        }

    }

}

