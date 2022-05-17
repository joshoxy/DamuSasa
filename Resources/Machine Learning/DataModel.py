import numpy as np
import pandas as pd

from ML3 import KNN
df = pd.read_csv('transfusion.csv')
df.sample(100)
X = df.drop(columns=['donated blood in March 2007'])
y = df['donated blood in March 2007']
from sklearn.model_selection import train_test_split
X_train,X_test,y_train,y_test = train_test_split(X,y,test_size=0.2,random_state=2)
# print(df)

#train the model
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
rf = RandomForestClassifier()

clf = rf
clf.fit(X_train,y_train)

y_pred = rf.predict(X_test)
accuracy = accuracy_score(y_test,y_pred)
# print(accuracy_score(y_test,y_pred))
round_accuracy = round(accuracy*100, 2)  #Round off to 2dp
print(round_accuracy)


#save the model in pickle format
import pickle 
pickle.dump(clf,open('model.pkl','wb'))  #save the model as"model.pkl"