package develop.admin.it.formular;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class viewSmsNotMoney extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private final static String LogFile = "LogFile";
    GlobalClass controller = new GlobalClass();
    ListView mylistView;
    TextView textViewDate;
    DatabaseHelper sql;
    private int year_x, month_x, day_x;
    private static CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sms_not_money);
        String today = controller.dateDay("yyyy-MM-dd");
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewDate.setText(Html.fromHtml("<font color=\"BLUE\">" + today + "</font>"));
        sql = new DatabaseHelper(viewSmsNotMoney.this);
        showSmsNotMoneyfunction();
        sideBarMenu();
    }

    private void showSmsNotMoneyfunction() {
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        String date = textViewDate.getText().toString();
        String query = "SELECT SMSID FROM sms_ready_table where STATUS=2 and NGAY=\"" + date + "\"";
        Cursor smsNotMoney = sql.getAllDb(query);
        ArrayList<String> bodySms = new ArrayList<String>();
        if (smsNotMoney.getCount() > 0) {
            String id = "0";
            while (smsNotMoney.moveToNext()) {
                id += "," + smsNotMoney.getString(smsNotMoney.getColumnIndex("SMSID"));
            }
            Uri mSmsinboxQueryUri = Uri.parse("content://sms/");
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            String filter = "_id  IN (" + id + ") ";
            Cursor cursor1 = getContentResolver().query(mSmsinboxQueryUri, projection, filter, null, "address asc");
            if (cursor1.getCount() > 0) {
                String Sessionsdt = "";
                String content = "";
                int i = 0;
                Log.d("LogFile", String.valueOf(cursor1.getCount()));
                while (cursor1.moveToNext()) {
                    i++;
                    String strAddress = cursor1.getString(cursor1.getColumnIndex("address"))
                            .replace("(", "").replace(")", "").replace("-", "").replace(" ", "").replace("+84", "0");
                    Log.d("LogFile", String.valueOf(i));
                    if (Sessionsdt.equals(strAddress)) {
                        String smsType = "";
                        if (cursor1.getString(cursor1.getColumnIndex("type")).equals("1")) {
                            smsType = "------Tin gửi đến------";
                        } else {
                            smsType = "------Tin gửi đi------";
                        }
                        String body = "<big>" + cursor1.getString(cursor1.getColumnIndex("Body")) + "</big>";
                        content += body + "<br/>" + smsType + "<br/>";
                        if (i == cursor1.getCount()) {
                            bodySms.add(content);
                        }
                    } else {
                        Sessionsdt = strAddress;
                        if (!content.equals("")) {
                            bodySms.add(content);
                        }
                        Cursor dongia = sql.getAllDb("SELECT TEN FROM dongia_table WHERE SDT=\"" + strAddress + "\" LIMIT 0,1 ");
                        String showContact = "";
                        if (dongia.getCount() > 0) {
                            dongia.moveToFirst();
                            String ten = dongia.getString(dongia.getColumnIndex("TEN"));
                            showContact = "<font color=\"BLUE\">" + ten + "-" + strAddress + "</font>";
                        } else {
                            showContact = "<font color=\"BLUE\">" + strAddress + "-" + strAddress + "</font>";
                        }
                        String smsType = "";
                        if (cursor1.getString(cursor1.getColumnIndex("type")).equals("1")) {
                            smsType = "------Tin gửi đến-----";
                        } else {
                            smsType = "------Tin gửi đi------";
                        }
                        String body = "<big>" + cursor1.getString(cursor1.getColumnIndex("Body")) + "</big>";
                        content = showContact + "<br/>" + body + "<br/>" + smsType + "<br/>";
                        if (i == cursor1.getCount()) {
                            bodySms.add(content);
                        }
                    }
                }
            }
        }
        ListView listViewShowSms = (ListView) findViewById(R.id.listviewNotMoneySms);
        showSmsNotMoneyAdapter customAdapter = new showSmsNotMoneyAdapter(viewSmsNotMoney.this, bodySms);
        listViewShowSms.setAdapter(customAdapter);
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
                        Intent intent = new Intent(viewSmsNotMoney.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(viewSmsNotMoney.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(viewSmsNotMoney.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(viewSmsNotMoney.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(viewSmsNotMoney.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(viewSmsNotMoney.this, MainXoaCongNo.class);
                        startActivity(intent6);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(viewSmsNotMoney.this, new DatePickerDialog.OnDateSetListener() {
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
                            final String date = String.valueOf(year) + "-" + month_s + "-" + dayOfMonth_s;
                            textViewDate = (TextView) findViewById(R.id.textViewDate);
                            textViewDate.setText(Html.fromHtml("<font color=\"BLUE\">" + date + "</font>"));
                            showSmsNotMoneyfunction();
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
