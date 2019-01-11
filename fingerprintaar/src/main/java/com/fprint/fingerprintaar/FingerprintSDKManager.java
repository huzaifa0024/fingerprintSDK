package com.fprint.fingerprintaar;

import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import com.voidlabs.phonesiminfolib.support.PhoneSimInfoManager;

import java.io.Serializable;

/**
 * Created by GlobalCharge on 27/12/2018.
 */
public class FingerprintSDKManager implements SecuritySettingsSelectedListener,Serializable {
    private Context mContext;

    private static final String TAG = "FPSDK";

    private FingerprintUiHelper mFingerprintUiHelper;
    private FingerprintCallBacks callBacks;
    private boolean bypassAuthProcess;


    private FingerprintSDKManager(FingerprintSDKManager.Builder builder) {
        this.mContext = builder.mContext;
        this.callBacks = builder.callBacks;
        this.bypassAuthProcess = builder.bypassAuthProcess;

    }

    private void initDefaultValues() {
        //Info manager initialization

    }


    public static class Builder {

        Context mContext;
        FingerprintCallBacks callBacks;
        boolean bypassAuthProcess;

        public Builder(Context context) {
            this.mContext = context;
        }


        public FingerprintSDKManager build() {
            FingerprintSDKManager manager = new FingerprintSDKManager(this);
            manager.initDefaultValues();
            return manager;
        }

        public FingerprintSDKManager.Builder setCallBacks(FingerprintCallBacks callback) {
            this.callBacks = callback;
            return this;
        }

        public FingerprintSDKManager.Builder setBypassSDK(boolean bypass) {
            this.bypassAuthProcess = bypass;
            return this;
        }

    }

    private boolean checkCompatability(){
        boolean osLowerThanM = Build.VERSION.SDK_INT < 23;
        if(osLowerThanM) {

            callBacks.osLessThanAndroidM();
            return false;
        }

        mFingerprintUiHelper = new FingerprintUiHelper(
                mContext.getSystemService(FingerprintManager.class),null,null,null);

        boolean hardWareNotAvailable = !mFingerprintUiHelper.isFingerprintHardwarevailable();

        if(hardWareNotAvailable) {

            callBacks.onHardWareNotAvailable();
            return false;
        }

        return true;
    }


    @Override
    public void openSecuritySettings(boolean open) {
        if(open){
            mContext.startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));
        }
    }

    public void startFingerprintAuthProcess()  {

        boolean compatable = checkCompatability();

        if(!compatable)
            return;

        if(bypassAuthProcess==true) {

            getCallBacks().onBypassTheFingerprintSDK();

        }else {

            Intent intent = new Intent(mContext, FingerPrintAvailableActivity.class);
            HolderClass.setManager(this);

            mContext.startActivity(intent);
        }

    }

    public FingerprintCallBacks getCallBacks(){
        return callBacks;
    }

}
