# README #

### What is this repository for? ###

* Yelp Dataset Challenge
	* Parse Yelp Json format data to get required data in the required format
	* Perform logisitc CMF on the parsed data to predict relations.

### Details on Logistic CMF Code ###

* This Java project currently has 2 packages : 
	* yelpDataProcessing - Contains classes/functions to read the yelp dataset in json format and parse to get different data in required format.
	* logisticCMF - Contains classes/functions to read data produced in required format and then split train/validation/test data. Learn the embeddings for entities and print prediction evaluation.
	
* The folder PythonSCript contains a file cleantext.py that reads the yelp review data in user format and pre-processes the text review.
	* The text pre-processing contains tokenization, stemming, removal of stop words and punctuations. 
	* Each word is kept only once if occurs multiple times in a review.

#### Project Contributors ####
* Nitish Gupta
* Sameer Singh