package com.example.vittorio.socketclient;


 import android.app.Activity;
 import android.content.BroadcastReceiver;
 import android.content.Context;
 import android.content.Intent;
 import android.content.IntentFilter;
 import android.content.SharedPreferences;
 import android.net.ConnectivityManager;
 import android.net.wifi.WifiInfo;
 import android.net.wifi.WifiManager;
 import android.os.AsyncTask;
 import android.os.Bundle;
 import android.os.Vibrator;
 import android.support.v7.widget.Toolbar;
 import android.util.Log;
 import android.view.Menu;
 import android.view.MenuInflater;
 import android.view.MenuItem;
 import android.view.View;
 import android.view.View.OnClickListener;
 import android.widget.Button;
 import android.widget.TextView;
 import android.support.v7.app.AppCompatActivity;
 import android.widget.Toast;

 import java.io.IOException;

 import static android.R.drawable.button_onoff_indicator_off;
 import static android.R.drawable.button_onoff_indicator_on;
 import static android.os.VibrationEffect.createOneShot;

// per non aver problemi con le tool bar è necessario aver come classe ereditata AppCompatActivity invece di Acvivity
public class MainActivity extends AppCompatActivity {
    Activity activityReference = this;
    TextView response,wlanSsid;
    String ip, port, message, SSID;
    Button buttonSend,buttonConnect, buttonDisconnect, buttonTest;
    String serverAnswer;
    String TAG = "Main_Activity";
    String TAG1 = "WIFI";
    String TAG2 = "vibrator";
    String TAG3 = "THREAD";
    String TAG4 = "LIST";
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

        SSID = loadSharedPreferences()[3];
        TextView wifiNameView = (TextView) findViewById(R.id.SSIDTextView);
        wifiNameView.setText(SSID);

        // Associo le variabili ai widget
        wlanSsid = (TextView) findViewById(R.id.wlanTextView);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonConnect = (Button) findViewById(R.id.buttonConnect);
        buttonDisconnect = (Button) findViewById(R.id.buttonDisconnect);
        buttonTest = (Button) findViewById(R.id.buttonTest);


        ///*****************************************************************
        // Verifica se la connessione wifi è accesa, nel caso non lo fosse
        // provvede alla sua accensione
        WifiHandler startHandler = new WifiHandler();
        startHandler.wifiEnableIfDisabled(getApplicationContext());
        //******************************************************************
        // dopo aver effettuato l'accessione si effettua una scansione degli AP in modo asincrono con async task
        Log.d(TAG4, "Eseguo AsyncTask per la scansione degli ap");
        new Scan().execute();


        //TODO scrivere dei commenti

        //registerReceiver(BroadcastReceiver receiver, IntentFilter filter)
        //Register a BroadcastReceiver to be run in the main activity thr

        // la costante  CONNECTIVITY_ACTION rileva se cambia la connettività del network
        // da internet: A change in network connectivity has occurred.
        final String CONNECTIVITY_ACTION = ConnectivityManager.CONNECTIVITY_ACTION;

        // Istanzio un intent filter con il costruttore che richiede solo la String action come argomento
        IntentFilter intentFilter = new IntentFilter(CONNECTIVITY_ACTION);

        // registerReceiver è un metodo pubblico della classe astratta Context
        this.registerReceiver(this.broadcastWifiReceiver,intentFilter);


        // #####################   BUTTON SEND LISTENER   #######################

        buttonSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                int portToUse;
                try {
                    Log.d(TAG, "Conversione della stringa in int");
                    portToUse = Integer.parseInt(port);
                    Log.d(TAG, "Conversione della stringa in int "+portToUse);
                }catch (NumberFormatException e) {
                    Log.d(TAG, "Errore conversione stringa in int "+e);
                    portToUse =1234;
                }

                Log.d(TAG, "Inizializzo start");
                Message start = new Message(ip,portToUse,message);
                ///String result =start.toString();

