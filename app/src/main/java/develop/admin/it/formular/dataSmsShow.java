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
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class dataSmsShow extends AppCompatActivity {
    DatabaseHelper sql;
    GlobalClass controller;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    TextView showDate;

    private static final String LogFile = "LogFile";
    private int year_x, month_x, day_x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_sms_show);
        controller = new GlobalClass();
        String getDays = controller.dateDay("yyyy-MM-dd");
        showStatistic(getDays, "all");
        sideBarMenu();
    }

    private void showStatistic(String getDays, String kieu) {
        String queryKieu = "";
        ArrayList<String> limitNumber = controller.limitNumber();
        ArrayList<String> limitNumberBaCang = controller.limitNumberBaCang();
        switch (kieu) {
            case "send":
                queryKieu = "KIEU=\"send\" AND ";
                break;
            case "inbox":
                queryKieu = "KIEU=\"inbox\" AND ";
                break;
        }
        sql = new DatabaseHelper(this);
        String query = "SELECT * FROM solieu_table WHERE " + queryKieu + " NGAY=\"" + getDays.replaceAll("(^\\s+|\\s+$)", "").trim() + "\" ";
        Cursor table_solieu = sql.getAllDb(query);

        TableLayout tableLayout = (TableLayout) findViewById(R.id.tableStatistic);
        TableRow tr1 = new TableRow(dataSmsShow.this);
        TextView tong = new TextView(dataSmsShow.this);
        String dateText = "<font color=\"blue\"><big>" + getDays + "</big></font><br/>";
        showDate = (TextView) findViewById(R.id.textViewDate);
        showDate.setText(Html.fromHtml(dateText));

        TableRow tr2 = new TableRow(dataSmsShow.this);
        TextView de = new TextView(dataSmsShow.this);
        TextView lo = new TextView(dataSmsShow.this);

        TableRow tr3 = new TableRow(dataSmsShow.this);
        TextView bacang = new TextView(dataSmsShow.this);
        TextView xi = new TextView(dataSmsShow.this);

        Integer[] deArr = new Integer[100];
        Integer[] deTrung = new Integer[100];
        Integer[] loArr = new Integer[100];
        Integer[] loTrung = new Integer[100];
        Integer[] bacangArr = new Integer[1000];
        HashMap<String, ArrayList<String>> hashmap = sql.readkitu();
        String compareDe = sql.compareDe(getDays);
        ArrayList<String> compareLo = sql.getArrayKeyRes(getDays);

        if (table_solieu.getCount() > 0) {
            while (table_solieu.moveToNext()) {
                String loto = table_solieu.getString(table_solieu.getColumnIndex("LOTO"));
                String kieuChoi = table_solieu.getString(table_solieu.getColumnIndex("KIHIEU"));
                String tienDanh = table_solieu.getString(table_solieu.getColumnIndex("TIENDANH"));
                String tienThuong = table_solieu.getString(table_solieu.getColumnIndex("TIENTHUONG"));
                String kieuguitin = table_solieu.getString(table_solieu.getColumnIndex("KIEU"));
                String tienDanhSms = table_solieu.getString(table_solieu.getColumnIndex("TIENDANHSMS"));
                String tienThuongSms = table_solieu.getString(table_solieu.getColumnIndex("TRUNGSMS"));
                String tongbang = table_solieu.getString(table_solieu.getColumnIndex("TONG"));
                double moicon2 = Math.round(Double.parseDouble(table_solieu.getString(table_solieu.getColumnIndex("MOICON"))) * 100.0) / 100.0;
                Integer moicon = Integer.parseInt(String.valueOf(Math.round(moicon2)));
                if (hashmap.get(loto) != null) {
                    String value = hashmap.get(loto).get(0);
                    String[] arrVal = value.split(",");
                    if (kieuChoi.equals("de")) {
                        for (int i = 0; i < arrVal.length; i++) {
                            int solo = Integer.parseInt(String.valueOf(arrVal[i].replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "").replace(" ", "")));
                            if (kieuguitin.equals("inbox")) {
                                if (deArr[solo] != null) {
                                    deArr[solo] += moicon;
                                } else {
                                    deArr[solo] = moicon;
                                }
                            } else {
                                if (deArr[solo] != null) {
                                    deArr[solo] -= moicon;
                                } else {
                                    deArr[solo] = 0 - moicon;
                                }
                            }
                        }
                    } else if (kieuChoi.equals("lo")) {
                        for (int i = 0; i < arrVal.length; i++) {
                            int solo = Integer.parseInt(String.valueOf(arrVal[i].replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "").replace(" ", "")));
                            if (kieuguitin.equals("inbox")) {
                                if (loArr[solo] != null) {
                                    loArr[solo] += moicon;
                                } else {
                                    loArr[solo] = moicon;
                                }
                            } else {
                                if (loArr[solo] != null) {
                                    loArr[solo] -= moicon;
                                } else {
                                    loArr[solo] = 0 - moicon;
                                }
                            }
                        }
                    }
                } else {
                    if (limitNumber.contains(loto.replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", ""))) {
                        if (kieuChoi.equals("de")) {
                            int intLoTo = Integer.parseInt(String.valueOf(loto.replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "").replace(" ", "")));
                            if (kieuguitin.equals("inbox")) {
                                if (deArr[intLoTo] != null) {
                                    deArr[intLoTo] += moicon;
                                } else {
                                    deArr[intLoTo] = moicon;
                                }
                            } else {
                                if (deArr[intLoTo] != null) {
                                    deArr[intLoTo] -= moicon;
                                } else {
                                    deArr[intLoTo] = 0 - moicon;
                                }
                            }
                        } else if (kieuChoi.equals("lo")) {
                            int intLoTo = Integer.parseInt(String.valueOf(loto.replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "").replace(" ", "")));
                            if (kieuguitin.equals("inbox")) {
                                if (loArr[intLoTo] != null) {
                                    loArr[intLoTo] += moicon;
                                } else {
                                    loArr[intLoTo] = moicon;
                                }
                            } else {
                                if (loArr[intLoTo] != null) {
                                    loArr[intLoTo] -= moicon;
                                } else {
                                    loArr[intLoTo] = 0 - moicon;
                                }
                            }
                        }
                    } else if (limitNumberBaCang.contains(loto.replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "")) && kieuChoi.equals("bacang")) {
                        int intBaCang = Integer.parseInt(String.valueOf(loto.replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "").replace(" ", "")));
                        if (kieuguitin.equals("inbox")) {
                            if (bacangArr[intBaCang] != null) {
                                bacangArr[intBaCang] += moicon;
                            } else {
                                bacangArr[intBaCang] = moicon;
                            }
                        } else {
                            if (bacangArr[intBaCang] != null) {
                                bacangArr[intBaCang] -= moicon;
                            } else {
                                bacangArr[intBaCang] = 0 - moicon;
                            }
                        }
                    }

                }
            }
        }

        String textDe = "<font color =\"blue\"><big>Đề</big></font><br/>";
        String textLo = "<font color =\"blue\"><big>Lô</big></font><br/>";
        String textBaCang = "<font color =\"blue\"><big>Ba Càng</big></font><br/>";

        String[] sortDe = new String[100];
        String[] sortLo = new String[100];
        String[] sortBacang = new String[1000];
        int z = 0;
        int k = 0;
        int s = 0;
        for (int q = 0; q < deArr.length; q++) {
            if (deArr[q] != null) {
                String showDe = "";
                if (q < 10) {
                    showDe = "0" + String.valueOf(q);
                } else {
                    showDe = String.valueOf(q);
                }
                sortDe[z] = String.valueOf(showDe) + "_" + String.valueOf(deArr[q]) + "_" + String.valueOf(deTrung[q]);
                z++;
            } else {
                String showDe = "";
                if (q < 10) {
                    showDe = "0" + String.valueOf(q);
                } else {
                    showDe = String.valueOf(q);
                }
                sortDe[z] = String.valueOf(showDe) + "_0_0";
                z++;
            }

            if (loArr[q] != null) {
                String showLo = "";
                if (q < 10) {
                    showLo = "0" + String.valueOf(q);
                } else {
                    showLo = String.valueOf(q);
                }
                sortLo[k] = String.valueOf(showLo) + "_" + String.valueOf(loArr[q]) + "_" + String.valueOf(loTrung[q]);
                k++;
            } else {
                String showLo = "";
                if (q < 10) {
                    showLo = "0" + String.valueOf(q);
                } else {
                    showLo = String.valueOf(q);
                }
                sortLo[k] = String.valueOf(showLo) + "_0_0";
                k++;
            }
        }

        for (int f = 0; f < bacangArr.length; f++) {
            if (bacangArr[f] != null) {
                String showBaCang = "";
                if (f < 10) {
                    showBaCang = "00" + String.valueOf(f);
                } else if (f < 10) {
                    showBaCang = "0" + String.valueOf(f);
                } else {
                    showBaCang = String.valueOf(f);
                }
                sortBacang[s] = String.valueOf(showBaCang) + "_" + String.valueOf(bacangArr[f]);
                s++;
            }
        }

