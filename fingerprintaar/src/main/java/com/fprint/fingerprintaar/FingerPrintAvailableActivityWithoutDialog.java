package com.fprint.fingerprintaar;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
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
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FingerPrintAvailableActivityWithoutDialog extends SuperActivity implements SecuritySettingsSelectedListener, TextView.OnEditorActionListener,FingerprintUiHelper.Callback {
    Cipher defaultCipher;
    public static int REQUEST_CODE = 1234;

    private static final String TAG = FingerPrintAvailableActivityWithoutDialog.class.getSimpleName();

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    static final String DEFAULT_KEY_NAME = "default_key";

    final Handler handler = new Handler();


    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
  //  private SharedPreferences mSharedPreferences;
  //  private FingerprintUiHelper mFingerprintUiHelper;

    private FingerprintSDKManager manager;
    TextView mainText,secondaryText,amountToPay,warningText,helpText,termsAndConditions;
    ScrollView mainContainer;
    int colorYellow;

    //FingerprintDialogFragment Code

    private Button mCancelButton;
    private Button mSecondDialogButton;
    private View mFingerprintContent;
    private View mBackupContent;
    private EditText mPassword;
    private CheckBox mUseFingerprintFutureCheckBox;
    private TextView mPasswordDescriptionTextView;
    private TextView mNewFingerprintEnrolledTextView;
    private FingerprintCallBacks fingerprintCallBack;
    private FingerprintSDKManager fpSDKManager;

    private Stages mStage = Stages.FINGERPRINT;

    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;

    private KeyguardManager km;
    private InputMethodManager mInputMethodManager;
    private SharedPreferences mSharedPreferences;

    private ArrayList<String> productData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_available_without_dialog);

        manager = HolderClass.getManager();

        Bundle extras = getIntent().getExtras();

        if (extras.containsKey(Constants.PRODUCT_DATA)) {
            productData = (ArrayList<String>) extras.getSerializable(Constants.PRODUCT_DATA);
        }


    }

    private void initViews(){

        colorYellow = Color.parseColor("#f5d36a");

        setStatusBarColor("#2f2f2f");

        mainText = (TextView) findViewById(R.id.screen_main_text);
        secondaryText = (TextView) findViewById(R.id.screen_main_secondary_text);
        amountToPay = (TextView) findViewById(R.id.amount_to_pay);
        warningText = (TextView) findViewById(R.id.warning_text);
        termsAndConditions = (TextView) findViewById(R.id.terms_and_conditions);
        helpText = (TextView) findViewById(R.id.help_text);

        if(null!=productData && productData.size()>0) {
            mainText.setText(productData.get(0));
            secondaryText.setText(productData.get(1));
            amountToPay.setText(productData.get(2));
        //    warningText.setText(productData.get(3));
        //    termsAndConditions.setText(productData.get(4));
        //    helpText.setText(productData.get(5));
        }

        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=productData.get(4)
                        && productData.get(4).length()>0
                        && (productData.get(4).contains("http")
                        || productData.get(4).contains("https")))
                {
                    termsAndConditions.setMovementMethod(LinkMovementMethod.getInstance());
                    termsAndConditions.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setData(Uri.parse(productData.get(4)));
                            startActivity(browserIntent);
                        }
                    });
                }
                else if(null!=productData.get(4)
                        && productData.get(4).length()>0) {

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto",productData.get(4), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }

            }
        });


        warningText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=productData.get(3)
                        && productData.get(3).length()>0
                        && (productData.get(3).contains("http")
                        || productData.get(3).contains("https")))
                {
                    warningText.setMovementMethod(LinkMovementMethod.getInstance());
                    warningText.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setData(Uri.parse(productData.get(4)));
                            startActivity(browserIntent);
                        }
                    });
                }
                else if(null!=productData.get(3)
                        && productData.get(3).length()>0) {

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto",productData.get(3), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }

            }
        });
        helpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=productData.get(5)
                        && productData.get(4).length()>0
                        && (productData.get(4).contains("http")
                        || productData.get(4).contains("https")))
                {
                    helpText.setMovementMethod(LinkMovementMethod.getInstance());
                    helpText.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setData(Uri.parse(productData.get(5)));
                            startActivity(browserIntent);
                        }
                    });
                }
                else if(null!=productData.get(5)
                        && productData.get(5).length()>0) {

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto",productData.get(5), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }

            }
        });



    }

    private void initDialogFingerprintCode(){
        //Adding fingerfragmentDialogs data here
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fingerprintCallBack.onCancelled();

            }
        });

        mSecondDialogButton = (Button) findViewById(R.id.second_dialog_button);
        mSecondDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStage == Stages.FINGERPRINT) {
                    goToBackup();
                } else {
                    verifyPassword();
                }
            }
        });
        mFingerprintContent = findViewById(R.id.fingerprint_container);
        mBackupContent = findViewById(R.id.backup_container);
        mPassword = (EditText) findViewById(R.id.password);
        mPassword.setOnEditorActionListener(this);
        mPasswordDescriptionTextView = (TextView) findViewById(R.id.password_description);
        mUseFingerprintFutureCheckBox = (CheckBox)
                findViewById(R.id.use_fingerprint_in_future_check);
        mNewFingerprintEnrolledTextView = (TextView)
                findViewById(R.id.new_fingerprint_enrolled_description);
        mFingerprintUiHelper = new FingerprintUiHelper(
                this.getSystemService(FingerprintManager.class),
                (ImageView) findViewById(R.id.fingerprint_icon),
                (TextView) findViewById(R.id.fingerprint_status), this);
        updateStage();

        // If fingerprint authentication is not available, switch immediately to the backup
        // (password) screen.
        if (!mFingerprintUiHelper.isFingerprintAuthAvailable()) {
            goToBackup();
        }
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
                FingerPrintAvailableActivityWithoutDialog.this.finish();
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
            FingerPrintAvailableActivityWithoutDialog.this.finish();
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

        initDialogFingerprintCode();

        if (initCipher(mCipher, mKeyName)) {

            // Show the fingerprint dialog. The user has the option to use the fingerprint with
            // crypto, or you can fall back to using a server-side verified password.
            boolean useFingerprintPreference = mSharedPreferences
                    .getBoolean(this.getString(R.string.use_fingerprint_to_authenticate_key),
                            true);
            if (useFingerprintPreference) {
                mStage = Stages.FINGERPRINT;
            } else {
                mStage = Stages.PASSWORD;
            }

        } else {
            // This happens if the lock screen has been disabled or or a fingerprint got
            // enrolled. Thus show the dialog to authenticate with their password first
            // and ask the user if they want to authenticate with fingerprints in the
            // future

            mStage = Stages.NEW_FINGERPRINT_ENROLLED;



        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        initViews();

        initFingerPrint();

        if (null!= mFingerprintUiHelper && mStage == Stages.FINGERPRINT) {
            mFingerprintUiHelper.startListening(mCryptoObject);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(null!= mFingerprintUiHelper)
            mFingerprintUiHelper.stopListening();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        manager.getCallBacks().onCancelled();
    }

    @Override
    public void onAuthenticated() {
        // Callback from FingerprintUiHelper. Let the activity know that authentication was
        // successful.
        onPurchased(true /* withFingerprint */, mCryptoObject);

    }

    @Override
    public void onError() {
        goToBackup();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            verifyPassword();
            return true;
        }
        return false;
    }

    /**
     * Switches to backup (password) screen. This either can happen when fingerprint is not
     * available or the user chooses to use the password authentication method by pressing the
     * button. This can also happen when the user had too many fingerprint attempts.
     */
    private void goToBackup() {
        mStage = Stages.PASSWORD;
        updateStage();
        mPassword.requestFocus();

        // Show the keyboard.
        //     mPassword.postDelayed(mShowKeyboardRunnable, 500);

        // Fingerprint is not used anymore. Stop listening for it.
        mFingerprintUiHelper.stopListening();

    }

    /**
     * Checks whether the current entered password is correct, and dismisses the the dialog and
     * let's the activity know about the result.
     */
    private void verifyPassword() {
        if (!checkPassword(mPassword.getText().toString())) {
            return;
        }
        if (mStage == Stages.NEW_FINGERPRINT_ENROLLED) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                    mUseFingerprintFutureCheckBox.isChecked());
            editor.apply();

            if (mUseFingerprintFutureCheckBox.isChecked()) {
                // Re-create the key so that fingerprints including new ones are validated.

                createKey(DEFAULT_KEY_NAME, true);
                mStage = Stages.FINGERPRINT;
            }
        }
        mPassword.setText("");
        onPurchased(false /* withFingerprint */, null);

    }

    /**
     * @return true if {@code password} is correct, false otherwise
     */
    private boolean checkPassword(String password) {
        // Assume the password is always correct.
        // In the real world situation, the password needs to be verified in the server side.
        return password.length() > 0;
    }

    private final Runnable mShowKeyboardRunnable = new Runnable() {
        @Override
        public void run() {
            mInputMethodManager.showSoftInput(mPassword, 0);
        }
    };

    private void updateStage() {
        switch (mStage) {
            case FINGERPRINT:
                mCancelButton.setText(R.string.cancel);
                mSecondDialogButton.setText(R.string.use_pincode);
                mFingerprintContent.setVisibility(View.VISIBLE);
                mBackupContent.setVisibility(View.GONE);
                break;
            case NEW_FINGERPRINT_ENROLLED:
                // Intentional fall through
            case PASSWORD:

                km = (KeyguardManager) this.getSystemService(this.KEYGUARD_SERVICE);
                if(km.isKeyguardSecure()) {
                    Intent i = km.createConfirmDeviceCredentialIntent(null, null);
                    this.startActivityForResult(i, FingerPrintAvailableActivity.REQUEST_CODE);
                }
                break;
        }
    }

    public enum Stages {
        FINGERPRINT,
        NEW_FINGERPRINT_ENROLLED,
        PASSWORD
    }
}
