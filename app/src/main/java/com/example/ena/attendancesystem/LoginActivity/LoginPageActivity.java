package com.example.ena.attendancesystem.LoginActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ena.attendancesystem.Misc.Constant;
import com.example.ena.attendancesystem.PhotoActivity;
import com.example.ena.attendancesystem.R;
import com.example.ena.attendancesystem.Misc.RequestHandler;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginPageActivity extends AppCompatActivity implements TextWatcher, CompoundButton.OnCheckedChangeListener {

    private EditText editTextUser;
    private EditText editTextPassword;
    private Button Login_Button;
    private TextView Forgot_Password;
    private TextView textViewSignUp;
    private ProgressDialog progressDialog;
    private ImageView targetImage;
    private Switch switchManagePrefs;

    private Uri mCropImageUri;
    SharedPreferences sharedPreferences;



    public static final String MyPREFERENCES = "MyPref";
    public static final String usrname = "USERNAME";
    public static final String passwd = "PASSWORD";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    private static final String KEY_REMEMBER = "remember";


    boolean doubleBackToExitPressedOnce = false;

    Animation slideUpAnimation, slideDownAnimation;
    LinearLayout linearLayout,linearLayoutOne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login_page);

        editTextUser = (EditText) findViewById(R.id.editTextUser);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        Login_Button = (Button) findViewById(R.id.btnLogin);
      //  Forgot_Password = (TextView) findViewById(R.id.textViewForgotPassword);
        textViewSignUp = (TextView)findViewById(R.id.textViewSignUp);
        progressDialog = new ProgressDialog(LoginPageActivity.this);
        switchManagePrefs = (Switch)findViewById(R.id.switchManagePrefs);

        targetImage = (ImageView)findViewById(R.id.targetImage);

        sharedPreferences = getSharedPreferences(getString(R.string.app_name) + "_ProfileDetails", MODE_PRIVATE);
        final String profilePicUri = sharedPreferences.getString("profilePicUrl", "");

        Bitmap bitmap = null;
        if(bitmap == null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(profilePicUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // set bitmap to imageview
            targetImage.setImageBitmap(bitmap);
        }

        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        linearLayoutOne = (LinearLayout)findViewById(R.id.linearLayoutOne);

        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up_animation);

        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_animation);

        linearLayout.startAnimation(slideUpAnimation);
        linearLayout.setVisibility(View.VISIBLE);
        linearLayoutOne.startAnimation(slideDownAnimation);
        linearLayoutOne.setVisibility(View.VISIBLE);

        switchManagePrefs.setChecked(true);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        if(sharedpreferences.getBoolean(KEY_REMEMBER, false))
            switchManagePrefs.setChecked(true);

        String username = sharedpreferences.getString(usrname, null);
        editTextUser.setText(username);
        String password = sharedpreferences.getString(passwd, null);
        editTextPassword.setText(password);

        editTextUser.addTextChangedListener(this);
        editTextPassword.addTextChangedListener(this);
        switchManagePrefs.setOnCheckedChangeListener(this);

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String username = editTextUser.getText().toString().trim();
                final String Password = editTextPassword.getText().toString().trim();



                if (!isValidUsername(username)) {
                    editTextUser.setError(" username Field cannot be blank!!!");
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                } else if (!isValidPassword(Password)) {
                    editTextPassword.setError("password Field cannot be blank!!!");
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);

                } else {

                    textViewSignUp.setEnabled(false);
                    progressDialog.setMessage("Registering Your Data... Please Wait...");
                    progressDialog.show();


                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            Constant.URL_SIGNUP,
                            new Response.Listener<String>() {

                                @Override
                                public void onResponse(String response) {


                                    try {
                                        //Toast.makeText(getApplicationContext(),"JASON :",Toast.LENGTH_LONG).show();
                                        JSONObject jsonObject = new JSONObject(response);

                                        String code = jsonObject.getString("code");
                                        String message = jsonObject.getString("message");
                                        if (code.equals("success")) {


                                            progressDialog.dismiss();
                                            textViewSignUp.setEnabled(true);


                                        } else if (code.equals("failed")) {

                                            Toast.makeText(LoginPageActivity.this, response.toString(), Toast.LENGTH_LONG).show();

                                            Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            v1.vibrate(100);
                                            progressDialog.dismiss();
                                            textViewSignUp.setEnabled(true);
                                        }

                                        //Toast.makeText(getApplicationContext(), "Respnse :" + response, Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Toast.makeText(LoginPageActivity.this, "Database Connectivity Error!!! Check Your Network Connection And Try Again...", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    textViewSignUp.setEnabled(true);
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();

                            params.put("username", username);
                            params.put("Password", Password);
                            return params;
                        }
                    };


                    RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                }

            }
        });


        Login_Button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            final String username = editTextUser.getText().toString().trim();
            final String Password = editTextPassword.getText().toString().trim();


            if (!isValidUsername(username) ) {
                editTextUser.setError("username Field cannot be blank!!!");
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
            }

                else if(!isValidPassword(Password)){
                    editTextPassword.setError("password Field cannot be blank!!!");
                    Vibrator vibrator1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator1.vibrate(100);

                }

             else {


                Login_Button.setEnabled(false);
                progressDialog.setMessage("Logging In... Please Wait...");
                progressDialog.show();



                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Constant.URL_LOGIN1,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {


                                try {
                                    //Toast.makeText(getApplicationContext(),"JASON :",Toast.LENGTH_LONG).show();
                                    JSONObject jsonObject = new JSONObject(response);

                                    String code = jsonObject.getString("code");
                                    String message = jsonObject.getString("message");
                                    if (code.equals("success")) {

                                        Intent intent = new Intent(LoginPageActivity
                                                .this, PhotoActivity.class);
                                        startActivity(intent);
                                        finish();
                                        progressDialog.dismiss();
                                        Login_Button.setEnabled(true);


                                    } else if (code.equals("failed")) {

                                        Toast.makeText(LoginPageActivity.this,response.toString(),Toast.LENGTH_LONG).show();

                                        Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        v1.vibrate(100);
                                        progressDialog.dismiss();
                                        Login_Button.setEnabled(true);
                                    }

                                    //Toast.makeText(getApplicationContext(), "Respnse :" + response, Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(LoginPageActivity.this,"Database Connectivity Error!!! Check Your Network Connection And Try Again...",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                Login_Button.setEnabled(true);
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();

                        params.put("username", username);
                        params.put("Password", Password);
                            /*params.put("enrollment_no", enrollment_no);
                            params.put("roll_no", roll_no);
                            params.put("password", password);
                            params.put("confirm_password", confirm_password);*/
                        return params;
                    }
                };


                RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
            }

        }
    });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        managePrefs();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        managePrefs();
    }

    public boolean isValidUsername(String un) {
        if (un.equals("") || un.length() == 0) {
            return false;
        }

        return true;

    }

    public boolean isValidPassword(String ps) {
        if (ps.equals("") || ps.length() == 0) {
            return false;
        }

        return true;

    }


    private void managePrefs()
    {
        if (switchManagePrefs.isChecked())
        {
            editor.putString(usrname, editTextUser.getText().toString().trim());
            editor.putString(passwd, editTextPassword.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
        }

        else
        {
            editor.putBoolean(KEY_REMEMBER, false);
            editor.remove(passwd);
            editor.remove(usrname);
            editor.apply();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to close STUDENT DIARY", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 4000);
    }
    }

