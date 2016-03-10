package team3.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import javax.swing.JPanel;

import team3.codefile.MapColoring;

public class GraphPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Color[] COLORS = new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA};
	
	private MapColoring<String> graph;
	
	public GraphPanel(MapColoring<String> graph) {
		this.graph = graph;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		// Half of radius
		int r2 = 25;
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		
		Map<String, Vec2> placements = getNodePlacements();
		
		Iterator<Entry<String, Vec2>> itr = placements.entrySet().iterator();
		
		while (itr.hasNext()) {			// Pass 1 - draw edges
			Entry<String, Vec2> entry = itr.next();

			int x1 = (int)entry.getValue().x;
			int y1 = (int)entry.getValue().y;
			
			List<String> neighbors = graph.getNeighborsOfData(entry.getKey());
			for (String neighbor : neighbors) {
				int x2 = (int)placements.get(neighbor).x;
				int y2 = (int)placements.get(neighbor).y;
				g.setColor(Color.BLACK);
				g.drawLine(x1, y1, x2, y2);
			}
		}
		
		Map<String, Integer> nodeColors = graph.getColors();
		
		itr = placements.entrySet().iterator();
		while (itr.hasNext()) {			// Pass 2 - draw circles
			Entry<String, Vec2> entry = itr.next();

			int x = (int)entry.getValue().x;
			int y = (int)entry.getValue().y;
			g.setColor(COLORS[nodeColors.get(entry.getKey())]);
			g.fillOval(x-r2, y-r2, 2*r2, 2*r2);
		}
	}
	
	private Map<String, Vec2> getNodePlacements() {
		
		Map<String, Vec2> placements = new HashMap<>();
		
		// Assign random positions between (0,0) and (1,1)
		Iterator<String> itr = graph.dataIterator();
		while (itr.hasNext())
			placements.put(itr.next(), new Vec2(Math.random()*getWidth(), Math.random()*getHeight()));
		
		return placements;
	}
}

class Vec2 {
	double x, y;
	
	Vec2(double a, double b) {
		x = a; y = b;
	}
}