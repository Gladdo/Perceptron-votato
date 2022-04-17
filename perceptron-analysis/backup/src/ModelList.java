import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ModelList {
	//number of parameters for model w
	private int attributes;
	
	private List<Model> modelList;
	private Model lastModel;
	
	int votedPrediction;
	int avgPrediction;
	
	public ModelList(int a) {
		attributes = a;
		modelList = new LinkedList<Model>();
		modelList.add(new Model(attributes));
		lastModel = modelList.get(modelList.size() - 1);
	}
	
	//entry already parsed	
	public void AddModel(Model m) {
		modelList.add(m);
		lastModel = m;
	}
	
	public Model GetLastModel() {
		return lastModel;
	}
	
	public void ExecuteModelPrediction(Entry entry) {
		float avgResult = 0;
		float votedResult = 0;
		for(Iterator<Model> m = modelList.iterator(); m.hasNext();) {
			Model currentModel = m.next();
			float currentModelPredictionValue = currentModel.ModelPredictionValue(entry);
			int currentModelSuccesses = currentModel.GetModelClassificationSuccesses();
			votedResult = votedResult + currentModelSuccesses * Sign(currentModelPredictionValue);
			avgResult = avgResult + currentModelSuccesses * currentModelPredictionValue;
		}
		votedPrediction = Sign(votedResult);
		avgPrediction = Sign(avgResult);
	}
	
	public int GetVotedPrediction() {
		return votedPrediction;
	}
	public int GetAvgPrediction() {
		return avgPrediction;
	}
	
	int Sign(float value) {
		if(value < 0) {
			return -1;
		}else {
			return 1;
		}
	}
	
	public void PrintModelsList() {
		for(Iterator<Model> m = modelList.iterator(); m.hasNext();) {
			Model currentModel = m.next();
			PrintModel(currentModel);
		}
	}
	
	void PrintModel(Model m) {
		for(int i = 0; i < m.GetAttributes(); i++) {
			System.out.print(m.GetWValue(i) + "," );
		}
		System.out.print( "b = " + m.GetBValue());
		System.out.println("");
	}
	
	public int ListLength() {
		return modelList.size();
	}
}
