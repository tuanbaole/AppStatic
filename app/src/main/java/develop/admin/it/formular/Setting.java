package develop.admin.it.formular;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Setting extends AppCompatActivity {
    TextView textViewTimeDe, textViewTimeLo, textViewKhoaApp, textViewShowImei;
    Button buttonCreated;
    GlobalClass controller;
    DatabaseHelper sql;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_setting );
        controller = new GlobalClass();
        sql = new DatabaseHelper( this );
        Cursor timeTable = sql.getAllDb( "SELECT * FROM time_table LIMIT 0,1" );
        String timeDe = "18:14";
        String timeLo = "18:29";
        String timeApp = "chưa có";
        String time_id = "";
        if (timeTable.getCount() > 0) {
            timeTable.moveToFirst();
            time_id = timeTable.getString( timeTable.getColumnIndex( "ID" ) );
            timeDe = timeTable.getString( timeTable.getColumnIndex( "KHOADE" ) );
            timeLo = timeTable.getString( timeTable.getColumnIndex( "KHOALO" ) );
            timeApp = timeTable.getString( timeTable.getColumnIndex( "KHOAAPP" ) );
        }

        textViewKhoaApp = (TextView) findViewById( R.id.textViewKhoaApp );
        textViewKhoaApp.setText( timeApp );

        TelephonyManager telemamanger = (TelephonyManager) getSystemService( Context.TELEPHONY_SERVICE );
        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String getSimSerialNumber = telemamanger.getSimSerialNumber();
        textViewShowImei = (TextView) findViewById( R.id.textViewShowImei );
        textViewShowImei.setText( getSimSerialNumber );

        textViewTimeDe = (TextView) findViewById( R.id.textViewTimeDe );
        textViewTimeDe.setText( timeDe );
        final String finalTime_id = time_id;
        final String finalTimeLo = timeLo;
        final String finalTimeDe = timeDe;
        textViewTimeDe.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] deArr = textViewTimeDe.getText().toString().split( ":" );
                int hour = controller.dateInt( "HH" );
                int minute = controller.dateInt( "mm" );
                if (deArr.length == 2) {
                    hour = Integer.parseInt( deArr[0].replaceAll( "(^\\s+|\\s+$)", "" ).trim() );
                    minute = Integer.parseInt( deArr[1].replaceAll( "(^\\s+|\\s+$)", "" ).trim() );
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog( Setting.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String newTimeDe = String.valueOf( hourOfDay ) + " : " + String.valueOf( minute );
                        textViewTimeDe.setText( newTimeDe );
                        if (!finalTime_id.equals( "" )) {
                            sql.updateTimeTable( finalTime_id, newTimeDe, finalTimeLo );
                        }
                    }
                }, hour, minute, true );//Yes 24 hour time
                timePickerDialog.setTitle( "Chọn thời gian khóa đề" );
                timePickerDialog.show();
            }
        } );

        textViewTimeLo = (TextView) findViewById( R.id.textViewTimeLo );
        textViewTimeLo.setText( timeLo );
        textViewTimeLo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] loArr = textViewTimeLo.getText().toString().split( ":" );
                int hour = controller.dateInt( "HH" );
                int minute = controller.dateInt( "mm" );
                if (loArr.length == 2) {
                    hour = Integer.parseInt( loArr[0].replaceAll( "(^\\s+|\\s+$)", "" ).trim() );
                    minute = Integer.parseInt( loArr[1].replaceAll( "(^\\s+|\\s+$)", "" ).trim() );
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog( Setting.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String newTimeLo = String.valueOf( hourOfDay ) + " : " + String.valueOf( minute );
                        textViewTimeLo.setText( newTimeLo );
                        if (!finalTime_id.equals( "" )) {
                            sql.updateTimeTable( finalTime_id, finalTimeDe, newTimeLo );
                        }
                    }
                }, hour, minute, true );//Yes 24 hour time
                timePickerDialog.setTitle( "Chọn thời gian khóa lô" );
                timePickerDialog.show();
            }
        } );

        buttonCreated = (Button) findViewById( R.id.buttonCreated );
        buttonCreated.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE );
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            TelephonyManager telemamanger = (TelephonyManager) getSystemService( Context.TELEPHONY_SERVICE );
                            if (ActivityCompat.checkSelfPermission( Setting.this, Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            String getSimSerialNumber = telemamanger.getSimSerialNumber();
                            String link = "http://hostingkqxs.esy.es/registerApp.php?imei=" + getSimSerialNumber;
                            Log.d("LogFile", link);
                            new ReadXml().execute(link);
                            String link2 = "http://hostingkqxs.esy.es/lockApp.php?iccid=" + getSimSerialNumber;
                            new ReadXmlLock().execute(link2);

                        }
                    });
                } else {
                    controller.showAlertDialog(Setting.this, "Thông Báo", "Không kết nối được internet");
                }
            }
        });

        sideBarMenu();

    }

    class ReadXmlLock extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String kq = controller.getXMLFromUrl(params[0]);
            return kq;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.indexOf("1") > -1) {
                Toast.makeText(Setting.this, "thêm imei thành công", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Setting.this, "không thêm được imei thành công", Toast.LENGTH_LONG).show();
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
            Toast.makeText(Setting.this, s, Toast.LENGTH_LONG).show();
            sql = new DatabaseHelper(Setting.this);
            sql.updateTimeTableIMEI("1", s);
            textViewKhoaApp = (TextView) findViewById(R.id.textViewKhoaApp);
            textViewKhoaApp.setText(s);
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
                        Intent intent = new Intent(Setting.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(Setting.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(Setting.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(Setting.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(Setting.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(Setting.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(Setting.this, viewSmsNotMoney.class);
                        startActivity(intent7);
                        return true;
                    case R.id.historySms:
                        Intent intent8 = new Intent(Setting.this, HistorySms.class);
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
        inflater.inflate(R.menu.controller_sms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DialogHandler appdialog = new DialogHandler();
        switch (item.getItemId()) {
            case R.id.deleteallsms:
                appdialog.Confirm(this, "Thông Báo", "Bạn có chắc xóa tất cả tin nhắn không?",
                        "Cancel", "OK", aproc("tất cả tin nhắn", "all"), bproc("tất cả tin nhắn"));
                return true;
            case R.id.deleteallinbox:
                appdialog.Confirm(this, "Thông Báo", "Bạn có chắc xóa tất cả tin đi không?",
                        "Cancel", "OK", aproc("tất cả tin đến", "inbox"), bproc("tất cả tin đến"));
                return true;
            case R.id.deleteallsend:
                appdialog.Confirm(this, "Thông Báo", "Bạn có chắc xóa tất cả tin đến không?",
                        "Cancel", "OK", aproc("tất cả tin đi", "sent"), bproc("tất cả tin đi"));
                return true;
            case R.id.deletesmsnotday:
                appdialog.Confirm(this, "Thông Báo", "Bạn có chắc xóa tất cả tin không phải hôm nay không?",
                        "Cancel", "OK", aproc("tất cả tin không phải ngày hôm nay", "notday"), bproc("tất cả tin không phải ngày hôm nay"));
                return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Runnable aproc(final String noti, final String type) {
        return new Runnable() {
            public void run() {
                DeleteSMS(type);
                Toast.makeText(Setting.this, "Bạn đã xóa thành công " + noti, Toast.LENGTH_LONG).show();
            }
        };
    }

    public Runnable bproc(final String noti) {
        return new Runnable() {
            public void run() {
                Toast.makeText(Setting.this, "Bạn đã từ chối xóa tin " + noti, Toast.LENGTH_LONG).show();
            }
        };
    }

    private int DeleteSMS(String type) {
        Uri deleteUri = Uri.parse("content://sms/failed");
        String fitter = "";
        switch (type) {
            case "all":
                deleteUri = Uri.parse("content://sms");
                break;
            case "inbox":
                deleteUri = Uri.parse("content://sms/inbox");
                break;
            case "sent":
                deleteUri = Uri.parse("content://sms/sent");
                break;
            case "notday":
                String dayDefault = controller.dateDay("yyyy-MM-dd 00:00:00");
                long lastDay = controller.converDayToMill("yyyy-MM-dd HH:mm:ss", dayDefault);
                deleteUri = Uri.parse("content://sms");
                fitter = "date <=" + lastDay;
                break;
        }

        int count = 0;
        Cursor c = getContentResolver().query(deleteUri, null, null, null, null);
        if (!fitter.equals("")) {
            c = getContentResolver().query(deleteUri, null, fitter, null, null);
        }

        while (c.moveToNext()) {
            try {
                // Delete the SMS
                String pid = c.getString(0); // Get id;
                String uri = "content://sms/" + pid;
                count = getContentResolver().delete(Uri.parse(uri),
                        null, null);
            } catch (Exception e) {
                Toast.makeText(Setting.this, "máy không cho xóa", Toast.LENGTH_SHORT).show();
            }
        }
        return count;
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        String release = Build.VERSION.RELEASE;
//        if(release.length() > 3) {
//            release = release.substring(0,3);
//        }
//        double version = Double.parseDouble(release);
//        if (version >= 4.4 ) {
//            final String myPackageName = getPackageName();
//            if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
//                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
//                startActivity(intent);
//            }
//        }
//    }

}
