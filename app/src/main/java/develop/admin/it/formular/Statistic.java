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
import java.util.Collections;
import java.util.HashMap;

public class Statistic extends AppCompatActivity {
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
        setContentView(R.layout.activity_statistic);
        controller = new GlobalClass();
        String getDays = controller.dateDay("yyyy-MM-dd");
//        int time = 24 * 60 * 60 * 1000;
//        String getDays = controller.dateDayChange("yyyy-MM-dd",time);
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

        TextView tong = new TextView(Statistic.this);
        String dateText = "<font color=\"blue\"><big>" + getDays + "</big></font><br/>";
        String tongText = "";
        showDate = (TextView) findViewById(R.id.textViewDate);
        showDate.setText(Html.fromHtml(dateText));

        TableRow tr2 = new TableRow(Statistic.this);
        TextView de = new TextView(Statistic.this);
        TextView lo = new TextView(Statistic.this);

        TableRow tr3 = new TableRow(Statistic.this);
        TextView bacang = new TextView(Statistic.this);
        TextView xi = new TextView(Statistic.this);

        Integer[] deArr = new Integer[100];
        Integer[] deTrung = new Integer[100];
        Integer[] dedauArr = new Integer[100];
        Integer[] dedauTrung = new Integer[100];
        Integer[] loArr = new Integer[100];
        Integer[] loTrung = new Integer[100];
        Integer[] loDauArr = new Integer[100];
        Integer[] loDauTrung = new Integer[100];
        Integer[] bacangArr = new Integer[1000];
        HashMap<String, ArrayList<String>> hashmap = sql.readkitu();
        String compareDe = sql.compareDe(getDays);
        String compareDeDau = sql.compareDeDau(getDays);
        ArrayList<String> compareLo = sql.getArrayKeyRes(getDays);
        ArrayList<String> compareLoDau = sql.getLoDauGiai(getDays);
        // de Inbox
        double tongDanhDeInbox = 0;
        double tongDanhDeInboxSms = 0;
        double tongThuongDeInbox = 0;
        double tongThuongDeInboxSms = 0;
        // de Send
        double tongDanhDeSend = 0;
        double tongDanhDeSendSms = 0;
        double tongThuongDeSend = 0;
        double tongThuongDeSendSms = 0;

        // de dau Inbox
        double tongDanhDeDauInbox = 0;
        double tongDanhDeDauInboxSms = 0;
        double tongThuongDeDauInbox = 0;
        double tongThuongDeDauInboxSms = 0;
        // de dau Send
        double tongDanhDeDauSend = 0;
        double tongDanhDeDauSendSms = 0;
        double tongThuongDeDauSend = 0;
        double tongThuongDeDauSendSms = 0;

        // giai nhat B Inbox
        double tongDanhgiainhatInbox = 0;
        double tongDanhgiainhatInboxSms = 0;
        double tongThuonggiainhatInbox = 0;
        double tongThuonggiainhatInboxSms = 0;
        // giai nhat B Send
        double tongDanhgiainhatSend = 0;
        double tongDanhgiainhatSendSms = 0;
        double tongThuonggiainhatSend = 0;
        double tongThuonggiainhatSendSms = 0;

        // giai nhat A Inbox
        double tongDanhgiainhatDauInbox = 0;
        double tongDanhgiainhatDauInboxSms = 0;
        double tongThuonggiainhatDauInbox = 0;
        double tongThuonggiainhatDauInboxSms = 0;
        // giai nhat A Send
        double tongDanhgiainhatDauSend = 0;
        double tongDanhgiainhatDauSendSms = 0;
        double tongThuonggiainhatDauSend = 0;
        double tongThuonggiainhatDauSendSms = 0;

        // lo inbox
        double tongDanhLoInbox = 0;
        double tongDanhLoInboxSms = 0;
        double tongThuongLoInbox = 0;
        double tongThuongLoInboxSms = 0;
        // lo send
        double tongDanhLoSend = 0;
        double tongThuongLoSend = 0;
        double tongDanhLoSendSms = 0;
        double tongThuongLoSendSms = 0;

        // lo dau inbox
        double tongDanhLoDauInbox = 0;
        double tongDanhLoDauInboxSms = 0;
        double tongThuongLoDauInbox = 0;
        double tongThuongLoDauInboxSms = 0;
        // lo dau send
        double tongDanhLoDauSend = 0;
        double tongThuongLoDauSend = 0;
        double tongDanhLoDauSendSms = 0;
        double tongThuongLoDauSendSms = 0;

        // ba cang inbox
        double tongDanhBaCangInbox = 0;
        double tongDanhBaCangInboxSms = 0;
        double tongThuongBaCangInbox = 0;
        double tongThuongBaCangInboxSms = 0;
        // ba cang send
        double tongDanhBaCangSend = 0;
        double tongThuongBaCangSend = 0;
        double tongDanhBaCangSendSms = 0;
        double tongThuongBaCangSendSms = 0;
        // xien inbox
        double tongDanhXienInbox = 0;
        double tongDanhXienInboxSms = 0;
        double tongThuongXienInbox = 0;
        double tongThuongXienInboxSms = 0;
        // xien send
        double tongDanhXienSend = 0;
        double tongThuongXienSend = 0;
        double tongDanhXienSendSms = 0;
        double tongThuongXienSendSms = 0;

        // xien dau inbox
        double tongDanhXienDauInbox = 0;
        double tongDanhXienDauInboxSms = 0;
        double tongThuongXienDauInbox = 0;
        double tongThuongXienDauInboxSms = 0;
        // xien dau send
        double tongDanhXienDauSend = 0;
        double tongThuongXienDauSend = 0;
        double tongDanhXienDauSendSms = 0;
        double tongThuongXienDauSendSms = 0;