//        String tempBaCang;
//        for (int c = 0; c < sortBacang.length; c++) {
//            if (sortBacang[c] != null) {
//                String[] arrSortBacang1 = sortBacang[c].split("_");
//                for (int a = c + 1; a < sortBacang.length; a++) {
//                    if (sortBacang[a] != null) {
//                        String[] arrSortBacang2 = sortBacang[a].split("_");
//                        if (Integer.parseInt(arrSortBacang1[1]) < Integer.parseInt(arrSortBacang2[1])) {
//                            tempBaCang = sortBacang[a];
//                            sortBacang[a] = sortBacang[c];
//                            sortBacang[c] = tempBaCang;
//                            arrSortBacang1[1] = arrSortBacang2[1];
//                        }
//                    }
//                }
//            }
//        }
//
//        String tempDe;
//        for (int x = 0; x < sortDe.length; x++) {
//            if (sortDe[x] != null) {
//                String[] arrSortDe1 = sortDe[x].split("_");
//                for (int t = x + 1; t < sortDe.length; t++) {
//                    if (sortDe[t] != null) {
//                        String[] arrSortDe2 = sortDe[t].split("_");
//                        if (Integer.parseInt(arrSortDe1[1]) < Integer.parseInt(arrSortDe2[1])) {
//                            tempDe = sortDe[t];
//                            sortDe[t] = sortDe[x];
//                            sortDe[x] = tempDe;
//                            arrSortDe1[1] = arrSortDe2[1];
//                        }
//                    }
//                }
//            }
//        }
//
//        String tempLo;
//        for (int c = 0; c < sortLo.length; c++) {
//            if (sortLo[c] != null) {
//                String[] arrSortLo1 = sortLo[c].split("_");
//                for (int r = c + 1; r < sortLo.length; r++) {
//                    if (sortLo[r] != null) {
//                        String[] arrSortLo2 = sortLo[r].split("_");
//                        if (Integer.parseInt(arrSortLo1[1]) < Integer.parseInt(arrSortLo2[1])) {
//                            tempLo = sortLo[r];
//                            sortLo[r] = sortLo[c];
//                            sortLo[c] = tempLo;
//                            arrSortLo1[1] = arrSortLo2[1];
//                        }
//                    }
//                }
//            }
//        }

        for (int a = 0; a < sortDe.length; a++) {
            if (sortDe[a] != null) {
                String[] showResDe = sortDe[a].split("_");
                textDe += "<font color=\"red\"><big>" + showResDe[0] + "</big>   </font>" +
                        "<big>" + showResDe[1] + "n</big><br/>";

            }
        }

        for (int b = 0; b < sortLo.length; b++) {
            if (sortLo[b] != null) {
                String[] showResLo = sortLo[b].split("_");
                textLo += "<font color=\"red\"><big>" + showResLo[0] + "</big>   </font>" +
                        "<big>" + showResLo[1] + "d</big><br/>";
            }
        }

        for (int n = 0; n < sortBacang.length; n++) {
            if (sortBacang[n] != null) {
                String[] showResBaCang = sortBacang[n].split("_");
                textBaCang += "<font color=\"red\"><big>" + showResBaCang[0] + "</big>   </font>" +
                        "<big>" + showResBaCang[1] + "d</big><br/>";
            }
        }

        TableRow.LayoutParams rowSpanLayout2 = new TableRow.LayoutParams();
        rowSpanLayout2.span = 2;
        tong.setText(Html.fromHtml(textBaCang));
        tr1.addView(tong, rowSpanLayout2);
        tableLayout.addView(tr1);

        de.setText(Html.fromHtml(textDe));
        lo.setText(Html.fromHtml(textLo));
        tr2.addView(de);
        tr2.addView(lo);
        tableLayout.addView(tr2);


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
                        Intent intent = new Intent(dataSmsShow.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(dataSmsShow.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(dataSmsShow.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(dataSmsShow.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(dataSmsShow.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(dataSmsShow.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(dataSmsShow.this, viewSmsNotMoney.class);
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
        inflater.inflate(R.menu.data_number, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_show_dialog:
                year_x = controller.dateInt("yyyy");
                month_x = controller.dateInt("MM") - 1;
                day_x = controller.dateInt("dd");

                DatePickerDialog datePickerDialog = new DatePickerDialog(dataSmsShow.this, new DatePickerDialog.OnDateSetListener() {
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
                            final String dateLink = String.valueOf(year) + "-" + month_s + "-" + dayOfMonth_s;
                            TableLayout tableLayout = (TableLayout) findViewById(R.id.tableStatistic);
                            tableLayout.removeAllViews();
                            showStatistic(dateLink, "all");
                        }
                    }
                }, year_x, month_x, day_x);
                datePickerDialog.show();
                return true;
            case R.id.all:
                showDate = (TextView) findViewById(R.id.textViewDate);
                String getDays = showDate.getText().toString();
                TableLayout tableLayoutAll = (TableLayout) findViewById(R.id.tableStatistic);
                tableLayoutAll.removeAllViews();
                showStatistic(getDays, "all");
                return true;
            case R.id.send:
                showDate = (TextView) findViewById(R.id.textViewDate);
                String getDaysend = showDate.getText().toString();
                TableLayout tableLayoutSend = (TableLayout) findViewById(R.id.tableStatistic);
                tableLayoutSend.removeAllViews();
                showStatistic(getDaysend, "send");
                return true;
            case R.id.inbox:
                showDate = (TextView) findViewById(R.id.textViewDate);
                String getDayinbox = showDate.getText().toString();
                TableLayout tableLayoutInbox = (TableLayout) findViewById(R.id.tableStatistic);
                tableLayoutInbox.removeAllViews();
                showStatistic(getDayinbox, "inbox");
                return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
