/*
 * Un oggetto di tipo Entry modella le informazioni di una esempio nel dataset; l'array x[] contiene i 
 * valori degli attributi dell'immagine dell'esempio mentre la variabile y contiene la label associata a tali valori,
 * ovvero la cifra rappresentata dall'immagine.
 * 
 * y è dunque si riferisce all'esempio del dataset e non rappresenta un una classe 1 o -1; la classe binaria di 
 * appartenenza dell'istanza x si ottiene invece con il metodo GetClass il quale codifica, in relazione
 * al label y, a quale classe binaria (1 o -1) appartiene l'istanza x.
 * 
 * Nel nostro caso le classi scelte sono:
 * - classe 1: immagini che raffigurano uno 0 un 6 un 8 o un 9
 * - classe -1: immagini che raffigurano un 1 un 2 un 3 un 4 un 5 o un 7
 * */

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
	
	public float Norm() {
		float sum = 0;
		for(int i = 0; i < attributes; i++) {
			sum = sum + x[i]*x[i];
		}
		return (float)Math.sqrt(sum);
	}
	
}
