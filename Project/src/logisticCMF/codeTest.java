package logisticCMF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

public class codeTest {
	
	static Random rand = new Random();
	
	private static final long MEGABYTE = 1024L * 1024L;
	public static long bytesToMegabytes(long bytes) {
	    return bytes / MEGABYTE;
	  }
	public static void getMemoryDetails(){
		// Get the Java runtime
	    Runtime runtime = Runtime.getRuntime();
	    // Run the garbage collector
	    //runtime.gc();
	    // Calculate the used memory
	    System.out.println("Total memory : " + bytesToMegabytes(runtime.totalMemory()) + " Free memory : " + bytesToMegabytes(runtime.freeMemory()) + 
	    					" Used memory is megabytes: " + bytesToMegabytes(runtime.totalMemory() - runtime.freeMemory()));
	   
	}
	
	// Input Data, that is split in Train, Validation and Test. Input K. Learn and stop on convergence. Then test with learnt no. of epochs.
	public static embeddings learnAndTest(data Data, int K, boolean busWord, int busWordNegSamSize, boolean userWord, int userWordNegSamSize){
		embeddings e = new embeddings(Data, K);
		learner l = new learner();
		embeddings eBest = l.learnAndStop1(Data, e, false, false, false, busWord, busWordNegSamSize, userWord, userWordNegSamSize);
		System.out.println("learning done");
		Eval.getEvalMap(Data, eBest, "test");
		Eval.printEval();
		return eBest;
	}
	
	public static data readAttributes(String folder, double valPerc, double testPerc, boolean coldStart, int index) throws IOException{
		data att = new data();
		att.readBusAtt(System.getProperty("user.dir")+"/../Dataset/data/"+ folder +"/busAtt.txt", folder);
		if(coldStart)
			att.splitColdStart(valPerc, testPerc, index);
		else
			att.splitTrainTestValidation(valPerc, testPerc);
		
		return att;
	}
	
	public static data readCategories(String folder, int pruneThresh) throws IOException{
		data cat = new data();
		cat.readAndCompleteCategoryData(System.getProperty("user.dir")+"/../Dataset/data/"+ folder +"/busCat.txt", pruneThresh, folder);
		cat.splitTrainTestValidation(0.0, 0.0);
		return cat;
	}
	
	public static data readRatings(String folder, double valPerc, double testPerc, boolean coldStart, int index) throws IOException{
		data rate = new data();
		rate.readRatingData(System.getProperty("user.dir")+"/../Dataset/data/"+ folder +"/reviews.txt", folder);
		if(coldStart)
			rate.splitColdStart(valPerc, testPerc, index);
		else
			rate.splitTrainTestValidation(valPerc, testPerc);
		return rate;
	}
	
	public static data readReviewData(String folder, int occThresh, boolean busWord, boolean userWord, double valPerc, double testPerc) throws IOException{
		data rD = new data();
		rD.readReviewData(System.getProperty("user.dir")+"/../Dataset/data/"+ folder +"/reviews_textProc.txt");		// Makes EnWord maps (Business and User Word maps) and Word-Count map
		rD.pruneVocab_EntityMap(occThresh);	
		if(busWord)
			rD.makeEnWordCells("b-word");
		if(userWord)
			rD.makeEnWordCells("u-word");
		rD.splitTrainTestValidation(valPerc, testPerc);
		return rD;
	}
	
