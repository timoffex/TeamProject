package team3.graphics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

public class ConsoleWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JTextArea consoleOutput;
	private JTextField consoleInput;
	
	private JScrollPane outputScroll;

	
	private List<String> previousInputs = new ArrayList<>();
	private int currentEntry = 0;
	private boolean currentInputInList = false;
	
	
	public ConsoleWindow() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		int width = 500;
		int height = 300;
		int x = screenSize.width/2 - 3*width/2;
		int y = screenSize.height/2 - 3*height/2;
		
		setBounds(x, y, width, height);
		
		consoleOutput = new JTextArea();
		consoleOutput.setLineWrap(true);
		consoleOutput.setWrapStyleWord(true);
		consoleOutput.setEditable(false);
		consoleOutput.setFont(Font.decode("Courier"));
		
		((DefaultCaret) consoleOutput.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		outputScroll = new JScrollPane(consoleOutput, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		setLayout(new BorderLayout());
		add(outputScroll, BorderLayout.CENTER);
		add(consoleInput = new JTextField(), BorderLayout.PAGE_END);
		
		consoleInput.addActionListener(new InputActionListener());
		
		
		// Add listeners to consoleInput to detect UP/DOWN key strokes.
		Action upAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (previousInputs.size() == 0)
					return;
				
				int newIndex = (currentEntry - 1) % previousInputs.size();
				if (newIndex < 0) newIndex += previousInputs.size();
				
				updateInputField(newIndex);
			}
		};
		
		Action downAction = new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (previousInputs.size() == 0)
					return;
				
				updateInputField((currentEntry + 1) % previousInputs.size());
			}
		};
		
		KeyStroke upKey = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
		KeyStroke downKey = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);

		consoleInput.getInputMap().put(upKey, "up");
		consoleInput.getInputMap().put(downKey, "down");
		consoleInput.getActionMap().put("up", upAction);
		consoleInput.getActionMap().put("down", downAction);
		
		
		setVisible(true);
	}
	
	private void updateInputField(int newIndex) {
		if (!currentInputInList) {
			previousInputs.add(consoleInput.getText());
			currentInputInList = true;
		} else if (currentEntry == previousInputs.size()-1)
			previousInputs.set(currentEntry, consoleInput.getText());
		
		consoleInput.setText(previousInputs.get(newIndex));
		currentEntry = newIndex;
	}
	
	
	public void printLine(String str) {
		consoleOutput.append(str + "\n");
	}
	
	public void print(String str) {
		consoleOutput.append(str);
	}
	
	public String getLine() {
	
		// wait for enter key to be pressed
		try {
			synchronized (consoleInput) {
				consoleInput.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (currentInputInList)
			previousInputs.remove(previousInputs.size()-1);
		
		
		currentEntry = 0;
		
		String input = consoleInput.getText();
		previousInputs.add(input);
		currentInputInList = false;
		
		consoleInput.setText("");
		return input + "\n";
	}
	
	private class InputActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized (consoleInput) {
				consoleInput.notify();
			}
		}
	}
}