import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/*
 * Un oggetto di tipo Modello contiene una lista di piani; la lista di piani è prodotta dalla fase di training; i piani
 * nella lista sono ordinati nella sequenza con cui vengono prodotti.
 * 
 * Modello Specifica un metodo (ExecuteModelPrediction) per calcolare la predizione dei classificatori lineari voted e 
 * average sulla entry in input; questi fanno dell'intera lista dei piani.
 * */

public class Model {
	//number of parameters for model w
	private int attributes;
	
	private List<LinearPlane> linearPlanes;
	private LinearPlane lastLinearPlane;
	
	int votedPrediction;
	int avgPrediction;
	
	public Model(int a) {
		attributes = a;
		linearPlanes = new LinkedList<LinearPlane>();
		linearPlanes.add(new LinearPlane(attributes));
		lastLinearPlane = linearPlanes.get(linearPlanes.size() - 1);
	}
	
	//entry already parsed	
	public void AddPlane(LinearPlane m) {
		linearPlanes.add(m);
		lastLinearPlane = m;
	}
	
	public LinearPlane GetLastPlane() {
		return lastLinearPlane;
	}
	
	//Il calcolo della predizione in modalità voted e average si ottiene scorrendo la lista dei piani prodotti
	//dal training; per non scorrere due volte la lista, il calcolo della predizione di entrambi i metodi viene
	//fatta sulla stesso loop
	public void ExecuteModelPrediction(Entry entry) {
		float avgResult = 0;
		float votedResult = 0;
		for(Iterator<LinearPlane> planeIterator = linearPlanes.iterator(); planeIterator.hasNext();) {
			LinearPlane currentPlane = planeIterator.next();
			float currentPlaneValue = currentPlane.PlaneValue(entry);
			int currentPlaneSuccesses = currentPlane.GetPlaneClassificationSuccesses();
			votedResult = votedResult + currentPlaneSuccesses * Sign(currentPlaneValue);
			avgResult = avgResult + currentPlaneSuccesses * currentPlaneValue;
		}
		votedPrediction = Sign(votedResult);
		avgPrediction = Sign(avgResult);
	}
	
	public int GetVotedPrediction() {
		return votedPrediction;
	}
	public int GetAvgPrediction() {
		return avgPrediction;
	}
	
	int Sign(float value) {
		if(value < 0) {
			return -1;
		}else {
			return 1;
		}
	}
	
	public void PrintPlaneList() {
		for(Iterator<LinearPlane> m = linearPlanes.iterator(); m.hasNext();) {
			LinearPlane currentModel = m.next();
			PrintPlane(currentModel);
		}
	}
	
	void PrintPlane(LinearPlane m) {
		for(int i = 0; i < m.GetAttributes(); i++) {
			System.out.print(m.GetWValue(i) + "," );
		}
		System.out.print( "b = " + m.GetBValue());
		System.out.println("");
	}
	
	public int ListLength() {
		return linearPlanes.size();
	}
}
