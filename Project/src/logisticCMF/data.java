package logisticCMF;
import java.io.*;
import java.util.*;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

public class data {
	
	public ArrayList<Cell> trainData;
	public ArrayList<Cell> testData;
	public ArrayList<Cell> valData;
	public ArrayList<Cell> Data;
	public Map<String, Integer> relDataCount;		// Map [Relation Id, Count]
	ArrayList<Set<String>> entityIds;
	
	public Map<String, Integer> wordCount = new HashMap<String, Integer>();					// Map [Words -> Count] - In original review Data
	public Map<String, Set<String>> busWord = new HashMap<String, Set<String>>();			// Map [restaurant -> [words used for it]] - Can be pruned according to word occurrence threshold
	public Map<String, Set<String>> userWord = new HashMap<String, Set<String>>();			// Map [user -> [words used for it]] - Can be pruned according to word occurrence threshold
	public ArrayList<String> words = new ArrayList<String>();								// Set [words] - The vocab for our review data. Can be pruned according to word occurrence threshold
	public static Random seed = new Random(50);												// Also defined in Embedding & Learner Class
	
	
	public data(){
		trainData = new ArrayList<Cell>();
		valData = new ArrayList<Cell>();
		testData = new ArrayList<Cell>();
		Data = new ArrayList<Cell>();
		relDataCount = new HashMap<String, Integer>();
		wordCount = new HashMap<String, Integer>();					
		busWord = new HashMap<String, Set<String>>();			
		userWord = new HashMap<String, Set<String>>();			
		words = new ArrayList<String>();	
		entityIds = new ArrayList<Set<String>>();
		entityIds.add(new HashSet<String>());
		entityIds.add(new HashSet<String>());
	}
	
	public data(data D){
		busWord = D.busWord;
		userWord = D.userWord;
		words = D.words;
		wordCount = D.wordCount;
		trainData = new ArrayList<Cell>();
		valData = new ArrayList<Cell>();
		testData = new ArrayList<Cell>();
		Data = new ArrayList<Cell>();
		relDataCount = new HashMap<String, Integer>();
		entityIds = new ArrayList<Set<String>>();
		entityIds.add(new HashSet<String>());
		entityIds.add(new HashSet<String>());
	}
	
	public void addToEntitySets(Cell cell){
		entityIds.get(0).add(cell.entity_ids.get(0));
		entityIds.get(1).add(cell.entity_ids.get(1));
	}
	
