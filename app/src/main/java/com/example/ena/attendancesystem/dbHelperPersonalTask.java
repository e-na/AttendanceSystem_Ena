package com.example.ena.attendancesystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ena.attendancesystem.Misc.Constant;

public class dbHelperPersonalTask extends SQLiteOpenHelper {


    private static final String TAG = dbHelperPersonalTask.class.getSimpleName();

    private static final int DATABASE_VERSION = 2;
    private static final String CREATE_TABLE = "create table "+ Constant.TABLE_NAME +
            "(id integer primary key autoincrement,"
             +Constant.SUBJECT+" text,"
             +Constant.DATE+" text,"
             +Constant.DESCRIPTION+" text," +
             " integer) ;";

    private static final String DROP_TABLE = "drop table if exists "+Constant.TABLE_NAME;

    public dbHelperPersonalTask(Context context)
    {
        super(context,Constant.DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void saveToLocalDatabase(String SUBJECT,String DATE,
                                    String DESCRIPTION, SQLiteDatabase database)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.SUBJECT,SUBJECT);
        contentValues.put(Constant.DATE,DATE);
        contentValues.put(Constant.DESCRIPTION,DESCRIPTION);
        database.insert(Constant.TABLE_NAME,null,contentValues);
    }

    public Cursor readFromLocalDatabase(SQLiteDatabase database)
    {
        String [] projection = {Constant.SUBJECT,Constant.DATE,Constant.DESCRIPTION};

        return (database.query(Constant.TABLE_NAME,projection,null,null,null,null,null,null));
    }

    public void updateLocalDatabase(String SUBJECT, String DATE, String DESCRIPTION, SQLiteDatabase database)
    {
        ContentValues contentValues = new ContentValues();

        String selection = Constant.SUBJECT+" LIKE ?";
        String[] selection_args = {SUBJECT};
        database.update(Constant.TABLE_NAME,contentValues,selection,selection_args);
    }

    public String[] SelectAllData() {
        // TODO Auto-generated method stub

        try {
            String arrData[] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            //String strSQL = "SELECT  FROM " + Constants.TABLE_NAME;
            String strSQL = "SELECT DISTINCT SUBJECT FROM " + Constant.TABLE_NAME;
            Cursor cursor = db.rawQuery(strSQL, null);

            if(cursor != null)
            {
                if (cursor.moveToFirst()) {
                    arrData = new String[cursor.getCount()];
                    /***
                     *  [x] = Name
                     */
                    int i= 0;
                    do {
                        arrData[i] = cursor.getString(0);
                        i++;
                    } while (cursor.moveToNext());

                }
            }
            cursor.close();

            return arrData;

        } catch (Exception e) {
            return null;
        }

    }

    public String[] SelectData() {
        // TODO Auto-generated method stub

        try {
            String arrData[] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            String strSQL = "SELECT  DISTINCT * FROM " + Constant.TABLE_NAME;
            Cursor cursor = db.rawQuery(strSQL, null);

            if(cursor != null)
            {
                if (cursor.moveToFirst()) {
                    arrData = new String[cursor.getCount()];

                    int i= 0;
                    do {
                        arrData[i] = cursor.getString(0);
                        i++;
                    } while (cursor.moveToNext());

                }
            }
            cursor.close();

            return arrData;

        } catch (Exception e) {
            return null;
        }

    }

    public dbHelperPersonalTask open() throws SQLException {

        SQLiteDatabase db = this.getWritableDatabase();
        return this;
    }

    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(Constant.TABLE_NAME, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }


    public void addUser(String SUBJECT, String DATE, String DESCRIPTION) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.SUBJECT,SUBJECT);
        values.put(Constant.DATE, DATE);
        values.put(Constant.DESCRIPTION, DESCRIPTION);


        long id = db.insert(Constant.TABLE_NAME, null, values);
        db.close();

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /*public void deleteRow(String RANDOM_STRING)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.RANDOM_STRING+"="+RANDOM_STRING, null);
        db.close();
    }*/

    public void deleteRow(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constant.TABLE_NAME, Constant.SUBJECT + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }



}
