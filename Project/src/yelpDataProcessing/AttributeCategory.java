package yelpDataProcessing;

import java.util.*;
import java.io.*;

import javax.json.*;
import javax.json.spi.*;

/*
 * Reads the yelp_dataset_restaurant json file and creates
 * - resAtt.txt - The restaurant-attribute data that can directly be factorized
 * - resCat.txt - The retaurant-category data that can be factorized but needs negative data
 */


public class AttributeCategory {
	
	Map<String, Map<String, Integer>> attributes;		// Map [Attribute, SubAttribute]
	Set<String> categories;											
	Map<String, Integer> catCount;						// Map [Category, Occurrence Count]
	Map<String, Integer> busCatCount;					// Map [Restaurant, No. of Category Count]
	Set<String> catReduced;								// Set [Categories] - Thresholded on terms of occurrence
	Map<String, ArrayList<String>> busCat;				// Map [Res, List[Categories]]
	
	
	public void printCategories() {
		for(Object ob : categories){
			System.out.println(ob);
		}
	}
	
	public void printAttributes() {
		for(String att : attributes.keySet()){
			System.out.print(att + " : ");
			for(String subatt : attributes.get(att).keySet())
				System.out.print(subatt+", ");
			System.out.print("\n");
		}
		System.out.println("Total Attributes : " + attributes.keySet().size());
	}
	
	private void buildCategorySet(String folder) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/../Dataset/json/"+folder+"/business"));
		categories = new HashSet<String>();
		catCount = new HashMap<String, Integer>();
		busCatCount = new HashMap<String, Integer>();
		busCat = new HashMap<String, ArrayList<String>>();
		
