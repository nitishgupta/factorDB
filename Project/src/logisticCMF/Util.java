package logisticCMF;

import java.io.IOException;
import java.util.ArrayList;



/*import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;*/
import java.util.*;

//import postProcessing.data;


public class Util {
	
	static Random seed = new Random(100);
		
	public static HashSet<String> getColdEntites(ArrayList<Cell> data, int index, double perc){
		HashSet<String> entities = new HashSet<String>();
		for(Cell cell : data)
			entities.add(cell.entity_ids.get(index));
		
		ArrayList<String> ents = new ArrayList<String>(entities);
		Collections.shuffle(ents, seed);
		HashSet<String> coldEntities = new HashSet<String>(ents.subList(0, (int)(perc*entities.size()/100.0)));
		return coldEntities;
		
	}
	
	public static int getNegSampleSize(data Data){
		return (int)(((double)Data.Data.size())/Data.entityIds.get(0).size());
	}
	
	public static double getMatrixDetails(data Data){
		System.out.println("rows : " + Data.entityIds.get(0).size());
		System.out.println("cols : " + Data.entityIds.get(1).size());
		System.out.println("size : " + Data.Data.size());
		return ((double)Data.Data.size())/Data.entityIds.get(0).size();
		
	}
	
	/*public static void plotGraph(ArrayList<Double> x, ArrayList<Double> y, String filename) throws IOException{
		XYSeries series = new XYSeries(filename);
		 for(int i =0; i<x.size(); i++)
			 series.add(x.get(i), y.get(i));
		 // Add the series to your data set
		 XYSeriesCollection dataset = new XYSeriesCollection();
		 dataset.addSeries(series);
		 // Generate the graph
		 JFreeChart chart = ChartFactory.createXYLineChart(
		 "XY Chart", // Title
		 "x-axis", // x-axis Label
		 "y-axis", // y-axis Label
		 dataset, // Dataset
		 PlotOrientation.VERTICAL, // Plot Orientation
		 true, // Show Legend
		 true, // Use tooltips
		 false // Configure chart to generate URLs?
		 );
		 XYPlot plot = (XYPlot) chart.getPlot();
		 ValueAxis yAxis = plot.getRangeAxis();
		 yAxis.setRange(0.5, 1.0);
		 
		 ChartFrame frame = new ChartFrame("Results", chart);
		 frame.pack();
		 frame.setVisible(true);

	}*/

	public static void checkColdStartSanity(data D){
		HashSet<String> entities = new HashSet<String>();
		int index = 0, flag = 0;
		for(Cell cell : D.trainData){
			entities.add(cell.entity_ids.get(index));
		}
		for(Cell cell : D.valData){
			entities.add(cell.entity_ids.get(index));
		}
		for(Cell cell : D.testData){
			if(entities.contains(cell.entity_ids.get(index))){
				System.out.print("WRONG COLD START SPLIT, Index : " + index + ", ");
				flag = 1;
				break;
			}
		}
		if(flag == 0){
			System.out.print("Index ColdStart : " + index + ", ");
		}
		
		index = 1; flag = 0;
		entities = new HashSet<String>();
		for(Cell cell : D.trainData){
			entities.add(cell.entity_ids.get(index));
		}
		for(Cell cell : D.valData){
			entities.add(cell.entity_ids.get(index));
		}
		for(Cell cell : D.testData){
			if(entities.contains(cell.entity_ids.get(index))){
				System.out.println("NO COLD START SPLIT, Index : " + index);
				flag = 1;
				break;
			}
		}
		if(flag == 0)
			System.out.println("Index ColdStart : " + index);
		
	}

	public static void countEntities(ArrayList<Cell> data){
		HashSet<String> e1 = new HashSet<String>();
		HashSet<String> e2 = new HashSet<String>();
		
		for(Cell cell : data){
			e1.add(cell.entity_ids.get(0));
			e2.add(cell.entity_ids.get(1));
		}
		
		System.out.println("e1 : " + e1.size() + "  e2 : "+e2.size());
		
	}
	
	public static void implicitColdStart(ArrayList<Cell> trdata, ArrayList<Cell> testData){
		HashSet<String> tre1 = new HashSet<String>();
		HashSet<String> tre2 = new HashSet<String>();
		
		for(Cell cell : trdata){
			tre1.add(cell.entity_ids.get(0));
			tre2.add(cell.entity_ids.get(1));
		}
		
		System.out.println("tre1 : " + tre1.size() + "  tre2 : "+ tre2.size());
		
		HashSet<String> tee1 = new HashSet<String>();
		HashSet<String> tee2 = new HashSet<String>();
		for(Cell cell : testData){
			tee1.add(cell.entity_ids.get(0));
			tee2.add(cell.entity_ids.get(1));
		}
		
		System.out.println("tee1 : " + tee1.size() + "  tee2 : "+ tee2.size());
		tee1.removeAll(tre1);
		tee2.removeAll(tre2);
		
		
		System.out.println("e1 cold : " + tee1.size() + "  e2 cold : " + tee2.size() );
		
	}
}
