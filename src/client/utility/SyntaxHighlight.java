package client.utility;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class SyntaxHighlight {
    public static Boolean STOP_FLAG = false;
    public static Boolean RESET_FLAG = false;
    private JTextPane textPane;
    private List<String > lines;
    private StyledDocument styledDocument;
    private SimpleAttributeSet keywordAttribute, generalAttribute;
    private Keywords keywords;
    private HashMap< String , Boolean > map_c;

    public SyntaxHighlight(JTextPane textPane){
        this.textPane = textPane;
        styledDocument  = this.textPane.getStyledDocument();
        keywordAttribute = new SimpleAttributeSet();
        StyleConstants.setForeground(keywordAttribute, Color.red );
        generalAttribute = new SimpleAttributeSet();
        StyleConstants.setForeground(generalAttribute, Color.DARK_GRAY);
        keywords = new Keywords();
        map_c = keywords.map_c;
        STOP_FLAG = false;
        RESET_FLAG = false;
    }

    public void syntaxColor(){
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
                        else{ break; }
                    }
                    synchronized (textPane){
                        if( tmp != null && !tmp.equals("") && !tmp.equals("\n") && map_c.containsKey(tmp) && tmp.length() > 0 ){
                            styledDocument.setCharacterAttributes(i , tmp.length() , keywordAttribute, true);
                        }
                        else{
                            styledDocument.setCharacterAttributes(i , j-i , generalAttribute, true);

                        }
                    }
                    // System.out.println(tmp);

                    i = j;
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

    }

    public void updateColor(DocumentEvent e){
        if(STOP_FLAG) return;
        if(RESET_FLAG){
            syntaxColor();
            RESET_FLAG = false;
        }

        int curr_ind = e.getOffset();
        int len = textPane.getDocument().getLength();
        int i = curr_ind , j , k;
        char ch = 0;

        try {
            ch = styledDocument.getText(i , 1).charAt(0);
            if(ch == '{'){
                insertChar('}' , i+1);
                return;
            }
            if(ch == '('){
                insertChar(')' , i+1);
                return;
            }
            if(ch == '['){
                insertChar(']' , i+1);
                return;
            }
            String tmp = "";
            j = i;
            while(j < len){
                ch = styledDocument.getText(j , 1).charAt(0);
                if( ch >= 'a' && ch <= 'z' ){
                    tmp += ch;
                    j++;
                }
                else{ break; }
            }

            k = i-1;
            while(k>=0){
                ch = styledDocument.getText(k , 1).charAt(0);
                if( ch >= 'a' && ch <= 'z' ){
                    tmp = ch + tmp;
                    k--;
                }
                else{ break; }
            }

            int finalK = k;
            int finalJ = j;

            String finalTmp = tmp;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if(!finalTmp.equals("") && !finalTmp.equals("\n") && map_c.containsKey(finalTmp)){
                        styledDocument.setCharacterAttributes(finalK+1 , finalJ-finalK-1 , keywordAttribute, true);
                    }
                    else{
                        styledDocument.setCharacterAttributes(finalK+ 1, finalJ-finalK-1 , generalAttribute, true);
                    }
                }
            });
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

    }


    public void insertChar(final char ch , final int pos){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    styledDocument.insertString(pos ,""+ch , null);
                    textPane.setCaretPosition(pos);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
