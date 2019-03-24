package com.example.ena.attendancesystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ena.attendancesystem.Misc.Constant;
import com.example.ena.attendancesystem.Misc.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AttendanceDetailsActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private TableLayout stk;
    private EditText editTextSubjectName;
    private Button buttonViewDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_details);

        progressDialog = new ProgressDialog(AttendanceDetailsActivity.this);
        editTextSubjectName=(EditText) findViewById(R.id.editTextSubjectName) ;
        stk = (TableLayout)findViewById(R.id.tableLayout1);
        buttonViewDetails=(Button)findViewById(R.id.buttonViewDetails);

        TableRow tbrow = new TableRow(AttendanceDetailsActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tbrow.setBackground(getResources().getDrawable(R.drawable.cell_background));
        }

        final TextView tv0 = new TextView(AttendanceDetailsActivity.this);
        tv0.setText(" No ");
        tv0.setTextColor(Color.parseColor("#263237"));
        tv0.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setPadding(50, 20, 0, 20);
        tbrow.addView(tv0);

        final TextView tv = new TextView(AttendanceDetailsActivity.this);
        tv.setText(" Subject ");
        tv.setTextColor(Color.parseColor("#263237"));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(50, 20, 0, 20);
        tbrow.addView(tv);

        final TextView tv01 = new TextView(AttendanceDetailsActivity.this);
        tv01.setText(" Date ");
        tv01.setTextColor(Color.parseColor("#263237"));
        tv01.setGravity(Gravity.CENTER_HORIZONTAL);
        tv01.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv01.setTypeface(null, Typeface.BOLD);
        tv01.setPadding(50, 20, 0, 20);
        tbrow.addView(tv01);

        final TextView tv02 = new TextView(AttendanceDetailsActivity.this);
        tv02.setText(" Description ");
        tv02.setTextColor(Color.parseColor("#263237"));
        tv02.setGravity(Gravity.CENTER_HORIZONTAL);
        tv02.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv02.setTypeface(null, Typeface.BOLD);
        tv02.setPadding(50, 20, 0, 20);
        tbrow.addView(tv02);
        stk.addView(tbrow);

        buttonViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String subjectname = editTextSubjectName.getText().toString().trim();

                if (!isValidsubjectname(subjectname)) {
                    editTextSubjectName.setError(" subject Field cannot be blank!!!");
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                }
                else {

                    //buttonViewDetails.setEnabled(false);
                    progressDialog.setMessage("searching data!!");
                    progressDialog.show();

                    progressDialog.show();
                    progressDialog.setMessage("Fetching Data... Please Wait...");

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            Constant.URL_FETCH_ATTENDANCE_DATA,
                            new Response.Listener<String>() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void onResponse(String response) {

                                    Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                                    try {

                                        JSONObject jsonObject = new JSONObject(response);
                                        if(jsonObject.getString("code").contentEquals("success")) {
                                            JSONArray Jarray = jsonObject.getJSONArray("resp");
                                            for (int i = 0; i < Jarray.length(); i++) {
                                                JSONObject json_data = Jarray.getJSONObject(i);

                                                TableLayout table1 = (TableLayout) findViewById(R.id.tableLayout1);
                                                TableRow tbrow0 = new TableRow(AttendanceDetailsActivity.this);
                                                tbrow0.setBackground(getResources().getDrawable(R.drawable.cell_background));

                                                TextView tv0 = new TextView(AttendanceDetailsActivity.this);
                                                tv0.setText(json_data.getString("no"));
                                                tv0.setTextColor(Color.parseColor("#263237"));
                                                tv0.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                                                tv0.setTypeface(null, Typeface.BOLD);
                                                tv0.setPadding(20, 15, 0, 15);
                                                tbrow0.addView(tv0);

                                                TextView tv1 = new TextView(AttendanceDetailsActivity.this);
                                                tbrow0.setBackground(getResources().getDrawable(R.drawable.cell_background));
                                                tv1.setText(json_data.getString("subject"));
                                                tv1.setGravity(Gravity.CENTER_HORIZONTAL);
                                                tv1.setTextColor(Color.parseColor("#263237"));
                                                tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                                                tv1.setTypeface(null, Typeface.BOLD);
                                                tv1.setPadding(0, 15, 0, 15);
                                                tbrow0.addView(tv1);

                                                TextView tv2 = new TextView(AttendanceDetailsActivity.this);
                                                tbrow0.setBackground(getResources().getDrawable(R.drawable.cell_background));
                                                tv2.setText(json_data.getString("date"));
                                                tv2.setGravity(Gravity.CENTER_HORIZONTAL);
                                                tv2.setTextColor(Color.parseColor("#263237"));
                                                tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                                                tv2.setTypeface(null, Typeface.BOLD);
                                                tv2.setPadding(0, 10, 0, 15);
                                                tbrow0.addView(tv2);

                                    /*if(!tv2.getText().toString().equals(""))
                                    {
                                        tv21.setTextColor(Color.parseColor("#ff8c00"));
                                        tv2.setTextColor(Color.parseColor("#ff8c00"));
                                        tv01.setTextColor(Color.parseColor("#ff8c00"));
                                    }*/

                                                TextView tv3 = new TextView(AttendanceDetailsActivity.this);
                                                tbrow0.setBackground(getResources().getDrawable(R.drawable.cell_background));
                                                tv3.setText(json_data.getString("description"));
                                                tv3.setGravity(Gravity.CENTER_HORIZONTAL);
                                                tv3.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                                                tv3.setTypeface(null, Typeface.BOLD);
                                                tv3.setTextColor(Color.parseColor("#263237"));
                                                tv3.setPadding(0, 15, 0, 15);
                                                tbrow0.addView(tv3);

                                    /*if(!tv3.getText().toString().equals(""))
                                    {
                                        tv31.setTextColor(Color.parseColor("#303F9F"));
                                        tv3.setTextColor(Color.parseColor("#303F9F"));
                                        tv02.setTextColor(Color.parseColor("#303F9F"));

                                    }*/

                                                table1.addView(tbrow0);


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

                                    Toast.makeText(AttendanceDetailsActivity.this,"Database Connectivity Error!!! Check Your Network Connection And Try Again...",Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("subjectname", subjectname);


                            return params;
                        }
                    };


                    RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                }

            }
        });



    }

    public boolean isValidsubjectname(String sn) {
        if (sn.equals("") || sn.length() == 0) {
            return false;
        }

        return true;

    }
}
