package develop.admin.it.formular;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ViewHistorySms extends AppCompatActivity {
    GlobalClass controller;
    ListView mylistViewDetail;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private String today,filename,phone;
    private int year_x, month_x, day_x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_history_sms );
        controller = new GlobalClass();
        sideBarMenu();
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            String nameAndSdt = bd.getString( "nameandsdt" );
            String [] resIntent = nameAndSdt.split( "," );
            phone = resIntent[0];
            if (resIntent[1] != null) {
                phone = resIntent[1];
            }
            String date = bd.getString( "date" );
            filename = date + ".xml";
            updateListViewSms();
        } else {
            Intent intent = new Intent(ViewHistorySms.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(ViewHistorySms.this, "Chưa tìm thấy dữ liệu", Toast.LENGTH_LONG).show();
        }
    }

    private void updateListViewSms() {
        File file = new File(getApplicationContext().getFilesDir(),filename);
        ArrayList<String> dataXml =  controller.readTextFileToPhone(file,phone);
        mylistViewDetail = (ListView) findViewById(R.id.mylistViewDetail);
        ViewHistorySmsAdapter viewHistorySmsAdapter = new ViewHistorySmsAdapter(ViewHistorySms.this ,dataXml);
        mylistViewDetail.setAdapter(viewHistorySmsAdapter);
    }

    public Runnable aprocAll(final int numberDate) {
        return new Runnable() {
            public void run() {
                File dir = new File( String.valueOf( getApplicationContext().getFilesDir() ) );
                File[] Files = dir.listFiles();
                if(Files != null) {
                    String notRemoveDate = "";
                    for (int q = 0; q < numberDate; q++) {
                        notRemoveDate += getApplicationContext().getFilesDir() + "/" + controller.dateDayChange("yyyy-MM-dd",24 * 60 * 60 * 1000 *q) + ".xml,";
                    }
                    int j;
                    boolean deleteAll = true;
                    for(j = 0; j < Files.length; j++) {
                        if (String.valueOf( Files[j]).indexOf(".xml") > -1 && notRemoveDate.indexOf( String.valueOf( Files[j] ) ) == -1) {
                            File myFile = new File( String.valueOf( Files[j] ) );
                            boolean deleted = myFile.delete();
                            if (!deleted) {
                                deleteAll = false;
                            }
                        }
                    }
                    if (deleteAll) {
                        Toast.makeText(ViewHistorySms.this, "Xóa thành công dữ liệu", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ViewHistorySms.this, "Dữ liệu đã xóa hết", Toast.LENGTH_LONG).show();
                    }
                    updateListViewSms();
                } else {
                    Toast.makeText(ViewHistorySms.this, "Đã Xóa Sạch.Dữ liệu Đang Trống", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    public Runnable bproc() {
        return new Runnable() {
            public void run() {

            }
        };
    }


    public void sideBarMenu() {
        /* SideBar */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                //Closing drawer on item click
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Intent intent = new Intent(ViewHistorySms.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(ViewHistorySms.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(ViewHistorySms.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(ViewHistorySms.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(ViewHistorySms.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(ViewHistorySms.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(ViewHistorySms.this, viewSmsNotMoney.class);
                        startActivity(intent7);
                        return true;
                    case R.id.historySms:
                        Intent intent8 = new Intent(ViewHistorySms.this, HistorySms.class);
                        startActivity(intent8);
                        return true;
                    default:
                        return true;
                }
            }
        });

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /* SideBar */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_sms_read, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_date :
                year_x = controller.dateInt("yyyy");
                month_x = controller.dateInt("MM") - 1;
                day_x = controller.dateInt("dd");

                DatePickerDialog datePickerDialog = new DatePickerDialog(ViewHistorySms.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, int month, int dayOfMonth) {
                        if (view.isShown()) {
                            String month_s = String.valueOf(month + 1);
                            if (month < 9) {
                                month_s = "0" + String.valueOf(month + 1);
                            }
                            String dayOfMonth_s = String.valueOf(dayOfMonth);
                            if (dayOfMonth < 10) {
                                dayOfMonth_s = "0" + String.valueOf(dayOfMonth);
                            }
                            today = String.valueOf(year) + "-" + month_s + "-"
                                    + dayOfMonth_s;//"yyyy-MM-dd"
                            filename = today + ".xml";
                            updateListViewSms();
                        }
                    }
                }, year_x, month_x, day_x);

                datePickerDialog.show();
                return true;

            case R.id.buttonDeleteFile:
                DialogHandler appdialog = new DialogHandler();
                appdialog.Confirm(ViewHistorySms.this, "Thông Báo", "Bạn có chắc xóa dữ liệu tin nhắn không phải ngày hôm nay không ?",
                        "Cancel", "OK", aprocAll(1), bproc());
                return true;

            case R.id.buttonDeleteFileThree:
                DialogHandler appdialogThree = new DialogHandler();
                appdialogThree.Confirm(ViewHistorySms.this, "Thông Báo", "Bạn có chắc xóa dữ liệu tin nhắn chỉ để lại 3 gần đây không ?",
                        "Cancel", "OK", aprocAll(3), bproc());
                return true;

            case R.id.buttonDeleteFileWeek:
                DialogHandler appdialogWeek = new DialogHandler();
                appdialogWeek.Confirm(ViewHistorySms.this, "Thông Báo", "Bạn có chắc xóa dữ liệu tin nhắnchỉ để lại 1 gần đây không ?",
                        "Cancel", "OK", aprocAll(7), bproc());
                return true;

            case R.id.buttonDeleteAllFile:
                DialogHandler appdialogAll = new DialogHandler();
                appdialogAll.Confirm(ViewHistorySms.this, "Thông Báo", "Bạn có chắc xóa tất cả dữ liệu tin nhắn không ?",
                        "Cancel", "OK", aprocAll(-1), bproc());
                return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
