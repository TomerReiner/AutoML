package com.automl.automl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "database.db";
    public static final String USERS = "users";
    public static final String USERNAME = "username";
    public static final String PHONE_NUMBER = "phoneNum";
    public static final String PASSWORD = "password";

    public SQLiteDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT DEFAULT '', %s TEXT DEFAULT '', %s TEXT DEFAULT '');",
                USERS, USERNAME, PHONE_NUMBER, PASSWORD);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * This function saves the user that has logged into the app in a local database.
     * This is used to login the user quickly into the app when its loaded.
     * @param user The user that we would like to login into the app.
     */
    public void fastLogin(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("DELETE FROM %s;", USERS);
        db.execSQL(query);

        ContentValues cv = new ContentValues();

        if (user != null) {
            cv.put(USERNAME, user.getUsername());
            cv.put(PHONE_NUMBER, user.getPhoneNum());
            cv.put(PASSWORD, user.getPassword());
        }
        db.insert(USERS, null, cv);
    }

    public User getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = String.format("SELECT * FROM %s;", USERS);

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        try {
            String username = cursor.getString(0);
            String phoneNum = cursor.getString(1);
            String password = cursor.getString(2);

            User user = new User(username, phoneNum, password);
            cursor.close();
            return user;
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * This function logs out the user from the database.
     */
    public void signOut() {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("DELETE FROM %s;", USERS);
        db.execSQL(query);
        db.close();
    }

}
