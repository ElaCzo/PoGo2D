package com.example.pogo2d;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// extends array list
public class CustomAdapter extends ArrayAdapter<Object> {

    String [] result;
    Context context;
    int [] imageId;
    private static LayoutInflater inflater=null;
    // passer une list d'objets en argument
    public CustomAdapter(CollectionActivity activity, Object[] list) {
        super(activity, R.layout.activity_collection, list); // pas sûre du layout

        result=(String[])list[0];
        context=activity;
        imageId=(int[]) list[1];
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    /*@Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }*/

    public class Holder
    {
        TextView os_text;
        ImageView os_img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.item_pokemon, null);

        holder.os_text =(TextView) rowView.findViewById(R.id.nom);
        holder.os_img =(ImageView) rowView.findViewById(R.id.img);

        holder.os_text.setText(result[position]);
        holder.os_img.setImageResource(imageId[position]);

        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_SHORT).show();
            }
        });

        return rowView;
    }

}
