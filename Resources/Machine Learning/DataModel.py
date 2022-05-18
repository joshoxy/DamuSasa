import numpy as np
import pandas as pd
import pickle

from ML3 import KNN
df = pd.read_csv('transfusion.csv')
df.fillna(0,inplace=True)
#df.sample(100)
#np.any(np.isnan(df))
# X = df.drop(columns=['donated blood in March 2007'])
# y = df['donated blood in March 2007']

X = df.iloc[:, :-4].values
y = df.iloc[:, 4].values

from sklearn.model_selection import train_test_split
X_train,X_test,y_train,y_test = train_test_split(X,y,test_size=0.2,random_state=2)
# print(df)


from sklearn.linear_model import LinearRegression
regressor = LinearRegression()
regressor.fit(X_train, y_train)

# Predicting the Test set results
y_pred = regressor.predict(X_test)

# Saving model using pickle
pickle.dump(regressor, open('model.pkl','wb'))

# Loading model to compare the results
model = pickle.load( open('model.pkl','rb'))
print(model.predict([[2]]))

#train the model
# from sklearn.ensemble import RandomForestClassifier
# from sklearn.metrics import accuracy_score
# rf = RandomForestClassifier()
# rf.fit(X_train,y_train)


# y_pred = rf.predict(X_test)
# accuracy = accuracy_score(y_test,y_pred)
# # print(accuracy_score(y_test,y_pred))
# round_accuracy = round(accuracy*100, 2)  #Round off to 2dp
# print(round_accuracy)


# #save the model in pickle format
# import pickle 
# pickle.dump(rf,open('model.pkl','wb'))  #save the model as"model.pkl"