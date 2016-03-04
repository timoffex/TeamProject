package team3;

import team3.UserInterface.UserOption;
import team3.codefile.MapColoring;
import team3.codefile.Pair;
import team3.codefile.Visitor;

public class Team3Driver {
	private static MapColoring<String> graph;
	private static UserInterface ui;
	
	public static void main(String[] args) {
		ui = new UserInterface();
		graph = new MapColoring<>();
		
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
		}
	}
	
	private static void removeEdge(String args) {
		String[] vertices = args.split(" ");
		
		if (vertices.length != 2)
			System.out.println("Wrong arguments.");
		else {
			graph.remove(vertices[0], vertices[1]);
			graph.remove(vertices[1], vertices[0]);
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

}
