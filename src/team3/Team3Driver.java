package team3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import team3.UserInterface.UserOption;
import team3.codefile.MapColoring;
import team3.codefile.Pair;
import team3.codefile.Visitor;
import team3.graphics.ConsoleWindow;
import team3.graphics.GraphWindow;

public class Team3Driver {
	private static MapColoring<String> graph;
	private static UserInterface ui;
	
	private static ConsoleWindow console;
	private static GraphWindow display;
	
	public static void main(String[] args) {
		ui = new UserInterface();
		graph = new MapColoring<>();
		
		console = new ConsoleWindow();
		display = new GraphWindow(graph);
		
		ui.displayHello();
		ui.displayHelp();
		
		Pair<UserOption, String> option = ui.getUserOption();
		while (option.first != UserOption.QUIT) {
			performAction(option);
			
			option = ui.getUserOption();
		}
	}
	
	
	private static void performAction(Pair<UserOption, String> action) {
		switch (action.first) {
		case ADD_EDGE: addEdge(action.second); break;
		case REMOVE_EDGE: removeEdge(action.second); break;
		case DISPLAY_GRAPH: displayGraph(action.second); break;
		case LOAD_GRAPH_FROM_FILE: loadGraph(action.second); break;
		case DISPLAY_SOLUTION: displaySolution(action.second); break;
		
		// If we're here, there is a command that wasn't implemented yet.
		default:
			System.out.println("UNIMPLEMENTED COMMAND");
			break;
		}
	}
	
	
	private static void addEdge(String args) {
		String[] vertices = args.split(" ");
		
		if (vertices.length != 2)
			System.out.println("Wrong arguments.");
		else {
			graph.addEdge(vertices[0], vertices[1], 1);
			graph.addEdge(vertices[1], vertices[0], 1);
			display.repaint();
		}
	}
	
	private static void removeEdge(String args) {
		String[] vertices = args.split(" ");
		
		if (vertices.length != 2)
			System.out.println("Wrong arguments.");
		else {
			graph.remove(vertices[0], vertices[1]);
			graph.remove(vertices[1], vertices[0]);
			display.repaint();
		}
	}
	
	private static void loadGraph(String options) {
		graph.clear();
		
		try {
			Scanner in = new Scanner(new File(options));
			
			while (in.hasNextLine()) {
				String line = in.nextLine().trim();
				
				if (line.isEmpty())
					break;
				
				String[] vertices = line.split("\\s");
				
				if (vertices.length != 2) {
					System.out.println("Syntax error on line: " + line);
				}
				
				graph.addEdge(vertices[0], vertices[1], 1);
			}

			display.repaint();
			System.out.println("Loaded the graph!");
			
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file!");
		}
	}
	
	private static void displayGraph(String options) {
		String startNode;
		boolean depthFirst;
		
//		if (options == null || options.isEmpty()) {
		if (true) { // temporary
			String depthString = ui.ask("Depth first or breadth first? (d/b): ");
			
			do {
				if (depthString.equalsIgnoreCase("d") || depthString.equalsIgnoreCase("depth")) {
					depthFirst = true;
					break;
				} else if (depthString.equalsIgnoreCase("b") || depthString.equalsIgnoreCase("breadth")) {
					depthFirst = false;
					break;
				}
			} while (true);
			
			
			startNode = ui.ask("Enter start node: ");
		}
		
		Visitor<String> visitor = new Visitor<String>() {
			public void visit(String s) {
				System.out.println(s);
			}
		};
		
		if (depthFirst)
			graph.depthFirstTraversal(startNode, visitor);
		else
			graph.breadthFirstTraversal(startNode, visitor);
	}

	private static void displaySolution(String options) {
		displayMinColors();
		displayColorAssignments();
	}
	
	private static void displayMinColors() {
		int minColors = graph.getMinimumNumberOfColors();
		System.out.println("Minimum colors needed = " + minColors);
	}
	
	private static void displayColorAssignments() {
		Map<String, Integer> colors = graph.getColors();
		int minColors = graph.getMinimumNumberOfColors();
		
		Iterator<Entry<String, Integer>> itr = colors.entrySet().iterator();
		
		System.out.println("Color assignments (1-" + minColors + "): ");
		while (itr.hasNext()) {
			Entry<String, Integer> entry = itr.next();
			System.out.println(entry.getKey() + ": " + (entry.getValue()+1));
		}
	}
}
