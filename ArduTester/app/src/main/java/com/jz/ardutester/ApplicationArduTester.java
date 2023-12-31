package com.jz.ardutester;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;

import com.jz.ardutester.db.DBConnection;
import com.jz.ardutester.db.DaoSettings;

import java.util.concurrent.Delayed;

public class ApplicationArduTester extends Application
{
    private static String macAddress;
    private static Context sContext;
    private static ToneGenerator toneGenerator;
    private static SQLiteDatabase _database;

    public static void Beep()
    {
        if (toneGenerator != null)
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
    }

    public static void BeepError()
    {
        if (toneGenerator != null)
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2);
    }

    public static SQLiteDatabase Database()
    {
        return _database;
    }

    public static Context getContext()
    {
        return sContext;
    }

    public static String getMacAddress()
    {
        return macAddress;
    }

    public static void UpdateMacAddress()
    {
        DaoSettings daoSettings = new DaoSettings();
        macAddress = daoSettings.getMacAddress();
    }

    public static void ReadSettings()
    {
        DaoSettings daoSettings = new DaoSettings();
        macAddress = daoSettings.getMacAddress();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        sContext = getApplicationContext();
        DBConnection _dbHelper = new DBConnection(sContext);
        _database = _dbHelper.getWritableDatabase();
        toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, 80);
        ReadSettings();
    }
}


