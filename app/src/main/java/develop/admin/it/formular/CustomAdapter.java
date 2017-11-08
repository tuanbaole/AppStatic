package develop.admin.it.formular;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> names;
    private final ArrayList<String> sdts;
    private final Context context;

    public CustomAdapter(Context context, ArrayList<String> names, ArrayList<String> sdts) {
        super(context, R.layout.customadapter, R.id.textViewName, names);
        this.context = context;
        this.names = names;
        this.sdts = sdts;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View rowView = layoutInflater.inflate(R.layout.customadapter, null, true);
        TextView textViewnames = (TextView) rowView.findViewById(R.id.textViewName);
        TextView textViewsdt = (TextView) rowView.findViewById(R.id.textViewSDT);
        textViewnames.setText(names.get(position));
        textViewsdt.setText(sdts.get(position));
        return rowView;
    }
}
