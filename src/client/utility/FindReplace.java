package client.utility;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import javax.swing.JButton;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;


public class FindReplace extends JFrame implements ActionListener  {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextPane textPane;
	private int findPosn = 0;

	private boolean findCase = false;
	private boolean replaceConfirm = true;
	private JTextField Find_TextField;
	private JTextField replace_TextField;
	private DefaultHighlighter defaultHighlighter;
	private DefaultHighlighter.DefaultHighlightPainter defaultHighlightPainter;
	private DefaultStyledDocument defaultStyledDocument;
	JCheckBox caseSensitive;
	private SimpleAttributeSet simpleAttributeSet , simpleAttributeSet1;

	public FindReplace(JTextPane textPane) {
		setResizable(false);
		setTitle("Find Replace");

		this.textPane =textPane;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 230);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton Find = new JButton("Find");
		Find.setBounds(312, 32, 100, 30);
		contentPane.add(Find);

		JButton replaceButton = new JButton("Replace");
		replaceButton.setBounds(312, 80, 100, 30);
		contentPane.add(replaceButton);

		JButton Find_Cancel = new JButton("Cancel");
		Find_Cancel.setBounds(312, 128, 100, 30);
		contentPane.add(Find_Cancel);

		JLabel findLabel = new JLabel("Find ");
		findLabel.setBounds(10, 34, 75, 23);
		contentPane.add(findLabel);

		Find_TextField = new JTextField();
		Find_TextField.setBounds(73, 28, 183, 34);
		contentPane.add(Find_TextField);
		Find_TextField.setColumns(10);

		JLabel replaceLabel = new JLabel("Replace");
		replaceLabel.setBounds(10, 82, 75, 23);
		contentPane.add(replaceLabel);
		replaceLabel.setVisible(true);

		replace_TextField = new JTextField();
		replace_TextField.setBounds(73, 76, 183, 34);
		contentPane.add(replace_TextField);
		replace_TextField.setColumns(10);

		caseSensitive = new JCheckBox("Case Sensitive");
		caseSensitive.setBounds(10, 140, 150, 30);
		contentPane.add(caseSensitive);


		Find.addActionListener(this);
		Find_Cancel.addActionListener(this);
		replaceButton.addActionListener(this);
		setVisible(true);
		requestFocus();
		setAlwaysOnTop(true);

		// adding highlighter //
		defaultHighlighter = (DefaultHighlighter) textPane.getHighlighter();
		defaultHighlighter.removeAllHighlights();
		defaultHighlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
		defaultStyledDocument = (DefaultStyledDocument) textPane.getDocument();

		// setting attribute
		simpleAttributeSet = new SimpleAttributeSet();
		StyleConstants.setForeground( simpleAttributeSet , Color.red );
		simpleAttributeSet1 = new SimpleAttributeSet();
		StyleConstants.setForeground(simpleAttributeSet1 , Color.DARK_GRAY);

	}

	public void findText(String find) {

		try {
			if (find == null || find.length() <= 0) return;

			defaultHighlighter.removeAllHighlights();
			String text = "";
			try {
				text = defaultStyledDocument.getText(0, defaultStyledDocument.getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			int index = 0;

			if(!caseSensitive.isSelected()){
				find = find.toLowerCase();
				text = text.toLowerCase();
			}


			while ((index = text.indexOf(find, index)) > -1) {

				try {
					defaultHighlighter.addHighlight(index, find.length() + index, defaultHighlightPainter);
					index = index + find.length();
				} catch (BadLocationException ex) {
					System.out.println("error in highlighting ...");
					//System.out.println(index);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void replaceWords(String find, String replace) {

		try {
			if (find == null || find.length() <= 0 || replace == null || replace.length() == 0) return;
			defaultHighlighter.removeAllHighlights();

			String text = "";
			try {
				//text = defaultStyledDocument.getText(0, defaultStyledDocument.getLength());
				text = textPane.getText();
			} catch (Exception e) {
				e.printStackTrace();
			}
			int index = 0;

			if(!caseSensitive.isSelected()){
				find = find.toLowerCase();
				text = text.toLowerCase();
			}
			while ((index = text.indexOf(find, index)) > -1) {
				try {
					//defaultStyledDocument.remove(index , find.length());
					//defaultStyledDocument.insertString(index , replace , simpleAttributeSet1);

					//defaultStyledDocument.replace(index , find.length() , replace , simpleAttributeSet1);
					//defaultStyledDocument = (DefaultStyledDocument) textPane.getDocument();
					//text = defaultStyledDocument.getText(0 , defaultStyledDocument.getLength());
					textPane.select(index , index+find.length());
					textPane.replaceSelection(replace);
					text = textPane.getText();
				} catch (Exception ex) {
					System.out.println("error in removing");
					//System.out.println(index);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	public int getLineNumber(JTextPane component, int pos)
	{
	    int posLine = 0;
	    int y = 0;
	    try
	    {
	        Rectangle caretCoords = component.modelToView(pos);
	        y = (int)caretCoords.getY();
	    }
	    catch (BadLocationException ex){ }

	    int lineHeight = component.getFontMetrics(component.getFont()).getHeight();
	    posLine = (y / lineHeight) + 1;
	    return posLine;
	}


	public int nextIndex(String input, String find, int start, boolean caseSensitive ) {
    	int textPosn = -1;
    	if ( input != null && find != null && start < input.length() ) {
        	if ( caseSensitive == true ) {
        	       textPosn = input.indexOf( find, start );
            } else {
        	  textPosn = input.toLowerCase().indexOf( find.toLowerCase(), start );
        	}
    	}
    	return textPosn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="Cancel")
		{
			SyntaxHighlight.STOP_FLAG = false;
			AutoSuggestion.STOP_FLAG = false;
			defaultHighlighter.removeAllHighlights();
			this.setVisible(false);
			this.dispose();
		}
		else if(e.getActionCommand()=="Find")
		{
			findText(Find_TextField.getText());
		}
		else if(e.getActionCommand() == "Replace"){
			replaceWords(Find_TextField.getText() , replace_TextField.getText());
		}
		else{

		}
	}


//	@Override
//	public void windowOpened(WindowEvent e) {
//
//	}
//
//	@Override
//	public void windowClosing(WindowEvent e) {
//		defaultHighlighter.removeAllHighlights();
//	}
//
//	@Override
//	public void windowClosed(WindowEvent e) {
//		defaultHighlighter.removeAllHighlights();
//		this.setVisible(false);
//		this.dispose();
//	}
//
//	@Override
//	public void windowIconified(WindowEvent e) {
//
//	}
//
//	@Override
//	public void windowDeiconified(WindowEvent e) {
//
//	}
//
//	@Override
//	public void windowActivated(WindowEvent e) {
//
//	}
//
//	@Override
//	public void windowDeactivated(WindowEvent e) {
//
//	}
}
