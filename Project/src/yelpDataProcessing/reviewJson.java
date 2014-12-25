package yelpDataProcessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

import logisticCMF.Cell;

public class reviewJson {
	
	public static HashSet<String> resIds = new HashSet<String>();			// Set - [Restaurant Ids]
	
	// Reads Yelp Dataset Json - extracts only Review Jsons
	public static void extractReviewJson(String fileAddress) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileAddress));
		BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/Data/json/yelp_reviews.txt"));
		String line;
		int count=1;
		while( ((line = br.readLine()) != null) && count < 1199228){
			if(count <= 73770)
				count++;
			else{
				bw.write(line+"\n");
				count++;
			}
		}
		bw.close();
		br.close();
	}
	
	// Makes resIds Set - Then extracts json objects for restaurant reviews and stores them in file
	public static void extractResReviewJson() throws IOException{
		makeResIds();
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/Data/json/yelp_reviews.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/Data/json/yelp_reviews_restaurants.txt"));
		String line;
		int count = 0;
		while(( (line = br.readLine()) != null) ){
			JsonReader reader = Json.createReader(new StringReader(line));
			JsonObject object = reader.readObject();
			if(object.get("type").toString().equals("\"review\"")){
				JsonValue b_id = object.get("business_id");
				JsonString bus_id = (JsonString) b_id;
				String bId = bus_id.getString();
				if(resIds.contains(bId)){
					bw.write(line.trim() + "\n");
					count++;
				}
			}
		}
		
		System.out.println("Count : " + count);
		br.close();
		bw.close();
	}
	
	// Reads file that contains Restaurant Ids and stores in Set - resIds
	public static void makeResIds() throws IOException{
		String fA = System.getProperty("user.dir") + "/Data/new/restaurant_ids";
		BufferedReader br = new BufferedReader(new FileReader(fA));
		String line;
		while((line = br.readLine()) != null){
			resIds.add(line.trim());
		}
		br.close();
		System.out.println("No. of res : " + resIds.size());
	}
	
	public static void putReviewDatatoFile(String fileAddress ) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileAddress));
		BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/../Data/new/res_review_data.txt"));
		String line;		
		int count = 0;	String relation = "restaurant-user";
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
		System.out.println(relation + " : " + count);
	}

	
	
	public static void main(String [] args) throws IOException{
		//convertResAttLogisticReadableFile(System.getProperty("user.dir")+"/Data/yelp_dataset_restaurant_att");
		//String fileAddress = System.getProperty("user.dir") + "/../Dataset/yelp_dataset";
		String fileAddress = System.getProperty("user.dir") + "/../Data/json/yelp_reviews_restaurants.txt";
		//putReviewDatatoFile(fileAddress);
		//extractReviewJson(fileAddress);
		//extractResReviewJson();
		
	}

}
