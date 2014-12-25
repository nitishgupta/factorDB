package yelpDataProcessing;

import java.io.*;
import java.util.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonParsingException;


/*
 * Processes Yelp Dataset Json and creates different files
 * - yelp_business - Business dataset in json format.
 * - yelp_dataset_retaurant - Restaurant Json data from Yelp
 * - 
 * 
 * 
 */

public class ProcessYelpJson {
	
	Set<String> busIds = new HashSet<String>();
	
	// Create file - yelp_reviews - in dataset/json
	public void createCompleteReviewJson(String yelpDataset) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/../Dataset/json/"+yelpDataset));
		BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/../Dataset/json/complete/reviews"));
		String line;
		int count = 0; int cr = 0;
		while( ((line = br.readLine()) != null) && count < 1199227){
			if(count <= 73770)
				count++;
			else{
				cr++;
				bw.write(line+"\n");
				count++;
			}
		}
		bw.close();
		br.close();
		System.out.println("Reviews : " + cr);
		
		
	}
	
	// Creates file - yelp_business - in dataset/json folder
	public void createCompleteBusinessJson(String yelpDataset) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/../Dataset/json/"+yelpDataset));
		BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/../Dataset/json/complete/business"));
		String line; int count = 0;
		
		while(( (line = br.readLine()) != null) && count < 42151 ){
			bw.write(line + "\n");
			count++;
		}
		bw.close();
		System.out.println("No. of Businesses  : " + count);
		
	}
	
	public void createStateBusinessJson(String folder, String state) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/../Dataset/json/"+folder+"/business"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/../Dataset/json/"+state+"/business"));
		
		String line;
		int count = 0; int cr = 0;
		while(( (line = br.readLine()) != null) ){
			JsonReader reader = Json.createReader(new StringReader(line));
			JsonObject object = reader.readObject();
			if(object.get("type").toString().equals("\"business\"")){
				JsonValue s = object.get("state");
				JsonString st = (JsonString) s;
				String place = st.getString();
				if(place.equals(state)){
					bw.write(line + "\n");
					cr++;
				}
			}
		}
		bw.close();
		System.out.println("No. of Businesses in "+state + " : " + cr);
	}
	
	
	// Creates file - yelp_dataset_restaurant - in dataset/json folder
	public void createRestaurantJson(String folder) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/../Dataset/json/"+folder+"/business"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/../Dataset/json/Restaurant/business"));
		
		String line;
		int count = 0; int cr = 0;
		while(( (line = br.readLine()) != null) ){
			JsonReader reader = Json.createReader(new StringReader(line));
			JsonObject object = reader.readObject();
			if(object.get("type").toString().equals("\"business\"")){
				if(object.get("categories").getValueType().toString().equals("ARRAY")){
					JsonArray cat = (JsonArray) object.get("categories");
					for(JsonValue s : cat){
						JsonString c = (JsonString) s;
						String category = c.getString();
						if(category.equals("Restaurants")){
							bw.write(line + "\n");
							cr ++;
						}
					}
				}
			}
		}
		bw.close();
		System.out.println("No. of Restaurants  : " + cr);
	}
	
	
	// Create file - yelp_reviews_restaurant - in dataset/json
	public void createBusReviewJson(String folder_complete, String folder) throws IOException{
		makeBusIdSet(folder);
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/../Dataset/json/"+folder_complete+"/reviews"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/../Dataset/json/"+folder+"/reviews"));
		int count = 0;
		String line;
		while(( (line = br.readLine()) != null) ){
			JsonReader reader = Json.createReader(new StringReader(line));
			JsonObject object = reader.readObject();
			if(object.get("type").toString().equals("\"review\"")){
				JsonValue b_id = object.get("business_id");
				JsonString bus_id = (JsonString) b_id;
				String bid = bus_id.getString();
				if(busIds.contains(bid)){
					bw.write(line+"\n");
					count++;
				}
			}
		}
		bw.close();
		System.out.println("Reviews Count : " + count);
		
	}
	
	// Make a Set of Res-Ids to extract reviews
	public void makeBusIdSet(String folder) throws IOException{
		busIds = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/../Dataset/json/"+folder+"/business"));
		String line;
		while(( (line = br.readLine()) != null)){
			JsonReader reader = Json.createReader(new StringReader(line));
			JsonObject object = reader.readObject();
			if(object.get("type").toString().equals("\"business\"")){
				JsonValue b_id = object.get("business_id");
				JsonString bus_id = (JsonString) b_id;
				String bid = bus_id.getString();
				busIds.add(bid);
			}
		}
		System.out.println("Size of resIds set :" + busIds.size());
	}
	
	public static void putReviewDatatoFile(String folder) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/../Dataset/json/"+folder+"/reviews"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/../Dataset/data/"+folder+"/reviews.txt"));
		String line;		
		int count = 0;	
		while((line = br.readLine()) != null){
			JsonReader reader = Json.createReader(new StringReader(line));
			JsonObject object = reader.readObject();
			
			JsonValue b_id = object.get("business_id");
			JsonString bus_id = (JsonString) b_id;
			String bId = bus_id.getString();
			bw.write("bus_id : " + bId + "\n");
			
 			JsonValue u_id = object.get("user_id");
			JsonString user_id = (JsonString) u_id;
			String uId = user_id.getString();
			bw.write("user_id : " + uId + "\n");
			
			
			JsonValue star = object.get("stars");
			JsonNumber s = (JsonNumber) star;
			Double st = s.doubleValue();
			bw.write("star : " + st + "\n");
			
			JsonValue t = object.get("text");
			JsonString te = (JsonString) t;
			String text = te.getString();
			bw.write("text: " + t + "\n\n");
			
			count++;
		}
		br.close();
		bw.close();
		System.out.println("Reviews Written : " + count);
	}
	
	public static void main(String [] args) throws Exception{
		String yelpDataset = "yelp_dataset";
		String State = "NV";
		
		ProcessYelpJson yelp = new ProcessYelpJson();
		
		
		//yelp.createCompleteBusinessJson(yelpDataset);
		
		//yelp.createRestaurantJson("complete");
		yelp.createStateBusinessJson("complete", State);
		
		//yelp.createCompleteReviewJson(yelpDataset);
		
		yelp.createBusReviewJson("complete", State);
		
		yelp.putReviewDatatoFile(State);
		
		
		
	}
}
