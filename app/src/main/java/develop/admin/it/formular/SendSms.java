package develop.admin.it.formular;

import android.app.DatePickerDialog;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SendSms extends AppCompatActivity {
    private static SendSms inst;
    DatabaseHelper sql;
    EditText sdtCus;
    EditText contentSend;
    Button sendSms;
    ProgressBar loadingimage;
    GlobalClass controller = new GlobalClass();
    TextView sdt, kieuSms, date;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private int year_x, month_x, day_x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        sideBarMenu();
        sql = new DatabaseHelper(this);
        String getDays = "";
        String contentSms = "";
        String formatSms = "";
        String sdtCustomer = "0";
        int donGiaId = 0;
        int cophan = 100;
        String ten = "";
        String ngoimot = "em";
        String ngoihai = "bac";
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            sdtCustomer = bd.getString("sdt");
            String kieu = bd.getString("kieu");
            getDays = bd.getString("date");
            if (kieu.indexOf("+") > -1) {
                formatSms = "all";
            } else if (kieu.indexOf("đến") > 1) {
                formatSms = "inbox";
            } else {
                formatSms = "send";
            }
            sdt = (TextView) findViewById(R.id.textViewSdtCus);
            sdt.setText(sdtCustomer);

            kieuSms = (TextView) findViewById(R.id.textViewKieuSms);
            kieuSms.setText(formatSms);

            Cursor dongia = sql.getAllDb("SELECT COPHAN,TEN,NGOIMOT,NGOIHAI FROM dongia_table WHERE SDT=\"" + sdtCustomer + "\"  LIMIT 0,1");
            if (dongia.getCount() > 0) {
                dongia.moveToFirst();
                cophan = Integer.parseInt(dongia.getString(dongia.getColumnIndex("COPHAN")));
                ten = dongia.getString(dongia.getColumnIndex("TEN"));
                ngoimot = dongia.getString(dongia.getColumnIndex("NGOIMOT"));
                ngoihai = dongia.getString(dongia.getColumnIndex("NGOIHAI"));
                TextView addName = (TextView) findViewById(R.id.textViewSdt);
                addName.setText("SDT - " + ten);
            }
            EditText sdtEditText = (EditText) findViewById(R.id.editTextSdt);
            sdtEditText.setText(sdtCustomer);
        }
        importSms(getDays, sdtCustomer, formatSms, ten, cophan, ngoimot, ngoihai);

    }

    private String chitiettungtin(String sdt, String date) {
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
        String smsIdAll = "";
        String ketqua = "";
        if (smsReady.getCount() > 0) {
            int i = 0;
            while (smsReady.moveToNext()) {
                i++;
                ketqua += "tin"+i+" : ";
                String tiende = "0";
                String thuongde = "0";
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
            }
        }
        return ketqua;

    }

    private void importSms(final String getDays, String sdt, String formatSms,
                           String ten, int cophan, String ngoiMot, String ngoiHai) {
//        String chitiettungtin = chitiettungtin(sdt,getDays);
        String chitiettungtin = "";
                String queryKieu = "";
        if (formatSms.equals("inbox")) {
            queryKieu = " AND KIEU=\"inbox\"";
        } else if (formatSms.equals("send")) {
            queryKieu = " AND KIEU=\"send\"";
        }

        String query = "SELECT * FROM solieu_table WHERE NGAY=\"" + getDays + "\" AND SDT=\"" + sdt + "\"" + queryKieu;
        Cursor table_solieu = sql.getAllDb(query);

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

        date = (TextView) findViewById(R.id.textViewDate);
        date.setText(getDays);
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
            }
        }
        String tongText = "";
        if (tongDanhDeInbox != 0 || tongDanhLoInbox != 0 ||
                tongDanhXienInbox != 0 || tongDanhBaCangInbox != 0 ||
                tongDanhDeSend != 0 || tongDanhLoSend != 0 ||
                tongDanhBaCangSend != 0 || tongDanhBaCangSend != 0) {
            tongText += getDays + "<br/>" + chitiettungtin;
        }
        /***************** thong tin phan inbox sms **************************/
        /***************** chu y cac dong thuong tien inbox thi dau bao do bị nguoc lại **************/
        if (tongDanhDeInbox != 0 || tongDanhLoInbox != 0 ||
                tongDanhXienInbox != 0 || tongDanhBaCangInbox != 0) {
            tongText += ngoiHai + " chuyen cho " + ngoiMot + "<br/>";
        }
        if (tongDanhDeInbox < 0) {
            tongText += "de" +
                    "=" + String.valueOf(Math.round(tongDanhDeInboxSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhDeInbox * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongDanhDeInbox > 0) {
            tongText += "de" +
                    "=" + String.valueOf(Math.round(tongDanhDeInboxSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhDeInbox * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        if (tongThuongDeInbox > 0) {
            tongText += "thde" +
                    "=" + String.valueOf(Math.round(tongThuongDeInboxSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuongDeInbox * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongThuongDeInbox < 0) {
            tongText += "thde" +
                    "=" + String.valueOf(Math.round(tongThuongDeInboxSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuongDeInbox * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        }


        if (tongDanhLoInbox < 0) {
            tongText += "lo" +
                    "=" + String.valueOf(Math.round(tongDanhLoInboxSms * -1 * 100.0) / 100.0) + "d" +
                    "=" + String.valueOf(Math.round(tongDanhLoInbox * -1 * 100.0) / 100.0) + "d" +
                    "<br/>";
        } else if (tongDanhLoInbox > 0) {
            tongText += "lo" +
                    "=" + String.valueOf(Math.round(tongDanhLoInboxSms * 100.0) / 100.0) + "d" +
                    "=" + String.valueOf(Math.round(tongDanhLoInbox * 100.0) / 100.0) + "d" +
                    "<br/>";
        }
        if (tongThuongLoInbox > 0) {
            tongText += "thlo" +
                    "=" + String.valueOf(Math.round(tongThuongLoInboxSms * 100.0) / 100.0) + "d" +
                    "=" + String.valueOf(Math.round(tongThuongLoInbox * 100.0) / 100.0) + "d" +
                    "<br/>";
        } else if (tongThuongLoInbox < 0) {
            tongText += "thlo" +
                    "=" + String.valueOf(Math.round(tongThuongLoInboxSms * -1 * 100.0) / 100.0) + "d" +
                    "=" + String.valueOf(Math.round(tongThuongLoInbox * -1 * 100.0) / 100.0) + "d" +
                    "<br/>";
        }

        if (tongDanhXienInbox < 0) {
            tongText += "xi" +
                    "=" + String.valueOf(Math.round(tongDanhXienInboxSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhXienInbox * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongDanhXienInbox > 0) {
            tongText += "xi" +
                    "=" + String.valueOf(Math.round(tongDanhXienInboxSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhXienInbox * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        if (tongThuongXienInbox > 0) {
            tongText += "thxi" +
                    "=" + String.valueOf(Math.round(tongThuongXienInboxSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuongXienInbox * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongThuongXienInbox < 0) {
            tongText += "thxi" +
                    "=" + String.valueOf(Math.round(tongThuongXienInboxSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuongXienInbox * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        }

        if (tongDanhBaCangInbox < 0) {
            tongText += "3c" +
                    "=" + String.valueOf(Math.round(tongDanhBaCangInboxSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhBaCangInbox * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongDanhBaCangInbox > 0) {
            tongText += "3c" +
                    "=" + String.valueOf(Math.round(tongDanhBaCangInboxSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhBaCangInbox * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        if (tongThuongBaCangInbox > 0) {
            tongText += "th3c" +
                    "=" + String.valueOf(Math.round(tongThuongBaCangInboxSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuongBaCangInbox * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongThuongBaCangInbox < 0) {
            tongText += "th3c" +
                    "= " + String.valueOf(Math.round(tongThuongBaCangInboxSms * -1 * 100.0) / 100.0) + "n" +
                    "= " + String.valueOf(Math.round(tongThuongBaCangInbox * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        if (tongBangInbox < 0) {
            tongText += ngoiMot + " bu cho " + ngoiHai +
                    "=" + String.valueOf(Math.round(tongBangInbox * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongBangInbox > 0) {
            tongText += ngoiHai + " bu cho " + ngoiMot +
                    "=" + String.valueOf(Math.round(tongBangInbox * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        /***************** thong tin phan end inbox sms **************************/

        /***************** thong tin phan send sms **************************/
        if (tongDanhDeSend != 0 || tongDanhLoSend != 0 ||
                tongDanhXienSend != 0 || tongDanhBaCangSend != 0) {
            tongText += ngoiMot + " chuyen cho " + ngoiHai + "<br/>";
        }
        if (tongDanhDeSend > 0) {
            tongText += "de" +
                    "=" + String.valueOf(Math.round(tongDanhDeSendSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhDeSend * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongDanhDeSend < 0) {
            tongText += "de" +
                    "=" + String.valueOf(Math.round(tongDanhDeSendSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhDeSend * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        if (tongThuongDeSend < 0) {
            tongText += "thde" +
                    "=" + String.valueOf(Math.round(tongThuongDeSendSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuongDeSend * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongThuongDeSend > 0) {
            tongText += "thde" +
                    "=" + String.valueOf(Math.round(tongThuongDeSendSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuongDeSend * 100.0) / 100.0) + " n" +
                    "<br/>";
        }

        if (tongDanhLoSend > 0) {
            tongText += "lo" +
                    "=" + String.valueOf(Math.round(tongDanhLoSendSms * 100.0) / 100.0) + "d" +
                    "=" + String.valueOf(Math.round(tongDanhLoSend * 100.0) / 100.0) + "d" +
                    "<br/>";
        } else if (tongDanhLoSend < 0) {
            tongText += "lo" +
                    "=" + String.valueOf(Math.round(tongDanhLoSendSms * -1 * 100.0) / 100.0) + "d" +
                    "=" + String.valueOf(Math.round(tongDanhLoSend * -1 * 100.0) / 100.0) + "d" +
                    "<br/>";
        }
        if (tongThuongLoSend < 0) {
            tongText += "thlo" +
                    "=" + String.valueOf(Math.round(tongThuongLoSendSms * 100.0) / 100.0) + "d" +
                    "=" + String.valueOf(Math.round(tongThuongLoSend * 100.0) / 100.0) + "d" +
                    "<br/>";
        } else if (tongThuongLoSend > 0) {
            tongText += "thlo" +
                    "=" + String.valueOf(Math.round(tongThuongLoSendSms * 100.0) / 100.0) + "d" +
                    "=" + String.valueOf(Math.round(tongThuongLoSend * 100.0) / 100.0) + "d" +
                    "<br/>";
        }

        if (tongDanhXienSend > 0) {
            tongText += "xi" +
                    "=" + String.valueOf(Math.round(tongDanhXienSendSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhXienSend * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongDanhXienSend < 0) {
            tongText += "xi" +
                    "=" + String.valueOf(Math.round(tongDanhXienSendSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhXienSend * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        if (tongThuongXienSend < 0) {
            tongText += "thxi" +
                    "=" + String.valueOf(Math.round(tongThuongXienSendSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuongXienSend * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongThuongXienSend > 0) {
            tongText += "thxi" +
                    "=" + String.valueOf(Math.round(tongThuongXienSendSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuongXienSend * 100.0) / 100.0) + " n" +
                    "<br/>";
        }

        if (tongDanhBaCangSend > 0) {
            tongText += "3c" +
                    "=" + String.valueOf(Math.round(tongDanhBaCangSendSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhBaCangSend * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongDanhBaCangSend < 0) {
            tongText += "bacang" +
                    "=" + String.valueOf(Math.round(tongDanhBaCangSendSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongDanhBaCangSend * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        if (tongThuongBaCangSend < 0) {
            tongText += "th3c" +
                    "=" + String.valueOf(Math.round(tongThuongBaCangSendSms * -1 * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuongBaCangSend * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongThuongBaCangSend > 0) {
            tongText += "th3c" +
                    "=" + String.valueOf(Math.round(tongThuongBaCangSendSms * 100.0) / 100.0) + "n" +
                    "=" + String.valueOf(Math.round(tongThuongBaCangSend * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        if (tongBangSend > 0) {
            tongText += ngoiMot + " bu cho " + ngoiHai +
                    "=" + String.valueOf(Math.round(tongBangSend * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongBangSend < 0) {
            tongText += ngoiHai + " bu cho " + ngoiMot +
                    "=" + String.valueOf(Math.round(tongBangSend * -1 * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        double tongbang = tongBangInbox - tongBangSend;
        if (tongbang < 0) {
            tongText += "tong bang " + ngoiMot + " bu cho " + ngoiHai +
                    "=" + String.valueOf(Math.round(tongbang * 100.0) / 100.0) + " n" +
                    "<br/>";
        } else if (tongbang > 0) {
            tongText += "tong bang " + ngoiHai + " bu cho " + ngoiMot +
                    "=" + String.valueOf(Math.round(tongbang * 100.0) / 100.0) + " n" +
                    "<br/>";
        }
        /***************** thong tin phan end send sms **************************/

        contentSend = (EditText) findViewById(R.id.editTextContentSms);
        sendSms = (Button) findViewById(R.id.buttonGuiCanBang);
        contentSend.setText(Html.fromHtml(tongText));
        getSms(getDays, sdt);
        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdtCus = (EditText) findViewById(R.id.editTextSdt);
                String phoneNo = sdtCus.getText().toString();
                controller = new GlobalClass();
                String message = controller.uniStrip(contentSend.getText().toString());
                if (phoneNo.length() > 0 && message.length() > 0) {
                    sendMessage(phoneNo, message,getDays);
                } else {
                    Toast.makeText(SendSms.this, "Nhập giá trị số hoặc nội dung tin nhắn", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getSms(String getDays, String sdt) {
        String today = controller.dateDay("yyyy-MM-dd");
        String dayDefault = controller.dateDayNoTimeZone(today + " 23:59:59"); // lay ngay theo gio nuoc anh mac dinh
        long dayMillsecond = controller.converDayToMill("yyyy-MM-dd HH:mm:ss", dayDefault);
        long lastMillsecond = dayMillsecond - 1000L * 60L * 60L * 24L;
        Uri mSmsinboxQueryUri = Uri.parse("content://sms/");
        String stdRe = "+84" + sdt.substring(1, sdt.length());
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        String filter = "(address='" + sdt + "' or address='" + stdRe + "') and date>=" + lastMillsecond +
                " and date <=" + dayMillsecond;
        ArrayList<String> bodySms = new ArrayList<String>();
        Cursor cursor1 = getContentResolver().query(mSmsinboxQueryUri, projection, filter, null, "_id desc LIMIT 3");
        if (cursor1.getCount() > 0) {
            while (cursor1.moveToNext()) {
                String body = cursor1.getString(cursor1.getColumnIndex("Body"));
                bodySms.add(body);
            }
        }
        ListView listViewShowSms = (ListView) findViewById(R.id.listViewShowSms);
        // get data from the table by the ListAdapter
        ShowSmsAdapter customAdapter = new ShowSmsAdapter(SendSms.this,bodySms);
        listViewShowSms.setAdapter(customAdapter);

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

    protected void sendMessage(final String phoneNo, String message, final String getDays) {
        try {
            final int smsCountDefault = countSms(phoneNo);
            message = message.replace("<br/>", " ");

            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(message);
            sms.sendMultipartTextMessage(phoneNo, null, parts, null, null);
            SmsManager smsManager = SmsManager.getDefault();
            loadingimage= (ProgressBar) findViewById(R.id.progressBar);
            loadingimage.setVisibility(View.VISIBLE);
            Toast.makeText(SendSms.this, "đang gửi tin nhắn", Toast.LENGTH_LONG).show();

            long start = 60;
            long end = 3;

            final CountDownTimer timer = new CountDownTimer(start * 1000, end * 1000) {
                public void onTick(long millisUntilFinished) {
                    int smsCountNew = countSms(phoneNo);
                    if (smsCountDefault < smsCountNew) {
                        loadingimage= (ProgressBar) findViewById(R.id.progressBar);
                        loadingimage.setVisibility(View.GONE);
                        getSms(getDays,phoneNo);
                        contentSend = (EditText) findViewById(R.id.editTextContentSms);
                        contentSend.setText("");
                        controller.showAlertDialog( SendSms.this, "Thông báo", "Gửi tin nhắn thành công" );
                        this.cancel();
                    }
                }
                public void onFinish() {
                    loadingimage= (ProgressBar) findViewById(R.id.progressBar);
                    loadingimage.setVisibility(View.GONE);
                    controller.showAlertDialog( SendSms.this, "Thông báo", "Chưa gửi được tin nhắn! làm ơn thử lại!" );
                }

            };
            timer.start();

        } catch (Exception e) {
            Toast.makeText(SendSms.this, "Đã có lỗi xảy ra trong quá trình gửi", Toast.LENGTH_LONG).show();
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
                        Intent intent = new Intent(SendSms.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(SendSms.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(SendSms.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(SendSms.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(SendSms.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(SendSms.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(SendSms.this, viewSmsNotMoney.class);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(SendSms.this, new DatePickerDialog.OnDateSetListener() {
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
                            kieuSms = (TextView) findViewById(R.id.textViewKieuSms);
                            String formatSms = kieuSms.getText().toString();
                            String getDays = String.valueOf(year) + "-" + month_s + "-" + dayOfMonth_s;
                            showSelectSms(getDays, formatSms);
                        }
                    }
                }, year_x, month_x, day_x);
                datePickerDialog.show();
                return true;
            case R.id.all:
                date = (TextView) findViewById(R.id.textViewDate);
                String getDays = date.getText().toString();
                showSelectSms(getDays, "all");
                return true;
            case R.id.send:
                date = (TextView) findViewById(R.id.textViewDate);
                String getDays2 = date.getText().toString();
                showSelectSms(getDays2, "send");
                return true;
            case R.id.inbox:
                date = (TextView) findViewById(R.id.textViewDate);
                String getDays3 = date.getText().toString();
                showSelectSms(getDays3, "inbox");
                return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSelectSms(String getDays, String formatSms) {
        sdt = (TextView) findViewById(R.id.textViewSdtCus);
        String sdtCusString = sdt.getText().toString();
        int cophan = 100;
        String ten = "";
        String ngoimot = "em";
        String ngoihai = "bac";
        int donGiaId = 0;
        Cursor dongia = sql.getAllDb("SELECT ID,COPHAN,TEN,NGOIMOT,NGOIHAI FROM dongia_table WHERE SDT=\"" + sdtCusString + "\"  LIMIT 0,1");
        if (dongia.getCount() > 0) {
            dongia.moveToFirst();
            cophan = Integer.parseInt(dongia.getString(dongia.getColumnIndex("COPHAN")));
            ten = dongia.getString(dongia.getColumnIndex("TEN"));
            ngoimot = dongia.getString(dongia.getColumnIndex("NGOIMOT"));
            ngoihai = dongia.getString(dongia.getColumnIndex("NGOIHAI"));
        }
        importSms(getDays, sdtCusString, formatSms, ten, cophan, ngoimot, ngoihai);
    }
}
