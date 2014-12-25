package postProcessing;
import java.util.*;
import java.io.*;


public class Similarity {
	
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
	
	public Map<String, ArrayList<String>> getSimilarity(EntityEmbeddings ee1, EntityEmbeddings ee2){
		Map<String, ArrayList<String>> simMap = KNN(ee1, ee2, 20);
		
		for(String en : simMap.keySet()){
			System.out.print(en + " : ");
			for(String nn : simMap.get(en)){
				System.out.print(nn + ", ");
			}
			System.out.println();
		}
		
		return simMap;
	}
	
	
	public static void main(String [] args) throws IOException {
		String folder = "AZ";
		String evaluation = "HeldOut";
		System.out.println("Start");
		
		EntityEmbeddings attributes = new EntityEmbeddings(folder, evaluation+"/"+"attributes-bw", 30);
		EntityEmbeddings words = new EntityEmbeddings(folder, evaluation+"/"+"words-bw", 30);
		EntityEmbeddings categories = new EntityEmbeddings(folder, evaluation+"/"+"categories-bw", 30);
		EntityEmbeddings business = new EntityEmbeddings(folder, evaluation+"/"+"business-bw", 30);
		
		Similarity s = new Similarity();
		s.getSimilarity(categories, words);
		
		
		
		
	}

}
