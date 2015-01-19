package postProcessing;

import java.io.IOException;
import java.util.*;

public class QualitativeEvaluation {
	
	public static void main(String [] args) throws IOException {
		String folder = "AZ";
		String evaluation = "HeldOut";
		System.out.println("Start");
		
		EntityEmbeddings attributes = new EntityEmbeddings(folder, evaluation+"/"+"attributes-bw", 30);
		EntityEmbeddings words = new EntityEmbeddings(folder, evaluation+"/"+"words-bw", 30);
		EntityEmbeddings categories = new EntityEmbeddings(folder, evaluation+"/"+"categories-bw", 30);
		EntityEmbeddings business = new EntityEmbeddings(folder, evaluation+"/"+"business-bw", 30);
		
		Similarity s = new Similarity();
		s.getSimilarity(categories, words, 5);
		s.printSimMap();
		
		String folderToRead = "CatAttWord/";
		String entitiesToRead = "categories.txt";
		String entityReadPath = System.getProperty("user.dir")+"/../Embeddings_Prediction_Data/Qualitative/"+ folderToRead + entitiesToRead;
		Set<String> e1sToWrite = Util.readEntitiesForTSNE(entityReadPath);
		Set<String> e2sToWrite = Util.getKNNEntities(e1sToWrite, s);	
		System.out.println(e2sToWrite.size());
		//System.out.println(entitiesToWrite);
		
		String writePath = System.getProperty("user.dir")+"/../Embeddings_Prediction_Data/Qualitative/"+ folderToRead;
		Util.writeEmbeddingsForSet(e1sToWrite, categories, writePath + "catEmbeddings.txt");
		Util.writeEmbeddingsForSet(e2sToWrite, words, writePath + "wordEmbeddings.txt");
		Util.writeSimilarEntities(e1sToWrite, s.simMap, writePath + "catWords.txt");
		
		
		
		
		
		
	}
}
