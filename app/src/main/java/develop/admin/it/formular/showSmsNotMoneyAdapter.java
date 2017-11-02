package develop.admin.it.formular;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by IT on 8/3/2017.
 */

public class showSmsNotMoneyAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> names;
    private final Context context;
    DatabaseHelper sql;
    GlobalClass controller;

    public showSmsNotMoneyAdapter(Context context, ArrayList<String> names) {
        super(context, R.layout.showsmsnotmoneyadapter, R.id.textViewInformation, names);
        this.context = context;
        this.names = names;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        final View rowView = layoutInflater.inflate(R.layout.showsmsnotmoneyadapter, null, true);
        TextView infomation = (TextView) rowView.findViewById(R.id.textViewInformation);
        String htmlshow = names.get(position);
        infomation.setText(Html.fromHtml(htmlshow) );
        return rowView;
    }

}


