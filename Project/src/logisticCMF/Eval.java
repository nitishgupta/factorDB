package logisticCMF;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Eval {
	static Map<String, Integer> relTrue = new HashMap<String, Integer>();			// Correct predictions per relation
	static Map<String, Integer> relCount = new HashMap<String, Integer>();			// Total test size per relation
	static Map<String, Double> relL2	= new HashMap<String, Double>();			
	
	static Map<String, Integer> relActualTruth = new HashMap<String, Integer>();	// Actual Truth values in each relation
	static Map<String, Integer> relPredTruth = new HashMap<String, Integer>();		// Truth predicted in each relation
	static Map<String, Integer> relTruthCorrect = new HashMap<String, Integer>();	// Correct Truth predictions in each relation
	
	static Map<String, ArrayList<Double>> relEvalMap = new HashMap<String, ArrayList<Double>>(); //Map [Rel,<Accuracy, Precision, Recall, F1>] Also contains "weighted average" as relation
	
	
	public static Map<String, ArrayList<Double>> getEvalMap(data Data, embeddings e, String set){
		refreshMaps();
		if(set.equals("test")){
			for(Cell cell : Data.testData)
				updateTestMaps(cell, e);
		}
		else{
			for(Cell cell : Data.valData)
				updateTestMaps(cell, e);
		}
		makeRelationEvalMap();			// Final Map [Relation, <Accuracy, Precision, Recall, F1>]
		return relEvalMap;
	}
	
	public static void refreshMaps(){
		relTrue = new HashMap<String, Integer>();
		relCount = new HashMap<String, Integer>();
		relL2	= new HashMap<String, Double>();			
		relActualTruth = new HashMap<String, Integer>();	// Actual Truth values in each relation
		relPredTruth = new HashMap<String, Integer>();		// Truth predicted in each relation
		relTruthCorrect = new HashMap<String, Integer>();	// Correct Truth predictions in each relation
		relEvalMap = new HashMap<String, ArrayList<Double>>();
	}

	public static void addInRelCountMap(Cell cell){
		if(!relCount.containsKey(cell.relation_id))
			relCount.put(cell.relation_id, 1);
		else
			relCount.put(cell.relation_id, relCount.get(cell.relation_id)+1);
	}
	
	public static void addInprfRelationMap(String relation, Integer actual, Integer pred, Integer correct){
		if(!relActualTruth.containsKey(relation)){
			relActualTruth.put(relation, 0);
			relPredTruth.put(relation, 0);
			relTruthCorrect.put(relation, 0);
		}
		
		else{
			if(!(actual == 0 && pred == 0)){
				relActualTruth.put(relation, relActualTruth.get(relation) + actual);
				relPredTruth.put(relation, relPredTruth.get(relation) + pred);
				relTruthCorrect.put(relation, relTruthCorrect.get(relation) + correct);
			}
		}
	}
	
	public static void addInRelAccuracyMap(Cell cell, Integer correct){
		if(!relTrue.containsKey(cell.relation_id))
			relTrue.put(cell.relation_id, correct);
		else
			relTrue.put(cell.relation_id, relTrue.get(cell.relation_id)+correct);
	}
	
	public static void addInRelL2Map(Cell cell, double l2){
		if(!relL2.containsKey(cell.relation_id))
			relL2.put(cell.relation_id, l2);
		else
			relL2.put(cell.relation_id, relTrue.get(cell.relation_id) + l2);
	}
	
	//Map [Rel, <Accuracy, Precision, Recall, F1> ]
	public static void makeRelationEvalMap(){
		//makeTestMaps(Data, e);
		double f = 0; double wf1=0.0, waccuracy=0.0, wp = 0.0, wr = 0.0; int total = 0;
		for(String rel : relActualTruth.keySet()){
			double accuracy = ((double)relTrue.get(rel))/relCount.get(rel);
			double precision = (double)relTruthCorrect.get(rel) / relPredTruth.get(rel) ;
			double recall = (double) relTruthCorrect.get(rel) / relActualTruth.get(rel) ;
			double f1 = 2*precision*recall / (precision + recall) ;
			//System.out.println("a : " + accuracy + " p : " + precision + " r : " + recall + " f1 : " +f1);
			relEvalMap.put(rel, new ArrayList<Double>());
			relEvalMap.get(rel).add(round(accuracy, 3));	// Accuracy
			relEvalMap.get(rel).add(round(precision, 3));			// Precision
			relEvalMap.get(rel).add(round(recall, 3));			// Recall
			relEvalMap.get(rel).add(round(f1, 3));				// F1
			wf1 += (relCount.get(rel)*f1);
			wp += (relCount.get(rel)*precision);
			wr += (relCount.get(rel)*recall);
			waccuracy += (relCount.get(rel)*accuracy); 
			total += relCount.get(rel);
		}
		wf1 = round(wf1/total, 3);	waccuracy = round(waccuracy/total, 3);	wp = round(wp/total, 3);	wr = round(wr/total, 3);
		relEvalMap.put("average", new ArrayList<Double>());
		relEvalMap.get("average").add(round(waccuracy, 3));	// Accuracy
		relEvalMap.get("average").add(round(wp, 3));			// Precision
		relEvalMap.get("average").add(round(wr, 3));			// Recall
		relEvalMap.get("average").add(round(wf1, 3));				// F1
		
	}
	
	public static void updateTestMaps(Cell cell, embeddings e){
		int correct = 0;	int t =0, f=0; int c = 0;	double l2Sum = 0.0;
		double dot = e.dot(cell, learner.enableBias, e.K, learner.ealpha, learner.onlyAlpha);
		double sigmdot = learner.sigm(dot);
		int pred = (sigmdot >= 0.5) ? 1 : 0;
		int truth = (cell.truth) ? 1 : 0;
		double l2 = (truth - sigmdot)*(truth-sigmdot);
		l2Sum += l2;
		//System.out.println(sigmdot + " " + pred + " " + truth + " " + l2);
		
		if(pred == truth)
			correct = 1;
		else
			correct = 0;
		c += correct;
		//System.out.println("rel : " + truth + " pred : " + pred);
		//System.out.println(cell.relation_id + " : " + pred);
		addInprfRelationMap(cell.relation_id, truth, pred, correct);
		addInRelAccuracyMap(cell, correct);
		addInRelCountMap(cell);
		addInRelL2Map(cell, l2);
	}
	
	public static void printEval(){
		for(String rel : relEvalMap.keySet()){
			ArrayList<Double> eval = relEvalMap.get(rel);
			System.out.print(rel + " : ");
			System.out.println("P : " + eval.get(1) + " R : " + eval.get(2) + " F1 : " + eval.get(3) + " Accuracy : " + eval.get(0));
		}
	}
	
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    if(Double.isNaN(value))
	    	return Double.NaN;
	    
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

	
	
}
