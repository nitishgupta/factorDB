#!/usr/bin/env python
# -*- coding: utf-8 -*-

## Inputs review file in format

# businessId : asnhdjkasld
# userId : kjhdjkfadfjbasdbjfbasdb	
# stars : 4.0	
# text : I was amazed with the quality of the food

##### OUTPUTS the file in the same order, but tokenizes, removes punctuatuations, removes stop words and stems the words before outputing. Also each word in review occurs once in output.


from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
import re
import string
from nltk.stem import PorterStemmer

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



fin = open('reviews.txt', 'r')
fout = open('reviews_textProc.txt', 'w')
count = 0
for line in fin: 
#	print count
	if(count %100000 == 0):
		print count
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
