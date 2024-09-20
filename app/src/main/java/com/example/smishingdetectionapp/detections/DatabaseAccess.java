package com.example.smishingdetectionapp.detections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.smishingdetectionapp.R;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseAccess {
    private static SQLiteOpenHelper openHelper;
    static SQLiteDatabase db;
    private static DatabaseAccess instance;
    Context context;

    public static class DatabaseOpenHelper extends SQLiteAssetHelper {

        private static final String DATABASE_NAME="detectlist.db";
        private static final int DATABASE_VERSION=1;
        private static final String TABLE_DETECTIONS = "Detections";
        private static final String TABLE_REPORTS = "Reports";
        public static final String KEY_ROWID = "_id";
        public static final String KEY_PHONENUMBER="Phone_Number";
        public static final String KEY_MESSAGE = "Message";
        public static final String KEY_DATE = "Date";
        public static final String KEY_TYPE = "Type";

        public DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }
    }

    DatabaseAccess(Context context) {

        openHelper= new DatabaseOpenHelper(context);
        this.context = context;
    }

    public static DatabaseAccess getInstance(Context context){
        if(instance==null){
            instance=new DatabaseAccess(context);
        }
        return instance;
    }

    public void open(){
        this.db=openHelper.getWritableDatabase();
        System.out.println("Database Opened!");
    }

    public void close(){
        if(db!=null){
            this.db.close();
            System.out.println("Database Closed!");
        }
    }

    //Total detections counter
    public int getCounter() {
        Cursor cursor = db.rawQuery("select * from Detections", null);
        System.out.println("Number of Records: "+cursor.getCount());
        return cursor.getCount();
    }

    public int SmishingCounter(){
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Detections WHERE Type = 'Smishing'", null);
        int count = 0;
        if (cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int HamCounter(){
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Detections WHERE Type = 'Ham'", null);
        int count = 0;
        if (cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int SpamCounter(){
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Detections WHERE Type = 'Spam'", null);
        int count = 0;
        if (cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    //Used to get current device time
    private static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    //Report sending function with database
    public static boolean sendReport(int phonenumber, String message) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseOpenHelper.KEY_PHONENUMBER, phonenumber);
        contentValues.put(DatabaseOpenHelper.KEY_MESSAGE, message);
        contentValues.put(DatabaseOpenHelper.KEY_DATE, getDateTime());
        long result = db.insert(DatabaseOpenHelper.TABLE_REPORTS, null, contentValues);
        return result != -1;
    }

    public Cursor populateList(){
        SQLiteDatabase db = openHelper.getReadableDatabase();

        String query = "SELECT _id, Phone_Number, Message, Date, Type FROM Detections";

        return db.rawQuery(query, null);
    }
}