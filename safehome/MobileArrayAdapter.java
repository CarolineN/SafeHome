package com.example.caroline.safehome;

/**
 * Created by Caroline on 4/11/2016.
 */




        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

public class MobileArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public MobileArrayAdapter(Context context, String[] values) {
        super(context, R.layout.list_mobile, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_mobile, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
        textView.setText(values[position]);

        // Change icon based on name
        String s = values[position];

        System.out.println(s);

        if (s.equals("My Location")) {
            imageView.setImageResource(R.drawable.smalllocation);
        } else if (s.equals("Start Journey")) {
            imageView.setImageResource(R.drawable.smalljourney);
        } else if (s.equals("My Followers")) {
            imageView.setImageResource(R.drawable.smallfollowers);
        } else if (s.equals("Notifications")) {
            imageView.setImageResource(R.drawable.smallnotification);
        }  else if (s.equals("Emergency Contact")) {
            imageView.setImageResource(R.drawable.smallemergency);
        }else if (s.equals("Messages")) {
            imageView.setImageResource(R.drawable.smallmessage);
        }
        else if(s.equals("Log Out")){
            imageView.setImageResource(R.drawable.logout);
        }
        else if(s.equals("Panic Button")){
            imageView.setImageResource(R.drawable.panic);
        }

        return rowView;
    }
}