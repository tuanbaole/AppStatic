package develop.admin.it.formular;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static develop.admin.it.formular.DatabaseHelper.TABLE_NAME_11;
import static develop.admin.it.formular.DatabaseHelper.TABLE_NAME_5;
import static develop.admin.it.formular.R.id.textViewSmsId;

public class Message extends AppCompatActivity {
    GlobalClass controller = new GlobalClass();
    EditText textV;
    TextView textContact, smsId, textViewSmsType, textViewDate,
            textViewHSD, textViewKhoaLo, textViewKhoaDe, timeSmsVn,
            textViewTypeCheckSms, textMessage;
    DatabaseHelper sql;

    private int year_x, month_x, day_x;
    Button buttonDate, buttonSkipMessage, buttonDeleteAllData,
            buttonClearEditText, buttonQuayLai, buttonSkipSmsErr,
            buttonGetSmsDefault;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private ProgressDialog procress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_message );
        sideBarMenu();
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            String typeCheckSms = bd.getString( "checksms" );
            if (typeCheckSms.equals( "1" )) {
                textViewTypeCheckSms = (TextView) findViewById( R.id.textViewTypeCheckSms );
                textViewTypeCheckSms.setText( typeCheckSms );
            }
        }

        final String getDays = controller.dateDay( "yyyy-MM-dd" );
        buttonDate = (Button) findViewById( R.id.buttonDate );
        textV = (EditText) findViewById( R.id.editTextMessage );
        buttonDate.setText( getDays );
        buttonDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year_x = controller.dateInt( "yyyy" );
                month_x = controller.dateInt( "MM" ) - 1;
                day_x = controller.dateInt( "dd" );
                DatePickerDialog datePickerDialog = new DatePickerDialog( Message.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, int month, int dayOfMonth) {
                        if (view.isShown()) {
                            String month_s = String.valueOf( month + 1 );
                            if (month < 9) {
                                month_s = "0" + String.valueOf( month + 1 );
                            }
                            String day_s = "";
                            if (dayOfMonth < 10) {
                                day_s = "0" + String.valueOf( dayOfMonth );
                            } else {
                                day_s = String.valueOf( dayOfMonth );
                            }
                            String newDate = year + "-" + month_s + "-" + day_s;
                            buttonDate.setText( newDate );
                            textV.setText( "" );
                        }
                    }
                }, year_x, month_x, day_x );

                datePickerDialog.show();
            }
        } );
        buttonSkipSmsErr = (Button) findViewById( R.id.buttonSkipSmsErr );
        buttonSkipSmsErr.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateMath = buttonDate.getText().toString();
                String newDateMath = controller.convertFormatDate( dateMath );
                Cursor smsReadyString = sql.getAllDb( "SELECT SMSID FROM sms_ready_table WHERE NGAY=\"" + newDateMath + "\"" );
                String smsNotIn = "0";
                if (smsReadyString.getCount() > 0) {
                    while (smsReadyString.moveToNext()) {
                        String smsReadyId = smsReadyString.getString( smsReadyString.getColumnIndex( "SMSID" ) );
                        if (smsNotIn.indexOf( smsReadyId ) == -1) {
                            smsNotIn += "," + smsReadyId;
                        }
                    }
                }
                Uri mSmsinboxQueryUri = Uri.parse( "content://sms/" );
                String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
                String filter = "_id  NOT IN (" + smsNotIn + ") ";
                Cursor cursor1 = getContentResolver().query( mSmsinboxQueryUri, projection, filter, null, "_id asc" );
                if (cursor1.getCount() > 0) {
                    cursor1.moveToFirst();
                    String idMessage = cursor1.getString( cursor1.getColumnIndex( "_id" ) );
                    String body = cursor1.getString( cursor1.getColumnIndex( "body" ) );
                    String address = cursor1.getString( cursor1.getColumnIndex( "address" ) );
                    boolean insert = sql.insertSmsReady( Integer.parseInt( idMessage ), 0, body, 1, newDateMath );
                    String table5 = TABLE_NAME_5;
                    Integer deleteSolieu = sql.deleteAll( table5, newDateMath );
                    textV = (EditText) findViewById( R.id.editTextMessage );
                    textV.setText( "" );
                    clickShowEditText( newDateMath );
                    controller.showAlertDialog( Message.this, "Tin nhắn chưa xử lý được", "SDT : " + address + "\n" + body );
                } else {
                    controller.showAlertDialog( Message.this, "Thông báo", "đã xử lý hết tin nhắn" );
                }
            }
        } );

        buttonGetSmsDefault = (Button) findViewById( R.id.buttonGetSmsDefault );
        buttonGetSmsDefault.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> smsReady = new ArrayList<>();
                textV = (EditText) findViewById( R.id.editTextMessage );
                textContact = (TextView) findViewById( R.id.textViewContact );
                buttonDate = (Button) findViewById( R.id.buttonDate );
                String smsType;
                String dateMath = buttonDate.getText().toString();
                String newDateMath = controller.convertFormatDate( dateMath );
                Cursor smsReadyString = sql.getAllDb( "SELECT SMSID FROM sms_ready_table WHERE NGAY=\"" + newDateMath + "\"" );
                String smsNotIn = "0";
                if (smsReadyString.getCount() > 0) {
                    while (smsReadyString.moveToNext()) {
                        String smsReadyId = smsReadyString.getString( smsReadyString.getColumnIndex( "SMSID" ) );
                        if (smsNotIn.indexOf( smsReadyId ) == -1) {
                            smsNotIn += "," + smsReadyId;
                        }
                    }
                }
                Uri mSmsinboxQueryUri = Uri.parse( "content://sms/" );
                String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
                String filter = "_id  NOT IN (" + smsNotIn + ") ";
                Cursor cursor1 = getContentResolver().query( mSmsinboxQueryUri, projection, filter, null, "_id asc" );
                if (cursor1.getCount() > 0) {
                    cursor1.moveToFirst();
                    String strAddress = cursor1.getString( cursor1.getColumnIndex( "address" ) )
                            .replace( "(", "" ).replace( ")", "" ).replace( "-", "" ).replace( " ", "" ).replace( "+84", "0" );
                    String body = cursor1.getString( cursor1.getColumnIndex( "Body" ) );
                    String date = cursor1.getString( cursor1.getColumnIndex( "date" ) );
                    String dateTimeSms = controller.converTimeMill( "yyyy-MM-dd HH:mm:ss", Long.parseLong( date ) );
                    String dateDaySms = controller.converTimeMill( "yyyy-MM-dd", Long.parseLong( date ) );
                    textViewDate = (TextView) findViewById( R.id.textViewDate );
                    textViewDate.setText( dateTimeSms );
                    String timeSms = controller.converTimeMill( "HH:mm", Long.parseLong( date ) );
                    timeSmsVn = (TextView) findViewById( R.id.textViewTimeSms );
                    timeSmsVn.setText( timeSms );
                    if (cursor1.getString( cursor1.getColumnIndex( "type" ) ).equals( "1" )) {
                        smsType = "inbox";
                    } else {
                        smsType = "send";
                    }
                    textViewSmsType = (TextView) findViewById( R.id.textViewSmsType );
                    textViewSmsType.setText( smsType );
                    String idMessage = cursor1.getString( cursor1.getColumnIndex( "_id" ) );
                    smsId = (TextView) findViewById( textViewSmsId );
                    smsId.setText( idMessage );
                    long dateSms = Long.parseLong( cursor1.getString( cursor1.getColumnIndex( "date" ) ) );
                    String vnTime = controller.converTimeMill( "yyyy-MM-dd HH:mm:ss", dateSms );
                    Cursor dongia = sql.getAllDb( "SELECT TEN FROM dongia_table WHERE SDT=\"" + strAddress + "\" LIMIT 0,1 " );
                    if (dongia.getCount() > 0) {
                        dongia.moveToFirst();
                        String ten = dongia.getString( dongia.getColumnIndex( "TEN" ) );
                        textContact.setText( ten + "-" + strAddress );
                    } else {
                        textContact.setText( strAddress + "-" + strAddress );
                    }
                    textV = (EditText) findViewById( R.id.editTextMessage );
                    textV.setText( body );
                } else {
                    controller.showAlertDialog( Message.this, "Thông báo", "đã xử lý hết tin nhắn" );
                }
            }
        } );

        buttonDeleteAllData = (Button) findViewById( R.id.buttonDeleteAllData );
        buttonDeleteAllData.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHandler appdialog = new DialogHandler();
                appdialog.Confirm( Message.this, "Thông Báo", "Bạn có chắc xóa tất cả công nợ ngày hôm nay?",
                        "Cancel", "OK", aproc(), bproc() );

            }
        } );

        buttonClearEditText = (Button) findViewById( R.id.buttonClearEditText );
        buttonClearEditText.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textV.setText( "" );
            }
        } );

        buttonQuayLai = (Button) findViewById( R.id.buttonQuayLai );
        buttonQuayLai.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateMath = buttonDate.getText().toString();
                String newDateMath = controller.convertFormatDate( dateMath );
                Cursor smsBackString = sql.getAllDb( "SELECT SMSID FROM sms_ready_table WHERE NGAY=\"" + newDateMath + "\" ORDER BY ID DESC LIMIT 1" );
                if (smsBackString.getCount() > 0) {
                    smsBackString.moveToFirst();
                    String smsIdBack = smsBackString.getString( smsBackString.getColumnIndex( "SMSID" ) );
                    String table5 = TABLE_NAME_5;
                    String table6 = sql.TABLE_NAME_6;
                    sql.deleteSolieuSmsID( table5, smsIdBack );
                    sql.deleteSolieuSmsID( table6, smsIdBack );
                    textV = (EditText) findViewById( R.id.editTextMessage );
                    textV.setText( "" );
                    Toast.makeText( Message.this, "Đã quay lại tin nhắn trước.làm ơn nhấn nút tính tiếp", Toast.LENGTH_LONG ).show();
                }
            }
        } );

        sql = new DatabaseHelper( this );
        Cursor timeTable = sql.getAllDb( "SELECT * FROM time_table LIMIT 0,1" );
        String HSD = "1970-01-01";
        if (timeTable.getCount() > 0) {
            timeTable.moveToFirst();
            HSD = timeTable.getString( timeTable.getColumnIndex( "KHOAAPP" ) );
        }
        textViewHSD = (TextView) findViewById( R.id.textViewHSD );
        textViewHSD.setText( HSD );
        Cursor timeLock = sql.getAllDb( "SELECT * FROM time_table LIMIT 0,1" );
        String KhoaLo = "23:59";
        String KhoaDe = "23:59";
        if (timeLock.getCount() > 0) {
            timeLock.moveToFirst();
            KhoaLo = timeLock.getString( timeLock.getColumnIndex( "KHOALO" ) );
            KhoaDe = timeLock.getString( timeLock.getColumnIndex( "KHOADE" ) );
        }
        textViewKhoaDe = (TextView) findViewById( R.id.textViewKhoaDe );
        textViewKhoaLo = (TextView) findViewById( R.id.textViewKhoaLo );
        textViewKhoaDe.setText( KhoaDe );
        textViewKhoaLo.setText( KhoaLo );
    }

    public void sideBarMenu() {
        /* SideBar */
        mDrawerLayout = (DrawerLayout) findViewById( R.id.drawer_layout );
        mDrawerToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open, R.string.close );
        mNavigationView = (NavigationView) findViewById( R.id.navigation );
        mNavigationView.setItemIconTintList( null );
        mNavigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked( false );
                else menuItem.setChecked( true );
                //Closing drawer on item click
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Intent intent = new Intent( Message.this, MainActivity.class );
                        startActivity( intent );
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent( Message.this, Contact.class );
                        startActivity( intent2 );
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent( Message.this, Customer.class );
                        startActivity( intent3 );
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent( Message.this, ManagerMoney.class );
                        startActivity( intent4 );
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent( Message.this, Statistic.class );
                        startActivity( intent5 );
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent( Message.this, MainXoaCongNo.class );
                        startActivity( intent6 );
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent( Message.this, viewSmsNotMoney.class );
                        startActivity( intent7 );
                        return true;
                    case R.id.historySms:
                        Intent intent8 = new Intent( Message.this, HistorySms.class );
                        startActivity( intent8 );
                        return true;
                    default:
                        return true;
                }
            }
        } );
        mDrawerLayout.addDrawerListener( mDrawerToggle );
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        /* SideBar */
    }

    public Runnable aproc() {
        return new Runnable() {
            public void run() {
                smsId = (TextView) findViewById( textViewSmsId );
                smsId.setText( "0" );
                textContact = (TextView) findViewById( R.id.textViewContact );
                textContact.setText( "" );
                textMessage = (TextView) findViewById( R.id.editTextMessage );
                textMessage.setText( "" );
                textViewDate = (TextView) findViewById( R.id.textViewDate );
                textViewDate.setText( "" );
                sql = new DatabaseHelper( Message.this );
                String table5 = TABLE_NAME_5;
                String table6 = sql.TABLE_NAME_6;
                buttonDate = (Button) findViewById( R.id.buttonDate );
                String getDays = buttonDate.getText().toString();
                Toast.makeText( Message.this, getDays, Toast.LENGTH_LONG ).show();
                sql.deleteAllNgay( table5, getDays );
                sql.deleteAllNgay( table6, getDays );
                Toast.makeText( Message.this, "Xóa thành công dữ liệu", Toast.LENGTH_LONG ).show();
            }
        };
    }

    public Runnable aprocNext() {
        return new Runnable() {
            public void run() {
                buttonDate = (Button) findViewById( R.id.buttonDate );
                String ngayDaChon = buttonDate.getText().toString();
                clickShowEditText( ngayDaChon );
            }
        };
    }

    public Runnable bprocNext() {
        return new Runnable() {
            public void run() {
                buttonDate = (Button) findViewById( R.id.buttonDate );
                String ngayDaChon = buttonDate.getText().toString();
                clickShowEditText( ngayDaChon );
            }
        };
    }

    public Runnable bproc() {
        return new Runnable() {
            public void run() {
            }
        };
    }


    private void getKqsxmb(String dateDelete, String dateImport) {
        final String delete = dateDelete;
        final String dateLink = dateImport;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    String newDateLink = controller.convertFormatDateDd( dateLink );
                    String link = "http://18.221.7.60/kqsx.php?date=" + newDateLink;
                    new ReadXml().execute( link );
                }
            } );
        } else {
            controller.showAlertDialog( Message.this, "Thông báo", "Không kết nối được internet" );
        }
    }

    class ReadXml extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String kq = controller.getXMLFromUrl( params[0] );
            return kq;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.indexOf( "false" ) == -1) {
                JSONObject root = null;
                try {
                    sql = new DatabaseHelper( Message.this );
                    JSONObject o = new JSONObject( s );
                    JSONArray a = o.getJSONArray( "ketqua" ); // lay gia tri ngoai cung roi moi foreach
                    String ngay = o.getJSONArray( "link" ).getString( 0 );
                    String ngayTrue = controller.convertFormatDate( ngay );
                    String loto, value;
                    String valueDau = "100";
                    String query = "SELECT * FROM kq_table WHERE NGAY=\"" + ngayTrue + "\" ";
                    sql.getAllDb( "DELETE FROM kq_table WHERE NGAY=\"" + ngayTrue + "\"" );
                    Cursor res = sql.getAllDb( query );
                    if (res.getCount() != 27) { // kiem tra da insert ket qua ngay hom nay
                        for (int i = 0; i < a.length(); i++) {
                            String[] separated = a.getString( i ).split( "-" );
                            for (int j = 0; j < separated.length; j++) {
                                loto = separated[j].replaceAll( "(^\\s+|\\s+$)", "" );
                                if (loto.length() > 2) {
                                    value = loto.substring( loto.length() - 2 );
                                    valueDau = loto.substring( 0, 2 );
                                } else {
                                    value = loto;
                                    valueDau = loto;
                                }
                                boolean ket = sql.insertDataKq( loto, i, j, ngayTrue, value, valueDau );
                            }
                        }
                        Cursor kqsx2 = sql.getAllDb( "SELECT * FROM kq_table WHERE NGAY=\"" + ngayTrue + "\"" );
                        if (kqsx2.getCount() != 27) {
                            controller.showAlertDialog( Message.this, "Thông Báo",
                                    "Chưa tìm thấy kết quả sổ xố ngày " + ngay + "\n" +
                                            "Không thể tính được tổng bảng.\n" );
                        } else {
                            clickShowEditText( ngayTrue );
                        }
                    } else {
                        clickShowEditText( ngayTrue );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                controller.showAlertDialog( Message.this, "Thông Báo", "Chưa tìm thấy kết quả nào!" );
            }
        }

    }

    public String scanSms() {
        ArrayList<String> smsReady = new ArrayList<>();
        textV = (EditText) findViewById( R.id.editTextMessage );
        textContact = (TextView) findViewById( R.id.textViewContact );
        buttonDate = (Button) findViewById( R.id.buttonDate );
        String err = "";
        String smsType;
        if (textV.getText().toString().equals( "" )) {
            String dateMath = buttonDate.getText().toString();
            String newDateMath = controller.convertFormatDate( dateMath );
            Cursor smsReadyString = sql.getAllDb( "SELECT SMSID FROM sms_ready_table WHERE NGAY=\"" + newDateMath + "\"" );
            String smsNotIn = "0";
            if (smsReadyString.getCount() > 0) {
                while (smsReadyString.moveToNext()) {
                    String smsReadyId = smsReadyString.getString( smsReadyString.getColumnIndex( "SMSID" ) );
                    if (smsNotIn.indexOf( smsReadyId ) == -1) {
                        smsNotIn += "," + smsReadyId;
                    }
                }
            }
            Uri mSmsinboxQueryUri = Uri.parse( "content://sms/" );
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            String filter = "_id  NOT IN (" + smsNotIn + ") AND Body!=\"\" ";
            Cursor cursor1 = getContentResolver().query( mSmsinboxQueryUri, projection, filter, null, "_id asc" );

            if (cursor1.getCount() > 0) {
                cursor1.moveToFirst();
                String strAddress = cursor1.getString( cursor1.getColumnIndex( "address" ) )
                        .replace( "(", "" ).replace( ")", "" ).replace( "-", "" ).replace( " ", "" ).replace( "+84", "0" );
                String body = cursor1.getString( cursor1.getColumnIndex( "Body" ) );
                String date = cursor1.getString( cursor1.getColumnIndex( "date" ) );
                String dateTimeSms = controller.converTimeMill( "yyyy-MM-dd HH:mm:ss", Long.parseLong( date ) );
                String dateDaySms = controller.converTimeMill( "yyyy-MM-dd", Long.parseLong( date ) );

                textViewDate = (TextView) findViewById( R.id.textViewDate );
                textViewDate.setText( dateTimeSms );

                String timeSms = controller.converTimeMill( "HH:mm", Long.parseLong( date ) );
                timeSmsVn = (TextView) findViewById( R.id.textViewTimeSms );
                timeSmsVn.setText( timeSms );

                if (cursor1.getString( cursor1.getColumnIndex( "type" ) ).equals( "1" )) {
                    smsType = "inbox";
                } else {
                    smsType = "send";
                }
                textViewSmsType = (TextView) findViewById( R.id.textViewSmsType );
                textViewSmsType.setText( smsType );

                String idMessage = cursor1.getString( cursor1.getColumnIndex( "_id" ) );
                smsId = (TextView) findViewById( textViewSmsId );
                smsId.setText( idMessage );
                String addre = cursor1.getString( cursor1.getColumnIndex( "address" ) );
                String sdtfk = "(" + addre.substring(0,3) + ")" + " " + addre.substring(3,6) + "-" + addre.substring(6);
                String filtera = "_id IN (" + smsNotIn + ") AND Body=\""+ body
                        + "\" AND type = \""  + cursor1.getString( cursor1.getColumnIndex( "type" ) ) + "\""
                        + " AND ( address = \""  + addre + "\"" + " OR address = \"" + sdtfk + "\" )";
                Cursor cursor2 = getContentResolver().query( mSmsinboxQueryUri, projection, filtera, null, "_id asc" );
                if (cursor2.getCount() > 0) {
                    body += "@tinlap@";
                }

                long dateSms = Long.parseLong( cursor1.getString( cursor1.getColumnIndex( "date" ) ) );
                String vnTime = controller.converTimeMill( "yyyy-MM-dd HH:mm:ss", dateSms );
                Cursor dongia = sql.getAllDb( "SELECT TEN FROM dongia_table WHERE SDT=\"" + strAddress + "\" LIMIT 0,1 " );
                if (dongia.getCount() > 0) {
                    dongia.moveToFirst();
                    String ten = dongia.getString( dongia.getColumnIndex( "TEN" ) );
                    textContact.setText( ten + "-" + strAddress );
                } else {
                    textContact.setText( strAddress + "-" + strAddress );
                }
                err = checkMessage( body, dateMath, smsType ).replace( "lh", "nh" ).replace( "al", "an" );
            } else {

            }
        }
        return err;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.sms_button, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_calculate:
                buttonDate = (Button) findViewById(R.id.buttonDate);
                String getDays = buttonDate.getText().toString();
                textViewHSD = (TextView) findViewById(R.id.textViewHSD);
                String getHsd = textViewHSD.getText().toString();
                long miliGetDay = controller.converDayToMill("yyyy-MM-dd", getDays);
                long miligetHsd = controller.converDayToMill("yyyy-MM-dd", getHsd);
                if (miligetHsd >= miliGetDay) {
                    textViewTypeCheckSms = (TextView) findViewById(R.id.textViewTypeCheckSms);
                    String newGetDays = controller.convertFormatDate(getDays);
                    if (textViewTypeCheckSms.getText().toString().equals("0")) {
                        sql = new DatabaseHelper(this);
                        Cursor kqsx = sql.getAllDb("SELECT * FROM kq_table WHERE NGAY=\"" + newGetDays + "\"");
                        if (kqsx.getCount() != 27) {
                            String dateDayLink = controller.dateDay("dd-MM-yyyy");
                            getKqsxmb(dateDayLink, newGetDays);
                        } else {
                            clickShowEditText(newGetDays);
                        }
                    } else {
                        clickShowEditText(newGetDays);
                    }
                } else {
                    controller.showAlertDialog(Message.this, "Thông báo", "Đã hết hạn sử dụng ứng dụng! làm ơn đăng kí sử dụng tiếp");
                }
                return true;
            case R.id.luutinkho:
                TextView smsIdLuuTin = (TextView) findViewById(textViewSmsId);
                String smsIdLuu = smsIdLuuTin.getText().toString();
                Uri mSmsinboxQueryUri = Uri.parse("content://sms/");
                String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
                String filter = "_id=" + smsIdLuu;
                Cursor cursor2 = getContentResolver().query(mSmsinboxQueryUri, projection, filter, null, "_id asc");
                if (cursor2.getCount() > 0) {
                    cursor2.moveToFirst();
                    String idMessage = cursor2.getString(cursor2.getColumnIndex("_id"));
                    String noiDungTin = cursor2.getString(cursor2.getColumnIndex("Body"));
                    buttonDate = (Button) findViewById(R.id.buttonDate);
                    String dateMath = buttonDate.getText().toString();
                    String newDateMath = controller.convertFormatDate(dateMath);
                    boolean insert = sql.insertSmsReady(Integer.parseInt(idMessage), 0, noiDungTin, 2, newDateMath);
                    String table5 = TABLE_NAME_5;
                    Integer deleteSolieu = sql.deleteAll(table5, newDateMath);
                    textV = (EditText) findViewById(R.id.editTextMessage);
                    textV.setText("");
                    clickShowEditText(newDateMath);
                    Toast.makeText(Message.this, "Lưu tin chưa tính tiền thành công", Toast.LENGTH_SHORT).show();
                } else {
                    controller.showAlertDialog(Message.this, "Thông báo", "Chưa có tin nào để lưu");
                }
                return true;
            case R.id.guitincanbang:
                Intent intent = new Intent(Message.this, GuiTinCanBang.class);
                startActivity(intent);
                return true;
            case R.id.caidaicanbang:
                Intent intent2 = new Intent(Message.this, CaiDatCanBang.class);
                startActivity(intent2);
                return true;
            case R.id.chitiettinnhan:
                Intent intent3 = new Intent(Message.this, ChiTietTinNhan.class);
                startActivity(intent3);
                return true;
            case R.id.capnhatketqua:
                buttonDate = (Button) findViewById(R.id.buttonDate);
                final String ngaySelect = buttonDate.getText().toString();
                textViewHSD = (TextView) findViewById(R.id.textViewHSD);
                String getHsd1 = textViewHSD.getText().toString();
                long miliGetDay1 = controller.converDayToMill("yyyy-MM-dd", ngaySelect);
                long miligetHsd1 = controller.converDayToMill("yyyy-MM-dd", getHsd1);
                if (miligetHsd1 >= miliGetDay1) {
                    Cursor kqsx = sql.getAllDb("SELECT * FROM kq_table WHERE NGAY=\"" + ngaySelect + "\"");
                    if (kqsx.getCount() != 27) {
                        new CountDownTimer(4000, 2000) {
                            public void onTick(long millisUntilFinished) {
                                getKqsxmb(ngaySelect, ngaySelect);
                            }

                            public void onFinish() {
                                Cursor kqsx1 = sql.getAllDb("SELECT * FROM kq_table WHERE NGAY=\"" + ngaySelect + "\"");
                                if (kqsx1.getCount() == 27) {
                                    xulySms(ngaySelect);
                                } else {
                                    controller.showAlertDialog(Message.this, "Thông báo", "Chưa tìm thấy kết quả");
                                }
                            }
                        }.start();
                    } else {
                        xulySms(ngaySelect);
                    }
                } else {
                    controller.showAlertDialog(Message.this, "Thông báo", "Đã hết hạn sử dụng ứng dụng! làm ơn đăng kí sử dụng tiếp");
                }
                return true;
            case R.id.luutincanbang:
                ArrayList<String> smsReady = new ArrayList<>();
                textV = (EditText) findViewById(R.id.editTextMessage);
                String body = textV.getText().toString();

                textContact = (TextView) findViewById(R.id.textViewContact);
                buttonDate = (Button) findViewById(R.id.buttonDate);
                String err = "";
                String smsType = "inbox";

                if (!body.equals("")) {
                    String dateMath = buttonDate.getText().toString();
                    String newDateMath = controller.convertFormatDate(dateMath);
                    String smsNotIn = "0";
                    String strAddress = "0912345678";
                    String date = controller.dateDay("yyyy-MM-dd HH:mm:ss");
                    String dateTimeSms = date;
                    String dateDaySms = date;
                    textViewDate = (TextView) findViewById(R.id.textViewDate);
                    textViewDate.setText(dateTimeSms);
                    String timeSms = controller.dateDay("HH:mm");
                    timeSmsVn = (TextView) findViewById(R.id.textViewTimeSms);
                    timeSmsVn.setText(timeSms);
                    smsType = "inbox";
                    textViewSmsType = (TextView) findViewById(R.id.textViewSmsType);
                    textViewSmsType.setText(smsType);
                    String idMessage = "999999";
                    smsId = (TextView) findViewById(textViewSmsId);
                    smsId.setText(idMessage);
                    textContact.setText(strAddress + "-" + strAddress);
                    err = checkMessage(body, dateMath, smsType).replace("lh", "nh").replace("al", "an");
                    String table5 = TABLE_NAME_5;
                    String decb = "";
                    String locb = "";
                    String bacangcb = "";
                    if (err.indexOf( "</font>" ) > -1) {
                        sql.deleteSolieuSmsID( table5, String.valueOf( idMessage ) );
                        textV.setText( Html.fromHtml( err ) );
                    } else {
                        String query = "SELECT * FROM solieu_table WHERE SMSID=" + idMessage;
                        Cursor table_solieu = sql.getAllDb(query);
                        if (table_solieu.getCount() > 0) {
                            while (table_solieu.moveToNext()) {
                                if(table_solieu.getString(table_solieu.getColumnIndex("KIHIEU")).equals("de")) {
                                    decb += table_solieu.getString(table_solieu.getColumnIndex("LOTO")) + " ";
                                } else if(table_solieu.getString(table_solieu.getColumnIndex("KIHIEU")).equals("lo")){
                                    locb += table_solieu.getString(table_solieu.getColumnIndex("LOTO")) + " ";
                                } else if(table_solieu.getString(table_solieu.getColumnIndex("KIHIEU")).equals("bacang")){
                                    bacangcb += table_solieu.getString(table_solieu.getColumnIndex("LOTO")) + " ";
                                }
                            }
                            sql.deletealllotocanbang(TABLE_NAME_11,"-1");
                            sql.insertlotocanbang("de",decb);
                            sql.insertlotocanbang("lo",locb);
                            sql.insertlotocanbang("bacang",bacangcb);
                            sql.deleteSolieuSmsID( table5, String.valueOf( idMessage ) );
                        }
                        textV.setText( "" );

                        controller.showAlertDialog(Message.this, "Thông báo", "Đã cập nhật lưu tin thành công " +
                                "--- đề : " + decb +
                                "--- lô : " + locb +
                                "--- 3c : " + bacangcb
                        );
                    }
                } else {
                    String decb = "";
                    String locb = "";
                    String bacangcb = "";
                    String lotocanbang = "SELECT * FROM lotocanbang WHERE 1" ;
                    Cursor table_lotocanbang = sql.getAllDb(lotocanbang);
                    if (table_lotocanbang.getCount() > 0) {
                        while (table_lotocanbang.moveToNext()) {
                            if(table_lotocanbang.getString(table_lotocanbang.getColumnIndex("KIHIEU")).equals("de")) {
                                decb = table_lotocanbang.getString(table_lotocanbang.getColumnIndex("LOTO"));
                            } else if(table_lotocanbang.getString(table_lotocanbang.getColumnIndex("KIHIEU")).equals("lo")){
                                locb = table_lotocanbang.getString(table_lotocanbang.getColumnIndex("LOTO"));
                            } else if(table_lotocanbang.getString(table_lotocanbang.getColumnIndex("KIHIEU")).equals("bacang")){
                                bacangcb = table_lotocanbang.getString(table_lotocanbang.getColumnIndex("LOTO")) + ",";
                            }
                        }
                    }
                    controller.showAlertDialog(Message.this, "Thông báo", "Điền dữ liệu vào ô trống " +
                            "--- đề : " + decb +
                            "--- lô : " + locb +
                            "--- 3c : " + bacangcb
                    );
                }
                return true;

        } // end cac nut


        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void xulySms(final String ngaySelect) {
        procress = new ProgressDialog( Message.this );
        procress.setCancelable( false );
        procress.setTitle( "Lấy dữ liệu" );
        procress.setMessage( "Đang Xử lý" );
        procress.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
        procress.setProgress( 0 );
        procress.show();
        String sms_id_ready_query = "SELECT SMSID FROM \"solieu_table\" WHERE NGAY=\"" + ngaySelect + "\"";
        Cursor sms_ready_table_query = sql.getAllDb( sms_id_ready_query );
        String sms_get_by_id = "";
        if (sms_ready_table_query.getCount() > 0) {
            while (sms_ready_table_query.moveToNext()) {
                String get_smsID = sms_ready_table_query.getString( sms_ready_table_query.getColumnIndex( "SMSID" ) );
                if (sms_get_by_id.indexOf( get_smsID ) == -1) {
                    if (sms_get_by_id.equals( "" )) {
                        sms_get_by_id += get_smsID;
                    } else {
                        sms_get_by_id += "," + get_smsID;
                    }
                }
            }
        }
        final String query = "SELECT sms_ready_table.ID,sms_ready_table.SMSID,sms_ready_table.CONTENT," +
                "sms_ready_table.NGAY,solieu_table.KIEU AS KIEU," +
                "solieu_table.SDT AS SDT,solieu_table.TEN AS TEN" +
                " FROM sms_ready_table" +
                " LEFT JOIN solieu_table ON (sms_ready_table.SMSID = solieu_table.SMSID )" +
                " WHERE sms_ready_table.NGAY = \"" + ngaySelect + "\"" +
                " AND sms_ready_table.SMSID IN (" + sms_get_by_id + ") AND sms_ready_table.CONTENT != \"\" GROUP BY sms_ready_table.ID";
        Cursor sms_ready_table1 = sql.getAllDb( query );
        int totalSms = sms_ready_table1.getCount();
        procress.setMax( totalSms );
        new CountDownTimer( totalSms * 50, 25 ) {
            String startCountdown = "0";

            public void onTick(long millisUntilFinished) {
                if (startCountdown.equals( "0" )) {
                    PrcrosseData p = new PrcrosseData();
                    Cursor sms_ready_table = sql.getAllDb( query );
                    if (sms_ready_table.getCount() > 0) {
                        p.execute( sms_ready_table );
                    }
                    startCountdown = "1";
                }
            }

            public void onFinish() {
                Cursor sms_ready_table1 = sql.getAllDb( query );
                if (sms_ready_table1.getCount() > 0) {
                    int i = 0;
                    while (sms_ready_table1.moveToNext()) {
                        i++;
                        String content = sms_ready_table1.getString( sms_ready_table1.getColumnIndex( "CONTENT" ) );
                        String smsType = sms_ready_table1.getString( sms_ready_table1.getColumnIndex( "KIEU" ) );
                        String sms_id = sms_ready_table1.getString( sms_ready_table1.getColumnIndex( "SMSID" ) );
                        String ten = sms_ready_table1.getString( sms_ready_table1.getColumnIndex( "TEN" ) );
                        String sdt = sms_ready_table1.getString( sms_ready_table1.getColumnIndex( "SDT" ) );
                        String table5 = TABLE_NAME_5;
                        TextView textContact1 = (TextView) findViewById( R.id.textViewContact );
                        textContact1.setText( ten + "-" + sdt );
                        textViewSmsType = (TextView) findViewById( R.id.textViewSmsType );
                        textViewSmsType.setText( smsType );
                        smsId = (TextView) findViewById( textViewSmsId );
                        smsId.setText( sms_id );
                        sql.deleteSolieuSmsID( table5, sms_id );
                        checkMessage( content, ngaySelect, smsType );
                        if (i == sms_ready_table1.getCount()) {
                            textViewSmsType.setText( "0" );
                            textContact1.setText( "" );
                            textViewDate = (TextView) findViewById( R.id.textViewDate );
                            textViewDate.setText( "" );
                            TextView editTextMessage1 = (TextView) findViewById( R.id.editTextMessage );
                            editTextMessage1.setText( "" );
                            smsId.setText( "0" );
                            DialogHandler appdialog = new DialogHandler();
                            appdialog.Confirm( Message.this, "Thông Báo", "Hãy nhấn đồng ý để xử lý tin tiếp theo !",
                                    "Đóng", "Đồng ý", aprocNext(), bprocNext() );
                            procress.cancel();
                        }
                    }
                } else {
                    DialogHandler appdialog = new DialogHandler();
                    appdialog.Confirm( Message.this, "Thông Báo", "Hãy nhấn đồng ý để xử lý tin tiếp theo !",
                            "Đóng", "Đồng ý", aprocNext(), bprocNext() );
                    procress.cancel();
                }
            }
        }.start();
    }

    public class PrcrosseData extends AsyncTask<Cursor, String, String> {
        @Override
        protected String doInBackground(Cursor... cursors) {
            int count_sms = cursors[0].getCount();
            int progcessPrecent = 1;
            if (count_sms > 0) {
                while (cursors[0].moveToNext()) {
                    try {
                        Thread.sleep( 50 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String content = cursors[0].getString( cursors[0].getColumnIndex( "CONTENT" ) );
                    this.publishProgress( String.valueOf( progcessPrecent ), String.valueOf( count_sms ),
                            content );
                    progcessPrecent++;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate( values );
            int progress = Integer.parseInt( values[0] );
            int total = Integer.parseInt( values[1] );
            String message = values[2];
//            int precent = progress * 100 / total;
            procress.setProgress( progress );
            procress.setMessage( message );
            if (values[0] == values[1]) {
                procress.setMessage( "Đang hoàn tất cập nhật" );
//                procress.cancel();
            }
        }
    }

    private void clickShowEditText(String getDays) {
        sql = new DatabaseHelper( this );
        proccessMessage( getDays );  // neu sai xu ly tiep
    }

    public void proccessMessage(String getDays) {
        textV = (EditText) findViewById( R.id.editTextMessage );
        String kitu = textV.getText().toString().toLowerCase();
        if (kitu.equals( "" )) {
            TextView smsId2 = (TextView) findViewById( textViewSmsId );
            int smsID2 = Integer.parseInt( smsId2.getText().toString() );
            if (smsID2 != 0) {
                buttonDate = (Button) findViewById( R.id.buttonDate );
                String daySkip2 = buttonDate.getText().toString();
                boolean insert2 = sql.insertSmsReady( smsID2, 0, "", 1, daySkip2 );
            }
            String err = scanSms(); // in ki tu ra o edittext
            if (err != "") {
                if (err.indexOf( "</font>" ) == -1) {
                    smsId = (TextView) findViewById( textViewSmsId );
                    int smsID = Integer.parseInt( smsId.getText().toString() );
                    String smsReadyDay = controller.convertFormatDate( getDays );
                    boolean insert = sql.insertSmsReady( smsID, 0, err, 1, smsReadyDay );
                    textV.setText( "" );
                    clickShowEditText( getDays );
                } else {  // khong luu du luu khi van chua dung dinh dang
                    if (err.equals( "<font color=\"red\" >ok</font>" )
                            || err.equals( "<font color=\"red\" >ok</font>1" )
                            || err.equals( "<font color=\"red\" >ok</font>2" )
                            || err.equals( "<font color=\"red\" >ok</font>3" )
                            || err.equals( "<font color=\"red\" >ok</font>4" )
                            || err.equals( "<font color=\"red\" >ok</font>5" )
                            || err.equals( "<font color=\"red\" >ok</font>6" )
                            || err.equals( "<font color=\"red\" >ok</font>7" )
                            || err.equals( "<font color=\"red\" >ok</font>8" )
                            || err.equals( "<font color=\"red\" >ok</font>9" )
                            || err.equals( "<font color=\"red\" >ok</font>10" )
                            || err.equals( "<font color=\"red\" >ok</font>11" )
                            || err.equals( "<font color=\"red\" >ok</font>12" )
                            || err.equals( "<font color=\"red\" >ok</font>13" )
                            || err.equals( "<font color=\"red\" >ok</font>14" )
                            || err.equals( "<font color=\"red\" >ok</font>15" )
                            ) {
                        smsId = (TextView) findViewById( textViewSmsId );
                        int smsID = Integer.parseInt( smsId.getText().toString() );
                        String smsReadyDay = controller.convertFormatDate( getDays );
                        boolean insert = sql.insertSmsReady( smsID, 0, err, 1, smsReadyDay );
                        textV.setText( "" );
                        clickShowEditText( getDays );
                    } else {
                        String idSms = smsId.getText().toString();
                        int idSmsInt = Integer.parseInt( idSms );
                        String table5 = TABLE_NAME_5;
                        sql.deleteSolieuSmsID( table5, String.valueOf( idSmsInt ) );
                        textV.setText( Html.fromHtml( err ) );
                    }
                }
            } else {
                controller.showAlertDialog( Message.this, "Thông báo", "đã xử lý hết tin nhắn" );
            }
        } else {
            textViewSmsType = (TextView) findViewById( R.id.textViewSmsType );
            String smsType = textViewSmsType.getText().toString();
            if (!smsType.equals( "" )) {
                String errFix = checkMessage( kitu, getDays, smsType );  // neu o input co du lieu thi ta xu ly cac chuoi co trong edittext
                if (errFix.indexOf( "</font>" ) == -1) {
                    smsId = (TextView) findViewById( textViewSmsId );
                    int smsID = Integer.parseInt( smsId.getText().toString() );
                    String smsReadyDay = controller.convertFormatDate( getDays );
                    boolean insert = sql.insertSmsReady( smsID, 0, errFix, 1, smsReadyDay );
                    textV.setText( "" );
                    clickShowEditText( getDays );
                } else { /* khong luu du luu khi van chua dung dinh dang */
                    if (errFix.equals( "<font color=\"red\" >ok</font>" )
                            || errFix.equals( "<font color=\"red\" >ok</font>1" )
                            || errFix.equals( "<font color=\"red\" >ok</font>2" )
                            || errFix.equals( "<font color=\"red\" >ok</font>3" )
                            || errFix.equals( "<font color=\"red\" >ok</font>4" )
                            || errFix.equals( "<font color=\"red\" >ok</font>5" )
                            || errFix.equals( "<font color=\"red\" >ok</font>6" )
                            || errFix.equals( "<font color=\"red\" >ok</font>7" )
                            || errFix.equals( "<font color=\"red\" >ok</font>8" )
                            || errFix.equals( "<font color=\"red\" >ok</font>9" )
                            || errFix.equals( "<font color=\"red\" >ok</font>10" )
                            || errFix.equals( "<font color=\"red\" >ok</font>11" )
                            || errFix.equals( "<font color=\"red\" >ok</font>12" )
                            || errFix.equals( "<font color=\"red\" >ok</font>13" )
                            || errFix.equals( "<font color=\"red\" >ok</font>14" )
                            || errFix.equals( "<font color=\"red\" >ok</font>15" )) {
                        smsId = (TextView) findViewById( textViewSmsId );
                        int smsID = Integer.parseInt( smsId.getText().toString() );
                        String smsReadyDay = controller.convertFormatDate( getDays );
                        boolean insert = sql.insertSmsReady( smsID, 0, errFix, 1, smsReadyDay );
                        textV.setText( "" );
                        clickShowEditText( getDays );
                    } else {
                        String idSms = smsId.getText().toString();
                        int idSmsInt = Integer.parseInt( idSms );
                        String table5 = TABLE_NAME_5;
                        sql.deleteSolieuSmsID( table5, String.valueOf( idSmsInt ) ); /* khong luu du luu khi van chua dung dinh dang */
                        textV.setText( Html.fromHtml( errFix ) );
                    }
                }
            } else {
                controller.showAlertDialog( Message.this, "Thông báo", "hãy làm sạch trước khi bấm nút" );
            }
        }
    }

    public String checkMessage(String kitu, String getDays, String smsType) {
        if (kitu.replaceAll( "(^\\s+|\\s+$)", "" ).length() == 1) {
            return "<font color=\"RED\">" + kitu.replaceAll( "(^\\s+|\\s+$)", "" ) + " </font>";
        }
        String error = "";
        ArrayList<String> kieuboso = controller.kieuboso();
        ArrayList<String> kieubosodan = controller.kieubosodan();
        HashMap<String, ArrayList<String>> hashmap = sql.readkitu();
        ArrayList<String> limitNumber = controller.limitNumber();
        ArrayList<String> limitMiniNumber = controller.limitMiniNumber();
        ArrayList<String> limitNumberBaCang = controller.limitNumberBaCang();
        textContact = (TextView) findViewById( R.id.textViewContact );
        String[] listDonGia = textContact.getText().toString().split( "-" );
        smsId = (TextView) findViewById( textViewSmsId );
        String idSms = smsId.getText().toString();
        int idSmsInt = Integer.parseInt( idSms );
        String compareDe = "100";
        String compareDeDau = sql.compareDeDau( getDays );
        String compareDe2 = sql.compareDe( getDays );
        String compareGiai1A = sql.compareGiai( getDays, 1, "VALUEDAU" );
        String compareGiai1B = sql.compareGiai( getDays, 1, "VALUE" );
        ArrayList<String> compareLo = sql.getArrayKeyRes( getDays );
        ArrayList<String> compareLo2 = sql.getArrayKeyRes( getDays );
        ArrayList<String> compareLoDau = sql.getLoDauGiai( getDays );
        String compareBaCang = sql.compareBaCang( getDays );
        String dataSoLieuDate = getDays;
        String [] xiAb = controller.inArrayXiAb();
        timeSmsVn = (TextView) findViewById( R.id.textViewTimeSms );
        String timeSms = timeSmsVn.getText().toString();
        String[] timeArrSms = timeSms.split( ":" );
        int dongiaId = 0;
        double hsde = 0.8;
        double thuongde = 70;
        double hslo = 22;
        double thuonglo = 80;
        double hsx2 = 0.8;
        double thuongxien2 = 10;
        double hsx3 = 0.8;
        double thuongxien3 = 40;
        double hsx4 = 0.8;
        double thuongxien4 = 100;
        double hsbacang = 0.8;
        double thuongbacang = 400;
        if (listDonGia.length == 2) {
            sql = new DatabaseHelper( this );
            Cursor dongia = sql.getAllDb( "SELECT * FROM dongia_table WHERE SDT=\"" + listDonGia[1] + "\" LIMIT 0,1 " );
            if (dongia.getCount() != 0) {
                dongia.moveToFirst();
                dongiaId = Integer.parseInt( dongia.getString( dongia.getColumnIndex( "ID" ) ) );
                hsde = Double.parseDouble( dongia.getString( dongia.getColumnIndex( "HSDE" ) ) );
                thuongde = Double.parseDouble( dongia.getString( dongia.getColumnIndex( "THDE" ) ) );
                hslo = Double.parseDouble( dongia.getString( dongia.getColumnIndex( "HSLO" ) ) );
                thuonglo = Double.parseDouble( dongia.getString( dongia.getColumnIndex( "THLO" ) ) );
                hsx2 = Double.parseDouble( dongia.getString( dongia.getColumnIndex( "HSXIENHAI" ) ) );
                thuongxien2 = Double.parseDouble( dongia.getString( dongia.getColumnIndex( "THXIENHAI" ) ) );
                hsx3 = Double.parseDouble( dongia.getString( dongia.getColumnIndex( "HSXIENBA" ) ) );
                thuongxien3 = Double.parseDouble( dongia.getString( dongia.getColumnIndex( "THXIENBA" ) ) );
                hsx4 = Double.parseDouble( dongia.getString( dongia.getColumnIndex( "HSXIENBON" ) ) );
                thuongxien4 = Double.parseDouble( dongia.getString( dongia.getColumnIndex( "THXIENBON" ) ) );
                hsbacang = Double.parseDouble( dongia.getString( dongia.getColumnIndex( "HSBACANG" ) ) );
                thuongbacang = Double.parseDouble( dongia.getString( dongia.getColumnIndex( "THBACANG" ) ) );
            }
        }
        String getKiTu = kitu;
        kitu = kitu.toLowerCase();// loai bo dau ,viet hoa thanh viet thuong
        String converKitu = controller.uniStrip( controller.removeAccent( kitu.replaceAll( "₫", "d" ) ) );
        String addkitu = controller.repkDau( converKitu );
        String[] message = addkitu.replaceAll( "(^\\s+|\\s+$)", "" ).split( "JAVASTR " );
        for (int i = 0; i < message.length; i++) {
            if (!message[i].equals( "" ) && !message[i].equals( " " )) {
                // neu chuoi de - lo - xien k trong
                String kieuchoi = "";
                if (message[i].replaceAll( "(^\\s+|\\s+$)", "" ).length() > 2) {
                    kieuchoi = message[i].replaceAll( "(^\\s+|\\s+$)", "" ).substring( 0, 2 );
                    switch (kieuchoi.replaceAll( "(^\\s+|\\s+$)", "" )) { // kiem cha duoc kieu choi de hay lo
                        case "DQ":
                        case "db":
                        case "de":
                        case "gi":
                            boolean degiai = false;
                            if (kieuchoi.equals( "DQ" )) {
                                error += "dq ";
                                kieuchoi = "dq";
                                compareDe = compareDeDau;
                            } else if (kieuchoi.equals( "db" )) {
                                error += "db ";
                                kieuchoi = "de";
                                compareDe = compareDe2;
                            } else if (kieuchoi.equals( "de" )) {
                                error += "de ";
                                kieuchoi = "de";
                                compareDe = compareDe2;
                            } else if (kieuchoi.equals( "gi" )) {
                                kieuchoi = message[i].replaceAll( "(^\\s+|\\s+$)", "" ).substring( 0, 6 );
                                if (kieuchoi.equals( "giai1a" )) {
                                    error += "giai1a ";
                                    kieuchoi = "g1a";
                                    compareDe = compareGiai1A;
                                    degiai = true;
                                } else if (kieuchoi.equals( "giai1b" )) {
                                    error += "giai1b ";
                                    kieuchoi = "g1b";
                                    compareDe = compareGiai1B;
                                    degiai = true;
                                } else {
                                    error += "<font color=\"RED\">" + message[i] + " </font>";
                                    break;
                                }
                            } else {
                                error += "<font color=\"RED\">" + message[i] + " </font>";
                                break;
                            }
                            String chuoiDe = message[i].substring( 2, message[i].length() ).replaceAll( "(^\\s+|\\s+$)", "" ).
                                    replace( "n1c", "N1c JAVASTR" ).replace( "n", "n JAVASTR" ).replace( "k", "k JAVASTR" )
                                    .replace( "trieu", "trieu JAVASTR" );
                            if (degiai) {
                                chuoiDe = message[i].substring( 6, message[i].length() ).replaceAll( "(^\\s+|\\s+$)", "" ).
                                        replace( "n1c", "N1c JAVASTR" ).replace( "n", "n JAVASTR" ).replace( "k", "k JAVASTR" )
                                        .replace( "trieu", "trieu JAVASTR" );
                            }
                            String[] mangDe2 = chuoiDe.split( "JAVASTR" );
                            ArrayList<String> tachChuoiDe = controller.tachchuoi( mangDe2 );
                            for (int j = 0; j < tachChuoiDe.size(); j++) {
                                if (!tachChuoiDe.get( j ).equals( "" )) {
                                    if (tachChuoiDe.get( j ).indexOf( "x" ) > -1) { // kiem tra co dau x trong chuoi khong
                                        String borDeCoX = "";
                                        String[] mangDecoX = tachChuoiDe.get( j ).replaceAll( "(^\\s+|\\s+$)", "" ).split( "x" );
                                        if (mangDecoX.length == 2) {
                                            double getNum = 0; // so tien danh la getNum
                                            if (mangDecoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                if (mangDecoX[1].split( "j" ).length == 2) {
                                                    mangDecoX[1] = mangDecoX[1].replace( "j", "." );
                                                }
                                                if (mangDecoX[1].indexOf( "trieu" ) > -1) {
                                                    getNum = Double.parseDouble( mangDecoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                } else {
                                                    getNum = Double.parseDouble( mangDecoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                }
                                            }
                                            mangDecoX[0] = mangDecoX[0].replace( ".", " " );
                                            if (!mangDecoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).equals( "" )) {
                                                if (mangDecoX[0].indexOf( "bro" ) > -1) {
                                                    ArrayList<String> resDeBor = controller.xuLyDeBro( mangDecoX[0], hashmap, kieubosodan, kieuboso, limitNumber, limitNumberBaCang );
                                                    if (resDeBor.get( 1 ).length() > 1) {
                                                        xuLyDanhLeDeGhepAB( compareDe, getNum, hsde, resDeBor.get( 1 ), thuongde, idSmsInt
                                                                , dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                    }
                                                    error += resDeBor.get( 0 );
                                                } else if (mangDecoX[0].indexOf( "ghepab" ) > -1 || mangDecoX[0].indexOf( "gepab" ) > -1) {
                                                    ArrayList<String> resGhepDeab = controller.ghepab( mangDecoX[0] );
                                                    xuLyDanhLeDeGhepAB( compareDe, getNum, hsde, resGhepDeab.get( 1 ), thuongde, idSmsInt
                                                            , dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                    error += resGhepDeab.get( 0 );
                                                } else {
                                                    String valueDe = controller.converStringSms( mangDecoX[0] );
                                                    String[] valueDeArr = valueDe.split( "JAVASTR" );
                                                    String sessionDeCoX = "";
                                                    for (int k = 0; k < valueDeArr.length; k++) {
                                                        String[] valueImprotDb = valueDeArr[k].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                        String bosovtat = valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                                                        if (sessionDeCoX == "" && (bosovtat.equals( "cham" ) || bosovtat.equals( "co" ) || bosovtat.equals( "dilh" ))) {
                                                            if (valueImprotDb.length > 1) {
                                                                error += valueImprotDb[0] + " " + controller.resVtBoso( valueImprotDb, hashmap ).get( 1 );
                                                                xulydanhbosoVtDe( controller.resVtBoso( valueImprotDb, hashmap ).get( 0 ), getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                        , kieuchoi, listDonGia[0], dataSoLieuDate, valueImprotDb[0], controller.resVtBoso( valueImprotDb, hashmap ).get( 2 ), smsType );
                                                            } else {
                                                                error += "<font color=\"RED\">" + valueDeArr[k] + " </font>";
                                                            }
                                                        } else if (sessionDeCoX == "" && kieubosodan.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                        /* danh dan nhonho -toto ... */
                                                            for (int sd = 0; sd < valueImprotDb.length; sd++) {
                                                                if (kieubosodan.contains( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                                    if (sessionDeCoX.equals( "" )) {  /* de dau chan chan x 100n */
                                                                        if (!valueImprotDb[sd].equals( "" )) {
                                                                            if (hashmap.get( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ) != null) {
                                                                                error += valueImprotDb[sd] + " ";
                                                                                String value = hashmap.get( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ).get( 0 );
                                                                                borDeCoX += value + ",";
                                                                                xulydanhboDe( value, getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                                        , kieuchoi, listDonGia[0], dataSoLieuDate, valueImprotDb[0], smsType );

                                                                            } else {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                            }
                                                                        }
                                                                    } else {
                                                                        error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                    }
                                                                } else {
                                                                    error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                }
                                                            }

                                                        } else if (kieuboso.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                            if (valueDeArr.length == 2 && valueImprotDb.length == 1) {
                                                                error += "<font color=\"RED\">" + valueDeArr[k] + " </font>";
                                                            } else {
                                                                if (valueImprotDb.length > 1) {
                                                                    if ((sessionDeCoX.equals("dau") && valueImprotDb[0].equals("dit")) || (sessionDeCoX.equals("dit") || valueImprotDb[0].equals("dau")) || sessionDeCoX.equals("")) {
                                                                        error += valueImprotDb[0] + " ";
                                                                    } else {
                                                                        if (!sessionDeCoX.equals("")) {
                                                                            error += "<font color=\"RED\">" + valueImprotDb[0] + " </font>";
                                                                        } else {
                                                                            error += valueImprotDb[0] + " ";
                                                                        }
                                                                    }
                                                                    for (int q = 1; q < valueImprotDb.length; q++) {
                                                                        if (!valueImprotDb[q].equals( "" )) {
                                                                            if (hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + valueImprotDb[q] ) != null
                                                                                    && !valueImprotDb[q].equals("")
                                                                                    && valueImprotDb[q] != null
                                                                                    && !valueImprotDb[0].equals("")
                                                                                    && !sessionDeCoX.equals(valueImprotDb[0]) ) {
                                                                                String value = hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + valueImprotDb[q] ).get( 0 );
                                                                                if (sessionDeCoX != "" ) {
                                                                                    String SessionValueDeCoX = "";
                                                                                    if (hashmap.get( sessionDeCoX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                            valueImprotDb[q] ) != null) {
                                                                                        SessionValueDeCoX = hashmap.get( sessionDeCoX + valueImprotDb[q] ).get( 0 );
                                                                                        error += valueImprotDb[q] + " ";
                                                                                    } else {
                                                                                        error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                                    }
                                                                                    if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                                                        for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                                                            if (!valueImprotDb[bcx].equals( "" )) {
                                                                                                String bcpRep = valueImprotDb[q] + valueImprotDb[bcx];
                                                                                                SessionValueDeCoX = SessionValueDeCoX.replace( bcpRep + ",", "" ).replace( bcpRep, "" );
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    borDeCoX += SessionValueDeCoX + ",";
                                                                                    xuLyDanhLeDeGhep( SessionValueDeCoX, compareDe, getNum, hsde, thuongde, idSmsInt,
                                                                                            dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                                } else {
                                                                                    error += valueImprotDb[q] + " ";
                                                                                }
                                                                                borDeCoX += value + ",";
                                                                                xuLyDanhLeDeGhep( value, compareDe, getNum, hsde, thuongde, idSmsInt,
                                                                                        dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                            } else {
                                                                                if (!valueImprotDb[q].equals( "bcp" )) {
                                                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                                } else {
                                                                                    if(valueImprotDb[0].equals(sessionDeCoX)) {
                                                                                        error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                                    } else {
                                                                                        error += valueImprotDb[q] + " ";
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    sessionDeCoX = "";
                                                                } else {
                                                                    if (sessionDeCoX != "") {
                                                                        error += "<font color=\"RED\">" + valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " </font>";
                                                                    } else {
                                                                        error += valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " ";
                                                                    }
                                                                    sessionDeCoX = valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                                                                }
                                                            }
                                                        } else {
                                                            String newVal = valueImprotDb[0].replaceAll( "\\d", "" );
                                                            // kiem tra truong hop dau9 khong co dau cach
                                                            if (kieuboso.contains( newVal )) {
                                                                String[] arrNewVal = valueImprotDb[0].replace( newVal, newVal + "JAVASTR" ).
                                                                        split( "JAVASTR" );
                                                                error += arrNewVal[0];
                                                                if (!arrNewVal[1].equals( "" )) {
                                                                    if (hashmap.get( arrNewVal[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                            arrNewVal[1] ) != null) {
                                                                        error += arrNewVal[1] + " ";
                                                                    /* danh dan bo - he -tong...*/
                                                                        String value = hashmap.get( arrNewVal[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + arrNewVal[1] ).get( 0 );

                                                                        if (sessionDeCoX != "") {
                                                                            String SessionValueDeCoX1 = "";
                                                                            // xu ly cac tin nhan kieu de dau dit09 viet sat
                                                                            if (hashmap.get( sessionDeCoX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                    arrNewVal[1] ) != null ) {
                                                                                SessionValueDeCoX1 = hashmap.get( sessionDeCoX + arrNewVal[1] ).get( 0 );
                                                                            }
                                                                            if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                                                // doan xu ly chuoi 36 dau dit 09 bcp
                                                                                String bcpRepa = arrNewVal[1] + arrNewVal[1];
                                                                                SessionValueDeCoX1 = SessionValueDeCoX1.replace( bcpRepa + ",", "" ).replace( bcpRepa, "" );
                                                                                for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                                                    if (!valueImprotDb[bcx].equals( "" )) {
                                                                                        String bcpRepb = arrNewVal[1] + valueImprotDb[bcx];
                                                                                        SessionValueDeCoX1 = SessionValueDeCoX1.replace( bcpRepb + ",", "" ).replace( bcpRepb, "" );
                                                                                    }
                                                                                }
                                                                            }
                                                                            borDeCoX += SessionValueDeCoX1 + ",";
                                                                            xulydanhboDe( SessionValueDeCoX1, getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                                    , kieuchoi, listDonGia[0], dataSoLieuDate, sessionDeCoX + arrNewVal[1], smsType );
                                                                        }
                                                                        borDeCoX += value + ",";
                                                                        xulydanhboDe( value, getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                                , kieuchoi, listDonGia[0], dataSoLieuDate, arrNewVal[0] + arrNewVal[1], smsType );

                                                                        if (valueImprotDb.length == 1) {
                                                                            sessionDeCoX = "";
                                                                        }
                                                                    } else {
                                                                        error += "<font color=\"RED\">" + arrNewVal[1] + " </font>";
                                                                    }
                                                                }
                                                                if (valueImprotDb.length > 1) {
                                                                    for (int q = 1; q < valueImprotDb.length; q++) {
                                                                        if (!valueImprotDb[q].equals( "" )) {
                                                                            if (hashmap.get( arrNewVal[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                    valueImprotDb[q] ) != null) {
                                                                            /* danh dan bo - he -tong...*/
                                                                                error += valueImprotDb[q] + " ";
                                                                                String value = hashmap.get( arrNewVal[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" )
                                                                                        + valueImprotDb[q] ).get( 0 );
                                                                                if (sessionDeCoX != "") {
                                                                                    String SessionValueDeCoX2 = "";
                                                                                    if (hashmap.get( sessionDeCoX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                            valueImprotDb[q] ) != null) {
                                                                                        // xu ly cac tin nhan kieu de dau dit09 viet sat
                                                                                        SessionValueDeCoX2 = hashmap.get( sessionDeCoX + valueImprotDb[q] ).get( 0 );
                                                                                    }
                                                                                    if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                                                        // doan xu ly chuoi 36 dau dit 09 bcp
                                                                                        String bcpRep2 = valueImprotDb[q] + arrNewVal[1];
                                                                                        SessionValueDeCoX2 = SessionValueDeCoX2.replace( bcpRep2 + ",", "" ).replace( bcpRep2, "" );
                                                                                        for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                                                            if (!valueImprotDb[bcx].equals( "" )) {
                                                                                                String bcpRep1 = valueImprotDb[q] + valueImprotDb[bcx];
                                                                                                SessionValueDeCoX2 = SessionValueDeCoX2.replace( bcpRep1 + ",", "" ).replace( bcpRep1, "" );
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    borDeCoX += SessionValueDeCoX2 + ",";
                                                                                    xulydanhboDe( SessionValueDeCoX2, getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                                            , kieuchoi, listDonGia[0], dataSoLieuDate, sessionDeCoX + valueImprotDb[q], smsType );
                                                                                }
                                                                                borDeCoX += value + ",";
                                                                                xulydanhboDe( value, getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                                        , kieuchoi, listDonGia[0], dataSoLieuDate, arrNewVal[0] + valueImprotDb[q], smsType );

                                                                                if (valueImprotDb.length - 2 == q) {
                                                                                    sessionDeCoX = "";
                                                                                }
                                                                            } else {
                                                                                if (!valueImprotDb[q].equals( "bcp" )) {
                                                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                                } else {
                                                                                    error += valueImprotDb[q] + " ";
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                            } else if (valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).equals( "gepbc" )) {
                                                                // doan nay xu ly de ghep 1234 thanh 12 cap so 12-21-13-31...
                                                                error += valueImprotDb[0] + controller.resGhepBcBoso( valueImprotDb ).get( 0 ) + " ";
                                                                xuLyDanhLeDeGhep( controller.resGhepBcBoso( valueImprotDb ).get( 1 ), compareDe, getNum, hsde, thuongde, idSmsInt, dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
//
                                                            } else {
                                                                for (int q = 0; q < valueImprotDb.length; q++) {
                                                                    if (limitNumber.contains( valueImprotDb[q] )) {
                                                                        error += valueImprotDb[q] + " ";

                                                                        borDeCoX += valueImprotDb[q] + ",";
                                                                        xuLyDanhLeDe( compareDe, getNum, hsde, valueImprotDb[q], thuongde, idSmsInt
                                                                                , dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );

                                                                    } else {
                                                                        if (limitNumberBaCang.contains( valueImprotDb[q] )) {
                                                                            // doan nay xu ly cac so kieu de 565 656 555
                                                                            if (valueImprotDb[q].substring( 0, 1 ).equals( valueImprotDb[q].substring( 2, 3 ) )) {
                                                                                String vtSo1 = valueImprotDb[q].substring( 0, 2 );
                                                                                String vtSo2 = valueImprotDb[q].substring( 1, 3 );
                                                                                if  (vtSo1.equals(vtSo2)) {
                                                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                                } else {
                                                                                    error += " " + valueImprotDb[q] + " ";
                                                                                }
                                                                                // mang co x thi khong phai chia cho 2
                                                                                borDeCoX += vtSo1 + ",";
                                                                                xuLyDanhLeDe( compareDe, getNum, hsde, vtSo1, thuongde, idSmsInt
                                                                                        , dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                                borDeCoX += vtSo2 + ",";
                                                                                xuLyDanhLeDe( compareDe, getNum, hsde, vtSo2, thuongde, idSmsInt
                                                                                        , dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                            } else {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                            }
                                                                        } else {
                                                                            if (!valueImprotDb[q].equals( "" )) {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            } // end if
                                                        }
                                                    }
                                                } // end ghepab
                                                if (mangDecoX[1].indexOf( "n" ) > -1 &&
                                                        mangDecoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                    error += "x " + mangDecoX[1].replace( ".", "j" ) + " ";
                                                } else if (mangDecoX[1].indexOf( "k" ) > -1 &&
                                                        mangDecoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                    error += "x " + mangDecoX[1].replace( ".", "j" ) + " ";
                                                } else if (mangDecoX[1].indexOf( "trieu" ) > -1 &&
                                                        mangDecoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 5) {
                                                    error += "x " + mangDecoX[1].replace( ".", "j" ) + " ";
                                                //} else if (mangDecoX[1].indexOf( "N1c" ) > -1 &&
                                                //        mangDecoX[1].replaceAll( "[0-9]", "" ).replace( ".", "" ).replaceAll( "(^\\s+|\\s+$)", "" ).length() == 2) {
                                                //    error += "x " + mangDecoX[1].replace( ".", "j" ) + " ";

                                                //------------------------------------------------------------
                                                // xoa neu khong phai vuong bat buoc danh chu n
                                                // } else if (mangDecoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).
                                                //        replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 0) {
                                                //    error += "x " + mangDecoX[1].replace( ".", "j" ) + " ";
                                                // -------------------------------------------------------------
                                                } else {
                                                    error += "x " + "<font color=\"RED\">" + mangDecoX[1].replace( ".", "j" ) + " </font>";
                                                }

                                            } else {
                                                error += "<font color=\"RED\">" + tachChuoiDe.get( j ) + "</font>";
                                            }
                                        } else {
                                            error += "<font color=\"RED\">" + tachChuoiDe.get( j ) + " </font>";
                                        }
                                    } else if (tachChuoiDe.get( j ).indexOf( "=" ) > -1) {
                                        Log.d("LogFile","=========");
                                        String borDeCoDauB = "";
                                        String[] mangDecoDauB = tachChuoiDe.get( j ).replaceAll( "(^\\s+|\\s+$)", "" ).split( "=" );
                                        if (mangDecoDauB.length == 2) {
                                            double getNum = 0; // so tien danh la getNum
                                            if (mangDecoDauB[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                if (mangDecoDauB[1].split( "j" ).length == 2) {
                                                    mangDecoDauB[1] = mangDecoDauB[1].replace( "j", "." );
                                                }
                                                if (mangDecoDauB[1].indexOf( "trieu" ) > -1) {
                                                    getNum = Double.parseDouble( mangDecoDauB[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                } else {
                                                    getNum = Double.parseDouble( mangDecoDauB[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                }
                                            }
                                            if (!mangDecoDauB[0].replaceAll( "(^\\s+|\\s+$)", "" ).equals( "" )) {
                                                String valueDeCoDauB = controller.converStringSms( mangDecoDauB[0] );
                                                String[] valueDeArrCoDauB = valueDeCoDauB.split( "JAVASTR" );
                                                String sessionDeCoDauB = "";
                                                for (int k = 0; k < valueDeArrCoDauB.length; k++) {
                                                    String[] valueImprotDb = valueDeArrCoDauB[k].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                    if (kieubosodan.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                        /* danh dan nhonho -toto ... */
                                                        for (int sd = 0; sd < valueImprotDb.length; sd++) {
                                                            if (kieubosodan.contains( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                                if (sessionDeCoDauB.equals( "" )) { /* de dau chan chan x 100n */
                                                                    if (hashmap.get( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ) != null) {
                                                                        error += valueImprotDb[sd] + " ";
                                                                        String value = hashmap.get( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ).get( 0 );

                                                                        borDeCoDauB += value + ",";

                                                                    } else {
                                                                        error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                    }
                                                                } else {
                                                                    error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                }
                                                            } else {
                                                                error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                            }
                                                        }
                                                    } else if (kieuboso.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                        if (valueImprotDb.length > 1) {
                                                            error += valueImprotDb[0] + " ";
                                                            for (int q = 1; q < valueImprotDb.length; q++) {
                                                                if (!valueImprotDb[q].equals( "" )) {
                                                                    if (hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                            valueImprotDb[q] ) != null) {
                                                                        /*... danh dan bo - he -tong...*/
                                                                        error += valueImprotDb[q] + " ";
                                                                        String value = hashmap.get( valueImprotDb[0] + valueImprotDb[q] ).get( 0 );

                                                                        if (sessionDeCoDauB != "") {
                                                                            if (hashmap.get( sessionDeCoDauB.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                    valueImprotDb[q] ) != null) {
                                                                                String SessionValueDeCoDauB = hashmap.get( sessionDeCoDauB + valueImprotDb[q] ).get( 0 );
                                                                                if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                                                    // doan xu ly chuoi 36 dau dit 09 bcp
                                                                                    for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                                                        if (!valueImprotDb[bcx].equals( "" )) {
                                                                                            String bcpRep = valueImprotDb[q] + valueImprotDb[bcx];
                                                                                            SessionValueDeCoDauB = SessionValueDeCoDauB.replace( bcpRep + ",", "" ).replace( bcpRep, "" );
                                                                                        }
                                                                                    }
                                                                                }
                                                                                borDeCoDauB += SessionValueDeCoDauB + ",";
                                                                            }
                                                                        }
                                                                        borDeCoDauB += value + ",";

                                                                    } else {
                                                                        if (!valueImprotDb[q].equals( "bcp" )) {
                                                                            error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                        } else {
                                                                            error += valueImprotDb[q] + " ";
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            sessionDeCoDauB = "";
                                                        } else {
                                                            if (sessionDeCoDauB != "") {
                                                                error += "<font color=\"RED\">" + valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " </font>";
                                                            } else {
                                                                error += valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " ";
                                                            }
                                                            sessionDeCoDauB = valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                                                        }
                                                    } else {
                                                        String newVal = valueImprotDb[0].replaceAll( "\\d", "" );
                                                        // kiem tra truong hop dau9 khong co dau cach
                                                        if (kieuboso.contains( newVal )) {
                                                            String[] arrNewVal = valueImprotDb[0].replace( newVal, newVal + "JAVASTR" ).
                                                                    split( "JAVASTR" );
                                                            error += arrNewVal[0];
                                                            if (!arrNewVal[1].equals( "" )) {
                                                                if (hashmap.get( arrNewVal[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                        arrNewVal[1] ) != null) {
                                                                    error += arrNewVal[1] + " ";
                                                                        /* danh dan bo - he -tong...*/
                                                                    String value = hashmap.get( arrNewVal[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + arrNewVal[1] ).get( 0 );

                                                                    if (sessionDeCoDauB != "") {
                                                                        if (hashmap.get( sessionDeCoDauB.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                arrNewVal[1] ) != null) {
                                                                            // xu ly cac tin nhan kieu de dau dit09 viet sat
                                                                            String SessionValueDeCoX1 = hashmap.get( sessionDeCoDauB + arrNewVal[1] ).get( 0 );
                                                                            if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                                                // doan xu ly chuoi 36 dau dit 09 bcp
                                                                                String bcpRepa = arrNewVal[1] + arrNewVal[1];
                                                                                SessionValueDeCoX1 = SessionValueDeCoX1.replace( bcpRepa + ",", "" ).replace( bcpRepa, "" );
                                                                                for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                                                    if (!valueImprotDb[bcx].equals( "" )) {
                                                                                        String bcpRepb = arrNewVal[1] + valueImprotDb[bcx];
                                                                                        SessionValueDeCoX1 = SessionValueDeCoX1.replace( bcpRepb + ",", "" ).replace( bcpRepb, "" );
                                                                                    }
                                                                                }
                                                                            }
                                                                            borDeCoDauB += SessionValueDeCoX1 + ",";
                                                                        }
                                                                    }
                                                                    borDeCoDauB += value + ",";

                                                                    if (valueImprotDb.length == 1) {
                                                                        sessionDeCoDauB = "";
                                                                    }
                                                                } else {
                                                                    error += "<font color=\"RED\">" + arrNewVal[1] + " </font>";
                                                                }
                                                            }
                                                            for (int q = 1; q < valueImprotDb.length; q++) {
                                                                if (!valueImprotDb[q].equals( "" )) {
                                                                    if (hashmap.get( arrNewVal[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                            valueImprotDb[q] ) != null) {
                                                                        /* danh dan bo - he -tong...*/
                                                                        error += valueImprotDb[q] + " ";
                                                                        String value = hashmap.get( arrNewVal[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" )
                                                                                + valueImprotDb[q] ).get( 0 );

                                                                        if (sessionDeCoDauB != "") {
                                                                            if (hashmap.get( sessionDeCoDauB.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                    valueImprotDb[q] ) != null) {
                                                                                // xu ly cac tin nhan kieu de dau dit09 viet sat
                                                                                String SessionValueDeCoX2 = hashmap.get( sessionDeCoDauB + valueImprotDb[q] ).get( 0 );
                                                                                if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                                                    // doan xu ly chuoi 36 dau dit 09 bcp
                                                                                    String bcpRep2 = valueImprotDb[q] + arrNewVal[1];
                                                                                    SessionValueDeCoX2 = SessionValueDeCoX2.replace( bcpRep2 + ",", "" ).replace( bcpRep2, "" );
                                                                                    for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                                                        String bcpRep1 = valueImprotDb[q] + valueImprotDb[bcx];
                                                                                        SessionValueDeCoX2 = SessionValueDeCoX2.replace( bcpRep1 + ",", "" ).replace( bcpRep1, "" );
                                                                                    }
                                                                                }
                                                                                borDeCoDauB += SessionValueDeCoX2 + ",";
                                                                            }
                                                                        }
                                                                        borDeCoDauB += value + ",";

                                                                        sessionDeCoDauB = "";
                                                                    } else {
                                                                        if (!valueImprotDb[q].equals( "bcp" )) {
                                                                            error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                        } else {
                                                                            error += valueImprotDb[q] + " ";
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else if (valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).equals( "gepbc" )) {
                                                            // doan nay xu ly de ghep 1234 thanh 12 cap so 12-21-13-31...
                                                            error += valueImprotDb[0] + controller.resGhepBcBoso( valueImprotDb ).get( 0 ) + " ";
                                                            xuLyDanhLeDeGhep( controller.resGhepBcBoso( valueImprotDb ).get( 1 ), compareDe, getNum, hsde, thuongde, idSmsInt, dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
//
                                                        } else {
                                                            if (valueImprotDb.length > 1) {
                                                                for (int q = 0; q < valueImprotDb.length; q++) {
                                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                }
                                                            } else {
                                                                for (int q = 0; q < valueImprotDb.length; q++) {
                                                                    if (limitNumber.contains( valueImprotDb[q] )) {
                                                                        error += valueImprotDb[q] + " ";

                                                                        borDeCoDauB += valueImprotDb[q] + ",";

                                                                    } else {
                                                                        if (limitNumberBaCang.contains( valueImprotDb[q] )) {
                                                                            // doan nay xu ly cac so kieu de 565 656 555
                                                                            if (valueImprotDb[q].substring( 0, 1 ).equals( valueImprotDb[q].substring( 2, 3 ) )) {
                                                                                String vtSo1 = valueImprotDb[q].substring( 0, 2 );
                                                                                String vtSo2 = valueImprotDb[q].substring( 1, 3 );
                                                                                if  (vtSo1.equals(vtSo2)) {
                                                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                                } else {
                                                                                    error += " " + valueImprotDb[q] + " ";
                                                                                }
                                                                                // mang co x thi khong phai chia cho 2
                                                                                borDeCoDauB += vtSo1 + ",";
                                                                                borDeCoDauB += vtSo2 + ",";
                                                                            } else {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                            }
                                                                        } else {
                                                                            if (!valueImprotDb[q].equals( "" )) {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if (message[i].replaceAll( "(^\\s+|\\s+$)", "" ).length() > 6) {
                                                    String[] arrDeCoDauB = borDeCoDauB.split( "," );
                                                    if (arrDeCoDauB.length > 0) {
                                                        if (arrDeCoDauB.length > 0) {
                                                            double newGetNum = Math.round( getNum / arrDeCoDauB.length * 100.0 ) / 100.0;
                                                            for (int b = 0; b < arrDeCoDauB.length; b++) {
                                                                xuLyDanhLeDe( compareDe, newGetNum, hsde, arrDeCoDauB[b], thuongde, idSmsInt
                                                                        , dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                            }
                                                        }
                                                    }
                                                }
                                                if (valueDeArrCoDauB.length > 1) {
                                                    error += "= " + "<font color=\"RED\">" + mangDecoDauB[1] + " </font>";
                                                } else if (mangDecoDauB[1].indexOf( "n" ) > -1 &&
                                                        mangDecoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                    error += "= " + mangDecoDauB[1].replace( ".", "j" ) + " ";
                                                } else if (mangDecoDauB[1].indexOf( "k" ) > -1 &&
                                                        mangDecoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                    error += "= " + mangDecoDauB[1].replace( ".", "j" ) + " ";
                                                } else if (mangDecoDauB[1].indexOf( "trieu" ) > -1 &&
                                                        mangDecoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 5) {
                                                    error += "= " + mangDecoDauB[1].replace( ".", "j" ) + " ";
                                                //} else if (mangDecoDauB[1].indexOf( "N1c" ) > -1 &&
                                                //        mangDecoDauB[1].replaceAll( "[0-9]", "" ).replace( ".", "" ).replaceAll( "(^\\s+|\\s+$)", "" ).length() == 2) {
                                                //    error += "= " + mangDecoDauB[1].replace( ".", "j" ) + " ";
                                                //----------------------------------------------------------------
                                                // neu khong phai la anh vuong
                                                // } else if (mangDecoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).
                                                //        replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 0) {
                                                //   error += "= " + mangDecoDauB[1].replace( ".", "j" ) + " ";
                                                //-----------------------------------------------------------------
                                                } else {
                                                    error += "= " + "<font color=\"RED\">" + mangDecoDauB[1] + " </font>";
                                                }
                                            } else {
                                                error += "<font color=\"RED\">" + tachChuoiDe.get( j ) + "</font>";
                                            }
                                        } else {
                                            error += "<font color=\"RED\">" + tachChuoiDe.get( j ) + " </font>";
                                        }

                                    } else if (tachChuoiDe.get( j ).indexOf( "khongcodau" ) > -1) { //xoa doan if else khong de gia tri
                                        String[] mangDekhongX = tachChuoiDe.get( j ).replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                        if (mangDekhongX.length == 2 && mangDekhongX[0].replaceAll( "[0-9]", "" ).length() == 0) {
                                            String valueMangDe = String.valueOf( mangDekhongX[mangDekhongX.length - 1] );
                                            double getNum = 0; // gia tri loto
                                            if (valueMangDe.replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                if (valueMangDe.split( "j" ).length == 2) {
                                                    valueMangDe = valueMangDe.replace( "j", "." );
                                                }
                                                if (valueMangDe.indexOf( "trieu" ) > -1) {
                                                    getNum = Double.parseDouble( valueMangDe.replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                } else {
                                                    getNum = Double.parseDouble( valueMangDe.replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                }
                                            }
                                            String boDeKX = "";
                                            int endSubString = tachChuoiDe.get( j ).length() - valueMangDe.length() - 1;
                                            if (endSubString > 0) {
                                                String valueDe = controller.converStringSms( tachChuoiDe.get( j ).substring( 0, endSubString ) ).replaceAll( "(^\\s+|\\s+$)", "" ).replace( ".", " " );
                                                String[] valueDeArr = valueDe.split( "JAVASTR" );
                                                String SessionDeKX = "";
                                                for (int k = 0; k < valueDeArr.length; k++) {
                                                    String[] valueImprotDb = valueDeArr[k].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                    if (kieubosodan.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                        /* danh dan nhonho -toto ... */
                                                        for (int sd = 0; sd < valueImprotDb.length; sd++) {
                                                            if (kieubosodan.contains( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                                if (SessionDeKX.equals( "" )) { // de dau chan chan x 100n
                                                                    if (hashmap.get( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ) != null) {
                                                                        error += valueImprotDb[sd] + " ";
                                                                        String value = hashmap.get( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ).get( 0 );

                                                                        boDeKX += value + ",";
                                                                        xulydanhboDe( value, getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                                , kieuchoi, listDonGia[0], dataSoLieuDate, valueImprotDb[0], smsType );

                                                                    } else {
                                                                        error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                    }
                                                                } else {
                                                                    error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                }
                                                            } else {
                                                                error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                            }
                                                        }
                                                    } else if (kieuboso.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                        if (valueImprotDb.length > 1) {
                                                            error += valueImprotDb[0] + " ";
                                                            for (int q = 1; q < valueImprotDb.length; q++) {
                                                                if (!valueImprotDb[q].equals( "" )) {
                                                                    if (hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                            valueImprotDb[q] ) != null) {
                                                                    /* danh dan bo - he -tong...*/
                                                                        error += valueImprotDb[q] + " ";
                                                                        String value = hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" )
                                                                                + valueImprotDb[q] ).get( 0 );

                                                                        if (SessionDeKX != "") {
                                                                            if (hashmap.get( SessionDeKX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                    valueImprotDb[q] ) != null) {
                                                                                String valueSessionDeKX = hashmap.get( SessionDeKX + valueImprotDb[q] ).get( 0 );
                                                                                boDeKX += valueSessionDeKX + ",";
                                                                                xulydanhboDe( valueSessionDeKX, getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                                        , kieuchoi, listDonGia[0], dataSoLieuDate, SessionDeKX + valueImprotDb[q], smsType );
                                                                            }
                                                                        }
                                                                        boDeKX += value + ",";
                                                                        xulydanhboDe( value, getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                                , kieuchoi, listDonGia[0], dataSoLieuDate, valueImprotDb[0] + valueImprotDb[q], smsType );

                                                                    } else {
                                                                        error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            if (SessionDeKX != "") {
                                                                error += "<font color=\"RED\">" + valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " </font>";
                                                            } else {
                                                                error += valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " ";
                                                            }
                                                            SessionDeKX = valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                                                        }
                                                    } else {
                                                        String newVal2 = valueImprotDb[0].replaceAll( "\\d", "" );
                                                        // xu ly cac ki tu dau9 viet lien
                                                        if (kieuboso.contains( newVal2 )) {
                                                            String[] arrNewVal2 = valueImprotDb[0].replace( newVal2, newVal2 + "JAVASTR" ).
                                                                    split( "JAVASTR" );
                                                            error += arrNewVal2[0];
                                                            if (!arrNewVal2[1].equals( "" )) {
                                                                if (hashmap.get( arrNewVal2[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                        arrNewVal2[1] ) != null) {
                                                                    error += arrNewVal2[1] + " ";
                                                                        /* danh dan bo - he -tong...*/
                                                                    String value = hashmap.get( arrNewVal2[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + arrNewVal2[1] ).get( 0 );
                                                                    if (SessionDeKX != "") {
                                                                        if (hashmap.get( SessionDeKX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                arrNewVal2[1] ) != null) {
                                                                            String valueSessionDeKX = hashmap.get( SessionDeKX + arrNewVal2[1] ).get( 0 );
                                                                            boDeKX += valueSessionDeKX + ",";
                                                                            xulydanhboDe( valueSessionDeKX, getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                                    , kieuchoi, listDonGia[0], dataSoLieuDate, SessionDeKX + arrNewVal2[1], smsType );
                                                                        }
                                                                    }
                                                                    boDeKX += value + ",";
                                                                    xulydanhboDe( value, getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                            , kieuchoi, listDonGia[0], dataSoLieuDate, arrNewVal2[0] + arrNewVal2[1], smsType );

                                                                } else {
                                                                    error += "<font color=\"RED\">" + arrNewVal2[1] + " </font>";
                                                                }
                                                            }
                                                            for (int q = 1; q < valueImprotDb.length; q++) {
                                                                if (!valueImprotDb[q].equals( "" )) {
                                                                    if (hashmap.get( arrNewVal2[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                            valueImprotDb[q] ) != null) {
                                                                    /* danh dan bo - he -tong...*/
                                                                        error += valueImprotDb[q] + " ";
                                                                        String value = hashmap.get( arrNewVal2[0] + valueImprotDb[q] ).get( 0 );
                                                                        if (SessionDeKX != "") {
                                                                            if (hashmap.get( SessionDeKX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                    valueImprotDb[q] ) != null) {
                                                                                String valueSessionDeKX = hashmap.get( SessionDeKX + valueImprotDb[q] ).get( 0 );
                                                                                boDeKX += valueSessionDeKX + ",";
                                                                                xulydanhboDe( valueSessionDeKX, getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                                        , kieuchoi, listDonGia[0], dataSoLieuDate, SessionDeKX + valueImprotDb[q], smsType );
                                                                            }
                                                                        }
                                                                        boDeKX += value + ",";
                                                                        xulydanhboDe( value, getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, listDonGia[1]
                                                                                , kieuchoi, listDonGia[0], dataSoLieuDate, arrNewVal2[0] + valueImprotDb[q], smsType );

                                                                    } else {
                                                                        error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                    }
                                                                }
                                                            }
                                                            SessionDeKX = "";
                                                        } else if (valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).equals( "gepbc" )) {
                                                            // doan nay xu ly de ghep 1234 thanh 12 cap so 12-21-13-31...
                                                            error += valueImprotDb[0] + controller.resGhepBcBoso( valueImprotDb ).get( 0 ) + " ";
                                                            xuLyDanhLeDeGhep( controller.resGhepBcBoso( valueImprotDb ).get( 1 ), compareDe, getNum, hsde, thuongde, idSmsInt, dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
//
                                                        } else {
                                                            for (int q = 0; q < valueImprotDb.length; q++) {
                                                                if (limitNumber.contains( valueImprotDb[q] )) {
                                                                    error += valueImprotDb[q] + " ";

                                                                    boDeKX += valueImprotDb[q] + ",";
                                                                    xuLyDanhLeDe( compareDe, getNum, hsde, valueImprotDb[q], thuongde, idSmsInt
                                                                            , dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );

                                                                } else {
                                                                    if (limitNumberBaCang.contains( valueImprotDb[q] )) {
                                                                        // doan nay xu ly cac so kieu de 565 656 555
                                                                        if (valueImprotDb[q].substring( 0, 1 ).equals( valueImprotDb[q].substring( 2, 3 ) )) {
                                                                            String vtSo1 = valueImprotDb[q].substring( 0, 2 );
                                                                            String vtSo2 = valueImprotDb[q].substring( 1, 3 );
                                                                            if  (vtSo1.equals(vtSo2)) {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                            } else {
                                                                                error += " " + valueImprotDb[q] + " ";
                                                                            }
                                                                            boDeKX += vtSo1 + ",";
                                                                            xuLyDanhLeDe( compareDe, getNum, hsde, vtSo1, thuongde, idSmsInt
                                                                                    , dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                            boDeKX += vtSo2 + ",";
                                                                            xuLyDanhLeDe( compareDe, getNum, hsde, vtSo2, thuongde, idSmsInt
                                                                                    , dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                        } else {
                                                                            error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                        }
                                                                    } else {
                                                                        if (!valueImprotDb[q].equals( "" )) {
                                                                            error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } // endfor

                                                String[] checkGiatriMangDeCoX = valueMangDe.replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                if (checkGiatriMangDeCoX.length > 1) {
                                                    error += "<font color=\"RED\">" + valueMangDe + " </font>";
                                                    for (int val = 1; val < checkGiatriMangDeCoX.length; val++) {
                                                        error += checkGiatriMangDeCoX[val] + " ";
                                                    }
                                                } else if (valueDe.length() == 0) {
                                                    error += "<font color=\"RED\">" + valueMangDe + " </font>";
                                                } else {
                                                    if (valueMangDe.indexOf( "n" ) > -1 &&
                                                            valueMangDe.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                            valueMangDe.replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1
                                                            ) {
                                                        error += valueMangDe.replace( ".", "j" ) + " ";
                                                    } else if (valueMangDe.indexOf( "k" ) > -1 &&
                                                            valueMangDe.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                            valueMangDe.replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                        error += valueMangDe.replace( ".", "j" ) + " ";
                                                    } else if (valueMangDe.indexOf( "trieu" ) > -1 &&
                                                            valueMangDe.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 5 &&
                                                            valueMangDe.replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                        error += valueMangDe.replace( ".", "j" ) + " ";
                                                    //} else if (valueMangDe.indexOf( "N1c" ) > -1 &&
                                                    //        valueMangDe.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 2 &&
                                                    //        valueMangDe.replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 2) {
                                                    //    error += valueMangDe.replace( ".", "j" ) + " ";

                                                    //-----------------------------------------------------
                                                    // neu khong phai la anh vuong
                                                    // } else if (valueMangDe.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 0 &&
                                                    //        valueMangDe.replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                    //    error += valueMangDe.replace( ".", "j" ) + " ";
                                                    //--------------------------------------------------------

                                                    } else {
                                                        error += "<font color=\"RED\">" + valueMangDe.replace( ".", "j" ) + " </font>";
                                                    }
                                                }
                                            } else {
                                                error += "<font color=\"RED\">" + tachChuoiDe.get( j ) + " </font>";
                                            }
                                        } else {
                                            error += "<font color=\"RED\">" + tachChuoiDe.get( j ) + " </font>";
                                        }
                                    } else {
                                        error += "<font color=\"RED\">" + tachChuoiDe.get( j ) + " </font>";
                                    }
                                }
                            }
                            break;
                        case "lD":
                        case "lo":
                            if (kieuchoi.equals( "lD" )) {
                                error += "ld ";
                                compareLo = compareLoDau;
                                kieuchoi = "ld";
                            } else {
                                error += "lo ";
                                compareLo = compareLo2;
                                kieuchoi = "lo";
                            }
                            String chuoiLo = message[i].substring( 2, message[i].length() ).replaceAll( "(^\\s+|\\s+$)", "" )
                                    .replace( " d", "d" ).replace( "D1c", "D1c JAVASTR" ).replace( "d", "d JAVASTR" );
                            String[] mangLo2 = chuoiLo.split( "JAVASTR" );
                            ArrayList<String> tachChuoiLo = controller.tachchuoi( mangLo2 );
                            for (int j = 0; j < tachChuoiLo.size(); j++) {
                                if (!tachChuoiLo.get( j ).equals( "" )) {
                                    if (tachChuoiLo.get( j ).indexOf( "x" ) > -1) { // kiem tra co dau x trong chuoi khong
                                        String[] mangLocoX = tachChuoiLo.get( j ).replaceAll( "(^\\s+|\\s+$)", "" ).split( "x" );
                                        if (mangLocoX.length == 2) {
                                            String boLoCoX = "";
                                            double getNum = 0; // so tien danh la getNum
                                            if (mangLocoX[1].replace( "D1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                if (mangLocoX[1].split( "j" ).length == 2) {
                                                    mangLocoX[1] = mangLocoX[1].replace( "j", "." );
                                                }
                                                getNum = Double.parseDouble( mangLocoX[1].replace( "D1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                            }
                                            mangLocoX[0] = mangLocoX[0].replace( ".", " " );
                                            if (!mangLocoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).equals( "" )) {
                                                if (mangLocoX[0].indexOf( "bro" ) > -1) {
                                                    ArrayList<String> resLoBor = controller.xuLyLoBro( mangLocoX[0], hashmap, kieubosodan, kieuboso, limitNumber, limitNumberBaCang );
                                                    if (resLoBor.get( 1 ).length() > 1) {
                                                        xulydanhleLoToGhepAB( getNum, hslo, compareLo, resLoBor.get( 1 )
                                                                , thuonglo, idSmsInt, dongiaId, listDonGia[1],
                                                                kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                    }
                                                    error += resLoBor.get( 0 );
                                                } else if (mangLocoX[0].indexOf( "ghepab" ) > -1 || mangLocoX[0].indexOf( "gepab" ) > -1) {
                                                    ArrayList<String> resGhepLoab = controller.ghepab( mangLocoX[0] );
                                                    xulydanhleLoToGhepAB( getNum, hslo, compareLo, resGhepLoab.get( 1 )
                                                            , thuonglo, idSmsInt, dongiaId, listDonGia[1],
                                                            kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                    error += resGhepLoab.get( 0 );
                                                } else {
                                                    String valueLoCoX = controller.converStringSms( mangLocoX[0] );
                                                    String[] valueLoArrCoX = valueLoCoX.split( "JAVASTR" );
                                                    String SessionLoCoX = "";
                                                    for (int k = 0; k < valueLoArrCoX.length; k++) {
                                                        String[] valueImprotDb = valueLoArrCoX[k].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                        String bosovtat = valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                                                        if (SessionLoCoX == "" && (bosovtat.equals( "cham" ) || bosovtat.equals( "co" ) || bosovtat.equals( "dilh" ))) {
                                                            if (valueImprotDb.length > 1) {
                                                                error += valueImprotDb[0] + " " + controller.resVtBoso( valueImprotDb, hashmap ).get( 1 );
                                                                xulydanhbosoVtLo( controller.resVtBoso( valueImprotDb, hashmap ).get( 0 ), compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId,
                                                                        valueImprotDb[0], controller.resVtBoso( valueImprotDb, hashmap ).get( 2 ),
                                                                        listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                            } else {
                                                                error += "<font color=\"RED\">" + valueLoArrCoX[k] + " </font>";
                                                            }
                                                        } else if (SessionLoCoX == "" && (kieubosodan.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ) ) ) {
                                                        /* danh dan nhonho -toto ... */
                                                            for (int sd = 0; sd < valueImprotDb.length; sd++) {
                                                                if (kieubosodan.contains( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                                    if (SessionLoCoX.equals( "" )) {
                                                                        if (hashmap.get( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ) != null) {
                                                                            error += valueImprotDb[sd] + " ";
                                                                            String value = hashmap.get( valueImprotDb[sd] ).get( 0 );
                                                                            boLoCoX += value + ",";
                                                                            xulydanhboLoTo( value, compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId, valueImprotDb[0],
                                                                                    listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                        } else {
                                                                            error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                        }
                                                                    } else {
                                                                        error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                    }
                                                                } else {
                                                                    error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                }
                                                            }
                                                        } else if (kieuboso.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                            if (valueLoArrCoX.length == 2 && valueImprotDb.length == 1) {
                                                                error += "<font color=\"RED\">" + valueLoArrCoX[k] + " </font>";
                                                            } else {
                                                                if (valueImprotDb.length > 1) {
                                                                    if ((SessionLoCoX.equals("dau") && valueImprotDb[0].equals("dit")) || (SessionLoCoX.equals("dit") || valueImprotDb[0].equals("dau")) || SessionLoCoX.equals("")) {
                                                                        error += valueImprotDb[0] + " ";
                                                                    } else {
                                                                        if (!SessionLoCoX.equals("")) {
                                                                            error += "<font color=\"RED\">" + valueImprotDb[0] + " </font>";
                                                                        } else {
                                                                            error += valueImprotDb[0] + " ";
                                                                        }
                                                                    }
                                                                    for (int q = 1; q < valueImprotDb.length; q++) {
                                                                        if (!valueImprotDb[q].equals( "" )) {
                                                                            if (hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                    valueImprotDb[q] ) != null
                                                                                    && valueImprotDb[q] != ""
                                                                                    && ( !SessionLoCoX.equals(valueImprotDb[0]) || SessionLoCoX.equals("") ) ) {
                                                                                /* danh dan bo - he -tong...*/
                                                                                String value = hashmap.get( valueImprotDb[0] + valueImprotDb[q] ).get( 0 );

                                                                                if (SessionLoCoX != "") {
                                                                                    String valueSessinoLoCoX = "";
                                                                                    if (hashmap.get( SessionLoCoX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                            valueImprotDb[q] ) != null) {
                                                                                        valueSessinoLoCoX = hashmap.get( SessionLoCoX + valueImprotDb[q] ).get( 0 );
                                                                                        error += valueImprotDb[q];
                                                                                    } else {
                                                                                        error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                                    }
                                                                                    if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                                                        // doan xu ly chuoi 36 dau dit 09 bcp
                                                                                        for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                                                            if (!valueImprotDb[bcx].equals( "" )) {
                                                                                                String bcpRep = valueImprotDb[q] + valueImprotDb[bcx];
                                                                                                valueSessinoLoCoX = valueSessinoLoCoX.replace( bcpRep + ",", "" ).replace( bcpRep, "" );
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    boLoCoX += valueSessinoLoCoX + ",";
                                                                                    xulydanhleLoToGhep( valueSessinoLoCoX, getNum, hslo, compareLo, thuonglo,
                                                                                            idSmsInt, dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                                } else {
                                                                                    error += valueImprotDb[q] + " ";
                                                                                }
                                                                                boLoCoX += value + ",";
                                                                                xulydanhleLoToGhep( value, getNum, hslo, compareLo, thuonglo,
                                                                                        idSmsInt, dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );

                                                                            } else {
                                                                                if (!valueImprotDb[q].equals( "bcp" )) {
                                                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                                } else {
                                                                                    error += valueImprotDb[q] + " ";
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                    SessionLoCoX = "";
                                                                } else {
                                                                    if (SessionLoCoX != "") {
                                                                        error += "<font color=\"RED\">" + valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " </font>";
                                                                    } else {
                                                                        error += valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " ";
                                                                    }
                                                                    SessionLoCoX = valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                                                                }
                                                            }

                                                        } else {
                                                            String newValLoCoX = valueImprotDb[0].replaceAll( "\\d", "" );
                                                            // xu ly cac ki tu dau9 viet lien
                                                            if (kieuboso.contains( newValLoCoX )) {
                                                                String[] arrNewValLoCoX = valueImprotDb[0].replace( newValLoCoX, newValLoCoX + "JAVASTR" ).
                                                                        split( "JAVASTR" );
                                                                error += arrNewValLoCoX[0] + " ";
                                                                if (!arrNewValLoCoX[1].equals( "" )) {
                                                                    if (hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                            arrNewValLoCoX[1] ) != null) {
                                                                    /* danh dan bo - he -tong...*/
                                                                        error += arrNewValLoCoX[1] + " ";
                                                                        String value = hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + arrNewValLoCoX[1] ).get( 0 );
                                                                        if (!SessionLoCoX.equals( "" )) {
                                                                            String valueSessinoLoCoX1 = "";
                                                                            if (hashmap.get( SessionLoCoX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                    arrNewValLoCoX[1] ) != null) {
                                                                                valueSessinoLoCoX1 = hashmap.get( SessionLoCoX + arrNewValLoCoX[1] ).get( 0 );
                                                                            }
                                                                            if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                                                // doan xu ly chuoi 36 dau dit 09 bcp
                                                                                if (valueImprotDb.length == 4) {
                                                                                    String bcpRepa = arrNewValLoCoX[1] + arrNewValLoCoX[1];
                                                                                    valueSessinoLoCoX1 = valueSessinoLoCoX1.replace( bcpRepa + ",", "" ).replace( bcpRepa, "" );
                                                                                    for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                                                        if (!valueImprotDb[bcx].equals( "" )) {
                                                                                            String bcpRepb = arrNewValLoCoX[1] + valueImprotDb[bcx];
                                                                                            valueSessinoLoCoX1 = valueSessinoLoCoX1.replace( bcpRepb + ",", "" ).replace( bcpRepb, "" );
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                            boLoCoX += valueSessinoLoCoX1 + ",";
                                                                            xulydanhboLoTo( valueSessinoLoCoX1, compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId,
                                                                                    SessionLoCoX + arrNewValLoCoX[1],
                                                                                    listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                        }
                                                                        boLoCoX += value + ",";
                                                                        xulydanhboLoTo( value, compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId,
                                                                                arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + arrNewValLoCoX[1],
                                                                                listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                        if (valueImprotDb.length == 1) {
                                                                            SessionLoCoX = "";
                                                                        }
                                                                    } else {
                                                                        error += "<font color=\"RED\">" + arrNewValLoCoX[1] + " </font>";
                                                                    }
                                                                }
                                                                for (int q = 1; q < valueImprotDb.length; q++) {
                                                                    if (!valueImprotDb[q].equals( "" )) {
                                                                        if (hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                valueImprotDb[q] ) != null) {
                                                                        /* danh dan bo - he -tong...*/
                                                                            error += valueImprotDb[q] + " ";
                                                                            String value = hashmap.get( arrNewValLoCoX[0] + valueImprotDb[q] ).get( 0 );

                                                                            if (SessionLoCoX != "") {
                                                                                String valueSessinoLoCoX2 = "";
                                                                                if (hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                        valueImprotDb[q] ) != null) {
                                                                                    valueSessinoLoCoX2 = hashmap.get( SessionLoCoX + valueImprotDb[q] ).get( 0 );
                                                                                }
                                                                                if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                                                    // doan xu ly chuoi 36 dau dit 09 bcp
                                                                                    String bcpRep2 = valueImprotDb[q] + arrNewValLoCoX[1];
                                                                                    valueSessinoLoCoX2 = valueSessinoLoCoX2.replace( bcpRep2 + ",", "" ).replace( bcpRep2, "" );
                                                                                    for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                                                        if (!valueImprotDb[bcx].equals( "" )) {
                                                                                            String bcpRep1 = valueImprotDb[q] + valueImprotDb[bcx];
                                                                                            valueSessinoLoCoX2 = valueSessinoLoCoX2.replace( bcpRep1 + ",", "" ).replace( bcpRep1, "" );
                                                                                        }
                                                                                    }
                                                                                }
                                                                                boLoCoX += valueSessinoLoCoX2 + ",";
                                                                                xulydanhboLoTo( valueSessinoLoCoX2, compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId,
                                                                                        SessionLoCoX + valueImprotDb[q],
                                                                                        listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                            }
                                                                            boLoCoX += value + ",";
                                                                            xulydanhboLoTo( value, compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId,
                                                                                    arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + valueImprotDb[q],
                                                                                    listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );

                                                                        } else {
                                                                            if (!valueImprotDb[q].equals( "bcp" )) {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                            } else {
                                                                                error += valueImprotDb[q] + " ";
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                SessionLoCoX = "";
                                                            } else if (valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).equals( "gepbc" )) {
                                                                // doan nay xu ly Lo ghep 1234 thanh 12 cap so 12-21-13-31...
                                                                error += valueImprotDb[0] + controller.resGhepBcBoso( valueImprotDb ).get( 0 ) + " ";
                                                                xulydanhleLoToGhep( controller.resGhepBcBoso( valueImprotDb ).get( 1 ), getNum, hslo, compareLo, thuonglo,
                                                                        idSmsInt, dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
//
                                                            } else {
                                                                for (int q = 0; q < valueImprotDb.length; q++) {
                                                                    if (limitNumber.contains( valueImprotDb[q] )) {
                                                                        error += valueImprotDb[q] + " ";

                                                                        boLoCoX += valueImprotDb[q] + ",";
                                                                        xulydanhleLoTo( getNum, hslo, compareLo, valueImprotDb[q]
                                                                                , thuonglo, idSmsInt, dongiaId, listDonGia[1],
                                                                                kieuchoi, listDonGia[0], dataSoLieuDate, smsType );

                                                                    } else {
                                                                        if (limitNumberBaCang.contains( valueImprotDb[q] )) {
                                                                            // doan nay xu ly cac so kieu de 565 656 555
                                                                            if (valueImprotDb[q].substring( 0, 1 ).equals( valueImprotDb[q].substring( 2, 3 ) )) {
                                                                                String vtSo1 = valueImprotDb[q].substring( 0, 2 );
                                                                                String vtSo2 = valueImprotDb[q].substring( 1, 3 );
                                                                                if  (vtSo1.equals(vtSo2)) {
                                                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                                } else {
                                                                                    error += " " + valueImprotDb[q] + " ";
                                                                                }
                                                                                // lo co x thi khong phai chia cho 2
                                                                                boLoCoX += vtSo1 + ",";
                                                                                boLoCoX += vtSo2 + ",";
                                                                                xulydanhleLoTo( getNum, hslo, compareLo, vtSo1
                                                                                        , thuonglo, idSmsInt, dongiaId, listDonGia[1],
                                                                                        kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                                xulydanhleLoTo( getNum, hslo, compareLo, vtSo2
                                                                                        , thuonglo, idSmsInt, dongiaId, listDonGia[1],
                                                                                        kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                            } else {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                            }
                                                                        } else {
                                                                            if (!valueImprotDb[q].equals( "" )) {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }// end ghepab
                                                String[] checkGiatriMangLo = mangLocoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                if (checkGiatriMangLo.length > 2) {
                                                    error += "x " + "<font color=\"RED\">" + checkGiatriMangLo[0] + " </font>";
                                                    for (int val = 1; val < checkGiatriMangLo.length; val++) {
                                                        error += checkGiatriMangLo[val] + " ";
                                                    }
                                                } else {
                                                    if (mangLocoX[1].indexOf( "d" ) > -1 &&
                                                            mangLocoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                            mangLocoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                        error += "x " + mangLocoX[1].replace( ".", "j" ) + " ";
                                                    //----------------------------------------------
                                                    // khong phai vuong xoa di
                                                    //} else if (mangLocoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 0 &&
                                                    //        mangLocoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                    //    error += "x " + mangLocoX[1].replace( ".", "j" ) + " ";
                                                    // -----------------------------------------------
                                                    //} else if (mangLocoX[1].indexOf( "D1c" ) > -1 && mangLocoX[1].replace( ".", "" ).replaceAll( "(^\\s+|\\s+$)", "" ).length() == 3 &&
                                                    //        mangLocoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                    //    error += "x " + mangLocoX[1].replace( ".", "j" ) + " ";
                                                    } else {
                                                        error += "x " + "<font color=\"RED\">" + mangLocoX[1].replace( ".", "j" ) + " </font>";
                                                    }
                                                }
                                            } else {
                                                error += "<font color=\"RED\">" + tachChuoiLo.get( j ) + "</font>";
                                            }
                                        } else {
                                            error += "<font color=\"RED\">" + tachChuoiLo.get( j ) + " </font>";
                                        }
                                    } else if (tachChuoiLo.get( j ).indexOf( "=" ) > -1) { // kiem tra co dau x trong chuoi khong
                                        String[] mangLocoDauB = tachChuoiLo.get( j ).replaceAll( "(^\\s+|\\s+$)", "" ).split( "=" );
                                        if (mangLocoDauB.length == 2) {
                                            String boLoCoDauB = "";
                                            double getNum = 0; // so tien danh la getNum
                                            if (mangLocoDauB[1].replace( "D1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                if (mangLocoDauB[1].split( "j" ).length == 2) {
                                                    mangLocoDauB[1] = mangLocoDauB[1].replace( "j", "." );
                                                }
                                                getNum = Double.parseDouble( mangLocoDauB[1].replace( "D1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                            }
                                            if (!mangLocoDauB[0].replaceAll( "(^\\s+|\\s+$)", "" ).equals( "" )) {
                                                String valueLoCoDauB = controller.converStringSms( mangLocoDauB[0] );
                                                String[] valueLoArrCoDauB = valueLoCoDauB.split( "JAVASTR" );
                                                String SessionLoCoDaub = "";
                                                for (int k = 0; k < valueLoArrCoDauB.length; k++) {
                                                    String[] valueImprotDb = valueLoArrCoDauB[k].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                    if (kieubosodan.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                        /* danh dan nhonho -toto ... */
                                                        for (int sd = 0; sd < valueImprotDb.length; sd++) {
                                                            if (kieubosodan.contains( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                                if (SessionLoCoDaub.equals( "" )) {
                                                                    if (hashmap.get( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ) != null) {
                                                                        error += valueImprotDb[sd] + " ";
                                                                        String value = hashmap.get( valueImprotDb[sd] ).get( 0 );

                                                                        boLoCoDauB += value + ",";

                                                                    } else {
                                                                        error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                    }
                                                                } else {
                                                                    error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                }
                                                            } else {
                                                                error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                            }
                                                        }
                                                    } else if (kieuboso.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                        if (valueImprotDb.length > 1) {
                                                            error += valueImprotDb[0] + " ";
                                                            for (int q = 1; q < valueImprotDb.length; q++) {
                                                                if (!valueImprotDb[q].equals( "" )) {
                                                                    if (hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                            valueImprotDb[q] ) != null) {
                                                                        /* danh dan bo - he -tong...*/
                                                                        error += valueImprotDb[q] + " ";
                                                                        String value = hashmap.get( valueImprotDb[0] + valueImprotDb[q] ).get( 0 );

                                                                        if (SessionLoCoDaub != "") {
                                                                            String valueSessinoLoCoDauB = "";
                                                                            if (hashmap.get( SessionLoCoDaub.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                    valueImprotDb[q] ) != null) {
                                                                                valueSessinoLoCoDauB = hashmap.get( SessionLoCoDaub + valueImprotDb[q] ).get( 0 );
                                                                            }
                                                                            if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                                                // doan xu ly chuoi 36 dau dit 09 bcp
                                                                                for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                                                    if (!valueImprotDb[bcx].equals( "" )) {
                                                                                        String bcpRep = valueImprotDb[q] + valueImprotDb[bcx];
                                                                                        valueSessinoLoCoDauB = valueSessinoLoCoDauB.replace( bcpRep + ",", "" ).replace( bcpRep, "" );
                                                                                    }
                                                                                }
                                                                            }
                                                                            boLoCoDauB += valueSessinoLoCoDauB + ",";
                                                                        }
                                                                        boLoCoDauB += value + ",";

                                                                    } else {
                                                                        if (!valueImprotDb[q].equals( "bcp" )) {
                                                                            error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                        } else {
                                                                            error += valueImprotDb[q] + " ";
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            SessionLoCoDaub = "";
                                                        } else {
                                                            if (SessionLoCoDaub != "") {
                                                                error += "<font color=\"RED\">" + valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " </font>";
                                                            } else {
                                                                error += valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " ";
                                                            }
                                                            SessionLoCoDaub = valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                                                        }
                                                    } else {
                                                        String newValLoCoDauB = valueImprotDb[0].replaceAll( "\\d", "" );
                                                        // xu ly cac ki tu dau9 viet lien
                                                        if (kieuboso.contains( newValLoCoDauB )) {
                                                            String[] arrNewValLoCoX = valueImprotDb[0].replace( newValLoCoDauB, newValLoCoDauB + "JAVASTR" ).
                                                                    split( "JAVASTR" );
                                                            error += arrNewValLoCoX[0] + " ";
                                                            if (!arrNewValLoCoX[1].equals( "" )) {
                                                                if (hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                        arrNewValLoCoX[1] ) != null) {
                                                                    /* danh dan bo - he -tong...*/
                                                                    error += arrNewValLoCoX[1] + " ";
                                                                    String value = hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + arrNewValLoCoX[1] ).get( 0 );

                                                                    if (!SessionLoCoDaub.equals( "" )) {
                                                                        String valueSessinoLoCoX1 = "";
                                                                        if (hashmap.get( SessionLoCoDaub.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                arrNewValLoCoX[1] ) != null) {
                                                                            valueSessinoLoCoX1 = hashmap.get( SessionLoCoDaub + arrNewValLoCoX[1] ).get( 0 );
                                                                        }
                                                                        if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                                            // doan xu ly chuoi 36 dau dit 09 bcp
                                                                            String bcpRepa = arrNewValLoCoX[1] + arrNewValLoCoX[1];
                                                                            valueSessinoLoCoX1 = valueSessinoLoCoX1.replace( bcpRepa + ",", "" ).replace( bcpRepa, "" );
                                                                            for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                                                if (!valueImprotDb[bcx].equals( "" )) {
                                                                                    String bcpRepb = arrNewValLoCoX[1] + valueImprotDb[bcx];
                                                                                    valueSessinoLoCoX1 = valueSessinoLoCoX1.replace( bcpRepb + ",", "" ).replace( bcpRepb, "" );
                                                                                }
                                                                            }
                                                                        }
                                                                        boLoCoDauB += valueSessinoLoCoX1 + ",";
                                                                    }
                                                                    boLoCoDauB += value + ",";

                                                                    if (valueImprotDb.length == 1) {
                                                                        SessionLoCoDaub = "";
                                                                    }
                                                                } else {
                                                                    error += "<font color=\"RED\">" + arrNewValLoCoX[1] + " </font>";
                                                                }
                                                            }
                                                            for (int q = 1; q < valueImprotDb.length; q++) {
                                                                if (!valueImprotDb[q].equals( "" )) {
                                                                    if (hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                            valueImprotDb[q] ) != null) {
                                                                        /* danh dan bo - he -tong...*/
                                                                        error += valueImprotDb[q] + " ";
                                                                        String value = hashmap.get( arrNewValLoCoX[0] + valueImprotDb[q] ).get( 0 );

                                                                        if (SessionLoCoDaub != "") {
                                                                            String valueSessinoLoCoDau2 = "";
                                                                            if (hashmap.get( SessionLoCoDaub.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                    valueImprotDb[q] ) != null) {
                                                                                valueSessinoLoCoDau2 = hashmap.get( SessionLoCoDaub + valueImprotDb[q] ).get( 0 );
                                                                            }
                                                                            if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                                                // doan xu ly chuoi 36 dau dit 09 bcp
                                                                                String bcpRep2 = valueImprotDb[q] + arrNewValLoCoX[1];
                                                                                valueSessinoLoCoDau2 = valueSessinoLoCoDau2.replace( bcpRep2 + ",", "" ).replace( bcpRep2, "" );
                                                                                for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                                                    if (!valueImprotDb[bcx].equals( "" )) {
                                                                                        String bcpRep1 = valueImprotDb[q] + valueImprotDb[bcx];
                                                                                        valueSessinoLoCoDau2 = valueSessinoLoCoDau2.replace( bcpRep1 + ",", "" ).replace( bcpRep1, "" );
                                                                                    }
                                                                                }
                                                                            }
                                                                            boLoCoDauB += valueSessinoLoCoDau2 + ",";
                                                                        }
                                                                        boLoCoDauB += value + ",";

                                                                    } else {
                                                                        if (!valueImprotDb[q].equals( "bcp" )) {
                                                                            error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                        } else {
                                                                            error += valueImprotDb[q] + " ";
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            SessionLoCoDaub = "";
                                                        } else if (valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).equals( "gepbc" )) {
                                                            // doan nay xu ly Lo ghep 1234 thanh 12 cap so 12-21-13-31...
                                                            error += valueImprotDb[0] + controller.resGhepBcBoso( valueImprotDb ).get( 0 ) + " ";
                                                            xulydanhleLoToGhep( controller.resGhepBcBoso( valueImprotDb ).get( 1 ), getNum, hslo, compareLo, thuonglo,
                                                                    idSmsInt, dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
//
                                                        } else {
                                                            if (valueImprotDb.length > 1) {
                                                                for (int q = 0; q < valueImprotDb.length; q++) {
                                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                }
                                                            } else {
                                                                for (int q = 0; q < valueImprotDb.length; q++) {
                                                                    if (limitNumber.contains( valueImprotDb[q] )) {
                                                                        error += valueImprotDb[q] + " ";

                                                                        boLoCoDauB += valueImprotDb[q] + ",";

                                                                    } else {
                                                                        if (limitNumberBaCang.contains( valueImprotDb[q] )) {
                                                                            // doan nay xu ly cac so kieu de 565 656 555
                                                                            if (valueImprotDb[q].substring( 0, 1 ).equals( valueImprotDb[q].substring( 2, 3 ) )) {
                                                                                String vtSo1 = valueImprotDb[q].substring( 0, 2 );
                                                                                String vtSo2 = valueImprotDb[q].substring( 1, 3 );
                                                                                if  (vtSo1.equals(vtSo2)) {
                                                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                                } else {
                                                                                    error += " " + valueImprotDb[q] + " ";
                                                                                }
                                                                                // lo co x thi khong phai chia cho 2
                                                                                boLoCoDauB += vtSo1 + ",";
                                                                                boLoCoDauB += vtSo2 + ",";
                                                                            } else {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                            }
                                                                        } else {
                                                                            if (!valueImprotDb[q].equals( "" )) {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if (message[i].replaceAll( "(^\\s+|\\s+$)", "" ).length() > 6) {
                                                    String[] arrBoLoCoDauB = boLoCoDauB.split( "," );
                                                    if (arrBoLoCoDauB.length > 0) {
                                                        Double newGetNum = Math.round( getNum / arrBoLoCoDauB.length * 100.0 ) / 100.0;
                                                        for (int stt = 0; stt < arrBoLoCoDauB.length; stt++) {
                                                            xulydanhleLoTo( newGetNum, hslo, compareLo, arrBoLoCoDauB[stt]
                                                                    , thuonglo, idSmsInt, dongiaId, listDonGia[1],
                                                                    kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                        }
                                                    }
                                                }

                                                String[] checkGiatriMangLo = mangLocoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                if (checkGiatriMangLo.length > 2) {
                                                    error += "= " + "<font color=\"RED\">" + checkGiatriMangLo[0] + " </font>";
                                                    for (int val = 1; val < checkGiatriMangLo.length; val++) {
                                                        error += checkGiatriMangLo[val] + " ";
                                                    }
                                                } else if (valueLoArrCoDauB.length > 1) {
                                                    error += "= " + "<font color=\"RED\">" + mangLocoDauB[1] + " </font>";
                                                } else {
                                                    if (mangLocoDauB[1].indexOf( "d" ) > -1 &&
                                                            mangLocoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                            mangLocoDauB[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                        error += "= " + mangLocoDauB[1].replace( ".", "j" ) + " ";
                                                    } else if (mangLocoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 0 &&
                                                            mangLocoDauB[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                        error += "= " + mangLocoDauB[1].replace( ".", "j" ) + " ";
                                                    //} else if (mangLocoDauB[1].indexOf( "D1c" ) > -1 && mangLocoDauB[1].replace( ".", "" ).replaceAll( "(^\\s+|\\s+$)", "" ).length() == 3 &&
                                                    //        mangLocoDauB[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                    //    error += "= " + mangLocoDauB[1].replace( ".", "j" ) + " ";
                                                    } else {
                                                        error += "= " + "<font color=\"RED\">" + mangLocoDauB[1].replace( ".", "j" ) + " </font>";
                                                    }
                                                }
                                            } else {
                                                error += "<font color=\"RED\">" + tachChuoiLo.get( j ) + "</font>";
                                            }
                                        } else {
                                            error += "<font color=\"RED\">" + tachChuoiLo.get( j ) + " </font>";
                                        }
                                    }  else if (tachChuoiLo.get( j ).indexOf( "khongcodau" ) > -1) {
                                        String[] mangLokhongX = tachChuoiLo.get( j ).replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                        if (mangLokhongX.length == 2 && mangLokhongX[0].replaceAll( "[0-9]", "" ).length() == 0) {
                                            String valueMangLo = String.valueOf( mangLokhongX[mangLokhongX.length - 1] );
                                            double getNum = 0;
                                            String loBorKX = "";
                                            if (mangLokhongX.length >= 2) {
                                                if (valueMangLo.replace( "D1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                    if (valueMangLo.split( "j" ).length == 2) {
                                                        valueMangLo = valueMangLo.replace( "j", "." );
                                                    }
                                                    getNum = Double.parseDouble( valueMangLo.replace( "D1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                }
                                            }

                                            int endSubString = tachChuoiLo.get( j ).length() - valueMangLo.length() - 1;
                                            if (endSubString > 0) {
                                                String valueLo = controller.converStringSms( tachChuoiLo.get( j ).substring( 0, endSubString ) ).replace( ".", " " ).replaceAll( "(^\\s+|\\s+$)", "" );
                                                String[] valueLoArr = valueLo.split( "JAVASTR" );
                                                String SessionLoKX = "";
                                                for (int k = 0; k < valueLoArr.length; k++) {
                                                    String[] valueImprotDb = valueLoArr[k].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                    if (kieubosodan.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                    /* danh dan nhonho -toto ... */
                                                        for (int sd = 0; sd < valueImprotDb.length; sd++) {
                                                            if (kieubosodan.contains( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                                if (SessionLoKX.equals( "" )) {
                                                                    if (hashmap.get( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ) != null) {
                                                                        error += valueImprotDb[sd] + " ";
                                                                        String value = hashmap.get( valueImprotDb[sd] ).get( 0 );

                                                                        loBorKX += value + ",";
                                                                        xulydanhboLoTo( value, compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId, valueImprotDb[0],
                                                                                listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );

                                                                    } else {
                                                                        error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                    }
                                                                } else {
                                                                    error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                                }
                                                            } else {
                                                                error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                                            }
                                                        }
                                                    } else if (kieuboso.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                        if (valueImprotDb.length > 1) {
                                                            error += valueImprotDb[0] + " ";
                                                            for (int q = 1; q < valueImprotDb.length; q++) {
                                                                if (!valueImprotDb[q].equals( "" )) {
                                                                    if (hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                            valueImprotDb[q] ) != null) {
                                                                    /* danh dan bo - he -tong...*/
                                                                        error += valueImprotDb[q] + " ";
                                                                        String value = hashmap.get( valueImprotDb[0] + valueImprotDb[q] ).get( 0 );

                                                                        if (SessionLoKX != "") {
                                                                            if (hashmap.get( SessionLoKX + valueImprotDb[q] ) != null) {
                                                                                String valueSessionLoKX = hashmap.get( SessionLoKX + valueImprotDb[q] ).get( 0 );
                                                                                xulydanhboLoTo( valueSessionLoKX, compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId,
                                                                                        SessionLoKX + valueImprotDb[q],
                                                                                        listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                            } else {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                            }
                                                                        }
                                                                        loBorKX += value + ",";
                                                                        xulydanhboLoTo( value, compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId, valueImprotDb[0] + valueImprotDb[q],
                                                                                listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );

                                                                    } else {
                                                                        error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            if (SessionLoKX != "") {
                                                                error += "<font color=\"RED\">" + valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " </font>";
                                                            } else {
                                                                error += valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " ";
                                                            }
                                                            SessionLoKX = valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                                                        }
                                                    } else {
                                                        String newValLoCoX2 = valueImprotDb[0].replaceAll( "\\d", "" );
                                                        // xu ly cac ki tu dau9 viet lien
                                                        if (kieuboso.contains( newValLoCoX2 )) {
                                                            String[] arrNewValLoCoX = valueImprotDb[0].replace( newValLoCoX2, newValLoCoX2 + "JAVASTR" ).
                                                                    split( "JAVASTR" );
                                                            error += arrNewValLoCoX[0];
                                                            if (!arrNewValLoCoX[1].equals( "" )) {
                                                                if (hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                        arrNewValLoCoX[1] ) != null) {
                                                                /* danh dan bo - he -tong...*/
                                                                    error += arrNewValLoCoX[1] + " ";
                                                                    String value = hashmap.get( arrNewValLoCoX[0] + arrNewValLoCoX[1] ).get( 0 );

                                                                    if (SessionLoKX != "") {
                                                                        String valueSessionLoKX = "";
                                                                        if (hashmap.get( SessionLoKX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                arrNewValLoCoX[1] ) != null) {
                                                                            valueSessionLoKX = hashmap.get( SessionLoKX + arrNewValLoCoX[1] ).get( 0 );
                                                                        }
                                                                        loBorKX += valueSessionLoKX + ",";
                                                                        xulydanhboLoTo( valueSessionLoKX, compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId,
                                                                                SessionLoKX + arrNewValLoCoX[1],
                                                                                listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                    }
                                                                    loBorKX += value + ",";
                                                                    xulydanhboLoTo( value, compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId, arrNewValLoCoX[0] + arrNewValLoCoX[1],
                                                                            listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );

                                                                } else {
                                                                    error += "<font color=\"RED\">" + arrNewValLoCoX[1] + " </font>";
                                                                }
                                                            }
                                                            for (int q = 1; q < valueImprotDb.length; q++) {
                                                                if (!valueImprotDb[q].equals( "" )) {
                                                                    if (hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                            valueImprotDb[q] ) != null) {
                                                                /* danh dan bo - he -tong...*/
                                                                        error += valueImprotDb[q] + " ";
                                                                        String value = hashmap.get( arrNewValLoCoX[0] + valueImprotDb[q] ).get( 0 );

                                                                        if (SessionLoKX != "") {
                                                                            String valueSessionLoKX = "";
                                                                            if (hashmap.get( SessionLoKX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                                                    valueImprotDb[q] ) != null) {
                                                                                valueSessionLoKX = hashmap.get( SessionLoKX + valueImprotDb[q] ).get( 0 );
                                                                            }
                                                                            loBorKX += valueSessionLoKX + ",";
                                                                            xulydanhboLoTo( valueSessionLoKX, compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId,
                                                                                    SessionLoKX + valueImprotDb[q],
                                                                                    listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                        }
                                                                        loBorKX += value + ",";
                                                                        xulydanhboLoTo( value, compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId, arrNewValLoCoX[0] + valueImprotDb[q],
                                                                                listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );

                                                                    } else {
                                                                        error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                    }
                                                                }
                                                            }
                                                            SessionLoKX = "";
                                                        } else if (valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).equals( "gepbc" )) {
                                                            // doan nay xu ly de ghep 1234 thanh 12 cap so 12-21-13-31...
                                                            error += valueImprotDb[0] + controller.resGhepBcBoso( valueImprotDb ).get( 0 ) + " ";
                                                            xulydanhleLoToGhep( controller.resGhepBcBoso( valueImprotDb ).get( 1 ), getNum, hslo, compareLo, thuonglo,
                                                                    idSmsInt, dongiaId, listDonGia[1], kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
//
                                                        } else {
                                                            for (int q = 0; q < valueImprotDb.length; q++) {
                                                                if (limitNumber.contains( valueImprotDb[q] )) {
                                                                    error += valueImprotDb[q] + " ";

                                                                    loBorKX += valueImprotDb[q] + ",";
                                                                    xulydanhleLoTo( getNum, hslo, compareLo, valueImprotDb[q]
                                                                            , thuonglo, idSmsInt, dongiaId, listDonGia[1],
                                                                            kieuchoi, listDonGia[0], dataSoLieuDate, smsType );

                                                                } else {
                                                                    if (limitNumberBaCang.contains( valueImprotDb[q] )) {
                                                                        // doan nay xu ly cac so kieu de 565 656 555
                                                                        if (valueImprotDb[q].substring( 0, 1 ).equals( valueImprotDb[q].substring( 2, 3 ) )) {
                                                                            String vtSo1 = valueImprotDb[q].substring( 0, 2 );
                                                                            String vtSo2 = valueImprotDb[q].substring( 1, 3 );
                                                                            if  (vtSo1.equals(vtSo2)) {
                                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                            } else {
                                                                                error += " " + valueImprotDb[q] + " ";
                                                                            }
                                                                            loBorKX += vtSo1 + ",";
                                                                            loBorKX += vtSo2 + ",";
                                                                            xulydanhleLoTo( getNum, hslo, compareLo, vtSo1
                                                                                    , thuonglo, idSmsInt, dongiaId, listDonGia[1],
                                                                                    kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                            xulydanhleLoTo( getNum, hslo, compareLo, vtSo2
                                                                                    , thuonglo, idSmsInt, dongiaId, listDonGia[1],
                                                                                    kieuchoi, listDonGia[0], dataSoLieuDate, smsType );
                                                                        } else {
                                                                            error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                        }
                                                                    } else {
                                                                        if (!valueImprotDb[q].equals( "" )) {
//                                                                            if (valueImprotDb[q].replace(" ", "").equals("bro")) {
//                                                                                error += valueImprotDb[q] + " ";
//                                                                            } else {
                                                                            error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
//                                                                            }

                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } // endfor

                                                String[] checkGiatriMangLo = valueMangLo.replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                if (checkGiatriMangLo.length > 1) {
                                                    error += "<font color=\"RED\">" + valueMangLo + " </font>";
                                                    for (int val = 1; val < checkGiatriMangLo.length; val++) {
                                                        error += checkGiatriMangLo[val] + " ";
                                                    }
                                                } else if (valueLo.length() == 0) {
                                                    error += "<font color=\"RED\">" + valueMangLo + " </font>";
                                                } else {
                                                    if (valueMangLo.indexOf( "d" ) > -1 &&
                                                            valueMangLo.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                            valueMangLo.replaceAll( "[^\\d.]", "" ).replace( ".", "" ).length() >= 1
                                                            ) {
                                                        error += valueMangLo.replace( ".", "j" ) + " ";
                                                    //------------------------------------------------
                                                    //khong phai anh vuong vac
                                                    //} else if (valueMangLo.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 0 &&
                                                    //        valueMangLo.replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                    //    error += valueMangLo.replace( ".", "j" ) + " ";
                                                    //-------------------------------------------------
                                                    //} else if (valueMangLo.indexOf( "D1c" ) > -1 &&
                                                    //        valueMangLo.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replace( ".", "" ).replaceAll( "[0-9]", "" ).length() == 2 &&
                                                    //        valueMangLo.replaceAll( "[^\\d.]", "" ).replace( ".", "" ).length() >= 2) {
                                                    //    error += valueMangLo.replace( ".", "j" ) + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valueMangLo.replace( ".", "j" ) + " </font>";
                                                    }
                                                }
                                            } else {
                                                error += "<font color=\"RED\">" + tachChuoiLo.get( j ) + " </font>";
                                            }
                                        } else {
                                            error += "<font color=\"RED\">" + tachChuoiLo.get( j ) + " </font>";
                                        }
                                    } else {
                                        error += "<font color=\"RED\">" + tachChuoiLo.get( j ) + " </font>";
                                    }
                                }
                            }
                            break;
                        case "3c":
                            error += "3c ";
                            String chuoiBacang = message[i].substring( 2, message[i].length() ).replaceAll( "(^\\s+|\\s+$)", "" ).
                                    replace( "n1c", "N1c JAVASTR" ).replace( "n", "n JAVASTR" ).
                                    replace( "k", "k JAVASTR" ).replace( "trieu", "trieu JAVASTR" );
                            String[] mangBacang2 = chuoiBacang.split( "JAVASTR" );
                            ArrayList<String> tachChuoiBaCang = controller.tachchuoi( mangBacang2 );
                            for (int j = 0; j < tachChuoiBaCang.size(); j++) {
                                if (!tachChuoiBaCang.get( j ).equals( "" )) {
                                    if (tachChuoiBaCang.get( j ).indexOf( "x" ) > -1) { // kiem tra co dau x trong chuoi khong
                                        String[] mangBaCangcoX = tachChuoiBaCang.get( j ).replaceAll( "(^\\s+|\\s+$)", "" ).split( "x" );
                                        if (mangBaCangcoX.length == 2) {
                                            double getNum = 0;// so tien danh la getNum
                                            if (mangBaCangcoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                if (mangBaCangcoX[1].split( "j" ).length == 2) {
                                                    mangBaCangcoX[1] = mangBaCangcoX[1].replace( "j", "." );
                                                }
                                                if (mangBaCangcoX[1].indexOf( "trieu" ) > -1) {
                                                    getNum = Double.parseDouble( mangBaCangcoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                } else {
                                                    getNum = Double.parseDouble( mangBaCangcoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                }
                                            }
                                            if (!mangBaCangcoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).equals( "" )) {
                                                String valueBacang = controller.converStringSms( mangBaCangcoX[0] );
                                                String[] valueBacangArr = valueBacang.split( "JAVASTR" );
                                                for (int k = 0; k < valueBacangArr.length; k++) {
                                                    String[] valueImprotDb = valueBacangArr[k].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                    for (int q = 0; q < valueImprotDb.length; q++) {
                                                        if (limitNumberBaCang.contains( valueImprotDb[q] )) {
                                                            try {
                                                                error += valueImprotDb[q] + " ";
                                                                double trungSms = 0;
                                                                double tongTienDanh = 0;
                                                                double tongTienThuong = 0;
                                                                double tongTien = 0;
                                                                int trung = 0;
                                                                tongTienDanh = Math.round( getNum * hsbacang * 100.0 ) / 100.0;
                                                                if (valueImprotDb[q].equals( compareBaCang )) {
                                                                    trung = 1;
                                                                    trungSms += getNum;
                                                                    tongTienThuong = Math.round( getNum * thuongbacang * 100.0 ) / 100.0;
                                                                } else {
                                                                    trung = 0;
                                                                    tongTienThuong = 0;
                                                                }
                                                                double tong = tongTienDanh - tongTienThuong;
                                                                tongTien = Math.round( (tongTienDanh - tongTienThuong) * 100.0 ) / 100.0;
                                                                trungSms = Math.round( trungSms * 100.0 ) / 100.0;
                                                                boolean soLieu = sql.insertDataSourceSoLieu( idSmsInt, dongiaId, valueImprotDb[q]
                                                                        , trung, tongTienDanh, tongTienThuong, tongTien, smsType
                                                                        , listDonGia[1], "bacang", listDonGia[0], dataSoLieuDate
                                                                        , hsbacang, thuongbacang, getNum, getNum, trungSms );
                                                            } catch (NumberFormatException e) {
                                                                error += "<font color=\"RED\"> " + valueImprotDb[q] + "</font>";
                                                            }
                                                        } else {
                                                            if (!valueImprotDb[q].equals( "" )) {
                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                            }
                                                        }
                                                    }
                                                }
                                                if (mangBaCangcoX[1].indexOf( "n" ) > -1 &&
                                                        mangBaCangcoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( ".", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                        mangBaCangcoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                    error += "x " + mangBaCangcoX[1].replace( ".", "j" ) + " ";
                                                } else if (mangBaCangcoX[1].indexOf( "k" ) > -1 &&
                                                        mangBaCangcoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( ".", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                        mangBaCangcoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                    error += "x " + mangBaCangcoX[1].replace( ".", "j" ) + " ";
                                                } else if (mangBaCangcoX[1].indexOf( "trieu" ) > -1 &&
                                                        mangBaCangcoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( ".", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                        mangBaCangcoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                    error += "x " + mangBaCangcoX[1].replace( ".", "j" ) + " ";
                                                //-----------------------------------------------------------
                                                // khong phai a vuong vac
                                                // } else if (mangBaCangcoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( ".", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 0 &&
                                                //        mangBaCangcoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                //    error += "x " + mangBaCangcoX[1].replace( ".", "j" ) + " ";
                                                //-------------------------------------------------------------
                                                //} else if (mangBaCangcoX[1].indexOf( "N1c" ) > -1 && mangBaCangcoX[1].replace( ".", "" ).replaceAll( "(^\\s+|\\s+$)", "" ).length() == 3 &&
                                                //        mangBaCangcoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                //    error += "x " + mangBaCangcoX[1].replace( ".", "j" ) + " ";
                                                } else {
                                                    error += "x " + "<font color=\"RED\">" + mangBaCangcoX[1].replace( ".", "j" ) + " </font>";
                                                }
                                            } else {
                                                error += "<font color=\"RED\">" + tachChuoiBaCang.get( j ) + "</font>";
                                            }
                                        } else {
                                            error += "<font color=\"RED\">" + tachChuoiBaCang.get( j ) + " </font>";
                                        }
                                    } else if (tachChuoiBaCang.get( j ).indexOf( "=" ) > -1) { // kiem tra co dau = trong chuoi khong
                                        String[] mangBaCangcoX = tachChuoiBaCang.get( j ).replaceAll( "(^\\s+|\\s+$)", "" ).split( "=" );
                                        if (mangBaCangcoX.length == 2) {
                                            double getNum = 0;   // so tien danh la getNum
                                            if (mangBaCangcoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                if (mangBaCangcoX[1].split( "j" ).length == 2) {
                                                    mangBaCangcoX[1] = mangBaCangcoX[1].replace( "j", "." );
                                                }
                                                if (mangBaCangcoX[1].indexOf( "trieu" ) > -1) {
                                                    getNum = Double.parseDouble( mangBaCangcoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                } else {
                                                    getNum = Double.parseDouble( mangBaCangcoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                }
                                            }

                                            if (!mangBaCangcoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).equals( "" )) {
                                                String valueBacang = controller.converStringSms( mangBaCangcoX[0] );
                                                String[] valueBacangArr = valueBacang.split( "JAVASTR" );
                                                double newGetNum = 0;
                                                if (valueBacangArr.length > 0) {
                                                    newGetNum = Math.round( getNum / valueBacangArr.length * 100.0 ) / 100.0;
                                                }
                                                for (int k = 0; k < valueBacangArr.length; k++) {
                                                    String[] valueImprotDb = valueBacangArr[k].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                    if (valueImprotDb.length > 1) {
                                                        for (int q = 0; q < valueImprotDb.length; q++) {
                                                            error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                        }
                                                    } else {
                                                        for (int q = 0; q < valueImprotDb.length; q++) {
                                                            if (limitNumberBaCang.contains( valueImprotDb[q] )) {
                                                                try {
                                                                    error += valueImprotDb[q] + " ";
                                                                    double trungSms = 0;
                                                                    double tongTienDanh = 0;
                                                                    double tongTienThuong = 0;
                                                                    double tongTien = 0;
                                                                    int trung = 0;
                                                                    tongTienDanh = Math.round( newGetNum * hsbacang * 100.0 ) / 100.0;
                                                                    if (valueImprotDb[q].equals( compareBaCang )) {
                                                                        trung = 1;
                                                                        trungSms += newGetNum;
                                                                        tongTienThuong = Math.round( newGetNum * thuongbacang * 100.0 ) / 100.0;
                                                                    } else {
                                                                        trung = 0;
                                                                        tongTienThuong = 0;
                                                                    }
                                                                    double tong = tongTienDanh - tongTienThuong;
                                                                    tongTien = Math.round( (tongTienDanh - tongTienThuong) * 100.0 ) / 100.0;
                                                                    trungSms = Math.round( trungSms * 100.0 ) / 100.0;
                                                                    boolean soLieu = sql.insertDataSourceSoLieu( idSmsInt, dongiaId, valueImprotDb[q]
                                                                            , trung, tongTienDanh, tongTienThuong, tongTien, smsType
                                                                            , listDonGia[1], "bacang", listDonGia[0], dataSoLieuDate
                                                                            , hsbacang, thuongbacang, newGetNum, newGetNum, trungSms );
                                                                } catch (NumberFormatException e) {
                                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                }
                                                            } else {
                                                                if (!valueImprotDb[q].equals( "" )) {
                                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if (valueBacangArr.length > 1) {
                                                    error += "= " + "<font color=\"RED\">" + mangBaCangcoX[1].replace( ".", "j" ) + " </font>";
                                                } else if (mangBaCangcoX[1].indexOf( "n" ) > -1 &&
                                                        mangBaCangcoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( ".", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                        mangBaCangcoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                    error += "= " + mangBaCangcoX[1].replace( ".", "j" ) + " ";
                                                } else if (mangBaCangcoX[1].indexOf( "d" ) > -1 &&
                                                        mangBaCangcoX[1].replace( ".", "" ).replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                        mangBaCangcoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                    error += "= " + mangBaCangcoX[1].replace( ".", "j" ) + " ";
                                                } else if (mangBaCangcoX[1].indexOf( "trieu" ) > -1 &&
                                                        mangBaCangcoX[1].replace( ".", "" ).replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 5 &&
                                                        mangBaCangcoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                    error += "= " + mangBaCangcoX[1].replace( ".", "j" ) + " ";
                                                } else if (mangBaCangcoX[1].replace( ".", "" ).replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 0 &&
                                                        mangBaCangcoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                    error += "= " + mangBaCangcoX[1].replace( ".", "j" ) + " ";
                                                //} else if (mangBaCangcoX[1].indexOf( "N1c" ) > -1 && mangBaCangcoX[1].replace( ".", "" ).replaceAll( "(^\\s+|\\s+$)", "" ).length() == 3 &&
                                                //        mangBaCangcoX[1].replace( ".", "" ).replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                //    error += "= " + mangBaCangcoX[1].replace( ".", "j" ) + " ";
                                                } else {
                                                    error += "= " + "<font color=\"RED\">" + mangBaCangcoX[1].replace( ".", "j" ) + " </font>";
                                                }
                                            } else {
                                                error += "<font color=\"RED\">" + tachChuoiBaCang.get( j ) + "</font>";
                                            }
                                        } else {
                                            error += "<font color=\"RED\">" + tachChuoiBaCang.get( j ) + " </font>";
                                        }
                                    } else if (tachChuoiBaCang.get( j ).indexOf( "khongcodau" ) > -1) { //bo khong co dau
                                        String[] mangBacangkhongX = tachChuoiBaCang.get( j ).replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                        if (mangBacangkhongX.length == 2 && mangBacangkhongX[0].replaceAll( "[0-9]", "" ).length() == 0) {
                                            String valueMangBacang = String.valueOf( mangBacangkhongX[mangBacangkhongX.length - 1] );
                                            double getNum = 0;
                                            if (tachChuoiBaCang.get( j ).indexOf( "N1c" ) > -1) {
                                                if (valueMangBacang.replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                    if (valueMangBacang.split( "j" ).length == 2) {
                                                        valueMangBacang = valueMangBacang.replace( "j", "." );
                                                    }
                                                    if (valueMangBacang.indexOf( "trieu" ) > -1) {
                                                        getNum = Double.parseDouble( valueMangBacang.replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                    } else {
                                                        getNum = Double.parseDouble( valueMangBacang.replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                    }
                                                }
                                            } else {
                                                if (mangBacangkhongX.length >= 2) {
                                                    if (valueMangBacang.replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                        if (valueMangBacang.indexOf( "trieu" ) > -1) {
                                                            getNum = Double.parseDouble( valueMangBacang.replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                        } else {
                                                            getNum = Double.parseDouble( valueMangBacang.replaceAll( "[^\\d.]", "" ) );
                                                        }
                                                    }
                                                }
                                            }
                                            int endSubString = tachChuoiBaCang.get( j ).length() - valueMangBacang.length() - 1;
                                            if (endSubString > 0) {
                                                String valueBacang = controller.converStringSms( tachChuoiBaCang.get( j ).substring( 0, endSubString ) ).replaceAll( "(^\\s+|\\s+$)", "" );
                                                String[] valueBacangArr = valueBacang.split( "JAVASTR" );
                                                for (int k = 0; k < valueBacangArr.length; k++) {
                                                    String[] valueImprotDb = valueBacangArr[k].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                    for (int q = 0; q < valueImprotDb.length; q++) {
                                                        if (limitNumberBaCang.contains( valueImprotDb[q] )) {
                                                            try {
                                                                error += valueImprotDb[q] + " ";
                                                                double trungSms = 0;
                                                                double tongTienDanh = 0;
                                                                double tongTienThuong = 0;
                                                                double tongTien = 0;
                                                                int trung = 0;
                                                                tongTienDanh = Math.round( getNum * hsbacang * 100.0 ) / 100.0;
                                                                if (valueImprotDb[q].equals( compareBaCang )) {
                                                                    trung = 1;
                                                                    trungSms += getNum;
                                                                    tongTienThuong = Math.round( getNum * thuongbacang * 100.0 ) / 100.0;
                                                                } else {
                                                                    trung = 0;
                                                                    tongTienThuong = 0;
                                                                }
                                                                double tong = tongTienDanh - tongTienThuong;
                                                                tongTien = Math.round( (tongTienDanh - tongTienThuong) * 100.0 ) / 100.0;
                                                                trungSms = Math.round( trungSms * 100.0 ) / 100.0;
                                                                boolean soLieu = sql.insertDataSourceSoLieu( idSmsInt, dongiaId, valueImprotDb[q]
                                                                        , trung, tongTienDanh, tongTienThuong, tongTien, smsType
                                                                        , listDonGia[1], "bacang", listDonGia[0], dataSoLieuDate
                                                                        , hsbacang, thuongbacang, getNum, getNum, trungSms );

                                                            } catch (NumberFormatException e) {
                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + "</font>";
                                                            }
                                                        } else {
                                                            if (!valueImprotDb[q].equals( "" )) {
                                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                            }
                                                        }
                                                    }
                                                } // endfor
                                                String[] checkGiatriBacangLo = valueMangBacang.replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                if (checkGiatriBacangLo.length > 1) {
                                                    error += "<font color=\"RED\">" + valueMangBacang + " </font>";
                                                    for (int val = 1; val < checkGiatriBacangLo.length; val++) {
                                                        error += checkGiatriBacangLo[val] + " ";
                                                    }
                                                } else {
                                                    if (valueMangBacang.indexOf( "n" ) > -1 &&
                                                            valueMangBacang.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                            valueMangBacang.replaceAll( "[^\\d.]", "" ).length() >= 1
                                                            ) {
                                                        error += valueMangBacang.replace( ".", "j" ) + " ";
                                                    } else if (valueMangBacang.indexOf( "k" ) > -1 &&
                                                            valueMangBacang.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                            valueMangBacang.replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                        error += valueMangBacang.replace( ".", "j" ) + " ";
                                                    } else if (valueMangBacang.indexOf( "trieu" ) > -1 &&
                                                            valueMangBacang.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 5 &&
                                                            valueMangBacang.replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                        error += valueMangBacang.replace( ".", "j" ) + " ";
                                                    } else if (valueMangBacang.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 0 &&
                                                            valueMangBacang.replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                        error += valueMangBacang.replace( ".", "j" ) + " ";
                                                    //} else if (valueMangBacang.indexOf( "N1c" ) > -1 &&
                                                    //        valueMangBacang.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 2 &&
                                                    //       valueMangBacang.replaceAll( "[^\\d.]", "" ).length() >= 2) {
                                                    //    error += valueMangBacang.replace( ".", "j" ) + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valueMangBacang.replace( ".", "j" ) + " </font>";
                                                    }
                                                }
                                            } else {
                                                error += "<font color=\"RED\">" + tachChuoiBaCang.get( j ) + " </font>";
                                            }
                                        } else {
                                            error += "<font color=\"RED\">" + tachChuoiBaCang.get( j ) + " </font>";
                                        }
                                    } else {
                                        error += "<font color=\"RED\">" + tachChuoiBaCang.get( j ) + " </font>";
                                    }
                                }
                            }
                            break;
                        case "si":
                        case "sa":
                            if (message[i].length() > 3) {
                                String chuoiXien = message[i].replaceAll( "(^\\s+|\\s+$)", "" );
                                String kieuXien = chuoiXien.substring( 0, 3 );
                                if (Arrays.asList(xiAb).contains(kieuXien.trim())) {
                                    error += kieuXien;
                                } else {
                                    error += "<font color=\"RED\">" + kieuXien + " </font>";
                                }
                                if (kieuchoi.equals( "sa" )) {
                                    compareLo = compareLoDau;
                                } else {
                                    compareLo = compareLo2;
                                }
                                String valXien = chuoiXien.substring( 3, chuoiXien.length() ).replaceAll( "(^\\s+|\\s+$)", "" );
                                String chuoiXien2 = valXien.replace( "n1c", "N1c JAVASTR" ).replace( "n", "n JAVASTR" )
                                        .replace( "k", "k JAVASTR" ).replace( "trieu", "trieu JAVASTR" ).replace( "d", "d JAVASTR" );
                                String[] mangChuoiXien2 = chuoiXien2.split( " JAVASTR" );
                                ArrayList<String> tachChuoiXien = controller.tachchuoi( mangChuoiXien2 );
                                for (int tc = 0; tc < tachChuoiXien.size(); tc++) {
                                    if (!tachChuoiXien.get( tc ).equals( "" )) {
                                        if (tachChuoiXien.get( tc ).replaceAll( "(^\\s+|\\s+$)", "" ).indexOf( "x" ) > -1) {
                                            String[] mangXienCoX = tachChuoiXien.get( tc ).replaceAll( "(^\\s+|\\s+$)", "" ).split( "x" );
                                            if (mangXienCoX.length == 2) {
                                                double getNum = 0;
                                                if (mangXienCoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                    if (mangXienCoX[1].split( "j" ).length == 2) {
                                                        mangXienCoX[1] = mangXienCoX[1].replace( "j", "." );
                                                    }
                                                    if (mangXienCoX[1].indexOf( "trieu" ) > -1) {
                                                        getNum = Double.parseDouble( mangXienCoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                    } else if (mangXienCoX[1].indexOf( "d" ) > -1) {
                                                        getNum = Double.parseDouble( mangXienCoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 10;
                                                    } else {
                                                        getNum = Double.parseDouble( mangXienCoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                    }
                                                }
                                                String chuoiTachSoXienCoX = controller.tachChuoiXien( mangXienCoX[0], limitNumberBaCang );
                                                String[] mangValXien = chuoiTachSoXienCoX.replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                if (mangXienCoX[0].indexOf( "ghep" ) > -1 || mangXienCoX[0].indexOf( "gep" ) > -1) {
                                                    Log.d("LogFile",mangXienCoX[0].split(" ")[0]);
                                                    mangXienCoX[0] = mangXienCoX[0]
                                                            .replace(")(","x"+mangXienCoX[1]+" "+ mangXienCoX[0].split(" ")[0]+" ")
                                                            .replace(") (","x"+mangXienCoX[1]+" "+mangXienCoX[0].split(" ")[0]+" ");
                                                    mangXienCoX[0] = mangXienCoX[0].replace("(","").replace(")","");
                                                    ArrayList<String> resXien = controller.ghepxien( mangXienCoX[0] );
                                                    xulydanhMangXienKieu( getNum, hsx2, thuongxien2, hsx3, thuongxien3, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                            resXien.get( 1 ), resXien.get( 2 ), resXien.get( 3 ),
                                                            1, smsType, listDonGia[1], listDonGia[0], dataSoLieuDate,kieuXien );
                                                    error += resXien.get( 0 );
                                                } else {
                                                    switch (kieuXien) {
                                                        case "si2":
                                                        case "sa2":
                                                            if (mangValXien.length % 2 == 0) {
                                                                for (int valx = 0; valx < mangValXien.length; valx = valx + 2) {
                                                                    int valx1 = valx;
                                                                    int valx2 = valx + 1;
                                                                    String trungxi2 = "";
                                                                    for (int ixi = 0; ixi < compareLo.size(); ixi++) {
                                                                        if (mangValXien[valx1].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi2.indexOf( "1" ) == -1) {
                                                                            trungxi2 += "1";
                                                                            continue;
                                                                        } else if (mangValXien[valx2].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi2.indexOf( "2" ) == -1) {
                                                                            trungxi2 += "2";
                                                                            continue;
                                                                        }
                                                                    }
                                                                    if (trungxi2.length() == 2) {

                                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangValXien[valx1] + " " + mangValXien[valx2],
                                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuXien);

                                                                    } else {

                                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangValXien[valx1] + " " + mangValXien[valx2],
                                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuXien );

                                                                    }
                                                                    if (limitNumber.contains( mangValXien[valx1] )) {
                                                                        error += " " + mangValXien[valx1];
                                                                    } else {
                                                                        error += "<font color=\"RED\"> " + mangValXien[valx1] + " </font>";
                                                                    }
                                                                    if (limitNumber.contains( mangValXien[valx2] )) {
                                                                        error += " " + mangValXien[valx2];
                                                                    } else {
                                                                        error += "<font color=\"RED\"> " + mangValXien[valx2] + " </font>";
                                                                    }
                                                                }
                                                            } else {
                                                                if (mangValXien.length > 2) {
                                                                    for (int valx = 0; valx < mangValXien.length - 1; valx++) {
                                                                        error += " " + mangValXien[valx];
                                                                    }
                                                                    int endArr = mangValXien.length - 1;
                                                                    error += "<font color=\"RED\"> " + mangValXien[endArr] + " </font>";
                                                                    // bao do vi thua gia tri
                                                                } else {
                                                                    for (int valx = 0; valx < mangValXien.length; valx++) {
                                                                        error += " " + mangValXien[valx];
                                                                    }
                                                                }
                                                            }
                                                            break;
                                                        case "si3":
                                                        case "sa3":
                                                            if (mangValXien.length % 3 == 0) {
                                                                ArrayList<String> checkXien = new ArrayList<>();
                                                                for (int valx = 0; valx < mangValXien.length; valx = valx + 3) {
                                                                    int valx1 = valx;
                                                                    int valx2 = valx + 1;
                                                                    int valx3 = valx + 2;
                                                                    String trungxi3 = "";
                                                                    for (int ixi = 0; ixi < compareLo.size(); ixi++) {
                                                                        if (mangValXien[valx1].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi3.indexOf( "1" ) == -1) {
                                                                            trungxi3 += "1";
                                                                            continue;
                                                                        } else if (mangValXien[valx2].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi3.indexOf( "2" ) == -1) {
                                                                            trungxi3 += "2";
                                                                            continue;
                                                                        } else if (mangValXien[valx3].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi3.indexOf( "3" ) == -1) {
                                                                            trungxi3 += "3";
                                                                            continue;
                                                                        }
                                                                    }
                                                                    if (trungxi3.length() == 3) {

                                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                                mangValXien[valx1] + " " + mangValXien[valx2] + " " + mangValXien[valx3],
                                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuXien );

                                                                    } else {

                                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                                mangValXien[valx1] + " " + mangValXien[valx2] + " " + mangValXien[valx3],
                                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuXien );

                                                                    }
                                                                    if (limitNumber.contains( mangValXien[valx1] )) {
                                                                        error += " " + mangValXien[valx1];
                                                                    } else {
                                                                        error += "<font color=\"RED\"> " + mangValXien[valx1] + " </font>";
                                                                    }
                                                                    if (limitNumber.contains( mangValXien[valx2] )) {
                                                                        error += " " + mangValXien[valx2];
                                                                    } else {
                                                                        error += "<font color=\"RED\"> " + mangValXien[valx2] + " </font>";
                                                                    }
                                                                    if (limitNumber.contains( mangValXien[valx3] )) {
                                                                        error += " " + mangValXien[valx3];
                                                                    } else {
                                                                        error += "<font color=\"RED\"> " + mangValXien[valx3] + " </font>";
                                                                    }
                                                                }
                                                            } else {
                                                                if (mangValXien.length > 3) {
                                                                    int sodux3 = mangValXien.length % 3;
                                                                    for (int valx = 0; valx < mangValXien.length - sodux3; valx++) {
                                                                        error += " " + mangValXien[valx];
                                                                    }
                                                                    if (sodux3 == 1) {
                                                                        int endArr = mangValXien.length - 1;
                                                                        error += "<font color=\"RED\"> " + mangValXien[endArr] + " </font>"; // bao do vi thua gia tri
                                                                    } else {
                                                                        int endArr1 = mangValXien.length - 1;
                                                                        int endArr2 = mangValXien.length - 2;
                                                                        error += "<font color=\"RED\"> " + mangValXien[endArr1] + " " + mangValXien[endArr2] + " </font>"; // bao do vi thua gia tri
                                                                    }
                                                                } else {
                                                                    for (int valx = 0; valx < mangValXien.length; valx++) {
                                                                        error += "<font color=\"RED\"> " + mangValXien[valx] + " </font>";
                                                                    }
                                                                }
                                                            }
                                                            break;
                                                        case "si4":
                                                        case "sa4":
                                                            if (mangValXien.length % 4 == 0) {
                                                                ArrayList<String> checkXien = new ArrayList<>();
                                                                for (int valx = 0; valx < mangValXien.length; valx = valx + 4) {
                                                                    int valx1 = valx;
                                                                    int valx2 = valx + 1;
                                                                    int valx3 = valx + 2;
                                                                    int valx4 = valx + 3;
                                                                    String trungxi4 = "";
                                                                    for (int ixi = 0; ixi < compareLo.size(); ixi++) {
                                                                        if (mangValXien[valx1].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi4.indexOf( "1" ) == -1) {
                                                                            trungxi4 += "1";
                                                                            continue;
                                                                        } else if (mangValXien[valx2].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi4.indexOf( "2" ) == -1) {
                                                                            trungxi4 += "2";
                                                                            continue;
                                                                        } else if (mangValXien[valx3].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi4.indexOf( "3" ) == -1) {
                                                                            trungxi4 += "3";
                                                                            continue;
                                                                        } else if (mangValXien[valx4].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi4.indexOf( "4" ) == -1) {
                                                                            trungxi4 += "4";
                                                                            continue;
                                                                        }
                                                                    }
                                                                    if (trungxi4.length() == 4) {

                                                                        xulydanhXienKieu( getNum, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                                                mangValXien[valx1] + " " + mangValXien[valx2] + " " + mangValXien[valx3] + " " + mangValXien[valx4],
                                                                                1, smsType, listDonGia[1], "xien4", listDonGia[0], dataSoLieuDate,kieuXien );

                                                                    } else {

                                                                        xulydanhXienKieu( getNum, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                                                mangValXien[valx1] + " " + mangValXien[valx2] + " " + mangValXien[valx3] + " " + mangValXien[valx4],
                                                                                0, smsType, listDonGia[1], "xien4", listDonGia[0], dataSoLieuDate,kieuXien );

                                                                    }
                                                                    if (limitNumber.contains( mangValXien[valx1] )) {
                                                                        error += " " + mangValXien[valx1];
                                                                    } else {
                                                                        error += "<font color=\"RED\"> " + mangValXien[valx1] + " </font>";
                                                                    }
                                                                    if (limitNumber.contains( mangValXien[valx2] )) {
                                                                        error += " " + mangValXien[valx2];
                                                                    } else {
                                                                        error += "<font color=\"RED\"> " + mangValXien[valx2] + " </font>";
                                                                    }
                                                                    if (limitNumber.contains( mangValXien[valx3] )) {
                                                                        error += " " + mangValXien[valx3];
                                                                    } else {
                                                                        error += "<font color=\"RED\"> " + mangValXien[valx3] + " </font>";
                                                                    }
                                                                    if (limitNumber.contains( mangValXien[valx4] )) {
                                                                        error += " " + mangValXien[valx4];
                                                                    } else {
                                                                        error += "<font color=\"RED\"> " + mangValXien[valx4] + " </font>";
                                                                    }
                                                                }
                                                            } else {
                                                                if (mangValXien.length > 4) {
                                                                    int sodux4 = mangValXien.length % 4;
                                                                    for (int valx = 0; valx < mangValXien.length - sodux4; valx++) {
                                                                        error += " " + mangValXien[valx];
                                                                    }
                                                                    if (sodux4 == 1) {
                                                                        int endArr = mangValXien.length - 1;
                                                                        error += "<font color=\"RED\"> " + mangValXien[endArr] + " </font>"; // bao do vi thua gia tri
                                                                    } else if (sodux4 == 2) {
                                                                        int endArr1 = mangValXien.length - 1;
                                                                        int endArr2 = mangValXien.length - 2;
                                                                        error += "<font color=\"RED\"> " + mangValXien[endArr1] + " " + mangValXien[endArr2] + " </font>"; // bao do vi thua gia tri
                                                                    } else {
                                                                        int endArr1 = mangValXien.length - 1;
                                                                        int endArr2 = mangValXien.length - 2;
                                                                        int endArr3 = mangValXien.length - 3;
                                                                        error += "<font color=\"RED\"> " + mangValXien[endArr1] + " " + mangValXien[endArr2] + " " + mangValXien[endArr3] + " </font>"; // bao do vi thua gia tri
                                                                    }
                                                                } else {
                                                                    for (int valx = 0; valx < mangValXien.length; valx++) {
                                                                        error += "<font color=\"RED\"> " + mangValXien[valx] + " </font>";
                                                                    }
                                                                }
                                                            }
                                                            break;
                                                        case "si ":
                                                        case "sa ":
                                                            if (mangValXien.length <= 4 && mangValXien.length >= 2) {
                                                                String boSoXien = "";
                                                                ArrayList<String> checkXien = new ArrayList<>();
                                                                int trungxiAll = 0;
                                                                ArrayList<String> arrCheckDuplicateXien = new ArrayList<>();
                                                                for (int allX = 0; allX < mangValXien.length; allX++) {
                                                                    if (limitNumber.contains( mangValXien[allX] )) {
                                                                        try {
                                                                            error += " " + mangValXien[allX];
                                                                            boSoXien += " " + mangValXien[allX];
                                                                            int trung = Collections.frequency( compareLo, mangValXien[allX].replaceAll( "(^\\s+|\\s+$)", "" ) );
                                                                            if (trung > 0) {
                                                                                arrCheckDuplicateXien.add( mangValXien[allX].replaceAll( "(^\\s+|\\s+$)", "" ) );
                                                                                if (Collections.frequency( arrCheckDuplicateXien, mangValXien[allX].replaceAll( "(^\\s+|\\s+$)", "" ) ) <=
                                                                                        Collections.frequency( compareLo, mangValXien[allX].replaceAll( "(^\\s+|\\s+$)", "" ) )) {
                                                                                    checkXien.add( "true" );
                                                                                } else {
                                                                                    checkXien.add( "false" );
                                                                                }
                                                                            } else {
                                                                                checkXien.add( "false" );
                                                                            }
                                                                        } catch (NumberFormatException e) {
                                                                            error += "<font color=\"RED\"> " + mangValXien[allX] + "</font>";
                                                                        }
                                                                    } else {
                                                                        error += "<font color=\"RED\"> " + mangValXien[allX] + "</font>";
                                                                    }
                                                                }

                                                                xulydanhXien( checkXien, getNum, hsx2, thuongxien2, hsx3
                                                                        , thuongxien3, hsx4, thuongxien4, idSmsInt, dongiaId, boSoXien, smsType
                                                                        , listDonGia[1], "xien", listDonGia[0], dataSoLieuDate,kieuXien );

                                                            } else if (mangValXien.length > 4) {
                                                                error += mangValXien[0] + " " + mangValXien[1] + " " + mangValXien[2] + " " +
                                                                        mangValXien[3] + " ";
                                                                for (int allX = 4; allX < mangValXien.length; allX++) {
                                                                    error += "<font color=\"RED\"> " + mangValXien[allX] + " </font>";
                                                                }
                                                            } else {
                                                                for (int allX = 0; allX < mangValXien.length; allX++) {
                                                                    error += "<font color=\"RED\"> " + mangValXien[allX] + " </font>";
                                                                }
                                                            }
                                                            break;
                                                        default:
                                                            error += "<font color=\"red\" >" + tachChuoiXien.get( tc ).replaceAll( "(^\\s+|\\s+$)", "" ) + "</font>";
                                                            break;
                                                    }
                                                }
                                                String[] checkGiatriMangXien = mangXienCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                if (checkGiatriMangXien.length > 1) {
                                                    error += " x " + "<font color=\"RED\">" + checkGiatriMangXien[0] + " </font>";
                                                    for (int val = 1; val < checkGiatriMangXien.length; val++) {
                                                        error += checkGiatriMangXien[val] + " ";
                                                    }
                                                } else {
                                                    if (mangXienCoX[1].indexOf( "n" ) > -1 &&
                                                            mangXienCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                        error += " x " + mangXienCoX[1].replace( ".", "j" ) + " ";
                                                    } else if (mangXienCoX[1].indexOf( "k" ) > -1 &&
                                                            mangXienCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                        error += " x " + mangXienCoX[1].replace( ".", "j" ) + " ";
                                                    } else if (mangXienCoX[1].indexOf( "d" ) > -1 &&
                                                            mangXienCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                        error += " x " + mangXienCoX[1].replace( ".", "j" ) + " ";
                                                    } else if (mangXienCoX[1].indexOf( "trieu" ) > -1 &&
                                                            mangXienCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 5) {
                                                        error += " x " + mangXienCoX[1].replace( ".", "j" ) + " ";
                                                    } else if (mangXienCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 0) {
                                                        error += " x " + mangXienCoX[1].replace( ".", "j" ) + " ";
                                                    //} else if (mangXienCoX[1].indexOf( "N1c" ) > -1 && mangXienCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).length() == 3) {
                                                    //    error += " x " + mangXienCoX[1].replace( ".", "j" ) + " ";
                                                    } else {
                                                        error += " x " + "<font color=\"RED\">" + mangXienCoX[1].replace( ".", "j" ) + " </font>";
                                                    }
                                                }
                                            } else if (mangXienCoX.length > 2) {
                                                error += mangXienCoX[0] + "x  " + mangXienCoX[1];
                                                for (int si = 2; si < mangXienCoX.length; si++) {
                                                    error += "<font color=\"RED\">x " + mangXienCoX[si] + " </font>";
                                                }
                                            } else {
                                                error += "<font color=\"RED\"> " + tachChuoiXien.get( tc ).replaceAll( "(^\\s+|\\s+$)", "" ) + " </font>";
                                            }
                                        } else if (tachChuoiXien.get( tc ).replaceAll( "(^\\s+|\\s+$)", "" ).indexOf( "=" ) > -1) {
                                            String[] mangXienCoDauB = tachChuoiXien.get( tc ).replaceAll( "(^\\s+|\\s+$)", "" ).split( "=" );
                                            if (mangXienCoDauB.length == 2) {
                                                double getNum = 0;
                                                if (mangXienCoDauB[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                    if (mangXienCoDauB[1].split( "j" ).length == 2) {
                                                        mangXienCoDauB[1] = mangXienCoDauB[1].replace( "j", "." );
                                                    }
                                                    if (mangXienCoDauB[1].indexOf( "trieu" ) > -1) {
                                                        getNum = Double.parseDouble( mangXienCoDauB[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                    } else if (mangXienCoDauB[1].indexOf( "d" ) > -1) {
                                                        getNum = Double.parseDouble( mangXienCoDauB[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 10;
                                                    } else {
                                                        getNum = Double.parseDouble( mangXienCoDauB[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                    }
                                                }
                                                String chuoiTachSoXienCoDauB = controller.tachChuoiXien( mangXienCoDauB[0], limitNumberBaCang );
                                                String[] mangValXien = chuoiTachSoXienCoDauB.replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                switch (kieuXien) {
                                                    case "si2":
                                                    case "sa2":
                                                        if (mangValXien.length == 2) {
                                                            Double newGetNumSi2 = Math.round( getNum * 2 / mangValXien.length * 100.0 ) / 100.0;
                                                            for (int valx = 0; valx < mangValXien.length; valx = valx + 2) {
                                                                int valx1 = valx;
                                                                int valx2 = valx + 1;
                                                                String trungxi2 = "";
                                                                for (int ixi = 0; ixi < compareLo.size(); ixi++) {
                                                                    if (mangValXien[valx1].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi2.indexOf( "1" ) == -1) {
                                                                        trungxi2 += "1";
                                                                        continue;
                                                                    } else if (mangValXien[valx2].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi2.indexOf( "2" ) == -1) {
                                                                        trungxi2 += "2";
                                                                        continue;
                                                                    }
                                                                }
                                                                if (trungxi2.length() == 2) {

                                                                    xulydanhXienKieu( newGetNumSi2, hsx2, thuongxien2, idSmsInt, dongiaId, mangValXien[valx1] + " " + mangValXien[valx2],
                                                                            1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuXien );

                                                                } else {

                                                                    xulydanhXienKieu( newGetNumSi2, hsx2, thuongxien2, idSmsInt, dongiaId, mangValXien[valx1] + " " + mangValXien[valx2],
                                                                            0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuXien );

                                                                }
                                                                if (limitNumber.contains( mangValXien[valx1] )) {
                                                                    error += " " + mangValXien[valx1];
                                                                } else {
                                                                    error += "<font color=\"RED\"> " + mangValXien[valx1] + " </font>";
                                                                }
                                                                if (limitNumber.contains( mangValXien[valx2] )) {
                                                                    error += " " + mangValXien[valx2];
                                                                } else {
                                                                    error += "<font color=\"RED\"> " + mangValXien[valx2] + " </font>";
                                                                }
                                                            }
                                                        } else {
                                                            if (mangValXien.length > 2) {
                                                                for (int valx = 0; valx < mangValXien.length - 1; valx++) {
                                                                    error += " " + mangValXien[valx];
                                                                }
                                                                int endArr = mangValXien.length - 1;
                                                                error += "<font color=\"RED\"> " + mangValXien[endArr] + " </font>"; // bao do vi thua gia tri
                                                            } else {
                                                                for (int valx = 0; valx < mangValXien.length - 1; valx++) {
                                                                    error += "<font color=\"RED\"> " + mangValXien[valx] + " </font>";
                                                                }
                                                            }

                                                        }
                                                        break;
                                                    case "si3":
                                                    case "sa3":
                                                        if (mangValXien.length == 3) {
                                                            ArrayList<String> checkXien = new ArrayList<>();
                                                            Double newGetNumSi3 = Math.round( getNum * 3 / mangValXien.length * 100.0 ) / 100.0;
                                                            for (int valx = 0; valx < mangValXien.length; valx = valx + 3) {
                                                                int valx1 = valx;
                                                                int valx2 = valx + 1;
                                                                int valx3 = valx + 2;
                                                                String trungxi3 = "";
                                                                for (int ixi = 0; ixi < compareLo.size(); ixi++) {
                                                                    if (mangValXien[valx1].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi3.indexOf( "1" ) == -1) {
                                                                        trungxi3 += "1";
                                                                        continue;
                                                                    } else if (mangValXien[valx2].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi3.indexOf( "2" ) == -1) {
                                                                        trungxi3 += "2";
                                                                        continue;
                                                                    } else if (mangValXien[valx3].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi3.indexOf( "3" ) == -1) {
                                                                        trungxi3 += "3";
                                                                        continue;
                                                                    }
                                                                }
                                                                if (trungxi3.length() == 3) {

                                                                    xulydanhXienKieu( newGetNumSi3, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                            mangValXien[valx1] + " " + mangValXien[valx2] + " " + mangValXien[valx3],
                                                                            1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuXien );

                                                                } else {

                                                                    xulydanhXienKieu( newGetNumSi3, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                            mangValXien[valx1] + " " + mangValXien[valx2] + " " + mangValXien[valx3],
                                                                            0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuXien );

                                                                }
                                                                if (limitNumber.contains( mangValXien[valx1] )) {
                                                                    error += " " + mangValXien[valx1];
                                                                } else {
                                                                    error += "<font color=\"RED\"> " + mangValXien[valx1] + " </font>";
                                                                }
                                                                if (limitNumber.contains( mangValXien[valx2] )) {
                                                                    error += " " + mangValXien[valx2];
                                                                } else {
                                                                    error += "<font color=\"RED\"> " + mangValXien[valx2] + " </font>";
                                                                }
                                                                if (limitNumber.contains( mangValXien[valx3] )) {
                                                                    error += " " + mangValXien[valx3];
                                                                } else {
                                                                    error += "<font color=\"RED\"> " + mangValXien[valx3] + " </font>";
                                                                }
                                                            }
                                                        } else {
                                                            int sodux3 = mangValXien.length % 3;
                                                            if (mangValXien.length > 3) {
                                                                for (int valx = 0; valx < mangValXien.length - sodux3; valx++) {
                                                                    error += " " + mangValXien[valx];
                                                                }
                                                                if (sodux3 == 1) {
                                                                    int endArr = mangValXien.length - 1;
                                                                    error += "<font color=\"RED\"> " + mangValXien[endArr] + " </font>"; // bao do vi thua gia tri
                                                                } else {
                                                                    int endArr1 = mangValXien.length - 1;
                                                                    int endArr2 = mangValXien.length - 2;
                                                                    error += "<font color=\"RED\"> " + mangValXien[endArr1] + " " + mangValXien[endArr2] + " </font>"; // bao do vi thua gia tri
                                                                }
                                                            } else {
                                                                for (int valx = 0; valx < mangValXien.length - sodux3; valx++) {
                                                                    error += "<font color=\"RED\"> " + mangValXien[valx] + "</font>";
                                                                }
                                                            }
                                                        }
                                                        break;
                                                    case "si4":
                                                    case "sa4":
                                                        if (mangValXien.length == 4) {
                                                            ArrayList<String> checkXien = new ArrayList<>();
                                                            Double newGetNumSi4 = Math.round( getNum * 4 / mangValXien.length * 100.0 ) / 100.0;
                                                            for (int valx = 0; valx < mangValXien.length; valx = valx + 4) {
                                                                int valx1 = valx;
                                                                int valx2 = valx + 1;
                                                                int valx3 = valx + 2;
                                                                int valx4 = valx + 3;
                                                                String trungxi4 = "";
                                                                for (int ixi = 0; ixi < compareLo.size(); ixi++) {
                                                                    if (mangValXien[valx1].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi4.indexOf( "1" ) == -1) {
                                                                        trungxi4 += "1";
                                                                        continue;
                                                                    } else if (mangValXien[valx2].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi4.indexOf( "2" ) == -1) {
                                                                        trungxi4 += "2";
                                                                        continue;
                                                                    } else if (mangValXien[valx3].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi4.indexOf( "3" ) == -1) {
                                                                        trungxi4 += "3";
                                                                        continue;
                                                                    } else if (mangValXien[valx3].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi4.indexOf( "4" ) == -1) {
                                                                        trungxi4 += "4";
                                                                        continue;
                                                                    }
                                                                }
                                                                if (trungxi4.length() == 4) {

                                                                    xulydanhXienKieu( newGetNumSi4, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                                            mangValXien[valx1] + " " + mangValXien[valx2] + " " + mangValXien[valx3] + " " + mangValXien[valx4],
                                                                            1, smsType, listDonGia[1], "xien4", listDonGia[0], dataSoLieuDate,kieuXien );

                                                                } else {

                                                                    xulydanhXienKieu( newGetNumSi4, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                                            mangValXien[valx1] + " " + mangValXien[valx2] + " " + mangValXien[valx3] + " " + mangValXien[valx4],
                                                                            0, smsType, listDonGia[1], "xien4", listDonGia[0], dataSoLieuDate,kieuXien );

                                                                }
                                                                if (limitNumber.contains( mangValXien[valx1] )) {
                                                                    error += " " + mangValXien[valx1];
                                                                } else {
                                                                    error += "<font color=\"RED\"> " + mangValXien[valx1] + " </font>";
                                                                }
                                                                if (limitNumber.contains( mangValXien[valx2] )) {
                                                                    error += " " + mangValXien[valx2];
                                                                } else {
                                                                    error += "<font color=\"RED\"> " + mangValXien[valx2] + " </font>";
                                                                }
                                                                if (limitNumber.contains( mangValXien[valx3] )) {
                                                                    error += " " + mangValXien[valx3];
                                                                } else {
                                                                    error += "<font color=\"RED\"> " + mangValXien[valx3] + " </font>";
                                                                }
                                                                if (limitNumber.contains( mangValXien[valx4] )) {
                                                                    error += " " + mangValXien[valx4];
                                                                } else {
                                                                    error += "<font color=\"RED\"> " + mangValXien[valx4] + " </font>";
                                                                }
                                                            }
                                                        } else {
                                                            int sodux4 = mangValXien.length % 4;
                                                            if (mangValXien.length > 4) {
                                                                for (int valx = 0; valx < mangValXien.length - sodux4; valx++) {
                                                                    error += " " + mangValXien[valx];
                                                                }
                                                                if (sodux4 == 1) {
                                                                    int endArr = mangValXien.length - 1;
                                                                    error += "<font color=\"RED\"> " + mangValXien[endArr] + " </font>"; // bao do vi thua gia tri
                                                                } else if (sodux4 == 2) {
                                                                    int endArr1 = mangValXien.length - 1;
                                                                    int endArr2 = mangValXien.length - 2;
                                                                    error += "<font color=\"RED\"> " + mangValXien[endArr1] + " " + mangValXien[endArr2] + " </font>"; // bao do vi thua gia tri
                                                                } else {
                                                                    int endArr1 = mangValXien.length - 1;
                                                                    int endArr2 = mangValXien.length - 2;
                                                                    int endArr3 = mangValXien.length - 3;
                                                                    error += "<font color=\"RED\"> " + mangValXien[endArr1] + " " + mangValXien[endArr2] + " " + mangValXien[endArr3] + " </font>"; // bao do vi thua gia tri
                                                                }
                                                            } else {
                                                                for (int valx = 0; valx < mangValXien.length - sodux4; valx++) {
                                                                    error += "<font color=\"RED\"> " + mangValXien[valx] + " </font>";
                                                                }
                                                            }

                                                        }
                                                        break;
                                                    case "si ":
                                                    case "sa ":
                                                        if (mangValXien.length <= 4 && mangValXien.length >= 2) {
                                                            String boSoXien = "";
                                                            ArrayList<String> checkXien = new ArrayList<>();
                                                            ArrayList<String> arrCheckDuplicateXienDaub = new ArrayList<>();
                                                            for (int allX = 0; allX < mangValXien.length; allX++) {
                                                                if (limitNumber.contains( mangValXien[allX] )) {
                                                                    try {
                                                                    /* danh le*/
                                                                        error += " " + mangValXien[allX];
                                                                        boSoXien += " " + mangValXien[allX];
                                                                        int trung = Collections.frequency( compareLo, mangValXien[allX].replaceAll( "(^\\s+|\\s+$)", "" ) );
                                                                        if (trung > 0) {
                                                                            arrCheckDuplicateXienDaub.add( mangValXien[allX].replaceAll( "(^\\s+|\\s+$)", "" ) );
                                                                            if (Collections.frequency( arrCheckDuplicateXienDaub, mangValXien[allX].replaceAll( "(^\\s+|\\s+$)", "" ) ) <=
                                                                                    Collections.frequency( compareLo, mangValXien[allX].replaceAll( "(^\\s+|\\s+$)", "" ) )) {
                                                                                checkXien.add( "true" );
                                                                            } else {
                                                                                checkXien.add( "false" );
                                                                            }
                                                                        } else {
                                                                            checkXien.add( "false" );
                                                                        }
                                                                    } catch (NumberFormatException e) {
                                                                        error += "<font color=\"RED\"> " + mangValXien[allX] + "</font>";
                                                                    }
                                                                } else {
                                                                    error += "<font color=\"RED\"> " + mangValXien[allX] + "</font>";
                                                                }
                                                            }

                                                            xulydanhXien( checkXien, getNum, hsx2, thuongxien2, hsx3
                                                                    , thuongxien3, hsx4, thuongxien4, idSmsInt, dongiaId, boSoXien, smsType
                                                                    , listDonGia[1], "xien", listDonGia[0], dataSoLieuDate,kieuXien );

                                                        } else if (mangValXien.length > 4) {
                                                            error += mangValXien[0] + " " + mangValXien[1] + " " + mangValXien[2] + " " +
                                                                    mangValXien[3] + " ";
                                                            for (int allX = 4; allX < mangValXien.length; allX++) {
                                                                error += "<font color=\"RED\"> " + mangValXien[allX] + " </font>";
                                                            }
                                                        } else {
                                                            for (int allX = 0; allX < mangValXien.length; allX++) {
                                                                error += "<font color=\"RED\"> " + mangValXien[allX] + " </font>";
                                                            }
                                                        }
                                                        break;
                                                    default:
                                                        error += "<font color=\"red\" >" + tachChuoiXien.get( tc ).replaceAll( "(^\\s+|\\s+$)", "" ) + "</font>";
                                                        break;
                                                }
                                                String[] checkGiatriMangXien = mangXienCoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                                if (checkGiatriMangXien.length > 1) {
                                                    error += " = " + "<font color=\"RED\">" + checkGiatriMangXien[0] + " </font>";
                                                    for (int val = 1; val < checkGiatriMangXien.length; val++) {
                                                        error += checkGiatriMangXien[val] + " ";
                                                    }
                                                } else {
                                                    if (mangXienCoDauB[1].indexOf( "n" ) > -1 &&
                                                            mangXienCoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                        error += " = " + mangXienCoDauB[1].replace( ".", "j" ) + " ";
                                                    } else if (mangXienCoDauB[1].indexOf( "d" ) > -1 &&
                                                            mangXienCoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                        error += " = " + mangXienCoDauB[1].replace( ".", "j" ) + " ";
                                                    } else if (mangXienCoDauB[1].indexOf( "k" ) > -1 &&
                                                            mangXienCoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                        error += " = " + mangXienCoDauB[1].replace( ".", "j" ) + " ";
                                                    } else if (mangXienCoDauB[1].indexOf( "trieu" ) > -1 &&
                                                            mangXienCoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 5) {
                                                        error += " = " + mangXienCoDauB[1].replace( ".", "j" ) + " ";
                                                    } else if (mangXienCoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 0) {
                                                        error += " = " + mangXienCoDauB[1].replace( ".", "j" ) + " ";
                                                    //} else if (mangXienCoDauB[1].indexOf( "N1c" ) > -1 && mangXienCoDauB[1].replaceAll( "(^\\s+|\\s+$)", "" ).length() == 3) {
                                                    //    error += " = " + mangXienCoDauB[1].replace( ".", "j" ) + " ";
                                                    } else {
                                                        error += " = " + "<font color=\"RED\">" + mangXienCoDauB[1].replace( ".", "j" ) + " </font>";
                                                    }
                                                }
                                            } else if (mangXienCoDauB.length > 2) {
                                                error += mangXienCoDauB[0] + " =" + mangXienCoDauB[1];
                                                for (int si = 2; si < mangXienCoDauB.length; si++) {
                                                    error += "<font color=\"RED\">= " + mangXienCoDauB[si] + " </font>";
                                                }
                                            } else {
                                                error += "<font color=\"RED\"> " + tachChuoiXien.get( tc ) + " </font>";
                                            }
                                        } else {
                                            // xu ly xien khong co x
                                            String[] mangXienKhongX = tachChuoiXien.get( tc ).replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                            //   if (mangXienKhongX.length == 1) {
                                            int endArr = mangXienKhongX.length - 1;
                                            double getNum = 0;
                                            boolean checkGetNum = false;
                                            if (tachChuoiXien.get( tc ).replaceAll( "(^\\s+|\\s+$)", "" ).indexOf( "N1c" ) > -1) {
                                                if (mangXienKhongX[endArr].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                    if (mangXienKhongX[endArr].split( "j" ).length == 2) {
                                                        mangXienKhongX[endArr] = mangXienKhongX[1].replace( "j", "." );
                                                    }
                                                    if (mangXienKhongX[endArr].indexOf( "trieu" ) > -1) {
                                                        getNum = Double.parseDouble( mangXienKhongX[endArr].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                    } else if (mangXienKhongX[endArr].indexOf( "d" ) > -1) {
                                                        getNum = Double.parseDouble( mangXienKhongX[endArr].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 10;
                                                    } else {
                                                        getNum = Double.parseDouble( mangXienKhongX[endArr].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                    }
                                                }
                                            } else {
                                                if (mangXienKhongX.length >= 2) {
                                                    if (mangXienKhongX[endArr].replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                        if (mangXienKhongX[endArr].split( "j" ).length == 2) {
                                                            mangXienKhongX[endArr] = mangXienKhongX[endArr].replace( "j", "." );
                                                        }
                                                        if (mangXienKhongX[endArr].indexOf( "trieu" ) > -1) {
                                                            getNum = Double.parseDouble( mangXienKhongX[endArr].replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                        } else if (mangXienKhongX[endArr].indexOf( "d" ) > -1) {
                                                            getNum = Double.parseDouble( mangXienKhongX[endArr].replaceAll( "[^\\d.]", "" ) ) * 10;
                                                        } else {
                                                            getNum = Double.parseDouble( mangXienKhongX[endArr].replaceAll( "[^\\d.]", "" ) );
                                                        }
                                                        checkGetNum = true;
                                                    }
                                                }
                                            }
                                            switch (kieuXien) {
                                                case "si2":
                                                case "sa2":
                                                    if ((mangXienKhongX.length - 1) % 2 == 0) {
                                                        // -1 bo gia tri cuoi cung don vi diem danh
                                                        for (int valx = 0; valx < mangXienKhongX.length - 1; valx = valx + 2) {
                                                            int valx1 = valx;
                                                            int valx2 = valx + 1;
                                                            String trungxi2 = "";
                                                            for (int ixi = 0; ixi < compareLo.size(); ixi++) {
                                                                if (mangXienKhongX[valx1].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi2.indexOf( "1" ) == -1) {
                                                                    trungxi2 += "1";

                                                                    continue;
                                                                } else if (mangXienKhongX[valx2].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi2.indexOf( "2" ) == -1) {
                                                                    trungxi2 += "2";
                                                                    continue;
                                                                }
                                                            }
                                                            if (trungxi2.length() == 2) {

                                                                xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienKhongX[valx1] + " " + mangXienKhongX[valx2],
                                                                        1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuXien );

                                                            } else {

                                                                xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienKhongX[valx1] + " " + mangXienKhongX[valx2],
                                                                        0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuXien );

                                                            }
                                                            if (limitNumber.contains( mangXienKhongX[valx1] )) {
                                                                error += " " + mangXienKhongX[valx1];
                                                            } else {
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[valx1] + " </font>";
                                                            }
                                                            if (limitNumber.contains( mangXienKhongX[valx2] )) {
                                                                error += " " + mangXienKhongX[valx2];
                                                            } else {
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[valx2] + " </font>";
                                                            }
                                                        }
                                                    } else {
                                                        if (mangXienKhongX.length > 2) {
                                                            for (int valx = 0; valx < mangXienKhongX.length - 2; valx++) {
                                                                error += " " + mangXienKhongX[valx];
                                                            }
                                                            int endArr1 = mangXienKhongX.length - 2;
                                                            // lay vi tri gan cuoi vi chuoi bat dau la so 0 va k co so x
                                                            // chuoi bat dau tu 0 nen - 2 la vi tri sau cuoi cung
                                                            error += "<font color=\"RED\"> " + mangXienKhongX[endArr1] + " </font>"; // bao do vi thua gia tri
                                                        } else {
                                                            for (int valx = 0; valx < mangXienKhongX.length - 2; valx++) {
                                                                error += " " + mangXienKhongX[valx];
                                                            }
                                                        }

                                                    }
                                                    break;
                                                case "si3":
                                                case "sa3":
                                                    if ((mangXienKhongX.length - 1) % 3 == 0) {
                                                        ArrayList<String> checkXien = new ArrayList<>();
                                                        for (int valx = 0; valx < mangXienKhongX.length - 1; valx = valx + 3) {
                                                            int valx1 = valx;
                                                            int valx2 = valx + 1;
                                                            int valx3 = valx + 2;
                                                            if (checkGetNum) {
                                                                getNum = getNum / ((mangXienKhongX.length - 1) / 3);
                                                            }
                                                            String trungxi3 = "";
                                                            for (int ixi = 0; ixi < compareLo.size(); ixi++) {
                                                                if (mangXienKhongX[valx1].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi3.indexOf( "1" ) == -1) {
                                                                    trungxi3 += "1";
                                                                    continue;
                                                                } else if (mangXienKhongX[valx2].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi3.indexOf( "2" ) == -1) {
                                                                    trungxi3 += "2";
                                                                    continue;
                                                                } else if (mangXienKhongX[valx3].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi3.indexOf( "3" ) == -1) {
                                                                    trungxi3 += "3";
                                                                    continue;
                                                                }
                                                            }

                                                            if (trungxi3.length() == 3) {

                                                                xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                        mangXienKhongX[valx1] + " " + mangXienKhongX[valx2] + " " + mangXienKhongX[valx3],
                                                                        1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuXien );

                                                            } else {

                                                                xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                        mangXienKhongX[valx1] + " " + mangXienKhongX[valx2] + " " + mangXienKhongX[valx3],
                                                                        0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuXien );

                                                            }
                                                            if (limitNumber.contains( mangXienKhongX[valx1] )) {
                                                                error += " " + mangXienKhongX[valx1];
                                                            } else {
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[valx1] + " </font>";
                                                            }
                                                            if (limitNumber.contains( mangXienKhongX[valx2] )) {
                                                                error += " " + mangXienKhongX[valx2];
                                                            } else {
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[valx2] + " </font>";
                                                            }
                                                            if (limitNumber.contains( mangXienKhongX[valx3] )) {
                                                                error += " " + mangXienKhongX[valx3];
                                                            } else {
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[valx3] + " </font>";
                                                            }
                                                        }
                                                    } else {
                                                        int sodux3 = (mangXienKhongX.length - 1) % 3;
                                                        if (mangXienKhongX.length > 3) {
                                                            for (int valx = 0; valx < (mangXienKhongX.length - 1) - sodux3; valx++) {
                                                                error += " " + mangXienKhongX[valx];
                                                            }
                                                            if (sodux3 == 1) {
                                                                int endArr1 = mangXienKhongX.length - 2;
                                                                // lay vi tri gan cuoi vi chuoi bang dau bang 0
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[endArr1] + " </font>"; // bao do vi thua gia tri
                                                            } else {
                                                                int endArr1 = mangXienKhongX.length - 2; //nhu tren
                                                                int endArr2 = mangXienKhongX.length - 3; // nhu tren
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[endArr1] + " " + mangXienKhongX[endArr2] + " </font>"; // bao do vi thua gia tri
                                                            }
                                                        } else {
                                                            for (int valx = 0; valx < mangXienKhongX.length - 1; valx++) {
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[valx] + "</font>";
                                                            }
                                                        }
                                                    }
                                                    break;
                                                case "si4":
                                                case "sa4":
                                                    if ((mangXienKhongX.length - 1) % 4 == 0) {
                                                        for (int valx = 0; valx < mangXienKhongX.length - 1; valx = valx + 4) {
                                                            int valx1 = valx;
                                                            int valx2 = valx + 1;
                                                            int valx3 = valx + 2;
                                                            int valx4 = valx + 3;
                                                            if (checkGetNum) {
                                                                getNum = getNum / ((mangXienKhongX.length - 1) / 4);
                                                            }
                                                            String trungxi4 = "";
                                                            for (int ixi = 0; ixi < compareLo.size(); ixi++) {
                                                                if (mangXienKhongX[valx1].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi4.indexOf( "1" ) == -1) {
                                                                    trungxi4 += "1";
                                                                    continue;
                                                                } else if (mangXienKhongX[valx2].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi4.indexOf( "2" ) == -1) {
                                                                    trungxi4 += "2";
                                                                    continue;
                                                                } else if (mangXienKhongX[valx3].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi4.indexOf( "3" ) == -1) {
                                                                    trungxi4 += "3";
                                                                    continue;
                                                                } else if (mangXienKhongX[valx4].replaceAll( "(^\\s+|\\s+$)", "" ).equals( compareLo.get( ixi ) ) && trungxi4.indexOf( "4" ) == -1) {
                                                                    trungxi4 += "4";
                                                                    continue;
                                                                }
                                                            }

                                                            if (trungxi4.length() == 4) {

                                                                xulydanhXienKieu( getNum, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                                        mangXienKhongX[valx1] + " " + mangXienKhongX[valx2] + " " + mangXienKhongX[valx3] + " " + mangXienKhongX[valx4],
                                                                        1, smsType, listDonGia[1], "xien4", listDonGia[0], dataSoLieuDate,kieuXien );

                                                            } else {

                                                                xulydanhXienKieu( getNum, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                                        mangXienKhongX[valx1] + " " + mangXienKhongX[valx2] + " " + mangXienKhongX[valx3] + " " + mangXienKhongX[valx4],
                                                                        0, smsType, listDonGia[1], "xien4", listDonGia[0], dataSoLieuDate,kieuXien );

                                                            }
                                                            if (limitNumber.contains( mangXienKhongX[valx1] )) {
                                                                error += " " + mangXienKhongX[valx1];
                                                            } else {
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[valx1] + " </font>";
                                                            }
                                                            if (limitNumber.contains( mangXienKhongX[valx2] )) {
                                                                error += " " + mangXienKhongX[valx2];
                                                            } else {
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[valx2] + " </font>";
                                                            }
                                                            if (limitNumber.contains( mangXienKhongX[valx3] )) {
                                                                error += " " + mangXienKhongX[valx3];
                                                            } else {
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[valx3] + " </font>";
                                                            }
                                                            if (limitNumber.contains( mangXienKhongX[valx4] )) {
                                                                error += " " + mangXienKhongX[valx4];
                                                            } else {
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[valx4] + " </font>";
                                                            }
                                                        }
                                                    } else {
                                                        int sodux4 = mangXienKhongX.length % 4;
                                                        if (mangXienKhongX.length > 4) {
                                                            for (int valx = 0; valx < (mangXienKhongX.length - 1) - sodux4; valx++) {
                                                                error += " " + mangXienKhongX[valx];
                                                            }
                                                            if (sodux4 == 1) {
                                                                int endArr1 = mangXienKhongX.length - 2; // lay tu vi tri gan cuoi vi chuoi bang dau tu 0
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[endArr1] + " </font>"; // bao do vi thua gia tri
                                                            } else if (sodux4 == 2) {
                                                                int endArr1 = mangXienKhongX.length - 2; // lay tu vi tri gan cuoi vi chuoi bang dau tu 0
                                                                int endArr2 = mangXienKhongX.length - 3; //nt
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[endArr2] + " " + mangXienKhongX[endArr1] + " </font>"; // bao do vi thua gia tri
                                                            } else {
                                                                int endArr1 = mangXienKhongX.length - 2; // nhu tren
                                                                int endArr2 = mangXienKhongX.length - 3; // nhu tren
                                                                int endArr3 = mangXienKhongX.length - 4; // nhu tren
                                                                error += "<font color=\"RED\">" + mangXienKhongX[endArr3] + " " + mangXienKhongX[endArr2] + " " + mangXienKhongX[endArr1] + " </font>"; // bao do vi thua gia tri
                                                            }
                                                        } else {
                                                            for (int valx = 0; valx < mangXienKhongX.length - 1; valx++) {
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[valx] + "</font>";
                                                            }
                                                        }
                                                    }
                                                    break;
                                                case "si ":
                                                case "sa ":
                                                    if (mangXienKhongX.length <= 5 && mangXienKhongX.length >= 3) {
                                                        String boSoXien = "";
                                                        ArrayList<String> checkXien = new ArrayList<>();
                                                        ArrayList<String> arrCheckDuplicateXienKX = new ArrayList<>();
                                                        for (int allX = 0; allX < mangXienKhongX.length - 1; allX++) {
                                                            if (limitNumber.contains( mangXienKhongX[allX] )) {
                                                                try {
                                                            /* danh le*/
                                                                    error += " " + mangXienKhongX[allX];
                                                                    boSoXien += " " + mangXienKhongX[allX];
                                                                    int trung = Collections.frequency( compareLo, mangXienKhongX[allX] );
                                                                    if (trung > 0) {
                                                                        arrCheckDuplicateXienKX.add( mangXienKhongX[allX].replaceAll( "(^\\s+|\\s+$)", "" ) );
                                                                        if (Collections.frequency( arrCheckDuplicateXienKX, mangXienKhongX[allX].replaceAll( "(^\\s+|\\s+$)", "" ) ) <=
                                                                                Collections.frequency( compareLo, mangXienKhongX[allX].replaceAll( "(^\\s+|\\s+$)", "" ) )) {
                                                                            checkXien.add( "true" );
                                                                        } else {
                                                                            checkXien.add( "false" );
                                                                        }
                                                                    } else {
                                                                        checkXien.add( "false" );
                                                                    }
                                                                } catch (NumberFormatException e) {
                                                                    error += "<font color=\"RED\"> " + mangXienKhongX[allX] + "</font>";
                                                                }
                                                            } else {
                                                                error += "<font color=\"RED\"> " + mangXienKhongX[allX] + "</font>";
                                                            }
                                                        }

                                                        xulydanhXien( checkXien, getNum, hsx2, thuongxien2, hsx3
                                                                , thuongxien3, hsx4, thuongxien4, idSmsInt, dongiaId, boSoXien, smsType
                                                                , listDonGia[1], "xien", listDonGia[0], dataSoLieuDate,kieuXien );

                                                    } else if (mangXienKhongX.length > 5) {
                                                        error += mangXienKhongX[0] + " " + mangXienKhongX[1] + " " + mangXienKhongX[2] + " " +
                                                                mangXienKhongX[3] + " ";
                                                        for (int allX = 4; allX < mangXienKhongX.length - 1; allX++) {
                                                            error += "<font color=\"RED\"> " + mangXienKhongX[allX] + " </font>";
                                                        }
                                                    } else {
                                                        for (int allX = 0; allX < mangXienKhongX.length - 1; allX++) {
                                                            error += "<font color=\"RED\"> " + mangXienKhongX[allX] + " </font>";
                                                        }
                                                    }

                                                    break;
                                                default:
                                                    String valDefault = "";
                                                    for (int allX = 1; allX < mangXienKhongX.length - 1; allX++) {
                                                        valDefault += mangXienKhongX[allX];
                                                    }
                                                    error += "<font color=\"red\" > " + valDefault + "</font>";
                                                    break;
                                            }
                                            String valXienEnd = mangXienKhongX[mangXienKhongX.length - 1];
                                            if (mangXienKhongX.length == 1) {
                                                error += "<font color=\"RED\"> " + valXienEnd + " </font>";
                                            } else if (valXienEnd.indexOf( "n" ) > -1 &&
                                                    valXienEnd.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                    valXienEnd.replaceAll( "[^\\d.]", "" ).length() >= 1
                                                    ) {
                                                error += " " + valXienEnd.replace( ".", "j" ) + " ";
                                            } else if (valXienEnd.indexOf( "k" ) > -1 &&
                                                    valXienEnd.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                    valXienEnd.replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                error += " " + valXienEnd.replace( ".", "j" ) + " ";
                                            } else if (valXienEnd.indexOf( "d" ) > -1 &&
                                                    valXienEnd.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                    valXienEnd.replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                error += " " + valXienEnd.replace( ".", "j" ) + " ";
                                            } else if (valXienEnd.indexOf( "trieu" ) > -1 &&
                                                    valXienEnd.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 5 &&
                                                    valXienEnd.replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                error += " " + valXienEnd.replace( ".", "j" ) + " ";
                                            } else if (valXienEnd.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 0 &&
                                                    valXienEnd.replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                error += " " + valXienEnd.replace( ".", "j" ) + " ";
                                            //} else if (valXienEnd.indexOf( "N1c" ) > -1 &&
                                            //        valXienEnd.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 2 &&
                                            //        valXienEnd.replaceAll( "[^\\d.]", "" ).length() >= 2) {
                                            //    error += " " + valXienEnd.replace( ".", "j" ) + " ";
                                            } else {
                                                error += "<font color=\"RED\"> " + valXienEnd.replace( ".", "j" ) + " </font>";
                                            }
                                        }
                                    }
                                }
                            } else {
                                error += "<font color=\"red\" >" + message[i] + " </font>";
                                // gia tri mang chuoi dai khong toi 3
                            }
                            break;
                        case "sq":
                        case "qa":
                            error += kieuchoi+ " ";
                            if (kieuchoi.equals( "qa" )) {
                                compareLo = compareLoDau;
                            } else {
                                compareLo = compareLo2;
                            }
                            String chuoiXienQuay = message[i].substring( 2, message[i].length() ).replaceAll( "(^\\s+|\\s+$)", "" ).
                                    replace( "n1c", "N1c JAVASTR" ).replace( "n", "n JAVASTR" ).
                                    replace( "d", "d JAVASTR" ).replace( "k", "k JAVASTR" ).
                                    replace( "trieu", "trieu JAVASTR" );
                            String[] mangXienQuay2 = chuoiXienQuay.split( "JAVASTR" );
                            ArrayList<String> tachChuoiXq = controller.tachchuoi( mangXienQuay2 );
                            for (int xqu = 0; xqu < tachChuoiXq.size(); xqu++) {
                                if (!tachChuoiXq.get( xqu ).equals( "" )) {
                                    if (tachChuoiXq.get( xqu ).indexOf( "x" ) > -1) { // kiem tra co dau x trong chuoi khong
                                        String[] mangXienQuayCoX = tachChuoiXq.get( xqu ).replaceAll( "(^\\s+|\\s+$)", "" ).split( "x" );
                                        if (mangXienQuayCoX.length == 2) {
                                            double getNum = 0;
                                            if (mangXienQuayCoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                if (mangXienQuayCoX[1].split( "j" ).length == 2) {
                                                    mangXienQuayCoX[1] = mangXienQuayCoX[1].replace( "j", "." );
                                                }
                                                if (mangXienQuayCoX[1].indexOf( "trieu" ) > -1) {
                                                    getNum = Double.parseDouble( mangXienQuayCoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                } else if (mangXienQuayCoX[1].indexOf( "d" ) > -1) {
                                                    getNum = Double.parseDouble( mangXienQuayCoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 10;
                                                } else {
                                                    getNum = Double.parseDouble( mangXienQuayCoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                }
                                            }
                                            String chuoiTachSoXQCoX = controller.tachChuoiXien( mangXienQuayCoX[0], limitNumberBaCang );
                                            String[] valMangXienQuayCoX = chuoiTachSoXQCoX.replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                            switch (valMangXienQuayCoX.length) {
                                                case 4:
                                                    int trung1 = Collections.frequency( compareLo, valMangXienQuayCoX[0] );
                                                    int trung2 = Collections.frequency( compareLo, valMangXienQuayCoX[1] );
                                                    int trung3 = Collections.frequency( compareLo, valMangXienQuayCoX[2] );
                                                    int trung4 = Collections.frequency( compareLo, valMangXienQuayCoX[3] );
                                                    // 1-2-3-4
                                                    if (trung1 > 0 && trung2 > 0 && trung3 > 0 && trung4 > 0) {

                                                        xulydanhXienKieu( getNum, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien4", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien4", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    }
                                                    // 1-2-3
                                                    if (trung1 > 0 && trung2 > 0 && trung3 > 0) {

                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    }
                                                    //1-2-4
                                                    if (trung1 > 0 && trung2 > 0 && trung4 > 0) {

                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    }
                                                    //1-3-4
                                                    if (trung1 > 0 && trung3 > 0 && trung4 > 0) {

                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    }
                                                    //2-3-4
                                                    if (trung2 > 0 && trung3 > 0 && trung4 > 0) {

                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    }
                                                    // 1-2
                                                    if (trung1 > 0 && trung2 > 0) {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    }
                                                    // 1-3
                                                    if (trung1 > 0 && trung3 > 0) {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    }
                                                    // 1-4
                                                    if (trung1 > 0 && trung4 > 0) {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    }
                                                    // 2-3
                                                    if (trung2 > 0 && trung3 > 0) {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    }
                                                    // 2-4
                                                    if (trung2 > 0 && trung4 > 0) {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    }
                                                    // 3-4
                                                    if (trung3 > 0 && trung4 > 0) {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    }
                                                    if (limitNumber.contains( valMangXienQuayCoX[0] )) {
                                                        error += valMangXienQuayCoX[0] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[0] + " </font>";
                                                    }
                                                    if (limitNumber.contains( valMangXienQuayCoX[1] )) {
                                                        error += valMangXienQuayCoX[1] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[1] + " </font>";
                                                    }

                                                    if (limitNumber.contains( valMangXienQuayCoX[2] )) {
                                                        error += valMangXienQuayCoX[2] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[2] + " </font>";
                                                    }
                                                    if (limitNumber.contains( valMangXienQuayCoX[3] )) {
                                                        error += valMangXienQuayCoX[3] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[3] + " </font>";
                                                    }
                                                    break;
                                                case 3:
                                                    int trungQ1 = Collections.frequency( compareLo, valMangXienQuayCoX[0] );
                                                    int trungQ2 = Collections.frequency( compareLo, valMangXienQuayCoX[1] );
                                                    int trungQ3 = Collections.frequency( compareLo, valMangXienQuayCoX[2] );
                                                    // 1-2-3
                                                    if (trungQ1 > 0 && trungQ2 > 0 && trungQ3 > 0) {

                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate, kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate, kieuchoi );

                                                    }
                                                    // 1-2
                                                    if (trungQ1 > 0 && trungQ2 > 0) {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );

                                                    }
                                                    // 1-3
                                                    if (trungQ1 > 0 && trungQ3 > 0) {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );

                                                    }
                                                    // 2-3
                                                    if (trungQ2 > 0 && trungQ3 > 0) {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );

                                                    } else {

                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );

                                                    }
                                                    if (limitNumber.contains( valMangXienQuayCoX[0] )) {
                                                        error += valMangXienQuayCoX[0] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[0] + " </font>";
                                                    }
                                                    if (limitNumber.contains( valMangXienQuayCoX[1] )) {
                                                        error += valMangXienQuayCoX[1] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[1] + " </font>";
                                                    }

                                                    if (limitNumber.contains( valMangXienQuayCoX[2] )) {
                                                        error += valMangXienQuayCoX[2] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[2] + " </font>";
                                                    }
                                                    break;
                                                default:
                                                    error += "<font color=\"RED\">" + mangXienQuayCoX[0] + " </font>";
                                                    break;
                                            }

                                            if (mangXienQuayCoX[1].indexOf( "n" ) > -1 &&
                                                    mangXienQuayCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                error += "x " + mangXienQuayCoX[1].replace( ".", "j" ) + " ";
                                            } else if (mangXienQuayCoX[1].indexOf( "k" ) > -1 &&
                                                    mangXienQuayCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                error += "x " + mangXienQuayCoX[1].replace( ".", "j" ) + " ";
                                            } else if (mangXienQuayCoX[1].indexOf( "d" ) > -1 &&
                                                    mangXienQuayCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                error += "x " + mangXienQuayCoX[1].replace( ".", "j" ) + " ";
                                            } else if (mangXienQuayCoX[1].indexOf( "trieu" ) > -1 &&
                                                    mangXienQuayCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 5) {
                                                error += "x " + mangXienQuayCoX[1].replace( ".", "j" ) + " ";
                                            } else if (mangXienQuayCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 0) {
                                                error += "x " + mangXienQuayCoX[1].replace( ".", "j" ) + " ";
                                            //} else if (mangXienQuayCoX[1].indexOf( "N1c" ) > -1 &&
                                            //        mangXienQuayCoX[1].replaceAll( "[0-9]", "" ).replaceAll( "(^\\s+|\\s+$)", "" ).length() == 2) {
                                            //    error += "x " + mangXienQuayCoX[1].replace( ".", "j" ) + " ";
                                            } else {
                                                error += "x " + "<font color=\"RED\">" + mangXienQuayCoX[1].replace( ".", "j" ) + " </font>";
                                            }

                                        } else {
                                            error += "<font color=\"RED\">" + tachChuoiXq.get( xqu ) + " </font>";
                                        }
                                    } else if (tachChuoiXq.get( xqu ).indexOf( "=" ) > -1) { // kiem tra co dau x trong chuoi khong
                                        String[] mangXienQuayCoX = tachChuoiXq.get( xqu ).replaceAll( "(^\\s+|\\s+$)", "" ).split( "=" );
                                        if (mangXienQuayCoX.length == 1) {
                                            double getNum = 0;
                                            if (mangXienQuayCoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                if (mangXienQuayCoX[1].split( "j" ).length == 2) {
                                                    mangXienQuayCoX[1] = mangXienQuayCoX[1].replace( "j", "." );
                                                }
                                                if (mangXienQuayCoX[1].indexOf( "trieu" ) > -1) {
                                                    getNum = Double.parseDouble( mangXienQuayCoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                } else if (mangXienQuayCoX[1].indexOf( "d" ) > -1) {
                                                    getNum = Double.parseDouble( mangXienQuayCoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 10;
                                                } else {
                                                    getNum = Double.parseDouble( mangXienQuayCoX[1].replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                }
                                            }
                                            String chuoiTachSoXQCoX = controller.tachChuoiXien( mangXienQuayCoX[0], limitNumberBaCang );
                                            String[] valMangXienQuayCoX = chuoiTachSoXQCoX.replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                            switch (valMangXienQuayCoX.length) {
                                                case 4:
                                                    int trung1 = Collections.frequency( compareLo, valMangXienQuayCoX[0] );
                                                    int trung2 = Collections.frequency( compareLo, valMangXienQuayCoX[1] );
                                                    int trung3 = Collections.frequency( compareLo, valMangXienQuayCoX[2] );
                                                    int trung4 = Collections.frequency( compareLo, valMangXienQuayCoX[3] );
                                                    Double newGetNumXq4 = Math.round( getNum / 4 * 100.0 ) / 100.0;
                                                    // 1-2-3-4
                                                    if (trung1 > 0 && trung2 > 0 && trung3 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( newGetNumXq4, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien4", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq4, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien4", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    // 1-2-3
                                                    if (trung1 > 0 && trung2 > 0 && trung3 > 0) {
                                                        xulydanhXienKieu( newGetNumXq4, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq4, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    //1-2-4
                                                    if (trung1 > 0 && trung2 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( newGetNumXq4, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    //1-3-4
                                                    if (trung1 > 0 && trung3 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( newGetNumXq4, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq4, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    //2-3-4
                                                    if (trung2 > 0 && trung3 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( newGetNumXq4, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq4, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    // 1-2
                                                    if (trung1 > 0 && trung2 > 0) {
                                                        xulydanhXienKieu( newGetNumXq4, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq4, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    // 1-3
                                                    if (trung1 > 0 && trung3 > 0) {
                                                        xulydanhXienKieu( newGetNumXq4, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq4, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    // 1-4
                                                    if (trung1 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( newGetNumXq4, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq4, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    // 2-3
                                                    if (trung2 > 0 && trung3 > 0) {
                                                        xulydanhXienKieu( newGetNumXq4, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq4, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    // 2-4
                                                    if (trung2 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( newGetNumXq4, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq4, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    // 3-4
                                                    if (trung3 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( newGetNumXq4, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq4, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[2] + " " + valMangXienQuayCoX[3],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    if (limitNumber.contains( valMangXienQuayCoX[0] )) {
                                                        error += valMangXienQuayCoX[0] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[0] + " </font>";
                                                    }
                                                    if (limitNumber.contains( valMangXienQuayCoX[1] )) {
                                                        error += valMangXienQuayCoX[1] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[1] + " </font>";
                                                    }

                                                    if (limitNumber.contains( valMangXienQuayCoX[2] )) {
                                                        error += valMangXienQuayCoX[2] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[2] + " </font>";
                                                    }
                                                    if (limitNumber.contains( valMangXienQuayCoX[3] )) {
                                                        error += valMangXienQuayCoX[3] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[3] + " </font>";
                                                    }
                                                    break;
                                                case 3:
                                                    int trungQ1 = Collections.frequency( compareLo, valMangXienQuayCoX[0] );
                                                    int trungQ2 = Collections.frequency( compareLo, valMangXienQuayCoX[1] );
                                                    int trungQ3 = Collections.frequency( compareLo, valMangXienQuayCoX[2] );
                                                    double newGetNumXq3 = Math.round( getNum / 3 * 100.0 ) / 100.0;
                                                    // 1-2-3
                                                    if (trungQ1 > 0 && trungQ2 > 0 && trungQ3 > 0) {
                                                        xulydanhXienKieu( newGetNumXq3, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq3, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1] + " " + valMangXienQuayCoX[2],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    // 1-2
                                                    if (trungQ1 > 0 && trungQ2 > 0) {
                                                        xulydanhXienKieu( newGetNumXq3, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq3, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[1],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    // 1-3
                                                    if (trungQ1 > 0 && trungQ3 > 0) {
                                                        xulydanhXienKieu( newGetNumXq3, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq3, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    // 2-3
                                                    if (trungQ2 > 0 && trungQ3 > 0) {
                                                        xulydanhXienKieu( newGetNumXq3, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( newGetNumXq3, hsx2, thuongxien2, idSmsInt, dongiaId, valMangXienQuayCoX[0] + " " + valMangXienQuayCoX[2],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate, kieuchoi );
                                                    }
                                                    if (limitNumber.contains( valMangXienQuayCoX[0] )) {
                                                        error += valMangXienQuayCoX[0] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[0] + " </font>";
                                                    }
                                                    if (limitNumber.contains( valMangXienQuayCoX[1] )) {
                                                        error += valMangXienQuayCoX[1] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[1] + " </font>";
                                                    }

                                                    if (limitNumber.contains( valMangXienQuayCoX[2] )) {
                                                        error += valMangXienQuayCoX[2] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + valMangXienQuayCoX[2] + " </font>";
                                                    }
                                                    break;
                                                default:
                                                    error += "<font color=\"RED\">" + mangXienQuayCoX[0] + " </font>";
                                                    break;
                                            }

                                            if (mangXienQuayCoX[1].indexOf( "n" ) > -1 &&
                                                    mangXienQuayCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                error += "= " + mangXienQuayCoX[1].replace( ".", "j" ) + " ";
                                            } else if (mangXienQuayCoX[1].indexOf( "d" ) > -1 &&
                                                    mangXienQuayCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                error += "= " + mangXienQuayCoX[1].replace( ".", "j" ) + " ";
                                            } else if (mangXienQuayCoX[1].indexOf( "k" ) > -1 &&
                                                    mangXienQuayCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1) {
                                                error += "= " + mangXienQuayCoX[1].replace( ".", "j" ) + " ";
                                            } else if (mangXienQuayCoX[1].indexOf( "trieu" ) > -1 &&
                                                    mangXienQuayCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 5) {
                                                error += "= " + mangXienQuayCoX[1].replace( ".", "j" ) + " ";
                                            } else if (mangXienQuayCoX[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 0) {
                                                error += "= " + mangXienQuayCoX[1].replace( ".", "j" ) + " ";
                                            //} else if (mangXienQuayCoX[1].indexOf( "N1c" ) > -1 &&
                                            //        mangXienQuayCoX[1].replaceAll( "[0-9]", "" ).replaceAll( "(^\\s+|\\s+$)", "" ).length() == 2) {
                                            //    error += "= " + mangXienQuayCoX[1].replace( ".", "j" ) + " ";
                                            } else {
                                                error += "= " + "<font color=\"RED\">" + mangXienQuayCoX[1].replace( ".", "j" ) + " </font>";
                                            }
                                        } else {
                                            error += "<font color=\"RED\">" + tachChuoiXq.get( xqu ) + " </font>";
                                        }
                                    } else {
                                        String[] mangXienQuayKX = tachChuoiXq.get( xqu ).replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                                        if (mangXienQuayKX.length == 1) {
                                            String valueMangXienQuayKX = String.valueOf( mangXienQuayKX[mangXienQuayKX.length - 1] );
                                            double getNum = 0;
                                            if (tachChuoiXq.get( xqu ).indexOf( "N1c" ) > -1) {
                                                if (valueMangXienQuayKX.replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                    if (valueMangXienQuayKX.split( "j" ).length == 2) {
                                                        valueMangXienQuayKX = valueMangXienQuayKX.replace( "j", "." );
                                                    }
                                                    if (valueMangXienQuayKX.indexOf( "trieu" ) > -1) {
                                                        getNum = Double.parseDouble( valueMangXienQuayKX.replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                    } else if (valueMangXienQuayKX.indexOf( "d" ) > -1) {
                                                        getNum = Double.parseDouble( valueMangXienQuayKX.replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) ) * 10;
                                                    } else {
                                                        getNum = Double.parseDouble( valueMangXienQuayKX.replace( "N1c", "" ).replaceAll( "[^\\d.]", "" ) );
                                                    }
                                                }
                                            } else {
                                                if (mangXienQuayKX.length >= 2) {
                                                    if (valueMangXienQuayKX.replaceAll( "[^\\d.]", "" ).length() > 0) {
                                                        if (valueMangXienQuayKX.split( "j" ).length == 2) {
                                                            valueMangXienQuayKX = valueMangXienQuayKX.replace( "j", "." );
                                                        }
                                                        if (valueMangXienQuayKX.indexOf( "trieu" ) > -1) {
                                                            getNum = Double.parseDouble( valueMangXienQuayKX.replaceAll( "[^\\d.]", "" ) ) * 1000;
                                                        } else if (valueMangXienQuayKX.indexOf( "d" ) > -1) {
                                                            getNum = Double.parseDouble( valueMangXienQuayKX.replaceAll( "[^\\d.]", "" ) ) * 10;
                                                        } else {
                                                            getNum = Double.parseDouble( valueMangXienQuayKX.replaceAll( "[^\\d.]", "" ) );
                                                        }
                                                    }
                                                }
                                            }
                                            switch (mangXienQuayKX.length) {
                                                case 5:
                                                    int trung1 = Collections.frequency( compareLo, mangXienQuayKX[0] );
                                                    int trung2 = Collections.frequency( compareLo, mangXienQuayKX[1] );
                                                    int trung3 = Collections.frequency( compareLo, mangXienQuayKX[2] );
                                                    int trung4 = Collections.frequency( compareLo, mangXienQuayKX[3] );
                                                    // 1-2-3-4
                                                    if (trung1 > 0 && trung2 > 0 && trung3 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( getNum, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                                mangXienQuayKX[0] + " " + mangXienQuayKX[1] + " " + mangXienQuayKX[2] + " " + mangXienQuayKX[3],
                                                                1, smsType, listDonGia[1], "xien4", listDonGia[0], dataSoLieuDate,kieuchoi );

                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx4, thuongxien4, idSmsInt, dongiaId,
                                                                mangXienQuayKX[0] + " " + mangXienQuayKX[1] + " " + mangXienQuayKX[2] + " " + mangXienQuayKX[3],
                                                                0, smsType, listDonGia[1], "xien4", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    // 1-2-3
                                                    if (trung1 > 0 && trung2 > 0 && trung3 > 0) {
                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                mangXienQuayKX[0] + " " + mangXienQuayKX[1] + " " + mangXienQuayKX[2],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                mangXienQuayKX[0] + " " + mangXienQuayKX[1] + " " + mangXienQuayKX[2],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    //1-2-4
                                                    if (trung1 > 0 && trung2 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                mangXienQuayKX[0] + " " + mangXienQuayKX[1] + " " + mangXienQuayKX[3],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                mangXienQuayKX[0] + " " + mangXienQuayKX[1] + " " + mangXienQuayKX[3],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    //1-3-4
                                                    if (trung1 > 0 && trung3 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                mangXienQuayKX[0] + " " + mangXienQuayKX[2] + " " + mangXienQuayKX[3],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                mangXienQuayKX[0] + " " + mangXienQuayKX[2] + " " + mangXienQuayKX[3],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    //2-3-4
                                                    if (trung2 > 0 && trung3 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                mangXienQuayKX[1] + " " + mangXienQuayKX[2] + " " + mangXienQuayKX[3],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                mangXienQuayKX[1] + " " + mangXienQuayKX[2] + " " + mangXienQuayKX[3],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    // 1-2
                                                    if (trung1 > 0 && trung2 > 0) {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[0] + " " + mangXienQuayKX[1],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[0] + " " + mangXienQuayKX[1],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    // 1-3
                                                    if (trung1 > 0 && trung3 > 0) {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[0] + " " + mangXienQuayKX[2],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[0] + " " + mangXienQuayKX[2],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    // 1-4
                                                    if (trung1 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[0] + " " + mangXienQuayKX[3],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[0] + " " + mangXienQuayKX[3],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    // 2-3
                                                    if (trung2 > 0 && trung3 > 0) {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[1] + " " + mangXienQuayKX[2],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[1] + " " + mangXienQuayKX[2],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    // 2-4
                                                    if (trung2 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[1] + " " + mangXienQuayKX[3],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[1] + " " + mangXienQuayKX[3],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    // 3-4
                                                    if (trung3 > 0 && trung4 > 0) {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[2] + " " + mangXienQuayKX[3],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[2] + " " + mangXienQuayKX[3],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    if (limitNumber.contains( mangXienQuayKX[0] )) {
                                                        error += mangXienQuayKX[0] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + mangXienQuayKX[0] + " </font>";
                                                    }
                                                    if (limitNumber.contains( mangXienQuayKX[1] )) {
                                                        error += mangXienQuayKX[1] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + mangXienQuayKX[1] + " </font>";
                                                    }

                                                    if (limitNumber.contains( mangXienQuayKX[2] )) {
                                                        error += mangXienQuayKX[2] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + mangXienQuayKX[2] + " </font>";
                                                    }
                                                    if (limitNumber.contains( mangXienQuayKX[3] )) {
                                                        error += mangXienQuayKX[3] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + mangXienQuayKX[3] + " </font>";
                                                    }
                                                    break;
                                                case 4:
                                                    int trungQ1 = Collections.frequency( compareLo, mangXienQuayKX[0] );
                                                    int trungQ2 = Collections.frequency( compareLo, mangXienQuayKX[1] );
                                                    int trungQ3 = Collections.frequency( compareLo, mangXienQuayKX[2] );
                                                    // 1-2-3
                                                    if (trungQ1 > 0 && trungQ2 > 0 && trungQ3 > 0) {
                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                mangXienQuayKX[0] + " " + mangXienQuayKX[1] + " " + mangXienQuayKX[2],
                                                                1, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId,
                                                                mangXienQuayKX[0] + " " + mangXienQuayKX[1] + " " + mangXienQuayKX[2],
                                                                0, smsType, listDonGia[1], "xien3", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    // 1-2
                                                    if (trungQ1 > 0 && trungQ2 > 0) {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[0] + " " + mangXienQuayKX[1],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[0] + " " + mangXienQuayKX[1],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    // 1-3
                                                    if (trungQ1 > 0 && trungQ3 > 0) {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[0] + " " + mangXienQuayKX[2],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[0] + " " + mangXienQuayKX[2],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    // 2-3
                                                    if (trungQ2 > 0 && trungQ3 > 0) {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[0] + " " + mangXienQuayKX[2],
                                                                1, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    } else {
                                                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, mangXienQuayKX[0] + " " + mangXienQuayKX[2],
                                                                0, smsType, listDonGia[1], "xien2", listDonGia[0], dataSoLieuDate,kieuchoi );
                                                    }
                                                    if (limitNumber.contains( mangXienQuayKX[0] )) {
                                                        error += mangXienQuayKX[0] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + mangXienQuayKX[0] + " </font>";
                                                    }
                                                    if (limitNumber.contains( mangXienQuayKX[1] )) {
                                                        error += mangXienQuayKX[1] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + mangXienQuayKX[1] + " </font>";
                                                    }

                                                    if (limitNumber.contains( mangXienQuayKX[2] )) {
                                                        error += mangXienQuayKX[2] + " ";
                                                    } else {
                                                        error += "<font color=\"RED\">" + mangXienQuayKX[2] + " </font>";
                                                    }
                                                    break;
                                                case 3:
                                                    error += "<font color=\"RED\">" + mangXienQuayKX[0] + " " + mangXienQuayKX[1] + " </font>";
                                                    break;
                                                case 2:
                                                    error += "<font color=\"RED\">" + mangXienQuayKX[0] + " </font>";
                                                    break;
                                                default:
                                                    for (int q = 0; q < mangXienQuayKX.length - 1; q++) {
                                                        error += "<font color=\"RED\">" + mangXienQuayKX[q] + " </font>";
                                                    }
                                                    break;
                                            } //ket thuc swith
                                            String valueXienQuayKX = mangXienQuayKX[mangXienQuayKX.length - 1];
                                            if (mangXienQuayKX.length < 3) {
                                                error += "<font color=\"RED\">" + valueXienQuayKX + " </font>";
                                            } else if (valueXienQuayKX.indexOf( "n" ) > -1 &&
                                                    valueXienQuayKX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                    valueXienQuayKX.replaceAll( "[^\\d.]", "" ).length() >= 1
                                                    ) {
                                                error += valueXienQuayKX.replace( ".", "j" ) + " ";
                                            } else if (valueXienQuayKX.indexOf( "k" ) > -1 &&
                                                    valueXienQuayKX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                    valueXienQuayKX.replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                error += valueXienQuayKX.replace( ".", "j" ) + " ";
                                            } else if (valueXienQuayKX.indexOf( "d" ) > -1 &&
                                                    valueXienQuayKX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 1 &&
                                                    valueXienQuayKX.replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                error += valueXienQuayKX.replace( ".", "j" ) + " ";
                                            } else if (valueXienQuayKX.indexOf( "trieu" ) > -1 &&
                                                    valueXienQuayKX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 5 &&
                                                    valueXienQuayKX.replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                error += valueXienQuayKX.replace( ".", "j" ) + " ";
                                            } else if (valueXienQuayKX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 0 &&
                                                    valueXienQuayKX.replaceAll( "[^\\d.]", "" ).length() >= 1) {
                                                error += valueXienQuayKX.replace( ".", "j" ) + " ";
                                            //} else if (valueXienQuayKX.indexOf( "N1c" ) > -1 &&
                                            //        valueXienQuayKX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).replaceAll( "[0-9]", "" ).length() == 2 &&
                                            //        valueXienQuayKX.replaceAll( "[^\\d.]", "" ).length() >= 2) {
                                            //    error += valueXienQuayKX.replace( ".", "j" ) + " ";
                                            } else {
                                                error += "<font color=\"RED\">" + valueXienQuayKX + " </font>";
                                            }
                                        } else {
                                            error += "<font color=\"RED\">" + tachChuoiXq.get( xqu ) + " </font>";
                                        }
                                    } // ket thuc if else mang de khong x va co x
                                }
                            } // ket thuc vong for xienquay mang 11 22 33/ mang 33 44 55 77
                            break;
                        default:
                            error += "<font color=\"red\" >" + kieuchoi.replaceAll( "(^\\s+|\\s+$)", "" ) + "</font>";
                            error += message[i].substring( 2, message[i].length() );
                            break;
                    }
                } else {
                    if (message[i].length() != 1) {
                        error += "<font color=\"red\" >" + message[i] + "</font>";
                    }
                }
            }
        }
        return controller.resStringError( error );
    }

    private void xuLyDanhLeDeGhepAB(String compareDe, double getNum, double hsde, String boSoLoTo, double thuongde, int idSmsInt
            , int dongiaId, String sdt, String kieuchoi, String ten, String ngay, String smsType) {
        String[] arrBoso = boSoLoTo.split( "," );
        for (int bs = 0; bs < arrBoso.length; bs++) {
            double tongTienDanh = 0;
            double trungSms = 0;
            double tongTienThuong = 0;
            double tongTien = 0;
            int trung = 0;
            tongTienDanh = Math.round( getNum * hsde * 100.0 ) / 100.0;
            if (compareDe.equals( arrBoso[bs].replace( ".", "" ) )) {
                tongTienThuong = Math.round( getNum * thuongde * 100.0 ) / 100.0;
                trung = 1;
                trungSms += getNum;
            } else {
                tongTienThuong = 0;
            }
            double tong = tongTienDanh - tongTienThuong;
            trungSms = Math.round( trungSms * 100.0 ) / 100.0;
            tongTien = Math.round( (tongTienDanh - tongTienThuong) * 100.0 ) / 100.0;
            boolean soLieu = sql.insertDataSourceSoLieu( idSmsInt, dongiaId, arrBoso[bs].replace( ".", "" )
                    , trung, tongTienDanh, tongTienThuong, tongTien, smsType, sdt
                    , kieuchoi, ten, ngay, hsde, thuongde, getNum, getNum, trungSms );
        }
    }

    private void xuLyDanhLeDe(String compareDe, double getNum, double hsde, String boSoLoTo, double thuongde, int idSmsInt
            , int dongiaId, String sdt, String kieuchoi, String ten, String ngay, String smsType) {
        double tongTienDanh = 0;
        double trungSms = 0;
        double tongTienThuong = 0;
        double tongTien = 0;
        int trung = 0;
        tongTienDanh = Math.round( getNum * hsde * 100.0 ) / 100.0;
        if (compareDe.equals( boSoLoTo.replace( ".", "" ) )) {
            tongTienThuong = Math.round( getNum * thuongde * 100.0 ) / 100.0;
            trung = 1;
            trungSms += getNum;
        } else {
            tongTienThuong = 0;
        }
        double tong = tongTienDanh - tongTienThuong;
        trungSms = Math.round( trungSms * 100.0 ) / 100.0;
        tongTien = Math.round( (tongTienDanh - tongTienThuong) * 100.0 ) / 100.0;
        boolean soLieu = sql.insertDataSourceSoLieu( idSmsInt, dongiaId, boSoLoTo.replace( ".", "" )
                , trung, tongTienDanh, tongTienThuong, tongTien, smsType, sdt
                , kieuchoi, ten, ngay, hsde, thuongde, getNum, getNum, trungSms );
    }

    private void xulydanhboDe(String value, double getNum, double hsde, String compareDe, double thuongde, int idSmsInt
            , int dongiaId, String sdt, String kieuchoi, String ten, String ngay, String boSoLoTo, String smsType) {
        String[] arrValue = value.split( "," );
        int trung = 0;
        double tongTienDanhSms = 0;
        double trungSms = 0;
        double tongTienDanh = 0;
        double tongTienThuong = 0;
        double tongTien = 0;
        double fixErrDouble = 0.01;
        for (int xi = 0; xi < arrValue.length; xi++) {
            double tienDanh, tienThuong, tong;
            tienDanh = getNum * hsde;
            if (compareDe.equals( arrValue[xi] )) {
                tienThuong = getNum * thuongde;
                trungSms += getNum;
                trung += 1;
            } else {
                tienThuong = 0;
            }
            tong = tienDanh - tienThuong;

            tongTienDanhSms += getNum;
            tongTienDanh += tienDanh;
            tongTienThuong += tienThuong;
            tongTien += tong;
        }
        tongTienDanhSms = Math.round( tongTienDanhSms * 100.00 ) / 100.0;
        trungSms = Math.round( trungSms * 100.00 ) / 100.0;
        tongTienDanh = Math.round( tongTienDanh * 100.00 ) / 100.0;
        tongTienThuong = Math.round( tongTienThuong * 100.00 ) / 100.0;
        tongTien = Math.round( tongTien * 100.00 ) / 100.0;

        boolean soLieu = sql.insertDataSourceSoLieu( idSmsInt, dongiaId, boSoLoTo
                , trung, tongTienDanh, tongTienThuong, tongTien, smsType, sdt
                , kieuchoi, ten, ngay, hsde, thuongde, getNum, tongTienDanhSms, trungSms );
    }

    public void xulydanhboLoTo(String value, ArrayList<String> compareLo, double getNum, double hslo, double thuonglo
            , int idSmsInt, int dongiaId, String boSoLoTo, String sdt, String kieuchoi, String ten, String ngay, String smsType) {
        String[] arrValue = value.split( "," );
        double tongTienDanhSms = 0;
        double trungSms = 0;
        double tongTienDanh = 0;
        double tongTienThuong = 0;
        double tongTien = 0;
        int trung = 0;
        int tongTrung = 0;
        for (int xi = 0; xi < arrValue.length; xi++) {
            trung = Collections.frequency( compareLo, arrValue[xi].replaceAll( "(^\\s+|\\s+$)", "" ) );
            double tienDanh = getNum * hslo;
            double tienThuong = getNum * trung * thuonglo;
            double tong = tienDanh - tienThuong;
            tongTienDanhSms += getNum;
            trungSms += trung * getNum;
            tongTienDanh += tienDanh;
            tongTienThuong += tienThuong;
            tongTien += tong;
            tongTrung += trung;
        }
        tongTienDanhSms = Math.round( tongTienDanhSms * 100.0 ) / 100.0;
        trungSms = Math.round( trungSms * 100.0 ) / 100.0;
        tongTienDanh = Math.round( tongTienDanh * 100.0 ) / 100.0;
        tongTienThuong = Math.round( tongTienThuong * 100.0 ) / 100.0;
        tongTien = Math.round( tongTien * 100.0 ) / 100.0;
        boolean soLieu = sql.insertDataSourceSoLieu( idSmsInt, dongiaId, boSoLoTo
                , tongTrung, tongTienDanh, tongTienThuong, tongTien, smsType,
                sdt, kieuchoi, ten, ngay, hslo, thuonglo, getNum, tongTienDanhSms, trungSms );
    }

    public void xulydanhleLoTo(double getNum, double hslo, ArrayList<String> compareLo, String boSoLoTo
            , double thuonglo, int idSmsInt, int dongiaId, String sdt, String kieuchoi, String ten, String ngay, String smsType) {
        double trungSms = 0;
        double tongTienDanh = 0;
        double tongTienThuong = 0;
        double tongTien = 0;
        int trung = 0;
        tongTienDanh = Math.round( getNum * hslo * 100.0 ) / 100.0;
        trung = Collections.frequency( compareLo, boSoLoTo.replace( ".", "" ) );
        tongTienThuong = Math.round( getNum * thuonglo * trung * 100.0 ) / 100.0;
        trungSms = Math.round( getNum * trung * 100.0 ) / 100.0;
        double tong = tongTienDanh - tongTienThuong;
        tongTien = Math.round( (tongTienDanh - tongTienThuong) * 100.0 ) / 100.0;
        boolean soLieu = sql.insertDataSourceSoLieu( idSmsInt, dongiaId, boSoLoTo.replace( ".", "" )
                , trung, tongTienDanh, tongTienThuong, tongTien, smsType,
                sdt, kieuchoi, ten, ngay, hslo, thuonglo, getNum, getNum, trungSms );
    }

    private void xulydanhleLoToGhepAB(double getNum, double hslo, ArrayList<String> compareLo, String boSoLoTo
            , double thuonglo, int idSmsInt, int dongiaId, String sdt, String kieuchoi, String ten, String ngay, String smsType) {
        String[] ArrboSo = boSoLoTo.split( "," );
        for (int bs = 0; bs < ArrboSo.length; bs++) {
            double trungSms = 0;
            double tongTienDanh = 0;
            double tongTienThuong = 0;
            double tongTien = 0;
            int trung = 0;
            tongTienDanh = Math.round( getNum * hslo * 100.0 ) / 100.0;
            trung = Collections.frequency( compareLo, ArrboSo[bs].replace( ".", "" ) );
            tongTienThuong = Math.round( getNum * thuonglo * trung * 100.0 ) / 100.0;
            trungSms = Math.round( getNum * trung * 100.0 ) / 100.0;
            double tong = tongTienDanh - tongTienThuong;
            tongTien = Math.round( (tongTienDanh - tongTienThuong) * 100.0 ) / 100.0;
            boolean soLieu = sql.insertDataSourceSoLieu( idSmsInt, dongiaId, ArrboSo[bs].replace( ".", "" )
                    , trung, tongTienDanh, tongTienThuong, tongTien, smsType,
                    sdt, kieuchoi, ten, ngay, hslo, thuonglo, getNum, getNum, trungSms );
        }
    }

    public void xulydanhXienKieu(double getNum, double hsx, double thuongxien, int idSmsInt, int dongiaId,
                                 String boSoLoTo, int trung, String smsType, String sdt,
                                 String kieuchoi, String ten, String ngay,String kieuXien) {
        String [] xiA = controller.inArrayXiA();
        if (Arrays.asList(xiA).contains(kieuXien.trim())) {
            kieuchoi = kieuXien.trim();
        }
        double tongTienDanh, tongTienThuong, tongTien;
        double hsxien = hsx;
        double tienDanh = hsx * getNum;
        double tienThuong = thuongxien * getNum * trung;
        double tong = tienDanh - tienThuong;
        double trungSms = Math.round( getNum * trung * 100.00 ) / 100.0;
        tongTienDanh = Math.round( tienDanh * 100.00 ) / 100.0;
        tongTienThuong = Math.round( tienThuong * 100.00 ) / 100.0;
        tongTien = Math.round( tong * 100.00 ) / 100.0;
        String[] orderBosoLoTo = boSoLoTo.split( " " );
        Arrays.sort( orderBosoLoTo );
        String result = TextUtils.join( " ", orderBosoLoTo ).trim();
        boolean soLieu = sql.insertDataSourceSoLieu( idSmsInt, dongiaId, result
                , trung, tongTienDanh, tongTienThuong, tongTien, smsType,
                sdt, kieuchoi, ten, ngay, hsxien, thuongxien, getNum, getNum, trungSms );
    }

    public void xulydanhMangXienKieu(double getNum, double hsx2, double thuongxien2, double hsx3, double thuongxien3, double hsx4, double thuongxien4,
                                     int idSmsInt, int dongiaId, String boSoLoTox2, String boSoLoTox3, String boSoLoTox4, int trung,
                                     String smsType, String sdt, String ten, String dataSoLieuDate,String kieuXien) {
        ArrayList<String> compareXienLo = sql.getArrayKeyRes( dataSoLieuDate );
        String [] xiA = controller.inArrayXiA();
        String xiDau = "0";
        if (Arrays.asList(xiA).contains(kieuXien.trim())) {
           compareXienLo = sql.getLoDauGiai( dataSoLieuDate );
           xiDau = "1";
        }
        if (boSoLoTox2.length() > 0) {
            String[] boSoArr = boSoLoTox2.split( "," );
            for (int a = 0; a < boSoArr.length; a++) {
                String[] value = boSoArr[a].split( " " );
                //iterate on the general list
                if (Collections.frequency( compareXienLo, value[0] ) > 0 &&
                        Collections.frequency( compareXienLo, value[1] ) > 0) {
                    if (xiDau.equals( "0" )) {
                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, boSoArr[a],
                                1, smsType, sdt, "xien2", ten, dataSoLieuDate, kieuXien );
                    } else {
                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, boSoArr[a],
                                1, smsType, sdt, "sa2", ten, dataSoLieuDate, kieuXien );
                    }
                } else {
                    if (xiDau.equals( "0" )) {
                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, boSoArr[a],
                                0, smsType, sdt, "xien2", ten, dataSoLieuDate, kieuXien );
                    } else {
                        xulydanhXienKieu( getNum, hsx2, thuongxien2, idSmsInt, dongiaId, boSoArr[a],
                                0, smsType, sdt, "sa2", ten, dataSoLieuDate, kieuXien );
                    }
                }
            }
        }

        if (boSoLoTox3.length() > 0) {
            String[] boSoArr3 = boSoLoTox3.split( "," );
            for (int a = 0; a < boSoArr3.length; a++) {
                String[] value3 = boSoArr3[a].split( " " );
                //iterate on the general list
                if (Collections.frequency( compareXienLo, value3[0] ) > 0 &&
                        Collections.frequency( compareXienLo, value3[1] ) > 0 &&
                        Collections.frequency( compareXienLo, value3[2] ) > 0) {
                    if (xiDau.equals( "0" )) {
                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId, boSoArr3[a],
                                1, smsType, sdt, "xien3", ten, dataSoLieuDate,kieuXien );
                    } else {
                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId, boSoArr3[a],
                                1, smsType, sdt, "sa3", ten, dataSoLieuDate,kieuXien );
                    }
                } else {
                    if (xiDau.equals( "0" )) {
                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId, boSoArr3[a],
                                0, smsType, sdt, "xien3", ten, dataSoLieuDate,kieuXien );
                    } else {
                        xulydanhXienKieu( getNum, hsx3, thuongxien3, idSmsInt, dongiaId, boSoArr3[a],
                                0, smsType, sdt, "sa3", ten, dataSoLieuDate,kieuXien );
                    }
                }
            }
        }

        if (boSoLoTox4.length() > 0) {
            String[] boSoArr4 = boSoLoTox4.split( "," );
            for (int a = 0; a < boSoArr4.length; a++) {
                String[] value4 = boSoArr4[a].split( " " );
                if (Collections.frequency( compareXienLo, value4[0] ) > 0 &&
                        Collections.frequency( compareXienLo, value4[1] ) > 0 &&
                        Collections.frequency( compareXienLo, value4[2] ) > 0 &&
                        Collections.frequency( compareXienLo, value4[3] ) > 0) {
                    if (xiDau.equals( "0" )) {
                        xulydanhXienKieu( getNum, hsx4, thuongxien4, idSmsInt, dongiaId, boSoArr4[a],
                                1, smsType, sdt, "xien4", ten, dataSoLieuDate, kieuXien );
                    } else {
                        xulydanhXienKieu( getNum, hsx4, thuongxien4, idSmsInt, dongiaId, boSoArr4[a],
                                1, smsType, sdt, "sa4", ten, dataSoLieuDate, kieuXien );
                    }
                } else {
                    if (xiDau.equals( "0" )) {
                        xulydanhXienKieu( getNum, hsx4, thuongxien4, idSmsInt, dongiaId, boSoArr4[a],
                                0, smsType, sdt, "xien4", ten, dataSoLieuDate, kieuXien );
                    } else {
                        xulydanhXienKieu( getNum, hsx4, thuongxien4, idSmsInt, dongiaId, boSoArr4[a],
                                0, smsType, sdt, "sa4", ten, dataSoLieuDate, kieuXien );
                    }
                }
            }
        }
    }

    public void xulydanhXien(ArrayList<String> checkXien, double getNum, double hsx2, double thuongxien2, double hsx3, double thuongxien3
            , double hsx4, double thuongxien4, int idSmsInt, int dongiaId, String boSoLoTo, String smsType
            , String sdt, String kieuchoi, String ten, String ngay,String kieuXien) {
        double trungSms = 0;
        double tienDanh = 0;
        double tienThuong = 0;
        double tong = 0;
        double tongTienDanh, tongTienThuong, tongTien;
        int trung = 0;
        double hsxien = 0;
        double thuongxien = 0;
        String [] xiA = controller.inArrayXiA();
        if (Arrays.asList(xiA).contains(kieuXien.trim())) {
            kieuchoi = kieuXien.trim();
        }
        if (Collections.frequency( checkXien, "false" ) == 0) {
            trungSms += getNum;
            if (checkXien.size() == 2) {
                hsxien = hsx2;
                thuongxien = thuongxien2;
                tienDanh = hsx2 * getNum;
                tienThuong = thuongxien2 * getNum;
            } else if (checkXien.size() == 3) {
                hsxien = hsx3;
                thuongxien = thuongxien3;
                tienDanh = hsx3 * getNum;
                tienThuong = thuongxien3 * getNum;
            } else if (checkXien.size() == 4) {
                hsxien = hsx4;
                thuongxien = thuongxien4;
                tienDanh = hsx4 * getNum;
                tienThuong = thuongxien4 * getNum;
            }
            trung = 1;
            tong = tienDanh - tienThuong;
        } else {
            tienThuong = 0;
            if (checkXien.size() == 2) {
                hsxien = hsx2;
                thuongxien = thuongxien2;
                tienDanh = hsx2 * getNum;
            } else if (checkXien.size() == 3) {
                hsxien = hsx3;
                thuongxien = thuongxien3;
                tienDanh = hsx3 * getNum;
            } else if (checkXien.size() == 4) {
                hsxien = hsx4;
                thuongxien = thuongxien4;
                tienDanh = hsx4 * getNum;
            }
            trung = 0;
            tong = tienDanh;
        }
        trungSms = Math.round( trungSms * 100.00 ) / 100.0;
        tongTienDanh = Math.round( tienDanh * 100.00 ) / 100.0;
        tongTienThuong = Math.round( tienThuong * 100.00 ) / 100.0;
        tongTien = Math.round( tong * 100.00 ) / 100.0;
        String[] orderBosoLoTo = boSoLoTo.split( " " );
        Arrays.sort( orderBosoLoTo );
        String result = TextUtils.join( " ", orderBosoLoTo ).trim();
        boolean soLieu = sql.insertDataSourceSoLieu( idSmsInt, dongiaId, result
                , trung, tongTienDanh, tongTienThuong, tongTien, smsType,
                sdt, kieuchoi, ten, ngay, hsxien, thuongxien, getNum, getNum, trungSms );
    }

    public void xulydanhbosoVtDe(String value, double getNum, double hsde, String compareDe, double thuongde, int idSmsInt
            , int dongiaId, String sdt, String kieuchoi, String ten, String ngay, String tenBoso, String boSoLoTo, String smsType) {
        String[] importBoso = boSoLoTo.split( "," );
        String[] valueBoso = value.split( "," );
        String[] giatri = new String[importBoso.length];
        for (int r = 0; r < importBoso.length; r++) {
            giatri[r] = "";
        }
        for (int q = 0; q < valueBoso.length; q++) {
            for (int z = 0; z < importBoso.length; z++) {
                if (valueBoso[q].indexOf( importBoso[z] ) > -1) {
                    if (!giatri[z].equals( "" )) {
                        giatri[z] += "," + valueBoso[q];
                    } else {
                        giatri[z] += valueBoso[q];
                    }
                    break;
                }
            }
        }

        for (int k = 0; k < importBoso.length; k++) {
            if (k < giatri.length) {
                xulydanhboDe( giatri[k], getNum, hsde, compareDe, thuongde, idSmsInt, dongiaId, sdt, kieuchoi, ten, ngay, tenBoso + importBoso[k], smsType );
            }
        }
    }

    public void xulydanhbosoVtLo(String value, ArrayList<String> compareLo, double getNum, double hslo, double thuonglo
            , int idSmsInt, int dongiaId, String tenBoso, String boSoLoTo, String sdt, String kieuchoi, String ten, String ngay, String smsType) {
        String[] importBoso = boSoLoTo.split( "," );
        String[] valueBoso = value.split( "," );
        String[] giatri = new String[importBoso.length];
        for (int r = 0; r < importBoso.length; r++) {
            giatri[r] = "";
        }
        for (int q = 0; q < valueBoso.length; q++) {
            for (int z = 0; z < importBoso.length; z++) {

                if (valueBoso[q].indexOf( importBoso[z] ) > -1) {
                    if (!giatri[z].equals( "" )) {
                        giatri[z] += "," + valueBoso[q];
                    } else {
                        giatri[z] += valueBoso[q];
                    }
                    break;
                }
            }
        }

        for (int k = 0; k < importBoso.length; k++) {
            if (k < giatri.length) {
                xulydanhboLoTo( giatri[k], compareLo, getNum, hslo, thuonglo, idSmsInt, dongiaId, tenBoso + importBoso[k], sdt, kieuchoi, ten, ngay, smsType );
            }
        }
    }

    private void xuLyDanhLeDeGhep(String dayso, String compareDe, double getNum, double hsde, double thuongde, int idSmsInt
            , int dongiaId, String sdt, String kieuchoi, String ten, String ngay, String smsType) {
        String[] arrDayso = dayso.split( "," );
        for (int b = 0; b < arrDayso.length; b++) {
            xuLyDanhLeDe( compareDe, getNum, hsde, arrDayso[b], thuongde, idSmsInt, dongiaId, sdt, kieuchoi, ten, ngay, smsType );
        }
    }

    private void xulydanhleLoToGhep(String dayso, double getNum, double hslo, ArrayList<String> compareLo,
                                    double thuonglo, int idSmsInt, int dongiaId, String sdt, String kieuchoi, String ten, String ngay, String smsType) {
        String[] arrDayso = dayso.split( "," );
        for (int b = 0; b < arrDayso.length; b++) {
            xulydanhleLoTo( getNum, hslo, compareLo, arrDayso[b], thuonglo, idSmsInt, dongiaId, sdt, kieuchoi, ten, ngay, smsType );
        }
    }

}