                Log.d(TAG, "Eseguo AsyncTask");
                start.execute(message);
            }
        });
        // ########################################################################################

        // associo al button l'icona grigia di default di android di indiccatore off
        buttonConnect.setCompoundDrawablesWithIntrinsicBounds(0,0,0,button_onoff_indicator_off);

        // #####################   BUTTON CONNECT LISTENER   #######################


        buttonConnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //ISTANZIO L'oggetto handler di tipo WifiHandler tramite il costruttore senza argomenti
                WifiHandler handler = new WifiHandler(SSID);
                // verifico l'SSID delle wifi connessa, se c'è una connessione
                // questa si potrebbe ricavare anche dal broadcast receiver
                String wifiActive = handler.InfoWifiActive(getApplicationContext());
                Log.d(TAG1,"wifiActive.....= "+wifiActive);

                // ciclo if se il valore dell'SSID è quelo configurato nelle propietà e legato al target per il socket
                // si può collegare il socket, altrimenti è necessario connettersi alla wifi specifica per il socket

                // il punto ! significa not equal to
                if (!wifiActive.equals(SSID)) {
                    handler.wifiManager(getApplicationContext()).disconnect();
                    Log.d(TAG1, "Wifi Disconnected");

                    int netID = handler.specificNetwork(getApplicationContext());

                    handler.enableNetwork(getApplicationContext(), netID);
                    Log.d(TAG1, "Enabled Netwwork " + netID);

                    handler.wifiManager(getApplicationContext()).reconnect();
                    Log.d(TAG1, "Reconnected ");
                }

            }
        });
        // ########################################################################################



        // #####################   BUTTON DISCONNECT AND DISABLE LISTENER   #######################


        buttonDisconnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                WifiHandler handler = new WifiHandler(SSID);

                handler.wifiManager(getApplicationContext()).disconnect();

                int netID =handler.specificNetwork(getApplicationContext());

                handler.wifiManager(getApplicationContext()).disableNetwork(netID);
                Log.d(TAG1,"Disabled Network "+netID);

                handler.wifiManager(getApplicationContext()).reconnect();
            }
        });
        // ########################################################################################



        // #####################   BUTTON TEST LISTENER   #######################
        buttonTest.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                new Scan().execute();
    }
});
        // ########################################################################################

    }


    // ############### METODI DEL  BROADCAST RECEIVER ##############################################

    private BroadcastReceiver broadcastWifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            // Azione da eseguire quando il broadcast riceve l'intent in ingresso
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            String ssidNoQuotes = wifiInfo.getSSID().replace('"',' ');
            wlanSsid.setText(ssidNoQuotes);
            Log.d(TAG1,"ACTIONS NEL BROADCAST: "+ssid+"  SSID="+SSID);
            // associazione del tasto verde al pulsante connect se il valore della SSID
            // è uguale a quello impostato nelle preferenze
            // il pulsante connect ha il falg verde se  il cell è connesso alla wifi scelta

            if (ssid.equals("\""+SSID+"\"")){
                Log.d(TAG1,"CICLO IF NEL BROADCAST: "+ssid);
                buttonConnect .setCompoundDrawablesWithIntrinsicBounds(0,0,0,button_onoff_indicator_on);

                //**************************************
                // ATTIVA LA VIBRAZIONE AL CAMBIO RETE
               // Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //if (vibrator.hasVibrator()){
                //    vibrator.vibrate(100);
                //}
                // ************************************
            }else{
                Log.d(TAG1,"CICLO ELSE NEL BROADCAST: "+ssid);
                buttonConnect .setCompoundDrawablesWithIntrinsicBounds(0,0,0,button_onoff_indicator_off);
            }
        }
    };

    // Metodo per interrompere il Broadcast e la connessione alla wifi  quando l'app è in pausa
  /*  protected void onPause(){
        // sospende il broadcast
        unregisterReceiver(broadcastWifiReceiver);
        super.onPause();

        // disconnette dalla WIFI specifica se l'app viene messa in pausa

        WifiHandler handler = new WifiHandler(SSID);
        handler.wifiManager(getApplicationContext()).disconnect();
        int netID =handler.specificNetwork(getApplicationContext());
        handler.wifiManager(getApplicationContext()).disableNetwork(netID);
        Log.d(TAG1,"Disabled Network ON PAUSE "+netID);
        handler.wifiManager(getApplicationContext()).reconnect();
    }

    // Metodo per rirpistinare il Broadcast quando l'app viene ripristinata
    protected  void onResume(){
        // riattiva il broadcast
        registerReceiver(broadcastWifiReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();

        // Invia un messaggio Toast all'utene comunicando che sisogna effettuare la connessione
        // per poter operare sulla wi-fi specifica

        Toast.makeText(getApplicationContext(), "CONNECT TO SEND MESSAGE", Toast.LENGTH_SHORT).show();

    }*/
    // #############################################################################################

    // Questo metodo viene usato quando si passa dalla activity2 alla main activity per aggiornare quest'ultima
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


    // Metodo per effettuare delle scelte e quindi azioni al click delle voci del menu
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
            final Button send = (Button) findViewById(R.id.buttonSend);
            send.setEnabled(false);

            final TextView View = (TextView) findViewById(R.id.responseTextView);
            View.setText(null);
        }

        // metodo di Async Task
        protected String doInBackground(String... commandList) {
            String answerFromServer=".....";

            Log.d(TAG, "Metodo doInBackground-- Inizializzo socket Message");
            //SocketMessage socketMessage= new SocketMessage (ipAddress,serverPort,commandList[0]);
            SocketMessageBuffered socketMessage= new SocketMessageBuffered (ipAddress,serverPort,commandList[0]);
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
            final Button send = (Button) findViewById(R.id.buttonSend);
            send.setEnabled(true);
        }
    }

/* Classe Scan con metodo async task per la scsnaione degli AP
Questa classe viene istanziata durante la inizializzazione della app*/
    private class Scan extends AsyncTask<String, Void, String[]> {

        // il costruttore è sotinteso ed è automaticamente generato

        // metodo di Async Task
        protected String [] doInBackground(String... commandList) {
            String  list [] = null;
            Log.d(TAG, "Metodo doInBackground-- per la scansione della rete");

            try {
                WifiHandler wifiHandler = new WifiHandler();
                list = wifiHandler.scanList(getApplicationContext());
                Log.d(TAG4, "Lista scansione: "+list[0]);
            } catch (NullPointerException e) {
                list = null;
            }
            return list;
        }


        // metodo di Async Task
        // Ha come argomento l'output del do In Background
        protected void onPostExecute(String[] list) {
            final TextView View = (TextView) findViewById(R.id.textViewDebug);
            // la lista output di do in back ground è una lista di lunghezza non definita
            // verifico la lunghezza della lista
            String allList = "";
            String tempList [];
            int lenghtList= list.length;
            for (int i = 0; i < lenghtList; i++) {
                allList=allList.concat(list[i]+'\n');

                //Log.d(TAG1, "wifis[i] " + wifis[i]);
            }
            View.setText(allList);
        }
    }

/*    class ThreadRun implements Runnable {
        long minPrime;
        ThreadRun(long minPrime) {
            this.minPrime = minPrime;
        }

        public void run() {

        }
    }*/

}


