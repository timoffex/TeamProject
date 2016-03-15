package team3.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JPanel;

import team3.codefile.MapColoring;

public class GraphPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private static final Color[] COLORS = new Color[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN, Color.MAGENTA};
	
	private MapColoring<String> graph;
	private Map<String, Vec2> placements;
	
	public GraphPanel(MapColoring<String> graph) {
		this.graph = graph;
		
		placements = new HashMap<>();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		// Half of radius
		int r2 = 25;
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
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
			
			g.setColor(Color.BLACK);
			drawCenteredString(g, entry.getKey(), new Rectangle(x-r2, y, 2*r2, 2*r2));
		}
	}
	
	private void drawCenteredString(Graphics g, String text, Rectangle rect) {
		Font font = Font.decode("Georgia-18");
		
	    FontMetrics metrics = g.getFontMetrics(font); // Get the FontMetrics
	   
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) - metrics.getAscent();
	    
	    
	    g.setFont(font);
	    g.drawString(text, x, y);
	}
	
	public void recomputeNodePlacements() {
		placements = new HashMap<>();
		
		// Assign random positions between (0,0) and (w,h)
		Iterator<String> itr = graph.dataIterator();
		while (itr.hasNext())
			placements.put(itr.next(), new Vec2(Math.random()*getWidth(), Math.random()*getHeight()));
		
		// Use a rough, force-based algorithm to space out the graph
		List<String> allData = new ArrayList<>();
		allData.addAll(placements.keySet());
		
		Map<String, Vec2> forces = new HashMap<>();
		for (String data : allData) forces.put(data, new Vec2(0,0));
		for (int i = 0; i < 100; i++) {
			
			// Compute forces
			for (int j = 0; j < allData.size(); j++) {
				String data = allData.get(j);
				Vec2 posData = placements.get(data);
				Vec2 force = forces.get(data);
				
				for (int k = j+1; k < allData.size(); k++) {
					String other = allData.get(k);
					Vec2 posOther = placements.get(other);
					
					Vec2 toOther = Vec2.between(posData, posOther);
					
					double distance = toOther.length();
					toOther.multiply(0.001/distance);
					
					if (graph.areNodesConnected(data, other)) {
						if (distance < 100)
							toOther.multiply(-100/distance);
						
						force.add(toOther);
						forces.get(other).subtract(toOther);
					} else {
						force.subtract(toOther);
						forces.get(other).add(toOther);
					}
				}
				
				forces.put(data, force);
			}
			
			// Move nodes
			for (String data : allData)
				placements.get(data).add(forces.get(data));
		}
		
		// Renormalize positions
		Vec2 max = new Vec2(-Double.MAX_VALUE,-Double.MAX_VALUE);
		Vec2 min = new Vec2(Double.MAX_VALUE,Double.MAX_VALUE);
		
		for (String data : allData) {
			Vec2 pos = placements.get(data);
			if (pos.x > max.x) max.x = pos.x;
			if (pos.y > max.y) max.y = pos.y;
			if (pos.x < min.x) min.x = pos.x;
			if (pos.y < min.y) min.y = pos.y;
		}
		
		
		Vec2 dif = new Vec2(max.x,max.y);
		dif.subtract(min);
		
		for (String data : allData) {
			Vec2 pos = placements.get(data);
			pos.x = (pos.x-min.x)*(getWidth()-100)/dif.x + 50;
			pos.y = (pos.y-min.y)*(getHeight()-100)/dif.y + 50;
		}
		
		
		
		repaint();
	}
}

class Vec2 {
	double x, y;
	
	Vec2(double a, double b) {
		x = a; y = b;
	}
	
	void add(Vec2 other) {
		x += other.x;
		y += other.y;
	}
	
	void subtract(Vec2 other) {
		x -= other.x;
		y -= other.y;
	}
	
	void multiply(double d) {
		x *= d;
		y *= d;
	}
	
	double length() {
		return Math.sqrt(x*x + y*y);
	}
	
	static Vec2 between(Vec2 v1, Vec2 v2) {
		Vec2 n = new Vec2(v2.x, v2.y);
		n.subtract(v1);
		return n;
	}
}