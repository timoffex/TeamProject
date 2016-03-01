package team3;

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
	}
	
	public boolean userWantsToContinue() {
		System.out.println("Continue? ");
		system.nextLine();
		return true;
	}
	
	
	/**
	 * Gets user command and returns the command with its options as a pair P such
	 * that P.first is the command and P.second is the extra options.
	 * @return
	 */
	public Pair<UserOption, String> getUserOption() {
		// TODO present user with a menu and ask what user would like to do
		// TODO example: add an edge, remove an edge, undo, display, etc..
		
		displayMenu();
		
		String userInput = system.nextLine();
		
		String[] parts = userInput.split(" ");
		
		String command = parts[0];
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
		case "add":
			return new Pair<>(UserOption.ADD_EDGE, null);
		case "remove":
			return new Pair<>(UserOption.REMOVE_EDGE, null);
		case "save":
			return new Pair<>(UserOption.WRITE_GRAPH_TO_FILE, null);
		}
		
		return new Pair<>(UserOption.NOOP, null);
	}
	
	private void displayMenu() {
		// TODO Display user options
	}
	
	
	public Scanner getInputFile() {
		// TODO ask user for input file
		return null;
	}
	
	public enum UserOption {
		ADD_EDGE, REMOVE_EDGE, UNDO_EDGE_REMOVAL,
		DISPLAY_GRAPH, DISPLAY_SOLUTION,
		WRITE_GRAPH_TO_FILE,
		
		NOOP
	}
}
