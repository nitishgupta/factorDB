# From Repo Folder : python PythonScript/combineAllPredData.py Embeddings_Prediction_Data HeldOut

import sys
import os
from os import walk

embeddingsPath = sys.argv[1];
evToMerge = sys.argv[2];
print embeddingsPath+"/All/pred-data/"+evToMerge
if not os.path.exists(embeddingsPath+"/All/pred-data/"+evToMerge):
	    os.makedirs(embeddingsPath+"/All/pred-data/"+evToMerge)

foldersToMerge = ['AZ', 'NV', 'WI', 'EDH']

print embeddingsPath+"/WI/pred-data/"+evToMerge
files = []
for (dirpath, dirnames, filenames) in walk(embeddingsPath+"/WI/pred-data/"+evToMerge):
    files.extend(filenames)
    break

print files;

for folder in foldersToMerge:
	for filename in files:
		with open(embeddingsPath+"/All/pred-data/"+evToMerge+"/"+filename, 'a') as outfile:
			with open(embeddingsPath+"/"+folder+"/pred-data/"+evToMerge+"/"+filename) as infile:
			    for line in infile:
				outfile.write(line)

