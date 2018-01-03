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
        Integer[] loArr = new Integer[100];
        Integer[] loTrung = new Integer[100];
        Integer[] bacangArr = new Integer[1000];
        HashMap<String, ArrayList<String>> hashmap = sql.readkitu();
        String compareDe = sql.compareDe(getDays);
        ArrayList<String> compareLo = sql.getArrayKeyRes(getDays);
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
        // send inbox
        double tongDanhXienInbox = 0;
        double tongDanhXienInboxSms = 0;
        double tongThuongXienInbox = 0;
        double tongThuongXienInboxSms = 0;
        // xien send
        double tongDanhXienSend = 0;
        double tongThuongXienSend = 0;
        double tongDanhXienSendSms = 0;
        double tongThuongXienSendSms = 0;

        double tongBangInbox = 0;
        double tongBangSend = 0;

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
                            if (compareDe.equals(arrVal[i])) {
                                deTrung[solo] = 1;
                            } else {
                                deTrung[solo] = 0;
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
                            loTrung[solo] = Collections.frequency(compareLo, String.valueOf(solo));
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

                            if (compareDe.equals(loto)) {
                                deTrung[intLoTo] = 1;
                            } else {
                                deTrung[intLoTo] = 0;
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
                            loTrung[intLoTo] = Collections.frequency(compareLo, String.valueOf(intLoTo));
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
                    }
                }
            }
        }

        String textDeLeft = "<font color =\"blue\"><big>Đề</big></font><br/>";
        String textDeRight = "<font color =\"blue\"><big> </big></font><br/>";
        String textLoLeft = "<font color =\"blue\"><big>Lô</big></font><br/>";
        String textLoRight = "<font color =\"blue\"> </font><br/>";
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
                sortDe[z] = String.valueOf(showDe) + "-" + String.valueOf(deArr[q]) + "-" + String.valueOf(deTrung[q]);
                z++;
            } else {
                String showDe = "";
                if (q < 10) {
                    showDe = "0" + String.valueOf(q);
                } else {
                    showDe = String.valueOf(q);
                }
                sortDe[z] = String.valueOf(showDe) + "-0-0";
                z++;
            }

            if (loArr[q] != null) {
                String showLo = "";
                if (q < 10) {
                    showLo = "0" + String.valueOf(q);
                } else {
                    showLo = String.valueOf(q);
                }
                sortLo[k] = String.valueOf(showLo) + "-" + String.valueOf(loArr[q]) + "-" + String.valueOf(loTrung[q]);
                k++;
            } else {
                String showLo = "";
                if (q < 10) {
                    showLo = "0" + String.valueOf(q);
                } else {
                    showLo = String.valueOf(q);
                }
                sortLo[k] = String.valueOf(showLo) + "-0-0";
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
        String tempDe;
        for (int x = 0; x < sortDe.length; x++) {
            if (sortDe[x] != null) {
                String[] arrSortDe1 = sortDe[x].split("-");
                for (int t = x + 1; t < sortDe.length; t++) {
                    if (sortDe[t] != null) {
                        String[] arrSortDe2 = sortDe[t].split("-");
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
                String[] arrSortLo1 = sortLo[c].split("-");
                for (int r = c + 1; r < sortLo.length; r++) {
                    if (sortLo[r] != null) {
                        String[] arrSortLo2 = sortLo[r].split("-");
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

        for (int a = 0; a < sortDe.length; a++) {
            // if (a < 50) {
                if (sortDe[a] != null) {
                    String[] showResDe = sortDe[a].split("-");
                    if (Integer.parseInt(showResDe[2]) == 0) {
                        textDeLeft += "<big><big><b><font color=\"red\"> &nbsp; &nbsp; &nbsp;" + showResDe[0] + " </font>" +
                                showResDe[1] + "n</b></big></big><br />";
                    } else {
                        textDeLeft += "<big><big><b><font color=\"red\" > &nbsp; &nbsp; &nbsp;" + showResDe[0] + "</font>" +
                                "<font color=\"blue\"> " + showResDe[2] + "</font> " +
                                 showResDe[1] + "n</b></big></big><br />";
                    }
                }
//            } else {
//                if (sortDe[a] != null) {
//                    String[] showResDe = sortDe[a].split("-");
//                    if (Integer.parseInt(showResDe[2]) == 0) {
//                        textDeRight += "<big><big><b><font color=\"red\" > &nbsp; &nbsp; &nbsp;" +
//                                showResDe[0] + " </font>" +
//                                showResDe[1] + "n </b></big></big><br />";
//                    } else {
//                        textDeRight += "<big><big><b><font color=\"red\"> &nbsp; &nbsp; &nbsp;" + showResDe[0] + "</font>" +
//                                "<font color=\"blue\"> " + showResDe[2] + "</font> " +
//                                 showResDe[1] + "n</b></big></big><br />";
//                    }
//                }
//            }
        }

        for (int b = 0; b < sortLo.length; b++) {
           // if (b < 50) {
                if (sortLo[b] != null) {
                    String[] showResLo = sortLo[b].split("-");
                    if (Integer.parseInt(showResLo[2]) == 0) {
                        textLoLeft += "<big><big><b><font color=\"red\" > &nbsp; &nbsp; &nbsp;" +
                                showResLo[0] + "  </font>"
                                + showResLo[1] + "d</b></big></big><br />";
                    } else {
                        textLoLeft += "<big><big><b><font color=\"red\" > &nbsp; &nbsp; &nbsp;"
                                + showResLo[0] + "</font>" +
                                "<font color=\"blue\">" + showResLo[2] + "</font> "
                                + showResLo[1] + "d</b></big></big><br />";
                    }
                }
//            } else {
//                if (sortLo[b] != null) {
//                    String[] showResLo = sortLo[b].split("-");
//                    if (Integer.parseInt(showResLo[2]) == 0) {
//                        textLoRight += "<big><big><b><font color=\"red\" > &nbsp; &nbsp; &nbsp;" +
//                                showResLo[0] + "  </font>"
//                                + showResLo[1] + "d</b></big></big><br />";
//                    } else {
//                        textLoRight += "<big><big><b><font color=\"red\" > &nbsp; &nbsp; &nbsp;"
//                                + showResLo[0] + "</font>" +
//                                "<font color=\"blue\">" + showResLo[2] + "</font> "
//                                + showResLo[1] + "d</b></big></big><br />";
//                    }
//                }
//            }
        }

        for (int n = 0; n < sortBacang.length; n++) {
            if (sortBacang[n] != null) {
                String[] showResBaCang = sortBacang[n].split("_");
                textBaCang += "<big><big><b><font color=\"red\">&nbsp; &nbsp; &nbsp;" + showResBaCang[0] + " </font>"
                        + showResBaCang[1] + "n</b></big></big><br/>";
            }
        }

        /***************** thong tin phan inbox sms **************************/
        /***************** chu y cac dong thuong thi dau bao do bị nguoc lại **************/
        if (tongDanhDeInbox != 0 || tongDanhLoInbox != 0 ||
                tongDanhXienInbox != 0 || tongDanhBaCangInbox != 0) {
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
        String tongBaCangAndText = textBaCang + tongText;
        tong.setText(Html.fromHtml(tongBaCangAndText));
        TableRow tr1 = new TableRow(Statistic.this);
        tr1.addView(tong, rowSpanLayout2);

        TableRow tr4 = new TableRow(Statistic.this);

        de.setText(Html.fromHtml(textDeLeft));
        lo.setText(Html.fromHtml(textLoLeft));
        tr2.addView(de);
        tr2.addView(lo);
        tableLayout.addView(tr2);

//        TextView de1 = new TextView(Statistic.this);
//        TextView lo1 = new TextView(Statistic.this);
//        de1.setText(Html.fromHtml(textLoLeft));
//        lo1.setText(Html.fromHtml(textLoRight));
//
//        tr4.addView(de1);
//        tr4.addView(lo1);
//        tableLayout.addView(tr4);
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
