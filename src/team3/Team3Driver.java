package team3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

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
		
		// This should be done first.
		console = new ConsoleWindow();
		System.setOut(new PrintStream(new ConsoleOutputStream(console)));
		System.setIn(new ConsoleInputStream(console));
		
		
		ui = new UserInterface();
		graph = new MapColoring<>();
		display = new GraphWindow(graph);
		
		
		ui.displayHello();
		ui.displayHelp();
		
		Pair<UserOption, String> option = ui.getUserOption();
		while (option.first != UserOption.QUIT) {
			performAction(option);
			
			option = ui.getUserOption();
		}
		
		// Close everything!
		System.exit(0);
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

/**
 * Used to redirect System.out to custom console window.
 */
class ConsoleOutputStream extends OutputStream {
	private final ConsoleWindow console;
	
	ConsoleOutputStream(ConsoleWindow cons) {
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
 * Used to redirect System.out to custom console window.
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

