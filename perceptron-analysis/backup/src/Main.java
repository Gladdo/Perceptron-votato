import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.*;
import java.util.*;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.jfree.chart.ChartPanel;
import org.jfree.ui.RefineryUtilities;

public class Main {
	
	
	public static void main(String[] args) {
		int datasetSize = 70000;
		int trainsetSize = 60000;
		int testsetSize = 10000;
		int attributes = 784;
		int numberOfModels = 6;//Integer.valueOf(args[0]);
		
		DatasetsManager datasetsManager;
	
		ModelList[] modelsLists = new ModelList[numberOfModels];
		for(int i = 0; i < numberOfModels; i++) {
			modelsLists[i] = new ModelList(attributes);
		}
		Graphic graphic = new Graphic(numberOfModels);
		
		//generate datasets and testset
		datasetsManager = new DatasetsManager(numberOfModels, "dataset.txt", datasetSize, attributes, trainsetSize, testsetSize);
		
		File results = new File("analysisResults.txt");
		FileWriter resultsWriter = null;
		try {
			results.createNewFile();
			resultsWriter = new FileWriter(results);
		} catch (IOException e) {
			System.out.println("Errore apertura file analysisResults.txt");
			e.printStackTrace();
		}
		
		int trainingSets = 7;
		int[] trainingEntriesSets = {1000, 5000, 54000, 60000, 60000, 60000, 60000*6}; 
		
		for(int i = 0; i < trainingSets; i++) {
			int numberOfTrainedEntries = 0;
			for(int j = 0; j < i+1; j++) {
				numberOfTrainedEntries += trainingEntriesSets[j];
			}
				
			graphic.SetStatus("Status: Training" );
			PerceptronTrainAllModelsListsOverEntries(datasetsManager, modelsLists, numberOfModels, trainingEntriesSets[i], graphic);
				
			graphic.SetStatus("Status: Testing");
			TestAllModelsLists(datasetsManager, modelsLists, numberOfModels, numberOfTrainedEntries, graphic, resultsWriter);
		}

	}
	
