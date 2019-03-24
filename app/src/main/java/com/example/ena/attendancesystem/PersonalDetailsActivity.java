package com.example.ena.attendancesystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ena.attendancesystem.Misc.Constant;
import com.example.ena.attendancesystem.Misc.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PersonalDetailsActivity extends AppCompatActivity {

    private Button buttonUpdate;
    private Spinner spinner1;
    private EditText editTextRoll;
    private EditText editTextBranch;
    private EditText editTextName;
    private TextView textViewRandomString;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_personal_details);


        buttonUpdate=(Button)findViewById(R.id.buttonUpdate);
        spinner1=(Spinner)findViewById(R.id.spinner1);
        editTextRoll=(EditText)findViewById(R.id.editTextRoll);
        editTextBranch=(EditText)findViewById(R.id.editTextBranch);
        editTextName=(EditText)findViewById(R.id.editTextName);
        spinner1=(Spinner)findViewById(R.id.spinner1) ;
        textViewRandomString = (TextView) findViewById(R.id.textViewRandomString);
        progressDialog = new ProgressDialog(PersonalDetailsActivity.this);

        final String PREFERENCES = "MyPref";
        final String USERNAME = "USERNAME";
        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        final String usrname = sharedpreferences.getString(USERNAME, null);
        textViewRandomString.setText(usrname);

        String[] semester = new String[]
                {
                  "Semester",
                  "I",
                  "II",
                  "III",
                  "IV",
                  "V",
                  "VI",
                  "VII",
                  "VIII",
                  "IX",
                  "X"
                };

        final List<String> titleList = new ArrayList<>(Arrays.asList(semester));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,titleList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }

        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner1.setAdapter(spinnerArrayAdapter);

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = editTextName.getText().toString().trim();
                final String branch = editTextBranch.getText().toString().trim();
                final String roll = editTextRoll.getText().toString().trim();
                final String semester = String.valueOf(spinner1.getSelectedItem()).trim();



         if (!isValidname(name)) {
                    editTextName.setError(" name Field cannot be blank!!!");
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                } else if (!isValidbranch(branch)) {
                    editTextBranch.setError("branch Field cannot be blank!!!");
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);

                }
                else if (!isValidroll(roll)) {
                    editTextRoll.setError("roll no Field cannot be blank!!!");
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);

                }else {


                    buttonUpdate.setEnabled(false);
                    progressDialog.setMessage("Upating Your Data... Please Wait...");
                    progressDialog.show();


                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            Constant.URL_UPDATE_DETAILS,
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
                                            buttonUpdate.setEnabled(true);

                                            Intent intent = new Intent(PersonalDetailsActivity.this,PhotoActivity.class);
                                            startActivity(intent);
                                            finish();

                                            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();


                                        } else if (code.equals("failed")) {

                                            Toast.makeText(PersonalDetailsActivity.this, response.toString(), Toast.LENGTH_LONG).show();

                                            Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            v1.vibrate(100);
                                            progressDialog.dismiss();
                                            buttonUpdate.setEnabled(true);
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

                                    Toast.makeText(PersonalDetailsActivity.this, "Database Connectivity Error!!! Check Your Network Connection And Try Again...", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    buttonUpdate.setEnabled(true);
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();

                            params.put("Name", name);
                            params.put("Branch", branch);
                            params.put("Roll", roll);
                            params.put("semester", semester);

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

    public boolean isValidroll(String rl) {
        if (rl.equals("") || rl.length() == 0) {
            return false;
        }

        return true;

    }

    public boolean isValidname(String nm) {
        if (nm.equals("") || nm.length() == 0) {
            return false;
        }

        return true;

    }
    public boolean isValidbranch(String br) {
        if (br.equals("") || br.length() == 0) {
            return false;
        }

        return true;

    }

}

