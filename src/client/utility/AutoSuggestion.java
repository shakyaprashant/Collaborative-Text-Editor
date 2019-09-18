package client.utility;

import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import client.utility.Keywords;

public class AutoSuggestion {
    public static Boolean STOP_FLAG = false;
    private final JTextPane textPane;
    private ArrayList<String> dictionary;
    private String typedWord;
    private JPopupMenu popupMenu;
    private Keywords keywords;
    private StyledDocument styledDocument;
    private int offset;



    public AutoSuggestion(JTextPane textPane ){
        this.textPane = textPane;
        styledDocument = textPane.getStyledDocument();
        popupMenu = new JPopupMenu();

        typedWord = "";
        keywords = new Keywords();
        dictionary = new ArrayList<>();
        initDictionary();
        addKeyBindingToRequestFocusInPopUpMenu();
        STOP_FLAG = false;
    }


    public void checkAndShowSuggestion(DocumentEvent documentEvent){
        if(STOP_FLAG) return;
        popupMenu.setVisible(false);
        popupMenu.removeAll();
        int X = 0;
        int Y = 0;

        try {
            Rectangle rectangle = textPane.modelToView(textPane.getCaretPosition()+1);
            X = rectangle.x ;
            Y = rectangle.y ;
        } catch (BadLocationException e) {
            //e.printStackTrace();
        }
        getCurrentlyTypedWord(documentEvent);
        if(!typedWord.isEmpty()){
            popupMenu.removeAll();
            boolean added = getMatchingWords();
            if(!added){
                if(popupMenu.isVisible())
                popupMenu.setVisible(false);
            }
            else{
                popupMenu.show(textPane , X , Y+25);
                popupMenu.setVisible(true);
                //textPane.setCaretPosition(offset);
                textPane.requestFocusInWindow();

            }

        }

    }


    public void getCurrentlyTypedWord(DocumentEvent documentEvent){

        offset = documentEvent.getOffset();
        char ch = 0;
        try {
            ch = styledDocument.getText(offset , 1).charAt(0);
            System.out.println(ch);
            if( ch>='a' && ch<='z' )
            {
                int k = offset;
                String tmp = "";
                while(k>=0){
                    ch = styledDocument.getText(k , 1).charAt(0);
                    if( ch >= 'a' && ch <= 'z' ){
                        tmp = ch + tmp;
                        k--;
                    }
                    else{ break; }
                }
                typedWord = tmp;
            }
            else {
                typedWord = "";
                return;
            }

            System.out.println("---->>>"+typedWord);

        } catch (BadLocationException e) {
            e.printStackTrace();

        }
    }

    public Boolean getMatchingWords(){
        if (typedWord.isEmpty() || typedWord.length() <= 0) {
            return false;
        }

        boolean suggestionAdded = false;
        for (String word : dictionary) {
            if( typedWord.length() >= word.length()) continue;
            boolean fullymatches = true;
            for (int i = 0; i < typedWord.length(); i++) {

                if (!typedWord.toLowerCase().startsWith(String.valueOf(word.toLowerCase().charAt(i)), i)) {
                    fullymatches = false;
                    break;
                }
            }
            if (fullymatches) {
                addWordToSuggestions(word);
                suggestionAdded = true;
            }
        }
        return suggestionAdded;
    }

    public void addWordToSuggestions(String word){
        JMenuItem item = new JMenuItem(word);
        item.addActionListener(popupMenuListener);
        popupMenu.add(item);
    }

    ActionListener popupMenuListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(e.getActionCommand());
            insertcWord(e.getActionCommand()+" " , typedWord , offset);
        }
    };

//    KeyListener popupMenuKeyListener = new KeyListener() {
//        @Override
//        public void keyTyped(KeyEvent e) {
//            System.out.println(e.getKeyChar());
//        }
//
//        @Override
//        public void keyPressed(KeyEvent e) {
//            System.out.println(e.getKeyChar());
//        }
//
//        @Override
//        public void keyReleased(KeyEvent e) {
//            System.out.println(e.getKeyChar());
//
//        }
//    };

    public void addKeyBindingToRequestFocusInPopUpMenu(){
        textPane.getInputMap( textPane.WHEN_FOCUSED ).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN ,0 , true) , "Down pressed");
        textPane.getActionMap().put("Down pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!popupMenu.isVisible()){
                    return;
                }
                //System.out.println("down key pressed...");
                startFocusingPopupMenu();
            }
        });


    }

    public void insertcWord(String word  ,String typedWord,int x){
        String tmp = removePrefix(word , typedWord);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    styledDocument.insertString(x+1 ,tmp , null);
                    textPane.setCaretPosition(x+1+tmp.length());
                    removeSuggestion();
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public String removePrefix(String s, String prefix)
    {
        if (s != null && prefix != null && s.startsWith(prefix)){
            return s.substring(prefix.length());
        }
        return s;
    }

    public void startFocusingPopupMenu(){
        //System.out.println("insdide focus");
        popupMenu.setVisible(false);
        popupMenu.setVisible(true);

    }


    public void initDictionary(){
        for(String s : keywords.map_c.keySet()){
            dictionary.add(s);
        }
    }


    public void removeSuggestion(){
        popupMenu.removeAll();
        popupMenu.setVisible(false);
    }
}
