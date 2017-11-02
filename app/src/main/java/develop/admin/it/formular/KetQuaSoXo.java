package develop.admin.it.formular;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KetQuaSoXo extends AppCompatActivity {
    TableLayout table;
    TextView textViewKetQua;
    DatabaseHelper sql;
    GlobalClass controller = new GlobalClass();

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    private static final String LogFile = "LogFile";
    private int year_x, month_x, day_x;
    /* class nay co chuc nang
        * lay ket qua online
        * luc nao cung phai bat mang
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ket_qua_so_xo);
        sideBarMenu();
        sql = new DatabaseHelper(KetQuaSoXo.this);
        String date = controller.dateDay("yyyy-MM-dd");
        String dateLink = controller.dateDay("dd-MM-yyyy");
        updateKqsx(dateLink, date);
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
                JSONObject root = null;
                try {
                    sql = new DatabaseHelper(KetQuaSoXo.this);
                    JSONObject o = new JSONObject(s);
                    JSONArray a = o.getJSONArray("ketqua"); // lay gia tri ngoai cung roi moi foreach
                    String ngay = o.getJSONArray("link").getString(0);
                    String ngayTrue = controller.convertFormatDate(ngay);
                    String loto, value; // value la 2 so cuoi cua giai thuong
                    String valueDau = "100";
                    String table1 = sql.TABLE_NAME_1;
                    sql.deleteAll(table1,"0");
//                    if (res.getCount() != 27) { // kiem tra da insert ket qua ngay hom nay
                        for (int i = 0; i < a.length(); i++) {
                            String[] separated = a.getString(i).split("-");
                            for (int j = 0; j < separated.length; j++) {
                                loto = separated[j].trim();
                                if (loto.length() > 2) {
                                    value = loto.substring(loto.length() - 2);
                                    valueDau = loto.substring(0,2);
                                } else {
                                    value = loto;
                                    valueDau = loto;
                                }
                                boolean ket = sql.insertDataKq(loto, i, j, ngayTrue, value,valueDau);
                                if (ket) {
                                    Log.d(LogFile, "true");
                                } else {
                                    Log.d(LogFile, "false");
                                }
                            }
                        }
                        Cursor res2 = sql.getAllDb("SELECT * FROM kq_table Where NGAY =\"" + ngayTrue + "\"");
                        if (res2.getCount() == 27) {
                            showTableKQ(res2, ngayTrue);
                        } else {
                            controller.showAlertDialog(KetQuaSoXo.this,"Thông báo","Chưa có kết quả ngày "+ngay);
                        }
                } catch (JSONException e) {
                    controller.showAlertDialog(KetQuaSoXo.this, "Thông Báo", "Chưa tìm thấy kết quả nào! error.printStackTrace");
                    e.printStackTrace();
                }
            } else {
                controller.showAlertDialog(KetQuaSoXo.this, "Thông Báo", "Chưa tìm thấy kết quả nào!");
            }
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
                        Intent intent = new Intent(KetQuaSoXo.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price :
                        Intent intent2 = new Intent(KetQuaSoXo.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms :
                        Intent intent3 = new Intent(KetQuaSoXo.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money :
                        Intent intent4 = new Intent(KetQuaSoXo.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(KetQuaSoXo.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(KetQuaSoXo.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(KetQuaSoXo.this, viewSmsNotMoney.class);
                        startActivity(intent7);
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
        inflater.inflate(R.menu.show_dialog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_show_dialog:
                year_x = controller.dateInt("yyyy");
                month_x = controller.dateInt("MM") - 1;
                day_x = controller.dateInt("dd");

                DatePickerDialog datePickerDialog = new DatePickerDialog(KetQuaSoXo.this, new DatePickerDialog.OnDateSetListener() {
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
                            final String date = String.valueOf(year) + "-" + month_s + "-"
                                    + dayOfMonth_s;
                            final String dateLink = dayOfMonth_s + "-" + month_s + "-" + String.valueOf(year);
                            sql = new DatabaseHelper(KetQuaSoXo.this);
                            updateKqsx(date, date);
                        }
                    }
                }, year_x, month_x, day_x);

                datePickerDialog.show();

                return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateKqsx(String dateImport, String dateDelete) {
        final String delete = dateDelete;
        final String dateLink = dateImport;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String newDateLink = controller.convertFormatDateDd(dateLink);
                    sql.getAllDb("DELETE FROM kq_table WHERE NGAY=\"" + delete + "\"");
                    String link = "http://hostingkqxs.esy.es/kqsx.php?date=" + newDateLink;
                    new ReadXml().execute(link);
                }
            });
        } else {
            controller.showAlertDialog(KetQuaSoXo.this, "Thông Báo", "Không kết nối được internet");
        }
    }

    private void showTableKQ(Cursor res, String date) {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayoutKetQua);
//        tableLayout.removeAllViews();
        TableRow trTitle = new TableRow(KetQuaSoXo.this);
        TextView tvTile = new TextView(KetQuaSoXo.this);
        tvTile.setText(date);
        tvTile.setBackgroundColor(Color.YELLOW);
        tvTile.setTypeface(Typeface.DEFAULT_BOLD);
        tvTile.setTextSize(18);

        tableLayout.addView(tvTile);

        int i = 0;
        int end = 1;
        String colum = "";
        while (res.moveToNext()) {
            end++;
            TableRow tr = new TableRow(KetQuaSoXo.this);
            TextView tv = new TextView(KetQuaSoXo.this);
            TextView tv2 = new TextView(KetQuaSoXo.this);
            int giai = Integer.parseInt(res.getString(res.getColumnIndex("GIAI")));
            Log.d("LogFile", String.valueOf(res.getCount()));
            if (i == giai) {
                if (colum.equals("")) {
                    colum += res.getString(res.getColumnIndex("LOTO"));
                } else {
                    colum += " - " + res.getString(res.getColumnIndex("LOTO"));
                }
            } else {
                tv.setText(colum);
                tv2.setText(String.valueOf(i));
                tv2.setWidth(50);
                if (i % 2 == 0) {
                    tv.setBackgroundColor(Color.GRAY);
                    tv2.setBackgroundColor(Color.GRAY);
                }
                tr.addView(tv2);
                tr.addView(tv);
                colum = res.getString(res.getColumnIndex("LOTO"));
                i = giai;
            }
            if (end == res.getCount() + 1) {
                tv.setText(colum);
                tv2.setText(String.valueOf(i));
                tr.addView(tv2);
                tr.addView(tv);
            }

            tableLayout.addView(tr);
        }
    }

}
