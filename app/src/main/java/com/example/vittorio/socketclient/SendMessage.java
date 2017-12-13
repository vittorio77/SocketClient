package com.example.vittorio.socketclient;


import android.os.AsyncTask;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * Created by Vittorio on 09/12/2017.
 */

/**TODO creare una classe indipendente per la creazione del socket e lo scambio di informazioni
 * TODO Spostare Async Task nella MainActivity
 * TODO Creare il metodo onPre exectute per bloccare il pulsante
 * TODO Creare anche gli altri due metodi dei Async Task e ripristinare il pulsante a fine operazione
 */

public class SendMessage extends AsyncTask<String,Void,String> {
    private Exception exception;
    private static final String TAG = "AsyncTask";

    @Override
    protected String doInBackground(String... params) {
        String result="";
        BufferedReader bufferedReader;
        try {
            try {

                // crea il socket
                Socket socket = new Socket("192.168.4.1", 9999);
                Log.d(TAG, "New Socket just created");

                OutputStream outputStream = socket.getOutputStream();

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

                PrintWriter outToServer = new PrintWriter(outputStreamWriter);

                Log.d(TAG, "Print writer just done");

                InputStream inputStream = socket.getInputStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                bufferedReader = new BufferedReader(inputStreamReader);

                Log.d(TAG, "Buffered reader just done");
                //result = convertStreamToString(messageFromServer);
                outToServer.print(params[0]);
                Log.d(TAG, "command sent");
                //result = bufferedReaderToString(bufferedReader);
                //Log.d(TAG, "command sent:"+result);
                outToServer.flush();
                result = bufferedReaderToString(bufferedReader);
                Log.d(TAG, "Risposta del server:"+result);
                bufferedReader.close();
                outToServer.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Error IOException");
            }
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
        return result;



    }



    // metodo per convertire un inputstream in una stringa
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    // metodo per convertire un oggetto BufferedReader  in una stringa
    private String bufferedReaderToString(BufferedReader br){
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}