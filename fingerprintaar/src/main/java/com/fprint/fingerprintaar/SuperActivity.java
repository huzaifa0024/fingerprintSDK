package com.fprint.fingerprintaar;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by GlobalCharge on 25/10/2018.
 */
public class SuperActivity extends AppCompatActivity {

    public ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStatusBarColor(null);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        progressDialog = new ProgressDialog(this);

    }

    public final void showProgress() {
        showProgress("Please wait...");
    }

    public void showProgress(String message) {
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgress() {
        if (null != progressDialog) {
            progressDialog.cancel();
        }
    }

    public void updateProgressMessage(String message) {
        progressDialog.setMessage(message);
    }

    public void setStatusBarColor(String clr){

        String color = clr==null?"#f7c947":clr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           /* Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);*/
            try {

                ViewUtils.setStatusBar(this, Color.parseColor(color));
            }catch (Exception e){

            }
        }
    }

}
