package develop.admin.it.formular;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import static android.app.Service.START_REDELIVER_INTENT;

public class DetailSms extends AppCompatActivity implements IB {
    BroadcastReceiver receiver = null;
    TextView lbl;
    GlobalClass controller = new GlobalClass();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sms);
        //tạo bộ lọc để lắng nghe tin nhắn gửi tới
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        //tạo bộ lắng nghe
        receiver = new BroadcastReceiver() {
            //chú ý là dữ liệu trong tin nhắn được lưu trữ trong arg1
            @Override
            public void onReceive(Context context, Intent intent) {
                processReceive(context, intent);
            }
        };
        //đăng ký bộ lắng nghe vào hệ thống
        registerReceiver(receiver, filter);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    protected void onDestroy() {
        super.onDestroy();
//        hủy bỏ đăng ký khi tắt ứng dụng
        unregisterReceiver(receiver);
    }

    public void processReceive(Context context, Intent intent) {
        //Thông báo khi có tin nhắn mới
        Toast.makeText(context, "Có 1 tin nhắn mới", Toast.LENGTH_LONG).show();
        TextView txtContent = (TextView) findViewById(R.id.textView16);
        //pdus để lấy gói tin nhắn
        String sms_extra = "pdus";
        Bundle bundle = intent.getExtras();
        //bundle trả về tập các tin nhắn gửi về cùng lúc
        Object[] objArr = (Object[]) bundle.get(sms_extra);
        String sms = "";
        //duyệt vòng lặp để đọc từng tin nhắn
        for (int i = 0; i < objArr.length; i++) {
            //lệnh chuyển đổi về tin nhắn createFromPdu
            SmsMessage smsMsg = SmsMessage.
                    createFromPdu((byte[]) objArr[i]);
            //lấy nội dung tin nhắn
            String body = smsMsg.getMessageBody();
            //lấy số điện thoại tin nhắn
            String address = smsMsg.getDisplayOriginatingAddress();
            sms += address + ":\n" + body + "\n";
        }
        //hiển thị lên giao diện
        txtContent.setText(sms);
        updateKqsx();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    class ReadXml extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String kq = controller.getXMLFromUrl(params[0]);
            return kq;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.indexOf("false") == -1) {
            }
        }
    }

    public void updateKqsx() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lbl = (TextView) findViewById(R.id.textView16);
                    String smsText = lbl.getText().toString();
                    Toast.makeText(DetailSms.this, smsText, Toast.LENGTH_LONG).show();
                    String link = "http://hostingkqxs.esy.es/serversms.php?smstext=" + smsText;
                    new ReadXml().execute(link);
                }
            });
        } else {
            controller.showAlertDialog(DetailSms.this, "Thông Báo", "Không kết nối được internet");
        }
    }

}