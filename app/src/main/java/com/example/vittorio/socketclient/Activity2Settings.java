package com.example.vittorio.socketclient;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Objects;


public class Activity2Settings extends AppCompatActivity {
    // inizializzo la costante Activity per poi portarla come argomento nella classe PopUp generic
    private Activity activityReference;

    ListView listView;
    ArrayList<DataModel> dataModels;
    private static CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2settings);
        // assegno alla variabile activityreference l'attivita' attuale
        activityReference=this;

//Assegnare alla variabile istanziata di tipo ListView l'id di riferimento
        listView = (ListView) findViewById(R.id.listView1);
        //carico il valori riportati nella shared preferences

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        Object ipAddress = pref.getString("ipAddress",null);
        Object serverPort = pref.getString("serverPort",null);
        Object message = pref.getString("message",null);
        Object SSIDName = pref.getString("SSID",null);


        dataModels = new ArrayList<>();
        dataModels.add(new DataModel("IP Address/Server:",ipAddress+" ")); // ho aggiunto uno spazio per problemi di visualizzazione della text view
        dataModels.add(new DataModel("Server Port:",serverPort+" "));
        dataModels.add(new DataModel("Message:",message+" "));
        dataModels.add(new DataModel("Wifi SSID:",SSIDName+" "));


        adapter = new CustomAdapter(getApplicationContext(),dataModels);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                switch(position){

                    case 0:
                        PopUpGeneric popUpIp = new PopUpGeneric(activityReference,getApplicationContext(),
                                R.layout.popup_text,
                                R.id.popupText,
                                R.id.btn_Ok,
                                R.id.btn_Cancel,
                                "ipAddress");
                        popUpIp.show(getCurrentFocus(),0,0);
                        break;

                    case 1:
                        PopUpGeneric popUpServerPort = new PopUpGeneric(activityReference,getApplicationContext(),
                            R.layout.popup_text,
                            R.id.popupText,
                            R.id.btn_Ok,
                            R.id.btn_Cancel,
                            "serverPort");
                        popUpServerPort.show(getCurrentFocus(),0,0);
                        break;

                    case 2:
                        PopUpGeneric popUpMessage = new PopUpGeneric(activityReference,getApplicationContext(),
                                R.layout.popup_text,
                                R.id.popupText,
                                R.id.btn_Ok,
                                R.id.btn_Cancel,
                                "message");
                        popUpMessage.show(getCurrentFocus(),0,0);
                        break;

                    case 3:
                        PopUpGeneric popUpSSID = new PopUpGeneric(activityReference,getApplicationContext(),
                                R.layout.popup_text,
                                R.id.popupText,
                                R.id.btn_Ok,
                                R.id.btn_Cancel,
                                "SSID");
                        popUpSSID.show(getCurrentFocus(),0,0);
                        break;
                }
            }
        });


    }
}