        double tongBangInbox = 0;
        double tongBangSend = 0;
        String stringResXien = "<font color =\"blue\"><big>Xiên</big></font><br/>";

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
                switch (kieuguitin) {
                    case "inbox":
                        tongBangInbox += Math.round(Double.parseDouble(tongbang) * 100.0) / 100.0;
                        break;
                    case "send":
                        tongBangSend += Math.round(Double.parseDouble(tongbang) * 100.0) / 100.0;
                        break;
                }
                switch (kieuChoi) {
                    case "de":
                        if (kieuguitin.equals("inbox")) {
                            tongDanhDeInbox += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongDeInbox += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhDeInboxSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongDeInboxSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        } else if (kieuguitin.equals("send")) {
                            tongDanhDeSend += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongDeSend += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhDeSendSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongDeSendSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        }
                        break;
                    case "dq":
                        if (kieuguitin.equals("inbox")) {
                            tongDanhDeDauInbox += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongDeDauInbox += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhDeDauInboxSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongDeDauInboxSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        } else if (kieuguitin.equals("send")) {
                            tongDanhDeDauSend += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongDeDauSend += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhDeDauSendSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongDeDauSendSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        }
                        break;
                    case "g1a":
                        if (kieuguitin.equals("inbox")) {
                            tongDanhgiainhatDauInbox += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuonggiainhatDauInbox += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhgiainhatDauInboxSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuonggiainhatDauInboxSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        } else if (kieuguitin.equals("send")) {
                            tongDanhgiainhatDauSend += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuonggiainhatDauSend += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhgiainhatDauSendSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuonggiainhatDauSendSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        }
                        break;
                    case "g1b":
                        if (kieuguitin.equals("inbox")) {
                            tongDanhgiainhatInbox += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuonggiainhatInbox += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhgiainhatInboxSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuonggiainhatInboxSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        } else if (kieuguitin.equals("send")) {
                            tongDanhgiainhatSend += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuonggiainhatSend += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhgiainhatSendSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuonggiainhatSendSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        }
                        break;
                    case "lo":
                        if (kieuguitin.equals("inbox")) {
                            tongDanhLoInbox += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongLoInbox += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhLoInboxSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongLoInboxSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        } else if (kieuguitin.equals("send")) {
                            tongDanhLoSend += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongLoSend += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhLoSendSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongLoSendSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        }
                        break;
                    case "ld":
                        if (kieuguitin.equals("inbox")) {
                            tongDanhLoDauInbox += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongLoDauInbox += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhLoDauInboxSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongLoDauInboxSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        } else if (kieuguitin.equals("send")) {
                            tongDanhLoDauSend += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongLoDauSend += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhLoDauSendSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongLoDauSendSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        }
                        break;
                    case "bacang":
                        if (kieuguitin.equals("inbox")) {
                            tongDanhBaCangInbox += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongBaCangInbox += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhBaCangInboxSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongBaCangInboxSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        } else if (kieuguitin.equals("send")) {
                            tongDanhBaCangSend += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongBaCangSend += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhBaCangSendSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongBaCangSendSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        }
                        break;
                    case "xien":
                    case "xien2":
                    case "xien3":
                    case "xien4":
                        if (kieuguitin.equals("inbox")) {
                            tongDanhXienInbox += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongXienInbox += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhXienInboxSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongXienInboxSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        } else if (kieuguitin.equals("send")) {
                            tongDanhXienSend += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongXienSend += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhXienSendSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongXienSendSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        }
                        break;
                    case "sa":
                    case "sa2":
                    case "sa3":
                    case "sa4":
                        if (kieuguitin.equals("inbox")) {
                            tongDanhXienDauInbox += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongXienDauInbox += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhXienDauInboxSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongXienDauInboxSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        } else if (kieuguitin.equals("send")) {
                            tongDanhXienDauSend += Math.round(Double.parseDouble(tienDanh) * 100.0) / 100.0;
                            tongThuongXienDauSend += Math.round(Double.parseDouble(tienThuong) * 100.0) / 100.0;
                            tongDanhXienDauSendSms += Math.round(Double.parseDouble(tienDanhSms) * 100.0) / 100.0;
                            tongThuongXienDauSendSms += Math.round(Double.parseDouble(tienThuongSms) * 100.0) / 100.0;
                        }
                        break;
                }

