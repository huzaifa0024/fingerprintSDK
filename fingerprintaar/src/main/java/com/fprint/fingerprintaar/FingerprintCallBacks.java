package com.fprint.fingerprintaar;

import android.hardware.fingerprint.FingerprintManager;

/**
 * Created by GlobalCharge on 27/12/2018.
 */
public interface FingerprintCallBacks {

    void onAuthenticatedWithFingerprintAndCryptObj(FingerprintManager.CryptoObject cryptoObject);

    void onAuthenticatedWithPinCode();

    void onError(String error);

    void onHardWareNotAvailable();

    void osLessThanAndroidM();

    void onAuthenticatedWithFingerprintWithoutCryptObj();

    void onBypassTheFingerprintSDK();

    void onCancelled();

    void onTimeOut();

}
