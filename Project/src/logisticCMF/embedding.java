package logisticCMF;
import java.util.*;

/*
 * Embedding Class - Stores embedding for each entity which includes : 
 * 		Bias for each relation the entity belongs to
 * 		K- Dimensional Latent Vector for each entity
*/

public class embedding {
	Map<String, Double> bias;			// Map : [Relation_id, bias]
	double[] vector;					// K-Dimensional Latent Vector
	static Random rand = new Random(20);

	// For entity realized first time. Put Bias = 0.0 for relation and initialize latent vector to random
	public embedding(int K){
		bias = new HashMap<String, Double>();
		vector = new double[K];
		for(int k=0; k<K; k++)
			vector[k] = rand.nextGaussian()*0.01;
			//vector[k] = (2*(rand.nextDouble()-0.5))/K;
			
			//vector[k] = Math.random()/K;
	}

	public void addRelation(String relationId){
			bias.put(relationId, 0.0);
	}
}
