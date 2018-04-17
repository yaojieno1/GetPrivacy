package com.yao.privacytest;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmsAdapter implements Adapter {

    final private static String TAG = "ContactsAdapter";
    private static Context context = null;

    SmsAdapter(Context context) {
        SmsAdapter.context = context;
    }
    protected void finalize(){
        SmsAdapter.context = null;
    }

    public static String[] getSmsList() {
        final String SMS_URI_ALL = "content://sms/";
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_SEND = "content://sms/sent";
        final String SMS_URI_DRAFT = "content://sms/draft";
        final String SMS_URI_OUTBOX = "content://sms/outbox";
        final String SMS_URI_FAILED = "content://sms/failed";
        final String SMS_URI_QUEUED = "content://sms/queued";

        StringBuilder smsBuilder = new StringBuilder();
        List<String> list = new ArrayList<String>();

        try {
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
            Cursor cur = context.getContentResolver().query(uri, projection, null, null, "date desc");      // 获取手机内部短信

            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");
                int count = 0;

                do {
                    String strAddress = cur.getString(index_Address);
                    int intPerson = cur.getInt(index_Person);
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
                    int intType = cur.getInt(index_Type);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date d = new Date(longDate);
                    String strDate = dateFormat.format(d);

                    String strType = "";
                    if (intType == 1) {
                        strType = "接收";
                    } else if (intType == 2) {
                        strType = "发送";
                    } else {
                        strType = "null";
                    }

                    smsBuilder.append("[ ");
                    smsBuilder.append(strAddress + ", ");
                    smsBuilder.append(intPerson + ", ");
                    smsBuilder.append(strbody + ", ");
                    smsBuilder.append(strDate + ", ");
                    smsBuilder.append(strType);
                    smsBuilder.append(" ]");
                    list.add(smsBuilder.toString());
                    smsBuilder = new StringBuilder();
                    count ++;
                    if (count >= 20)
                        break;
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                list.add("no sms found!");
            } // end if
       } catch (Exception ex) {
            Log.e(TAG, "[Exception][getSms]" + ex.getMessage());
            Log.e(TAG, "[Exception]" +ex.getCause());
            list.add("[Exception]" + ex.getLocalizedMessage());
        }

        return list.toArray(new String[list.size()]);
    }


    public static String[] getSmsDenied() {
        List<String> error = new ArrayList<String>();
        error.add("no permission to read sms");
        return error.toArray(new String[error.size()]);
    }
}
