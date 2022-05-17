from flask import Flask,request,jsonify
import numpy as np
import pickle
model = pickle.load(open('model.pkl','rb'))
print(model)

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