package develop.admin.it.formular;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by IT-PC on 12/30/2017.
 */

public class tungtinAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> content;
    private final ArrayList<String> id;
    private final ArrayList<String> kieutin;
    private final Context context;

    public tungtinAdapter(Context context, ArrayList<String> content, ArrayList<String> id, ArrayList<String> kieutin) {
        super(context, R.layout.contactdapter, R.id.textViewContent, content);
        this.context = context;
        this.content = content;
        this.kieutin = kieutin;
        this.id = id;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View rowView = layoutInflater.inflate(R.layout.tungtinadapter, null, true);
        CheckBox checkBoxSms = (CheckBox) rowView.findViewById(R.id.checkBoxSms);
        checkBoxSms.setText(id.get(position));
        TextView textViewContent = (TextView) rowView.findViewById(R.id.textViewContent);
        if (kieutin.get(position).equals("send")) {
            textViewContent.setGravity(Gravity.RIGHT);
        }
        textViewContent.setText(Html.fromHtml(content.get(position)));
        return rowView;
    }
}
