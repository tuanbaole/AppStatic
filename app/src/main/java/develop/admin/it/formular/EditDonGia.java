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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class EditDonGia extends AppCompatActivity {
    DatabaseHelper sql;
    GlobalClass co = new GlobalClass();
    EditText sdt, ten, hsde, hslo, hsbc, hsx2, hsx3, hsx4, cophan, thde, thlo, th3c, thg1,
            thx2, thx3, thx4, thlt10, thlt9, thlt8, thlt7,ngoi1,ngoi2;
    Button buttonSave;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    TextView donGiaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);

        sideBarMenu();
        sdt = (EditText) findViewById(R.id.editTextSDT);
        ten = (EditText) findViewById(R.id.editTextTen);
        hsde = (EditText) findViewById(R.id.editTextHSDE);
        hslo = (EditText) findViewById(R.id.editTextHSL);
        hsbc = (EditText) findViewById(R.id.editTextHS3C);
        hsx2 = (EditText) findViewById(R.id.editTextHSX2);
        hsx3 = (EditText) findViewById(R.id.editTextHSX3);
        hsx4 = (EditText) findViewById(R.id.editTextHSX4);
        cophan = (EditText) findViewById(R.id.editTextCOPHAN);
        thde = (EditText) findViewById(R.id.editTextTHDE);
        thlo = (EditText) findViewById(R.id.editTextTHLO);
        th3c = (EditText) findViewById(R.id.editTextTH3C);
//        thg1 = (EditText) findViewById(R.id.editTextTHG1);
        thx2 = (EditText) findViewById(R.id.editTextTHX2);
        thx3 = (EditText) findViewById(R.id.editTextTHX3);
        thx4 = (EditText) findViewById(R.id.editTextTHX4);
        thlt10 = (EditText) findViewById(R.id.editTextthLT10);
        thlt9 = (EditText) findViewById(R.id.editTextTHLT9);
        thlt8 = (EditText) findViewById(R.id.editTextTHLT8);
        thlt7 = (EditText) findViewById(R.id.editTextTHLT7);

        ngoi1 = (EditText) findViewById( R.id.editTextNgoiMot );
        ngoi2 = (EditText) findViewById( R.id.editTextNgoiHai );

        sdt.setEnabled(false);
        String getIdDonGia = "0";
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            getIdDonGia = bd.getString("donGiaId");
            donGiaId = (TextView) findViewById(R.id.idDongia);
            donGiaId.setText(getIdDonGia);
        }
        sql = new DatabaseHelper(this);
        Cursor dongiaEdit = sql.getAllDb("SELECT * FROM dongia_table WHERE ID=\"" + getIdDonGia + "\";");
        if (dongiaEdit.getCount() == 1) {
            dongiaEdit.moveToFirst();
            sdt.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("SDT")));
            ten.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("TEN")));
            hsde.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("HSDE")));
            hslo.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("HSLO")));
            hsbc.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("HSBACANG")));
            hsx2.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("HSXIENHAI")));
            hsx3.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("HSXIENBA")));
            hsx4.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("HSXIENBON")));
            cophan.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("COPHAN")));
            thde.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("THDE")));
            thlo.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("THLO")));
            th3c.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("THBACANG")));
//            thg1.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("THGIAINHAT")));
            thx2.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("THXIENHAI")));
            thx3.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("THXIENBA")));
            thx4.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("THXIENBON")));
            thlt10.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("THTRUOTMUOI")));
            thlt9.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("THTRUOTCHIN")));
            thlt8.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("THTRUOTTAM")));
            thlt7.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("THTRUOTBAY")));
            ngoi1.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("NGOIMOT")));
            ngoi2.setText(dongiaEdit.getString(dongiaEdit.getColumnIndex("NGOIHAI")));

            int kieutinnhan = Integer.parseInt(dongiaEdit.getString(dongiaEdit.getColumnIndex("KIEUCOPHAN")));
            final Spinner spinner = (Spinner) findViewById(R.id.spinner);
            // Create an ArrayAdapter using the string array and a default spinner layout
            if (kieutinnhan == 0) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.planets_array, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
            } else if (kieutinnhan == 1) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.planets_array1, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
            } else {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.planets_array2, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
            }

            buttonSave = (Button) findViewById(R.id.buttonSave);
            final String finalGetIdDonGia = getIdDonGia;
            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sdtUp = sdt.getText().toString();
                    String tenUp = ten.getText().toString();

                    String hsdeText = hsde.getText().toString();
                    double hsdeUp = Double.parseDouble(hsdeText);

                    String hsloText = hslo.getText().toString();
                    double hsloUp = Double.parseDouble(hsloText);

                    String hsbcText = hsbc.getText().toString();
                    double hsbcUp = Double.parseDouble(hsbcText);

                    String hsx2Text = hsx2.getText().toString();
                    double hsx2Up = Double.parseDouble(hsx2Text);

                    String hsx3Text = hsx3.getText().toString();
                    double hsx3Up = Double.parseDouble(hsx3Text);

                    String hsx4Text = hsx4.getText().toString();
                    double hsx4Up = Double.parseDouble(hsx4Text);

                    String cophanText = cophan.getText().toString();
                    double cophanUp = Double.parseDouble(cophanText);

                    String thdeText = thde.getText().toString();
                    double thdeUp = Double.parseDouble(thdeText);

                    String thloText = thlo.getText().toString();
                    double thloUp = Double.parseDouble(thloText);

                    String th3cText = th3c.getText().toString();
                    double th3cUp = Double.parseDouble(th3cText);

