package develop.admin.it.formular;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class CutSmsAuto extends AppCompatActivity {

    DatabaseHelper sql;
    GlobalClass controller = new GlobalClass();
    CountDownTimer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_sms_auto);
//        long start = 24 * 60 * 60;
//        long end = 5;
//        timer = new CountDownTimer(start * 1000, end * 1000) {
//            public void onTick(long millisUntilFinished) {
//                Uri mSmsinboxQueryUri = Uri.parse("content://sms/");
//                String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
//                Cursor cursor1 = getContentResolver().query(mSmsinboxQueryUri, projection, null, null, "_id desc");
//                if (cursor1.getCount() > 0) {
//                    cursor1.moveToFirst();
//                    String body = cursor1.getString(cursor1.getColumnIndex("Body"));
//                    Toast.makeText(
//                            CutSmsAuto.this,
//                            body,
//                            Toast.LENGTH_SHORT
//                    ).show();
//                }
//            }
//            public void onFinish() {
//                Toast.makeText(
//                        CutSmsAuto.this,
//                        "done! ",
//                        Toast.LENGTH_SHORT
//                ).show();
//            }
//        };
//        timer.start();
    }

    private int countSms() {
        Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cursor1 = getContentResolver().query(mSmsinboxQueryUri, projection, null, null, "_id desc");
        return cursor1.getCount();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cân bằng bảng tự động");
        builder.setMessage("Bạn có muốn dừng lại việc cân bằng số tự động không ");
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                timer.cancel();
                CutSmsAuto.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Tiếp tục", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.show();
    }

}
