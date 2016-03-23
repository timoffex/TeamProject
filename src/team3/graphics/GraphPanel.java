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
import java.util.Map.Entry;

import javax.swing.JPanel;

import team3.codefile.MapColoring;

public class GraphPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/** The graph this panel should render. */
	private MapColoring<String> graph;
	
	/** A map from data in the graph to positions on the screen. */
	private Map<String, Vec2> placements;
	
	
	/** A mapping from data to whether the data should be highlighted. */
	private Map<String, Color> highlights;
	private static final Color HIGHLIGHT_OLD = Color.GRAY;
	private static final Color HIGHLIGHT_NEW = Color.BLACK;
	private String lastHighlighted;
	
	
	/** A thread used to animate in recomputeNodePlacements() */
	private Thread recomputeThread;
	
	/** Used in recomputeNodePlacements(). */
	private Map<String, Vec2> velocities = new HashMap<>();
	
	public GraphPanel(MapColoring<String> graph) {
		this.graph = graph;
		
		placements = new HashMap<>();
		highlights = new HashMap<>();
		lastHighlighted = null;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		// Half of radius
		int r2 = 25;
		
		// Half of highlight radius
		int r2h = 28;
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		Iterator<Entry<String, Vec2>> itr = placements.entrySet().iterator();
		
		while (itr.hasNext()) {			// Pass 1 - draw edges
			Entry<String, Vec2> entry = itr.next();

			int x1 = (int)entry.getValue().x;
			int y1 = (int)entry.getValue().y;
			
			List<String> neighbors = graph.getNeighborsOfData(entry.getKey());
			
			if (neighbors != null)
				for (String neighbor : neighbors) {
					int x2 = (int)placements.get(neighbor).x;
					int y2 = (int)placements.get(neighbor).y;
					g.setColor(Color.BLACK);
					g.drawLine(x1, y1, x2, y2);
				}
		}
		
		Map<String, Integer> nodeColors = graph.getColors();
	
		itr = placements.entrySet().iterator();
		while (itr.hasNext()) {			// Pass 2 - draw circles and highlights
			Entry<String, Vec2> entry = itr.next();

			int x = (int)entry.getValue().x;
			int y = (int)entry.getValue().y;
			
			Color highlight = highlights.get(entry.getKey());
			
			Color color = highlight==null ? getColor(nodeColors.get(entry.getKey())) : Color.WHITE;
				
			// Node highlight
			if (highlight != null) {
				g.setColor(highlight);
				g.fillOval(x-r2h, y-r2h, 2*r2h, 2*r2h);
			}
			
			// Node circle
			g.setColor(color);
			g.fillOval(x-r2, y-r2, 2*r2, 2*r2);
			
			g.setColor(Color.BLACK);
			drawCenteredString(g, entry.getKey(), new Rectangle(x-r2, y, 2*r2, 2*r2));
		}
	}
	
	/**
	 * Maps color indices (in range [0,minColors-1]) to distinct colors.
	 * @param index - Index of the color (values of the entries in graph.getColors())
	 * @return A color that should look visually distinct from the other colors. If
	 * minColors is too high, the method will still work, but the user should use
	 * the "solve" command to receive solutions.
	 */
	private Color getColor(Integer index) {
		if (index == null)
			return Color.WHITE;
		
		int minColors = graph.getMinimumNumberOfColors();
		
		float hue = (float)index/minColors;  // range: [0,1)
		
		return Color.getHSBColor(hue, 0.6f, 0.6f);
	}
	
	
	
	private void drawCenteredString(Graphics g, String text, Rectangle rect) {
		Font font = Font.decode("Georgia-18");
		
	    FontMetrics metrics = g.getFontMetrics(font); // Get the FontMetrics
	   
	    int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) - metrics.getAscent();
	    
	    
	    g.setFont(font);
	    g.drawString(text, x, y);
	}
	
	
	/**
	 * Computes a position for each node in the graph such that nodes
	 * are fairly well spaced-out. Then, calls repaint().
	 */
	public void recomputeNodePlacements() {
		
		if (recomputeThread != null && recomputeThread.isAlive())
			recomputeThread.interrupt();
		

		// This section needs to be outside of the recompute thread because we need
		// placements to be guaranteed to contain a key for every node in the graph
		// by the time this method exits.
		Map<String, Vec2> newPlacements = new HashMap<>();
		
		// Assign random positions between (0,0) and (w,h) to new nodes
		Iterator<String> itr = graph.dataIterator();
		while (itr.hasNext()) {
			String key = itr.next();
			if (!placements.containsKey(key))
				newPlacements.put(key, new Vec2(Math.random()*getWidth(), Math.random()*getHeight()));
			else
				newPlacements.put(key, placements.get(key));
		}
		
		placements = newPlacements;

		recomputeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// Use a force-based algorithm to space out the graph
				
				List<String> allData = new ArrayList<>();
				allData.addAll(placements.keySet());

				Map<String, Vec2> forces = new HashMap<>();
				for (int i = 0; i < 10000; i++) {
					for (String data : allData) forces.put(data, new Vec2(0,0));
					
					Vec2 screenCenter = new Vec2(getWidth()/2, getHeight()/2);
					
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
							toOther.multiply(1./distance);
							
							double targetDistance;
							
							if (graph.areNodesConnected(data, other))
								targetDistance = 100;
							else
								targetDistance = 300;
							
							toOther.multiply(Math.log(distance/targetDistance));
							
							force.add(toOther);
							forces.get(other).subtract(toOther);
						}
						
						Vec2 toCenter = Vec2.between(posData, screenCenter);
						toCenter.multiply(0.005);
						force.add(toCenter);
						
						forces.put(data, force);
						
						Vec2 vel = velocities.get(data);
						if (vel == null)
							vel = new Vec2(0, 0);
						
						vel.multiply(0.99);
						vel.add(new Vec2(force.x*0.02, force.y*0.02));
						velocities.put(data, vel);
					}
					
					// Move nodes
					for (String data : allData)
						placements.get(data).add(velocities.get(data));
					
					repaint();
					
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// we got interrupted-just stop the method!
						return;
					}
				}
			}
		});
		
		recomputeThread.start();
		
		
		
