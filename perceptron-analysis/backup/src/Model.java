
public class Model {
	
	private int attributes;
	private float[] w;
	private float b;
	
	private int modelClassificationSuccesses;
	
	public Model(int a) {
		attributes = a;
		w = new float[attributes];
		
		//after instancing the model is set to 0
		for(int i = 0; i < attributes; i++) {
			w[i] = 0;
		}
		b=0;
		modelClassificationSuccesses = 0;
	}
	
	//Clone constructor
	public Model(Model m) {
		attributes = m.GetAttributes();
		w = new float[attributes];
		
		for(int i = 0; i < attributes; i ++) {
			w[i] = m.GetWValue(i);
		}
		b = m.GetBValue();
		modelClassificationSuccesses = 0;
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
	
	//must return 1 or -1
	public int ModelPrediction(Entry entry) {
		float result = 0;
		for(int i = 0; i < attributes; i ++) {
			result = result + entry.GetAttributeValue(i) * w[i]; 
		}
		result = result + b;
		return Sign(result);
	}
	
	public float ModelPredictionValue(Entry entry) {
		float result = 0;
		for(int i = 0; i < attributes; i ++) {
			result = result + entry.GetAttributeValue(i) * w[i]; 
		}
		result = result + b;
		return result;
	}
	
	public void ModelUpdateOverEntry(Entry entry) {
		for(int i = 0; i < attributes; i ++) {
			w[i] = w[i] + entry.GetAttributeValue(i) * entry.GetClass();
		}
		b = b+entry.GetLabel();
	}
	
	public void IncreaseModelClassificationSuccesses() {
		modelClassificationSuccesses ++;
	}
	
	public int GetModelClassificationSuccesses() {
		return modelClassificationSuccesses;
	}
	
	int Sign(float value) {
		if(value < 0) {
			return -1;
		}else {
			return 1;
		}
	}
}
