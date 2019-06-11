package com.fprint.fingerprintaar;

import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Syed Huzaifa on 27/12/2018.
 */
public class FingerprintSDKManager implements SecuritySettingsSelectedListener,Serializable {
    private Context mContext;

    private static final String TAG = "FPSDK";

    private FingerprintUiHelper mFingerprintUiHelper;
    private FingerprintCallBacks callBacks;
    private boolean bypassAuthProcess;
    private ArrayList<String> productData;


    private FingerprintSDKManager(FingerprintSDKManager.Builder builder) {
        this.mContext = builder.mContext;
        this.callBacks = builder.callBacks;
        this.bypassAuthProcess = builder.bypassAuthProcess;
        this.productData = builder.productData;

    }

    private void initDefaultValues() {
        //Info manager initialization

    }


    public static class Builder {

        Context mContext;
        FingerprintCallBacks callBacks;
        boolean bypassAuthProcess;
        ArrayList<String> productData;

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
        public FingerprintSDKManager.Builder setProductData(ArrayList<String> productData) {
            this.productData = productData;
            return this;
        }

    }

    private boolean checkCompatability(){

        try {

            boolean osLowerThanM = Build.VERSION.SDK_INT < 23;
            if (osLowerThanM) {

                callBacks.osLessThanAndroidM();
                return false;
            }

            mFingerprintUiHelper = new FingerprintUiHelper(
                    mContext.getSystemService(FingerprintManager.class), null, null, null);


            boolean hardWareNotAvailable = !mFingerprintUiHelper.isFingerprintHardwarevailable();

            if (hardWareNotAvailable) {

                callBacks.onHardWareNotAvailable();
                return false;
            }

            return true;

        }catch (Exception ex){
            return false;
        }
    }


    @Override
    public void openSecuritySettings(boolean open) {
        if(open){
            mContext.startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));
        }
    }

    public void startFingerprintAuthProcess()  {

        try {


            boolean compatable = checkCompatability();

            if (!compatable)
                return;

            if (bypassAuthProcess == true) {

                getCallBacks().onBypassTheFingerprintSDK();

            } else {

                //need to check if the requirement is to show the full screen along with text coming from server i-e terms and conditions,title and contact info etc
                //in that case we need to show full screen activity with fingerprint dialog's code inside the activity because the fingerprinnt dialog won't allow us to
                //click the links coming from server and full screen dialogs don't match the screen design requested by the Denmark Team
                //10th June 2019*****Added by Syed*****

                Intent intent = null;

                if(true){
                    intent = new Intent(mContext, FingerPrintAvailableActivityWithoutDialog.class);
                    intent.putStringArrayListExtra(Constants.PRODUCT_DATA,productData);

                }

                else {
                    intent = new Intent(mContext, FingerPrintAvailableActivity.class);
                }

                HolderClass.setManager(this);

                mContext.startActivity(intent);
            }
        }catch (Exception e){
            getCallBacks().onError(e.getMessage());
        }

    }

    public FingerprintCallBacks getCallBacks(){
        return callBacks;
    }

}
