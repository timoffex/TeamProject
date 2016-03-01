package team3;

import java.io.File;
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
	
	
	public Scanner getInputFile() {
		// TODO ask user for input file
		return null;
	}
	
	public Scanner getOutputFile() {
		// TODO ask user for output file
		return null;
	}
	
	public enum UserOption {
		ADD_EDGE
	}
}
