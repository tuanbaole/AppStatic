package develop.admin.it.formular;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import static develop.admin.it.formular.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper sql;
    GlobalClass controller;
    ImageButton ketquasoxo, contact, sms, boso, managerMoney,
            statistic, setting, viewSms, checkNewSms, dataSms,kqxstn;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        sql = new DatabaseHelper(this);
        controller = new GlobalClass();
        ketquasoxo = (ImageButton) findViewById(R.id.imageButtonKetQuaSoXo);
        ketquasoxo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KetQuaSoXo.class);
                startActivity(intent);
            }
        });

        kqxstn = (ImageButton) findViewById(R.id.kqxstn);
        kqxstn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, KqxstnActivity.class);
                startActivity(intent);
            }
        });

        contact = (ImageButton) findViewById(R.id.imageButtonContact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Contact.class);
                startActivity(intent);
            }
        });

        sms = (ImageButton) findViewById(R.id.imageButtonSms);
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Message.class);
                intent.putExtra("checksms", "0");
                startActivity(intent);
            }
        });

        boso = (ImageButton) findViewById(R.id.imageButtonBoSo);
        boso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Format.class);
                startActivity(intent);
            }
        });

        managerMoney = (ImageButton) findViewById(R.id.imageButtonManger);
        managerMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManagerMoney.class);
                startActivity(intent);
            }
        });

        statistic = (ImageButton) findViewById(R.id.imageStatistic);
        statistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Statistic.class);
                startActivity(intent);
            }
        });

        setting = (ImageButton) findViewById(R.id.imageButtonSetting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Setting.class);
                startActivity(intent);
            }
        });

        checkNewSms = (ImageButton) findViewById(R.id.imageButtonCheckSms);
        checkNewSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Message.class);
                intent.putExtra("checksms", "1");
                startActivity(intent);
            }
        });

//        Intent intent = new Intent(this, StartService.class);
//        startService(intent);

        sideBarMenu();
        deleteData();
        insertTableTime();
    }

    private void deleteData() {
        String table_kq = sql.TABLE_NAME_1;
        String table_solieu = sql.TABLE_NAME_5;
        String table_manager = sql.TABLE_NAME_6;
        String table_sms_ready = sql.TABLE_NAME_7;
        int time = 60 * 60 * 24 * 7 * 1000;
        String lastWeek = controller.dateDayChange("yyyy-MM-dd", time);
        int res1 = sql.deleteAllNgayLast(table_kq, lastWeek);
        int res2 = sql.deleteAllNgayLast(table_solieu, lastWeek);
        int res3 = sql.deleteAllNgayLast(table_manager, lastWeek);
        int res4 = sql.deleteAllNgayLast(table_sms_ready, lastWeek);
    }

    private void insertTableTime() {
        Cursor timeTable = sql.getAllDb("SELECT * FROM time_table");
        if (timeTable.getCount() == 0) {
            int time = 60 * 60 * 24 * 1000;
            String getDays = controller.dateDayChange("yyyy-MM-dd", time);
            sql.insertDataTime("23:59", "23:59", getDays);
            controller = new GlobalClass();
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        String getSimSerialNumber = telemamanger.getSimSerialNumber();
                        String link = "http://hostingkqxs.esy.es/lockApp.php?iccid=" + getSimSerialNumber;
                        new ReadXml().execute(link);
                    }
                });
            } else {
                controller.showAlertDialog(MainActivity.this, "Thông Báo", "Không kết nối được internet");
            }
        }
    }

    class ReadXml extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String kq = controller.getXMLFromUrl(params[0]);
            return kq;
        }

        @Override
        protected void onPostExecute(String s) {

        }

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
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(MainActivity.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(MainActivity.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(MainActivity.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(MainActivity.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(MainActivity.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(MainActivity.this, viewSmsNotMoney.class);
                        startActivity(intent7);
                        return true;
                    case R.id.historySms:
                        Intent intent8 = new Intent(MainActivity.this, HistorySms.class);
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
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_server_sms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.serverSms:
                Intent intent = new Intent(MainActivity.this, DetailSms.class);
                startActivity(intent);
                return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
