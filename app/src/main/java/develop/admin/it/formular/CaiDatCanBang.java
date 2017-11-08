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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CaiDatCanBang extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    EditText
            MAXDE, MAXLO, MAXBACANG,
            TenA, TenB, TenC, TenD, TenE,
            SDTA, SDTB, SDTC, SDTD, SDTE;
    DatabaseHelper sql;
    GlobalClass controller = new GlobalClass();
    Button luuCaiDat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cai_dat_can_bang);
        sideBarMenu();
        sql = new DatabaseHelper(CaiDatCanBang.this);

        MAXDE = (EditText) findViewById(R.id.editTextGioiHanDe);
        MAXLO = (EditText) findViewById(R.id.editTextGioiHanLo);
        MAXBACANG = (EditText) findViewById(R.id.editTextGioiHanBaCang);
        TenA = (EditText) findViewById(R.id.editTextTenA);
        TenB = (EditText) findViewById(R.id.editTextTenB);
        TenC = (EditText) findViewById(R.id.editTextTenC);
        TenD = (EditText) findViewById(R.id.editTextTenD);
        TenE = (EditText) findViewById(R.id.editTextTenE);
        SDTA = (EditText) findViewById(R.id.editTextSDTA);
        SDTB = (EditText) findViewById(R.id.editTextSDTB);
        SDTC = (EditText) findViewById(R.id.editTextSDTC);
        SDTD = (EditText) findViewById(R.id.editTextSDTD);
        SDTE = (EditText) findViewById(R.id.editTextSDTE);
        String tablename = sql.TABLE_NAME_9;
        Cursor getval = sql.getAllDb("SELECT * FROM " + tablename + " WHERE 1 LIMIT 1");
        if (getval.getCount() > 0) {
            getval.moveToFirst();
            String MaxDeDb = getval.getString(getval.getColumnIndex("MAXDE"));
            String MaxLoDb = getval.getString(getval.getColumnIndex("MAXLO"));
            String MaxBaCangDb = getval.getString(getval.getColumnIndex("MAXBACANG"));
            String SDTDB2 = getval.getString(getval.getColumnIndex("SDT"));
            MAXDE.setText(MaxDeDb);
            MAXLO.setText(MaxLoDb);
            MAXBACANG.setText(MaxBaCangDb);
            String[] sdtArr = SDTDB2.split("JAVA");
            for (int i = 0; i < sdtArr.length; i++) {
                String[] detailSdt = sdtArr[i].split("---");
                if (detailSdt.length == 2) {
                    switch (i) {
                        case 0:
                            TenA.setText(detailSdt[0]);
                            SDTA.setText(detailSdt[1]);
                            break;
                        case 1:
                            TenB.setText(detailSdt[0]);
                            SDTB.setText(detailSdt[1]);
                            break;
                        case 2:
                            TenC.setText(detailSdt[0]);
                            SDTC.setText(detailSdt[1]);
                            break;
                        case 3:
                            TenD.setText(detailSdt[0]);
                            SDTD.setText(detailSdt[1]);
                            break;
                        case 4:
                            TenE.setText(detailSdt[0]);
                            SDTE.setText(detailSdt[1]);
                            break;
                    }
                }
            }
        }

        luuCaiDat = (Button) findViewById(R.id.buttonLuuCaiDat);
        luuCaiDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String limitDe = MAXDE.getText().toString();
                String limitLo = MAXLO.getText().toString();
                String limitBaCang = MAXBACANG.getText().toString();
                String SDTAll = "";
                if (!TenA.getText().toString().equals("") && !SDTA.getText().toString().equals("")) {
                    SDTAll += TenA.getText().toString() + "---" + SDTA.getText().toString() + "JAVA";
                }
                if (!TenB.getText().toString().equals("") && !SDTB.getText().toString().equals("")) {
                    SDTAll += TenB.getText().toString() + "---" + SDTB.getText().toString() + "JAVA";
                }
                if (!TenC.getText().toString().equals("") && !SDTC.getText().toString().equals("")) {
                    SDTAll += TenC.getText().toString() + "---" + SDTC.getText().toString() + "JAVA";
                }
                if (!TenD.getText().toString().equals("") && !SDTD.getText().toString().equals("")) {
                    SDTAll += TenD.getText().toString() + "---" + SDTD.getText().toString() + "JAVA";
                }
                if (!TenE.getText().toString().equals("") && !SDTE.getText().toString().equals("")) {
                    SDTAll += TenE.getText().toString() + "---" + SDTE.getText().toString() + "JAVA";
                }
                String STDDB = SDTAll.replaceAll("(^\\s+|\\s+$)", "").trim();
                if (!limitDe.equals("") && !limitLo.equals("") && !limitBaCang.equals("") && !STDDB.equals("")) {
                    String table9 = sql.TABLE_NAME_9;
                    sql.deleteAll(table9, "0");
                    boolean insert = sql.insertCaiDatCanBang(limitDe, limitLo, limitBaCang, STDDB);
                    if (insert) {
                        controller.showAlertDialog(CaiDatCanBang.this, "Thông báo", "Lưu Thành Công");
                    } else {
                        controller.showAlertDialog(CaiDatCanBang.this, "Thông báo", "Đã Có Sự Cố Xảy");
                    }
                } else {
                    controller.showAlertDialog(CaiDatCanBang.this, "Thông báo", "Hãy Điền 3 giá trị đầu và ít nhất 1 SDT");
                }
            }
        });

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
                        Intent intent = new Intent(CaiDatCanBang.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price:
                        Intent intent2 = new Intent(CaiDatCanBang.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms:
                        Intent intent3 = new Intent(CaiDatCanBang.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money:
                        Intent intent4 = new Intent(CaiDatCanBang.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(CaiDatCanBang.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(CaiDatCanBang.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(CaiDatCanBang.this, viewSmsNotMoney.class);
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
