package team3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class UserInterface {
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
	
	
	public UserOption getUserOption() {
		// TODO present user with a menu and ask what user would like to do
		// TODO example: add an edge, remove an edge, undo, display, etc..
		
		return UserOption.ADD_EDGE;
	}
	
	// Returns a PrintWriter which lets you write to a file
	public PrintWriter getOutputFile() {
		// TODO ask user for output file
		return null;
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
		ADD_EDGE, REMOVE_EDGE, UNDO_EDGE_REMOVAL,
		DISPLAY_GRAPH, DISPLAY_SOLUTION,
		WRITE_GRAPH_TO_FILE
	}
}
