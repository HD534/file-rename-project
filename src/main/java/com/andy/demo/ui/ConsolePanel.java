package com.andy.demo.ui;

import javax.swing.*;
import java.awt.*;

public class ConsolePanel {

    JScrollPane scrollPane;
    JTextArea textArea; //输出台

    public ConsolePanel(Rectangle panelLocation) {


        textArea = new JTextArea();
        textArea.setText("");
        textArea.setEditable(false);
//        textArea.setLineWrap(true); // 设置自动换行

        // 设置断行不断字
        // If set to true the lines will be wrapped at word boundaries (whitespace) if they are too long to fit within the allocated width.
        // If set to false, the lines will be wrapped at character boundaries. By default this property is false.
//        textArea.setWrapStyleWord(true);

        scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(panelLocation);

        scrollPane.setBackground(Color.LIGHT_GRAY);
        scrollPane.setBorder(BorderFactory.createTitledBorder("控制台"));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    public void addLog(String log) {
        textArea.append(log + "\n");
        textArea.paintImmediately(textArea.getBounds());
    }

    public void clearLog() {
        textArea.setText("");
        textArea.paintImmediately(textArea.getBounds());
    }

}
