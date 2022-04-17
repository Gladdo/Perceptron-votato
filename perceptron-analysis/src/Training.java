
public class Training extends Thread {
	Model[] models;
	int modelIndex;
	int numberOfEntries;
	
	public Training(Model[] models, int modelIndex, int numberOfEntries) {
		this.models = models;
		this.modelIndex = modelIndex;
		this.numberOfEntries = numberOfEntries;
	}
	
	//Questo metodo implementa i passi per l'addestramento del modello di indice modelIndex:
	public void run() {
		Entry entry = new Entry(Main.ATTRIBUTES);
		LinearPlane currentPlane = models[modelIndex].GetLastPlane();
		
		for(int i = 0; i < numberOfEntries; i++) {
			DatasetsManager.instance().ParseNextTrainEntry(modelIndex, entry);
			if(entry.GetClass() * currentPlane.PlanePrediction(entry) == -1) {
				LinearPlane newPlane = new LinearPlane(currentPlane);
				models[modelIndex].AddPlane(newPlane);
				currentPlane = newPlane;
				currentPlane.UpdatePlaneParametersOverEntry(entry);
			}else {
				currentPlane.IncreasePlaneClassificationSuccesses();
			}
			Graphic.instance().SetProgressBar(modelIndex, (int) (((float) i ) / numberOfEntries * 100) );
		}	
	}
}
