package com.example.ena.attendancesystem;

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
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ena.attendancesystem.LoginActivity.LoginPageActivity;
import com.example.ena.attendancesystem.Misc.Constant;
import com.example.ena.attendancesystem.Misc.RequestHandler;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PhotoActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextBranch;
    private EditText editTextRoll;
    private EditText editTextSem;
    ImageView targetImage;

    private ProgressDialog progressDialog;

    private Uri mCropImageUri;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);


        editTextName = (EditText)findViewById(R.id.editTextName);
        editTextBranch = (EditText)findViewById(R.id.editTextBranch);
        editTextRoll = (EditText) findViewById(R.id.editTextRoll);
        editTextSem = (EditText) findViewById(R.id.editTextSem);
        targetImage = (ImageView)findViewById(R.id.targetImage);
        progressDialog = new ProgressDialog(this);

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

            getPersonalDetails();
        }


        targetImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                sharedPreferences = getSharedPreferences(getString(R.string.app_name) + "_ProfileDetails", MODE_PRIVATE);
                final String profilePicUri = sharedPreferences.getString("profilePicUrl", "");

                if (profilePicUri.equalsIgnoreCase("")) {
                    onSelectImageClick(v);
                } else {

                    try {
                        final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        // Check for the freshest data.
                        getContentResolver().takePersistableUriPermission(Uri.parse(profilePicUri), takeFlags);
                        // convert uri to bitmap

                    } catch (Exception e) {
                        //handle exception
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getPersonalDetails() {

        final String PREFERENCES = "MyPref";
        final String USERNAME = "USERNAME";

        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        final String Name = sharedpreferences.getString(USERNAME, null);

        progressDialog.show();
        progressDialog.setMessage("Loading Details...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constant.URL_FETCH_PERSONAL_DETAILS,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(String response) {


                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("code").contentEquals("success")){
                                JSONArray Jarray= jsonObject.getJSONArray("resp");
                                for(int i=0; i< Jarray.length();i++) {
                                    JSONObject Jobj=Jarray.getJSONObject(i);
                                    String Name = Jobj.getString("Name");
                                    String Branch = Jobj.getString("Branch");
                                    String Roll = Jobj.getString("Roll");
                                    String semester = Jobj.getString("semester");

                                    editTextName.setText(Name);
                                    editTextBranch.setText(Branch);
                                    editTextRoll.setText(Roll);
                                    editTextSem.setText(semester);

                                    progressDialog.dismiss();

                                    }
                            }


                            else if(jsonObject.getString("code").contentEquals("failed")){

                                Toast.makeText(getApplicationContext(),"Message : "+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                //Toast.makeText(getApplicationContext(),"Database Connectivity Issue!!!  ", Toast.LENGTH_LONG).show();

                                progressDialog.dismiss();
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

                        Toast.makeText(PhotoActivity.this,"Database Connectivity Error!!! Check Your Network Connection And Try Again...",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("Name", Name);
                return params;
            }
        };


        RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);

    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        Uri imageUri = null;
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);

            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //((ImageButton) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                mCropImageUri = result.getUri();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // save uri to shared preference
                editor.putString("profilePicUrl", mCropImageUri.toString());
                editor.commit();
                targetImage.setImageURI(mCropImageUri);
                //Toast.makeText(this, "Cropping successful" + result.getSampleSize(), Toast.LENGTH_LONG).show();
            }

            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed!!! ", Toast.LENGTH_LONG).show();

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }


    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }


    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.personal_details:
                Intent intent = new Intent(PhotoActivity.this, PersonalDetailsActivity.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.logout:
                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(PhotoActivity.this);
                TextView myMsg = new TextView(PhotoActivity.this);
                builder.setTitle("Attendance System");
                myMsg.setText("\nAre you sure you want to logout" + "\nfrom the application?" + "\nThis will clear your saved credentials...");
                myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                myMsg.setPadding(10, 15, 15, 10);
                myMsg.setTextSize(17);
                myMsg.setTextColor(Color.BLACK);
                builder.setView(myMsg).setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finishAffinity();
                        this.close();
                        SharedPreferences sp = getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor e = sp.edit();
                        e.clear();
                        e.commit();
                        System.exit(0);
                    }

                    private void close() {
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.show();
                return(true);

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PhotoActivity.this,LoginPageActivity.class);
        startActivity(intent);
        finish();
    }

    @SuppressLint("NewApi")
    public void applyAttendance(View view) {
        Intent intent = new Intent(PhotoActivity.this,AttendanceActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @SuppressLint("NewApi")
    public void viewAttendance(View view) {
        Intent intent = new Intent(PhotoActivity.this,Atendance_Details_Activity.class);
        startActivity(intent);
        finishAffinity();
    }
}
