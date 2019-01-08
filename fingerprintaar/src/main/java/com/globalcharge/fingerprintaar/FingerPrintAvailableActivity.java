package com.globalcharge.fingerprintaar;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FingerPrintAvailableActivity extends SuperActivity implements SecuritySettingsSelectedListener {
    Cipher defaultCipher;
    public static int REQUEST_CODE = 1234;

    private static final String TAG = FingerPrintAvailableActivity.class.getSimpleName();

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    static final String DEFAULT_KEY_NAME = "default_key";

    final Handler handler = new Handler();


    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private SharedPreferences mSharedPreferences;
    private FingerprintUiHelper mFingerprintUiHelper;

    private FingerprintSDKManager manager;
    int colorYellow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_available);

        manager = HolderClass.getManager();

    }

    private void initViews(){

        colorYellow = Color.parseColor("#f5d36a");

        setStatusBarColor("#2f2f2f");

    }


    private void initFingerPrint(){
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            //    throw new RuntimeException("Failed to get an instance of KeyStore", e);
            manager.getCallBacks().onError(e.getLocalizedMessage());
            return;
        }
        try {

            mKeyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            //  throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
            manager.getCallBacks().onError(e.getLocalizedMessage());
            return;
        }

        Cipher cipherNotInvalidated;
        try {
            defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipherNotInvalidated = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
           manager.getCallBacks().onError("Failed to get cipher");
           return;
        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        KeyguardManager keyguardManager = getSystemService(KeyguardManager.class);
        FingerprintManager fingerprintManager = getSystemService(FingerprintManager.class);


        if (!keyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a fingerprint or lock screen.
            setupSecureLockScreen();
            return;
        }

         if (!fingerprintManager.hasEnrolledFingerprints()) {
             setUpAtleastOneFingerprint();
             return;
        }
        createKey(DEFAULT_KEY_NAME, true);
        createKey(KEY_NAME_NOT_INVALIDATED, true);

        startFingerprintAuthProcess();
    }

    public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    // Require the user to authenticate with a fingerprint to authorize every use
                    // of the key
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
            }
            if(mKeyGenerator!=null) {
                mKeyGenerator.init(builder.build());
                mKeyGenerator.generateKey();
            }
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            manager.getCallBacks().onError(e.getLocalizedMessage());
            return;

        }
    }

    public void onPurchased(boolean withFingerprint,
                            @Nullable FingerprintManager.CryptoObject cryptoObject) {
        if (withFingerprint) {
            // If the user has authenticated with fingerprint, verify that using cryptography and
            // then show the confirmation message.
            assert cryptoObject != null;
            tryEncrypt(cryptoObject);
        } else {
            // Authentication happened with backup password. Just show the confirmation message.
            showConfirmation(null,null);
        }
    }

    // Show confirmation, if fingerprint was used show crypto information.
    private void showConfirmation(final FingerprintManager.CryptoObject cryptoObject, final byte[] encrypted) {

        hideProgress();
        if(null==cryptoObject) {

            manager.getCallBacks().onAuthenticatedWithFingerprintWithoutCryptObj();
        }
        else {

            manager.getCallBacks().onAuthenticatedWithFingerprintAndCryptObj(cryptoObject);
        }


        finish();

    }

    private void finishActivity(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FingerPrintAvailableActivity.this.finish();
            }
        });
    }

    /**
     * Tries to encrypt some data with the generated key in {@link #createKey} which is
     * only works if the user has just authenticated via fingerprint.
     */
    private void tryEncrypt(FingerprintManager.CryptoObject cryptoObject) {
        try {
            byte[] encrypted = cryptoObject.getCipher().doFinal(SECRET_MESSAGE.getBytes());
            showConfirmation(cryptoObject,encrypted);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            Toast.makeText(this, "Failed to encrypt the data with the generated key. "
                    + "Retry the purchase", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
            manager.getCallBacks().onError( e.getMessage());
            FingerPrintAvailableActivity.this.finish();
        }
    }

    private boolean initCipher(Cipher cipher, String keyName) {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            // throw new RuntimeException("Failed to init Cipher", e);
            return false;

        }
    }





    @Override
    public void openSecuritySettings(boolean open) {
        if(open){
            startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));
        }
    }

    public void startFingerprintAuthProcess()  {

        Cipher mCipher = defaultCipher;
        String mKeyName = DEFAULT_KEY_NAME;;



        // Set up the crypto object for later. The object will be authenticated by use
        // of the fingerprint.
        if (initCipher(mCipher, mKeyName)) {

            // Show the fingerprint dialog. The user has the option to use the fingerprint with
            // crypto, or you can fall back to using a server-side verified password.
            FingerprintAuthenticationDialogFragment fragment
                    = new FingerprintAuthenticationDialogFragment();
            fragment.setCryptoObject(new android.hardware.fingerprint.FingerprintManager.CryptoObject(mCipher));
            fragment.setCallbackObject(manager.getCallBacks());
            fragment.setThisObject(manager);
            boolean useFingerprintPreference = mSharedPreferences
                    .getBoolean(this.getString(R.string.use_fingerprint_to_authenticate_key),
                            true);
            if (useFingerprintPreference) {
                fragment.setStage(
                        FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
            } else {
                fragment.setStage(
                        FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
            }
            fragment.show(this.getFragmentManager(), DIALOG_FRAGMENT_TAG);
            fragment.setCancelable(false);
        } else {
            // This happens if the lock screen has been disabled or or a fingerprint got
            // enrolled. Thus show the dialog to authenticate with their password first
            // and ask the user if they want to authenticate with fingerprints in the
            // future
            FingerprintAuthenticationDialogFragment fragment
                    = new FingerprintAuthenticationDialogFragment();
            fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
            fragment.setStage(
                    FingerprintAuthenticationDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);
            fragment.show(this.getFragmentManager(), DIALOG_FRAGMENT_TAG);


        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        initViews();

        initFingerPrint();

    }

    View.OnClickListener cancelOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


    View.OnClickListener setUpLockScreenOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setupSecureLockScreen();
        }
    };

    View.OnClickListener setUpOnePrintOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setUpAtleastOneFingerprint();
        }
    };


    private void setupSecureLockScreen(){

        AlertHelper.showSecuritySettings(this,"Secure lock screen hasn't set up.\n"
                + "Do you want to go to 'Settings -> Security -> Enable secure lock screen",this);

    }

    private void setUpAtleastOneFingerprint(){
        AlertHelper.showSecuritySettings(this,"You need to goto 'Settings -> Security -> Fingerprint' and register at least one" +
                " fingerprint",this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //do something you want when pass the security
                manager.getCallBacks().onAuthenticatedWithPinCode();
                finish();
            }else{
                Toast.makeText(this, "Incorrect pincode entered", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
