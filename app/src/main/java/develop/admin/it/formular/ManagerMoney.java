package develop.admin.it.formular;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ManagerMoney extends AppCompatActivity {
    DatabaseHelper sql;
    GlobalClass controller;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    private static final String LogFile = "LogFile";
    private int year_x, month_x, day_x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_money);

        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayoutManager);
        tableLayout.removeAllViews();

        controller = new GlobalClass();
        String getDays = controller.dateDay("yyyy-MM-dd");
        updateManagerMoney(getDays);
        showManagerMoney(getDays);
        sideBarMenu();
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
                        Intent intent = new Intent(ManagerMoney.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(ManagerMoney.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(ManagerMoney.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(ManagerMoney.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(ManagerMoney.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(ManagerMoney.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(ManagerMoney.this, viewSmsNotMoney.class);
                        startActivity(intent7);
                        return true;
                    case R.id.historySms:
                        Intent intent8 = new Intent(ManagerMoney.this, HistorySms.class);
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

    private void showManagerMoney(String getDays) {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayoutManager);
        TableRow.LayoutParams rowSpanLayout = new TableRow.LayoutParams();
        TableRow.LayoutParams rowSpanLayout2 = new TableRow.LayoutParams();
        rowSpanLayout2.span = 5;
        TableRow.LayoutParams rowSpanLayout3 = new TableRow.LayoutParams();
        String query = "SELECT SDT,TEN FROM solieu_table WHERE NGAY=\"" + getDays + "\"  GROUP BY SDT";
        Cursor solieu_table = sql.getAllDb(query);
        TableRow tr6 = new TableRow(ManagerMoney.this);
        TextView day = new TextView(ManagerMoney.this);
        day.setTextSize(20);
        day.setText(Html.fromHtml("<font color=\"blue\">" + getDays + "</font>"));
        tr6.addView(day, rowSpanLayout2);
        tableLayout.addView(tr6);

        TableRow tr5 = new TableRow(ManagerMoney.this);
        TextView titlekieu = new TextView(ManagerMoney.this);
        TextView titledanhInbox = new TextView(ManagerMoney.this);
        TextView titleheSoInbox = new TextView(ManagerMoney.this);
        TextView titledanhSend = new TextView(ManagerMoney.this);
        TextView titleheSoSend = new TextView(ManagerMoney.this);

        titlekieu.setBackgroundResource(R.drawable.border_style);
        titledanhInbox.setBackgroundResource(R.drawable.border_style);
        titleheSoInbox.setBackgroundResource(R.drawable.border_style);
        titledanhSend.setBackgroundResource(R.drawable.border_style);
        titleheSoSend.setBackgroundResource(R.drawable.border_style);

        titlekieu.setText("Kiểu");
        titledanhInbox.setText("Tin Đến");
        titleheSoInbox.setText("Tiền");
        titledanhSend.setText("Tin Gửi");
        titleheSoSend.setText("Tiền");

        tr5.addView(titlekieu);
        tr5.addView(titledanhInbox);
        tr5.addView(titleheSoInbox);
        tr5.addView(titledanhSend);
        tr5.addView(titleheSoSend);
        tableLayout.addView(tr5);

        if (solieu_table.getCount() > 0) {
            double tongCong = 0;
            double tongCongInbox = 0;
            double tongCongSend = 0;
            while (solieu_table.moveToNext()) {
                String sdt = solieu_table.getString(solieu_table.getColumnIndex("SDT"));
                String ten = solieu_table.getString(solieu_table.getColumnIndex("TEN"));
                String query_dongia = "SELECT COPHAN,KIEUCOPHAN FROM dongia_table WHERE SDT=\"" + sdt + "\" " +
                        "AND TEN=\"" + ten + "\" LIMIT 1";
                Cursor dongia_table = sql.getAllDb(query_dongia);
                double cophan = 0;
                String kieucophan = "";
                String is_dongia = "0";
                if (dongia_table.getCount() == 1) {
                    dongia_table.moveToFirst();
                    cophan = Math.round(
                            Double.parseDouble(dongia_table.getString(dongia_table.getColumnIndex("COPHAN")))
                                    * 100.0) / 100.0;
                    kieucophan = dongia_table.getString(dongia_table.getColumnIndex("KIEUCOPHAN"));// 0-all-1-inbox-2-send
                    is_dongia = "1";
                }

                Cursor manager = sql.getAllDb(
                        "SELECT * FROM manager_money_table WHERE NGAY=\"" + getDays + "\" AND SDT=\"" + sdt + "\""
                );

                if (manager.getCount() > 0) {
                    double tongInbox = 0;
                    double tongSend = 0;
                    while (manager.moveToNext()) {
                        TableRow tr1 = new TableRow(ManagerMoney.this);
                        TextView kieu = new TextView(ManagerMoney.this);
                        TextView danhInbox = new TextView(ManagerMoney.this);
                        TextView heSoInbox = new TextView(ManagerMoney.this);
                        TextView danhSend = new TextView(ManagerMoney.this);
                        TextView heSoSend = new TextView(ManagerMoney.this);
                        String kieuSoLieu = manager.getString(manager.getColumnIndex("KIEU"));
                        kieu.setText(manager.getString(manager.getColumnIndex("KIEU")));
                        double showDanhInbox = Math.round(Double.parseDouble(
                                manager.getString(manager.getColumnIndex("DANHINBOX"))) * 100.0) / 100.0;
                        double showHESOINBOX = Math.round(Double.parseDouble(
                                manager.getString(manager.getColumnIndex("HESOINBOX"))) * 100.0) / 100.0;
                        double showDANHSEND = Math.round(Double.parseDouble(
                                manager.getString(manager.getColumnIndex("DANHSEND"))) * 100.0) / 100.0;

                        double showHESOSEND = Math.round(Double.parseDouble(
                                manager.getString(manager.getColumnIndex("HESOSEND"))) * 100.0) / 100.0;
                        if (kieuSoLieu.indexOf("th") > -1) {
                            tongInbox = tongInbox - showHESOINBOX;
                            tongSend = tongSend - showHESOSEND;
                        } else {
                            tongInbox = tongInbox + showHESOINBOX;
                            tongSend = tongSend + showHESOSEND;
                        }

                        danhInbox.setText(String.valueOf(showDanhInbox));
                        heSoInbox.setText(String.valueOf(showHESOINBOX));
                        danhSend.setText(String.valueOf(showDANHSEND));
                        heSoSend.setText(String.valueOf(showHESOSEND));

                        kieu.setBackgroundResource(R.drawable.border_style);
                        danhInbox.setBackgroundResource(R.drawable.border_style);
                        heSoInbox.setBackgroundResource(R.drawable.border_style);
                        danhSend.setBackgroundResource(R.drawable.border_style);
                        heSoSend.setBackgroundResource(R.drawable.border_style);

                        tr1.addView(kieu);
                        tr1.addView(danhInbox);
                        tr1.addView(heSoInbox);
                        tr1.addView(danhSend);
                        tr1.addView(heSoSend);
                        tableLayout.addView(tr1);

                    } // end while manager.moveToNext()

                    TableRow tr2 = new TableRow(ManagerMoney.this);
                    TextView tongcong = new TextView(ManagerMoney.this);
                    TextView tongcongInbox = new TextView(ManagerMoney.this);
                    TextView tongcongSend = new TextView(ManagerMoney.this);

                    String showTongInbox, showTongSend, showTong;
                    tongInbox = Math.round(tongInbox * 100.0) / 100.0;
                    if (tongInbox < 0) {
                        showTongInbox = "<font color=\"red\">" + String.valueOf(Math.round(tongInbox * -1 * 100.0) / 100.0) + "</font>";
                    } else {
                        showTongInbox = "<font color=\"blue\">" + String.valueOf(Math.round(tongInbox * 100.0) / 100.0) + "</font>";
                    }
                    tongSend = Math.round(tongSend * 100.0) / 100.0;
                    if (tongSend > 0) {
                        showTongSend = "<font color=\"red\">" + String.valueOf(Math.round(tongSend * 100.0) / 100.0) + "</font>";
                    } else {
                        showTongSend = "<font color=\"blue\">" + String.valueOf(Math.round(tongSend * -1 * 100.0) / 100.0) + "</font>";
                    }
                    tongCongInbox += tongInbox;
                    tongCongSend += tongSend;
                    tongCong = tongInbox - tongSend;

                    String title = sdt + "-<font color=\"green\" >" + ten + "</font> : ";
                    if (tongCong > 0) {
                        title = title + "<font color=\"blue\" > thu " + String.valueOf(Math.round(tongCong * 100.0) / 100.0) + "</font>";
                    } else {
                        title = title + "<font color=\"red\" > bù " + String.valueOf(Math.round(tongCong * -1 * 100.0) / 100.0) + "</font>";
                    }
                    if (cophan < Double.parseDouble("100") && is_dongia.equals("1")) {
                        String cophanAll = "";
                        if (kieucophan.equals("1")) {
                            tongCongInbox = tongCongInbox - tongInbox + Math.round(tongInbox * cophan) / 100.0;
                            Double cophanInbox = Math.round(tongInbox * cophan) / 100.0 - tongSend;
                            if (cophanInbox > 0) {
                                cophanAll = "<font color=\"blue\" > thu " + String.valueOf(Math.round(cophanInbox * 100.0) / 100.0) + "</font>";
                            } else {
                                cophanAll = "<font color=\"red\" > bù " + String.valueOf(Math.round(cophanInbox * -1 * 100.0) / 100.0) + "</font>";
                            }
                            title = title + "<br>" + "<font color=\"green\" >" + " Cổ Phần Tin Đến " + String.valueOf(cophan) + "% :" + "</font>";
                            title = title + cophanAll;
                        } else if (kieucophan.equals("2")) {
                            tongCongSend = tongCongSend - tongSend + Math.round(tongSend * cophan) / 100.0;
                            Double cophanSend = tongInbox - Math.round(tongSend * cophan) / 100.0;
                            if (cophanSend > 0) {
                                cophanAll = "<font color=\"blue\" > thu " + String.valueOf(Math.round(cophanSend * 100.0) / 100.0) + "</font>";
                            } else {
                                cophanAll = "<font color=\"red\" > bù " + String.valueOf(Math.round(cophanSend * -1 * 100.0) / 100.0) + "</font>";
                            }
                            title = title + "<br>" + "<font color=\"green\" >" + " Cổ Phần Tin Gửi " + String.valueOf(cophan) + "% :" + "</font>";
                            title = title + cophanAll;
                        } else {
                            tongCongInbox = tongCongInbox - tongInbox + Math.round(tongInbox * cophan) / 100.0;
                            tongCongSend = tongCongSend - tongSend + Math.round(tongSend * cophan) / 100.0;
                            if (tongCong > 0) {
                                cophanAll = "<font color=\"blue\" > thu " + String.valueOf(Math.round(tongCong * cophan) / 100.0) + "</font>";
                            } else {
                                cophanAll = "<font color=\"red\" > bù " + String.valueOf(Math.round(tongCong * -1 * cophan) / 100.0) + "</font>";
                            }
                            title = title + "<br>" + "<font color=\"green\" >" + " Cổ Phần " + String.valueOf(cophan) + "% :" + "</font>";
                            title = title + cophanAll;
                        }
                    }
                    tongcong.setText("Tiền");
                    tongcongInbox.setText(Html.fromHtml(showTongInbox));
                    tongcongSend.setText(Html.fromHtml(showTongSend));

                    tongcong.setBackgroundResource(R.drawable.border_style);
                    tongcongInbox.setBackgroundResource(R.drawable.border_style);
                    tongcongSend.setBackgroundResource(R.drawable.border_style);

                    rowSpanLayout.span = 2;
                    tr2.addView(tongcong);
                    tr2.addView(tongcongInbox, rowSpanLayout);
                    tr2.addView(tongcongSend, rowSpanLayout);
                    tableLayout.addView(tr2);


                    TableRow tr = new TableRow(ManagerMoney.this);
                    TextView sdtTable = new TextView(ManagerMoney.this);
                    rowSpanLayout2.span = 5;
                    sdtTable.setText(Html.fromHtml(title));
                    sdtTable.setBackgroundResource(R.drawable.border_style);
                    sdtTable.setBackgroundColor(Color.YELLOW);
                    tr.addView(sdtTable, rowSpanLayout2);
                    tableLayout.addView(tr);

                }

            } // end while dongia.moveToNext()
            TableRow tr3 = new TableRow(ManagerMoney.this);
            TextView tongEnd = new TextView(ManagerMoney.this);
            TextView tongInboxEnd = new TextView(ManagerMoney.this);
            TextView tongSendEnd = new TextView(ManagerMoney.this);

            String showCongTongInbox, showCongTongSend, showCongTong;
            if (tongCongInbox < 0) {
                showCongTongInbox = "<font color=\"red\">" + String.valueOf(Math.round(tongCongInbox * -1 * 100.0) / 100.0) + "</font>";
            } else {
                showCongTongInbox = "<font color=\"blue\">" + String.valueOf(Math.round(tongCongInbox * 100.0) / 100.0) + "</font>";
            }
            if (tongCongSend > 0) {
                showCongTongSend = "<font color=\"red\">" + String.valueOf(Math.round(tongCongSend * 100.0) / 100) + "</font>";
            } else {
                showCongTongSend = "<font color=\"blue\">" + String.valueOf(Math.round(tongCongSend * -1 * 100.0) / 100.0) + "</font>";
            }
            tongCong = Math.round((tongCongInbox - tongCongSend) * 100.0) / 100.0;

            if (tongCong < 0) {
                showCongTong = "<font color=\"red\">Lỗ : " + String.valueOf(tongCong * -1) + "</font>";
            } else {
                showCongTong = "<font color=\"blue\">Lãi : " + String.valueOf(tongCong) + "</font>";
            }

            tongEnd.setText("Tổng");
            tongInboxEnd.setText(Html.fromHtml(showCongTongInbox));
            tongSendEnd.setText(Html.fromHtml(showCongTongSend));

            tongEnd.setBackgroundResource(R.drawable.border_style);
            tongInboxEnd.setBackgroundResource(R.drawable.border_style);
            tongSendEnd.setBackgroundResource(R.drawable.border_style);
            rowSpanLayout3.span = 2;
            tr3.addView(tongEnd);
            tr3.addView(tongInboxEnd, rowSpanLayout3);
            tr3.addView(tongSendEnd, rowSpanLayout3);
            tableLayout.addView(tr3);

            TableRow tr4 = new TableRow(ManagerMoney.this);
            TextView theEnd = new TextView(ManagerMoney.this);

            theEnd.setText(Html.fromHtml(showCongTong));
            theEnd.setBackgroundResource(R.drawable.border_style);
            theEnd.setBackgroundColor(Color.YELLOW);
            tr4.addView(theEnd, rowSpanLayout2);
            tableLayout.addView(tr4);
        }
    }

    private void updateManagerMoney(String getDays) {
        sql = new DatabaseHelper(ManagerMoney.this);
        controller = new GlobalClass();
//        Cursor getContact = sql.getAllDb("SELECT ID,SDT,TEN FROM dongia_table");
        String query = "SELECT DONGIAID,SDT,TEN FROM solieu_table WHERE NGAY=\"" + getDays + "\"  GROUP BY SDT";
        Cursor solieu_table = sql.getAllDb(query);
        String table7 = sql.TABLE_NAME_7;
        sql.deleteAllNgay(table7, getDays);
        int dongiaID = 0;
        if (solieu_table.getCount() > 0) {
            while (solieu_table.moveToNext()) {
                dongiaID = Integer.parseInt(solieu_table.getString(solieu_table.getColumnIndex("DONGIAID")));
                /* de inbox*/
                double danhDeInbox = 0;
                double heSoDeInbox = 0;
                double thuongDeInbox = 0;
                double hsThuongDeInbox = 0;
                /* de send*/
                double danhDeSend = 0;
                double heSoDeSend = 0;
                double thuongDeSend = 0;
                double hsThuongDeSend = 0;

                 /* de Dau inbox*/
                double danhDeDauInbox = 0;
                double heSoDeDauInbox = 0;
                double thuongDeDauInbox = 0;
                double hsThuongDeDauInbox = 0;
                /* de Dau send*/
                double danhDeDauSend = 0;
                double heSoDeDauSend = 0;
                double thuongDeDauSend = 0;
                double hsThuongDeDauSend = 0;

                /* lo inbox*/
                double danhLoInbox = 0;
                double heSoLoInbox = 0;
                double thuongLoInbox = 0;
                double hsThuongLoInbox = 0;
                /* lo send */
                double danhLoSend = 0;
                double heSoLoSend = 0;
                double thuongLoSend = 0;
                double hsThuongLoSend = 0;
                /* xien send*/
                double danhXienSend = 0;
                double heSoXienSend = 0;
                double thuongXienSend = 0;
                double hsThuongXienSend = 0;
                /* xien inbox */
                double danhXienInbox = 0;
                double heSoXienInbox = 0;
                double thuongXienInbox = 0;
                double hsThuongXienInbox = 0;
                /* bacang inbox */
                double danhBacangInbox = 0;
                double heSoBacangInbox = 0;
                double thuongBacangInbox = 0;
                double hsThuongBacangInbox = 0;
                /* bacang send */
                double danhBacangSend = 0;
                double heSoBacangSend = 0;
                double thuongBacangSend = 0;
                double hsThuongBacangSend = 0;

                String sdt = solieu_table.getString(solieu_table.getColumnIndex("SDT"));
                String ten = solieu_table.getString(solieu_table.getColumnIndex("TEN"));
                Cursor soLieu = sql.getAllDb("SELECT * FROM solieu_table WHERE SDT= \"" + sdt + "\" AND NGAY=\"" + getDays + "\"");
                if (soLieu.getCount() > 0) {
                    while (soLieu.moveToNext()) {
                        String kieu = soLieu.getString(soLieu.getColumnIndex("KIEU"));
                        String kiHieu = soLieu.getString(soLieu.getColumnIndex("KIHIEU"));
                        if (kieu.equals("send")) {
                            switch (kiHieu.trim()) {
                                case "de":
                                    danhDeSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoDeSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongDeSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongDeSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "dq":
                                    danhDeDauSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoDeDauSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongDeDauSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongDeDauSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "lo":
                                    danhLoSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoLoSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongLoSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongLoSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "xien2":
                                    danhXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "xien3":
                                    danhXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    Log.d("LogFile", String.valueOf(heSoXienSend));
                                    break;
                                case "xien4":
                                    danhXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "xien":
                                    danhXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongXienSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "bacang":
                                    danhBacangSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoBacangSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongBacangSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongBacangSend += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                default:
                                    break;
                            }
                        } else if (kieu.equals("inbox")) {
                            switch (kiHieu) {
                                case "de":
                                    danhDeInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoDeInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongDeInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongDeInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "dq":
                                    danhDeDauInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoDeDauInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongDeDauInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongDeDauInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "lo":
                                    danhLoInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoLoInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongLoInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongLoInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "xien2":
                                    danhXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "xien3":
                                    danhXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "xien4":
                                    danhXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "xien":
                                    danhXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongXienInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                                case "bacang":
                                    danhBacangInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANHSMS")));
                                    heSoBacangInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENDANH")));
                                    thuongBacangInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TRUNGSMS")));
                                    hsThuongBacangInbox += Double.parseDouble(soLieu.getString(soLieu.getColumnIndex("TIENTHUONG")));
                                    break;
                            }
                        }
                    }
                }

                if (danhDeInbox != 0 || heSoDeInbox != 0 || danhDeSend != 0 || heSoDeSend != 0) {
                    sql.insertManagerMoney(sdt, ten, danhDeInbox, heSoDeInbox, danhDeSend, heSoDeSend, "de", dongiaID, getDays);
                }
                if (thuongDeInbox != 0 || hsThuongDeInbox != 0 || thuongDeSend != 0 || hsThuongDeSend != 0) {
                    sql.insertManagerMoney(sdt, ten, thuongDeInbox, hsThuongDeInbox, thuongDeSend, hsThuongDeSend, "thde", dongiaID, getDays);
                }

                if (danhDeDauInbox != 0 || heSoDeDauInbox != 0 || danhDeDauSend != 0 || heSoDeDauSend != 0) {
                    sql.insertManagerMoney(sdt, ten, danhDeDauInbox, heSoDeDauInbox, danhDeDauSend, heSoDeDauSend, "dq", dongiaID, getDays);
                }
                if (thuongDeDauInbox != 0 || hsThuongDeDauInbox != 0 || thuongDeDauSend != 0 || hsThuongDeDauSend != 0) {
                    sql.insertManagerMoney(sdt, ten, thuongDeDauInbox, hsThuongDeDauInbox, thuongDeDauSend, hsThuongDeDauSend, "thdq", dongiaID, getDays);
                }

                if (danhLoInbox != 0 || heSoLoInbox != 0 || danhLoSend != 0 || heSoLoSend != 0) {
                    sql.insertManagerMoney(sdt, ten, danhLoInbox, heSoLoInbox, danhLoSend, heSoLoSend, "lo", dongiaID, getDays);
                }
                if (thuongLoInbox != 0 || hsThuongLoInbox != 0 || thuongLoSend != 0 || hsThuongLoSend != 0) {
                    sql.insertManagerMoney(sdt, ten, thuongLoInbox, hsThuongLoInbox, thuongLoSend, hsThuongLoSend, "thlo", dongiaID, getDays);
                }
                if (danhXienInbox != 0 || heSoXienInbox != 0 || danhXienSend != 0 || heSoXienSend != 0) {
                    sql.insertManagerMoney(sdt, ten, danhXienInbox, heSoXienInbox, danhXienSend, heSoXienSend, "xien", dongiaID, getDays);
                }
                if (thuongXienInbox != 0 || hsThuongXienInbox != 0 || thuongXienSend != 0 || hsThuongXienSend != 0) {
                    sql.insertManagerMoney(sdt, ten, thuongXienInbox, hsThuongXienInbox, thuongXienSend, hsThuongXienSend, "thxien", dongiaID, getDays);
                }
                if (danhBacangInbox != 0 || heSoBacangInbox != 0 || danhBacangSend != 0 || heSoBacangSend != 0) {
                    sql.insertManagerMoney(sdt, ten, danhBacangInbox, heSoBacangInbox, danhBacangSend, heSoBacangSend, "bacang", dongiaID, getDays);
                }
                if (thuongBacangInbox != 0 || hsThuongBacangInbox != 0 || thuongBacangSend != 0 || hsThuongBacangSend != 0) {
                    sql.insertManagerMoney(sdt, ten, thuongBacangInbox, hsThuongBacangInbox, thuongBacangSend, hsThuongBacangSend, "thbacang", dongiaID, getDays);
                }

            }
        }
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(ManagerMoney.this, new DatePickerDialog.OnDateSetListener() {
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
                            TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayoutManager);
                            tableLayout.removeAllViews();
                            updateManagerMoney(getDays);
                            showManagerMoney(getDays);

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

}
