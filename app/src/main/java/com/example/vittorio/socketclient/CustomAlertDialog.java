package com.example.vittorio.socketclient;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class CustomAlertDialog extends Activity{
    Activity activity;
    String titleText;
    String bodyText;

    public CustomAlertDialog(Activity activity, String titleText, String bodyText){
        this.activity=activity;
        this.titleText=titleText;
        this.bodyText=bodyText;

        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setCancelable(false);
        dialog.setTitle(titleText);
        dialog.setMessage(bodyText);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Action for "Delete".
            }
        });
 //               .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
 //                   @Override
 //                   public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".
 //                   }
 //               });

        final AlertDialog alert = dialog.create();
        alert.show();
    }
}
