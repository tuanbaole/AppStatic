package develop.admin.it.formular;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class Contact extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private final static String LogFile = "LogFile";
    GlobalClass controller;

    public ArrayList<String> names, sdts, idContact;
    public ArrayList<String> information = new ArrayList<>();
    ListView mylistView;
    EditText editTextSearch;
    DatabaseHelper sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        sideBarMenu();
        showItemListViewContact();
        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                if (s.toString().equals("")) {
                    initList(names, sdts, idContact);
                } else {
                    searchItem(s.toString(), names, sdts, idContact);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                initList(names, sdts, idContact);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            String getIdDonGia = bd.getString("noti");
            controller = new GlobalClass();
            if (getIdDonGia.equals("insertSuccess")){
                controller.showAlertDialog(Contact.this,"Thông Báo","Thay đổi thành công");
            } else {
                controller.showAlertDialog(Contact.this,"Thông Báo","Chưa thay đổi được");
            }
        }

    }

    private void showItemListViewContact() {
        sql = new DatabaseHelper(this);
        Cursor dongia = sql.getAllDb("SELECT * FROM dongia_table");
        names = new ArrayList<>();
        sdts = new ArrayList<>();
        idContact = new ArrayList<>();
        if (dongia.getCount() > 0) {
            while (dongia.moveToNext()) {

                String sdtName = dongia.getString(dongia.getColumnIndex("TEN")) +
                        " - " + dongia.getString(dongia.getColumnIndex("SDT"));
                names.add(sdtName);
                String viewInfo = "hsde:" + dongia.getString(dongia.getColumnIndex("HSDE")) +
                        " hslo: " + dongia.getString(dongia.getColumnIndex("HSLO")) +
                        " hs3cang: " + dongia.getString(dongia.getColumnIndex("HSBACANG")) +
                        " hsx2: " + dongia.getString(dongia.getColumnIndex("HSXIENHAI")) +
                        " hsx3: " + dongia.getString(dongia.getColumnIndex("HSXIENBA")) +
                        " hsx4: " + dongia.getString(dongia.getColumnIndex("HSXIENBON")) +
                        " cophan: " + dongia.getString(dongia.getColumnIndex("COPHAN")) + "%";
                sdts.add(viewInfo);
                idContact.add(dongia.getString(dongia.getColumnIndex("ID")));
            }
        }
        mylistView = (ListView) findViewById(R.id.mylistView);
        // hien thi them ra ngoai
        ContactAdapter contactadapter = new ContactAdapter(this, names, sdts, idContact);
        mylistView.setAdapter(contactadapter);
    }

    public void searchItem(String textToSearch, ArrayList<String> namesViewSearch, ArrayList<String> sdtsViewSearch, ArrayList<String> idContactViewSearch) {
        ArrayList<String> namesa = new ArrayList<>();
        ArrayList<String> sdtsa = new ArrayList<>();
        ArrayList<String> idContactsa = new ArrayList<>();
        for (int i = 0; i < namesViewSearch.size(); i++) {
            if (names.get(i).toLowerCase().contains(textToSearch.toLowerCase())) {
                namesa.add(names.get(i));
                sdtsa.add(sdtsViewSearch.get(i));
                idContactsa.add(idContactViewSearch.get(i));
            }
        }
        ContactAdapter contactadapter = new ContactAdapter(this, namesa, sdtsa, idContactsa);
        mylistView.setAdapter(contactadapter);
    }

    public void initList(ArrayList<String> namesViewSearch, ArrayList<String> sdtsViewSearch, ArrayList<String> idContactViewSearch) {
        ContactAdapter contactadapter = new ContactAdapter(this, namesViewSearch, sdtsViewSearch, idContactViewSearch);
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
                        Intent intent = new Intent(Contact.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price :
                        Intent intent2 = new Intent(Contact.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms :
                        Intent intent3 = new Intent(Contact.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money :
                        Intent intent4 = new Intent(Contact.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(Contact.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(Contact.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(Contact.this, viewSmsNotMoney.class);
                        startActivity(intent7);
                        return true;
                    case R.id.historySms:
                        Intent intent8 = new Intent(Contact.this, HistorySms.class);
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
        inflater.inflate(R.menu.menu_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addTelephone:
                Intent intent = new Intent(Contact.this, ContactAdd.class);
                startActivity(intent);
                return true;
            case R.id.deleteAll:
                DialogHandler appdialog = new DialogHandler();
                appdialog.Confirm(this, "Thông Báo", "Bạn có chắc xóa tất cả danh bạ đơn giá không?",
                        "Cancel", "OK", aproc(), bproc());
                return true;
            case R.id.deleteSelect:
                DialogHandler appdialog2 = new DialogHandler();
                appdialog2.Confirm(this, "Thông Báo", "Bạn có chắc xóa các danh mục đơn giá đã chọn không?",
                        "Cancel", "OK", aprocCheckBoxDelete(), bproc());
                return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Runnable aproc() {
        return new Runnable() {
            public void run() {
                sql = new DatabaseHelper(Contact.this);
                String table2 = sql.TABLE_NAME_2;
                sql.deleteAll(table2, "0");
                ArrayList<String> namesAlert = new ArrayList<>();
                ArrayList<String> sdtsAlert = new ArrayList<>();
                mylistView = (ListView) findViewById(R.id.mylistView);
                CustomAdapter customdapter = new CustomAdapter(Contact.this, namesAlert, sdtsAlert);
                mylistView.setAdapter(customdapter);
                Log.d("LogFile", "ban da nhan nut yes xoa ten ca danh ba");
            }
        };
    }

    public Runnable aprocCheckBoxDelete() {
        return new Runnable() {
            public void run() {
                mylistView = (ListView) findViewById(R.id.mylistView);
                sql = new DatabaseHelper(Contact.this);
                String table2 = sql.TABLE_NAME_2;
                for (int i = 0; i < mylistView.getChildCount(); i++) {
                    View v = mylistView.getChildAt(i);
                    CheckBox ch = (CheckBox) v.findViewById(R.id.checkBoxContact);
                    if (ch.isChecked()) {
                        sql.deleteData(table2, ch.getText().toString());
                    }
                }
                showItemListViewContact();
            }
        };
    }


    public Runnable bproc() {
        return new Runnable() {
            public void run() {
                Log.d("LogFile", "Ban da nhan nut No");
            }
        };
    }

}
