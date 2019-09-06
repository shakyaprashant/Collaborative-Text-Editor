package interCode.utility;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import interCode.utility.Keywords;

public class SyntexHighlight {
    private JTextPane textPane;
    private List<String > lines;
    private StyledDocument styledDocument;
    private SimpleAttributeSet simpleAttributeSet , simpleAttributeSet1;
    private Keywords keywords;
    private HashMap< String , Boolean > map_c;

    public SyntexHighlight(JTextPane textPane){
        this.textPane = textPane;
        styledDocument  = this.textPane.getStyledDocument();
        simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground( simpleAttributeSet , Color.red );
        simpleAttributeSet1 = new SimpleAttributeSet();
        StyleConstants.setForeground(simpleAttributeSet1 , Color.DARK_GRAY);
        keywords = new Keywords();
        map_c = keywords.map_c;

    }

    public void syntexColor(){
        Boolean comment = false;
        Boolean isString  = false;
        int len = textPane.getDocument().getLength();
        for(int i =0; i< len; i++){
            try {
                char ch = styledDocument.getText(i , 1).charAt(0);
                if( ch >= 'a' && ch <= 'z' ){
                    String tmp = "" + ch;
                    int j = i+1;
                    while(j < len){
                        ch = styledDocument.getText(j , 1).charAt(0);
                        if( ch >= 'a' && ch <= 'z' ){
                            tmp += ch;
                            j++;
                        }
                        else{
                            break;
                        }
                    }
                    // System.out.println(tmp);
                    if( !tmp.equals("") && !tmp.equals("\n") && map_c.containsKey(tmp)){
                        styledDocument.setCharacterAttributes(i , j-i , simpleAttributeSet , true);
                    }
                    i = j;
                }

            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

    }

    public void updateColor(DocumentEvent e){

        int curr_ind = e.getOffset();
        int len = textPane.getDocument().getLength();
        int i = curr_ind , j , k;
        char ch = 0;

        try {
            ch = styledDocument.getText(i , 1).charAt(0);
            // System.out.println("---->"+ch+"<-----");
            String tmp = "" ;
            j = i;
            while(j < len){
                ch = styledDocument.getText(j , 1).charAt(0);
                // System.out.print("+"+ch+"+");
                if( ch >= 'a' && ch <= 'z' ){
                    tmp += ch;
                    j++;
                }
                else{
                    break;
                }
            }

            k = i-1;
            while(k>=0){
                ch = styledDocument.getText(k , 1).charAt(0);
                if( ch >= 'a' && ch <= 'z' ){
                    tmp = ch + tmp;
                    k--;
                }
                else{
                    break;
                }
            }
            System.out.println(tmp);

            final String str = tmp;
            int finalK = k;
            int finalJ = j;

            String finalTmp = tmp;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if(!finalTmp.equals("") && !finalTmp.equals("\n") && map_c.containsKey(finalTmp)){
                        styledDocument.setCharacterAttributes(finalK+1 , finalJ-finalK-1 , simpleAttributeSet , true);
                    }
                    else{
                        styledDocument.setCharacterAttributes(finalK+ 1, finalJ-finalK-1 , simpleAttributeSet1 , true);
                    }
                }
            });
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

    }


}
