package com.example.automativesurvalliancesix;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AlertsAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> alertData;

    public AlertsAdapter(Context context, List<String> alertData) {
        super(context, 0, alertData);
        this.context = context;
        this.alertData = alertData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String alert = getItem(position);
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_alert, parent, false);
        }

        TextView alertTextView = view.findViewById(R.id.alert_text);
        alertTextView.setText(alert);

        return view;
}
}