//		// Renormalize positions
//		Vec2 max = new Vec2(-Double.MAX_VALUE,-Double.MAX_VALUE);
//		Vec2 min = new Vec2(Double.MAX_VALUE,Double.MAX_VALUE);
//		
//		for (String data : allData) {
//			Vec2 pos = placements.get(data);
//			if (pos.x > max.x) max.x = pos.x;
//			if (pos.y > max.y) max.y = pos.y;
//			if (pos.x < min.x) min.x = pos.x;
//			if (pos.y < min.y) min.y = pos.y;
//		}
//		
//		
//		Vec2 dif = new Vec2(max.x,max.y);
//		dif.subtract(min);
//		
//		for (String data : allData) {
//			Vec2 pos = placements.get(data);
//			pos.x = (pos.x-min.x)*(getWidth()-100)/dif.x + 50;
//			pos.y = (pos.y-min.y)*(getHeight()-100)/dif.y + 50;
//		}
//		
		
		
		repaint();
	}
	
	
	public void clearHighlights() {
		lastHighlighted = null;
		
		Iterator<String> itr = highlights.keySet().iterator();
		while (itr.hasNext()) highlights.put(itr.next(), null);
		
		repaint();
	}
	
	public void highlight(String data) {
		highlights.put(lastHighlighted, HIGHLIGHT_OLD);
		highlights.put(data, HIGHLIGHT_NEW);
		lastHighlighted = data;
		
		repaint();
	}
}

/**
 * Helper class used only in GraphPanel.java
 * @author timoffex
 *
 */
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