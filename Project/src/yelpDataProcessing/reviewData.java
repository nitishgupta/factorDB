package yelpDataProcessing;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import logisticCMF.Cell;


public class reviewData {
	
	public static Map<String, Integer> wordCount = new HashMap<String, Integer>();
	public static Map<String, Set<String>> resWord = new HashMap<String, Set<String>>();
	public static Map<String, Set<String>> userWord = new HashMap<String, Set<String>>();
	public static HashSet<String> words = new HashSet<String>();
	
	public static void addWordInMap(String word){
		if(!wordCount.containsKey(word))
			wordCount.put(word, 0);
		int wcount = wordCount.get(word);
		wcount++;
		wordCount.put(word, wcount);
	}
	
	public static void readData(String reviewData) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(reviewData));
		String line;	String bId = null;	String userId = null; int countl = 0, countR = 0;
		
		while((line = br.readLine()) != null){
			countl++;
			String[] array = line.split(":");
			
			if( array[0].trim().equals("user_id")){
				countR++;
				userId = array[1].trim();
				if(!userWord.containsKey(userId))
					userWord.put(userId, new HashSet<String>());
			}
			
			if( array[0].trim().equals("bus_id")){
				bId = array[1].trim();
				if(!resWord.containsKey(bId))
					resWord.put(bId, new HashSet<String>());
			}
			
			if(array[0].trim().equals("text")){
				String [] tokens = array[1].trim().split(" ");
				if(tokens.length > 0){
					for(String word : tokens){
						word = word.trim();
						if(word.length() >= 3){
							addWordInMap(word);
							resWord.get(bId).add(word);
							userWord.get(userId).add(word);
						}
					}
				}
			}
			if(countl % 100000 == 0)
				System.out.println("line : "+countl);
		}
		System.out.println("Total No. of Reviews : " + countR);
	}
	
	public static void getMapStats(Map<String, Set<String>> enWord){
		int potentialResWordCells = 0;
		int min=100000000; 
		System.out.println("No. of entities = " + enWord.keySet().size());
		System.out.println("No. of total words in Vocab = " + words.size());
		
		for(String en : enWord.keySet()){
			min = (enWord.get(en).size() < min) ? enWord.get(en).size() : min;
			for(String word : enWord.get(en)){
				potentialResWordCells++;
			}
			
		}
		System.out.println("Potential Entity-Word Cells : " + potentialResWordCells);
		System.out.println("Min No. of Words in Entity : " + min);
	}
	
	// Remove words from Map[Entity -> Set[words]] that occur few times in dictionary. If Set of words for entity go empty, remove Entity from Map.
	public static void pruneEntityWordMap(Map<String, Set<String>> enWord){
		Iterator<String> it = enWord.keySet().iterator();
		while(it.hasNext()){
			String en = it.next();
			Iterator<String> itr = enWord.get(en).iterator();
			while(itr.hasNext()){
				String word = itr.next();
				if(!words.contains(word))
					itr.remove();
			}
			if(enWord.get(en).size() == 0)
				it.remove();
		}
	}
	
	
	// Make a Array of Words that have frequency above the given threshold.
	public static void makePrunedWordList(int occThresh){
		words = new HashSet<String>();	int count = 0;
		for(String word : wordCount.keySet()){
			if(wordCount.get(word) > occThresh){
				count++;
				words.add(word);
			}
		}
		System.out.println("Words with greater than occurence of " + occThresh + " : " + words.size());
	}
	
	public static int countLines(String file) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line; int count = 0;
		while((line = br.readLine()) != null){
			count++;
		}
		return count;
	}
	
	
	public static void main(String [] args) throws IOException{
		
		String reviewData = System.getProperty("user.dir")+"/../Dataset/data/ON/reviews_textProc.txt";
		readData(reviewData);
		
		int occThresh = 1;
		System.out.println("Total Words in Review Data : " + wordCount.keySet().size());
		int count = 0;
		
		/*for(occThresh = 0; occThresh <= 50; occThresh++){
			makePrunedWordList(occThresh);
		}*/
		
		makePrunedWordList(4);
		pruneEntityWordMap(resWord);
		getMapStats(resWord);
		
		//pruneEntityWordMap(userWord, occThresh);
		//getMapStats(userWord);
	}
}
