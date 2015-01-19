#!/usr/bin/env python
# -*- coding: utf-8 -*-

## Inputs review file in format

# businessId : asnhdjkasld
# userId : kjhdjkfadfjbasdbjfbasdb	
# stars : 4.0	
# text : I was amazed with the quality of the food

##### OUTPUTS the file in the same order, but tokenizes, removes punctuatuations, removes stop words and stems the words before outputing. Also each word in review occurs once in output.

## Run from repo folder using
# python PythonScripts/processReview.py option
# option : all for processing four dataset(takes huge time)
#	   ON  for eg. processing only ON (or any dataset for that matter of fact)		

from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
import re
import string
from nltk.stem import PorterStemmer
import sys

stemmer = PorterStemmer()

def getTokens(doc):
	doc = re.sub("\d+", "", doc)

	tokenized = word_tokenize(doc.decode('utf-8'))

	regex = re.compile('[%s]' % re.escape(string.punctuation)) #see documentation here: http://docs.python.org/2/library/string.html

	tokenized_no_punctuation = []

	new_review = []


	## REMOVING PUNCTUATION
	#for token in tokenized: 
	#	new_token = regex.sub(u'', token.lower())
	#	if not new_token == u'':
	#	    tokenized_no_punctuation.append(new_token)

	tokenized_no_punctuation = [re.sub(r'[^A-Za-z0-9]+', '', x.lower()) for x in tokenized]
	tokenized_no_punctuation = [s for  s in tokenized_no_punctuation if (len(s)>1)]	
	
	#print tokenized_no_punctuation

	token_no_stop = []
	## REMOVING STOP WORDS
	for word in tokenized_no_punctuation:
		if not word in stopwords.words('english'):
			try:
				word = stemmer.stem(word.encode('utf-8'))
			except UnicodeDecodeError:
				word = word #.encode('utf-8')
		    	token_no_stop.append(word.encode('utf-8'))

	
	return token_no_stop


def processData(folder):
	filePath = "Dataset/data/"+folder
	fin = open(filePath+'/reviews.txt', 'r')
	fout = open(filePath+'/reviews_textProc.txt', 'w')
	count = 0

	for i, l in enumerate(fin):
		pass

	lines = i+1
	perc = 1;
	fin = open(filePath+'/reviews.txt', 'r')
	for line in fin: 
		if(line.strip().split(':')[0] == 'text'):
			tokens =  getTokens(line.strip().split(':')[1])
			tokenSet = set()
			for i in tokens:
				if (len(i) >= 3):
					tokenSet.add(i)
			fout.write("text : ")
			fout.write(" ".join(tokenSet))
			fout.write("\n\n")
		else:
			fout.write(line)
		count = count + 1
		if( (count % (lines/10) == 0) ):
			p = str(perc*10) + "% done"
			sys.stdout.write("\r"+p)
			perc = perc + 1

	print

option = sys.argv[1]
folders = ['EDH', 'AZ', 'ON', 'NV', 'WI']
if(option == 'all'):
	for folder in folders:
		print folder, "processing"
		processData(folder)	
else:
	processData(option)



		
		
