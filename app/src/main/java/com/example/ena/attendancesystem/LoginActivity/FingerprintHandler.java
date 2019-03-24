package com.example.ena.attendancesystem.LoginActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ena.attendancesystem.PhotoActivity;
import com.example.ena.attendancesystem.R;

import static android.content.Context.MODE_PRIVATE;


@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {


    private Context context;
    private ProgressDialog progressDialog;


    public FingerprintHandler(Context mContext) {
        context = mContext;
    }


    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update("Fingerprint Authentication error \nSwipe Down To Refresh And Login\n" + errString, false);
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update("Fingerprint Authentication help\n" + helpString, false);
    }


    @Override
    public void onAuthenticationFailed() {
        this.update("Fingerprint Authentication failed.", false);
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("Fingerprint Authentication successfull...", true);
        final Dialog dialog = new Dialog(context);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                context.startActivity(new Intent(context,
                        PhotoActivity.class));
                Toast.makeText(context, "Login Successfull !!!", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 2000);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Logging In... PleaseWait!!!");
        progressDialog.show();

    }


    public void update(String e, Boolean success){
        TextView textView = (TextView) ((Activity)context).findViewById(R.id.errorText);
        textView.setText(e);
        if(success){
            textView.setTextColor(ContextCompat.getColor(context,R.color.colorErrorText));

        }
    }
}
