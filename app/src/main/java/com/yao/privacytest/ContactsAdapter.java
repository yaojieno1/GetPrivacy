package com.yao.privacytest;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ContactsAdapter implements  Adapter{

    final private static String TAG = "ContactsAdapter";
    private static Context context = null;

    ContactsAdapter(Context context) {
        ContactsAdapter.context = context;
    }
    protected void finalize(){
        ContactsAdapter.context = null;
    }

    public static String[] getContactsList() {
        List<String> list = new ArrayList<String>();
        try {
            Uri uri = Uri.parse("content://com.android.contacts/contacts");
            ContentResolver resolver = context.getContentResolver();
            Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
            while (cursor.moveToNext()) {
                int contractID = cursor.getInt(0);
                StringBuilder sb = new StringBuilder(); //("contractID=");
                //sb.append(contractID);
                uri = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
                Cursor cursor1 = resolver.query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
                String name=null, phone=null;
                while (cursor1.moveToNext()) {
                    String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
                    String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));
                    if ("vnd.android.cursor.item/name".equals(mimeType)) { //是姓名
                        name = data1;/*
                    } else if ("vnd.android.cursor.item/email_v2".equals(mimeType)) { //邮箱
                        sb.append(",email=" + data1); */
                    } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { //手机
                        phone= data1.replace(" ", "");
                    }
                }
                sb.append(name + "\t\t" + phone);
                cursor1.close();
                Log.i(TAG, sb.toString());
                list.add(sb.toString());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            String x = e.getLocalizedMessage();
            x = x.substring(x.lastIndexOf("requires"));
            list.add(x);
        }
        return list.toArray(new String[list.size()]);
    }

    public static String[] getContactsDenied() {
        List<String> error = new ArrayList<String>();
        error.add("no permission to read contacts");
        return error.toArray(new String[error.size()]);
    }
}
