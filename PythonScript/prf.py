## Call getPRF(fileName) to get [P, R, F]

import numpy as np;
from sklearn.metrics import precision_recall_fscore_support as prf

def readFile(fileName):
	y_pred = []
	y_true = []
	y_pred_true = []
	f = open(fileName, 'r');
	for line in f:
		line.strip();
		a = line.split("::")
		if(float(a[2].strip()) >= 0.5):
			pred = 1;
			y_pred.append(pred);
		else:
			pred = 0;
			y_pred.append(pred);
		
		true = float(a[3].strip());
		y_true.append(true);		
	y_pred_true.append(y_pred)
	y_pred_true.append(y_true)
	return y_pred_true

def getPRF(fileName):
	y_p_t = readFile(fileName)		
	y_pred = y_p_t[0];
	y_true = y_p_t[1];
	acc = prf(y_true, y_pred, average = 'micro');
	p = round(acc[0]*100, 1);
	r = round(acc[1]*100, 1);
	f = round(acc[2]*100, 1);
	return np.array([p,r,f])

#fileName = "A-A"
#print getPRF(fileName)

