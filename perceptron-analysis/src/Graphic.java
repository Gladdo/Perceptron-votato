import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class Graphic {
	JFrame frame;
	LineChart chart;
	JPanel chartPanel;
	JPanel progressBarsPanel;
	JLabel progressBarsStatus;
	JProgressBar[] progressBars;

	private static Graphic instance;
	
	private Graphic() {
		init();
	}
	
	public static Graphic instance() {
		if(instance==null) {
			instance = new Graphic();
		}
		return instance;
	}
	
	public void init() {
		frame = new JFrame("Risultati");
		frame.setLayout(new FlowLayout());
	
		chart = new LineChart("" ,
		         "");
		chartPanel = new JPanel();
		chartPanel.add(chart.GetChartPanel());
		progressBarsPanel = new JPanel();
		progressBarsPanel.setLayout(new GridLayout(11,1));
		progressBarsStatus = new JLabel("Status: ");
		progressBarsPanel.add(progressBarsStatus);
		
		progressBars = new JProgressBar[Main.NUMBER_OF_MODELS];
		for(int i = 0 ; i < progressBars.length; i++) {
			 progressBars[i] = new JProgressBar();
			 progressBars[i].setMaximum(100);
			 progressBarsPanel.add(progressBars[i]);
		}
		
		frame.getContentPane().add(chartPanel);
		frame.getContentPane().add(progressBarsPanel);
		frame.pack();
		frame.setVisible(true);
	}
	
	public void SetStatus(String s) {
		progressBarsStatus.setText(s);
	}
	
	public void AddLastErrorPoint(float x, float y) {
		chart.AddLastErrorPoint(x, y);
	}
	
	public void AddVotedErrorPoint(float x, float y) {
		chart.AddVotedErrorPoint(x, y);
	}
	
	public void AddAvgErrorPoint(float x, float y) {
		chart.AddAvgErrorPoint(x, y);
	}
	
	public void SetProgressBar(int modelIndex, int progress) {
		progressBars[modelIndex].setValue(progress);
	}
}
