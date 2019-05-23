package develop.admin.it.formular;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class CaiDatChuyenTinActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    DatabaseHelper sql;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cai_dat_chuyen_tin );
        sideBarMenu();
        listContact();
    }

    protected void listContact() {
        ArrayList<String> getListContact = new ArrayList<>();
        sql = new DatabaseHelper(this);
        Cursor dongia = sql.getAllDb("SELECT * FROM dongia_table");
        if (dongia.getCount() > 0) {
            while (dongia.moveToNext()) {
                getListContact.add(dongia.getString(dongia.getColumnIndex("TEN")) +
                        " - " + dongia.getString(dongia.getColumnIndex("SDT")));
            }
        }
        Spinner listContact = (Spinner) findViewById( R.id.spinnerListContact );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( this,R.layout.spinner_layout,R.id.dropDownListContact,getListContact );
        listContact.setAdapter( adapter );
    }
    public void sideBarMenu() {
        /* SideBar */
        mDrawerLayout = (DrawerLayout) findViewById( R.id.drawer_layout );
        mDrawerToggle = new ActionBarDrawerToggle( this, mDrawerLayout, R.string.open, R.string.close );

        mNavigationView = (NavigationView) findViewById( R.id.navigation );
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked( false );
                else menuItem.setChecked( true );
                //Closing drawer on item click
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Intent intent = new Intent(CaiDatChuyenTinActivity.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price :
                        Intent intent2 = new Intent(CaiDatChuyenTinActivity.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms :
                        Intent intent3 = new Intent(CaiDatChuyenTinActivity.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money :
                        Intent intent4 = new Intent(CaiDatChuyenTinActivity.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(CaiDatChuyenTinActivity.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(CaiDatChuyenTinActivity.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(CaiDatChuyenTinActivity.this, viewSmsNotMoney.class);
                        startActivity(intent7);
                        return true;
                    case R.id.historySms:
                        Intent intent8 = new Intent(CaiDatChuyenTinActivity.this, HistorySms.class);
                        startActivity(intent8);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected( item )) {
            return true;
        }
        return super.onOptionsItemSelected( item );
    }
}
