package develop.admin.it.formular;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class CanBangSendSms extends AppCompatActivity {
    DatabaseHelper sql;
    EditText sdtCus, contentSend,editTextSdt;
    TextView textViewSdt;
    Button sendSms;
    ProgressBar loadingimage;
    GlobalClass controller = new GlobalClass();

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    private static final String LogFile = "LogFile";
    private int year_x, month_x, day_x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can_bang_send_sms);
        String getDays = controller.dateDay("yyyy-MM-dd");
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            String sdt = bd.getString("sdt");
            String ten = bd.getString("ten");
            editTextSdt = (EditText) findViewById(R.id.editTextSdt);
            textViewSdt = (TextView) findViewById(R.id.textViewSdt);
            editTextSdt.setText(sdt);
            textViewSdt.setText(ten);
        }

        showStatistic(getDays);
        sideBarMenu();
    }

    private void showStatistic(String getDays) {
        String queryKieu = "";
        ArrayList<String> limitNumber = controller.limitNumber();
        ArrayList<String> limitNumberBaCang = controller.limitNumberBaCang();
        sql = new DatabaseHelper(this);
        String query = "SELECT * FROM solieu_table WHERE NGAY=\"" + getDays.replaceAll("(^\\s+|\\s+$)", "").trim() + "\" ";
        Cursor table_solieu = sql.getAllDb(query);
        String query2 = "SELECT * FROM setingsend_table WHERE 1 LIMIT 1";
        Cursor setingsend_table = sql.getAllDb(query2);
        String maxde = "5000";
        String maxlo = "5000";
        String maxbacang = "5000";
        String maxXien = "5000";
        if (setingsend_table.getCount() > 0) {
            setingsend_table.moveToFirst();
            maxde = setingsend_table.getString(setingsend_table.getColumnIndex("MAXDE"));
            maxlo = setingsend_table.getString(setingsend_table.getColumnIndex("MAXLO"));
            maxbacang = setingsend_table.getString(setingsend_table.getColumnIndex("MAXBACANG"));
            maxXien = setingsend_table.getString(setingsend_table.getColumnIndex("MAXXIEN"));
        }

        Integer[] deArr = new Integer[100];
        Integer[] deTrung = new Integer[100];
        Integer[] loArr = new Integer[100];
        Integer[] loTrung = new Integer[100];
        Integer[] bacangArr = new Integer[1000];
        HashMap<String, ArrayList<String>> hashmap = sql.readkitu();
        String compareDe = sql.compareDe(getDays);
        ArrayList<String> compareLo = sql.getArrayKeyRes(getDays);
        String stringResXien = "";
        if (table_solieu.getCount() > 0) {
            ArrayList <String> allXienRes = new ArrayList<String>();
            ArrayList <String> allXienVal = new ArrayList<String>();
            ArrayList <String> allXienTh = new ArrayList<String>();
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
                    }  else if(kieuChoi.equals("xien") || kieuChoi.equals("xien2") || kieuChoi.equals("xien3") || kieuChoi.equals("xien4") ){
                        int position = -1;
                        position = allXienRes.indexOf(loto);
                        if (position == -1) {
                            if (kieuguitin.equals("send")) {
                                moicon = 0 - moicon;
                            }
                            allXienRes.add(loto);
                            allXienVal.add(String.valueOf(moicon));
                            int position2 = allXienRes.indexOf(loto);
                            if (tienThuong.equals("0")) {
                                allXienTh.add("0");
                            } else {
                                allXienTh.add("1");
                            }
                        } else {
                            Integer newValue = 0;
                            if (kieuguitin.equals("inbox")) {
                                newValue = Integer.valueOf(allXienVal.get(position)) + moicon;
                            } else {
                                newValue = Integer.valueOf(allXienVal.get(position)) - moicon;
                            }
                            allXienVal.set(Integer.parseInt(String.valueOf(position)), String.valueOf(newValue));
                        }
                    }
                }
            }
            for (int xs = 0; xs < allXienRes.size();xs++) {
                if (allXienVal.get(xs) != null && allXienTh.get(xs) != null ) {
                    int valXien = Integer.parseInt(allXienVal.get(xs)) - Integer.parseInt(maxXien);
                    if (valXien > 0) {
                        stringResXien += allXienRes.get(xs) + " x " + String.valueOf(valXien) + "n<br/>";
                    }
                }
            }
        }

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
                sortDe[z] = String.valueOf(showDe) + "_" + String.valueOf(deArr[q]);
                z++;
            }

            if (loArr[q] != null) {
                String showLo = "";
                if (q < 10) {
                    showLo = "0" + String.valueOf(q);
                } else {
                    showLo = String.valueOf(q);
                }
                sortLo[k] = String.valueOf(showLo) + "_" + String.valueOf(loArr[q]);
                k++;
            }
        }

        for (int f = 0; f < bacangArr.length; f++) {
            if (bacangArr[f] != null) {
                String showBaCang = "";
                if (f < 10) {
                    showBaCang = "00" + String.valueOf(f);
                } else if (f < 100) {
                    showBaCang = "0" + String.valueOf(f);
                } else {
                    showBaCang = String.valueOf(f);
                }
                sortBacang[s] = String.valueOf(showBaCang) + "_" + String.valueOf(bacangArr[f]);
                s++;
            }
        }

        String tempBaCang;
        for (int c = 0; c < sortBacang.length; c++) {
            if (sortBacang[c] != null) {
                String[] arrSortBacang1 = sortBacang[c].split("_");
                for (int a = c + 1; a < sortBacang.length; a++) {
                    if (sortBacang[a] != null) {
                        String[] arrSortBacang2 = sortBacang[a].split("_");
                        if (Integer.parseInt(arrSortBacang1[1]) < Integer.parseInt(arrSortBacang2[1])) {
                            tempBaCang = sortBacang[a];
                            sortBacang[a] = sortBacang[c];
                            sortBacang[c] = tempBaCang;
                            arrSortBacang1[1] = arrSortBacang2[1];
                        }
                    }
                }
            }
        }

        String tempDe;
        for (int x = 0; x < sortDe.length; x++) {
            if (sortDe[x] != null) {
                String[] arrSortDe1 = sortDe[x].split("_");
                for (int t = x + 1; t < sortDe.length; t++) {
                    if (sortDe[t] != null) {
                        String[] arrSortDe2 = sortDe[t].split("_");
                        if (Integer.parseInt(arrSortDe1[1]) < Integer.parseInt(arrSortDe2[1])) {
                            tempDe = sortDe[t];
                            sortDe[t] = sortDe[x];
                            sortDe[x] = tempDe;
                            arrSortDe1[1] = arrSortDe2[1];
                        }
                    }
                }
            }
        }

        String tempLo;
        for (int c = 0; c < sortLo.length; c++) {
            if (sortLo[c] != null) {
                String[] arrSortLo1 = sortLo[c].split("_");
                for (int r = c + 1; r < sortLo.length; r++) {
                    if (sortLo[r] != null) {
                        String[] arrSortLo2 = sortLo[r].split("_");
                        if (Integer.parseInt(arrSortLo1[1]) < Integer.parseInt(arrSortLo2[1])) {
                            tempLo = sortLo[r];
                            sortLo[r] = sortLo[c];
                            sortLo[c] = tempLo;
                            arrSortLo1[1] = arrSortLo2[1];
                        }
                    }
                }
            }
        }

        String content = "";
        for (int a = 0; a < sortDe.length; a++) {
            if (sortDe[a] != null) {
                String[] showResDe = sortDe[a].split("_");
                if (Integer.parseInt(showResDe[1]) > Integer.parseInt(maxde)) {
                    if (content.indexOf("de") == -1) {
                        content += "de ";
                    }
                    int valDe = Integer.parseInt(showResDe[1]) - Integer.parseInt(maxde);
                    content += showResDe[0] + " x " + String.valueOf(valDe) + "n ";
                }
            }
        }
        int valueFirst = 0;
        if (content.length() > 0) {
            content += "<br/>";
            valueFirst = content.length();
        }

        for (int b = 0; b < sortLo.length; b++) {
            if (sortLo[b] != null) {
                String[] showResLo = sortLo[b].split("_");
                if (Integer.parseInt(showResLo[1]) > Integer.parseInt(maxlo)) {
                    if (content.indexOf("lo") == -1) {
                        content += "lo ";
                    }
                    int valLo = Integer.parseInt(showResLo[1]) - Integer.parseInt(maxlo);
                    content += showResLo[0] + " x " + String.valueOf(valLo) + "d ";
                }
            }
        }

        if (content.length() > valueFirst) {
            content += "<br/>";
            int valueSecond = content.length();
        }

        for (int n = 0; n < sortBacang.length; n++) {
            if (sortBacang[n] != null) {
                String[] showResBaCang = sortBacang[n].split("_");
                if (Integer.parseInt(showResBaCang[1]) > Integer.parseInt(maxbacang)) {
                    if (content.indexOf("3c") == -1) {
                        content += "3c ";
                    }
                    int valBaCang = Integer.parseInt(showResBaCang[1]) - Integer.parseInt(maxbacang);
                    content += showResBaCang[0] + " x " + String.valueOf(valBaCang) + "n <br/>";
                }
            }
        }
        if (!stringResXien.equals("")) {
            content += "xiên " + stringResXien;
        }

        contentSend = (EditText) findViewById(R.id.editTextContentSms);
        contentSend.setText(Html.fromHtml(content));

        sendSms = (Button) findViewById(R.id.buttonGuiCanBang);
        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdtCus = (EditText) findViewById(R.id.editTextSdt);
                String phoneNo = sdtCus.getText().toString();
                controller = new GlobalClass();
                String message = controller.uniStrip(contentSend.getText().toString());
                if (phoneNo.length() > 0 && message.length() > 0) {
                    sendMessage(phoneNo, message);
                } else {
                    Toast.makeText(CanBangSendSms.this, "Nhập giá trị số hoặc nội dung tin nhắn", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private int countSms(String sdt) {
        String getDays = controller.dateDay("yyyy-MM-dd");
        String dayDefault = controller.dateDayNoTimeZone(getDays + " 23:59:59"); // lay ngay theo gio nuoc anh mac dinh
        long dayMillsecond = controller.converDayToMill("yyyy-MM-dd HH:mm:ss", dayDefault);
        long lastMillsecond = dayMillsecond - 1000L * 60L * 60L * 24L;
        Uri mSmsinboxQueryUri = Uri.parse("content://sms/");
        String stdRe = "+84" + sdt.substring(1, sdt.length());
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        String filter = "(address='" + sdt + "' or address='" + stdRe + "') and date>=" + lastMillsecond +
                " and date <=" + dayMillsecond;
        Cursor cursor1 = getContentResolver().query(mSmsinboxQueryUri, projection, filter, null, "_id desc");
        return cursor1.getCount();
    }

    protected void sendMessage(final String phoneNo, String message) {
        try {
            final int smsCountDefault = countSms(phoneNo);
            message = message.replace("<br/>", " ");

            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(message);
            sms.sendMultipartTextMessage(phoneNo, null, parts, null, null);
            SmsManager smsManager = SmsManager.getDefault();
            loadingimage= (ProgressBar) findViewById(R.id.progressBar);
            loadingimage.setVisibility(View.VISIBLE);
            Toast.makeText(CanBangSendSms.this, "đang gửi tin nhắn", Toast.LENGTH_LONG).show();

            long start = 60;
            long end = 3;

            final CountDownTimer timer = new CountDownTimer(start * 1000, end * 1000) {
                public void onTick(long millisUntilFinished) {
                    int smsCountNew = countSms(phoneNo);
                    if (smsCountDefault < smsCountNew) {
                        loadingimage= (ProgressBar) findViewById(R.id.progressBar);
                        loadingimage.setVisibility(View.GONE);
                        contentSend = (EditText) findViewById(R.id.editTextContentSms);
                        contentSend.setText("");
                        controller.showAlertDialog( CanBangSendSms.this, "Thông báo", "Gửi tin nhắn thành công" );
                        this.cancel();
                    }
                }
                public void onFinish() {
                    loadingimage= (ProgressBar) findViewById(R.id.progressBar);
                    loadingimage.setVisibility(View.GONE);
                    controller.showAlertDialog( CanBangSendSms.this, "Thông báo", "Chưa gửi được tin nhắn! làm ơn thử lại!" );
                }

            };
            timer.start();

        } catch (Exception e) {
            Toast.makeText(CanBangSendSms.this, "Đã có lỗi xảy ra trong quá trình gửi", Toast.LENGTH_LONG).show();
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
                        Intent intent = new Intent(CanBangSendSms.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(CanBangSendSms.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(CanBangSendSms.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(CanBangSendSms.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(CanBangSendSms.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(CanBangSendSms.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(CanBangSendSms.this, viewSmsNotMoney.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
