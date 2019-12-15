package client.utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import client.utility.LineNumberingTextPane;
import client.controllers.MainController;

public class Settings extends JFrame implements ActionListener  {

	private JPanel contentPane;
	private LineNumberingTextPane lineNumberingTextPane;
	private MainController mainController;
	private JTextField fontTextField;
	private JComboBox comboBox;
	private String[] fontTypes;

	public Settings(MainController mainController , LineNumberingTextPane lineNumberingTextPane) {
		setResizable(false);
		setTitle("Settings");
		this.mainController  = mainController;
		this.lineNumberingTextPane = lineNumberingTextPane;

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 230);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton apply = new JButton("Apply");
		apply.setBounds(312, 32, 100, 30);
		contentPane.add(apply);


		JButton Find_Cancel = new JButton("Cancel");
		Find_Cancel.setBounds(312, 80, 100, 30);
		contentPane.add(Find_Cancel);

		JLabel findLabel = new JLabel("Font Size ");
		findLabel.setBounds(10, 34, 75, 23);
		contentPane.add(findLabel);

		fontTextField = new JTextField();
		fontTextField.setBounds(73, 28, 200, 34);
		contentPane.add(fontTextField);
		fontTextField.setColumns(10);

		JLabel replaceLabel = new JLabel("Font type");
		replaceLabel.setBounds(10, 82, 75, 23);
		contentPane.add(replaceLabel);
		replaceLabel.setVisible(true);

		fontTypes = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		comboBox = new JComboBox(fontTypes);
		comboBox.setBounds(73, 76, 200, 34);
		comboBox.setSelectedIndex(15);
		contentPane.add(comboBox);


//		caseSensitive = new JCheckBox("Case Sensitive");
//		caseSensitive.setBounds(10, 140, 150, 30);
//		contentPane.add(caseSensitive);

		apply.addActionListener(this);
		Find_Cancel.addActionListener(this);
		//replaceButton.addActionListener(this);
		setVisible(true);
		requestFocus();
		setAlwaysOnTop(true);
	}


	public void applySettings(){
		String s = fontTextField.getText();
		if(s == null || s.length() == 0) s = "18";
		int fontSize = Integer.parseInt(s);
		int ind = comboBox.getSelectedIndex();
		if(ind == -1) ind = 15;
		mainController.setFontsize( fontTypes[ind] ,  fontSize);
		lineNumberingTextPane.setFontSize( fontTypes[ind] ,  fontSize);

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand()=="Cancel")
		{
			this.setVisible(false);
			this.dispose();
		}
		else if(e.getActionCommand()=="Apply")
		{
			applySettings();
		}
		else{  }
	}


}
