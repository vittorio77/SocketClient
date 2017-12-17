package com.example.vittorio.socketclient;

/**
 * Created by Vittorio on 11/09/2017.
 * Classe per la gestione dei valori delle stringhe delle righe del list item
 */

public class DataModel {
    private String intestazione;
    private String valore;

    public DataModel(String lineaTop, String lineaBottom){
        this.intestazione=lineaTop;
        this.valore=lineaBottom;
    }

    public String getIntestazione(){
        return intestazione;
    }

    public String getValore()
    {
        return valore;
    }
}
