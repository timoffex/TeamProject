package team3;

import team3.UserInterface.UserOption;

public class Team3Driver {
	public static void main(String[] args) {
		System.out.println("This is our main class.");
		
		
		UserInterface ui = new UserInterface();
		
		do {
			UserOption option = ui.getUserOption();
			
			performAction(option);
			
			ui.displayHello();
		} while (ui.userWantsToContinue());
	}
	
	
	private static void performAction(UserOption action) {
		switch (action) {
		case ADD_EDGE:
			break;
		}
	}

}
