package develop.admin.it.formular;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class XoaCongNoAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> names;
    private final ArrayList<String> sdts;
    private final ArrayList<String> id;
    private final String kieu;
    private final String date;
    private final Context context;
    DatabaseHelper sql;
    GlobalClass controller;

    public XoaCongNoAdapter(Context context, ArrayList<String> names, ArrayList<String> sdts, ArrayList<String> id,
                            String kieu, String date) {
        super(context, R.layout.xoacongnoadapter, R.id.textViewName, names);
        this.context = context;
        this.names = names;
        this.sdts = sdts;
        this.id = id;
        this.kieu = kieu;
        this.date = date;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        final View rowView = layoutInflater.inflate(R.layout.xoacongnoadapter, null, true);
        TextView dongiaId = (TextView) rowView.findViewById(R.id.textViewIdContact);
        TextView sdtName = (TextView) rowView.findViewById(R.id.textViewInformation);
        dongiaId.setText(id.get(position));
        sdtName.setText(sdts.get(position) + "-" + names.get(position));

        Button xoaCongNo = (Button) rowView.findViewById(R.id.buttonGuiCanBang);
        xoaCongNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sql = new DatabaseHelper(context);
                Cursor solieu_sdt = sql.getAllDb("SELECT SMSID FROM solieu_table where SDT=\""
                        + sdts.get(position) + "\" AND NGAY=\"" + date + "\"");
                String smsIdArr = "0,";
                if (solieu_sdt.getCount() > 0) {
                    while (solieu_sdt.moveToNext()) {
                        smsIdArr += solieu_sdt.getString(solieu_sdt.getColumnIndex("SMSID")) + ",";
                    }
                }

                smsIdArr = smsIdArr.substring(0, smsIdArr.length() - 1);
                String table5 = sql.TABLE_NAME_5;
                String table6 = sql.TABLE_NAME_6;
                sql.deleteCongNo(table5, smsIdArr);
                sql.deleteCongNo(table6, smsIdArr);
                sql.deleteCongNoReady();
                sql.deleteCongNoContent();
                rowView.setVisibility(View.GONE);
                Toast.makeText(context, "Xóa thành công dữ liệu", Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }

}

