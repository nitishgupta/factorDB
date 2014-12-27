package postProcessing;
import java.util.*;
import java.io.*;


public class Similarity {
	
	public Map<String, ArrayList<String>> simMap;
	
	public Map<String, ArrayList<String>> KNN(EntityEmbeddings a, EntityEmbeddings b, int K){
		Map<String, ArrayList<String>> kNN = new HashMap<String, ArrayList<String>>();
		for(String en1 : a.entityVector.keySet()){
			double [] vec1 = a.entityVector.get(en1);
			Map<String, Double> distances = new HashMap<String, Double>();
			for(String en2 : b.entityVector.keySet()){
				double[] vec2 = b.entityVector.get(en2);
				distances.put(en2, Util.dotProd(vec1, vec2));
			}
			
			kNN.put(en1, Util.getKNN(distances, K));
		}
		
		return kNN;
	}
	
	public void getSimilarity(EntityEmbeddings ee1, EntityEmbeddings ee2, int K){
		simMap = KNN(ee1, ee2, K);
	}
	
	public void printSimMap(){
		for(String en : simMap.keySet()){
			System.out.print(en + " : ");
			for(String nn : simMap.get(en)){
				System.out.print(nn + ", ");
			}
			System.out.println();
		}
	}
	
	
}
