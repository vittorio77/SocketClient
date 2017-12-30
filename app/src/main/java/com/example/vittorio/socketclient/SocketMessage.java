package com.example.vittorio.socketclient;

import android.util.Log;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
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
    int TIME_OUT=5000;


    // creazione del costrutore
    public SocketMessage(String ip, int port, String message) {
        this.ip = ip;
        this.port = port;
        this.message = message;
    }

    public String send() throws IOException{

            InetSocketAddress inetSocketAddress = new InetSocketAddress(ip,port);
            // crea il socket
            Socket socket = new Socket();
            socket.setTcpNoDelay(true);
            socket.getKeepAlive();
            socket.setReceiveBufferSize(1024);
            //socket.setReuseAddress(true);

            // connette il socket
            long now = System.currentTimeMillis();
            Log.d(TAG, "Connessione al socket...."+now);

            socket.connect(inetSocketAddress,TIME_OUT);

            Long nowNow =System.currentTimeMillis();
            Log.d(TAG, "Socket connesso -- tempo di connessione:"+(nowNow-now));

            //if (socket.isConnected()) {


                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());


                PrintWriter outToServer = new PrintWriter(outputStreamWriter);

                Log.d(TAG, "Print writer just done");

                InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                Log.d(TAG, "Buffered reader just done");

                outToServer.print(message);
                Log.d(TAG, "Message sent");

                outToServer.flush();
                Log.d(TAG, "Server flush");

                // Conversione del bufferedReader in String
                Log.d(TAG, "Conversion Start");
                String response = new String();
                for (String line; (line = bufferedReader.readLine()) != null; response += line);

                result = response;
                Log.d(TAG, "Server Answer:  " + result);

                bufferedReader.close();
                outToServer.close();
                return result;
            //} else{
            //    result="Nessuna risposta dal server";
            //}
            //return result;
    }

    // Metodo per convertire un oggetto BufferedReader  in una stringa
    // è un medoto piuttosto lento e non conviene utilizzarlo
    private String bufferedReaderToString(BufferedReader br) {
        Log.d(TAG, "conversion Start");
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


