# Rebuilds the friend's KNN model with the same preprocessing as heart.ipynb,
# but fits the scaler on TRAINING data only and saves model + scaler together.
import pandas as pd, joblib
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.neighbors import KNeighborsClassifier
from sklearn.pipeline import Pipeline
from sklearn.metrics import accuracy_score, f1_score

df = pd.read_csv("heart.csv")
# same cleaning as notebook: zeros in Cholesterol / RestingBP replaced by mean
for col in ["Cholesterol", "RestingBP"]:
    mean = df.loc[df[col] != 0, col].mean()
    df[col] = df[col].replace(0, mean).round(2)

df_enc = pd.get_dummies(df, drop_first=True).astype(float)
X = df_enc.drop("HeartDisease", axis=1)
y = df_enc["HeartDisease"]
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.33, random_state=42)

pipe = Pipeline([("scaler", StandardScaler()), ("knn", KNeighborsClassifier())])
pipe.fit(X_train, y_train)
pred = pipe.predict(X_test)
print("Accuracy:", round(accuracy_score(y_test, pred), 4), "F1:", round(f1_score(y_test, pred), 4))

joblib.dump(pipe, "heart_model.pkl")
joblib.dump(X.columns.tolist(), "columns.pkl")
print("Saved heart_model.pkl and columns.pkl")
print(X.columns.tolist())