	static void PerceptronTrainAllModelsListsOverEntries(DatasetsManager datasetsManager, ModelList[] modelsList, int numberOfModels, int numberOfEntries, Graphic graphic) {
		Training[] trainThreads = new Training[numberOfEntries];
		for(int modelIndex = 0; modelIndex < numberOfModels; modelIndex++) {
			trainThreads[modelIndex] = new Training(datasetsManager, modelsList, modelIndex, numberOfEntries, graphic);
			trainThreads[modelIndex].start();
			//PerceptronTrainModelsListOverEntries(datasetsManager, modelsList, modelIndex, numberOfEntries);
		}
		for(int modelIndex = 0; modelIndex < numberOfModels; modelIndex++) {
			try {
				trainThreads[modelIndex].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	static void TestAllModelsLists(DatasetsManager datasetsManager, ModelList[] modelsLists, int numberOfModels, int numberOfTrainedEntries, Graphic graphic, FileWriter resultsWriter) {
		//int[] lastResults = new int[numberOfModels];
		//int[] votedResults = new int[numberOfModels];
		
		Testing[] testThreads = new Testing[numberOfModels];
		
		for(int modelIndex = 0; modelIndex < numberOfModels; modelIndex++) {
			//TestModelsList(datasetsManager, modelsLists, modelIndex, lastResults, votedResults);
			testThreads[modelIndex] = new Testing(datasetsManager, modelsLists, modelIndex, graphic);
			testThreads[modelIndex].start();
		}
		for(int modelIndex = 0; modelIndex < numberOfModels; modelIndex++) {
			try {
				testThreads[modelIndex].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//PRINT ANALISYS
		try {
			
			resultsWriter.write("------------------------------------------------------------------");
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write("Results after training over " + numberOfTrainedEntries + " entries ");
			resultsWriter.write( System.getProperty( "line.separator" ));;
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write("     TRAINING ERRORS");
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			float avgTrainingErrors = 0;
			for(int modelIndex = 0; modelIndex < numberOfModels; modelIndex++) {
				resultsWriter.write("     Model " + modelIndex + ": " + modelsLists[modelIndex].ListLength());
				avgTrainingErrors = avgTrainingErrors + modelsLists[modelIndex].ListLength();
				resultsWriter.flush();
			}
			avgTrainingErrors = avgTrainingErrors / numberOfModels;
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write("     Average: "+avgTrainingErrors);
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			
			resultsWriter.write("     LAST MODELS TEST ERRORS:");
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			float avgLastErrors = 0;
			for(int modelIndex = 0; modelIndex < numberOfModels; modelIndex++) {
				resultsWriter.write("     Model " + modelIndex + ": " + testThreads[modelIndex].GetLastErrors());
				avgLastErrors = avgLastErrors +  testThreads[modelIndex].GetLastErrors();
				resultsWriter.flush();
			}
			avgLastErrors = avgLastErrors / numberOfModels;
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write("     Average: "+avgLastErrors);
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			
			resultsWriter.write("     VOTED MODELS TEST ERRORS:");
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			float avgVotedErrors = 0;
			for(int modelIndex = 0; modelIndex < numberOfModels; modelIndex++) {
				resultsWriter.write("     Model " + modelIndex + ": " + testThreads[modelIndex].GetVotedErrors());
				avgVotedErrors += testThreads[modelIndex].GetVotedErrors();
				resultsWriter.flush();
			}
			resultsWriter.write( System.getProperty( "line.separator" ));
			avgVotedErrors = avgVotedErrors / numberOfModels;
			resultsWriter.write("     Average: "+avgVotedErrors);	
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			
			resultsWriter.write("     AVERAGE MODELS TEST ERRORS:");
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			float avgAvgErrors = 0;
			for(int modelIndex = 0; modelIndex < numberOfModels; modelIndex++) {
				resultsWriter.write("     Model " + modelIndex + ": " + testThreads[modelIndex].GetAvgErrors());
				avgAvgErrors += testThreads[modelIndex].GetAvgErrors();
				resultsWriter.flush();
			}
			resultsWriter.write( System.getProperty( "line.separator" ));
			avgAvgErrors = avgAvgErrors / numberOfModels;
			resultsWriter.write("     Average: "+avgAvgErrors);	
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.write( System.getProperty( "line.separator" ));
			resultsWriter.flush();
			
			float epoch = ((float)numberOfTrainedEntries) / 60000;
			graphic.AddLastErrorPoint(epoch, avgLastErrors);
			graphic.AddVotedErrorPoint(epoch, avgVotedErrors);
			graphic.AddAvgErrorPoint(epoch, avgAvgErrors);
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	
	
	static void PrintModel(Model m) {
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

//---------------------------------------------------------------------------------------
/*
	static void PerceptronTrainModelsListOverEntries(DatasetsManager datasetManager, ModelList[] modelsList, int modelIndex, int numberOfEntries) {
		Entry entry = new Entry(datasetManager.GetAttributesNumber());
		Model currentModel = modelsList[modelIndex].GetLastModel();
		
		for(int i = 0; i < numberOfEntries; i++) {
			datasetManager.ParseInputDataset(modelIndex, entry);
			if(entry.GetClass() * currentModel.ModelPrediction(entry) == -1) {
				currentModel.ModelUpdateOverEntry(entry);
				Model newModel = new Model(currentModel);
				modelsList[modelIndex].AddModel(newModel);
				currentModel = newModel;
			}else {
				currentModel.IncreaseModelClassificationSuccesses();
			}
			//System.out.println("Training progress: " + (int)( i / (float) numberOfEntries * 100));
		}	
	}

	static void TestModelsList(DatasetsManager datasetsManager, ModelList[] modelsLists, int modelIndex, int[] lastResults, int[] votedResults) {
		int lastErrors = 0;
		int votedErrors = 0;
		
		Model lastModel = modelsLists[modelIndex].GetLastModel();
		ModelList votedModel = modelsLists[modelIndex];
		
		Entry entry = new Entry(datasetsManager.GetAttributesNumber());
		
		
		for(int i = 0; i < datasetsManager.GetTestsetSize(); i++) {
			
			datasetsManager.ParseInputTestset(modelIndex, entry);
			
			//last 
			if(entry.GetClass() * lastModel.ModelPrediction(entry) == -1) {
				lastErrors++;
			}
			
			//voted
			if(entry.GetClass()* votedModel.ModelPrediction(entry) == -1) {
				votedErrors++;
			}
		}
		
		lastResults[modelIndex] = lastErrors;
		votedResults[modelIndex] = votedErrors;
	}

*/
