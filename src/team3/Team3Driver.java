package team3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import team3.UserInterface.UserOption;
import team3.codefile.LinkedStack;
import team3.codefile.MapColoring;
import team3.codefile.Pair;
import team3.codefile.Visitor;
import team3.graphics.ConsoleWindow;
import team3.graphics.GraphWindow;


/*
 To make this easier to grade, here are the important classes and their
 important methods:
 	
 	class Team3Driver
 		main()
 		performAction()
 		undoEdgeCommand() (undo function)
 	
 	class UserInterface
 		enum UserOption
 		getUserOption()
 	
 	class MapColoring
 		solveProblem()
 		
 	
 Honorable mentions:
 	class ConsoleWindow		-- features multi-threading trickery
 	GraphPanel.getColor()   -- generates colors for the graph; there is no longer
 	 							a fixed-length color array
 */
public class Team3Driver {
	private static MapColoring<String> graph;
	private static UserInterface ui;
	
	private static ConsoleWindow console;
	private static GraphWindow display;
	
	/** Used for traversal animation. */
	private static boolean performingAnimation;
	
	/** A stack of REMOVE and ADD commands. Used for the UNDO command. */
	private static LinkedStack<Pair<UserOption, String>> edgeCommandHistory;
	
	public static void main(String[] args) {
		
		// This should be done first. Sets up the console window.
		console = new ConsoleWindow();
		System.setOut(new PrintStream(new ConsoleOutputStream(console)));
		System.setIn(new ConsoleInputStream(console));
		
		
		// Assign all other variables.
		ui = new UserInterface();
		graph = new MapColoring<>();
		display = new GraphWindow(graph);
		
		performingAnimation = false;
		
		
		ui.displayHello();
		ui.displayHelp();
		
		
		edgeCommandHistory = new LinkedStack<>();
		
		
		Pair<UserOption, String> option;
		while ((option = ui.getUserOption()).first == UserOption.BAD_INPUT)
			System.out.println("Unknown command. Type help for a list of commands.");
		
		
		// Command loop.
		while (option.first != UserOption.QUIT) {
			performAction(option);
			
			while ((option = ui.getUserOption()).first == UserOption.BAD_INPUT)
				System.out.println("Unknown command. Type help for a list of commands.");
		}
		
		// Close everything!
		System.exit(0);
	}
	
	/**
	 * Calls the corresponding method for the given command.
	 * @param action
	 */
	private static void performAction(Pair<UserOption, String> action) {
		switch (action.first) {
		case ADD_EDGE: addEdge(action.second); break;
		case REMOVE_EDGE: removeEdge(action.second); break;
		case UNDO_EDGE_REMOVAL: undoEdgeCommand(action.second); break;
		case CLEAR_GRAPH: graph.clear(); display.recomputeNodePlacements(); break;
		
		case TRAVERSE: traverseGraph(action.second); break;
		
		case DISPLAY_SOLUTION: displaySolution(action.second); break;
		
		case LOAD_GRAPH_FROM_FILE: loadGraph(action.second); break;
		case WRITE_GRAPH_TO_FILE: saveGraph(action.second); break;
		
		
		// If we're here, there is a command that wasn't implemented yet. This shouldn't happen.
		default:
			System.out.println("UNIMPLEMENTED COMMAND");
			break;
		}
	}
	
	
	private static void addEdge(String args) {
		if (args == null) {
			System.out.println("Usage: add <v1> <v2>");
			return;
		}
		
		String[] vertices = args.split(" ");
		
		if (vertices.length != 2)
			System.out.println("Wrong arguments.");
		else {
			graph.addEdge(vertices[0], vertices[1], 1);
			graph.addEdge(vertices[1], vertices[0], 1);
			display.recomputeNodePlacements();
			
			System.out.println("Added an edge between " + vertices[0] + " and " + vertices[1]);
			edgeCommandHistory.push(new Pair<UserOption, String>(UserOption.ADD_EDGE, args));
		}
	}
	
	private static void removeEdge(String args) {
		if (args == null) {
			System.out.println("Usage: remove <v1> <v2>");
			return;
		}
		
		String[] vertices = args.split(" ");
		
		if (vertices.length == 1) {		// We can't undo this operation. Remove a single vertex.
			
			if (graph.removeVertex(vertices[0])) {
				System.out.println("Removed the vertex: " + vertices[0]);
				display.recomputeNodePlacements();
			} else
				System.out.println("Could not find that vertex.");
			
		} else if (vertices.length == 2){	// Remove an edge.
			graph.remove(vertices[0], vertices[1]);
			graph.remove(vertices[1], vertices[0]);
			
			display.recomputeNodePlacements();

			System.out.println("Removed an edge between " + vertices[0] + " and " + vertices[1]);
			edgeCommandHistory.push(new Pair<UserOption, String>(UserOption.REMOVE_EDGE, args));
		} else  {
			System.out.println("Wrong arguments.");
		}
	}
	
