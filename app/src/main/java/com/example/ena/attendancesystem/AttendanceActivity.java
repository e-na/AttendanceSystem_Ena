package com.example.ena.attendancesystem;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity {
    private EditText editTextSubject;
    private TextView textViewMore;
    private EditText editTextDate;
    private EditText editTextSubjectDetail;
    private Button buttonSubmit;
    private ProgressDialog progressDialog;
    private LinearLayout linearLayout2;

    Calendar myCalendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        editTextSubject=(EditText)findViewById(R.id.editTextSubject);
        textViewMore=(TextView)findViewById(R.id.textViewMore);
        editTextSubjectDetail=(EditText)findViewById(R.id.editTextSubjectDetail);
        editTextDate=(EditText)findViewById(R.id.editTextDate);
        buttonSubmit=(Button)findViewById(R.id.buttonSubmit);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        progressDialog = new ProgressDialog(AttendanceActivity.this);
        textViewMore = (TextView)findViewById(R.id.textViewMore);

        textViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(AttendanceActivity.this,AttendanceDetailsActivity.class);
                startActivity(intent);
                finish();*/
                linearLayout2.setVisibility(View.VISIBLE);
            }
        });

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AttendanceActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String subject = editTextSubject.getText().toString().trim();
                final String subjectDetail = editTextSubjectDetail.getText().toString().trim();
                final String date = editTextDate.getText().toString().trim();


                if (!isValidsubject(subject)) {
                    editTextSubject.setError(" subject Field cannot be blank!!!");
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                } else if (!isValidsubjectDetail(subjectDetail)) {
                    editTextSubjectDetail.setError("write subject details!!!");
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);

                } else if (!isValiddate(subjectDetail)) {
                    editTextSubjectDetail.setError("date Field cannot be blank!!!");
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);

                } else {

                    buttonSubmit.setEnabled(false);
                    progressDialog.setMessage("submitting data!!");
                    progressDialog.show();

                    SharedPreferences sharedpreferences;
                    final String PREFERENCES = "MyPref";
                    final String USERNAME = "USERNAME";
                    sharedpreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
                    final String Name = sharedpreferences.getString(USERNAME, null);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            Constant.URL_SUBMIT,
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
                                            buttonSubmit.setEnabled(true);
                                            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

                                        } else if (code.equals("failed")) {

                                            Toast.makeText(AttendanceActivity.this, response.toString(), Toast.LENGTH_LONG).show();

                                            Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            v1.vibrate(100);
                                            progressDialog.dismiss();
                                            buttonSubmit.setEnabled(true);
                                            Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
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

                                    Toast.makeText(AttendanceActivity.this, "Database Connectivity Error!!! Check Your Network Connection And Try Again...", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                    buttonSubmit.setEnabled(true);
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();

                            params.put("subject", subject);
                            params.put("date", date);
                            params.put("subjectDetail", subjectDetail);
                            params.put("Name",Name);
                            return params;
                        }
                    };


                    RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


                }
            }
            });


}

    public boolean isValidsubject(String sb) {
        if (sb.equals("") || sb.length() == 0) {
            return false;
        }

        return true;

    }

    public boolean isValidsubjectDetail(String sd) {
        if (sd.equals("") || sd.length() == 0) {
            return false;
        }

        return true;

    }
    public boolean isValiddate(String dt) {
        if (dt.equals("") || dt.length() == 0) {
            return false;
        }

        return true;

    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
            editTextDate.setError(null);
        }

    };

    private void updateLabel() {

        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editTextDate.setText(sdf.format(myCalendar.getTime()));
        //editTextFromDate.setError(null);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AttendanceActivity.this,PhotoActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}

