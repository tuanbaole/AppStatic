package develop.admin.it.formular;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordActivity extends AppCompatActivity {
    GlobalClass controller = new GlobalClass();
    Button changePassword,back;
    EditText lastPassword, newPassword, confirmPassword;
    DatabaseHelper sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        sql = new DatabaseHelper(this);
        changePassword = (Button) findViewById(R.id.buttonChangePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPassword = (EditText) findViewById(R.id.editTextPasswordLast);
                String matkhaucu = lastPassword.getText().toString();
                newPassword = (EditText) findViewById(R.id.editTextPasswordNew);
                String matkhaumoi = newPassword.getText().toString();
                confirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
                String matkhauxacthuc = confirmPassword.getText().toString();
                if (matkhaucu.length() > 0 && matkhaumoi.length() > 0 && matkhauxacthuc.length() > 0) {
                    Cursor users = sql.getAllDb("SELECT * FROM user_table WHERE 1 ORDER BY ID DESC");
                    if (users.getCount() > 0) {
                        users.moveToFirst();
                        String validatePassword = users.getString(users.getColumnIndex("PASSWORD"));
                        String userID = users.getString(users.getColumnIndex("ID"));
                        if (matkhaucu.equals(validatePassword)) {
                            if (matkhaumoi.equals(matkhauxacthuc)) {
                                boolean resultUser = sql.updateUser(userID,matkhaumoi);
                                if (resultUser) {
                                    DialogHandler appdialog = new DialogHandler();
                                    appdialog.Confirm(ChangePasswordActivity.this, "Thông Báo", "Đổi mật khẩu thành công\nBạn có muốn quay lại đăng nhập không ?",
                                            "Cancel", "OK", aproc(), bproc());
                                } else {
                                    controller.showAlertDialog(ChangePasswordActivity.this, "Thông Báo", "Đổi mật khẩu không thành công\nLàm ơn thử lại!");
                                }
                            } else {
                                controller.showAlertDialog(ChangePasswordActivity.this, "Thông Báo", "Mật khẩu xác thực không chính xác");
                            }
                        } else {
                            controller.showAlertDialog(ChangePasswordActivity.this, "Thông Báo", "Mật khẩu cũ không chính xác");
                        }
                    } else {
                        controller.showAlertDialog(ChangePasswordActivity.this, "Thông Báo", "Mật khẩu cũ không chính xác\nLàm ơn liên hệ với chúng tôi!");
                    }
                } else {
                    controller.showAlertDialog(ChangePasswordActivity.this, "Thông Báo", "Vui lòng điền đầy đủ thông tin!");
                }

            }
        });

        back = (Button) findViewById(R.id.buttonBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    public Runnable aproc() {
        return new Runnable() {
            public void run() {
                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        };
    }

    public Runnable bproc() {
        return new Runnable() {
            public void run() {
            }
        };
    }
}
