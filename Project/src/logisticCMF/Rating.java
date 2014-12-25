package logisticCMF;
import java.io.*;
import java.util.*;



public class Rating {
	
	public Map<String, Map<String, Integer>> ratings; 			// Map[busId, Map[User, Rating]]
	public String loc;
	data [] busRate = new data[4];
	
	public Rating(String folder, double valp, double testp) throws IOException{
		ratings = new HashMap<String, Map<String, Integer>>();
		loc = folder;
		busRate[0] = new data();
		busRate[1] = new data();
		busRate[2] = new data();
		busRate[3] = new data();
		readRatingData(folder);
		makeTwoRatingDataCells(valp, testp);
		/*for(data rd : busRate){
			rd.dataStats();
		}*/
	}

	public void testRatings(){
		data mergeData = new data();
		ArrayList<data> tomerge = new ArrayList<data>();
		for(data d : busRate)
			tomerge.add(d);
		mergeData.addDataAfterSplit(tomerge);
		mergeData.dataStats();
		codeTest.learnAndTest(mergeData, 30, false, 0, false, 0);
	}
	
	public void readRatingData(String folder) throws NumberFormatException, IOException{
		String address = System.getProperty("user.dir")+"/../Dataset/data/"+ folder +"/reviews.txt";
		BufferedReader br = new BufferedReader(new FileReader(address));
		String line;	int countl = 0, countcell = 0;
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
				/*if(ratings.get(busid).containsKey(userid)){
					int r = ratings.get(busid).get(userid);
					System.out.println("user already exists " + busid + " " + userid);
					
				}*/
				ratings.get(busid).put(userid, (int)t);
				countcell++;
			}
		}
		br.close();
		//System.out.println("Ratings : " + countcell);
		
	}
	
	public  void makeTwoRatingDataCells(double valPerc, double testPerc){
		String relation = "busrate-"+loc+"-";
		int count2 = 0;	int count = 0;
		// Read all rating data from Map and create busRate data object for threshold = 2	
		for(String bus : ratings.keySet()){
			for(String user : ratings.get(bus).keySet()){
				int rate = ratings.get(bus).get(user);
				count++;
				Cell cell = new Cell();
				cell.relation_id = relation+"2";
				cell.entity_ids.add(bus);
				cell.entity_ids.add(user);
				if(rate >= 2){
					cell.truth = true;
					busRate[0].Data.add(cell);		
				}
				else{
					cell.truth = false;
					busRate[0].Data.add(cell);
				}
			}
		}
		busRate[0].splitTrainTestValidation(valPerc, testPerc);
		//busRate[0].dataStats();
		makeRestRatingDataCells();
		
		System.out.println("Ratings : " + busRate[0].Data.size());
	}
	
	public void makeRestRatingDataCells(){
		String r = "busrate-"+loc+"-";
		for(Cell cell : busRate[0].trainData){
			String b = cell.entity_ids.get(0), u = cell.entity_ids.get(1);
			int rate = ratings.get(b).get(u);
			if(rate <= 2)
				addCellsinRestTrain(r, b, u, false, false, false);
						
			if(rate == 3)
				addCellsinRestTrain(r, b, u, true, false, false);
			
			if(rate == 4)
				addCellsinRestTrain(r, b, u, true, true, false);
			
			if(rate == 5)
				addCellsinRestTrain(r, b, u, true, true, true);
		}
		
		for(Cell cell : busRate[0].testData){
			String b = cell.entity_ids.get(0), u = cell.entity_ids.get(1);
			int rate = ratings.get(b).get(u);
			if(rate <= 2)
				addCellsinRestTest(r, b, u, false, false, false);
			if(rate == 3)
				addCellsinRestTest(r, b, u, true, false, false);
			if(rate == 4)
				addCellsinRestTest(r, b, u, true, true, false);
			if(rate == 5)
				addCellsinRestTest(r, b, u, true, true, true);
		}
		
		for(Cell cell : busRate[0].valData){
			String b = cell.entity_ids.get(0), u = cell.entity_ids.get(1);
			int rate = ratings.get(b).get(u);
			if(rate <= 2)
				addCellsinRestVal(r, b, u, false, false, false);
			if(rate == 3)
				addCellsinRestVal(r, b, u, true, false, false);
			if(rate == 4)
				addCellsinRestVal(r, b, u, true, true, false);
			if(rate == 5)
				addCellsinRestVal(r, b, u, true, true, true);
		}
		
	}
	
	public void addCellsinRestTrain(String r, String b, String u, boolean t3, boolean t4, boolean t5){
		busRate[1].Data.add(new Cell(r+3, b, u, t3));
		busRate[1].trainData.add(new Cell(r+3, b, u, t3));
		
		busRate[2].Data.add(new Cell(r+4, b, u, t4));
		busRate[2].trainData.add(new Cell(r+4, b, u, t4));
		
		busRate[3].Data.add(new Cell(r+5, b, u, t5));
		busRate[3].trainData.add(new Cell(r+5, b, u, t5));
	}
	
	public void addCellsinRestTest(String r, String b, String u, boolean t3, boolean t4, boolean t5){
		busRate[1].Data.add(new Cell(r+3, b, u, t3));
		busRate[1].testData.add(new Cell(r+3, b, u, t3));
		
		busRate[2].Data.add(new Cell(r+4, b, u, t4));
		busRate[2].testData.add(new Cell(r+4, b, u, t4));
		
		busRate[3].Data.add(new Cell(r+5, b, u, t5));
		busRate[3].testData.add(new Cell(r+5, b, u, t5));
	}
	
	public void addCellsinRestVal(String r, String b, String u, boolean t3, boolean t4, boolean t5){
		busRate[1].Data.add(new Cell(r+3, b, u, t3));
		busRate[1].valData.add(new Cell(r+3, b, u, t3));
		
		busRate[2].Data.add(new Cell(r+4, b, u, t4));
		busRate[2].valData.add(new Cell(r+4, b, u, t4));
		
		busRate[3].Data.add(new Cell(r+5, b, u, t5));
		busRate[3].valData.add(new Cell(r+5, b, u, t5));
	}

}
	
