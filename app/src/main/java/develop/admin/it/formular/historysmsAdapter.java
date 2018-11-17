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

import java.util.ArrayList;

public class historysmsAdapter extends ArrayAdapter<String> {
    private final ArrayList<String> content;
    private final Context context;
    private final String date;

    public historysmsAdapter(Context context, ArrayList<String> content,String date) {
        super( context, R.layout.contactdapter, R.id.textViewContent, content );
        this.context = context;
        this.content = content;
        this.date = date;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from( getContext() );
        View rowView = layoutInflater.inflate( R.layout.historysmsapdapter, null, true );
        Button number = (Button) rowView.findViewById(R.id.buttonSdt);
        number.setText( content.get(position).replace( ","," " ));
        number.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewHistorySms.class);
                intent.putExtra("nameandsdt", content.get(position));
                intent.putExtra("date", date );
                context.startActivity(intent);
            }
        } );
        return rowView;
    }
}
