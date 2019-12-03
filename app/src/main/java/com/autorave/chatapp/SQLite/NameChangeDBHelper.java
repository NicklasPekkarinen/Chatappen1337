package com.autorave.chatapp.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.autorave.chatapp.User;

import java.util.ArrayList;
import java.util.List;

public final class NameChangeDBHelper extends SQLiteOpenHelper {

        // To prevent someone from accidentally instantiating the contract class,
        // make the constructor private.



            public static final String TABLE_NAME = "nicknameTable";
            public static final String ID = "_ID";
            public static final String USER_ID = "userId";
            public static final String COLUMN_NAME_NICKNAME = "nickName";
            private int counter = 0;

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

        public void onUpgrade(SQLiteDatabase dbSQLite, int i, int i1) {
            dbSQLite.execSQL(SQL_DELETE_ENTRIES);
            onCreate(dbSQLite);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        public void insertNickName(){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();

        }

        public void setDataSQL(List<String> userInfo) {
            SQLiteDatabase mDatabas = getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("nickname", userInfo.get(0));
            cv.put("userId", userInfo.get(1));
            counter++;
            //mDatabas.delete(TABLE_NAME,"userId" + "='" +userInfo.get(1)+"'",null);
            String query = "INSERT INTO nicknameTable (userId, nickname) VALUES ('" + userInfo.get(1) + "','" + userInfo.get(0) + "')";
            mDatabas.execSQL(query);

            //mDatabas.insert("nicknameTable", null, cv);
            Log.d("MarcusTag", mDatabas.toString());
        }
    public List<String> getDataSQL(){
        SQLiteDatabase mDatabas = getReadableDatabase();
        ArrayList<String>UsersInfo = new ArrayList<>();
        Log.d("MarcusTag","In sql method");

        Cursor cursor = mDatabas.query(
                NameChangeDBHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()){
                String nickname = cursor.getString(cursor.getColumnIndex(NameChangeDBHelper.COLUMN_NAME_NICKNAME));
                String userId = cursor.getString(cursor.getColumnIndex(NameChangeDBHelper.USER_ID));
                UsersInfo.add(nickname);
                UsersInfo.add(userId);
            }
        }
        Log.d("Autorave", UsersInfo.get(0) + " " + UsersInfo.get(1));
        return UsersInfo;
    }
}
