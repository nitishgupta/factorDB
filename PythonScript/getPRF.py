# From Logistic CMF : python PythonScript/getPRF.py Embeddings_Prediction_Data WI HeldOut

import prf;
from os import walk
import sys

embeddingsPath = sys.argv[1]
folderToTest = sys.argv[2]
evToTest = sys.argv[3]

path = embeddingsPath+"/"+folderToTest+"/pred-data/"+evToTest
print path
fs = []
for (dirpath, dirnames, filenames) in walk(path):
    fs.extend(filenames)
    break
fs.sort()
for fileName in fs:
	print fileName
	print prf.getPRF(path+"/"+fileName)


