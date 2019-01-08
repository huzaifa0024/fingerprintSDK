package com.globalcharge.fingerprintaar;

/**
 * Created by GlobalCharge on 31/12/2018.
 */
class HolderClass {
    private static FingerprintSDKManager manager;

    public static FingerprintSDKManager getManager() {
        return manager;
    }

    public static void setManager(FingerprintSDKManager manager) {
        HolderClass.manager = manager;
    }
}
