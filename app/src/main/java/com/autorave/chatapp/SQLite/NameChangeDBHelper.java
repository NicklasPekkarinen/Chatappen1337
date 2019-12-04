package com.autorave.chatapp.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public final class NameChangeDBHelper extends SQLiteOpenHelper {

            // Database colums
            public static final String TABLE_NAME = "nicknameTable";
            public static final String ID = "_ID";
            public static final String USER_ID = "userId";
            public static final String COLUMN_NAME_NICKNAME = "nickName";

        //Creates Database
        private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    USER_ID + " TEXT, " +
                   COLUMN_NAME_NICKNAME + " TEXT)";

        private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "NameChange.db";

        public NameChangeDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase dbSQLite) {
            dbSQLite.execSQL(SQL_CREATE_ENTRIES);

        }
        // Uppdaterar databasen
        public void onUpgrade(SQLiteDatabase dbSQLite, int i, int i1) {
            dbSQLite.execSQL(SQL_DELETE_ENTRIES);
            onCreate(dbSQLite);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        //Set values to databas on a spicific user
        public void setDataSQL(List<String> userInfo) {
            SQLiteDatabase mDatabas = getWritableDatabase();

            String query = "INSERT INTO nicknameTable (userId, nickname) VALUES ('" + userInfo.get(1) + "','" + userInfo.get(0) + "')";
            mDatabas.execSQL(query);
        }

        //Gets data from database and displays Nickname on specific person in contacts list
    public List<String> getDataSQL(){
        SQLiteDatabase mDatabas = getReadableDatabase();
        ArrayList<String>UsersInfo = new ArrayList<>();

        Cursor cursor = mDatabas.rawQuery("SELECT * FROM nicknameTable", null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String nickname = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICKNAME));
                String id = cursor.getString(cursor.getColumnIndex(USER_ID));

                UsersInfo.add(nickname);
                UsersInfo.add(id);

                cursor.moveToNext();
            }
        }
        return UsersInfo;
    }
}
