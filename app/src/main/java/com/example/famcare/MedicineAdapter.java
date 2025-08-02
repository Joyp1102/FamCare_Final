package com.example.famcare;

import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class MedicineAdapter extends ArrayAdapter<Medicine> {
    public MedicineAdapter(Context ctx, List<Medicine> items) {
        super(ctx, 0, items);
    }

    @Override
    public View getView(int pos, View v, ViewGroup parent) {
        if (v == null)
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_medicine, parent, false);
        Medicine m = getItem(pos);
        ((TextView)v.findViewById(R.id.itemName)).setText(m.name);
        ((TextView)v.findViewById(R.id.itemDose)).setText("Dose: " + m.dose);
        ((TextView)v.findViewById(R.id.itemReason)).setText("Reason: " + m.reason);
        return v;
    }
}
