import java.io.*;
import java.util.Scanner;
import java.util.Random;

/*
 * Questo oggetto gestisce le interazioni con il dataset.
 * Il dataset è caricato nella memoria del programma sotto forma di array di stringhe: ciascun elemento dell'array
 * contiene una riga del dataset.
 * Quindi per ogni modello viene creato un'array datasetIndexesPermutations[i][j] che contiene per ogni i-esimo
 * modello una permutazione che specifica con quale ordine leggere gli esempi dal dataset.
 * In questo modo le liste dei piani prodotti dai vari modelli sono auspicabilmente differenti e l'analisi del
 * metodo di training è meno influenzato dallo specifico ordine nel dataset (nel caso in cui si vada a utilizzare
 * più dataset).
 * 
 * L'array indiceLettura tiene conto per ogni modello quale è il prossimo esempio da leggere per la fase di
 * training. Si può quindi utilizzare il metodo ParseNextTrainEntry senza preoccuparsi di quale esempio si era rimasti
 * a leggere durante il training per un certo modello
 *
 * */

public class DatasetsManager {
	
	//Dataset utils
	String[] datasetRam;
	
	private int[] indiceLettura;
	private int[][] datasetIndexesPermutations;
	
	private static DatasetsManager instance = null;
	
	private float maxDatasetR;
	
	private DatasetsManager() {
		init();
	}
	
	public static DatasetsManager instance() {
		if(instance==null) {
			instance = new DatasetsManager();
		}
		return instance;
	}
	
	//Initalize datasetsManager
	private void init() {
		
		//Setup datasetRam
		LoadDatasetRam(Main.DATA_SET_FILE_PATH);
		
		//Setup datasets permutations		
		indiceLettura = new int[Main.NUMBER_OF_MODELS];
		for(int i = 0; i < Main.NUMBER_OF_MODELS; i++) {
			indiceLettura[i] = 0;
		}
		datasetIndexesPermutations = new int[Main.NUMBER_OF_MODELS][Main.DATA_SET_SIZE];
		InitializeIndexesPermutations();	
		
		maxDatasetR = calculateMaxNorm();
	}
	
	private float calculateMaxNorm() {
		float maxR = 0;
		Entry entry = new Entry(Main.ATTRIBUTES);
	
		for(int index = 0; index < Main.TRAIN_SET_SIZE; index++) {
			String[] entryParts = datasetRam[index].split(",");
			for(int j = 0; j < Main.ATTRIBUTES; j ++) {
				entry.SetAttributeValue(j, Integer.parseInt(entryParts[j]));
			}
			if(entry.Norm() > maxR) {
				maxR = entry.Norm();
			}
		}
		return maxR;
	}
	
	public float getMaxNorm() {
		return maxDatasetR;
	}
	
	//Carica il dataset in datasetPath nell'array di stringe datasetRam
	private void LoadDatasetRam(String datasetPath) {
		datasetRam = new String[Main.DATA_SET_SIZE];
		File datasetFile = new File(datasetPath);
		Scanner datasetReader = null;
		try {
			datasetReader = new Scanner(datasetFile);
		} catch (FileNotFoundException e) {
			System.out.println("Errore creazione datasetReader!");
			e.printStackTrace();
		}
		if(datasetReader != null) {
			for(int i = 0; i < Main.DATA_SET_SIZE; i++) {
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
		for(int datasetIndex = 0; datasetIndex < Main.NUMBER_OF_MODELS; datasetIndex++) {
			Random rand = new Random(datasetIndex * 27);
			for(int i = 0; i < Main.DATA_SET_SIZE; i++) {
				datasetIndexesPermutations[datasetIndex][i] = i;
			}
			for(int i = 0; i < Main.DATA_SET_SIZE; i++) {
				//scegli casualmente un indice da swappare tra 0 e TRAIN_SET_SIZE con l'indice i corrente
				int swapIndex = rand.nextInt(Main.TRAIN_SET_SIZE);
				int tmp = datasetIndexesPermutations[datasetIndex][i];
				datasetIndexesPermutations[datasetIndex][i] = datasetIndexesPermutations[datasetIndex][swapIndex];
				datasetIndexesPermutations[datasetIndex][swapIndex] = tmp;
			}
		}	
	}
	
	//Carica un esempio del dataset all'interno dell'oggetto entry. L'esempio caricato dipende da quale è l'ultimo
	//esempio che è stato letto per il modello di indice modelIndex (che corrisponde a index = indiceLettura[modelIndex])
	//e dalla permutazione degli indici con il quale quest'ultimo legge dal dataset ( specificato dall'array contenuto
	//in datasetIndexesPermutations[modelIndex]).
	public void ParseNextTrainEntry(int modelIndex, Entry entry) {
		int index = indiceLettura[modelIndex];
		int datasetIndex = datasetIndexesPermutations[modelIndex][index];
		
		String[] entryParts = datasetRam[datasetIndex].split(",");
		
		for(int i = 0; i < Main.ATTRIBUTES; i ++) {
			entry.SetAttributeValue(i, Integer.parseInt(entryParts[i]));
		}
		entry.SetLabel(Integer.parseInt(entryParts[Main.ATTRIBUTES]));
		
		//Aggiorna l'indice di lettura del modello modelIndex; se si supera l'ultimo esempio del dataset si riparte
		//dal primo esempio
		indiceLettura[modelIndex]++;
		if(indiceLettura[modelIndex] >= Main.TRAIN_SET_SIZE) {
			indiceLettura[modelIndex] -= Main.TRAIN_SET_SIZE;
		}
	}
	
	public void ParseInputTestset(int entryIndex, Entry entry) {
		int index = entryIndex + Main.TRAIN_SET_SIZE;
		if(index < Main.DATA_SET_SIZE) {
			String[] entryParts = datasetRam[index].split(",");
			for(int i = 0; i < Main.ATTRIBUTES; i ++) {
				entry.SetAttributeValue(i, Integer.parseInt(entryParts[i]));
			}
			entry.SetLabel(Integer.parseInt(entryParts[Main.ATTRIBUTES]));
		}else {
			System.out.println("Indice della entry del testset troppo grande");
		}
		
	}
}
