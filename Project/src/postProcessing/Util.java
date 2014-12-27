package postProcessing;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Util {
	
	public static double sigm(double x){
		return 1.0 / (1.0 + Math.exp(-x));
	}
	
	public static double norm(double [] vec){
		double norm = 0.0;
		for(int i=0; i<vec.length; i++){
			norm += vec[i]*vec[i];
		}
		norm = Math.sqrt(norm);
		return norm;
	}
	
	public static double dotProd(double [] vec1, double [] vec2){
		double dot = 0.0;
		for(int k = 0; k<vec1.length; k++)
			dot += vec1[k]*vec2[k];

		return dot;
	}

	public static double cosineDis(double[] vec1, double[] vec2){
		double cos = 0.0;
		cos = Util.dotProd(vec1, vec2);
		cos = cos/(Util.norm(vec1)*Util.norm(vec2));
		//return cos;
		return Util.dotProd(vec1, vec2);
		//return Util.sigm(Util.dotProd(vec1, vec2));
	}
	
	public static ArrayList<String> getKNN(Map<String, Double> map, int K){
		ArrayList<String> top = new ArrayList<String>();
		Set<Entry<String, Double>> set = map.entrySet();
        List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
        Collections.sort( list, new Comparator<Map.Entry<String, Double>>()
        {
            public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
            {
                return (o2.getValue()).compareTo( o1.getValue() );
            }
        } );
        
        for(int i=0; i<K; i++){
        	top.add(list.get(i).getKey());
        }
        
        return top;
        /*for(Map.Entry<String, Double> entry : list)
            System.out.println(entry.getKey() + "  ==== " + entry.getValue());*/
	}
	
	public static Set<String> readEntitiesForTSNE(String fileAddress) throws IOException{
		Set<String> entities = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(fileAddress));
		String line;
		while((line = br.readLine()) != null){
			String en = line.trim();
			entities.add(en);
		}
		
		return entities;
		
	}

	public static Set<String> getKNNEntities(Set<String> sourceEntities, Similarity s){
		Set<String> targetEntities = new HashSet<String>();
		
		for(String e1 : sourceEntities){
			for(String e2 : s.simMap.get(e1)){
				targetEntities.add(e2);
			}
		}
		return targetEntities;
	}
	
	public static void writeEmbeddingsForSet(Set<String> entitiesToWrite, EntityEmbeddings ee, String writePath) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(writePath));
		for(String entity : entitiesToWrite){
			bw.write(entity + " :: ");
			for(int i=0; i<ee.entityVector.get(entity).length; i++){
				bw.write(ee.entityVector.get(entity)[i] + ", ");
			}
			bw.write("\n");
		}
		bw.close();
	}
	
	public static void writeSimilarEntities(Set<String> e1sToWrite, Map<String, ArrayList<String>> simMap, String writePath) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(writePath));
		for(String entity : e1sToWrite){
			bw.write(entity + "\t");
			for(String e2 : simMap.get(entity)){
				bw.write(e2 + "\t");
			}
			bw.write("\n");
		}
		bw.close();
	}
}
