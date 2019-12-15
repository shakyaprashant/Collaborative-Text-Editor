package client.utility;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;

public class LineNumberingTextPane extends JTextPane {
    private JTextPane textArea;

    public LineNumberingTextPane(JTextPane textArea)
    {
        this.textArea = textArea;
        setBackground(Color.DARK_GRAY);
        setForeground(Color.lightGray);
        setFontSize("Arial" , 18);
        setEditable(false);

    }

    public void updateLineNumbers()
    {
        String lineNumbersText = getLineNumbersText();
        setText(lineNumbersText);

    }

    private String getLineNumbersText()
    {
        int caretPosition = textArea.getDocument().getLength();
        Element root = textArea.getDocument().getDefaultRootElement();
        StringBuilder lineNumbersTextBuilder = new StringBuilder();
        lineNumbersTextBuilder.append(" 1 ").append(System.lineSeparator());

        for (int elementIndex = 2; elementIndex < root.getElementIndex(caretPosition) + 2; elementIndex++)
        {
            lineNumbersTextBuilder.append(" "+elementIndex+" ").append(System.lineSeparator());
        }

        return lineNumbersTextBuilder.toString();
    }


    public  void setFontSize(String fontType ,  int fontSize){
        Font font = new Font(fontType, Font.PLAIN, fontSize);
        setFont(font);

    }
}
