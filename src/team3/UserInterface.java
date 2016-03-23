package team3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import team3.codefile.Pair;

public class UserInterface {
	/** User input scanner. */
	private Scanner system;

	public UserInterface() {
		system = new Scanner(System.in);
	}

	public void displayHello() {
		System.out.println("Hello");
		System.out.println();
	}

	public boolean userWantsToContinue() {
		System.out.println("Continue? ");
		system.nextLine();
		return true;
	}

	/**
	 * Gets user command and returns the command with its options as a pair P
	 * such that P.first is the command and P.second is the extra options.
	 */
	public Pair<UserOption, String> getUserOption() {
		String command, options;
		String userInput;

		
		do {
			userInput = ask("Enter command: ");

			int spaceIndex = userInput.indexOf(' ');
			if (spaceIndex == -1) {
				command = userInput;
				options = null;
			} else {
				command = userInput.substring(0, spaceIndex);
				options = userInput.substring(spaceIndex + 1);
			}
	
			
			if (command.equals("help")) 
				displayHelp();
			else break;
			
		} while (true);
		
		
		switch (command) {
		case "undo":
			return new Pair<>(UserOption.UNDO_EDGE_REMOVAL, options);
		case "s":
		case "solve":
			return new Pair<>(UserOption.DISPLAY_SOLUTION, options);
		case "a":
		case "add":
			return new Pair<>(UserOption.ADD_EDGE, options);
		case "r":
		case "remove":
			return new Pair<>(UserOption.REMOVE_EDGE, options);
		case "q":
		case "quit":
			return new Pair<>(UserOption.QUIT, options);
		case "load":
			return new Pair<>(UserOption.LOAD_GRAPH_FROM_FILE, options);
		case "save":
			return new Pair<>(UserOption.WRITE_GRAPH_TO_FILE, options);
		case "clear":
			return new Pair<>(UserOption.CLEAR_GRAPH, options);
		case "traverse":
			return new Pair<>(UserOption.TRAVERSE, options);
		}

		// add <city name 1>, <city name 2>
		// -> Pair(ADD_EDGE, "city name 1, city name 2")

		return new Pair<>(UserOption.BAD_INPUT, null);
	}

	/**
	 * Prompts a user and returns the response.
	 * @param prompt - Message to prompt with.
	 * @return User input.
	 */
	public String ask(String prompt) {
		System.out.print(prompt);
		
		// Needed because external console window separates input field from text output area.
		String line = system.nextLine();
		System.out.println(line);
		
		
		return line;
	}
	
	public void displayHelp() {
		System.out.println("\nOptions:");
		System.out.println("add <vertex 1> <vertex 2>        Add an edge between vertex 1 and vertex 2 to the graph. Adds vertices if necessary.");
		System.out.println("remove <vertex 1> <vertex 2>     Remove the edge between the specified vertices.");
		System.out.println("remove <vertex>                  Removes a vertex. This operation cannot be undone.");
		System.out.println("undo                             Undo remove or add command (does not undo clear command).");
		System.out.println("clear                            Remove all vertices from the graph.");
		System.out.println("solve|s                          Display the minimum number of colors required for the graph.");
		System.out.println("traverse						 Traverse the graph.");
		System.out.println("save <path to file>              Save the graph to a file.");
		System.out.println("load <path to file>              Load the graph from a file.");
		System.out.println("help                             Display this menu.");
		System.out.println("quit                             Quit program.");
		
		System.out.println();
	}

	public enum UserOption {
		ADD_EDGE, REMOVE_EDGE, UNDO_EDGE_REMOVAL, 
		CLEAR_GRAPH,
		
		TRAVERSE,
		
		DISPLAY_SOLUTION,

		WRITE_GRAPH_TO_FILE,
		LOAD_GRAPH_FROM_FILE,
		
		QUIT,
		BAD_INPUT
	}
}
