package com.jz.ardutester.db;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;

import com.jz.ardutester.utils.Common;

public class DaoSettings extends DaoBase
{
    public DaoSettings()
    {
        super();
    }

    public String getMacAddress()
    {
        return Read(Common.MAC_ADDRESS, "");
    }

    public void setMacAddress(String value)
    {
        Write(Common.MAC_ADDRESS, value);
    }

    //Write setting
    private void Write(String name, String value) throws SQLException
    {
        String sql;
        sql = "SELECT * FROM Settings WHERE Name = '" + name + "'";
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor != null)
        {
            try
            {
                if (cursor.moveToFirst())
                    sql = "UPDATE Settings SET Value = '" + value + "' WHERE Name = '" + name + "'";
                else
                    sql = "INSERT INTO Settings (Name, Value) values ('" + name + "','" + value + "')";
                database.execSQL(sql);
            } finally
            {
                cursor.close();
            }
        }
    }

    //Read setting
    private String Read(String name, String defaultValue)
    {
        String result = defaultValue;
        String query = "SELECT * FROM Settings WHERE Name = '" + name + "'";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null)
        {
            try
            {
                if (cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndex("Value"));
            } finally
            {
                cursor.close();
            }
        }
        return result;
    }
}