	public void readBusAtt(String fileAddress, String rel) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileAddress));
		String relation = "b-att-"+rel;
		String line;
		String bId = "";		
		int count = 0;
		while( (line = br.readLine()) != null ){
			//System.out.println(line);
			String [] arr = line.split(":");
			if(arr.length >= 2){
				if(arr[0].trim().equals("business_id"))
					bId = arr[1].trim();

				else{
					String att = arr[0].trim();
					double t = Double.parseDouble(arr[1].trim());
					Cell cell = new Cell();
					cell.relation_id = relation;
					cell.entity_ids.add(bId);
					cell.entity_ids.add(att);
					cell.truth = (t == 1.0) ? true : false;
					addToEntitySets(cell);
					Data.add(cell);
					count++;
				}
				
		
			}
		}
		br.close();
		System.out.println(relation + " : " + count);
	}
	
	public void printData(){
		for(Cell cell : Data){
			System.out.print("\n"+cell.relation_id + ", ");
			for(String e : cell.entity_ids)
				System.out.print(e + ", ");
			System.out.print(cell.truth);
		}
	}
	
	public void makeRestaurantSet(String fileAddress) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileAddress+"1"));
		HashMap<String, Double> restaurants = new HashMap<String, Double>();
		for(Cell cell : Data){
			if(restaurants.containsKey(cell.entity_ids.get(0)))
				continue;
			else{
				restaurants.put(cell.entity_ids.get(0), 1.0);
				bw.write(cell.entity_ids.get(0) + "\n");
			}
		}		
		bw.close();
	}
	
	public void refreshTrainTest(){
		trainData = new ArrayList<Cell>();
		valData = new ArrayList<Cell>();
		testData = new ArrayList<Cell>();
	}
	
	public void readRatingData(String fileAddress, String rel) throws IOException{
		Map<String, Map<String, Integer>> ratings = new HashMap<String, Map<String, Integer>>();
		BufferedReader br = new BufferedReader(new FileReader(fileAddress));
		String line;	int countl = 0, countcell = 0;
		String relation = "b-u-rate-"+rel;
		String busid = null, userid = null;	boolean value = false;
		while((line = br.readLine()) != null){
			countl++;
			String[] array = line.split(":");
			if( array[0].trim().equals("bus_id")){
				busid = array[1].trim();
				if(!ratings.containsKey(busid))
					ratings.put(busid, new HashMap<String, Integer>());
			}
			if( array[0].trim().equals("user_id"))
				userid = array[1].trim();
			if( array[0].trim().equals("star")){
				double t = Double.parseDouble(array[1].trim());
				ratings.get(busid).put(userid, (int)t);
			}
		}
		br.close();
		for(String bus : ratings.keySet()){
			for(String user : ratings.get(bus).keySet()){
				int rate = ratings.get(bus).get(user);
				Cell cell = new Cell();
				cell.relation_id = relation;
				cell.entity_ids.add(bus);
				cell.entity_ids.add(user);
				cell.truth = (rate >= 4) ? true : false;
				Data.add(cell);
				addToEntitySets(cell);
				countcell++;
			}
		}
		System.out.println("Ratings read : " + countcell);
	}
	
	public void countLines(String fileAddress) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileAddress));
		String line;	String relation = "restaurant-user-word";	int count = 0;
		String busid = null, userid = null;	boolean value = false;
		while((line = br.readLine()) != null){
			count ++;
		}
		System.out.println(count);
	}
	
	public void splitTrainTestValidation(double valPercentage, double testPercentage){
		Collections.shuffle(Data, seed);
		for(int j = 0; j<Data.size(); j++){
			if((double)j/Data.size() < (100.0 - (valPercentage + testPercentage))/100.0)
				trainData.add(Data.get(j));
			
			else if ((double)j/Data.size() < (100.0 - (testPercentage))/100.0)
				valData.add(Data.get(j));
			
			else
				testData.add(Data.get(j));
				
		}
	}
	
	public void splitColdStart(double valPercentage, double testPerc, int index){
		HashSet<String> coldEntities = Util.getColdEntites(Data, index, testPerc);
		int countTest = 0, i=0;
		for(Cell cell : Data){
			if(coldEntities.contains(cell.entity_ids.get(index))){
				testData.add(cell);
				countTest++;
			}
			else
				trainData.add(cell);
		}
		Collections.shuffle(trainData, seed);
		Iterator<Cell> it = trainData.iterator();
		int cellValidation = (int)((valPercentage/100)*Data.size());
		//System.out.println(cellValidation);
		while(i < cellValidation){
			Cell cell = it.next();
			valData.add(cell);
			it.remove();
			i++;
		}
	}
	
	public void addDatainExisting(data d){
		Collections.shuffle(Data, seed); 	Collections.shuffle(d.Data, seed);
		for(Cell cell : d.Data)
			Data.add(cell);
		
		Collections.shuffle(Data, seed);
 	}

	public void addDataAfterSplit(ArrayList<data> dataList){
		for(data d : dataList){
			for(Cell cell : d.Data)
				Data.add(cell);
			for (Cell cell : d.trainData)
				trainData.add(cell);
			for(Cell cell : d.valData)
				valData.add(cell);
			for(Cell cell : d.testData)
				testData.add(cell);
		}
		
		Collections.shuffle(Data, seed);
		Collections.shuffle(trainData, seed);
		Collections.shuffle(valData, seed);
		Collections.shuffle(testData, seed);
 	}
	
	// Used by dataStats() function
	public void countTrueFalse(ArrayList<Cell> data, Map<String, Integer> relTrue, Map<String, Integer> relFalse){
		for(Cell cell : data){
			int t = (cell.truth) ? 1 : 0; int f = (cell.truth) ? 0 : 1;
			if(t == 1){
				if(!relTrue.containsKey(cell.relation_id))
					relTrue.put(cell.relation_id, 1);
				else
					relTrue.put(cell.relation_id, relTrue.get(cell.relation_id) + 1);
			}
			if(f == 1){
				if(!relFalse.containsKey(cell.relation_id))
					relFalse.put(cell.relation_id, 1);
				else
					relFalse.put(cell.relation_id, relFalse.get(cell.relation_id) + 1);
			}
		}
	}
	
	public void dataStats(){
		Map<String, Integer> relTrue = new HashMap<String, Integer>();
		Map<String, Integer> relFalse = new HashMap<String, Integer>();
		countTrueFalse(Data, relTrue, relFalse);
		System.out.println("\nData Stats");
		for(String rel : relTrue.keySet())
			System.out.println(rel + " : " + "t : " + relTrue.get(rel) + " f : " + relFalse.get(rel));
		
		
		
		relTrue.clear();
		relFalse.clear();
		countTrueFalse(trainData, relTrue, relFalse);
		System.out.println("Train Data Stats");
		for(String rel : relTrue.keySet())
			System.out.println(rel + " : " + "t : " + relTrue.get(rel) + " f : " + relFalse.get(rel));
		
		relTrue.clear();
		relFalse.clear();
		countTrueFalse(valData, relTrue, relFalse);
		System.out.println("Validation Data Stats");
		for(String rel : relTrue.keySet())
			System.out.println(rel + " : " + "t : " + relTrue.get(rel) + " f : " + relFalse.get(rel));
		
		relTrue.clear();
		relFalse.clear();
		countTrueFalse(testData, relTrue, relFalse);
		System.out.println("Test Data Stats");
		for(String rel : relTrue.keySet())
			System.out.println(rel + " : " + "t : " + relTrue.get(rel) + " f : " + relFalse.get(rel));
		
	}
	
	public void reviewDataStats(int entityId, int thresh, boolean removeEntities){
		Map<String, Integer> users = new HashMap<String, Integer>();	// Map[EntityID, Count in Set]
		Set<String> e1 = new HashSet<String>();
		Set<String> e2 = new HashSet<String>();
		Set<String> setUsers = new HashSet<String>();
		int count = 0, max = -1, min = 1000000000;
		for(Cell cell : Data){
			String user = cell.entity_ids.get(entityId);		/// CHECK WHICH ENTITY MAP IS CREATED
			e1.add(cell.entity_ids.get(0));
			e2.add(cell.entity_ids.get(1));
			setUsers.add(cell.entity_ids.get(entityId));
			if(!users.containsKey(user)){
				users.put(user, 1);
			}
			else{
				int revCount = users.get(user);
				int newRevCount = revCount+1 ;
				users.put(user, newRevCount);
			}
		}
		
		for(String user : users.keySet()){
			if(users.get(user) > max)
				max = users.get(user);
			if(users.get(user) < min)
				min = users.get(user);
			if(users.get(user) <= thresh){
				count++;
				setUsers.remove(user);
				//System.out.println(user + " : " + users.get(user));
			}
		}
		int iterates = 0;
		if(removeEntities){
			for(Iterator<Cell> itr = Data.iterator();itr.hasNext();){
				iterates++;
				Cell cell = itr.next();
				if(!setUsers.contains(cell.entity_ids.get(entityId))){
					itr.remove();
				}
			}
		}
		System.out.println("Total iterates = "+iterates);
		System.out.println(e1.size() + " : " + e2.size() + " : " +users.keySet().size() + " : " + setUsers.size());
		System.out.println("count : " + count + " max = " + max + " min : " + min);
	}
	
	public void readAndCompleteCategoryData(String fileAddress, int thresh, String rel) throws NumberFormatException, IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileAddress));
		Map<String, ArrayList<String>> resCat = new HashMap<String, ArrayList<String>>();
		Map<String, Integer> catCount = new HashMap<String, Integer>();
		Set<String> categories = new HashSet<String>();
		String line;	String relation = "b-cat-"+rel;
		int count = 0;
		while( (line = br.readLine()) != null)  {
			
			String[] array = line.split(":");
			resCat.put(array[0].trim(), new ArrayList<String>());	
			
			String [] cats = array[1].trim().split(";");		// Delimiter used in resCat file. Refer to YelpChallenge-yeldData.java
			for(String cat : cats){
				String category = cat.trim();
				if(category.length()>1){
					categories.add(category);
					resCat.get(array[0].trim()).add(category);
					
					// To build Map[Category, Count]
					if(!catCount.containsKey(category))
						catCount.put(category, 1);
					else{
						int cat_count = catCount.get(category);
						cat_count++;
						catCount.put(category, cat_count);
					}
				}
			}
		}
		br.close();
		
		
		int cC = 0;
		for(String c : catCount.keySet()){
			if(catCount.get(c) > thresh)
				cC++;
		}
		//System.out.println("Categories after pruning : " + cC);
		
		for(String res : resCat.keySet()){
			int categoriesConsidered = 0;
			for(String cat : categories){
				if(catCount.get(cat) > thresh){
					categoriesConsidered++;
					Cell cell = new Cell();
					cell.relation_id = relation;
					cell.entity_ids.add(res);
					cell.entity_ids.add(cat);
					if(resCat.get(res).contains(cat))
						cell.truth = true;
					else
						cell.truth = false;
					count++;
					Data.add(cell);
					addToEntitySets(cell);
				}
				cC = categoriesConsidered;
			}
		}
		System.out.println(relation + " : " + count + " categories read : " + cC);
	}

	public void reduceDataSize(double perc){
		Collections.shuffle(Data, seed);
		int iterations = 0; int size = Data.size(); int stopAt = (int)((perc/100.0)*size);  
		System.out.println(stopAt);
		for(Iterator<Cell> itr = Data.iterator();itr.hasNext();){
			iterations++;
			if(iterations <= stopAt)
				itr.next();
			else{
				itr.next();
				itr.remove();
			}
		}
	}
	
	public void readReviewData(String fileAddress) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileAddress));
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
				if(!busWord.containsKey(bId))
					busWord.put(bId, new HashSet<String>());
			}
			
			if(array[0].trim().equals("text")){
				String [] tokens = array[1].trim().split(" ");
				if(tokens.length > 0){
					for(String word : tokens){
						word = word.trim();
						if(word.length() >= 3){
							addWordInMap(word);
							busWord.get(bId).add(word);
							userWord.get(userId).add(word);
						}
					}
				}
			}
			/*if(countl % 100000 == 0)
				System.out.println("line : "+countl);*/
		}
		System.out.println("Total No. of Reviews : " + countR);
	}
	
	public void addWordInMap(String word){
		if(!wordCount.containsKey(word))
			wordCount.put(word, 0);
		int wcount = wordCount.get(word);
		wcount++;
		wordCount.put(word, wcount);
	} 
	
	public void getMapStats(Map<String, Set<String>> enWord){
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
		System.out.println("Words per entiy : " + ((double) potentialResWordCells)/enWord.keySet().size());
		System.out.println("Min No. of Words in Entity : " + min);
		
	}
	
	public void pruneVocab_EntityMap(int occThresh){
		makePrunedWordList(occThresh);
		pruneEntityWordMap(busWord, occThresh);
		pruneEntityWordMap(userWord, occThresh);
	}
	
	// Remove words from Map[Entity -> Set[words]] that occur few times in dictionary. If Set of words for entity go empty, remove Entity from Map.
	public void pruneEntityWordMap(Map<String, Set<String>> enWord, int occThresh){
		Iterator<String> it = enWord.keySet().iterator();
		while(it.hasNext()){
			String en = it.next();
			Iterator<String> itr = enWord.get(en).iterator();
			while(itr.hasNext()){
				String word = itr.next();
				if(wordCount.get(word) <= occThresh)
					itr.remove();
			}
			if(enWord.get(en).size() == 0)
				it.remove();
		}
	}
	
	public void getWordCountStats(int start, int end){
		int count =  0;
		for(int i = start; i<=end; i++){
			for(String word : wordCount.keySet()){
				if(wordCount.get(word) > i)
					count++;
			}
			System.out.println("Words with greater that " + i + " count : " + count);
			count = 0;
		}
	}
	
	// Make a Array of Words that have frequency above the given threshold.
	public void makePrunedWordList(int occThresh){
		words = new ArrayList<String>();	int count = 0;
		for(String word : wordCount.keySet()){
			if(wordCount.get(word) > occThresh){
				count++;
				words.add(word);
			}
		}
		System.out.println("Words with greater than occurence of " + occThresh + " : " + words.size());
	}
	
	public void makeEnWordCells(String relation){
		if(relation.equals("b-word")){
			enWordCells(busWord, relation);
		}
		else if(relation.equals("u-word")){
			enWordCells(userWord, relation);
		}
	}
	
	public void enWordCells(Map<String, Set<String>> enWord, String relation){
		for(String en : enWord.keySet()){
			for(String word : enWord.get(en)){
				Cell cell = new Cell();
				cell.relation_id = relation;
				cell.entity_ids.add(en);
				cell.entity_ids.add(word);
				cell.truth = true;
				Data.add(cell);
				addToEntitySets(cell);
			}
		}
	}
	
	public ArrayList<Cell> getNegativeSamples(String relation, int negSamplesPerEntity){
		ArrayList<Cell> negSamples = new ArrayList<Cell>();
		int negSamplesDone = 0;
		if(relation.equals("b-word")){
			for(String en : busWord.keySet()){
				while(negSamplesDone < negSamplesPerEntity){
					Cell cell = genNegSample(busWord, en, relation);
					negSamplesDone++;
					negSamples.add(cell);
				}
				negSamplesDone = 0;
			}
			
		}
		else if(relation.equals("u-word")){
			for(String en : userWord.keySet()){
				while(negSamplesDone < negSamplesPerEntity){
					Cell cell = genNegSample(userWord, en, relation);
					negSamplesDone++;
					negSamples.add(cell);
				}
				negSamplesDone = 0;
			}
		}
		return negSamples;
	}
	
	public Cell genNegSample(Map<String, Set<String>> enWord, String en, String relation){
		Cell cell = new Cell();
		cell.relation_id = relation;
		boolean found = false;
		while(!found){
			int pos = randInt(0, words.size() - 1);
			if(!enWord.get(en).contains(words.get(pos))){
				cell.entity_ids.add(en);
				cell.entity_ids.add(words.get(pos));
				cell.truth = false;
				found = true;
			}
		}
		return cell;
	}
	
	public static int randInt(int min, int max) {
	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = seed.nextInt((max - min) + 1) + min;

	    return randomNum;
	}

}
