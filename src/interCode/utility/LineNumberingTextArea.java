package interCode.utility;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;

public class LineNumberingTextArea extends JTextPane {
    private JTextPane textPane;

    public LineNumberingTextArea(JTextPane textPane)
    {
        this.textPane = textPane;
        setBackground(Color.DARK_GRAY);
        setForeground(Color.lightGray);
        Font font = new Font("Serif", Font.PLAIN, 18);
        setFont(font);
        setEditable(false);

    }

    public void updateLineNumbers()
    {
        String lineNumbersText = getLineNumbersText();
        setText(lineNumbersText);

    }

    private String getLineNumbersText()
    {
        int caretPosition = textPane.getDocument().getLength();
        Element root = textPane.getDocument().getDefaultRootElement();
        StringBuilder lineNumbersTextBuilder = new StringBuilder();
        lineNumbersTextBuilder.append("1").append(System.lineSeparator());

        for (int elementIndex = 2; elementIndex < root.getElementIndex(caretPosition) + 2; elementIndex++)
        {
            lineNumbersTextBuilder.append(elementIndex).append(System.lineSeparator());
        }

        return lineNumbersTextBuilder.toString();
    }


}
