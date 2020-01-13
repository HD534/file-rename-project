package com.andy.demo.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DirectoryChooserActionListener implements ActionListener {

    private String chooseDirectory;

    private JTextField target;

    public DirectoryChooserActionListener(JTextField target) {
        this.target = target;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser fc=new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int val=fc.showOpenDialog(null);    //文件打开对话框
        if(val== JFileChooser.APPROVE_OPTION) {
            //正常选择文件
            target.setText(fc.getSelectedFile().toString());
        }
    }
}
