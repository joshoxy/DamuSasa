import numpy as np
import pandas as pd

from ML3 import KNN
df = pd.read_csv('transfusion.csv')
df.sample(5)
X = df.drop(columns=['donated blood in March 2007'])
y = df['donated blood in March 2007']
from sklearn.model_selection import train_test_split
X_train,X_test,y_train,y_test = train_test_split(X,y,test_size=0.2,random_state=2)
# print(df)

#train the model
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score
rf = RandomForestClassifier()
rf.fit(X_train,y_train)
y_pred = rf.predict(X_test)
accuracy = accuracy_score(y_test,y_pred)
# print(accuracy_score(y_test,y_pred))
round_accuracy = round(accuracy*100, 2)  #Round off to 2dp
print(round_accuracy)


#save the model in pickle format
import pickle 
pickle.dump(rf,open('model.pkl','wb'))

from flask import Flask,request,jsonify
import numpy as np
import pickle
model = pickle.load(open('model.pkl','rb'))
app = Flask(__name__)
@app.route('/')
def index():
    #return str(round_accuracy)
    return "Hello world"

@app.route('/predict',methods=['POST'])
def predict():
    Recency = request.form.get('Recency (months)')
    Frequency = request.form.get('Frequency (times)')
    Monetary = request.form.get('Monetary (c.c. blood)')
    Time = request.form.get('Time (months)')
    input_query = np.array([[Recency,Frequency,Monetary,Time]])
    result = model.predict(input_query)[0]
    return jsonify({'prediction':str(result)})
if __name__ == '__main__':
    app.run(debug=True)