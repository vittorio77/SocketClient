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
    String message;
    Button buttonConnect, buttonClear;
    String serverAnswer;
    String TAG = "Main_Activity";
    static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // definizione e avvio toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // inizializzazione variabile ip dalle sharedprefereces
        // ed associazione con il textview sulla main activity

        ip = loadSharedPreferences()[0];
        TextView ipTextView = (TextView) findViewById(R.id.ipTextView);
        ipTextView.setText(ip);

        port = loadSharedPreferences()[1];
        TextView portTextView = (TextView) findViewById(R.id.portTextView);
        portTextView.setText(port);

        message = loadSharedPreferences()[2];
        TextView messageTextView = (TextView) findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        TextView wifiNameView = (TextView) findViewById(R.id.wlanTextView);
        buttonConnect = (Button) findViewById(R.id.connectButton);

        //TODO definire un broadcast reveiver
        // Verifica wifi attiva e scritura nel textview associato
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



                Log.d(TAG, "Inizializzo start");
                Message start = new Message(ip,portToUse,message);
                ///String result =start.toString();

                Log.d(TAG, "Eseguo AsyncTask");
                start.execute(message);
            }
        });
    }

    // questo metodo viene usato quanto si passa dalla activity2 a questa activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        activityReference.recreate();
    }

    // Recupero  valori dalle sharedPreferences se già esistono
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
            values[3] = pref.getString("SSID", null);
        } else { values[3] = "NOT DEFINED";}

        return values;
    }


    // Metodo per effettuare delle scelte e quind azioni al click delle voci del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            // Inizializzazione intent per avviare Activity2Aettings
            Intent intent = new Intent(this, Activity2Settings.class);

            // start activityForREsult permette di aver feedback dalla activity che si sta per lancaire
            // questi risultati vengono poi riportati in questa ctivity mediante il metodo onActivityResult
            startActivityForResult(intent,REQUEST_CODE);
            return true;
        }
        if (id == R.id.about) {
            CustomAlertDialog dialog = new CustomAlertDialog(this,"App realizzata da:","Vittorio77");
            // alert dialog permette di rendere sfocato lo sfondo
            return true;
        }
        return true;
    }

    // Carica il menu a tendina 3dots all'avvio utilizzando il file menu.xml
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Created by Vittorio on 18/012/2017.
     * ------  MAIN FUNCTION ---------
     * Metodo per la crezione di un socket-invio messaggio e ricezione risposta
     * utilizzando la calsse socket e mediante il metodo AsyncTask
     */
    private class Message  extends AsyncTask<String, Void, String> {
        String ipAddress;
        int serverPort;
        String message;

        // Costruttore
        private Message (String ipAddress,int serverPort,String message) {
            this.ipAddress = ipAddress;
            this.serverPort = serverPort;
            this.message = message;
        }

        // metodo di Async Task
        // è il primo che viene eseguito all' avvio dell async task
        protected void onPreExecute() {
            final Button connect = (Button) findViewById(R.id.connectButton);
            connect.setEnabled(false);

            final TextView View = (TextView) findViewById(R.id.responseTextView);
            View.setText(null);
        }

        // metodo di Async Task
        protected String doInBackground(String... commandList) {
            String answerFromServer=".....";

            Log.d(TAG, "Metodo doInBackground-- Inizializzo socket Message");
            SocketMessage socketMessage= new SocketMessage (ipAddress,serverPort,commandList[0]);
            //answerFromServer =" Problemi di comunicazione con il socket";

            try {
                answerFromServer= socketMessage.send();
            } catch (IOException e) {
                e.printStackTrace();
                answerFromServer =" Problemi di comunicazione con il socket";
                Log.d(TAG, "Problemi di comunicazione con il socket "+e);
            }
            return answerFromServer;
        }

        // metodo di Async Task che viene eseguito durante il thread
        protected void onProgressUpdate(Integer... progress) {
            final TextView view = (TextView) findViewById(R.id.responseTextView);
            view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            view.setText("Working...");
        }

        // metodo di Async Task
        // Ha come argomento l'output del do In Backgroung
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


