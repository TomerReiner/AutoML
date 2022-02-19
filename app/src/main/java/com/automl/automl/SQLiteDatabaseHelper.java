package com.automl.automl;

import android.content.Context;
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
        String query = String.format("CREATE TABLE IF NOT EXISTSv %s (%s TEXT DEFAULT '', %s TEXT DEFAULT '', %s TEXT DEFAULT '');",
                USERS, USERNAME, PHONE_NUMBER, PASSWORD);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("DELETE FROM %s;", USERS);
        db.execSQL(query);

        if (user == null)
            query = String.format("INSERT INTO %s VALUES();", USERS);
        else
            query = String.format("INSERT INTO %s VALUES(%s, %s, %s);", USERS, user.getUsername(), user.getPhoneNum(), user.getPassword());
        db.execSQL(query);
        db.close();
    }
}
