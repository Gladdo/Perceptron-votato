import java.io.*;
import java.util.Scanner;
import java.util.Random;

//Crea e gestisce le varie permutazioni del dataset dataset.txt
public class DatasetsManager {
	
	//Dataset utils
	private int attributes;
	private int datasetSize;
	String[] datasetRam;
	
	private int trainsetSize;
	private int testsetSize; 
	
	private int numberOfDatasets;
	private int[] indiceLettura;
	private int[][] datasetIndexesPermutations;
	
	//Initalize datasetsManager
	public DatasetsManager(int numberOfDatasets, String datasetPath, int datasetSize, int attributes, int trainsetSize, int testsetSize) {
		
		//Setup datasetRam
		this.attributes = attributes;
		this.datasetSize = datasetSize;
		LoadDatasetRam(datasetPath, datasetSize);
		
		//Setup trainset and testset Size
		this.trainsetSize = trainsetSize;
		this.testsetSize = testsetSize;
		
		//Setup datasets permutations		
		this.numberOfDatasets = numberOfDatasets;
		indiceLettura = new int[numberOfDatasets];
		for(int i = 0; i < numberOfDatasets; i++) {
			indiceLettura[i] = 0;
		}
		datasetIndexesPermutations = new int[numberOfDatasets][datasetSize];
		InitializeIndexesPermutations();	
	}
	
	//Carica il dataset in datasetPath nell'array di stringe datasetRam
	private void LoadDatasetRam(String datasetPath, int datasetSize) {
		datasetRam = new String[datasetSize];
		File datasetFile = new File(datasetPath);
		Scanner datasetReader = null;
		try {
			datasetReader = new Scanner(datasetFile);
		} catch (FileNotFoundException e) {
			System.out.println("Errore creazione datasetReader!");
			e.printStackTrace();
		}
		if(datasetReader != null) {
			for(int i = 0; i < datasetSize; i++) {
				if(datasetReader.hasNextLine()) {
					datasetRam[i] = datasetReader.nextLine();
				}
			}
		}else {
			System.out.println("DatasetReader = NULL");
		}
		
		datasetReader.close();
	}
	
	void InitializeIndexesPermutations() {
		for(int datasetIndex = 0; datasetIndex < numberOfDatasets; datasetIndex++) {
			Random rand = new Random(datasetIndex * 27);
			for(int i = 0; i < datasetSize; i++) {
				datasetIndexesPermutations[datasetIndex][i] = i;
			}
			for(int i = 0; i < datasetSize; i++) {
				int swapIndex = rand.nextInt(trainsetSize);
				int tmp = datasetIndexesPermutations[datasetIndex][i];
				datasetIndexesPermutations[datasetIndex][i] = datasetIndexesPermutations[datasetIndex][swapIndex];
				datasetIndexesPermutations[datasetIndex][swapIndex] = tmp;
			}
		}	
	}
	
	public void ParseNextTrainEntry(int modelIndex, Entry entry) {
		int index = indiceLettura[modelIndex];
		int datasetIndex = datasetIndexesPermutations[modelIndex][index];
		
		String[] entryParts = datasetRam[datasetIndex].split(",");
		
		for(int i = 0; i < attributes; i ++) {
			entry.SetAttributeValue(i, Integer.parseInt(entryParts[i]));
		}
		entry.SetLabel(Integer.parseInt(entryParts[attributes]));
		
		//Aggiorna l'indice di lettura del modello modelIndex
		indiceLettura[modelIndex]++;
		if(indiceLettura[modelIndex] >= trainsetSize) {
			indiceLettura[modelIndex] -= trainsetSize;
		}
	}
	
	public void ParseInputTestset(int entryIndex, Entry entry) {
		int index = entryIndex + trainsetSize;
		if(index < datasetSize) {
			String[] entryParts = datasetRam[index].split(",");
			for(int i = 0; i < attributes; i ++) {
				entry.SetAttributeValue(i, Integer.parseInt(entryParts[i]));
			}
			entry.SetLabel(Integer.parseInt(entryParts[attributes]));
		}else {
			System.out.println("Indice della entry del testset troppo grande");
		}
		
	}
	
	public int GetAttributesNumber() {
		return attributes;
	}
	
	public int GetTestsetSize() {
		return testsetSize;
	}
}
