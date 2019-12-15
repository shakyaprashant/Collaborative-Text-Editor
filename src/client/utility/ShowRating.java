package client.utility;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;


public class ShowRating extends JFrame {

	//private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	String[] tokens;
	private JScrollPane scrollPane;

	public ShowRating(String[] tokens ) {
		this.tokens = tokens;
		setResizable(false);
		setTitle("Rating");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(600 , 600);


//		JButton Find_Cancel = new JButton("Cancel");
//		Find_Cancel.setBounds(312, 500, 100, 30);
//		contentPane.add(Find_Cancel);

		String[] columnName = { "Name"  , "Communication Rating" , "Technical Rating" , "Attitude Rating" , "Overall Score" };
		String[][] data = new String[Integer.parseInt(tokens[1])][5];
		int n = Integer.parseInt(tokens[1]);
		int ind = 2;
		for(int i =0; i< n; i++){
			for(int j = 0; j<5; j++){
				data[i][j] = tokens[ind];
				ind++;
			}
		}

		table = new JTable(data , columnName);
		table.setBounds(20 , 20 , 460 , 400);
		scrollPane = new JScrollPane(table);
		add(scrollPane);

//		Find_Cancel.addActionListener(this);
		setVisible(true);
		requestFocus();

	}


//	@Override
//	public void actionPerformed(ActionEvent e) {
//		if(e.getActionCommand()=="Cancel")
//			this.setVisible(false);
//			this.dispose();
//	}

}
