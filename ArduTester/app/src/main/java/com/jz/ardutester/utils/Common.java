package com.jz.ardutester.utils;

public class Common
{
    public final static int REQUEST_ENABLE_BLUETOOTH = 1;
    public final static String MAC_ADDRESS = "MAC_ADDRESS";

    private Common()
    {
    }

    //Like Delphi Copy
    public static String Copy(String value, int startChar, int size)
    {
        String result = "";
        if ((value == null) || (value.equalsIgnoreCase("")))
        {
            return result;
        }
        if (startChar > value.length())
        {
            return value;
        }
        char[] charArray = value.toCharArray();
        int pos = 0;
        for (int i = startChar - 1; i < charArray.length; ++i)
        {
            pos = pos + 1;
            result = result + charArray[i];
            if ((pos == size) || (i + 1 == charArray.length))
            {
                break;
            }
        }
        return result;
    }
}