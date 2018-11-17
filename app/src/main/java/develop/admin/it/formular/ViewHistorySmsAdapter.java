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
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by IT-PC on 11/16/2018.
 */

public class ViewHistorySmsAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> content;
    private final Context context;

    public ViewHistorySmsAdapter(Context context, ArrayList<String> content) {
        super( context, R.layout.contactdapter, R.id.textViewContent, content );
        this.context = context;
        this.content = content;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from( getContext() );
        View rowView = layoutInflater.inflate( R.layout.viewhistorysmsadapter, null, true );
        TextView number = (TextView) rowView.findViewById(R.id.textViewSms);
        String [] textArr = content.get(position).split( "JavaCode" );
        String textValue = "";
        if (textArr[2] != null) {
            textValue += "<font>";
            if (textArr[2].equals( "1" )) {
                textValue += "<font color=\"blue\">Tin "+ String.valueOf(position + 1) +" (Đến)</font><br>";
                number.setGravity( Gravity.LEFT);
            } else if(textArr[2].equals( "2" )) {
                textValue += "<font color=\"blue\">Tin "+ String.valueOf(position + 1) +" (Đi)</font><br>";
                number.setGravity( Gravity.RIGHT);
            }
        } else {
            textValue += "<font>";
        }

        if (textArr[0] != null ) {
            textValue += "<font>" + textArr[0].trim() + "</font><br>";
        }
        if(textArr[1] != null) {
            textValue += "<font color=\"red\">" + textArr[1].trim() + "</font>";
        }
        textValue += "</font>";
        number.setText( Html.fromHtml(textValue));
        return rowView;
    }

}
