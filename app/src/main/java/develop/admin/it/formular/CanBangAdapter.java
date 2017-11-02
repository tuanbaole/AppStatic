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
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by IT on 10/25/2017.
 */

public class CanBangAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> names;
    private final ArrayList<String> sdts;
    private final ArrayList<String> id;
    private final Context context;

    public CanBangAdapter(Context context, ArrayList<String> names, ArrayList<String> sdts, ArrayList<String> id) {
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
        View rowView = layoutInflater.inflate(R.layout.customeradapter, null, true);
        TextView dongiaId = (TextView) rowView.findViewById(R.id.textViewIdContact);
        TextView sdtName = (TextView) rowView.findViewById(R.id.textViewInformation);
        dongiaId.setText(id.get(position));
        sdtName.setText(sdts.get(position) + "-" +names.get(position));

        Button sendSms = (Button) rowView.findViewById(R.id.buttonGuiCanBang);
        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CanBangSendSms.class);
                intent.putExtra("sdt", sdts.get(position));
                intent.putExtra("ten", names.get(position));
                context.startActivity(intent);
            }
        });
        return rowView;
    }

}

