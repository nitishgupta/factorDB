package postProcessing;
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
	
	public static void writeSelectEmbeddingsToFile(String folder, String selectionEmbeddings, Map<String, ArrayList<String>> simMap){
		
	}

}
