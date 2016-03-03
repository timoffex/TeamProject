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
	 * such that P.first is the command and P.second is the extra options. <br/>
	 * <br/>
	 * If the command is ADD_EDGE, options will be the edge name.<br/>
	 * If the command is REMOVE_EDGE, options will be the edge name.<br/>
	 * If the command is WRITE_GRAPH_TO_FILE, options will be the file name.
	 * <br/>
	 * Otherwise, options is null.
	 */
	public Pair<UserOption, String> getUserOption() {
		// TODO present user with a menu and ask what user would like to do
		// TODO example: add an edge, remove an edge, undo, display, etc..


		System.out.print("Enter command: ");
		String userInput = system.nextLine();

		int spaceIndex = userInput.indexOf(' ');

		String command, options;
		if (spaceIndex == -1) {
			command = userInput;
			options = null;
		} else {
			command = userInput.substring(0, spaceIndex);
			options = userInput.substring(spaceIndex + 1);
		}

		switch (command) {
		case "u":
		case "undo":
			return new Pair<>(UserOption.UNDO_EDGE_REMOVAL, null);
		case "d":
		case "display":
			return new Pair<>(UserOption.DISPLAY_GRAPH, null);
		case "s":
		case "solve":
			return new Pair<>(UserOption.DISPLAY_SOLUTION, null);
		case "a":
		case "add":
			return new Pair<>(UserOption.ADD_EDGE, options);
		case "r":
		case "remove":
			return new Pair<>(UserOption.REMOVE_EDGE, options);
		case "save":
			return new Pair<>(UserOption.WRITE_GRAPH_TO_FILE, options);
		case "q":
		case "quit":
			return new Pair<>(UserOption.QUIT, null);
		}

		// add <city name 1>, <city name 2>
		// -> Pair(ADD_EDGE, "city name 1, city name 2")

		return new Pair<>(UserOption.NOOP, null);
	}

	public void displayHelp() {
		System.out.println("\nOptions:");
		System.out.println("undo|u                           Undo remove command.");
		System.out.println("display|d                        Display the graph.");
		System.out.println("solve|s                          Solve the graph and display the solution.");
		System.out.println("add|a <vertex 1> <vertex 2>      Add an edge between vertex 1 and vertex 2 to the graph. Adds vertices if necessary.");
		System.out.println("remove|r <vertex 1> <vertex 2>   Remove the edge between the specified vertices.");
		System.out.println("save <path to file>              Save the graph to a file.");
		System.out.println("help                             Display this menu.");
		System.out.println("quit                             Quit program.");
		
		System.out.println();
	}

	// Returns a PrintWriter which lets you write to a file
	public PrintWriter getOutputFile() {
		// TODO ask user for output file
		String filename;
		PrintWriter writer = null;
		Scanner userScanner = new Scanner(System.in);

		System.out.print("Enter output filename: ");
		filename = userScanner.nextLine();
		File file = new File(filename + ".txt");
		try {
			writer = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			System.out.println("Can't open output file\n");
		}

		return writer;
	}

	// used to read data from input file
	public static Scanner openInputFile() {
		String filename;
		Scanner userScanner = new Scanner(System.in);
		Scanner scanner = null;

		System.out.print("Enter the input filename: ");
		filename = userScanner.nextLine();
		File file = new File(filename);

		try {
			scanner = new Scanner(file);
		} // end try
		catch (FileNotFoundException fe) {
			System.out.println("Can't open input file\n");
			return null; // array of 0 elements
		} // end catch
		return scanner;
	}

	public enum UserOption {
		ADD_EDGE, REMOVE_EDGE, UNDO_EDGE_REMOVAL, DISPLAY_GRAPH, DISPLAY_SOLUTION, WRITE_GRAPH_TO_FILE,
		QUIT,
		NOOP
	}
}
