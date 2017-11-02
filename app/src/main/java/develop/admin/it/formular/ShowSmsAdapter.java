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

/**
 * Created by IT on 7/20/2017.
 */

public class ShowSmsAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> body;
    private final Context context;

    public ShowSmsAdapter(Context context, ArrayList<String> body) {
        super(context, R.layout.showsmsadapter, R.id.textViewBody, body);
        this.context = context;
        this.body = body;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View rowView = layoutInflater.inflate(R.layout.showsmsadapter, null, true);
        TextView bodyTv = (TextView) rowView.findViewById(R.id.textViewBody);
        if (body.get(position).length()>50) {
            String subBody = body.get(position).substring(0,50);
            bodyTv.setText(subBody+"...");
        } else {
            bodyTv.setText(body.get(position));
        }

        return rowView;
    }

}

