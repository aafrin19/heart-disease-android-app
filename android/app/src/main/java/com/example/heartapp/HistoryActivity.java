package com.example.heartapp;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView lvHistory;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        lvHistory = findViewById(R.id.lvHistory);
        Button btnClear = findViewById(R.id.btnClear);
        db = new DBHelper(this);

        loadList();

        btnClear.setOnClickListener(v -> {
            db.clearAll();
            loadList();
        });
    }

    private void loadList() {
        ArrayList<String> data = db.getAllRecords();
        lvHistory.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, data));
    }
}
