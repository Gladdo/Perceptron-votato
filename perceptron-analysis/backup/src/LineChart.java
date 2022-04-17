import org.jfree.chart.ChartPanel;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChart extends ApplicationFrame {
	
	XYSeries lastValues;
	XYSeries votedValues;
	XYSeries avgValues;
	XYSeriesCollection dataset;
	
	JFreeChart lineChart;
	ChartPanel chartPanel;

   public LineChart( String applicationTitle , String chartTitle ) {
      super(applicationTitle);
      
      lastValues = new XYSeries  ("lastValues");
      votedValues = new XYSeries  ("votedValues");
      avgValues = new XYSeries ("avgValues");
      dataset = new XYSeriesCollection();
      dataset.addSeries(lastValues); 
      dataset.addSeries(votedValues);
      dataset.addSeries(avgValues);
           
      lineChart = ChartFactory.createXYLineChart(
         chartTitle,
         "Epoch","Errors",
         dataset,
         PlotOrientation.VERTICAL,
         true,true,false);
         
      chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 512 , 316 ) );
      final XYPlot plot = lineChart.getXYPlot( );
      
      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
      renderer.setSeriesPaint( 0 , Color.RED );
      renderer.setSeriesPaint( 1 , Color.GREEN );
      renderer.setSeriesPaint( 2,  Color.YELLOW );
      plot.setRenderer( renderer );
      
      plot.setDomainAxis(new LogarithmicAxis("X"));
      
      //setContentPane( chartPanel ); 
   }
   
   public void AddLastErrorPoint(float x, float y) {
	   lastValues.add(x,y);
   }
   public void AddVotedErrorPoint(float x, float y) {
	   votedValues.add(x,y);
   }
   public void AddAvgErrorPoint(float x, float y) {
	   avgValues.add(x,y);
   }
   
   public ChartPanel GetChartPanel() {
	   return chartPanel;
   }
}