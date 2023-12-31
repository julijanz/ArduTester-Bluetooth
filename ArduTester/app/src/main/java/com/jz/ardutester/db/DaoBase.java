package com.jz.ardutester.db;

import android.database.sqlite.SQLiteDatabase;

import com.jz.ardutester.ApplicationArduTester;

public class DaoBase
{
    SQLiteDatabase database;

    public DaoBase()
    {
        this.database = ApplicationArduTester.Database();
    }
}
