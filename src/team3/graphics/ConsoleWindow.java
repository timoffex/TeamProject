package team3.graphics;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ConsoleWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JTextArea consoleOutput;
	private JTextField consoleInput;

	public ConsoleWindow() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		int width = 500;
		int height = 300;
		int x = screenSize.width/2 - 3*width/2;
		int y = screenSize.height/2 - 3*height/2;
		
		setBounds(x, y, width, height);
		
		add(consoleOutput = new JTextArea());
		add(consoleInput = new JTextField());
		
		consoleInput.setEnabled(false);
		consoleInput.addActionListener(new InputActionListener());
		
		setVisible(true);
	}
	
	
	public void printLine(String str) {
		consoleOutput.append(str + "\n");
	}
	
	public String getLine() {
		consoleInput.setEnabled(true);
		
		// wait for enter key to be pressed
		try {
			synchronized (consoleInput) {
				consoleInput.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		consoleInput.setEnabled(false);
		
		String input = consoleInput.getText();
		consoleInput.setText("");
		return input;
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

// Copied from http://stackoverflow.com/questions/14706674/system-out-println-to-jtextarea
class JTextAreaOutputStream extends OutputStream
{
    private final JTextArea destination;

    public JTextAreaOutputStream (JTextArea destination)
    {
        if (destination == null)
            throw new IllegalArgumentException ("Destination is null");

        this.destination = destination;
    }

    @Override
    public void write(byte[] buffer, int offset, int length) throws IOException
    {
        final String text = new String (buffer, offset, length);
        SwingUtilities.invokeLater(new Runnable ()
            {
                @Override
                public void run() 
                {
                    destination.append (text);
                }
            });
    }

    @Override
    public void write(int b) throws IOException
    {
        write (new byte [] {(byte)b}, 0, 1);
    }
}