package develop.admin.it.formular;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> names;
    private final ArrayList<String> sdts;
    private final ArrayList<String> id;
    private final Context context;

    public ContactAdapter(Context context, ArrayList<String> names, ArrayList<String> sdts, ArrayList<String> id) {
        super(context, R.layout.contactdapter, R.id.textViewName, names);
        this.context = context;
        this.names = names;
        this.sdts = sdts;
        this.id = id;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View rowView = layoutInflater.inflate(R.layout.contactdapter, null, true);
        TextView textViewnames = (TextView) rowView.findViewById(R.id.textViewName);
        TextView textViewsdt = (TextView) rowView.findViewById(R.id.textViewSDT);
        final TextView textViewDongiaId = (TextView) rowView.findViewById(R.id.textViewDongiaId);
        CheckBox checkBoxContact = (CheckBox) rowView.findViewById(R.id.checkBoxContact);
        Button buttonEdit = (Button) rowView.findViewById(R.id.buttonEditDonGia);
        textViewnames.setText(names.get(position));
        textViewsdt.setText(sdts.get(position));
        checkBoxContact.setText(id.get(position));
        textViewDongiaId.setText(id.get(position));
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String donGiaId = textViewDongiaId.getText().toString();
                Intent intent = new Intent(context, EditDonGia.class);
                intent.putExtra("donGiaId", donGiaId);
                context.startActivity(intent);
            }
        });
        return rowView;
    }

}
