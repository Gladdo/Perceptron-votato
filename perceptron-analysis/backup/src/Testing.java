
public class Testing extends Thread{
	DatasetsManager datasetsManager;
	ModelList[] modelsLists;
	int modelIndex;
	int lastErrors;
	int votedErrors;
	int avgErrors;
	Graphic graphic;
	
	public Testing(DatasetsManager datasetsManager, ModelList[] modelsLists, int modelIndex, Graphic g) {
		this.datasetsManager = datasetsManager;
		this.modelsLists = modelsLists;
		this.modelIndex = modelIndex;
		this.lastErrors = 0;
		this.votedErrors = 0;
		this.avgErrors = 0;
		this.graphic = g;
	}
	
	public void run() {
		
		Model lastModel = modelsLists[modelIndex].GetLastModel();
		ModelList votedModel = modelsLists[modelIndex];
		
		Entry entry = new Entry(datasetsManager.GetAttributesNumber());
		
		for(int i = 0; i < datasetsManager.GetTestsetSize(); i++) {
			
			datasetsManager.ParseInputTestset(i, entry);
			
			//last 
			if(entry.GetClass() * lastModel.ModelPrediction(entry) == -1) {
				lastErrors++;
			}
			
			votedModel.ExecuteModelPrediction(entry);
			
			//voted
			if(entry.GetClass()* votedModel.GetVotedPrediction() == -1) {
				votedErrors++;
			}
			
			//avg
			if(entry.GetClass()* votedModel.GetAvgPrediction() == -1) {
				avgErrors++;
			}
			
			graphic.SetProgressBar(modelIndex, (int) (((float) i ) / datasetsManager.GetTestsetSize()* 100) );
		}
	}
	
	public int GetLastErrors() {
		return lastErrors;
	}
	
	public int GetVotedErrors() {
		return votedErrors;
	}
	
	public int GetAvgErrors() {
		return avgErrors;
	}
}
