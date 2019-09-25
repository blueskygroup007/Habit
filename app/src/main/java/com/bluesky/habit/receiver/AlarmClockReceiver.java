package com.bluesky.habit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bluesky.habit.service.ForegroundService;
import com.bluesky.habit.util.LogUtils;

import static com.bluesky.habit.data.Habit.HABIT_ID;
import static com.bluesky.habit.service.ForegroundService.ACTION_TIMEUP;


public class AlarmClockReceiver extends BroadcastReceiver {

    public static final String TAG = AlarmClockReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        LogUtils.e(TAG, "AlarmClockReceiver接收到了广播");
/*        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("提示");
        dialogBuilder.setMessage("这是在BroadcastReceiver弹出的对话框。");
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlarmUtils.setAlarm(context, AlarmClockReceiver.class, 10 * 1000);
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();*/

        String id = intent.getStringExtra(HABIT_ID);

        //发送给ForegroundService
        Intent actIntent = new Intent();
        actIntent.setClass(context, ForegroundService.class);
        actIntent.setAction(ACTION_TIMEUP);
        actIntent.putExtra(HABIT_ID, id);
        context.startService(actIntent);

        //发送给AlertDialogActivity
/*        Intent actIntent = new Intent();
        actIntent.setClass(context, AlertDialogActivity.class);
        actIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        actIntent.putExtra(HABIT_ID, id);
        context.startActivity(actIntent);*/

    }

}
