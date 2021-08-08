package com.hfad.repeattweet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteDB extends SQLiteOpenHelper {

    public static String DB_NAME = "MESSAGES";
    public static int DB_VERSION = 1;

    SQLiteDB(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onCreate(SQLiteDatabase SQLiteDatabase) {
        String SQLMessageTable = "CREATE TABLE messages ( " +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " message String, " +
                " poastedDate String, " +
                " isFacebook Boolean, " +
                " isTwitter Boolean" +
                ")";
        SQLiteDatabase.execSQL(SQLMessageTable);

        String SQLFacebookTable = "CREATE TABLE facebook ( " +
                " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " appID String, " +
                " pageID String, " +
                " userID String, " +
                " userToken String" +
                ")";

        SQLiteDatabase.execSQL(SQLFacebookTable);
    }


}
