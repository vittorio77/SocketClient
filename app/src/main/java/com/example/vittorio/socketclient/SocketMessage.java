package com.example.vittorio.socketclient;

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
 * Created by Vittorio on 14/12/2017.
 */

public class SocketMessage {
    String ip;
    int port;
    String message;
    BufferedReader bufferedReader;
    String TAG = "Socket_Class";
    String result;


    // creazione del costrutore
    public SocketMessage(String ip, int port, String message) {
        this.ip = ip;
        this.port = port;
        this.message = message;
    }

    public String send() throws IOException{

             // crea il socket
            Log.d(TAG, "Crea il socket");
            Socket socket = new Socket(ip, port);
            socket.setSoTimeout(5000);
            Log.d(TAG, "New Socket just created");
            if (socket.isConnected()) {

                OutputStream outputStream = socket.getOutputStream();

                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

                PrintWriter outToServer = new PrintWriter(outputStreamWriter);

                Log.d(TAG, "Print writer just done");

                InputStream inputStream = socket.getInputStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                bufferedReader = new BufferedReader(inputStreamReader);

                Log.d(TAG, "Buffered reader just done");

                outToServer.print(message);
                Log.d(TAG, "Message sent");

                //result = bufferedReaderToString(bufferedReader);

                outToServer.flush();

                result = bufferedReaderToString(bufferedReader);
                Log.d(TAG, "Risposta del server è:  " + result);
                //outToServer.flush();
                bufferedReader.close();
                outToServer.close();
                return result;
            } else{
                result="socket non connesso";
            }
            return result;
    }

    // metodo per convertire un oggetto BufferedReader  in una stringa
    private String bufferedReaderToString(BufferedReader br) {
        StringBuilder sb = new StringBuilder();
        String line;
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


