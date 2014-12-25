# From Logisitc CMF : python PythonScript/writePRFTable.py Embeddings_Prediction_Data A HeldOut

import sys
import os
from os import walk
import prf

embeddingsPath = sys.argv[1]
relation = sys.argv[2]
evToTest = sys.argv[3]

writePath = embeddingsPath+"/Tables/"+relation+"-"+evToTest
out = open(writePath, 'w')

foldersToWrite = ['AZ', 'NV', 'WI', 'EDH', 'All']
filesToWrite = []
for (dirpath, dirnames, filenames) in walk(embeddingsPath+"/WI/pred-data/"+evToTest):
	for filename in filenames:
		r = filename.split("-")[0].strip()
		if r == relation:
			filesToWrite.append(filename)
	break

for model in filesToWrite:
	out.write("\\textbf{"+model.split("-")[1].strip()+"}\n")
	for folder in foldersToWrite:
		PRF = prf.getPRF(embeddingsPath+"/"+folder+"/pred-data/"+evToTest+"/"+model)
		out.write("& " + str(PRF[0]) + "\t & " + str(PRF[1]) + "\t & " + str(PRF[2]) + "\n")
	out.write("\\\\ \n")
		
	

	



