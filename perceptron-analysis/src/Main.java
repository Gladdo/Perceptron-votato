
import java.io.*;
import java.util.*;

public class Main {
	
	public static final String CONFIG_DATA_SET_SIZE_TAG = "dataSetSize";
	public static final String CONFIG_TRAIN_SET_SIZE_TAG = "trainSetSize";
	public static final String CONFIG_TEST_SET_SIZE_TAG = "testSetSize";
	public static final String CONFIG_ATTRIBUTES_TAG = "attributes";
	public static final String CONFIG_NUMBER_OF_MODELS_TAG = "numberOfModels";
	public static final String CONFIG_DATA_SET_FILE_PATH_TAG = "dataSetPath";
	public static final String CONFIG_TRAINING_ENTRIES_SETS = "trainingEntriesSets";
	private static final Properties config;
	
	static {
		//LOAD PROPERTIES
		config = new Properties();
		try {
			config.load(new FileInputStream("config.properties"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static final int DATA_SET_SIZE =  Integer.valueOf(config.getProperty(CONFIG_DATA_SET_SIZE_TAG));
	public static final int TRAIN_SET_SIZE = Integer.valueOf(config.getProperty(CONFIG_TRAIN_SET_SIZE_TAG));
	public static final int TEST_SET_SIZE = Integer.valueOf(config.getProperty(CONFIG_TEST_SET_SIZE_TAG));
	public static final int ATTRIBUTES = Integer.valueOf(config.getProperty(CONFIG_ATTRIBUTES_TAG));
	public static final int NUMBER_OF_MODELS = Integer.valueOf(config.getProperty(CONFIG_NUMBER_OF_MODELS_TAG));
	public static final String DATA_SET_FILE_PATH = config.getProperty(CONFIG_DATA_SET_FILE_PATH_TAG);
	
	//L'array TRAINING_ENTRIES_SETS specifica per ogni indice quanto addestrare i modelli prima di testarli.
	//TRAINING_ENTRIES_SETS[i] = V => all'iterazione i-esima si addestra i modelli su ulteriori V esempi del trainset
	//e poi si testa l'accuratezza
	public static final int[] TRAINING_ENTRIES_SETS = readIntegerArrayFromString(config.getProperty(CONFIG_TRAINING_ENTRIES_SETS), ",");
			
	public static int[] readIntegerArrayFromString(String string, String sep) {
		
		String[] splittedString = string.split(sep);
		
		int[] result = new int[splittedString.length];
		
		for(int i = 0; i < splittedString.length; i++) 
			result[i] = Integer.valueOf(splittedString[i]);
		
		return result;
	}
	
	static File results;
	static FileWriter resultsWriter;
	
	public static void main(String[] args) {

		//Inizializza il file di output: al suo interno verranno scritti 
		results = new File("analysisResults.txt");
		resultsWriter = null;
		try {
			results.createNewFile();
			resultsWriter = new FileWriter(results);
		} catch (IOException e) {
			System.out.println("Errore apertura file analysisResults.txt");
			e.printStackTrace();
		}
		
		//Initialize models	
		//models è un array di oggetti Model; ciascun oggetto nell'array models è utilizzato individualmente per 
		//produrre i dati dell'analisi del metodo
		Model[] models = new Model[Main.NUMBER_OF_MODELS];
		for(int i = 0; i < models.length; i++) {
			models[i] = new Model(Main.ATTRIBUTES);
		}
		
		//Loop principale:
		//All'iterazione i-esima si fa il training di tutti i modelli su un numero di esempi pari a
		//TRAINING_ENTRIES_SETS[i] (attraverso il metodo PerceptronTrainAllModelsListsOverEntries), si testa 
		//tutti i modelli (attraverso il metodo TestAllModels) e si stampa i risultati dell'analisi 
		//(attraverso il metodo PrintTestAnalysis)
		
		for(int i = 0; i < TRAINING_ENTRIES_SETS.length; i++) {
			int numberOfEntriesTrained = 0;
			for(int j = 0; j < i+1; j++) {
				numberOfEntriesTrained += TRAINING_ENTRIES_SETS[j];
			}
				
			Graphic.instance().SetStatus("Status: Training" );
			PerceptronTrainAllModelsListsOverEntries(models, TRAINING_ENTRIES_SETS[i]);
				
			Graphic.instance().SetStatus("Status: Testing");
			TestAllModels(models, numberOfEntriesTrained);
		}

	}
	
	/*	
	 *	L'aggiornamento dei modelli viene fatto in parallelo su diversi tread, ciascuno dei quali gestisce
	 *	un singolo modello.
	 *	Questo metodo da solo il via ai thread che addestrano i modelli; il codice per il training è nel metodo
	 *	run dell'oggetto Training
	 */
	static void PerceptronTrainAllModelsListsOverEntries(Model[] models, int numberOfEntries) {
		
		Training[] trainThreads = new Training[models.length];
		for(int modelIndex = 0; modelIndex < models.length; modelIndex++) {
			trainThreads[modelIndex] = new Training(models, modelIndex, numberOfEntries);
			trainThreads[modelIndex].start();
			//PerceptronTrainModelsListOverEntries(datasetsManager, modelsList, modelIndex, numberOfEntries);
		}
		for(int modelIndex = 0; modelIndex < models.length; modelIndex++) {
			try {
				trainThreads[modelIndex].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*	
	 *	Il test dei modelli viene fatto in parallelo su diversi tread, ciascuno dei quali gestisce
	 *	un singolo modello.
	 *	Questo metodo da solo il via ai thread che testano i modelli; il codice per il test è nel metodo
	 *	run dell'oggetto Testing
	 */
	static void TestAllModels(Model[] models, int numberOfEntriesTrained) {
		
		Testing[] testThreads = new Testing[models.length];
		
		for(int modelIndex = 0; modelIndex < models.length; modelIndex++) {
			//TestModelsList(datasetsManager, modelsLists, modelIndex, lastResults, votedResults);
			testThreads[modelIndex] = new Testing(models, modelIndex);
			testThreads[modelIndex].start();
		}
		for(int modelIndex = 0; modelIndex < models.length; modelIndex++) {
			try {
				testThreads[modelIndex].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		PrintTestAnalysis(testThreads,numberOfEntriesTrained,models);
	}
	
	
	//PRINT METHODS:
	
	/* ||PrintTestAnalysis||
	 * Ogni i-esimo testThread contiene i risultati di testing con predizioni last, avg e voted ottenuti dal test sul
	 * modello i-esimo.
	 * Questo metodo fa la media del numero di errori nei diversi tipi di predizioni commessi da ogni modello e 
	 * stampa le seguenti cose sul file txt aperto nell'oggetto results:
	 * - Il numero di entries su cui sono stati addestrati i modelli
	 * - Per ciascun modello, quanti errori sono stati commessi durante il training
	 * - La media degli errori di training
	 * - Per ciascun modello, quanti errori commettono nella previsione Last
	 * - La media degli errori  nella previsione Last
	 * - Per ciascun modello, quanti errori commettono nella previsione Voted
	 * - La media degli errori  nella previsione Voted
	 * - Per ciascun modello, quanti errori commettono nella previsione Avg
	 * - La media degli errori  nella previsione Avg
	 * 
	 * Quindi stampa le precedenti informazioni sugli errori come punti sul grafico dell'UI dell'applicazione
	 * */	
	static void PrintTestAnalysis(Testing[] testThreads,int numberOfEntriesTrained, Model[] models) {
		try {
			resultsWriter.write("------------------------------------------------------------------");
			resultsWriter.write( System.getProperty( "line.separator" ));
			
			//TRAINING INFO PRINT:
			
			//Stampa le informazioni sul training di ciascun modello dopo che questi sono stati addestrati su
			//numberOfTrainedEntries entries
			resultsWriter.write("Results after training over " + numberOfEntriesTrained + " entries ");
			resultsWriter.write( System.getProperty( "line.separator" ));;
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write("     TRAINING ERRORS");
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			float avgTrainingErrors = 0;
			for(int modelIndex = 0; modelIndex < models.length; modelIndex++) {
				
				//Per ciascun modello stampa la lunghezza della lista dei piani: questa equivale al numero
				//di errori commessi durante il training da ciascun modello
				resultsWriter.write("     Model " + modelIndex + ": " + models[modelIndex].ListLength());
				avgTrainingErrors = avgTrainingErrors + models[modelIndex].ListLength();
				resultsWriter.flush();
			}
			
			//Stampa la media del numero di errori commessi da ciascun modello durante il training; questa sarà
			//la media tra le lunghezze delle liste di piani in ciascun modello
			avgTrainingErrors = avgTrainingErrors / models.length;
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write("     Average: "+avgTrainingErrors);
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			
			//#######################
			
			//TESTING INFO PRINT:
			
			//Stampa il numero di errori commesso da ciascun modello sul testset con previsione Last e la media
			//di tali valori
			
			resultsWriter.write("     LAST MODELS TEST ERRORS:");
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			float avgLastErrors = 0;
			for(int modelIndex = 0; modelIndex < models.length; modelIndex++) {
				resultsWriter.write("     Model " + modelIndex + ": " + testThreads[modelIndex].GetLastErrors());
				avgLastErrors = avgLastErrors +  testThreads[modelIndex].GetLastErrors();
				resultsWriter.flush();
			}
			avgLastErrors = avgLastErrors / models.length;
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write("     Average: "+avgLastErrors);
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			
			//Stampa il numero di errori commesso da ciascun modello sul testset con previsione Voted e la media
			//di tali valori
			resultsWriter.write("     VOTED MODELS TEST ERRORS:");
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			float avgVotedErrors = 0;
			for(int modelIndex = 0; modelIndex < models.length; modelIndex++) {
				resultsWriter.write("     Model " + modelIndex + ": " + testThreads[modelIndex].GetVotedErrors());
				avgVotedErrors += testThreads[modelIndex].GetVotedErrors();
				resultsWriter.flush();
			}
			resultsWriter.write( System.getProperty( "line.separator" ));
			avgVotedErrors = avgVotedErrors / models.length;
			resultsWriter.write("     Average: "+avgVotedErrors);	
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			
			//Stampa il numero di errori commesso da ciascun modello sul testset con previsione Average e la media
			//di tali valori
			resultsWriter.write("     AVERAGE MODELS TEST ERRORS:");
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			
			//Calcola la media degli errori di classificazione commessi da ciascun modello average 
			float avgAvgErrors = 0;
			for(int modelIndex = 0; modelIndex < models.length; modelIndex++) {
				resultsWriter.write("     Model " + modelIndex + ": " + testThreads[modelIndex].GetAvgErrors());
				avgAvgErrors += testThreads[modelIndex].GetAvgErrors();
				resultsWriter.flush();
			}
			resultsWriter.write( System.getProperty( "line.separator" ));
			avgAvgErrors = avgAvgErrors / models.length;
			
			resultsWriter.write("     Average: "+avgAvgErrors);	
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			
			
			//Aggiungi i punti sul grafico dell'UI: epoch è l'ascissa, il numero di errori è l'ordinata.
			//l'epoca è calcolata in relazione al numero di entries su cui si è fatto il training fino
			//a questo momento in relazione alla grandezza del dataset di training. 
			
			float epoch = ((float)numberOfEntriesTrained) / TRAIN_SET_SIZE;
			Graphic.instance().AddLastErrorPoint(epoch, avgLastErrors);
			Graphic.instance().AddVotedErrorPoint(epoch, avgVotedErrors);
			Graphic.instance().AddAvgErrorPoint(epoch, avgAvgErrors);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void PrintModel(LinearPlane m) {
		for(int i = 0; i < m.GetAttributes(); i++) {
			System.out.print(m.GetWValue(i) + "," );
		}
		System.out.print( "b = " + m.GetBValue());
		System.out.println("");
	}
	
	static void PrintEntry(Entry e) {
		for(int i = 0; i < e.GetAttributes(); i++) {
			System.out.print(e.GetAttributeValue(i) + ",");
		}
		System.out.print( "y = " + e.GetLabel());
		System.out.println("");
	}
	
}

//#####################################################################
