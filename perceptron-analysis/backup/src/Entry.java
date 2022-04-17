
public class Entry {
	private int attributes;
	
	private float[] x;
	private int y;
	
	public Entry(int a) {
		attributes = a;
		x = new float[attributes];
		
		for(int i = 0; i < attributes; i++) {
			x[i] = 0;
		}
		y = 0;
	}
	
	public int GetAttributes() {
		return attributes;
	}
	
	public void SetAttributeValue(int index, int value) {
		x[index] = value;
	}
	
	public float GetAttributeValue(int index) {
		return x[index];
	}
	
	public void SetLabel(int label) {
		y = label;
	}
	
	public int GetLabel() {
		return y;
	}
	
	public int GetClass() {
		if(y == 0 || y == 8 || y == 9 || y == 6) {
			return 1;
		}else {
			return -1;
		}
	}
	
}
