package client.utility;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.*;
import javax.swing.JButton;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;


public class FindReplace extends JFrame implements ActionListener  {

	//private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextPane textPane;
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
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
			} catch (BadLocationException e) { e.printStackTrace(); }

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
				}
			}
		} catch (Exception e){ e.printStackTrace(); }
	}

	public void replaceWords(String find, String replace) {
		try {
			if (find == null || find.length() <= 0 || replace == null || replace.length() == 0 || find.equals(replace)) return;
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
				} catch (Exception e) {
					System.out.println("error in replacing");
				}
			}
		} catch (Exception e){ e.printStackTrace(); }

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="Cancel")
		{
			SyntaxHighlight.STOP_FLAG = false;
			AutoSuggestion.STOP_FLAG = false;
			SyntaxHighlight.RESET_FLAG = true;
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
		else{  }
	}


}
