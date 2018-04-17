package com.yao.privacytest;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallLogAdapter implements  Adapter{

    final private static String TAG = "CallLogAdapter";
    private static Context context = null;
    //private static AsyncQueryHandler asyncQuery;
    private List<CallLogBean> callLogs;

    CallLogAdapter(Context context) {
        CallLogAdapter.context = context;
    }

    protected void finalize(){
        CallLogAdapter.context = null;
    }

    private static String getTypeStr(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                return "INCOMING ";
            case CallLog.Calls.OUTGOING_TYPE:
                return "OUTGOING ";
            case CallLog.Calls.MISSED_TYPE:
                return "MISSED   ";
            case CallLog.Calls.REJECTED_TYPE:
                return "REJECTED ";
            default:
                return "UNKNOWN" + type + " ";
        }
    }

    public static String[] getCallLogList() {
        List<String> list = new ArrayList<String>();
        try {
            Uri uri = android.provider.CallLog.Calls.CONTENT_URI;
            ContentResolver resolver = context.getContentResolver();
            // 查询的列
            String[] projection = { CallLog.Calls.DATE, // 日期
                    CallLog.Calls.NUMBER, // 号码
                    CallLog.Calls.TYPE, // 类型
                    CallLog.Calls.CACHED_NAME, // 名字
                    CallLog.Calls._ID, // id
            };
            final Cursor cursor = resolver.query(uri,
                    new String[] {
                            CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
                            CallLog.Calls.TYPE, CallLog.Calls.DATE },
                    null,
                    null,
                    CallLog.Calls.DEFAULT_SORT_ORDER);

            for (int i = 0; i < cursor.getCount(); i++) {
                String name="", phone="";
                int type=0;
                cursor.moveToPosition(i);
                name = cursor.getString(0);
                type = cursor.getInt(2);
                StringBuilder sb = new StringBuilder("");
                sb.append(getTypeStr(type) + "\t\t" + name + "\t\t" + phone);
                list.add(sb.toString());
            } //取得值
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            String x = e.getLocalizedMessage();
            x = x.substring(x.lastIndexOf("requires"));
            list.add(x);
        }
        return list.toArray(new String[list.size()]);
    }

    public static String[] getCallLogDenied() {
        List<String> error = new ArrayList<String>();
        error.add("no permission to read contacts");
        return error.toArray(new String[error.size()]);
    }

    public class CallLogBean {

        private int id;
        private String name; // 名称
        private String number; // 号码
        private String date; // 日期
        private int type; // 来电:1，拨出:2,未接:3
        private int count; // 通话次数

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

    }
}
