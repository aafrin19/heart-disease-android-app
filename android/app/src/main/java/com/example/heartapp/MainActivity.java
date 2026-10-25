package com.example.heartapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // ====== CHANGE THIS ======
    // Emulator:        http://10.0.2.2:5000/predict
    // Real phone:      http://YOUR_LAPTOP_IP:5000/predict   (same Wi-Fi), e.g. http://192.168.1.5:5000/predict
    // Hosted (Render): https://your-app.onrender.com/predict
    private static final String API_URL = "http://10.0.2.2:5000/predict";

    EditText etName, etAge, etBp, etChol, etHr, etOldpeak;
    Spinner spSex, spCp, spEcg, spSlope;
    CheckBox cbFbs, cbAngina;
    TextView tvResult;
    Button btnPredict, btnHistory;
    DBHelper db;
    RequestQueue queue;

    // Text shown to the user  ->  code the model understands
    String[] sexLabels = {"Male", "Female"};
    String[] sexCodes = {"M", "F"};
    String[] cpLabels = {"Typical angina", "Atypical angina", "Non-anginal pain", "Asymptomatic"};
    String[] cpCodes = {"TA", "ATA", "NAP", "ASY"};
    String[] ecgLabels = {"Normal", "ST-T abnormality", "Left ventricular hypertrophy"};
    String[] ecgCodes = {"Normal", "ST", "LVH"};
    String[] slopeLabels = {"Upsloping", "Flat", "Downsloping"};
    String[] slopeCodes = {"Up", "Flat", "Down"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etBp = findViewById(R.id.etBp);
        etChol = findViewById(R.id.etChol);
        etHr = findViewById(R.id.etHr);
        etOldpeak = findViewById(R.id.etOldpeak);
        spSex = findViewById(R.id.spSex);
        spCp = findViewById(R.id.spCp);
        spEcg = findViewById(R.id.spEcg);
        spSlope = findViewById(R.id.spSlope);
        cbFbs = findViewById(R.id.cbFbs);
        cbAngina = findViewById(R.id.cbAngina);
        tvResult = findViewById(R.id.tvResult);
        btnPredict = findViewById(R.id.btnPredict);
        btnHistory = findViewById(R.id.btnHistory);

        db = new DBHelper(this);
        queue = Volley.newRequestQueue(this);

        setupSpinner(spSex, sexLabels);
        setupSpinner(spCp, cpLabels);
        setupSpinner(spEcg, ecgLabels);
        setupSpinner(spSlope, slopeLabels);

        btnPredict.setOnClickListener(v -> predict());
        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, HistoryActivity.class)));
    }

    private void setupSpinner(Spinner sp, String[] items) {
        sp.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, items));
    }

    private void predict() {
        final String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String bp = etBp.getText().toString().trim();
        String chol = etChol.getText().toString().trim();
        String hr = etHr.getText().toString().trim();
        String oldpeak = etOldpeak.getText().toString().trim();

        if (name.isEmpty() || age.isEmpty() || bp.isEmpty() || chol.isEmpty()
                || hr.isEmpty() || oldpeak.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        final int ageInt = Integer.parseInt(age);

        JSONObject body = new JSONObject();
        try {
            body.put("age", ageInt);
            body.put("sex", sexCodes[spSex.getSelectedItemPosition()]);
            body.put("chestPain", cpCodes[spCp.getSelectedItemPosition()]);
            body.put("restingBP", Integer.parseInt(bp));
            body.put("cholesterol", Integer.parseInt(chol));
            body.put("fastingBS", cbFbs.isChecked() ? 1 : 0);
            body.put("restingECG", ecgCodes[spEcg.getSelectedItemPosition()]);
            body.put("maxHR", Integer.parseInt(hr));
            body.put("exerciseAngina", cbAngina.isChecked() ? "Y" : "N");
            body.put("oldpeak", Double.parseDouble(oldpeak));
            body.put("stSlope", slopeCodes[spSlope.getSelectedItemPosition()]);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            return;
        }

        tvResult.setTextColor(Color.DKGRAY);
        tvResult.setText("Predicting...");
        btnPredict.setEnabled(false);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, API_URL, body,
                response -> {
                    btnPredict.setEnabled(true);
                    int pred = response.optInt("prediction", 0);
                    double prob = response.optDouble("probability", 0);
                    int percent = (int) Math.round(prob * 100);

                    String result;
                    if (pred == 1) {
                        result = "HIGH RISK of heart disease";
                        tvResult.setTextColor(Color.parseColor("#C62828"));
                    } else {
                        result = "LOW RISK of heart disease";
                        tvResult.setTextColor(Color.parseColor("#2E7D32"));
                    }
                    tvResult.setText(result + "\nModel confidence of disease: " + percent + "%");
                    db.insertRecord(name, ageInt, result, percent);
                },
                error -> {
                    btnPredict.setEnabled(true);
                    tvResult.setTextColor(Color.RED);
                    tvResult.setText("Could not reach the server.\nIs the Flask API running and the URL correct?");
                });

        req.setRetryPolicy(new DefaultRetryPolicy(15000, 0, 1f));
        queue.add(req);
    }
}
