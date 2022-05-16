from calendar import c
import numpy as np
import pandas as pd
from sklearn import neighbors, metrics
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder

data = pd.read_csv("transfusion.csv")

print(data)

le = LabelEncoder()

X = data[['Frequency (times)', 'Recency (months)', 'Monetary (c.c. blood)','Time (months)']].values
X = np.array(X)

#Select the label
y = data[['donated blood in March 2007']].values
y = np.array(y)


#Create model
knn = neighbors.KNeighborsClassifier(n_neighbors=25, weights="uniform")

#Train the model
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.5)
knn.fit(X_train, y_train)

predictions = knn.predict(X_test)

accuracy = metrics.accuracy_score(y_test, predictions)
print("Predictions:", predictions)
print("Accuracy:", accuracy)

#index_of_instance = 1727
#print("Actual value: ", y[index_of_instance])
#print("Predicted value: ", knn.predict(X)[index_of_instance])


