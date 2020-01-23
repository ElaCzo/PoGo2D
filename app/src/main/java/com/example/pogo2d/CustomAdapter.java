package com.example.pogo2d;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

// extends array list
public class CustomAdapter extends ArrayAdapter<Object> {

    private static LayoutInflater inflater = null;
    ArrayList<String> result;
    Context context;
    ArrayList<Bitmap> imageBitmap;

    public CustomAdapter(CollectionActivity activity, Object[] list) {
        super(activity, R.layout.activity_collection, list);

        result = (ArrayList<String>) list[0];
        context = activity;
        imageBitmap = (ArrayList<Bitmap>) list[1];
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.item_pokemon, null);

        holder.os_text = (TextView) rowView.findViewById(R.id.nom);
        holder.os_img = (ImageView) rowView.findViewById(R.id.img);

        holder.os_text.setText(result.get(position));
        holder.os_img.setImageBitmap(imageBitmap.get(position));

        rowView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Clicked " + result.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        return rowView;
    }

    public class Holder {
        TextView os_text;
        ImageView os_img;
    }
}
