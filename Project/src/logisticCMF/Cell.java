package logisticCMF;
import java.util.*;


public class Cell {
	String relation_id;												// Id of relation in this data element
	ArrayList<String> entity_ids;									// List of entities participating in Relation ' relation_id '
	boolean truth;													// Truth Value for this data element
	
	public Cell(){
		entity_ids = new ArrayList<String>();
	}
	
	public Cell(String r, String e1, String e2, boolean t){
		entity_ids = new ArrayList<String>();
		relation_id = r;
		entity_ids.add(e1);
		entity_ids.add(e2);
		truth = t;
	}
	
}
