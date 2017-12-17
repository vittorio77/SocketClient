package com.example.vittorio.socketclient;


 import android.app.Activity;
 import android.content.Context;
 import android.content.Intent;
 import android.content.SharedPreferences;
 import android.net.ConnectivityManager;
 import android.net.NetworkInfo;
 import android.os.AsyncTask;
 import android.os.Bundle;

 import android.support.v7.widget.Toolbar;
 import android.util.Log;
 import android.view.Menu;
 import android.view.MenuInflater;
 import android.view.MenuItem;
 import android.view.View;
 import android.view.View.OnClickListener;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.TextView;
 import android.support.v7.app.AppCompatActivity;
 import java.io.IOException;


// per non aver prblemi con le tool bar è necessario aver come classe ereditata AppCompatActivity invece di Acvivity
public class MainActivity extends AppCompatActivity {
    Activity activityReference = this;
    TextView response;
    String ip;
    String port;
    EditText message;
    Button buttonConnect, buttonClear;
    String serverAnswer;
    String TAG = "Main_Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ip = loadSharedPreferences()[0];
        TextView ipTextView = (TextView) findViewById(R.id.ipTextView);
        ipTextView.setText(ip);

        port = loadSharedPreferences()[1];
        TextView portTextView = (TextView) findViewById(R.id.portTextView);
        portTextView.setText(port);

        message = (EditText) findViewById(R.id.messageEditText);

        TextView wifiNameView = (TextView) findViewById(R.id.wlanTextView);
        buttonConnect = (Button) findViewById(R.id.connectButton);

        String ssid= InfoWifiActive();
        wifiNameView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        wifiNameView.setText(ssid);


        buttonConnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                int portToUse;
                try {
                    Log.d(TAG, "Conversione della stringa in int");
                    portToUse = Integer.parseInt(port);
                }catch (NumberFormatException e) {
                    portToUse =1234;
                }

                Log.d(TAG, "Get message from text View");
                String messageToSend = message.getText().toString();

                Message start = new Message(ip,portToUse,messageToSend);

                String result =start.toString();
                // lancio il metodo on Post Exectute
                //start.onPostExecute(result);
                // lancio il metodo on progress update
                //start.onProgressUpdate();

                Log.d(TAG, "execute to Start");
                start.execute(messageToSend);


            }
        });
    }

    // load values from SheredPreferences if they exist
    private String[] loadSharedPreferences() {
        String[] values = new String[4];
        SharedPreferences pref = getSharedPreferences("MyPref", MODE_PRIVATE);

        if (pref.contains("ipAddress")) {
            values[0] = pref.getString("ipAddress", null);
        } else { values[0] = "NOT DEFINED";}

        if (pref.contains("serverPort")) {
            values[1] = pref.getString("serverPort", null);
        } else { values[1] = "NOT DEFINED";}

        if (pref.contains("message")) {
            values[2] = pref.getString("message", null);
        } else { values[2] = "NOT DEFINED";}

        if (pref.contains("SSID")) {
            values[2] = pref.getString("SSID", null);
        } else { values[2] = "NOT DEFINED";}

        return values;
    }


    // metodo per effettuare delle scelte e quind azioni al click delle voci del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            // do action
            Intent intent1 = new Intent(this, Activity2Settings.class);
            startActivity(intent1);
            return true;
        }
        if (id == R.id.about) {
            CustomAlertDialog dialog = new CustomAlertDialog(this,"App realizzata da:","Vittorio77");
            // do action
            // alert dialog permette di rendere sfocato lo sfondo
            return true;
        }
        return true;
    }

    // carica il menu a tendina 3dots all'avvio utilizzando il file menu.xml
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu, menu);
        return true;
    }


    private class Message  extends AsyncTask<String, Void, String> {
        String ipAddress;
        int serverPort;
        String message;

        // costruttore
        private Message (String ipAddress,int serverPort,String message) {
            this.ipAddress = ipAddress;
            this.serverPort = serverPort;
            this.message = message;
        }

        // metodo di Async Task
        protected void onPreExecute() {
            final Button connect = (Button) findViewById(R.id.connectButton);
            connect.setEnabled(false);
            final TextView View = (TextView) findViewById(R.id.responseTextView);
            View.setText(null);
        }

        protected String doInBackground(String... commandList) {
            String answerFromServer=".....";
            Log.d(TAG, "Metodo doInBackground");

            SocketMessage socketMessage1= new SocketMessage (ipAddress,serverPort,commandList[0]);
            answerFromServer =" Problemi di comunicazione con il socket";


            try {
                answerFromServer= socketMessage1.send();
            } catch (IOException e) {
                e.printStackTrace();
                answerFromServer =" Problemi di comunicazione con il socket";
            }
            return answerFromServer;
        }

        protected void onProgressUpdate(Integer... progress) {
            final TextView view = (TextView) findViewById(R.id.responseTextView);
            view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            view.setText("Working...");
        }

        protected void onPostExecute(String answer) {
            final TextView View = (TextView) findViewById(R.id.responseTextView);
            View.setText(answer);
            final Button connect = (Button) findViewById(R.id.connectButton);
            connect.setEnabled(true);
        }
    }

    /**
     * Created by Vittorio on 11/09/2017.
     * Metodo per la verifica della connessione WIFI o Internet
     */
    private String InfoWifiActive() throws ArrayIndexOutOfBoundsException, NullPointerException {
        String result = null;
        try {
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean checkInternet = connManager.getActiveNetworkInfo().isAvailable();

            if (checkInternet) {
                NetworkInfo mWifi = connManager.getActiveNetworkInfo();
                String info = mWifi.toString();
                String[] arrayInfo = info.split("\"", -1);//per Ottenere il nome della rete
                result = arrayInfo[1];
                Log.d(TAG, "WLAN= "+result);
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            e.printStackTrace();
            result = "NO CONNECTION";
        }
        return result;
    }

}


