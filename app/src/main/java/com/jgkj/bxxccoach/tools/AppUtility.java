package com.jgkj.bxxccoach.tools;

import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jgkj.bxxccoach.R;

public class AppUtility {

    @SuppressWarnings("unused")
    private static final String TAG = "AppUtility";
    private static Context context;
    //private static Toast toast;
    private static Toast toast;
    public static void setContext(Application app) {
        context = app.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static void showToastMsg(String msg) {
        //if (toast == null) {
        //	toast = new Toast(context);
        //}
        if(null==toast) {
            toast = new Toast(context);
        }
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout v = (LinearLayout) inflate.inflate(R.layout.view_custom_toast, null);
        v.getBackground().setAlpha(125);
        TextView tvMessage = (TextView) v.findViewById(R.id.tv_message);
        tvMessage.setText(msg);
        toast.setView(v);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
