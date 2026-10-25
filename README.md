# Heart Disease Predictor – Android App

A simple Android app (XML + Java + SQLite) that predicts heart disease risk. The app sends the patient's details to a small Flask API, which runs a trained **KNN machine-learning model** and returns the result.

> **Education only – not medical advice.**

## How it works

```
Android app (XML + Java)  --JSON-->  Flask API (Python)  -->  KNN model
        |                                                          |
   SQLite history  <------------- prediction + confidence <--------+
```

## Features
- Form with 11 medical inputs (age, sex, chest pain type, blood pressure, cholesterol, fasting blood sugar, resting ECG, max heart rate, exercise angina, oldpeak, ST slope)
- Result: **High risk / Low risk** with the model's confidence percentage
- Past results saved in a local **SQLite** database, with a History screen

## Project structure
```
backend/    Flask API + model training script + dataset
android/    Android source files (layouts, Java classes, manifest)
screenshots/ App screenshots
```

## How to run

### 1. Backend
```
cd backend
pip install -r requirements.txt
python train_model.py     # creates heart_model.pkl for your scikit-learn version
python app.py             # API runs on http://localhost:5000
```

### 2. Android app
1. In Android Studio create a new **Empty Views Activity** project: name `HeartApp`, package `com.example.heartapp`, language **Java**, min SDK 24.
2. Add the Volley dependency (see `android/build_gradle_dependency.txt`) and sync.
3. Copy the files from `android/app/src/main/` into the same places in your project (layouts into `res/layout`, Java files into the package folder, replace `AndroidManifest.xml`).
4. In `MainActivity.java`, set `API_URL`:
   - Emulator: `http://10.0.2.2:5000/predict`
   - Real phone: `http://<your-laptop-IP>:5000/predict` (same Wi-Fi)
5. Run the app.

## Technologies
Android (Java, XML, SQLite, Volley) · Python · Flask · scikit-learn · pandas

## Credits
- The heart disease ML model and dataset come from **Aarushi Nahar's** project: https://github.com/naharaarushi/heart-prediction-disease
- The scaling in the model was re-fitted on training data only so it can be served by the API (`backend/train_model.py`).
- Android app, SQLite history and API integration: **<YOUR NAME>**

