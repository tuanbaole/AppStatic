package develop.admin.it.formular;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class GetListContact extends AppCompatActivity {
    public ArrayList<String> names, sdts;
    EditText editTextSearch;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_list_contact);
        sideBarMenu();

        names = getContactName();
        sdts = getContactNumber();
        initList(names,sdts);
        ListView mylistView = (ListView) findViewById(R.id.listViewContact);
        mylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView name = (TextView) view.findViewById(R.id.textViewName);
                TextView sdt = (TextView) view.findViewById(R.id.textViewSDT);
                String nameStr = name.getText().toString();
                String sdtStr = sdt.getText().toString();
                Intent intent = new Intent(GetListContact.this, ContactAdd.class);
                intent.putExtra("name", nameStr);
                intent.putExtra("sdt", sdtStr);
                startActivity(intent);
            }
        });

//        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
//        editTextSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // Call back the Adapter with current character to Filter
//                if (s.toString().equals("")) {
//                    initList(names, sdts);
//                } else {
//                    searchItem(s.toString(), names, sdts);
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

    }

//    public void searchItem(String textToSearch, ArrayList<String> namesViewSearch, ArrayList<String> sdtsViewSearch) {
//        ArrayList<String> namesa = new ArrayList<>();
//        ArrayList<String> sdtsa = new ArrayList<>();
//        for (int i = 0; i < namesViewSearch.size(); i++) {
//            if (namesViewSearch.get(i).toLowerCase().contains(textToSearch.toLowerCase())) {
//                namesa.add(namesViewSearch.get(i));
//                sdtsa.add(sdtsViewSearch.get(i));
//            }
//        }
//        CustomAdapter customdapter = new CustomAdapter(this, namesa, sdtsa);
//        ListView mylistView = (ListView) findViewById(R.id.listViewContact);
//        mylistView.setAdapter(customdapter);
//    }

    public void initList(ArrayList<String> namesViewSearch, ArrayList<String> sdtsViewSearch) {
        CustomAdapter customdapter = new CustomAdapter(this, namesViewSearch, sdtsViewSearch);
        ListView mylistView = (ListView) findViewById(R.id.listViewContact);
        mylistView.setAdapter(customdapter);
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
                        Intent intent = new Intent(GetListContact.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price :
                        Intent intent2 = new Intent(GetListContact.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms :
                        Intent intent3 = new Intent(GetListContact.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money :
                        Intent intent4 = new Intent(GetListContact.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(GetListContact.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(GetListContact.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(GetListContact.this, viewSmsNotMoney.class);
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

    public ArrayList<String> getContactNumber() {
        ArrayList<String> inner = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, "lower(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ")" + " ASC");
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String number = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        String newNumber = number.replaceAll("[()-. ]+", "");
                        inner.add(newNumber);
                    }
                    pCur.close();
                }
            }
        }
        return inner;
    }

    public ArrayList<String> getContactName() {
        ArrayList<String> inner = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, "lower(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ")" + " ASC");
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                inner.add(cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME)));
            }
        }
        return inner;
    }
}
