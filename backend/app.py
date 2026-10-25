# Flask API for the Android app. Run:  python app.py
from flask import Flask, request, jsonify
import pandas as pd, joblib

app = Flask(__name__)
model = joblib.load("heart_model.pkl")      # scaler + KNN in one pipeline
columns = joblib.load("columns.pkl")        # exact column order used in training

@app.route("/")
def home():
    return "Heart disease prediction API is running"

@app.route("/predict", methods=["POST"])
def predict():
    d = request.get_json(force=True)
    try:
        row = {
            "Age": float(d["age"]),
            "RestingBP": float(d["restingBP"]),
            "Cholesterol": float(d["cholesterol"]),
            "FastingBS": int(d["fastingBS"]),          # 0 or 1
            "MaxHR": float(d["maxHR"]),
            "Oldpeak": float(d["oldpeak"]),
        }
        # one-hot columns (the dropped category = all zeros)
        row["Sex_" + d["sex"]] = 1                      # M or F
        row["ChestPainType_" + d["chestPain"]] = 1      # TA, ATA, NAP, ASY
        row["RestingECG_" + d["restingECG"]] = 1        # Normal, ST, LVH
        row["ExerciseAngina_" + d["exerciseAngina"]] = 1  # Y or N
        row["ST_Slope_" + d["stSlope"]] = 1             # Up, Flat, Down
    except (KeyError, ValueError) as e:
        return jsonify({"error": "Bad input: " + str(e)}), 400

    df = pd.DataFrame([row]).reindex(columns=columns, fill_value=0)
    pred = int(model.predict(df)[0])
    prob = float(model.predict_proba(df)[0][1])
    return jsonify({"prediction": pred, "probability": round(prob, 3)})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
