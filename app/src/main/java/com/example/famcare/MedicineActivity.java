package com.example.famcare;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MedicineActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new MedicineFragment())
                .commit();
    }
}
