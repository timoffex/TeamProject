package team3;

import team3.UserInterface.UserOption;
import team3.codefile.Pair;

public class Team3Driver {
	public static void main(String[] args) {
		UserInterface ui = new UserInterface();
		
		ui.displayHello();
		
		do {
			Pair<UserOption, String> option = ui.getUserOption();
			
			performAction(option);
		} while (ui.userWantsToContinue());
	}
	
	
	private static void performAction(Pair<UserOption, String> action) {
		switch (action.first) {
		case ADD_EDGE:
			break;
		}
	}

}