	private static void loadGraph(String options) {
		if (options == null) {
			System.out.println("No file name given. Usage: load <file name>");
			return;
		}
		
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
					break;
				}
				
				graph.addEdge(vertices[0], vertices[1], 1);
			}

			display.recomputeNodePlacements();
			System.out.println("Loaded the graph!");
			
			in.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file!");
		}
	}
	
	private static void saveGraph(String options) {
		if (options == null) {
			System.out.println("No file name given. Usage: save <file name>");
			return;
		}
		
		
		try {
			File file = new File(options);
			final PrintWriter out = new PrintWriter(file);
			
			graph.breadthFirstTraversal(graph.getAnyData(), new Visitor<String>() {
				@Override
				public void visit(String obj) {
					List<String> neighbors = graph.getUnvisitedNeighborsOfData(obj);
					
					for (String neighbor : neighbors)
						out.println(obj + " " + neighbor);
				}
			});
			

			System.out.println("Saved the graph to " + file.getAbsolutePath());			
			out.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not create file!");
		}
	}
	
	private static void traverseGraph(String options) {
		if (performingAnimation) {
			System.out.println("Error: traversal animation in progress.");
			return;
		}
		
		String startNode;
		
		do {
			startNode = ui.ask("Enter start node: ");
			
			if (graph.contains(startNode))
				break;
			else
				System.out.println("Node doesn't exist. (note: case matters)");
		} while (true);
		
		
		final boolean depthFirst;
		do {
			String depthString = ui.ask("Depth first or breadth first? (d/b or depth/breadth): ");

			if (depthString.equalsIgnoreCase("d") || depthString.equalsIgnoreCase("depth")) {
				depthFirst = true;
				break;
			} else if (depthString.equalsIgnoreCase("b") || depthString.equalsIgnoreCase("breadth")) {
				depthFirst = false;
				break;
			}
			
			System.out.println("Invalid option.");
		} while (true);
		
		
		
		final Visitor<String> visitor = new Visitor<String>() {
			public void visit(String s) {
				display.highlight(s);
				
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		
		final String finalStartNode = startNode;
		
		// Perform the animation asynchronously.
		new Thread(new Runnable() {
			@Override
			public void run() {
				performingAnimation = true;
				
				display.clearHighlights();
				if (depthFirst)
					graph.depthFirstTraversal(finalStartNode, visitor);
				else
					graph.breadthFirstTraversal(finalStartNode, visitor);
				
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				display.clearHighlights();
				

				performingAnimation = false;
			}
		}).start();
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
	
	private static void undoEdgeCommand(String options) {
		Pair<UserOption, String> commandPair = edgeCommandHistory.pop();
		
		switch (commandPair.first) {
		case ADD_EDGE: removeEdge(commandPair.second); break;
		case REMOVE_EDGE: addEdge(commandPair.second); break;
		default:
			System.out.println("Something went wrong in the code. Check Team3Driver.undoEdgeRemoval()");
		}
	}
}

/**
 * Used to redirect System.out to custom console window.
 */
class ConsoleOutputStream extends OutputStream {
	private final ConsoleWindow console;
	
	public ConsoleOutputStream(ConsoleWindow cons) {
		console = cons;
	}
	
	@Override
	public void write(byte[] buffer, int offset, int length) throws IOException {
		String text = new String(buffer, offset, length);
		console.print(text);
	}

	@Override
	public void write(int b) throws IOException {
		write(new byte[] { (byte) b }, 0, 1);
	}
}

/**
 * Used to redirect System.in to custom console window.
 */
class ConsoleInputStream extends InputStream {
	private final ConsoleWindow console;
	private String lastLine;
	
	public ConsoleInputStream(ConsoleWindow cons) {
		console = cons;
	}
	
	@Override
	public int read() throws IOException {
		updateLastLine();
		
		byte[] bytes = lastLine.getBytes();
		
		lastLine = lastLine.substring(1);
		
		return bytes[0];
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		updateLastLine();
		
		byte[] bytes = lastLine.getBytes();
		
		for (int i = 0; i < bytes.length; i++)
			b[off + i] = bytes[i];
		
		lastLine = "";
		
		return bytes.length;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	private void updateLastLine() {
		if (lastLine == null || lastLine.length() == 0)
			lastLine = console.getLine();
	}
}

