package logisticCMF;

import java.util.*;
import java.io.*;

public class writeDataToFile {
	public static void writePrediction(String fileName, String folder, data Data, embeddings e) throws IOException{
		String fileAddress = System.getProperty("user.dir")+"/../Embeddings_Prediction_Data/"+ folder +"/pred-data/" + fileName;
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileAddress));
		for(Cell cell : Data.testData){
			double dot = e.dot(cell, learner.enableBias, e.K, learner.ealpha, learner.onlyAlpha);
			double sigmdot = learner.sigm(dot);
			int truth = (cell.truth) ? 1 : 0;
			String e1 = cell.entity_ids.get(0), e2 = cell.entity_ids.get(1);
			bw.write(e1 + " :: " + e2 + " :: " + sigmdot + " :: " + truth +"\n") ;
		}
		bw.close();
	}
	
	public static void writeEmbeddings(String folder, String fileName, data Data, int entityNumber, embeddings e) throws IOException{
		String fileAddress = System.getProperty("user.dir")+"/../Embeddings_Prediction_Data/"+ folder +"/embeddings/" + fileName;
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileAddress));
		for(String entity : Data.entityIds.get(entityNumber)){
			bw.write(entity + " :: ");
			embedding em = e.embs.get(entity);
			for(int i=0; i<em.vector.length; i++){
				bw.write(em.vector[i] + ", ");
			}
			bw.write("\n");
		}
		bw.close();
	}
}
