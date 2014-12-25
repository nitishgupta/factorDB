# From  Logistic_CMF : python PythonScript/getPRCurveData.py Embeddings_Prediction_Data All HeldOut

from os import walk
import sys
import os

embeddingsPath = sys.argv[1]
folderToTest = sys.argv[2]
evToTest = sys.argv[3]

path = embeddingsPath+"/"+folderToTest+"/pred-data/"+evToTest

files = []
for (dirpath, dirnames, filenames) in walk(path):
    files.extend(filenames)
    break

for fileName in files:
	f = open(path+"/"+fileName, 'r')
	if not os.path.exists(path+"/PRCurve"):
	    os.makedirs(path+"/PRCurve")
	o = open(path+"/PRCurve/"+fileName, 'w')
	for line in f:
		line.strip();
		a = line.split("::")
		o.write(a[2].strip() + "\t" + a[3].strip() + "\n");
	o.close();
	f.close();
