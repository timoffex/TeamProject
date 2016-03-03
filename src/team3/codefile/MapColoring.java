package team3.codefile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MapColoring<E> extends Graph<E> {
	private Map<Vertex<E>, Integer> vertexColors = new HashMap<>();
	
	/**
	 * The amount of "colors" used so far. The "colors" are
	 * numbered 0 - (minColors-1). A "color" is any property
	 * that must be different for any two adjacent vertices. 
	 * <br/><br/>
	 * For example, if vertices represent classes, then
	 * a "color" could represent a time slot, and the goal
	 * could be to assign all "adjacent" classes different time
	 * slots.
	 */
	private int minColors = 0;
	
	public int getMinimumNumberOfColors() {
		minColors = 1;
		vertexColors.clear();
		unvisitVertices();
		
		// Perform a breadth-first traversal of the graph,
		// assigning a color to each vertex.
		
		LinkedQueue<Vertex<E>> toVisit = new LinkedQueue<>();
		
		Vertex<E> unvisited = getUnvisitedVertex();
		
		// Repeat until all vertices are visited. Outer loop
		// makes sure that separate connected components are
		// visited too.
		while ((unvisited = getUnvisitedVertex()) != null) {
			toVisit.enqueue(unvisited);
			
			while (!toVisit.isEmpty()) {
				Vertex<E> next = toVisit.dequeue();
				
				next.visit();
				assignColor(next);
				
				
				// Enqueue all unvisited neighbors.
				List<Vertex<E>> neighbors = getNeighbors(next);
				for (Vertex<E> neighbor : neighbors)
					if (!neighbor.isVisited())
						toVisit.enqueue(neighbor);
			}
		}
		
		
		return minColors;
	}
	
	
	/**
	 * Assigns a color to vertex such that it has a different
	 * color from each neighbor on its adjacency list.
	 */
	private void assignColor(Vertex<E> v) {
		List<Vertex<E>> neighbors = getNeighbors(v);
		
		
		// 1) Check if any existing colors are available.
		boolean[] takenColors = new boolean[minColors];
		
		for (Vertex<E> neighbor : neighbors) {
			Integer color = vertexColors.get(neighbor); 
			
			if (color != null)
				takenColors[color] = true;
		}
		
		// Try to assign an available color.
		for (int i = 0; i < minColors; i++) {
			if (!takenColors[i]) {
				vertexColors.put(v, i);
				return;
			}
		}
		
		// 2) No existing color available. Create new color and assign it.
		vertexColors.put(v, minColors++);
	}
	
	/**
	 * Returns the neighbors of the vertex.
	 */
	private List<Vertex<E>> getNeighbors(Vertex<E> v) {
		Iterator<Pair<Vertex<E>, Double>> itr = v.adjList.values().iterator();
		
		List<Vertex<E>> neighbors = new ArrayList<>();
		
		while (itr.hasNext())
			neighbors.add(itr.next().first);
		
		return neighbors;
	}
	
	/**
	 * @return An unvisited vertex or null if all vertices are visited.
	 */
	private Vertex<E> getUnvisitedVertex() {
		Iterator<Vertex<E>> itr = vertexColors.keySet().iterator();
		
		while (itr.hasNext()) {
			Vertex<E> v = itr.next();
			
			if (!v.isVisited())
				return v;
		}
		
		return null;
	}
	
	
}
