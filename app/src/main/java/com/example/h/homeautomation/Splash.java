package com.example.h.homeautomation;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Splash extends AppCompatActivity {
    Context context = this;
    private static final int REQUEST_CODE_PERMISSIONS = 1240;
    private static final int SPLASH_TIMEOUT = 3000;
    String [] mPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.INTERNET
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkAndRequestPermissions()) {
                    startActivity(new Intent(Splash.this, HomeActivity.class));
                    finish();
                }
            }
        }, SPLASH_TIMEOUT);

    }

    public boolean checkAndRequestPermissions () {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : mPermissions) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(perm);
            }
        }

        //ask for non granted permissions
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(Splash.this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_CODE_PERMISSIONS);
            return false;
        }
        //app has all permissions, proceed ahead
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e("Req Code", "" + requestCode);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            HashMap<String, Integer> permissionsResult = new HashMap<>();
            int deniedCount = 0;

            //Gather permissions grant results
            for (int i=0; i<grantResults.length; i++) {

                //Add only permissions which are denied
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionsResult.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

            //check if all permissions are granted
            if (deniedCount == 0) {
                //proceed ahead with the app
                startActivity(new Intent(Splash.this, HomeActivity.class));
                finish();
            }else {
                for (Map.Entry<String, Integer> entry : permissionsResult.entrySet()) {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();

                    //permission is denied (this is the first time , when "never ask again" is not checked)
                    //so ask again, explaining the usage of permissions
                    //shouldShowRequestPermissionRationale will return true
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Splash.this, permName)) {
                        //show dialog of explanation
                        showDialog("", "This app needs the following permissions to work without problem",
                                "Yes, Grant permissions",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick (DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        checkAndRequestPermissions();
                                    }
                                } , "No, Exit App", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                },false);
                    }
                    //permission is denied and "never ask again" is Checked
                    //shouldShowRequestPermissionRationale will return false
                    else {
                        //Ask the user to go to settings and manually allow permissions
                        showDialog("", "You have denied some permissions. Allow all permissions at " +
                                "[Settings] > [Permissions]", "Go to Settings", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick (DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                //Intent to go to app settings
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", Splash.this.getPackageName(), null);
                                intent.setData(uri);
                                context.startActivity(intent);
                                //startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                            }
                        },"No, Exit App", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick (DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        }, false);
                        break;

                    }
                }
            }
        }

    }


    public AlertDialog showDialog(String title, String msg, String positiveLabel,
                                  DialogInterface.OnClickListener positiveOnClick, String negativeLabel,
                                  DialogInterface.OnClickListener negativeOnClick, boolean isCancelAble) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(isCancelAble);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveOnClick);
        builder.setNegativeButton(negativeLabel, negativeOnClick);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return alertDialog;
    }


}
