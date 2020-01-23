package com.example.pogo2d;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StringAdapter extends ArrayAdapter<String> {

    private final List<String> objects;
    private final int layoutResource;

    public StringAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.layoutResource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(this.layoutResource, null);
        }

        TextView title = convertView.findViewById(R.id.nom);
        title.setText(objects.get(position));

        return convertView;
    }
}