		String line;
		int count = 0;
		while(( (line = br.readLine()) != null) ){
			JsonReader reader = Json.createReader(new StringReader(line));
			JsonObject object = reader.readObject();
			if(object.get("type").toString().equals("\"business\"")){
				if(object.get("categories").getValueType().toString().equals("ARRAY")){
					JsonArray cat = (JsonArray) object.get("categories");
					JsonValue b_id = object.get("business_id");
					JsonString bus_id = (JsonString) b_id;
					busCatCount.put(bus_id.getString(), cat.size());
					busCat.put(bus_id.getString(), new ArrayList<String>());
					for(JsonValue s : cat){
						JsonString c = (JsonString) s;
						String category = c.getString();
						categories.add(category);
						busCat.get(bus_id.getString()).add(category);
						if(!catCount.containsKey(category))
							catCount.put(category, 1);
						else
							catCount.put(category, catCount.get(category)+1 );
					}
				}
				count++;
			}
		}
		//return categories;
		//System.out.println("Businesses Read  : " + count);
	}
	
	private void buildThresholdCatSet(int thresh){
		catReduced = new HashSet<String>();
		for(String cat : catCount.keySet()){
			if(catCount.get(cat) >= thresh){
				catReduced.add(cat);
			}
		}
	}
	
	/* Reads the Yelp JSON Dataset and makes attributes hashmap */
	private void readAttributes(String folder) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/../Dataset/json/"+folder+"/business"));
		attributes = new HashMap<String, Map<String, Integer>>();
		String line;
		while(( (line = br.readLine()) != null) ){
			JsonReader reader = Json.createReader(new StringReader(line));
			JsonObject object = reader.readObject();
			//if(object.containsKey("attributes")){
			if(object.get("type").toString().equals("\"business\"")){
				if(object.get("attributes").getValueType().toString() == "OBJECT"){
					JsonObject attributeObject = (JsonObject) object.getJsonObject("attributes");
					for(String key : attributeObject.keySet()){
						if(!attributes.containsKey(key)){		// To Make Attributes Keys Set
							attributes.put(key, new HashMap<String,Integer>());
							getValueSet(attributeObject.get(key), key, attributes);
						}
						else
							getValueSet(attributeObject.get(key), key, attributes);
					}
				}
			}
		}
		br.close();
	}
	
	/* Creates a dataset with business Id and values for attributes and stores them in a file. Uses the attributes hashmap. */
	private void buildBusiness_AttributeDataset(String folder) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/../Dataset/json/"+folder+"/business"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/../Dataset/data/" + folder + "/busAtt.txt"));
		String line;
		int count = 0;
		
		while(( (line = br.readLine()) != null) && count < 42151 ){
			StringBuilder str = new StringBuilder();
			JsonReader reader = Json.createReader(new StringReader(line));
			JsonObject object = reader.readObject();
			JsonValue b_id = object.get("business_id");
			JsonString bus_id = (JsonString) b_id;
			bw.write("business_id : "+bus_id.getString()+"\n");
			
			if(object.get("attributes").getValueType().toString() == "OBJECT"){
				JsonObject attributeObject = (JsonObject) object.getJsonObject("attributes");
				navigateObjectForValues(attributeObject, null, null, str);
				//System.out.println(str.toString());
				bw.write(str.toString()+"\n\n");
				
			}
			count++;
		}
		//System.out.println("No. of Business : "+count);
		br.close();
		bw.close();
	}
	
	/*  */
	public void navigateObjectForValues(JsonValue tree, String key, String prevKey, StringBuilder str) { 
		   switch(tree.getValueType()) {
		      case OBJECT:
		         //System.out.println("OBJECT");
		         JsonObject object = (JsonObject) tree;
		         for (String name : object.keySet())
		            navigateObjectForValues(object.get(name), name, key, str);
		         break;
		      case ARRAY:
		         break;
		      case STRING:
		         JsonString st = (JsonString) tree;
		         if (key!= null){
				    if(prevKey != null){
				    	//System.out.println(prevKey+"_" + key + "_"+st.getString()+" : "+1);
				    	str.append(prevKey+"_" + key + "_"+st.getString()+" : "+1 + "\n");
				    	if(attributes.get(key).keySet().size() != 0)
				    		for(String s: attributes.get(key).keySet())
				    			if(!s.equals(st.getString()))
				    				//System.out.println(prevKey+"_" + key + "_"+s+" : "+0);
				    				str.append(prevKey+"_" + key + "_"+s+" : "+0+"\n");
				    }
		         	else{	
		         		//System.out.println(key + "_"+st.getString()+" : "+1);
		         		str.append(key + "_"+st.getString()+" : "+1+"\n");
		         		
		         		if(attributes.get(key).keySet().size() != 0)
		         			for(String s: attributes.get(key).keySet())
		         				if(!s.equals(st.getString()))
		         					//System.out.println(key + "_"+s+" : "+0);
		         					str.append(key + "_"+s+" : "+0+"\n");
		         	}
		         }
		         
		        	 
		         break;
		      case NUMBER:
		    	  /*if (key!= null)
				      if(prevKey != null)
				    	  System.out.print(prevKey+"_" + key + " : ");
				      else
				    	  System.out.print(key + " : ");
		         JsonNumber num = (JsonNumber) tree;
		         System.out.println(num.toString());
		         */
		         break;
		      case TRUE:
		    	  if (key!= null)
				      if(prevKey != null)
				    	  //System.out.print(prevKey+"_" + key + " : ");
				    	  str.append(prevKey+"_" + key + " : "+1+"\n");
				      else
//				    	 //System.out.print(key + " : " + 1);
		    	  		 str.append(key + " : " + 1+"\n");
		    	  //System.out.println(1);
		    	  break;
		      case FALSE:
		      case NULL:
		    	  if (key!= null)
				      if(prevKey != null)
				    	  //System.out.print(prevKey+"_" + key + " : ");
				    	  str.append(prevKey+"_"+key + " : " + 0+"\n");
				      else
				    	  //System.out.print(key + " : ");
				    	  str.append(key + " : " + 0+"\n");
		         //System.out.println(0);
		         break;
		   }
	}
	
	private void getValueSet(JsonValue attribute, String attributeName, Map<String, Map<String, Integer>> attributes){
		switch(attribute.getValueType()){
		
		case STRING:
			JsonString st = (JsonString) attribute;
			if(!attributes.get(attributeName).containsKey(st))
				attributes.get(attributeName).put(st.getString(), 1);
			break;
			
		case NUMBER:
			if(!attributes.get(attributeName).containsKey("NUMBER"))
				attributes.get(attributeName).put("NUMBER", 1);
			break;
		case TRUE:
			if(!attributes.get(attributeName).containsKey("TRUE"))
				attributes.get(attributeName).put("TRUE", 1);
			break;
		case FALSE:
			if(!attributes.get(attributeName).containsKey("FALSE"))
				attributes.get(attributeName).put("FALSE", 1);
		break;
		case OBJECT:
			JsonObject att = (JsonObject) attribute;
			for(String subAttributeName : att.keySet())
				if(!attributes.get(attributeName).containsKey(subAttributeName))
					attributes.get(attributeName).put(subAttributeName, 1);
		break;
		}
	}

	// Writes Restaurant : Category1, Category2, ...   - to file resCat.txt
	private void writeResCatToFile(String folder) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")+"/../Dataset/data/" + folder +"/busCat.txt"));
		for(String res : busCat.keySet()){
			bw.write(res + " : ");
			for(String cat : busCat.get(res))
					bw.write(cat + "; ");
			bw.write("\n");
		}
		bw.close();
	}
	
	private void makeCitySet(String dataset) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.dir")+"/../Dataset/json"+dataset));
		String line;	int count = 0;
		Map<String, Integer> states = new HashMap<String, Integer>();
		while(( (line = br.readLine()) != null) && count < 42151){
			JsonReader reader = Json.createReader(new StringReader(line));
			JsonObject object = reader.readObject();
			
			JsonString city = (JsonString) object.get("state");
			String ci = city.getString();
			if(!states.containsKey(ci))
				states.put(ci, 0);
			int sc = states.get(ci);
			sc++;
			states.put(ci, sc);
			
			count++;
		}
		
		for(String state : states.keySet()){
			System.out.println(state + " : " + states.get(state));
		}
		System.out.println(states.keySet().size());
	}
	
	
	public static void main(String[] args) throws Exception{
		String State = "ON";	
		String [] folders = {"ON", "AZ", "EDH", "WI", "NV", "complete"};
		
		AttributeCategory data = new AttributeCategory();

		
		// Read file for attributes and write to a file
		for(String state : folders){
			System.out.println("Processing "+state);
			data.readAttributes(state);
			data.buildBusiness_AttributeDataset(state);
			data.buildCategorySet(state);
			data.writeResCatToFile(state);
		}
		
	}


}
