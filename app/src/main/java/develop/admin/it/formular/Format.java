package develop.admin.it.formular;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Format extends AppCompatActivity {
    GlobalClass controller = new GlobalClass();
    DatabaseHelper sql;
    TableLayout table;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_format );
        sql = new DatabaseHelper( this );
        checkIntenet();
        sideBarMenu();
    }

    private void checkIntenet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    new ReadXml().execute( "http://hostingkqxs.esy.es/boso.php" );
//                    new ReadXml().execute( "https://hostingkqsx.000webhostapp.com/" );
                }
            } );
        } else {
            controller.showAlertDialog(Format.this, "Thong Bao", "Khong ket noi duoc internet");
        }
    }

    private void addTbale(Cursor kiheu) {
        table = (TableLayout) findViewById( R.id.tableLayoutKiHieu );
        int i = 0;
        while (kiheu.moveToNext()) {
            TableRow tableRow2 = new TableRow( Format.this );
            TextView tvid = new TextView( Format.this );
            TextView tvid2 = new TextView( Format.this );
            tvid.setText( kiheu.getString( kiheu.getColumnIndex( "KIHIEU" ) ) );
            tvid2.setText( kiheu.getString( kiheu.getColumnIndex( "DAYSO" ) ) );
            tvid2.setPadding(20,0,15,0);
            if (i % 2 == 0) {

                tvid.setBackgroundColor( Color.WHITE );
                tvid2.setBackgroundColor( Color.WHITE );
            } else {
                tvid.setBackgroundColor( Color.GRAY );
                tvid2.setBackgroundColor( Color.GRAY );
            }

            tableRow2.addView( tvid );
            tableRow2.addView( tvid2 );
            table.addView( tableRow2 );
            i++;
        }

    }

    class ReadXml extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String kihieu = controller.getXMLFromUrl( params[0] );
            return kihieu;
        }

        @Override
        protected void onPostExecute(String s) {
            sql = new DatabaseHelper( Format.this );
            sql.deleteAll( "kihieu_table", "0" );
            JSONObject json = null;
            try {
                json = new JSONObject( s );
                Iterator<?> keys = json.keys();
                int j = 0;
                while (keys.hasNext()) {
                    j++;
                    String key = (String) keys.next();
                    JSONArray kiHieu = json.getJSONArray( key );
                    if (!kiHieu.isNull( 0 )) {
                        String kiHieuinsert = key;
                        sql.insertDataKiHieu( kiHieuinsert, kiHieu.getString( 0 ), 3, j );
                    }
                }
                sql = new DatabaseHelper( Format.this );
                Cursor kiheu2 = sql.getAllDb( "SELECT *  FROM kihieu_table ORDER BY KIHIEU DESC;" );
                addTbale(kiheu2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                        Intent intent = new Intent(Format.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(Format.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(Format.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(Format.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(Format.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(Format.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(Format.this, viewSmsNotMoney.class);
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
