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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class Customer extends AppCompatActivity {
    DatabaseHelper sql;
    public ArrayList<String> names, sdts, idContact;
    ListView mylistView;
    Button sendSms;
    TextView textViewDate;

    GlobalClass controller = new GlobalClass();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private int year_x, month_x, day_x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);
        sideBarMenu();
        String today = controller.dateDay("yyyy-MM-dd");
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewDate.setText(Html.fromHtml("<font color=\"BLUE\">" + today + "</font>"));
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        showItemListViewContact(today);


    }

    private void showItemListViewContact(String getDays) {
        sql = new DatabaseHelper(this);
        String query = "SELECT DONGIAID,SDT,TEN FROM solieu_table WHERE NGAY='"+getDays+"' GROUP BY SDT";
        Cursor solieu_table = sql.getAllDb(query);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String kieu = spinner.getSelectedItem().toString();

        names = new ArrayList<>();
        sdts = new ArrayList<>();
        idContact = new ArrayList<>();
        if (solieu_table.getCount() > 0) {
            while (solieu_table.moveToNext()) {
                String sdtName = solieu_table.getString(solieu_table.getColumnIndex("TEN"));
                names.add(sdtName);
                String viewInfo = solieu_table.getString(solieu_table.getColumnIndex("SDT"));
                sdts.add(viewInfo);
                idContact.add(solieu_table.getString(solieu_table.getColumnIndex("DONGIAID")));
            }
        }
        mylistView = (ListView) findViewById(R.id.mylistView);
        // hien thi them ra ngoai
        CustomerAdapter contactadapter = new CustomerAdapter(this, names, sdts, idContact,kieu,getDays);
        mylistView.setAdapter(contactadapter);

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
                        Intent intent = new Intent(Customer.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(Customer.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(Customer.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(Customer.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(Customer.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(Customer.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(Customer.this, viewSmsNotMoney.class);
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(Customer.this, new DatePickerDialog.OnDateSetListener() {
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
                            String date = String.valueOf(year) + "-" + month_s + "-" + dayOfMonth_s;
                            textViewDate = (TextView) findViewById(R.id.textViewDate);
                            textViewDate.setText(Html.fromHtml("<font color=\"BLUE\">" + date + "</font>"));
                            showItemListViewContact(date);
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