	public static void completeEvaluation(String folder, data A, data C, data R, data W, boolean busWord, int bwNS, boolean userWord, int uwNS, 
													boolean attCold, int coldIndexAtt, boolean rateCold, int coldIndexRate, String folderToWriteData) throws IOException{

		System.out.print("Attribute Cold Start, ");
		Util.checkColdStartSanity(A);
		Util.implicitColdStart(A.trainData, A.testData);
		System.out.print("Rate Cold Start, ");
		Util.checkColdStartSanity(R);
		Util.implicitColdStart(R.trainData, R.testData);
		
		ArrayList<data> tomerge = new ArrayList<data>();
		data mergeData = new data();
		
		getMemoryDetails();
		
		System.out.println("###################################################  "+folder+"  - Att-Word ######################################");
		tomerge.clear();
		tomerge.add(A);
		tomerge.add(W);
		mergeData = new data();
		mergeData.busWord = W.busWord;
		mergeData.words = W.words;
		mergeData.wordCount = W.wordCount;
		mergeData.userWord = W.userWord; 
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		embeddings e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord))
			writeDataToFile.writePrediction(folderToWriteData+"A-A", folder, A, e);
		if(busWord && !userWord)
			writeDataToFile.writePrediction(folderToWriteData+"A-A+BW", folder, A, e);
		if(userWord && !busWord)
			writeDataToFile.writePrediction(folderToWriteData+"A-A+UW", folder, A, e);
		if(userWord && busWord)
			writeDataToFile.writePrediction(folderToWriteData+"A-A+BUW", folder, A, e);
		getMemoryDetails();
		
		System.out.println("###################################################  "+folder+"  - Att-Cat-Word ######################################");
		//rD = readReviewData(folder, 10, busWord, false, 0.0, 0.0);
		tomerge = new ArrayList<data>();
		tomerge.clear();
		tomerge.add(A);
		tomerge.add(C);
		tomerge.add(W);
		mergeData = new data();
		mergeData.busWord = W.busWord;
		mergeData.words = W.words;
		mergeData.wordCount = W.wordCount;
		mergeData.userWord = W.userWord; 
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord))
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C", folder, A, e);
		if(busWord && !userWord)
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+BW", folder, A, e);
		if(userWord && !busWord)
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+UW", folder, A, e);
		if(busWord && userWord)
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+BUW", folder, A, e);
		System.gc();
		
		System.out.println("###################################################  "+folder+"  - Rating - Word ######################################");
		tomerge = new ArrayList<data>();
		tomerge.clear();
		tomerge.add(R);
		tomerge.add(W);
		mergeData = new data();
		mergeData.busWord = W.busWord;
		mergeData.words = W.words;
		mergeData.wordCount = W.wordCount;
		mergeData.userWord = W.userWord; 
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord))
			writeDataToFile.writePrediction(folderToWriteData+"R-R", folder, R, e);
		if(busWord && !userWord)
			writeDataToFile.writePrediction(folderToWriteData+"R-R+BW", folder, R, e);
		if(userWord && !busWord)
			writeDataToFile.writePrediction(folderToWriteData+"R-R+UW", folder, R, e);
		if(busWord && userWord)
			writeDataToFile.writePrediction(folderToWriteData+"R-R+BUW", folder, R, e);
		System.gc();
		
		System.out.println("###################################################  "+folder+"  - Rating - Cat - Word ######################################");
		tomerge = new ArrayList<data>();
		tomerge.clear();
		tomerge.add(R);
		tomerge.add(C);
		tomerge.add(W);
		mergeData = new data();
		mergeData.busWord = W.busWord;
		mergeData.words = W.words;
		mergeData.wordCount = W.wordCount;
		mergeData.userWord = W.userWord; 
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord))
			writeDataToFile.writePrediction(folderToWriteData+"R-R+C", folder, R, e);
		if(busWord && !userWord)
			writeDataToFile.writePrediction(folderToWriteData+"R-R+C+BW", folder, R, e);
		if(userWord && !busWord)
			writeDataToFile.writePrediction(folderToWriteData+"R-R+C+UW", folder, R, e);
		if(busWord && userWord)
			writeDataToFile.writePrediction(folderToWriteData+"R-R+C+BUW", folder, R, e);
		System.gc();
		
		System.out.println("###################################################  "+folder+"  - Att Rate - Word ######################################");
		tomerge = new ArrayList<data>();
		tomerge.clear();
		tomerge.add(A);
		tomerge.add(R);
		tomerge.add(W);
		mergeData = new data();
		mergeData.busWord = W.busWord;
		mergeData.words = W.words;
		mergeData.wordCount = W.wordCount;
		mergeData.userWord = W.userWord; 
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord)){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+R", folder, A, e);
			writeDataToFile.writePrediction(folderToWriteData+"R-A+R", folder, R, e);
		}
		if(busWord && !userWord){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+R+BW", folder, A, e);
			writeDataToFile.writePrediction(folderToWriteData+"R-A+R+BW", folder, R, e);
		}
		if(userWord && !busWord){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+R+UW", folder, A, e);
			writeDataToFile.writePrediction(folderToWriteData+"R-A+R+UW", folder, R, e);
		}
		if(userWord && busWord){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+R+BUW", folder, A, e);
			writeDataToFile.writePrediction(folderToWriteData+"R-A+R+BUW", folder, R, e);
		}
		System.gc();
		
		
		System.out.println("###################################################  "+folder+"  - Att Cat Rate Word ######################################");
		tomerge = new ArrayList<data>();
		tomerge.clear();
		tomerge.add(A);
		tomerge.add(R);
		tomerge.add(C);
		tomerge.add(W);
		mergeData = new data();
		mergeData.busWord = W.busWord;
		mergeData.words = W.words;
		mergeData.wordCount = W.wordCount;
		mergeData.userWord = W.userWord; 
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord)){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+R", folder, A, e);
			writeDataToFile.writePrediction(folderToWriteData+"R-A+C+R", folder, R, e);
		}
		if(busWord && !userWord){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+R+BW", folder, A, e);
			writeDataToFile.writePrediction(folderToWriteData+"R-A+C+R+BW", folder, R, e);
		}
		if(userWord && !busWord){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+R+UW", folder, A, e);
			writeDataToFile.writePrediction(folderToWriteData+"R-A+C+R+UW", folder, R, e);
		}
		if(busWord && userWord){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+R+BUW", folder, A, e);
			writeDataToFile.writePrediction(folderToWriteData+"R-A+C+R+BUW", folder, R, e);
		}
		getMemoryDetails();
		
		if(busWord || userWord){
			if(busWord && !userWord){
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"words-bw", W, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"attributes-bw", A, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"categories-bw", C, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"business-bw", R, 0, e);
			}
			if(userWord && !busWord){
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"words-uw", W, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"attributes-uw", A, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"categories-uw", C, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"business-uw", R, 0, e);
			}
			if(userWord && busWord){
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"words-buw", W, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"attributes-buw", A, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"categories-buw", C, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"business-buw", R, 0, e);
			}
		}
		
	}

	public static void AttBusColdCompleteEvaluation(String folder, data A, data C, data R, data W, boolean busWord, int bwNS, boolean userWord, int uwNS, String folderToWriteData) throws IOException{
		System.out.print("Attribute Cold Start, ");
		Util.checkColdStartSanity(A);
		Util.implicitColdStart(A.trainData, A.testData);
		
		ArrayList<data> tomerge = new ArrayList<data>();
		data mergeData = new data();
		
		getMemoryDetails();
		
		System.out.println("###################################################  "+folder+"  - Att-Word ######################################");
		tomerge.clear();
		tomerge.add(A);
		tomerge.add(W);
		mergeData = new data(W);
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		embeddings e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord))
			writeDataToFile.writePrediction(folderToWriteData+"A-A", folder, A, e);
		if(busWord && !userWord)
			writeDataToFile.writePrediction(folderToWriteData+"A-A+BW", folder, A, e);
		if(userWord && !busWord)
			writeDataToFile.writePrediction(folderToWriteData+"A-A+UW", folder, A, e);
		if(userWord && busWord)
			writeDataToFile.writePrediction(folderToWriteData+"A-A+BUW", folder, A, e);
		getMemoryDetails();
		
		System.out.println("###################################################  "+folder+"  - Att-Cat-Word ######################################");
		tomerge = new ArrayList<data>();
		tomerge.clear();
		tomerge.add(A);
		tomerge.add(C);
		tomerge.add(W);
		mergeData = new data(W);
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord))
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C", folder, A, e);
		if(busWord && !userWord)
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+BW", folder, A, e);
		if(userWord && !busWord)
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+UW", folder, A, e);
		if(busWord && userWord)
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+BUW", folder, A, e);
		System.gc();
		
		System.out.println("###################################################  "+folder+"  - Att Rate - Word ######################################");
		tomerge = new ArrayList<data>();
		tomerge.clear();
		tomerge.add(A);
		tomerge.add(R);
		tomerge.add(W);
		mergeData = new data(W);
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord)){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+R", folder, A, e);
		}
		if(busWord && !userWord){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+R+BW", folder, A, e);
		}
		if(userWord && !busWord){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+R+UW", folder, A, e);
		}
		if(userWord && busWord){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+R+BUW", folder, A, e);
		}
		System.gc();
		
		
		System.out.println("###################################################  "+folder+"  - Att Cat Rate Word ######################################");
		tomerge = new ArrayList<data>();
		tomerge.clear();
		tomerge.add(A);
		tomerge.add(R);
		tomerge.add(C);
		tomerge.add(W);
		mergeData = new data(W);
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord)){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+R", folder, A, e);
		}
		if(busWord && !userWord){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+R+BW", folder, A, e);
		}
		if(userWord && !busWord){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+R+UW", folder, A, e);
		}
		if(busWord && userWord){
			writeDataToFile.writePrediction(folderToWriteData+"A-A+C+R+BUW", folder, A, e);
		}
		getMemoryDetails();
		
		if(busWord || userWord){
			if(busWord && !userWord){
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"words-bw", W, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"attributes-bw", A, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"categories-bw", C, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"business-bw", R, 0, e);
			}
			if(userWord && !busWord){
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"words-uw", W, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"attributes-uw", A, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"categories-uw", C, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"business-uw", R, 0, e);
			}
			if(userWord && busWord){
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"words-buw", W, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"attributes-buw", A, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"categories-buw", C, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"business-buw", R, 0, e);
			}
		}
		
	}
	
	public static void RateColdCompleteEvaluation(String folder, data A, data C, data R, data W, boolean busWord, int bwNS, boolean userWord, int uwNS, 
													String folderToWriteData) throws IOException{
		
		System.out.print("Rate Cold Start, ");
		Util.checkColdStartSanity(R);
		Util.implicitColdStart(R.trainData, R.testData);
		
		ArrayList<data> tomerge = new ArrayList<data>();
		data mergeData = new data();
		
		getMemoryDetails();
		
		System.out.println("###################################################  "+folder+"  - Rating - Word ######################################");
		tomerge = new ArrayList<data>();
		tomerge.clear();
		tomerge.add(R);
		tomerge.add(W);
		mergeData = new data(W);
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		embeddings e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord))
			writeDataToFile.writePrediction(folderToWriteData+"R-R", folder, R, e);
		if(busWord && !userWord)
			writeDataToFile.writePrediction(folderToWriteData+"R-R+BW", folder, R, e);
		if(userWord && !busWord)
			writeDataToFile.writePrediction(folderToWriteData+"R-R+UW", folder, R, e);
		if(busWord && userWord)
			writeDataToFile.writePrediction(folderToWriteData+"R-R+BUW", folder, R, e);
		System.gc();
		
		System.out.println("###################################################  "+folder+"  - Rating - Cat - Word ######################################");
		tomerge = new ArrayList<data>();
		tomerge.clear();
		tomerge.add(R);
		tomerge.add(C);
		tomerge.add(W);
		mergeData = new data(W);
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord))
			writeDataToFile.writePrediction(folderToWriteData+"R-R+C", folder, R, e);
		if(busWord && !userWord)
			writeDataToFile.writePrediction(folderToWriteData+"R-R+C+BW", folder, R, e);
		if(userWord && !busWord)
			writeDataToFile.writePrediction(folderToWriteData+"R-R+C+UW", folder, R, e);
		if(busWord && userWord)
			writeDataToFile.writePrediction(folderToWriteData+"R-R+C+BUW", folder, R, e);
		System.gc();
		
		System.out.println("###################################################  "+folder+"  - Att Rate - Word ######################################");
		tomerge = new ArrayList<data>();
		tomerge.clear();
		tomerge.add(A);
		tomerge.add(R);
		tomerge.add(W);
		mergeData = new data(W);
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord)){
			writeDataToFile.writePrediction(folderToWriteData+"R-A+R", folder, R, e);
		}
		if(busWord && !userWord){
			writeDataToFile.writePrediction(folderToWriteData+"R-A+R+BW", folder, R, e);
		}
		if(userWord && !busWord){
			writeDataToFile.writePrediction(folderToWriteData+"R-A+R+UW", folder, R, e);
		}
		if(userWord && busWord){
			writeDataToFile.writePrediction(folderToWriteData+"R-A+R+BUW", folder, R, e);
		}
		System.gc();
		
		
		System.out.println("###################################################  "+folder+"  - Att Cat Rate Word ######################################");
		tomerge = new ArrayList<data>();
		tomerge.clear();
		tomerge.add(A);
		tomerge.add(R);
		tomerge.add(C);
		tomerge.add(W);
		mergeData = new data(W);
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		e = learnAndTest(mergeData, 30, busWord, bwNS, userWord, uwNS);
		if(!(busWord || userWord)){
			writeDataToFile.writePrediction(folderToWriteData+"R-A+C+R", folder, R, e);
		}
		if(busWord && !userWord){
			writeDataToFile.writePrediction(folderToWriteData+"R-A+C+R+BW", folder, R, e);
		}
		if(userWord && !busWord){
			writeDataToFile.writePrediction(folderToWriteData+"R-A+C+R+UW", folder, R, e);
		}
		if(busWord && userWord){
			writeDataToFile.writePrediction(folderToWriteData+"R-A+C+R+BUW", folder, R, e);
		}
		getMemoryDetails();
		
		if(busWord || userWord){
			if(busWord && !userWord){
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"words-bw", W, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"attributes-bw", A, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"categories-bw", C, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"business-bw", R, 0, e);
			}
			if(userWord && !busWord){
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"words-uw", W, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"attributes-uw", A, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"categories-uw", C, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"business-uw", R, 0, e);
			}
			if(userWord && busWord){
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"words-buw", W, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"attributes-buw", A, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"categories-buw", C, 1, e);
				writeDataToFile.writeEmbeddings(folder, folderToWriteData+"business-buw", R, 0, e);
			}
		}
		System.gc();
	}
	
	public static void performAttBusColdEvaluation(String folder) throws IOException{
		String folderToWriteData = "AttBusCold/";
		data A = readAttributes(folder, 15.0, 15.0, true, 0);
		data C = readCategories(folder, 5);
		data R = readRatings(folder, 0.0, 0.0, false, 1);
		data W = new data();
		
		AttBusColdCompleteEvaluation(folder, A, C, R, W, false, 0, false, 0, folderToWriteData);
		System.gc();
		
		// Business - Words
		W = readReviewData(folder, 10, true, false, 0.0, 0.0);
		int bwNS = Util.getNegSampleSize(W);
		AttBusColdCompleteEvaluation(folder, A, C, R, W, true, bwNS, false, 0, folderToWriteData);
		System.gc();
		
		// User - Words
		W = readReviewData(folder, 10, false, true, 0.0, 0.0);
		int uwNS = Util.getNegSampleSize(W);
		AttBusColdCompleteEvaluation(folder, A, C, R, W, false, 0, true, uwNS, folderToWriteData);
		System.gc();
		
		// BusWords and UserWords
		/*W = readReviewData(folder, 10, true, true, 0.0, 0.0);
		AttBusColdCompleteEvaluation(folder, A, C, R, W, true, bwNS, true, uwNS, folderToWriteData);
		System.gc();*/
	}
	
	public static void performRateBusColdEvaluation(String folder) throws IOException{
		String folderToWriteData = "RateBusCold/";
		data A = readAttributes(folder, 0.0, 0.0, false, 0);
		data C = readCategories(folder, 5);
		data R = readRatings(folder, 15.0, 15.0, true, 0);
		data W = new data();
		
		RateColdCompleteEvaluation(folder, A, C, R, W, false, 0, false, 0, folderToWriteData);
		System.gc();
		
		// Business - Words
		W = readReviewData(folder, 10, true, false, 0.0, 0.0);
		int bwNS = Util.getNegSampleSize(W);
		RateColdCompleteEvaluation(folder, A, C, R, W, true, bwNS, false, 0, folderToWriteData);
		System.gc();
		
		// User - Words
		W = readReviewData(folder, 10, false, true, 0.0, 0.0);
		int uwNS = Util.getNegSampleSize(W);
		RateColdCompleteEvaluation(folder, A, C, R, W, false, 0, true, uwNS, folderToWriteData);
		System.gc();
		
		// BusWords and UserWords
		/*W = readReviewData(folder, 10, true, true, 0.0, 0.0);
		RateColdCompleteEvaluation(folder, A, C, R, W, true, bwNS, true, uwNS, folderToWriteData);
		System.gc();*/
	}
	
	public static void performRateUserColdEvaluation(String folder) throws IOException{
		String folderToWriteData = "RateUserCold/";
		data A = readAttributes(folder, 0.0, 0.0, false, 0);
		data C = readCategories(folder, 5);
		data R = readRatings(folder, 15.0, 15.0, true, 1);
		data W = new data();
		
		RateColdCompleteEvaluation(folder, A, C, R, W, false, 0, false, 0, folderToWriteData);
		System.gc();
		
		// Business - Words
		W = readReviewData(folder, 10, true, false, 0.0, 0.0);
		int bwNS = Util.getNegSampleSize(W);
		RateColdCompleteEvaluation(folder, A, C, R, W, true, bwNS, false, 0, folderToWriteData);
		System.gc();
		
		// User - Words
		W = readReviewData(folder, 10, false, true, 0.0, 0.0);
		int uwNS = Util.getNegSampleSize(W);
		RateColdCompleteEvaluation(folder, A, C, R, W, false, 0, true, uwNS, folderToWriteData);
		System.gc();
		
		// BusWords and UserWords
		/*W = readReviewData(folder, 10, true, true, 0.0, 0.0);
		RateColdCompleteEvaluation(folder, A, C, R, W, true, bwNS, true, uwNS, folderToWriteData);
		System.gc();*/
	}
	
	public static void performHeldOutEvaluation(String folder) throws IOException{
		String folderToWriteData = "HeldOut/";
		data A = readAttributes(folder, 15.0, 15.0, false, 0);
		data C = readCategories(folder, 5);
		data R = readRatings(folder, 15.0, 15.0, false, 1);
		data W = new data();
		
		// No Words
		completeEvaluation(folder, A, C, R, W, false, 0, false, 0, false, 0, false, 0, folderToWriteData);
		System.gc();
		
		// Business - Words
		W = readReviewData(folder, 10, true, false, 0.0, 0.0);
		int bwNS = Util.getNegSampleSize(W);
		completeEvaluation(folder, A, C, R, W, true, bwNS, false, 0, false, 0, false, 0, folderToWriteData);
		System.gc();
		
		// User - Words
		W = readReviewData(folder, 10, false, true, 0.0, 0.0);
		int uwNS = Util.getNegSampleSize(W);
		completeEvaluation(folder, A, C, R, W, false, 0, true, uwNS, false, 0, false, 0, folderToWriteData);
		System.gc();
		
		// BusWords and UserWords
		/*W = readReviewData(folder, 10, true, true, 0.0, 0.0);
		getMemoryDetails();
		completeEvaluation(folder, A, C, R, W, true, bwNS, true, uwNS, false, 0, false, 0, folderToWriteData);
		System.gc();*/
	}
	
	// To test one dataset completely and write embeddings for (A + R + C + W)
	/*public static void main(String [] args) throws Exception {
		String folder = args[0];
		String todo = args[1];
		todo = "heldOut";
		folder = "EDH";
		
		if(todo.equals("heldOut"))
			performHeldOutEvaluation(folder);
		if(todo.equals("attBusCold"))
			performAttBusColdEvaluation(folder);
		if(todo.equals("rateBusCold"))
			performRateBusColdEvaluation(folder);
		if(todo.equals("rateUserCold"))
			performRateUserColdEvaluation(folder);
		//attBusColdEvaluations(folder);
		//rateBusColdEvaluations(folder);
	}*/
	
	
	
	// To make the sizes table
		public static void main(String [] args) throws Exception {
			
			String folder = "EDH";
			data A = readAttributes(folder, 0.0, 0.0, false, 0);
			//data C = readCategories(folder, 5);
			//data R = readRatings(folder, 0.0, 0.0, false, 1);
			//data BW = readReviewData(folder, 10, true, false, 0.0, 0.0);
			//data UW = readReviewData(folder, 10, false, true, 0.0, 0.0);
			
			A.dataStats();
			
			Util.getMatrixDetails(A);
			//Util.getMatrixDetails(C);
//			//Util.getMatrixDetails(R);
			//Util.getMatrixDetails(BW);
					
			
			
		}
	
}