                if (hashmap.get(loto) != null) {
                    String value = hashmap.get(loto).get(0);
                    String[] arrVal = value.split(",");
                    if (kieuChoi.equals("de")) {
                        for (int i = 0; i < arrVal.length; i++) {
                            int solo = Integer.parseInt(String.valueOf(arrVal[i].replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "").replace(" ", "")));
                            if(kieuguitin.equals("inbox")) {
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
                            String compreIntDe = String.valueOf(arrVal[i]);
                            if (solo < 10 ) {
                                compreIntDe = "0" + String.valueOf(arrVal[i]);
                            }
                            if (compareDe.equals(compreIntDe)) {
                                deTrung[solo] = 1;
                            } else {
                                deTrung[solo] = 0;
                            }
                        }
                    } else if (kieuChoi.equals("dq")) {
                        for (int i = 0; i < arrVal.length; i++) {
                            int solo = Integer.parseInt(String.valueOf(arrVal[i].replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "").replace(" ", "")));
                            if(kieuguitin.equals("inbox")) {
                                if (dedauArr[solo] != null) {
                                    dedauArr[solo] += moicon;
                                } else {
                                    dedauArr[solo] = moicon;
                                }
                            } else {
                                if (dedauArr[solo] != null) {
                                    dedauArr[solo] -= moicon;
                                } else {
                                    dedauArr[solo] = 0 - moicon;
                                }
                            }
                            String compreIntDeDau = String.valueOf(arrVal[i]);
                            if (solo < 10 ) {
                                compreIntDeDau = "0" + String.valueOf(arrVal[i]);
                            }
                            if (compareDeDau.equals(compreIntDeDau)) {
                                dedauTrung[solo] = 1;
                            } else {
                                dedauTrung[solo] = 0;
                            }
                        }
                    } else if (kieuChoi.equals("lo")) {
                        for (int i = 0; i < arrVal.length; i++) {
                            int solo = Integer.parseInt(String.valueOf(arrVal[i].replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "").replace(" ", "")));
                            if(kieuguitin.equals("inbox")) {
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
                            String compreIntLo = String.valueOf(solo);
                            if (solo < 10 ) {
                                compreIntLo = "0" + String.valueOf(solo);
                            }
                            loTrung[solo] = Collections.frequency(compareLo, String.valueOf(compreIntLo));
                        }
                    } else if (kieuChoi.equals("ld")) {
                        for (int i = 0; i < arrVal.length; i++) {
                            int solo = Integer.parseInt(String.valueOf(arrVal[i].replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "").replace(" ", "")));
                            if(kieuguitin.equals("inbox")) {
                                if (loDauArr[solo] != null) {
                                    loDauArr[solo] += moicon;
                                } else {
                                    loDauArr[solo] = moicon;
                                }
                            } else {
                                if (loDauArr[solo] != null) {
                                    loDauArr[solo] -= moicon;
                                } else {
                                    loDauArr[solo] = 0 - moicon;
                                }
                            }
                            String compreIntLoDau = String.valueOf(solo);
                            if (solo < 10 ) {
                                compreIntLoDau = "0" + String.valueOf(solo);
                            }
                            loDauTrung[solo] = Collections.frequency(compareLoDau, String.valueOf(compreIntLoDau));
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
                            String compreIntDe = String.valueOf(intLoTo);
                            if (intLoTo < 10 ) {
                                compreIntDe = "0" + String.valueOf(intLoTo);
                            }
                            if (compareDe.equals(compreIntDe)) {
                                deTrung[intLoTo] = 1;
                            } else {
                                deTrung[intLoTo] = 0;
                            }
                        } else if (kieuChoi.equals("dq")) {
                            int intLoTo = Integer.parseInt(String.valueOf(loto.replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "").replace(" ", "")));
                            if (kieuguitin.equals("inbox")) {
                                if (dedauArr[intLoTo] != null) {
                                    dedauArr[intLoTo] += moicon;
                                } else {
                                    dedauArr[intLoTo] = moicon;
                                }
                            } else {
                                if (dedauArr[intLoTo] != null) {
                                    dedauArr[intLoTo] -= moicon;
                                } else {
                                    dedauArr[intLoTo] = 0 - moicon;
                                }
                            }
                            String compreIntDeDau = String.valueOf(intLoTo);
                            if (intLoTo < 10 ) {
                                compreIntDeDau = "0" + String.valueOf(intLoTo);
                            }
                            if (compareDeDau.equals(compreIntDeDau)) {
                                dedauTrung[intLoTo] = 1;
                            } else {
                                dedauTrung[intLoTo] = 0;
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
                            String compreIntLoTo = String.valueOf(intLoTo);
                            if (intLoTo < 10 ) {
                                compreIntLoTo = "0" + String.valueOf(intLoTo);
                            }
                            loTrung[intLoTo] = Collections.frequency(compareLo, compreIntLoTo);
                        }  else if (kieuChoi.equals("ld")) {
                            int intLoTo = Integer.parseInt(String.valueOf(loto.replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "").replace(" ", "")));
                            if (kieuguitin.equals("inbox")) {
                                if (loDauArr[intLoTo] != null) {
                                    loDauArr[intLoTo] += moicon;
                                } else {
                                    loDauArr[intLoTo] = moicon;
                                }
                            } else {
                                if (loDauArr[intLoTo] != null) {
                                    loDauArr[intLoTo] -= moicon;
                                } else {
                                    loDauArr[intLoTo] = 0 - moicon;
                                }
                            }
                            String compreIntLoDau = String.valueOf(intLoTo);
                            if (intLoTo < 10 ) {
                                compreIntLoDau = "0" + String.valueOf(intLoTo);
                            }
                            loDauTrung[intLoTo] = Collections.frequency(compareLoDau, compreIntLoDau);
                        }
                    } else if(limitNumberBaCang.contains(loto.replaceAll("[^\\d.]", "").replaceAll("(^\\s+|\\s+$)", "")) && kieuChoi.equals("bacang")) {
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
                    } else if(kieuChoi.equals("xien") || kieuChoi.equals("xien2") || kieuChoi.equals("xien3") || kieuChoi.equals("xien4") ){
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

            int sstxi = 0;
            for (int xs = 0; xs < allXienRes.size();xs++) {
                if (allXienVal.get(xs) != null && allXienTh.get(xs) != null ) {
                    sstxi++;
                    stringResXien += sstxi+".<big><big><b><font color=\"red\"> &nbsp;" +
                            allXienRes.get(xs) + " </font>" + "=" + allXienVal.get(xs) + "n</b></big>" +
                            "<font color=\"blue\">" +  allXienTh.get(xs) + "</font></big><br/>";
                }
            }

        }

        String textDeLeft = "<font color =\"blue\"><big>Đề</big></font><br/>";
        String textDeRight = "<font color =\"blue\"><big> </big></font><br/>";
        String textDeDauLeft = "<font color =\"blue\"><big>Đề Đầu</big></font><br/>";
        String textDeDauRight = "<font color =\"blue\"><big> </big></font><br/>";
        String textLoLeft = "<font color =\"blue\"><big>Lô</big></font><br/>";
        String textLoRight = "<font color =\"blue\"> </font><br/>";
        String textLoDauLeft = "<font color =\"blue\"><big>Lô Đầu</big></font><br/>";
        String textLoDauRight = "<font color =\"blue\"> </font><br/>";
        String textBaCang = "<font color =\"blue\"><big>Ba Càng</big></font><br/>";

        String[] sortDe = new String[100];
        String[] sortDeDau = new String[100];
        String[] sortLo = new String[100];
        String[] sortLoDau = new String[100];
        String[] sortBacang = new String[1000];

        int z = 0;
        int k = 0;
        int s = 0;
        int h = 0;
        int w = 0;

        for (int q = 0; q < deArr.length; q++) {
            String showDe = "";
            if (q < 10) {
                showDe = "0" + String.valueOf(q);
            } else {
                showDe = String.valueOf(q);
            }

            if (deArr[q] != null) {
                sortDe[z] = String.valueOf(showDe) + "java" + String.valueOf(deArr[q]) + "java" + String.valueOf(deTrung[q]);
            } else {
               // sortDe[z] = String.valueOf(showDe) + "java0java0"; // xoa di neu k phai a vuong
            }
            z++;

            String showDeDau = "";
            if (q < 10) {
                showDeDau = "0" + String.valueOf(q);
            } else {
                showDeDau = String.valueOf(q);
            }

            if (dedauArr[q] != null) {
                sortDeDau[h] = String.valueOf(showDeDau) + "java" + String.valueOf(dedauArr[q]) + "java" + String.valueOf(dedauTrung[q]);
            } else {
              //  sortDeDau[h] = String.valueOf(showDeDau) + "java0java0"; // xoa di neu k phai a vuong
            }
            h++;

            String showLo = "";
            if (q < 10) {
                showLo = "0" + String.valueOf(q);
            } else {
                showLo = String.valueOf(q);
            }

            if (loArr[q] != null) {
                sortLo[k] = String.valueOf(showLo) + "java" + String.valueOf(loArr[q]) + "java" + String.valueOf(loTrung[q]);
            } else {
               // sortLo[k] = String.valueOf(showLo) + "java0java0"; // xoa di neu k phai a vuong
            }
            k++;

            String showLoDau = "";
            if (q < 10) {
                showLoDau = "0" + String.valueOf(q);
            } else {
                showLoDau = String.valueOf(q);
            }

            if (loDauArr[q] != null) {
                sortLoDau[w] = String.valueOf(showLoDau) + "java" + String.valueOf(loDauArr[q]) + "java" + String.valueOf(loDauTrung[q]);
            } else {
               // sortLoDau[w] = String.valueOf(showLoDau) + "java0java0"; // xoa di neu k phai a vuong
            }
            w++;
        }

        ArrayList<String> bacangsortarray = new ArrayList<>();
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
                sortBacang[s] = String.valueOf(showBaCang) + "java" + String.valueOf(bacangArr[f]);
                bacangsortarray.add(sortBacang[s]);
                s++;
            }
        }

        String tempDe;
        for (int x = 0; x < sortDe.length; x++) {
            if (sortDe[x] != null) {
                String[] arrSortDe1 = sortDe[x].split("java");
                for (int t = x + 1; t < sortDe.length; t++) {
                    if (sortDe[t] != null) {
                        String[] arrSortDe2 = sortDe[t].split("java");
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

        String tempDeDau;
        for (int m = 0; m < sortDeDau.length; m++) {
            if (sortDeDau[m] != null) {
                String[] arrSortDe1 = sortDeDau[m].split("java");
                for (int t = m + 1; t < sortDeDau.length; t++) {
                    if (sortDeDau[t] != null) {
                        String[] arrSortDe2 = sortDeDau[t].split("java");
                        if (Integer.parseInt(arrSortDe1[1]) < Integer.parseInt(arrSortDe2[1])) {
                            tempDe = sortDeDau[t];
                            sortDeDau[t] = sortDeDau[m];
                            sortDeDau[m] = tempDe;
                            arrSortDe1[1] = arrSortDe2[1];
                        }
                    }
                }
            }
        }

        String tempLo;
        for (int c = 0; c < sortLo.length; c++) {
            if (sortLo[c] != null) {
                String[] arrSortLo1 = sortLo[c].split("java");
                for (int r = c + 1; r < sortLo.length; r++) {
                    if (sortLo[r] != null) {
                        String[] arrSortLo2 = sortLo[r].split("java");
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

        String tempLoDau = "";
        for (int lc = 0; lc < sortLoDau.length; lc++) {
            if (sortLoDau[lc] != null) {
                String[] arrSortLoDau1 = sortLoDau[lc].split("java");
                for (int lr = lc + 1; lr < sortLoDau.length; lr++) {
                    if (sortLoDau[lr] != null) {
                        String[] arrSortLoDau2 = sortLoDau[lr].split("java");
                        if (Integer.parseInt(arrSortLoDau1[1]) < Integer.parseInt(arrSortLoDau2[1])) {
                            tempLoDau = sortLoDau[lr];
                            sortLoDau[lr] = sortLoDau[lc];
                            sortLoDau[lc] = tempLoDau;
                            arrSortLoDau1[1] = arrSortLoDau2[1];
                        }
                    }
                }
            }
        }
        int aq = 0;
        for (int a = 0; a < sortDe.length; a++) {
            if (sortDe[a] != null) {
                aq++;
                String[] showResDe = sortDe[a].split("java");
                if (Integer.parseInt(showResDe[2]) == 0) {
//                    if (a < 50) {
                        textDeLeft += aq+".<big><big><b><font color=\"red\">" + showResDe[0] + " </font>" +
                                showResDe[1] + "n</b></big></big><br />";
//                    } else {
//                        textDeRight += aq+".<big><big><b><font color=\"red\">." + showResDe[0] + " </font>" +
//                                showResDe[1] + "n</b></big></big><br />";
//                    }
                } else {
                   // if (a < 50) {
                        textDeLeft += aq+".<big><big><b><font color=\"red\" >" + showResDe[0] + "</font>" +
                                "<font color=\"blue\"> " + showResDe[2] + "</font> " +
                                showResDe[1] + "n</b></big></big><br />";
//                    } else {
//                        textDeRight += aq+".<big><big><b><font color=\"red\" >" + showResDe[0] + "</font>" +
//                                "<font color=\"blue\"> " + showResDe[2] + "</font> " +
//                                showResDe[1] + "n</b></big></big><br />";
//                    }
                }
            }
        }

        int aw = 0;
        for (int f = 0; f < sortDeDau.length; f++) {
            if (sortDeDau[f] != null) {
                aw++;
                String[] showResDeDau = sortDeDau[f].split("java");
                if (Integer.parseInt(showResDeDau[2]) == 0) {
                  //  if (f < 50) {
                        textDeDauLeft += aw+".<big><big><b><font color=\"red\">" + showResDeDau[0] + " </font>" +
                            showResDeDau[1] + "n</b></big></big><br />";
//                    } else {
//                        textDeDauRight += aw+".<big><big><b><font color=\"red\">" + showResDeDau[0] + " </font>" +
//                                showResDeDau[1] + "n</b></big></big><br />";
//                    }
                } else {
                  //  if (f < 50) {
                        textDeDauLeft += aw+".<big><big><b><font color=\"red\" >" + showResDeDau[0] + "</font>" +
                            "<font color=\"blue\"> " + showResDeDau[2] + "</font> " +
                            showResDeDau[1] + "n</b></big></big><br />";
//                    } else {
//                        textDeDauRight += aw+".<big><big><b><font color=\"red\" >" + showResDeDau[0] + "</font>" +
//                                "<font color=\"blue\"> " + showResDeDau[2] + "</font> " +
//                                showResDeDau[1] + "n</b></big></big><br />";
//                    }
                }
            }
        }

        int ap = 0;
        for (int b = 0; b < sortLo.length; b++) {
            if (sortLo[b] != null) {
                ap++;
                String[] showResLo = sortLo[b].split("java");
                if (Integer.parseInt(showResLo[2]) == 0) {
                   // if (b < 50) {
                        textLoLeft +=  ap+".<big><big><b><font color=\"red\" >" +
                                showResLo[0] + "  </font>"
                                + showResLo[1] + "d</b></big></big><br />";
//                    } else {
//                        textLoRight += ap+".<big><big><b><font color=\"red\" > &nbsp;" +
//                                showResLo[0] + "  </font>"
//                                + showResLo[1] + "d</b></big></big><br />";
//                    }
                } else {
                   // if (b < 50) {
                        textLoLeft +=  ap+".<big><big><b><font color=\"red\" >"
                                + showResLo[0] + "</font>" +
                                "<font color=\"blue\">" + showResLo[2] + "</font> "
                                + showResLo[1] + "d</b></big></big><br />";
//                    } else {
//                        textLoRight += ap+".<big><big><b><font color=\"red\" > &nbsp;"
//                                + showResLo[0] + "</font>" +
//                                "<font color=\"blue\">" + showResLo[2] + "</font> "
//                                + showResLo[1] + "d</b></big></big><br />";
//                    }
                }
            }
        }

        int at = 0;
        for (int lb = 0; lb < sortLoDau.length; lb++) {
            if (sortLoDau[lb] != null) {
                at++;
                String[] showResLoDau = sortLoDau[lb].split("java");
                if (Integer.parseInt(showResLoDau[2]) == 0) {
                    if (lb < 50) {
                    textLoDauLeft += at+".<big><big><b><font color=\"red\" > &nbsp;" +
                            showResLoDau[0] + "  </font>"
                            + showResLoDau[1] + "d</b></big></big><br />";
                    } else {
                        textLoDauRight += at+".<big><big><b><font color=\"red\" > &nbsp;" +
                                showResLoDau[0] + "  </font>"
                                + showResLoDau[1] + "d</b></big></big><br />";
                    }
                } else {
                    if (lb < 50) {
                    textLoDauLeft += at+".<big><big><b><font color=\"red\" > &nbsp;"
                            + showResLoDau[0] + "</font>" +
                            "<font color=\"blue\">" + showResLoDau[2] + "</font> "
                            + showResLoDau[1] + "d</b></big></big><br />";
                    } else {
                        textLoDauRight += at+".<big><big><b><font color=\"red\" > &nbsp;"
                                + showResLoDau[0] + "</font>" +
                                "<font color=\"blue\">" + showResLoDau[2] + "</font> "
                                + showResLoDau[1] + "d</b></big></big><br />";
                    }
                }
            }
        }

        int ay = 0;

        String tempBaCang = "";
        for (int vb = 0; vb < sortBacang.length; vb++) {
            if (sortBacang[vb] != null) {
                String[] arrSortBacang1 = sortBacang[vb].split("java");
                for (int vn = vb + 1; vn < sortLoDau.length; vn++) {
                    if (sortBacang[vn] != null) {
                        String[] arrSortLoDau2 = sortBacang[vn].split("java");
                        if (Integer.parseInt(arrSortBacang1[1]) < Integer.parseInt(arrSortLoDau2[1])) {
                            tempBaCang = sortBacang[vn];
                            sortBacang[vn] = sortBacang[vb];
                            sortBacang[vb] = tempBaCang;
                            arrSortBacang1[1] = arrSortLoDau2[1];
                        }
                    }
                }
            }
        }

        for (int n = 0; n < sortBacang.length; n++) {
            if (sortBacang[n] != null) {
                ay++;
                String[] showResBaCang = sortBacang[n].split("java");
                textBaCang += ay+".<big><big><b><font color=\"red\">" + showResBaCang[0] + " </font>"
                        + showResBaCang[1] + "n</b></big></big><br/>";
            }
        }

        /***************** thong tin phan inbox sms **************************/
        /***************** chu y cac dong thuong thi dau bao do bị nguoc lại **************/
        if (tongDanhDeInbox != 0 || tongDanhLoInbox != 0 ||
                tongDanhXienInbox != 0 || tongDanhBaCangInbox != 0 ||
                tongDanhgiainhatInbox != 0 || tongDanhgiainhatDauInbox != 0) {
            tongText += "-----tổng hợp tin nhắn gửi đến-----<br/>";
        }
        if (tongDanhDeInbox < 0) {
            tongText += "<font color=\"red\"><big>de" +
                    "=" + String.valueOf(Math.round(tongDanhDeInboxSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhDeInbox * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongDanhDeInbox > 0) {
            tongText += "<font color=\"blue\"><big>de" +
                    "=" + String.valueOf(Math.round(tongDanhDeInboxSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhDeInbox * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        }
        if (tongThuongDeInbox > 0) {
            tongText += "<font color=\"red\"><big>thde" +
                    "=" + String.valueOf(Math.round(tongThuongDeInboxSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongDeInbox * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongThuongDeInbox < 0) {
            tongText += "<font color=\"blue\"><big>thde" +
                    "=" + String.valueOf(Math.round(tongThuongDeInboxSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongDeInbox * -1 * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }

        if (tongDanhDeDauInbox < 0) {
            tongText += "<font color=\"red\"><big>de dau" +
                    "=" + String.valueOf(Math.round(tongDanhDeDauInboxSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhDeDauInbox * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongDanhDeDauInbox > 0) {
            tongText += "<font color=\"blue\"><big>de dau" +
                    "=" + String.valueOf(Math.round(tongDanhDeDauInboxSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhDeDauInbox * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        }
        if (tongThuongDeDauInbox > 0) {
            tongText += "<font color=\"red\"><big>thde dau" +
                    "=" + String.valueOf(Math.round(tongThuongDeDauInboxSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongDeDauInbox * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongThuongDeInbox < 0) {
            tongText += "<font color=\"blue\"><big>thde dau" +
                    "=" + String.valueOf(Math.round(tongThuongDeDauInboxSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongDeDauInbox * -1 * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }

        if (tongDanhgiainhatDauInbox < 0) {
            tongText += "giainhatA" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatDauInboxSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatDauInbox * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongDanhgiainhatDauInbox > 0) {
            tongText += "giainhatA" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatDauInboxSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatDauInbox * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        if (tongThuonggiainhatDauInbox > 0) {
            tongText += "thgiainhatA" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatDauInboxSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatDauInbox * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongThuonggiainhatDauInbox < 0) {
            tongText += "thgiainhatA" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatDauInboxSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatDauInbox * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        }

        if (tongDanhgiainhatInbox < 0) {
            tongText += "giainhatB" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatInboxSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatInbox * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongDanhgiainhatInbox > 0) {
            tongText += "giainhatB" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatInboxSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatInbox * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        if (tongThuonggiainhatInbox > 0) {
            tongText += "thgiainhatB" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatInboxSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatInbox * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongThuonggiainhatInbox < 0) {
            tongText += "thgiainhatB" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatInboxSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatInbox * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        }

        if (tongDanhLoInbox < 0) {
            tongText += "<font color=\"red\"><big>lo" +
                    "=" + String.valueOf(Math.round(tongDanhLoInboxSms * -1 * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongDanhLoInbox * -1 * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        } else if (tongDanhLoInbox > 0) {
            tongText += "<font color=\"blue\"><big>lo" +
                    "=" + String.valueOf(Math.round(tongDanhLoInboxSms * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongDanhLoInbox * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        }
        if (tongThuongLoInbox > 0) {
            tongText += "<font color=\"red\"><big>thlo" +
                    "=" + String.valueOf(Math.round(tongThuongLoInboxSms * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongThuongLoInbox * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        } else if (tongThuongLoInbox < 0) {
            tongText += "<font color=\"blue\"><big>thlo" +
                    "=" + String.valueOf(Math.round(tongThuongLoInboxSms * -1 * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongThuongLoInbox * -1 * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        }

        if (tongDanhLoDauInbox < 0) {
            tongText += "<font color=\"red\"><big>lo dau" +
                    "=" + String.valueOf(Math.round(tongDanhLoDauInboxSms * -1 * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongDanhLoDauInbox * -1 * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        } else if (tongDanhLoDauInbox > 0) {
            tongText += "<font color=\"blue\"><big>lo dau" +
                    "=" + String.valueOf(Math.round(tongDanhLoDauInboxSms * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongDanhLoDauInbox * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        }
        if (tongThuongLoDauInbox > 0) {
            tongText += "<font color=\"red\"><big>thlo dau" +
                    "=" + String.valueOf(Math.round(tongThuongLoDauInboxSms * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongThuongLoDauInbox * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        } else if (tongThuongLoDauInbox < 0) {
            tongText += "<font color=\"blue\"><big>thlo dau" +
                    "=" + String.valueOf(Math.round(tongThuongLoDauInboxSms * -1 * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongThuongLoDauInbox * -1 * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        }

        if (tongDanhXienInbox < 0) {
            tongText += "<font color=\"red\"><big>xien" +
                    "=" + String.valueOf(Math.round(tongDanhXienInboxSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhXienInbox * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongDanhXienInbox > 0) {
            tongText += "<font color=\"blue\"><big>xien" +
                    "=" + String.valueOf(Math.round(tongDanhXienInboxSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhXienInbox * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }
        if (tongThuongXienInbox > 0) {
            tongText += "<font color=\"red\"><big>thxien" +
                    "=" + String.valueOf(Math.round(tongThuongXienInboxSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongXienInbox * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongThuongXienInbox < 0) {
            tongText += "<font color=\"blue\"><big>thxien" +
                    "=" + String.valueOf(Math.round(tongThuongXienInboxSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongXienInbox * -1 * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }

        if (tongDanhXienDauInbox < 0) {
            tongText += "<font color=\"red\"><big>xidau" +
                    "=" + String.valueOf(Math.round(tongDanhXienDauInboxSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhXienDauInbox * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongDanhXienDauInbox > 0) {
            tongText += "<font color=\"blue\"><big>xidau" +
                    "=" + String.valueOf(Math.round(tongDanhXienDauInboxSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhXienDauInbox * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }
        if (tongThuongXienDauInbox > 0) {
            tongText += "<font color=\"red\"><big>thxidau" +
                    "=" + String.valueOf(Math.round(tongThuongXienDauInboxSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongXienDauInbox * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongThuongXienDauInbox < 0) {
            tongText += "<font color=\"blue\"><big>thxidau" +
                    "=" + String.valueOf(Math.round(tongThuongXienDauInboxSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongXienDauInbox * -1 * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }

        if (tongDanhBaCangInbox < 0) {
            tongText += "<font color=\"red\"><big>bacang" +
                    "=" + String.valueOf(Math.round(tongDanhBaCangInboxSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhBaCangInbox * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongDanhBaCangInbox > 0) {
            tongText += "<font color=\"blue\"><big>bacang" +
                    "=" + String.valueOf(Math.round(tongDanhBaCangInboxSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhBaCangInbox * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }
        if (tongThuongBaCangInbox > 0) {
            tongText += "<font color=\"red\"><big>thbacang" +
                    "=" + String.valueOf(Math.round(tongThuongBaCangInboxSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongBaCangInbox * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongThuongBaCangInbox < 0) {
            tongText += "<font color=\"blue\"><big>thbacang" +
                    "= " + String.valueOf(Math.round(tongThuongBaCangInboxSms * -1 * 100.0) / 100.0) + "n " +
                    "= " + String.valueOf(Math.round(tongThuongBaCangInbox * -1 * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }
        if (tongBangInbox < 0) {
            tongText += "<font color=\"red\"><big>thua" +
                    "=" + String.valueOf(Math.round(tongBangInbox * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongBangInbox > 0) {
            tongText += "<font color=\"blue\"><big>lãi" +
                    "=" + String.valueOf(Math.round(tongBangInbox * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        }
        /***************** thong tin phan end inbox sms **************************/

        /***************** thong tin phan send sms **************************/
        if (tongDanhDeSend != 0 || tongDanhLoSend != 0 ||
                tongDanhXienSend != 0 || tongDanhBaCangSend != 0) {
            tongText += "-----tổng hợp tin nhắn gửi đi-----<br/>";
        }
        if (tongDanhDeSend > 0) {
            tongText += "<font color=\"red\"><big>de" +
                    "=" + String.valueOf(Math.round(tongDanhDeSendSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhDeSend * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongDanhDeSend < 0) {
            tongText += "<font color=\"blue\"><big>de" +
                    "=" + String.valueOf(Math.round(tongDanhDeSendSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhDeSend * -1 * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }
        if (tongThuongDeSend < 0) {
            tongText += "<font color=\"red\"><big>thde" +
                    "=" + String.valueOf(Math.round(tongThuongDeSendSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongDeSend * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongThuongDeSend > 0) {
            tongText += "<font color=\"blue\"><big>thde" +
                    "=" + String.valueOf(Math.round(tongThuongDeSendSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongDeSend * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }

        if (tongDanhDeDauSend > 0) {
            tongText += "<font color=\"red\"><big>de" +
                    "=" + String.valueOf(Math.round(tongDanhDeSendSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhDeSend * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongDanhDeDauSend < 0) {
            tongText += "<font color=\"blue\"><big>de" +
                    "=" + String.valueOf(Math.round(tongDanhDeSendSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhDeSend * -1 * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }
        if (tongThuongDeDauSend < 0) {
            tongText += "<font color=\"red\"><big>thde" +
                    "=" + String.valueOf(Math.round(tongThuongDeSendSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongDeSend * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongThuongDeDauSend > 0) {
            tongText += "<font color=\"blue\"><big>thde" +
                    "=" + String.valueOf(Math.round(tongThuongDeSendSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongDeSend * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }

        if (tongDanhgiainhatDauSend > 0) {
            tongText += "giainhatA" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatDauSendSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatDauSend * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongDanhgiainhatDauSend < 0) {
            tongText += "giainhatA" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatDauSendSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatDauSend * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        if (tongThuonggiainhatDauSend < 0) {
            tongText += "thgiainhatA" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatDauSendSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatDauSend * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongThuonggiainhatDauSend > 0) {
            tongText += "thgiainhatA" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatDauSendSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatDauSend * 100.0) / 100.0) + " n" +
                    "<br/>";
        }

        if (tongDanhgiainhatSend > 0) {
            tongText += "giainhatB" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatSendSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatSend * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongDanhgiainhatSend < 0) {
            tongText += "giainhatB" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatSendSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhgiainhatSend * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        if (tongThuonggiainhatSend < 0) {
            tongText += "thgiainhatB" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatSendSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatSend * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongThuonggiainhatSend > 0) {
            tongText += "thgiainhatB" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatSendSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuonggiainhatSend * 100.0) / 100.0) + " n" +
                    "<br/>";
        }

        if (tongDanhLoSend > 0) {
            tongText += "<font color=\"red\"><big>lo" +
                    "=" + String.valueOf(Math.round(tongDanhLoSendSms * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongDanhLoSend * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        } else if (tongDanhLoSend < 0) {
            tongText += "<font color=\"blue\"><big>lo" +
                    "=" + String.valueOf(Math.round(tongDanhLoSendSms * -1 * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongDanhLoSend * -1 * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        }
        if (tongThuongLoSend < 0) {
            tongText += "<font color=\"red\"><big>thlo" +
                    "=" + String.valueOf(Math.round(tongThuongLoSendSms * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongThuongLoSend * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        } else if (tongThuongLoSend > 0) {
            tongText += "<font color=\"blue\"><big>thlo" +
                    "=" + String.valueOf(Math.round(tongThuongLoSendSms * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongThuongLoSend * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        }

        if (tongDanhLoDauSend > 0) {
            tongText += "<font color=\"red\"><big>lo dau" +
                    "=" + String.valueOf(Math.round(tongDanhLoDauSendSms * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongDanhLoDauSend * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        } else if (tongDanhLoDauSend < 0) {
            tongText += "<font color=\"blue\"><big>lo dau" +
                    "=" + String.valueOf(Math.round(tongDanhLoDauSendSms * -1 * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongDanhLoDauSend * -1 * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        }
        if (tongThuongLoDauSend < 0) {
            tongText += "<font color=\"red\"><big>thlo dau" +
                    "=" + String.valueOf(Math.round(tongThuongLoDauSendSms * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongThuongLoDauSend * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        } else if (tongThuongLoDauSend > 0) {
            tongText += "<font color=\"blue\"><big>thlo dau" +
                    "=" + String.valueOf(Math.round(tongThuongLoDauSendSms * 100.0) / 100.0) + "d " +
                    "=" + String.valueOf(Math.round(tongThuongLoDauSend * 100.0) / 100.0) + "d " +
                    "</big></font><br/>";
        }

        if (tongDanhXienSend > 0) {
            tongText += "<font color=\"red\"><big>xien" +
                    "=" + String.valueOf(Math.round(tongDanhXienSendSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhXienSend * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongDanhXienSend < 0) {
            tongText += "<font color=\"blue\"><big>xien" +
                    "=" + String.valueOf(Math.round(tongDanhXienSendSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhXienSend * -1 * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }
        if (tongThuongXienSend < 0) {
            tongText += "<font color=\"red\"><big>thxien" +
                    "=" + String.valueOf(Math.round(tongThuongXienSendSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongXienSend * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongThuongXienSend > 0) {
            tongText += "<font color=\"blue\"><big>thxien" +
                    "=" + String.valueOf(Math.round(tongThuongXienSendSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongXienSend * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }

        if (tongDanhXienDauSend > 0) {
            tongText += "<font color=\"red\"><big>xidau" +
                    "=" + String.valueOf(Math.round(tongDanhXienDauSendSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhXienDauSend * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongDanhXienDauSend < 0) {
            tongText += "<font color=\"blue\"><big>xidau" +
                    "=" + String.valueOf(Math.round(tongDanhXienDauSendSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhXienDauSend * -1 * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }
        if (tongThuongXienDauSend < 0) {
            tongText += "<font color=\"red\"><big>thxidau" +
                    "=" + String.valueOf(Math.round(tongThuongXienDauSendSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongXienDauSend * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongThuongXienDauSend > 0) {
            tongText += "<font color=\"blue\"><big>thxidau" +
                    "=" + String.valueOf(Math.round(tongThuongXienDauSendSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongXienDauSend * 100.0) / 100.0) + "n" +
                    "</big></font><br/>";
        }

        if (tongDanhBaCangSend > 0) {
            tongText += "<font color=\"red\"><big>bacang" +
                    "=" + String.valueOf(Math.round(tongDanhBaCangSendSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhBaCangSend * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongDanhBaCangSend < 0) {
            tongText += "<font color=\"blue\"><big>bacang" +
                    "=" + String.valueOf(Math.round(tongDanhBaCangSendSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongDanhBaCangSend * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        }
        if (tongThuongBaCangSend < 0) {
            tongText += "<font color=\"red\"><big>thbacang" +
                    "=" + String.valueOf(Math.round(tongThuongBaCangSendSms * -1 * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongBaCangSend * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongThuongBaCangSend > 0) {
            tongText += "<font color=\"blue\"><big>thbacang" +
                    "=" + String.valueOf(Math.round(tongThuongBaCangSendSms * 100.0) / 100.0) + "n " +
                    "=" + String.valueOf(Math.round(tongThuongBaCangSend * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        }
        if (tongBangSend > 0) {
            tongText += "<font color=\"red\"><big>thua" +
                    "=" + String.valueOf(Math.round(tongBangSend * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongBangSend < 0) {
            tongText += "<font color=\"blue\"><big>lãi" +
                    "=" + String.valueOf(Math.round(tongBangSend * -1 * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        }
        double tongbang = tongBangInbox - tongBangSend;
        if (tongbang != 0) {
            tongText += "-----tổng hơp tin nhắn-----<br/>";
        }
        if (tongbang < 0) {
            tongText += "<font color=\"red\"><big>thua" +
                    "=" + String.valueOf(Math.round(tongbang * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        } else if (tongbang > 0) {
            tongText += "<font color=\"blue\"><big>lãi" +
                    "=" + String.valueOf(Math.round(tongbang * 100.0) / 100.0) + "n " +
                    "</big></font><br/>";
        }
        /***************** thong tin phan end send sms **************************/
        TableRow.LayoutParams rowSpanLayout2 = new TableRow.LayoutParams();
        rowSpanLayout2.span = 2;
        String tongBaCangAndText = textBaCang + stringResXien + tongText;
        tong.setText(Html.fromHtml(tongBaCangAndText));
        TableRow tr1 = new TableRow(Statistic.this);
        tr1.addView(tong, rowSpanLayout2);

        de.setText(Html.fromHtml(textDeLeft));
        lo.setText(Html.fromHtml(textLoLeft));
        tr2.addView(de);
        tr2.addView(lo);
        tableLayout.addView(tr2);

        TableRow trlo = new TableRow(Statistic.this);
        TextView loleft = new TextView(Statistic.this); //1
        TextView loright = new TextView(Statistic.this); //1
        loleft.setText(Html.fromHtml(textDeRight)); // 1
        loright.setText(Html.fromHtml(textLoRight));//1
        trlo.addView(loleft);
        trlo.addView(loright);
        tableLayout.addView(trlo);

        TableRow tr4 = new TableRow(Statistic.this);
        TextView dedauleft = new TextView(Statistic.this);
        TextView dedauright = new TextView(Statistic.this);
        dedauleft.setText(Html.fromHtml(textDeDauLeft));
        dedauright.setText(Html.fromHtml(textLoDauLeft));
        tr4.addView(dedauleft);
        tr4.addView(dedauright);
        tableLayout.addView(tr4);

        TableRow trlodau = new TableRow(Statistic.this);
        TextView lodauleft = new TextView(Statistic.this);
        TextView lodauright = new TextView(Statistic.this);
        lodauleft.setText(Html.fromHtml(textDeDauRight));
        lodauright.setText(Html.fromHtml(textLoDauRight));
        trlodau.addView(lodauleft);
        trlodau.addView(lodauright);
        tableLayout.addView(trlodau);

        tableLayout.addView(tr1);
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
                        Intent intent = new Intent(Statistic.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(Statistic.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(Statistic.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(Statistic.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(Statistic.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(Statistic.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(Statistic.this, viewSmsNotMoney.class);
                        startActivity(intent7);
                        return true;
                    case R.id.historySms:
                        Intent intent8 = new Intent(Statistic.this, HistorySms.class);
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
        inflater.inflate(R.menu.statistic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_show_dialog:
                year_x = controller.dateInt("yyyy");
                month_x = controller.dateInt("MM") - 1;
                day_x = controller.dateInt("dd");

                DatePickerDialog datePickerDialog = new DatePickerDialog(Statistic.this, new DatePickerDialog.OnDateSetListener() {
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
