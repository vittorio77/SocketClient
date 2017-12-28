package com.example.vittorio.socketclient;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

public class PopUpGeneric extends PopupWindow{
    private Context ctx;
    private Button btnConfirm;
    private Button btnCancel;
    private TextView editText;
    private View popupView;
    private int resourceLayout;
    private int resourceText;
    private int resourceOkButton;
    private int resourceCancelButton;
    private final String editTextType;
    private final Activity activity;

    // inizio costruttore della classe
    public PopUpGeneric(final Activity activity, Context context, int resourceLayout, int resourceText, int resourceOkButton, int resourceCancelButton, final String editTextType)
    {
        //invoca un costruttore della classe PopupWindow
        super(context);
        this.activity=activity;
        this.resourceLayout = resourceLayout;
        this.resourceText=resourceText;
        this.resourceOkButton = resourceOkButton;
        this.resourceCancelButton = resourceCancelButton;
        this.editTextType = editTextType;

        ctx = context;
        popupView = LayoutInflater.from(context).inflate(resourceLayout, null);

        // recupero il colore dalle risorse xml colori
        int colorPopUp = ContextCompat.getColor(context,R.color.Gray);
        // imposto il colore
        popupView.setBackgroundColor(colorPopUp);
        setContentView(popupView);


        btnConfirm = (Button)popupView.findViewById(resourceOkButton);
        btnCancel = (Button)popupView.findViewById(resourceCancelButton);

        SharedPreferences pref = ctx.getSharedPreferences("MyPref", MODE_PRIVATE);
        editText = (TextView)popupView.findViewById(resourceText);

        String value = pref.getString(editTextType,null);
        editText.setText(value);

        View root = getContentView();
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        // Closes the popup_text window when touch outside of it - when looses focus
        setOutsideTouchable(true);
        // get the focus
        setFocusable(true);

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                // add instruction in case of click outside of the popup_text
                //CustomAlertDialog dialog = new CustomAlertDialog(activity,"","Non è stato modificato nulla");
            }
        });

        btnConfirm.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Inizializzo sharedPreferences --
                SharedPreferences pref = ctx.getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(editTextType, editText.getText().toString());
                editor.commit();

                dismiss();
                //Update activity per aggiornare i valori appena inseriti
                activity.recreate();

            }});
        btnCancel.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }});

    } // End constructor

    // Attaches the view to its parent anchor-view at position x and y
    public void show(View anchor, int x, int y)
    {
        showAtLocation(anchor, Gravity.CENTER, x, y);
    }


}


//Toast toast1 = Toast.makeText(ctx, passwordSaved,Toast.LENGTH_LONG);
//toast1.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
//toast1.show();