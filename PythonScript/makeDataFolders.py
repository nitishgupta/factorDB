# From Repo Folder : python PythonScript/makeDataFolders.py 
import sys
import os
from os import walk

dataset = "Dataset/"

folders = ['AZ', 'NV', 'WI', 'EDH', 'ON', 'complete']


if not os.path.exists(dataset):
	os.makedirs(dataset)

if not os.path.exists(dataset+"data"):
	os.makedirs(dataset+"data")

if not os.path.exists(dataset+"json"):
	os.makedirs(dataset+"json")

for folder in folders:
	if not os.path.exists(dataset+"json/"+folder):
		os.makedirs(dataset+"json/"+folder)



for folder in folders:
	if not os.path.exists(dataset+"data/"+folder):
		os.makedirs(dataset+"data/"+folder)


em_pred = "Embeddings_Prediction_Data/"
evs = ['AttBusCold', 'RateBusCold', 'RateUserCold', 'HeldOut']

if not os.path.exists(em_pred):
	os.makedirs(em_pred)

if not os.path.exists(em_pred+"All"):
	os.makedirs(em_pred+"All")
	os.makedirs(em_pred+"All/"+"pred-data")
	for ev in evs:
		os.makedirs(em_pred+"All"+"/pred-data/"+ev)		



for folder in folders:
	if not os.path.exists(em_pred+folder):
		os.makedirs(em_pred+folder)	
		os.makedirs(em_pred+folder+"/pred-data")
		for ev in evs:
			os.makedirs(em_pred+folder+"/pred-data/"+ev)		
		os.makedirs(em_pred+folder+"/embeddings")
		for ev in evs:
			os.makedirs(em_pred+folder+"/embeddings/"+ev)		
							
	



