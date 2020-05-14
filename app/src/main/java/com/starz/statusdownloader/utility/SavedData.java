package com.starz.statusdownloader.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.starz.statusdownloader.MainActivity;

public class SavedData {
    private static final String DEFAULT_VALUE = "no value";
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor edit;
    public SavedData(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences("data", Context.MODE_PRIVATE);
        edit=sharedPreferences.edit();
    }

    public void toast(String message) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public boolean haveValue(String key) {
        return getValue(key)!=DEFAULT_VALUE;
    }

    public String getValue(String key) {
        return sharedPreferences.getString(key,DEFAULT_VALUE);
    }

    public void setValue(String key, String value) {
        edit.putString(key,value);
        edit.apply();
    }
}
