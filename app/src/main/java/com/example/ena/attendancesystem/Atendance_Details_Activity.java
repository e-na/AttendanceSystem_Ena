package com.example.ena.attendancesystem;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ena.attendancesystem.Misc.Constant;
import com.example.ena.attendancesystem.Misc.NetworkCheck;
import com.example.ena.attendancesystem.Misc.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Atendance_Details_Activity extends AppCompatActivity implements Attendance_Details_Adapter.ContactsAdapterListener, View.OnClickListener {

    private static final String TAG = AttendanceDetailsActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private ArrayList<Attendance_Details_Report> contactList = new ArrayList<>();
    private Attendance_Details_Adapter mAdapter;
    private SearchView searchView;
    private ProgressDialog progressDialog;

    dbHelperPersonalTask sqlite_obj;
    List<String> list1, list2, list3;
    InputStream is = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atendance_details_);

        recyclerView = findViewById(R.id.recycler_view);
        contactList = new ArrayList<>();
        mAdapter = new Attendance_Details_Adapter(this, contactList, this);
        //tv = new TextView(this);



        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
        recyclerView.setAdapter(mAdapter);


        progressDialog = new ProgressDialog(this);

        sqlite_obj = new dbHelperPersonalTask(getApplicationContext());
        readFromLoacalStorage();

        final dbHelperPersonalTask dbHelper = new dbHelperPersonalTask(this);
        final String[] myData = dbHelper.SelectAllData();


        if (myData != null) {

            //Toast.makeText(getApplicationContext(),"JAY"+User_Name,Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(getApplicationContext(),"No Data Found!!!",Toast.LENGTH_LONG).show();
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Kindly Synchronize To Populate The Data!!!", Snackbar.LENGTH_LONG).show();
        }
        
        fetchAttendanceDetails();

    }

    private void sqlite() {
        // TODO Auto-generated method stub

        sqlite_obj.open();

        sqlite_obj.deleteUsers();

        if (list1 != null && list2 != null && list3 != null) {
            for (int i = 0; i < list1.size(); i++) {

                sqlite_obj.addUser(list1.get(i).toString(), list2.get(i).toString(), list3.get(i).toString());

                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Data Synchronized!!!", Snackbar.LENGTH_LONG)
                        /*.setAction("CLOSE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))*/
                        .show();
            }
        } else {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "No Data Found!!!", Snackbar.LENGTH_LONG).show();
        }


        sqlite_obj.close();
    }


    private void readFromLoacalStorage()
    {
        contactList.clear();
        dbHelperPersonalTask dbHelper = new dbHelperPersonalTask(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.readFromLocalDatabase(database);

        while (cursor.moveToNext())
        {

            String SUBJECT = cursor.getString(cursor.getColumnIndex(Constant.SUBJECT));
            String DATE = cursor.getString(cursor.getColumnIndex(Constant.DATE));
            String DESCRIPTION = cursor.getString(cursor.getColumnIndex(Constant.DESCRIPTION));
            contactList.add(new Attendance_Details_Report(SUBJECT,DATE,DESCRIPTION));
        }

        mAdapter.notifyDataSetChanged();
        cursor.close();
        dbHelper.close();
    }

    private void fetchAttendanceDetails() {

        SharedPreferences sharedpreferences;
        final String PREFERENCES = "MyPref";
        final String USERNAME = "USERNAME";
        sharedpreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        final String Name = sharedpreferences.getString(USERNAME, null);

        //Toast.makeText(getApplicationContext(),MOBILE,Toast.LENGTH_LONG).show();

        progressDialog.setMessage("Loading Data... Please Wait!!!");
        progressDialog.show();

        if(NetworkCheck.checkNetworkConnection(getApplicationContext())) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constant.URL_FETCH_ATTENDANCE_DETAILS,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(String response) {


                            try {
                                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getString("code").contentEquals("success")) {
                                    JSONArray Jarray = jsonObject.getJSONArray("resp");
                                    JSONObject jo = null;

                                    list1 = new ArrayList<String>();
                                    list2 = new ArrayList<String>();
                                    list3 = new ArrayList<String>();



                                    for (int i = 0; i < Jarray.length(); i++) {

                                        jo = Jarray.getJSONObject(i);

                                        list1.add(jo.getString("subject"));
                                        list2.add(jo.getString("date"));
                                        list3.add(jo.getString("description"));



                                    }

                                    sqlite();
                                    progressDialog.dismiss();
                                    readFromLoacalStorage();
                                    sqlite_obj.close();


                                } else if (jsonObject.getString("code").contentEquals("failed")) {

                                    Toast.makeText(Atendance_Details_Activity.this, response, Toast.LENGTH_LONG).show();
                                    sqlite_obj.deleteUsers();
                                    readFromLoacalStorage();
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

                            Toast.makeText(Atendance_Details_Activity.this, "Database Connectivity Error!!! Check Your Network Connection And Try Again...", Toast.LENGTH_LONG).show();
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
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

        }

        else
        {

            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    progressDialog.cancel();
                    Snackbar.make(findViewById(android.R.id.content), "No Internet ! Local Data Populated...", Snackbar.LENGTH_LONG).show();
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.attendance_details_menu, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                mAdapter.getFilter().filter(query);
                return false;
            }
        });

        //int profile_counts = (int) sqlite_obj.getProfilesCount();
        //sqlite_obj.close();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_search:

                break;

            case R.id.action_refresh:


                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Syncing Data... Please Wait!!!", Snackbar.LENGTH_LONG).show();

                fetchAttendanceDetails();

                break;


            default:
                break;
        }

        return true;
    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Atendance_Details_Activity.this, PhotoActivity.class);
        startActivity(intent);
        finishAffinity();

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onContactSelected(Attendance_Details_Report attendance_details_report) {


        /*Intent intent = new Intent(Atendance_Details_Activity.this, PersonalTaskInProgressContentActivity.class);
        intent.putExtra("TASK_NAME", attendance_details_report.getDate());
        intent.putExtra("TASK_DESCRIPTION", attendance_details_report.getTASK_DESCRIPTION());
        intent.putExtra("TASK_PRIORITY", personalTaskInProgressReport.getTASK_PRIORITY());
        intent.putExtra("TASK_PERTAINS_TO", personalTaskInProgressReport.getTASK_PERTAINS_TO());
        intent.putExtra("RANDOM_STRING",personalTaskInProgressReport.getRANDOM_STRING());
        startActivity(intent);
        finishAffinity();*/

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onClick(View view) {
        Toast.makeText(getApplicationContext(), "Total Counts Of Pending Faults", Toast.LENGTH_LONG).show();
    }
}