//                    String thg1Text = thg1.getText().toString();
//                    double thg1Up = Double.parseDouble(thg1Text);
                    double thg1Up = 0;

                    String thx2Text = thx2.getText().toString();
                    double thx2Up = Double.parseDouble(thx2Text);

                    String thx3Text = thx3.getText().toString();
                    double thx3Up = Double.parseDouble(thx3Text);

                    String thx4Text = thx4.getText().toString();
                    double thx4Up = Double.parseDouble(thx4Text);

                    String thlt10Text = thlt10.getText().toString();
                    double thlt10Up = Double.parseDouble(thlt10Text);

                    String thlt9Text = thlt9.getText().toString();
                    double thlt9Up = Double.parseDouble(thlt9.getText().toString());

                    String thlt8Text = thlt8.getText().toString();
                    double thlt8Up = Double.parseDouble(thlt8Text);

                    String thlt7Text = thlt7.getText().toString();
                    double thlt7Up = Double.parseDouble(thlt7Text);

                    String ngoiSo1 = ngoi1.getText().toString();
                    String ngoiSo2 = ngoi2.getText().toString();
                    String kieu = spinner.getSelectedItem().toString();

                    if (sdtUp.trim().equals("") || tenUp.trim().equals("") || hsdeText.trim().equals("") ||
                            hsloText.trim().equals("") || hsbcText.trim().equals("") || hsx2Text.trim().equals("") ||
                            hsx3Text.trim().equals("") || hsx4Text.trim().equals("") || cophanText.trim().equals("") ||
                            thdeText.trim().equals("") || thloText.trim().equals("") || th3cText.trim().equals("") ||
                            thx2Text.trim().equals("") || thx3Text.trim().equals("") ||
                            thx4Text.trim().equals("") || thlt10Text.trim().equals("") || thlt9Text.trim().equals("") ||
                            thlt8Text.trim().equals("") || thlt7Text.trim().equals("")
                            ) {
                        co.showAlertDialog(EditDonGia.this, "Thông báo", "Làm ơn hãy điền hết các ô giá trị");
                        return;
                    }
                    int kieucophan = 0;
                    if (kieu.indexOf("+") > -1) {
                        kieucophan = 0;
                    } else if (kieu.indexOf("đến") > 1) {
                        kieucophan = 1;
                    } else {
                        kieucophan = 2;
                    }
                    boolean insert = sql.updateDonGia(finalGetIdDonGia,sdtUp, tenUp, hsdeUp, hsloUp, hsbcUp, hsx2Up, hsx3Up, hsx4Up, cophanUp, thdeUp,
                            thloUp, th3cUp, thg1Up, thx2Up, thx3Up, thx4Up, thlt10Up, thlt9Up, thlt8Up, thlt7Up,
                            kieucophan,ngoiSo1,ngoiSo2);
                    Intent intent = new Intent(EditDonGia.this,Contact.class);
                    if (insert) {
                        intent.putExtra("noti", "insertSuccess");
                    } else {
                        intent.putExtra("noti", "insertCancel");
                    }
                    startActivity(intent);
                }
            });
        } else {
            Intent intent = new Intent(EditDonGia.this,Contact.class);
            intent.putExtra("noti", "notId");
            startActivity(intent);

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
                        Intent intent = new Intent(EditDonGia.this, MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.unit_price :
                        Intent intent2 = new Intent(EditDonGia.this, Contact.class);
                        startActivity(intent2);
                        return true;
                    case R.id.send_sms :
                        Intent intent3 = new Intent(EditDonGia.this, Customer.class);
                        startActivity(intent3);
                        return true;
                    case R.id.manage_money :
                        Intent intent4 = new Intent(EditDonGia.this, ManagerMoney.class);
                        startActivity(intent4);
                        return true;
                    case R.id.statistic:
                        Intent intent5 = new Intent(EditDonGia.this, Statistic.class);
                        startActivity(intent5);
                        return true;
                    case R.id.SmsAgain:
                        Intent intent6 = new Intent(EditDonGia.this, MainXoaCongNo.class);
                        startActivity(intent6);
                        return true;
                    case R.id.smsnook:
                        Intent intent7 = new Intent(EditDonGia.this, viewSmsNotMoney.class);
                        startActivity(intent7);
                        return true;
                    case R.id.historySms:
                        Intent intent8 = new Intent(EditDonGia.this, HistorySms.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
