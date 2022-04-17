
public class Training extends Thread {
	DatasetsManager datasetManager;
	ModelList[] modelsList;
	int modelIndex;
	int numberOfEntries;
	Graphic graphic;
	
	public Training(DatasetsManager datasetManager, ModelList[] modelsList, int modelIndex, int numberOfEntries, Graphic g) {
		this.datasetManager = datasetManager;
		this.modelsList = modelsList;
		this.modelIndex = modelIndex;
		this.numberOfEntries = numberOfEntries;
		this.graphic = g;
	}
	
	public void run() {
		Entry entry = new Entry(datasetManager.GetAttributesNumber());
		Model currentModel = modelsList[modelIndex].GetLastModel();
		
		for(int i = 0; i < numberOfEntries; i++) {
			datasetManager.ParseNextTrainEntry(modelIndex, entry);
			if(entry.GetClass() * currentModel.ModelPrediction(entry) == -1) {
				currentModel.ModelUpdateOverEntry(entry);
				Model newModel = new Model(currentModel);
				modelsList[modelIndex].AddModel(newModel);
				currentModel = newModel;
			}else {
				currentModel.IncreaseModelClassificationSuccesses();
			}
			graphic.SetProgressBar(modelIndex, (int) (((float) i ) / numberOfEntries * 100) );
		}	
	}
}
