package develop.admin.it.formular;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    Button login,changePassword,forgetPassword;
    EditText matkhau;
    private String pass = "123456";
    DatabaseHelper sql;
    GlobalClass controller = new GlobalClass();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sql = new DatabaseHelper(this);
        Cursor users = sql.getAllDb("SELECT * FROM user_table WHERE 1 ORDER BY ID DESC");
        if (users.getCount() == 0) {
            sql.insertUser("123456");
        } else {
            users.moveToFirst();
            pass = users.getString(users.getColumnIndex("PASSWORD"));
        }
        login = (Button) findViewById(R.id.buttonLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matkhau = (EditText) findViewById(R.id.editPassword);
                String password = matkhau.getText().toString();
                if (password.equals(pass)) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    controller.showAlertDialog(LoginActivity.this, "Thông Báo", "Mật khẩu không đúng.\nHãy thử lại!");
                }
            }
        });

        changePassword = (Button) findViewById(R.id.buttonChangePass);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        forgetPassword = (Button) findViewById(R.id.buttonForgetPass);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri mSmsinboxQueryUri = Uri.parse("content://sms/");
                String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
                String filter = "Body LIKE '%matkhau:%'";
                Cursor cursor1 = getContentResolver().query(mSmsinboxQueryUri, projection, filter, null, "_id desc");
                if (cursor1.getCount() > 0) {
                    cursor1.moveToFirst();
                    String body = cursor1.getString(cursor1.getColumnIndex("Body")).replace("matkhau:","").replace(" ","").trim();
                    Cursor users = sql.getAllDb("SELECT * FROM user_table WHERE 1 ORDER BY ID DESC");
                    boolean resultUser = false;
                    if (users.getCount() == 1) {
                        users.moveToFirst();
                        String userID = users.getString(users.getColumnIndex("ID"));
                        resultUser = sql.updateUser(userID, body);
                    } else {
                        String user_table = sql.TABLE_NAME_10;
                        sql.deleteAll(user_table,"0");
                        resultUser = sql.insertUser(body);
                    }
                    if (resultUser) {
                        pass = body;
                        controller.showAlertDialog(LoginActivity.this, "Thông Báo", "Mật khẩu mới là\n"+ body +"\nHãy đăng nhập!");
                    } else {
                        controller.showAlertDialog(LoginActivity.this, "Thông Báo", "Lấy mật khẩu mới không thành công!");
                    }

                } else {
                    controller.showAlertDialog(LoginActivity.this, "Thông Báo", "Chưa có mật khẩu để thay thế.\nHãy thử lại!");
                }
            }
        });
//        countDownLogOut();
    }

//    public void countDownLogOut() {
//        CountDownTimer timer = new CountDownTimer(60 * 60 * 1000, 600000) {
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            public void onFinish() {
//                Intent intent7 = new Intent(LoginActivity.this, LoginActivity.class);
//                startActivity(intent7);
//            }
//        };
//        timer.start();
//    }

}
