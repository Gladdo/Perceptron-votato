
/*
 * Questo oggetto modella i parametri w e b di un piano classificatore; il metodo PlaneValue fornisce il risultato
 * del calcolo (w x + b), dove i valori di x sono contenuti nell'oggetto entry in input al metodo.
 * */

public class LinearPlane {
	
	private int attributes;
	private float[] w;
	private float b;
	
	private int successes;
	
	public LinearPlane(int a) {
		attributes = a;
		w = new float[attributes];
		
		//after instancing the model is set to 0
		for(int i = 0; i < attributes; i++) {
			w[i] = 0;
		}
		b=0;
		successes = 0;
	}
	
	//Clone constructor
	public LinearPlane(LinearPlane m) {
		attributes = m.GetAttributes();
		w = new float[attributes];
		
		for(int i = 0; i < attributes; i ++) {
			w[i] = m.GetWValue(i);
		}
		b = m.GetBValue();
		successes = 0;
	}
	
	public void SetWValue(int index, float value) {
		w[index] = value;
	}
	
	public float GetWValue(int i) {
		return w[i];
	}
	
	public float GetBValue() {
		return b;
	}
	
	public int GetAttributes() {
		return attributes;
	}
	
	//Ritorna il valore sign(w x + b) dove w e b sono i parametri del piano e x è l'istanza contenuta nella entry in
	//input
	public int PlanePrediction(Entry entry) {
		return Sign(PlaneValue(entry));
	}
	
	//Ritorna il valore (w x + b) dove w e b sono i parametri del piano e x è l'istanza contenuta nella entry in
	//input
	public float PlaneValue(Entry entry) {
		float result = 0;
		for(int i = 0; i < attributes; i ++) {
			result = result + entry.GetAttributeValue(i) * w[i]; 
		}
		result = result + b;
		return result;
	}
	
	//Questo metodo aggiorna i parametri del piano in relazione all'esempio contenuto nella entry
	//E' utilizzato durante il training
	public void UpdatePlaneParametersOverEntry(Entry entry) {
		for(int i = 0; i < attributes; i ++) {
			w[i] = w[i] + entry.GetAttributeValue(i) * entry.GetClass();
		}
		b = b+entry.GetClass() * DatasetsManager.instance().getMaxNorm();//*DatasetsManager.instance().getMaxNorm();
	}
	
	public void IncreasePlaneClassificationSuccesses() {
		successes ++;
	}
	
	public int GetPlaneClassificationSuccesses() {
		return successes;
	}
	
	int Sign(float value) {
		if(value < 0) {
			return -1;
		}else {
			return 1;
		}
	}
}
