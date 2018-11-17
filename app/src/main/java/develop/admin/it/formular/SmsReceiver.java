package develop.admin.it.formular;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;

/**
 * Created by IT-PC on 11/11/2018.
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//        if(!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//            return;
//        }
        return;
//            Bundle extras = intent.getExtras();
//            String receivedMessage = "";
//            String MSG_TYPE = intent.getAction();
//
//            Object[] pdus = (Object[]) extras.get("pdus");
//
//            for (int i = 0; i < pdus.length; i++) {
//                SmsMessage SMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                receivedMessage += SMessage.getMessageBody().toString();
//            }
//            String phone = SmsMessage.createFromPdu((byte[]) pdus[0]).getOriginatingAddress();
//            long time = SmsMessage.createFromPdu((byte[]) pdus[0]).getTimestampMillis();
//        Toast.makeText(context, "dang lang nghe", Toast.LENGTH_LONG).show();
    }

    private String converTime(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss  dd/MM/yyyy");
        return dateFormat.format(time);
    }

}
