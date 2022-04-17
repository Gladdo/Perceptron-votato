
public class Testing extends Thread{
	Model[] models;
	int modelIndex;
	int lastErrors;
	int votedErrors;
	int avgErrors;
	
	public Testing(Model[] models, int modelIndex) {
		this.models = models;
		this.modelIndex = modelIndex;
		this.lastErrors = 0;
		this.votedErrors = 0;
		this.avgErrors = 0;
	}
	
	public void run() {
		
		LinearPlane lastModel = models[modelIndex].GetLastPlane();
		Model votedModel = models[modelIndex];
		
		Entry entry = new Entry(Main.ATTRIBUTES);
		
		for(int i = 0; i < Main.TEST_SET_SIZE; i++) {
			
			DatasetsManager.instance().ParseInputTestset(i, entry);
			
			//last 
			if(entry.GetClass() * lastModel.PlanePrediction(entry) == -1) {
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
			
			Graphic.instance().SetProgressBar(modelIndex, (int) (((float) i ) / Main.TEST_SET_SIZE * 100) );
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
