package develop.admin.it.formular;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Xemtungtinnhan extends AppCompatActivity {
    String kieu, sdt, date;
    DatabaseHelper sql;
    GlobalClass controller = new GlobalClass();
    public ArrayList<String> content, smsId, kieuTin;
    ListView mylistView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private int year_x, month_x, day_x;
    TextView textViewDate, textViewSDT, textViewTen,textViewSmsIdAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new DatabaseHelper(this);
        setContentView(R.layout.activity_xemtungtinnhan);
        kieu = sdt = date = "";
        String getDays = controller.dateDay("yyyy-MM-dd");
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            kieu = bd.getString("kieu");
            sdt = bd.getString("sdt");
            date = bd.getString("date");
            getDays = bd.getString("date");
            textViewSDT = (TextView) findViewById(R.id.textViewSDT);
            textViewSDT.setText(sdt);
            String getNameQuery = "SELECT * FROM \"dongia_table\" WHERE SDT  =" + sdt + " LIMIT 1;";
            Cursor dongia_table = sql.getAllDb(getNameQuery);
            textViewTen = (TextView) findViewById(R.id.textViewTen);
            if (dongia_table.getCount() > 0) {
                dongia_table.moveToFirst();
                String ten = dongia_table.getString(dongia_table.getColumnIndex("TEN"));
                textViewTen.setText(ten);
            } else {
                textViewTen.setText(sdt);
            }
        }
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewDate.setText(getDays);
        showListSms(sdt, date);
        sideBarMenu();
    }

    private void showListSms(String sdt, String date) {
//        sql = new DatabaseHelper(this);
        String getIdQuery = "SELECT SMSID FROM solieu_table WHERE SDT=\"" + sdt + "\" AND NGAY=\"" + date + "\";";
        Cursor solieu_table = sql.getAllDb(getIdQuery);
        String GETSMSID = "";
        if (solieu_table.getCount() > 0) {
            while (solieu_table.moveToNext()) {
                if (!GETSMSID.equals("")) {
                    GETSMSID += ',' + solieu_table.getString(solieu_table.getColumnIndex("SMSID"));
                } else {
                    GETSMSID += solieu_table.getString(solieu_table.getColumnIndex("SMSID"));
                }
            }
        }
        String importQuery =
                "SELECT " +
                        "sms_ready_table.ID AS READY_ID," +
                        "sms_ready_table.SMSID AS READY_SMSID," +
                        "sms_ready_table.CONTENT AS READY_CONTENT," +
                        "tiendanh_de.TIENDANHDE AS TIENDANHDE," +
                        "tiendanh_de.TIENTHUONGDE AS TIENTHUONGDE," +
                        "tiendanh_dedau.TIENDANHDEDAU AS TIENDANHDEDAU," +
                        "tiendanh_dedau.TIENTHUONGDEDAU AS TIENTHUONGDEDAU," +
                        "tiendanh_lo.TIENDANHLO AS TIENDANHLO," +
                        "tiendanh_lo.TIENTHUONGLO AS TIENTHUONGLO," +
                        "tiendanh_xien.TIENDANHXIEN AS TIENDANHXIEN," +
                        "tiendanh_xien.TIENTHUONGXIEN AS TIENTHUONGXIEN," +
                        "tiendanh_bacang.TIENDANHBC AS TIENDANHBACANG," +
                        "tiendanh_bacang.TIENTHUONGBC AS TIENTHUONGBACANG," +
                        "kieu_choi.KIEU AS KIEU " +
                        "FROM sms_ready_table " +
                        "LEFT JOIN (" +
                        "SELECT SMSID,NGAY,KIHIEU," +
                        "sum(solieu_table.TIENDANHSMS) AS TIENDANHDE," +
                        "sum(solieu_table.TRUNGSMS) AS TIENTHUONGDE " +
                        "FROM solieu_table " +
                        "WHERE solieu_table.NGAY = '" + date + "' AND solieu_table.KIHIEU = 'de' " +
                        "GROUP BY solieu_table.SMSID" +
                        ") tiendanh_de ON (tiendanh_de.SMSID = sms_ready_table.SMSID) " +
                        "LEFT JOIN (" +
                        "SELECT SMSID,NGAY,KIHIEU," +
                        "sum(solieu_table.TIENDANHSMS) AS TIENDANHDEDAU," +
                        "sum(solieu_table.TRUNGSMS) AS TIENTHUONGDEDAU " +
                        "FROM solieu_table " +
                        "WHERE solieu_table.NGAY = '" + date + "' AND solieu_table.KIHIEU = 'dq' " +
                        "GROUP BY solieu_table.SMSID" +
                        ") tiendanh_dedau ON (tiendanh_dedau.SMSID = sms_ready_table.SMSID) " +
                        "LEFT JOIN (" +
                        "SELECT SMSID,NGAY,KIHIEU," +
                        "sum(solieu_table.TIENDANHSMS) AS TIENDANHLO," +
                        "sum(solieu_table.TRUNGSMS) AS TIENTHUONGLO " +
                        "FROM solieu_table " +
                        "WHERE solieu_table.NGAY = '" + date + "' AND solieu_table.KIHIEU = 'lo' " +
                        "GROUP BY solieu_table.SMSID" +
                        ") tiendanh_lo ON (tiendanh_lo.SMSID = sms_ready_table.SMSID)" +
                        "LEFT JOIN (" +
                        "SELECT SMSID,NGAY,KIHIEU," +
                        "sum(solieu_table.TIENDANHSMS) AS TIENDANHXIEN," +
                        "sum(solieu_table.TRUNGSMS) AS TIENTHUONGXIEN " +
                        "FROM solieu_table " +
                        "WHERE solieu_table.NGAY = '" + date + "' AND solieu_table.KIHIEU IN ('xien','xien2','xien3','xien4') " +
                        "GROUP BY solieu_table.SMSID" +
                        ") tiendanh_xien ON (tiendanh_xien.SMSID = sms_ready_table.SMSID)" +
                        "LEFT JOIN (" +
                        "SELECT SMSID,NGAY,KIHIEU," +
                        "sum(solieu_table.TIENDANHSMS) AS TIENDANHBC," +
                        "sum(solieu_table.TRUNGSMS) AS TIENTHUONGBC " +
                        "FROM solieu_table " +
                        "WHERE solieu_table.NGAY = '" + date + "' AND solieu_table.KIHIEU = 'bacang' " +
                        "GROUP BY solieu_table.SMSID" +
                        ") tiendanh_bacang ON (tiendanh_bacang.SMSID = sms_ready_table.SMSID)" +
                        "LEFT JOIN (" +
                        "SELECT SMSID,NGAY,KIEU," +
                        "sum(solieu_table.TIENDANHSMS) AS TIENDANHBC," +
                        "sum(solieu_table.TRUNGSMS) AS TIENTHUONGBC " +
                        "FROM solieu_table " +
                        "WHERE solieu_table.NGAY = '" + date + "' " +
                        "GROUP BY solieu_table.SMSID" +
                        ") kieu_choi ON (kieu_choi.SMSID = sms_ready_table.SMSID) " +
                        "WHERE sms_ready_table.CONTENT != '' AND sms_ready_table.NGAY = '" + date + "' AND sms_ready_table.SMSID IN (" + GETSMSID + ") " +
                        "GROUP BY sms_ready_table.ID ";
        Cursor smsReady = sql.getAllDb(importQuery);
        content = new ArrayList<>();
        smsId = new ArrayList<>();
        kieuTin = new ArrayList<>();
        String smsIdAll = "";
        if (smsReady.getCount() > 0) {
            int i = 0;
            while (smsReady.moveToNext()) {
                i++;
                String ketqua = "";
                String viewContent = String.valueOf(i) +
                        ".<font color=\"Blue\">" +
                        smsReady.getString(smsReady.getColumnIndex("READY_CONTENT"))
                        + "</font><br />";
                String tiende = "0";
                String thuongde = "0";
                String tiendedau = "0";
                String thuongdedau = "0";
                String tienlo = "0";
                String thuonglo = "0";
                String tienxien = "0";
                String thuongxien = "0";
                String tienbacang = "0";
                String thuongbacang = "0";
                if (smsReady.getString(smsReady.getColumnIndex("TIENDANHDE")) != null &&
                        !smsReady.getString(smsReady.getColumnIndex("TIENDANHDE")).isEmpty()) {
                    tiende = smsReady.getString(smsReady.getColumnIndex("TIENDANHDE"));
                }
                if (smsReady.getString(smsReady.getColumnIndex("TIENTHUONGDE")) != null &&
                        !smsReady.getString(smsReady.getColumnIndex("TIENTHUONGDE")).isEmpty()) {
                    thuongde = smsReady.getString(smsReady.getColumnIndex("TIENTHUONGDE"));
                }
                if (!tiende.equals("0") || !thuongde.equals("0")) {
                    ketqua += "Đề : " + tiende + "/" + thuongde + "<br />";
                }

                if (smsReady.getString(smsReady.getColumnIndex("TIENDANHDEDAU")) != null &&
                        !smsReady.getString(smsReady.getColumnIndex("TIENDANHDEDAU")).isEmpty()) {
                    tiendedau = smsReady.getString(smsReady.getColumnIndex("TIENDANHDEDAU"));
                }
                if (smsReady.getString(smsReady.getColumnIndex("TIENTHUONGDEDAU")) != null &&
                        !smsReady.getString(smsReady.getColumnIndex("TIENTHUONGDEDAU")).isEmpty()) {
                    thuongdedau = smsReady.getString(smsReady.getColumnIndex("TIENTHUONGDEDAU"));
                }
                if (!tiendedau.equals("0") || !thuongdedau.equals("0")) {
                    ketqua += "Đề Đầu : " + tiendedau + "/" + thuongdedau + "<br />";
                }

                if (smsReady.getString(smsReady.getColumnIndex("TIENDANHLO")) != null &&
                        !smsReady.getString(smsReady.getColumnIndex("TIENDANHLO")).isEmpty()) {
                    tienlo = smsReady.getString(smsReady.getColumnIndex("TIENDANHLO"));
                }
                if (smsReady.getString(smsReady.getColumnIndex("TIENTHUONGLO")) != null &&
                        !smsReady.getString(smsReady.getColumnIndex("TIENTHUONGLO")).isEmpty()) {
                    thuonglo = smsReady.getString(smsReady.getColumnIndex("TIENTHUONGLO"));
                }
                if (!tienlo.equals("0") || !thuonglo.equals("0")) {
                    ketqua += "Lô : " + tienlo + "/" + thuonglo + "<br />";
                }
                if (smsReady.getString(smsReady.getColumnIndex("TIENDANHXIEN")) != null &&
                        !smsReady.getString(smsReady.getColumnIndex("TIENDANHXIEN")).isEmpty()) {
                    tienxien = smsReady.getString(smsReady.getColumnIndex("TIENDANHXIEN"));
                }
                if (smsReady.getString(smsReady.getColumnIndex("TIENTHUONGXIEN")) != null &&
                        !smsReady.getString(smsReady.getColumnIndex("TIENTHUONGXIEN")).isEmpty()) {
                    thuongxien = smsReady.getString(smsReady.getColumnIndex("TIENTHUONGXIEN"));
                }
                if (!tienxien.equals("0") || !thuongxien.equals("0")) {
                    ketqua += "Xiên : " + tienxien + "/" + thuongxien + "<br />";
                }
                if (smsReady.getString(smsReady.getColumnIndex("TIENDANHBACANG")) != null &&
                        !smsReady.getString(smsReady.getColumnIndex("TIENDANHBACANG")).isEmpty()) {
                    tienbacang = smsReady.getString(smsReady.getColumnIndex("TIENDANHBACANG"));
                }
                if (smsReady.getString(smsReady.getColumnIndex("TIENTHUONGBACANG")) != null &&
                        !smsReady.getString(smsReady.getColumnIndex("TIENTHUONGBACANG")).isEmpty()) {
                    thuongbacang = smsReady.getString(smsReady.getColumnIndex("TIENTHUONGBACANG"));
                }
                if (!tienbacang.equals("0") || !thuongbacang.equals("0")) {
                    ketqua += "Ba Càng : " + tienbacang + "/" + thuongbacang + "<br />";
                }
                viewContent += "<font color=\"RED\">" + ketqua + "</font>";
                content.add(viewContent);
                String smsIdGet = smsReady.getString(smsReady.getColumnIndex("READY_SMSID"));
                smsId.add(smsIdGet);
                kieuTin.add(smsReady.getString(smsReady.getColumnIndex("KIEU")));
                if (!smsIdAll.equals("")) {
                    smsIdAll += "," + smsIdGet;
                } else {
                    smsIdAll += smsIdGet;
                }
            }
        }
        textViewSmsIdAll = (TextView) findViewById(R.id.textViewSmsIdAll);
        textViewSmsIdAll.setText(smsIdAll);
        mylistView = (ListView) findViewById(R.id.mylistView);
        tungtinAdapter viewSmsadapter = new tungtinAdapter(this, content, smsId, kieuTin);
        mylistView.setAdapter(viewSmsadapter);
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
                        Intent intent = new Intent(Xemtungtinnhan.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(Xemtungtinnhan.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(Xemtungtinnhan.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(Xemtungtinnhan.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(Xemtungtinnhan.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(Xemtungtinnhan.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(Xemtungtinnhan.this, viewSmsNotMoney.class);
                        startActivity(intent7);
                        return true;
                    case R.id.historySms:
                        Intent intent8 = new Intent(Xemtungtinnhan.this, HistorySms.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_show_dialog:
                year_x = controller.dateInt("yyyy");
                month_x = controller.dateInt("MM") - 1;
                day_x = controller.dateInt("dd");

                DatePickerDialog datePickerDialog = new DatePickerDialog(Xemtungtinnhan.this, new DatePickerDialog.OnDateSetListener() {
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
                            final String getDays = String.valueOf(year) + "-" + month_s + "-"
                                    + dayOfMonth_s;//"yyyy-MM-dd"
                            textViewDate = (TextView) findViewById(R.id.textViewDate);
                            textViewDate.setText(getDays);
                            showListSms(sdt, getDays);
                        }
                    }
                }, year_x, month_x, day_x);
                datePickerDialog.show();
                return true;
            case R.id.deleteAll:
                DialogHandler appdialog = new DialogHandler();
                appdialog.Confirm(this, "Thông Báo", "Bạn có chắc xóa tất cả kết quả không?",
                        "Cancel", "OK", aproc(), bproc());
                return true;
            case R.id.deleteSelect:
                DialogHandler appdialog2 = new DialogHandler();
                appdialog2.Confirm(this, "Thông Báo", "Bạn có chắc xóa các danh mục kết quả đã chọn không?",
                        "Cancel", "OK", aprocCheckBoxDelete(), bproc());
                return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_sms, menu);
        return true;
    }

    public Runnable aproc() {
        return new Runnable() {
            public void run() {
                sql = new DatabaseHelper(Xemtungtinnhan.this);
                Log.d("LogFile", "ban da nhan nut yes xoa all tin nhan");
                textViewSmsIdAll = (TextView) findViewById(R.id.textViewSmsIdAll);
                String allSmsID = textViewSmsIdAll.getText().toString();
                String table5 = sql.TABLE_NAME_5;
                String table6 = sql.TABLE_NAME_6;
                sql.deleteCongNo(table5, allSmsID);
                sql.deleteCongNo(table6, allSmsID);
                showListSms(sdt, date);
            }
        };
    }

    public Runnable aprocCheckBoxDelete() {
        return new Runnable() {
            public void run() {
                Log.d("LogFile", "Ban da nhan nut Yes Select");
                mylistView = (ListView) findViewById(R.id.mylistView);
                sql = new DatabaseHelper(Xemtungtinnhan.this);
                String table5 = sql.TABLE_NAME_5;
                String table6 = sql.TABLE_NAME_6;
                for (int i = 0; i < mylistView.getChildCount(); i++) {
                    View v = mylistView.getChildAt(i);
                    CheckBox ch = (CheckBox) v.findViewById(R.id.checkBoxSms);
                    if (ch.isChecked()) {
                        sql.deleteCongNo(table5, ch.getText().toString());
                        sql.deleteCongNo(table6, ch.getText().toString());
                    }
                }
                showListSms(sdt, date);
            }
        };
    }


    public Runnable bproc() {
        return new Runnable() {
            public void run() {
                Log.d("LogFile", "Ban da nhan nut No");
            }
        };
    }

}
