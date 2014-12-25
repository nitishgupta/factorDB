package postProcessing;
import java.io.*;
import java.util.*;


public class EntityEmbeddings {
	
	public HashMap<String, double []> entityVector;
	
	public EntityEmbeddings(String folder, String fileName, int K) throws IOException{
		entityVector = new HashMap<String, double []>();
		readEmbeddings(folder, fileName, K);
	}
	
	public void readEmbeddings(String folder, String fileName, int K) throws IOException{
		String fileAddress = System.getProperty("user.dir")+"/../Embeddings_Prediction_Data/"+ folder +"/embeddings/" + fileName; 
		BufferedReader br = new BufferedReader(new FileReader(fileAddress));
		String line;
		while((line = br.readLine()) != null){
			String[] array = line.split("::");
			
			// Reading entity from embeddings file and creating 
			String entity = array[0].trim();
			if(!entityVector.containsKey(entity))
				entityVector.put(entity, new double[K]);
			else
				System.out.println("Duplicate Entity. ERROR !!!!!!");

			// Reading latent vector and storing in Map
			String[] vec = array[1].trim().split(",");
			for(int i=0; i<vec.length; i++){
				entityVector.get(entity)[i] = Double.parseDouble(vec[i].trim());
			}
		}
		br.close();
	}
	
	public void printEmbeddings(){
		for(String en : entityVector.keySet()){
			System.out.println(en + " : ");
			for(int i=0; i<entityVector.get(en).length; i++){
				System.out.print(entityVector.get(en)[i] + ", ");
			}
			System.out.println();
		}
	}
}
