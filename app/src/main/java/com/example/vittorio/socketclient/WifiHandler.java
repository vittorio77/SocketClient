package com.example.vittorio.socketclient;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;


/**
 * Created by Vittorio on 20/12/2017.
 */

public class WifiHandler extends Application {
    String  TAG = "WifiHandler";
    String  TAG1 = "scan";
    String TAG2 = "network";

    String networkSSID;
    String networkPass;
    Context context;

    // primo costruttore con due argomenti in ingresso
    public WifiHandler(String networkSSID){
        this.networkSSID = networkSSID;
        Context context;
    }

    // secondo contruttore senza argomenti in ingresso
    public WifiHandler() {
        Context context;
    }

    // metodo per creare istanziare il  wifiManager
    protected WifiManager wifiManager(Context context) {
        WifiManager manager =null;
        try {
            manager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            Log.d(TAG, "Creato Wifi Manager");
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            Log.d(TAG, "catch field wifimanager");
        }
        return manager;
    }

    protected int specificNetwork(Context context){
        int netID=0;
        List<WifiConfiguration> list;

        list= this.wifiManager(context).getConfiguredNetworks();
        Log.d(TAG, "Lista reti configurate:"+list);
        for (WifiConfiguration wifiConfiguration : list){
            if (wifiConfiguration.SSID.equals("\""+networkSSID+"\"")){
                netID=wifiConfiguration.networkId;
            }
        }
        return netID;
    }

    //si abilita la rete con l'id NET id
    public void enableNetwork(Context context, int netID) {
        this.wifiManager(context).enableNetwork(netID,true);
    }

    public void  wifiEnableIfDisabled(Context context){
        WifiManager wifi = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        if(!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);
        }
    }
    public void  wifiDisable(Context context){
        WifiManager wifi = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        wifi.setWifiEnabled(false);
    }


    /**
     * Created by Vittorio on 11/09/2017.
     * Metodo per la verifica della connessione WIFI o Internet
     */
    public String InfoWifiActive(Context context) throws ArrayIndexOutOfBoundsException, NullPointerException {
        String result = null;
        try {
            ConnectivityManager connManager = (ConnectivityManager)  context.getSystemService(context.CONNECTIVITY_SERVICE);
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


    public String[] scanList(Context context) {
        Log.d(TAG1, "----  INIZIO SCANSIONE ----- ");
        WifiManager mainWifiObj = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        mainWifiObj.startScan();
        List<ScanResult> wifiScanlist = mainWifiObj.getScanResults();
        //Log.d(TAG1, "scan Result: " + wifiScanlist);
        // Dichiaro una lista di tipo stringa e di lungezza pari alla lunghezza della lista del risultato della scansione
        String wifis[] = new String[wifiScanlist.size()];
        for (int i = 0; i < wifiScanlist.size(); i++) {
            //Log.d(TAG1, "wifiScanlist.size()" + wifiScanlist.size());
            wifis[i] = ((wifiScanlist.get(i)).toString());
            //Log.d(TAG1, "wifis[i] " + wifis[i]);
        }
        String filtered[] = new String[wifiScanlist.size()];
        int counter = 0;
        for (String eachWifi : wifis) {
            String[] temp = eachWifi.split(",");
            filtered[counter] = temp[0].substring(5).trim();//per ottenere in nome del SSID
            Log.d(TAG1, "Nome SSID: " + filtered[counter]);
            counter++;
        }
        return filtered;
    }

    public boolean networkAvailabilityCheck(Context context,String wifiName){
        boolean result = false;
        this.scanList(context);
        String wifiList[] = this.scanList(context);
        for (int i = 0; i < wifiList.length; i++){
            Log.d(TAG1, "Nome SSID: " + wifiList[i]);
            String element = wifiList[i];
            if (element.equals(wifiName)){
                result=true;
            }
        }
        return result;
    }
}

