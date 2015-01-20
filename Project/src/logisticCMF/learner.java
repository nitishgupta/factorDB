package logisticCMF;

import java.io.IOException;
import java.util.*;



public class learner {
	
	static int numEpochs = 500;			// Best = 700 (Res-Att) , 70 (ResUser)
	static double learningRate = 0.01;		// // Best = 0.003
	static double lambda = 0.001;			// Best = 0.002 (Res-Att)(Res-User), 
	static boolean enableBias = false;
	static boolean ealpha = false;
	static boolean onlyAlpha = false;
	static Random seed = new Random(10);
	
	learner(){
		numEpochs = 500;
		learningRate = 0.01;
		lambda = 0.001;	
		enableBias = true;
		ealpha = true;
		onlyAlpha = false;
	}
	
	
	public void update(Cell cell, boolean enableBias, embeddings embedings, boolean eal, boolean onAl){
		double dot = embedings.dot(cell, enableBias, embedings.K, eal, onAl);
		for(String entityId : cell.entity_ids){
			update(cell, entityId, dot, embedings);
		}
		
	}
	
	public void update(Cell cell, String entityId, double dot, embeddings embedings){
		double truth = (cell.truth) ? 1.0 : 0.0;
		
		// Updating Alpha
		if(ealpha){
			double al = embedings.alpha.get(cell.relation_id);
			al += learningRate* (truth - sigm(dot) - lambda*al);
			embedings.alpha.put(cell.relation_id, al);
		}
		if(!onlyAlpha){
			//Updating Biases
			if(enableBias){
				double b = embedings.embs.get(entityId).bias.get(cell.relation_id);
				b += learningRate* (truth - sigm(dot) - lambda*b ) ;
				embedings.embs.get(entityId).bias.put(cell.relation_id, b);
				
			}
			
			// Updating Latent Variables
			for(int k=0; k<embedings.K; k++){
				double phi_k = embedings.embs.get(entityId).vector[k];
				double coefVec = embedings.coeffVector(cell, entityId, k);
				
				phi_k += learningRate*( (truth - sigm(dot))*coefVec  - lambda*phi_k ); 	
				embedings.embs.get(entityId).vector[k] = phi_k;
			}
		}
	}
	
	public static double sigm(double x){
		return 1.0 / (1.0 + Math.exp(-x));
	}
	
	public embeddings learnAndStop1(data Data, embeddings embedings, boolean enable, boolean eal, boolean onAl, boolean busWord, int busWordNegSamSize, boolean userWord, int userWordNegSamSize){
		enableBias = enable;	ealpha = eal;	onlyAlpha = onAl;
		double maxF1 = 0.0, maxAcc = 0.0; int bestEpoch = numEpochs;
		int nochange = 0, dropfor = 0;
		boolean notConverged = true;	int epoch = 0;
		ArrayList<Cell> trainData = new ArrayList<Cell>();
		embeddings eBest = new embeddings(embedings);
		
		Map<String, ArrayList<Double>> evalMap = Eval.getEvalMap(Data, eBest, "test");
		Eval.printEval();
		while(notConverged){
			
			trainData.clear();
			trainData.addAll(Data.trainData);
			if(busWord){
				ArrayList<Cell> negSamples = Data.getNegativeSamples("b-word", busWordNegSamSize); 
				trainData.addAll(negSamples);
			}
			if(userWord){
				ArrayList<Cell> negSamples = Data.getNegativeSamples("u-word", userWordNegSamSize); 
				trainData.addAll(negSamples);
			}
			System.gc();
			Collections.shuffle(trainData, seed);			// Shuffle List of Training Data before each iteration of learning parameters
			for(Cell cell : trainData){
				update(cell, enableBias, embedings, ealpha, onlyAlpha);
			}
			//System.out.println("Train Data size :" + trainData.size());
			epoch++;
			System.out.print(epoch + " ");
			System.gc();
			if(epoch%5 == 0){
				System.out.println("################## Epoch : " + epoch + " ############");
				evalMap = Eval.getEvalMap(Data, embedings, "validation");
				//Eval.printEval();
				double wf1 = evalMap.get("average").get(3), wacc = evalMap.get("average").get(0); 
				if(epoch == 5){
					//maxF1 = wf1;	maxAcc = wacc;
					maxF1 = 0.0;	maxAcc = 0.0;
					dropfor = 0;	nochange = 0;
					bestEpoch = epoch;
					eBest = new embeddings(embedings);
				}
				else{
					//System.out.println("dF : " + dropfor + "  nc : " + nochange + " maxF1 : " + maxF1 + "  maxAcc : " + maxAcc);
					if(wf1 > maxF1 || wacc > maxAcc){
						bestEpoch = epoch;
						eBest = new embeddings(embedings);
						maxF1 = wf1;
						maxAcc = wacc;
						dropfor = 0;	nochange = 0;
					}
					else{
						if(wf1 == maxF1 || wacc == maxAcc)
							nochange++;
						else 
							dropfor++;
					}
				}
				
			}
			if(dropfor >= 3 || nochange >= 4)			/// CONDITIONS for STOPPING
				notConverged = false;
		}
		System.out.println("TRAINING CONVEREGED, BEST EPOCH = " + bestEpoch);
		return eBest;
	}
	

}
