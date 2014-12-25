package logisticCMF;
import java.util.*;

public class embeddings {
	
	Map<String, embedding> embs;			// Map[Entity_Id, Embedding]
	Map<String, Double> alpha;				// Map [Relation_Id, Alpha (matrix mean) ] 
	HashSet<String> relIds;					// Set of relation Ids
	HashSet<String> entityIds;				// Set of Entity Ids
	int K ;
	static Random rand = new Random();
	
	// Instantiating Embeddings for Data 
	public embeddings(data YData, int lK){
		K = lK;
		alpha = new HashMap<String, Double>();
		embs = new HashMap<String, embedding>();
		for(Cell cell : YData.Data){
			for(String entityId : cell.entity_ids){
				if(!embs.containsKey(entityId))
					embs.put(entityId, new embedding(K));
				
				embs.get(entityId).addRelation(cell.relation_id);
			}
		}
		System.out.println("Unique Entites in Database : " + embs.keySet().size());
		computeAlpha(YData);
	}
	
	public embeddings(embeddings e){
		this.alpha = e.alpha;
		this.embs = e.embs;
		this.relIds = e.relIds;
		this.entityIds = e.entityIds;
		this.K = e.K;
	}
	
	//Compute Alpha - Relation wise mean of values
	public void computeAlpha(data D){
		Map<String, Integer> relSum = new HashMap<String, Integer>();			// Sum of truth values in each relation
		Map<String, Integer> relCount = new HashMap<String, Integer>();		// Count of truth values in each relation
		
		for(Cell cell : D.trainData){
			if(!relSum.containsKey(cell.relation_id)){
				if(cell.truth == true)
					relSum.put(cell.relation_id, 1);
				else
					relSum.put(cell.relation_id, 0);
				relCount.put(cell.relation_id, 1);
			}
			else{
				int sum = relSum.get(cell.relation_id);
				int count = relCount.get(cell.relation_id);
				if(cell.truth == true){
					sum += 1;  
				}
				count++;
				relSum.put(cell.relation_id, sum);
				relCount.put(cell.relation_id, count);
			}

		}

		for(String relId : relSum.keySet()){
			double a = ((double)relSum.get(relId))/relCount.get(relId);
			a = Math.log((a / (1-a)));
			//alpha.put(relId, a);
			alpha.put(relId, rand.nextGaussian()*0.001);
		}
		
	
	}
	
	
	// 	Dot Product of vectors in cell, but leave out one entity, For fixed k
	public double coeffVector(Cell cell, String leaveEntity, int k){
		double result=1.0;
		for(String entityId : cell.entity_ids){
			if(!entityId.equals(leaveEntity))
				result *=  embs.get(entityId).vector[k];
		}
		return result;
	}
	
	// 	Dot Product of vectors in cell, but leave out one entity, For fixed k
	public double dot(Cell cell, boolean enableBias, int K, boolean ealpha, boolean onlyAlpha){
		double result=0.0;

		if(ealpha)
			result += alpha.get(cell.relation_id);
		if(!onlyAlpha){
			if(enableBias){
				for(String entityId : cell.entity_ids){
					if(!embs.containsKey(entityId))
						System.out.println("Entity not found : " + entityId);
					if(!embs.get(entityId).bias.containsKey(cell.relation_id))
						System.out.println("Relation Not found : " + cell.relation_id);
					result += embs.get(entityId).bias.get(cell.relation_id);
				
				
				}
			}
			
			for(int k = 0; k<K; k++){
				double vecProd = 1.0;
				for(String entityId : cell.entity_ids)
					vecProd *=  embs.get(entityId).vector[k];
				result += vecProd;
			}
		}
		return result;
	}	
	
	public void printAlpha(){
		System.out.println("Relation Wise Alpha Values");
		for(String e : alpha.keySet()){
			System.out.println(e + " : " + alpha.get(e));
		}
	}
}
