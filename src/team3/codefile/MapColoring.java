package team3.codefile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;



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
	
	private void solveProblem() {
		minColors = 1;
		vertexColors.clear();
		unvisitVertices();
		
		// Perform a breadth-first traversal of the graph,
		// assigning a color to each vertex.
		
		LinkedQueue<Vertex<E>> toVisit = new LinkedQueue<>();
		
		Vertex<E> unvisited;
		
		// Repeat until all vertices are visited. Outer loop
		// makes sure that separate connected components are
		// visited too.
		while ((unvisited = getUnvisitedVertex()) != null) {
			toVisit.enqueue(unvisited);
			
			while (!toVisit.isEmpty()) {
				Vertex<E> next = toVisit.dequeue();
				
				if (next.isVisited())
					continue;
				
				next.visit();
				assignColor(next);
				
				
				// Enqueue all unvisited neighbors.
				List<Vertex<E>> neighbors = getNeighbors(next);
				for (Vertex<E> neighbor : neighbors)
					if (!neighbor.isVisited())
						toVisit.enqueue(neighbor);
			}
		}
	}
	
	public int getMinimumNumberOfColors() {
		solveProblem();
		return minColors;
	}
	
	public Map<E, Integer> getColors() {
		solveProblem();
		
		Map<E, Integer> colors = new HashMap<>();
		
		Iterator<Entry<Vertex<E>, Integer>> itr = vertexColors.entrySet().iterator();
		
		while (itr.hasNext()) {
			Entry<Vertex<E>, Integer> entry = itr.next();
			colors.put(entry.getKey().getData(), entry.getValue());
		}
		
		return colors;
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
		Iterator<Vertex<E>> itr = vertexSet.values().iterator();
		
		while (itr.hasNext()) {
			Vertex<E> v = itr.next();
			
			if (!v.isVisited())
				return v;
		}
		
		return null;
	}
	
	
	
	
	
	
	public List<E> getNeighborsOfData(E data) {
		Vertex<E> vertex = vertexSet.get(data);
		if (vertex == null) return null;
		
		List<Vertex<E>> neighbors = getNeighbors(vertex);
		
		List<E> dataNeighbors = new ArrayList<>();
		
		for (Vertex<E> v : neighbors)
			dataNeighbors.add(v.data);
		
		return dataNeighbors;
		
	}
	
	/**
	 * Removes the vertex that contains this data
	 * and removes all edges that connect to this vertex.
	 * @param data
	 */
	public boolean removeVertex(E data) {
		boolean found = false;
		
		Iterator<Vertex<E>> itr = vertexSet.values().iterator();
		
		while (itr.hasNext()) {
			Vertex<E> v = itr.next();
			v.adjList.remove(data);
			
			// If the vertex IS the one we're looking for, get rid of it.
			if (v.getData().equals(data)) {
				itr.remove();
				found = true;
			}
		}
		
		return found;
	}
	
	/**
	 * A helper method used to neatly save a graph to a file.
	 * @param data
	 * @return
	 */
	public List<E> getUnvisitedNeighborsOfData(E data) {
		Vertex<E> vertex = vertexSet.get(data);
		if (vertex == null) return null;
		
		List<Vertex<E>> neighbors = getNeighbors(vertex);
		
		List<E> dataNeighbors = new ArrayList<>();
		
		for (Vertex<E> v : neighbors)
			if (!v.isVisited())
				dataNeighbors.add(v.data);
		
		return dataNeighbors;
		
	}
	
	public boolean areNodesConnected(E d1, E d2) {
		return vertexSet.get(d1).adjList.containsKey(d2);
	}
	
	public Vertex<E> getAnyVertex() {
		Iterator<Vertex<E>> itr = vertexSet.values().iterator();
		return itr.next();
	}
	
	public E getAnyData() {
		Iterator<Vertex<E>> itr = vertexSet.values().iterator();
		return itr.next().getData();
	}
	
	public Iterator<E> dataIterator() {
		return new DataIterator(vertexSet.values().iterator());
	}
	
	private class DataIterator implements Iterator<E> {
		private Iterator<Vertex<E>> vItr;
		
		public DataIterator(Iterator<Vertex<E>> itr) {
			vItr = itr;
		}
		
		@Override
		public boolean hasNext() {
			return vItr.hasNext();
		}

		@Override
		public E next() {
			return vItr.next().getData();
		}

		@Override
		public void remove() {
			throw new Error("Unimplemented method!");
		}

		@Override
		public void forEachRemaining(Consumer<? super E> action) {
			throw new Error("Unimplemented method!");
		}
	}
}
