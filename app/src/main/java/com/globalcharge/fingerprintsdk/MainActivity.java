package com.globalcharge.fingerprintsdk;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fprint.fingerprintaar.FingerprintCallBacks;
import com.fprint.fingerprintaar.FingerprintSDKManager;
import com.fprint.fingerprintaar.SuperActivity;

import java.util.ArrayList;
import java.util.Currency;


public class MainActivity extends AppCompatActivity implements FingerprintCallBacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FingerprintSDKManager manager = new FingerprintSDKManager.Builder(MainActivity.this).setProductData(getProductsRelatedDataForWebAuthPage()).setShowFpInsideActivity(true).setBypassSDK(false).setCallBacks(this).build();

       //when u wanna show the dialog
        manager.startFingerprintAuthProcess();

    }


    @Override
    public void onAuthenticatedWithFingerprintAndCryptObj(FingerprintManager.CryptoObject cryptoObject) {
        Log.d("fingerprint",cryptoObject.getCipher().getAlgorithm());
    }

    @Override
    public void onAuthenticatedWithPinCode() {
        Log.d("pincode","picopde auth");
    }

    @Override
    public void onError(String error) {
        Log.d("fingerprint error","error");
    }

    @Override
    public void onHardWareNotAvailable() {
        Log.d("hardware error","hardware not available");
    }

    @Override
    public void osLessThanAndroidM() {
        Log.d("OS related error","OS less than M");
    }

    @Override
    public void onAuthenticatedWithFingerprintWithoutCryptObj() {
        Log.d("fingerprint","without cryptobject");
    }

    @Override
    public void onBypassTheFingerprintSDK() {
        Log.d("fingerprint","bypassed");
    }

    @Override
    public void onCancelled() {
            Log.d("fingerprint","cancelled");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234) {
            if (resultCode == RESULT_OK) {
                //do something you want when pass the security
                Log.d("auth successful","pin auth successful");
            }else{
                Toast.makeText(this, "Incorrect pincode entered", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList<String> getProductsRelatedDataForWebAuthPage(){



        ArrayList<String> productExtraData = new ArrayList<>();

        productExtraData.add("Badoo Premium");
        productExtraData.add("Social Online Payment Ltd.");
        String productLabel = "test product";

        productExtraData.add(productLabel);
        productExtraData.add("Warning Text"+"----"+"http://eu1.badoo.com/terms/");
        productExtraData.add("Vilkår og betingelser (betalinger)"+"----"+"http://eu1.badoo.com/terms/");
        productExtraData.add("Help | "+"----"+"kontakt@message-mobile.de");


        return productExtraData;

    }
}
