package com.jz.ardutester.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBConnection extends SQLiteOpenHelper
{

    private final static String DATABASE_NAME = "dbArduTester.db";
    private final static int DATABASE_VERSION = 1;
    private final static String CREATE_TABLE_SETTINGS =
            "CREATE TABLE [Settings] (" +
            "[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "[Name] TEXT NOT NULL UNIQUE," +
            "[Value] TEXT NOT NULL);";

    public DBConnection(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDB)
    {
        sqLiteDB.execSQL(CREATE_TABLE_SETTINGS);
        onUpgrade(sqLiteDB, 1, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDB, int oldVersion, int newVersion)
    {
        if (newVersion > oldVersion)
        {
            sqLiteDB.beginTransaction();

            boolean success = true;

            for (int i = oldVersion; i < newVersion; ++i)
            {
                int nextVersion = i + 1;
                switch (nextVersion)
                {
                    case 2:
                        try
                        {
                            //
                        } catch (Exception e)
                        {
                            success = false;
                        }
                        break;
                    case 3:
                        try
                        {
                            //
                        } catch (Exception e)
                        {
                            success = false;
                        }
                        break;
                }
                if (!success)
                {
                    break;
                }
            }

            if (success)
            {
                sqLiteDB.setTransactionSuccessful();
            }
            sqLiteDB.endTransaction();
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db)
    {
        db.setForeignKeyConstraintsEnabled(true);
    }

}