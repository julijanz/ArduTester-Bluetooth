package com.jz.ardutester.utils;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;

import com.jz.ardutester.ApplicationArduTester;
import com.jz.ardutester.R;

public class DialogMsg
{
    public static void Alert(Activity activity, String title, String msg, String btnText)
    {
        Show(DialogType.ALERT, activity, title, msg, btnText);
    };

    public static void Alert(Activity activity, String msg)
    {
        Show(DialogType.ALERT, activity,
            ApplicationArduTester.getContext().getResources().getString(R.string.msg_title_alert), msg,
            ApplicationArduTester.getContext().getResources().getString(R.string.btn_ok));
    }

    public static void Error(Activity activity, String title, String msg, String btnText)
    {
        Show(DialogType.ERROR, activity, title, msg, btnText);
    }

    public static void Error(Activity activity, String msg)
    {
        String s = ApplicationArduTester.getContext().getResources().getString(R.string.msg_title_error);
        Show(DialogType.ERROR, activity,
            ApplicationArduTester.getContext().getResources().getString(R.string.msg_title_error), msg,
            ApplicationArduTester.getContext().getResources().getString(R.string.btn_ok));
    }

    public static void Info(Activity activity, String title, String msg, String btnText)
    {
        Show(DialogType.INFO, activity, title, msg, btnText);
    }

    public static void Info(Activity activity, String msg)
    {
        Show(DialogType.INFO, activity,
            ApplicationArduTester.getContext().getResources().getString(R.string.msg_title_information), msg,
            ApplicationArduTester.getContext().getResources().getString(R.string.btn_ok));
    }

    private static void Show(DialogType type, Activity activity, String title, String msg, String btnText)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);

        if ((title != null) && (!title.trim().equalsIgnoreCase("")))
            builder.setTitle(title);
        if (type == DialogType.ALERT)
            builder.setIcon(R.drawable.ic_alert);
        if (type == DialogType.ERROR)
            builder.setIcon(R.drawable.ic_error);
        if (type == DialogType.INFO)
            builder.setIcon(R.drawable.ic_info);
        builder.setMessage(msg);
        builder.setPositiveButton(btnText, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public enum DialogType
    {
        ALERT, ERROR, INFO
    }

}











