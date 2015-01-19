# From Repo Folder : python PythonScript/makeDataFolders.py 
import sys
import os
from os import walk

dataset = "Dataset1/"

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
		os.makedirs(dataset+"data/"+folder+"/embeddings")
		os.makedirs(dataset+"data/"+folder+"/pred-data")

