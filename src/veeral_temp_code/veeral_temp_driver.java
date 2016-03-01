package veeral_temp_code;

import java.io.PrintWriter;

import team3.UserInterface;

public class veeral_temp_driver {

	public static void main(String[] args) {
		
		UserInterface ui = new UserInterface();
		PrintWriter w = ui.getOutputFile();
		w.println("hei");
		System.out.println(w.toString());
		System.out.println("succesfully written");
	}
	
	
}
