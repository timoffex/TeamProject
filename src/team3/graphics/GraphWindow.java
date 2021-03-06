package team3.graphics;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import team3.codefile.MapColoring;

public class GraphWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private GraphPanel panel;

	public GraphWindow(MapColoring<String> graph) {
		add(panel = new GraphPanel(graph));
		
		center();
		
		setVisible(true);
	}
	
	private void center() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		int width = 800;
		int height = 600;
		int x = screenSize.width/2 - width/2;
		int y = screenSize.height/2 - height/2;
		
		setBounds(x+200, y, width, height);
	}
	
	
	public void recomputeNodePlacements() {
		panel.recomputeNodePlacements();
	}
	
	
	public void clearHighlights() {
		panel.clearHighlights();
	}
	
	public void highlight(String data) {
		panel.highlight(data);
	}
}
