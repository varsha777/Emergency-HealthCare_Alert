package com.example.user.emergencyhealthcare.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by User on 4/25/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DOB = "dob";
    public static final String KEY_USER = "username";
    public static final String KEY_PASS = "password";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PHONENO = "phoneno";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_BLOODGR = "bloodgroup";
    public static final String ECONTACT1 = "econtact1";
    public static final String ECONTACT2 = "econtact2";


    DBHelper DB = null;
    private static final String DATABASE_NAME = "ehealthcare.db";
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_TABLE_NAME = "register";

    private static final String DATABASE_TABLE_CREATE =
            "CREATE TABLE " + DATABASE_TABLE_NAME + "(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "name TEXT NOT NULL, dob TEXT NOT NULL, username TEXT NOT NULL, password TEXT NOT NULL, email TEXT NOT NULL, " +
                    "phoneno TEXT NOT NULL, address TEXT NOT NULL, bloodgroup TEXT NOT NULL, econtact1 TEXT NOT NULL, econtact2 TEXT NOT NULL);";


    public DBHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println("In constructor");
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{

            db.execSQL(DATABASE_TABLE_CREATE);



        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor rawQuery(String string, String[] strings) {
        // TODO Auto-generated method stub
        return null;
    }




    public void open() {

        getWritableDatabase();
    }


    public Cursor getDetails(String text) throws SQLException
    {

        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor mCursor =
//                db.query(DATABASE_TABLE_NAME, new String[] {ECONTACT1, ECONTACT2},
//                        KEY_USER + "=" + text, null, null, null, null, null);
        Cursor mCursor = db.rawQuery("select * from register where username = '"+text+"'", null);
        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;


    }

}
