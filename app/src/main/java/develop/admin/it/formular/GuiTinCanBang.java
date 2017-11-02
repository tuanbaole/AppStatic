package develop.admin.it.formular;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class GuiTinCanBang extends AppCompatActivity {
    DatabaseHelper sql;
    public ArrayList<String> names, sdts, idContact;
    ListView mylistView;
    Button sendSms;
    TextView textViewDate;
    GlobalClass controller = new GlobalClass();

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gui_tin_can_bang);
        String today = controller.dateDay("yyyy-MM-dd");
        showItemListViewContact(today);
        sideBarMenu();
    }

    private void showItemListViewContact(String getDays) {
        sql = new DatabaseHelper(this);
        String query = "SELECT SDT FROM setingsend_table WHERE 1 LIMIT 1";
        Cursor setingsend_table = sql.getAllDb(query);

        names = new ArrayList<>();
        sdts = new ArrayList<>();
        idContact = new ArrayList<>();
        if (setingsend_table.getCount() > 0) {
            setingsend_table.moveToFirst();
            String SDTDB2 = setingsend_table.getString(setingsend_table.getColumnIndex("SDT"));
            String [] sdtArr = SDTDB2.split("JAVA");
            for (int i = 0; i < sdtArr.length; i++) {
                String [] detailSdt = sdtArr[i].split("---");
                if (detailSdt.length == 2) {
                    names.add(detailSdt[0]);
                    sdts.add(detailSdt[1]);
                    idContact.add(String.valueOf(i));
                }
            }
            mylistView = (ListView) findViewById(R.id.mylistView);
            // hien thi them ra ngoai
            CanBangAdapter contactadapter = new CanBangAdapter(this, names, sdts, idContact);
            mylistView.setAdapter(contactadapter);
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
                        Intent intent = new Intent(GuiTinCanBang.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(GuiTinCanBang.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(GuiTinCanBang.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(GuiTinCanBang.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(GuiTinCanBang.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(GuiTinCanBang.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(GuiTinCanBang.this, viewSmsNotMoney.class);
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
