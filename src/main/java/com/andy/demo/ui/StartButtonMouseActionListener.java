package com.andy.demo.ui;

import com.andy.demo.FileCopy;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public
class StartButtonMouseActionListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    boolean isRun = false;

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

        if (mouseEvent.getSource() instanceof JButton) {

            System.out.println("点击了开始按钮");
            MainWindow.consolePanel.clearLog();
            String mvDbPathInputText = MainWindow.mvDbPathInput.getText();
            String excelPathInputText = MainWindow.excelPathInput.getText();
            String targetPathInputText = MainWindow.targetPathInput.getText();
            if (StringUtils.isEmpty(mvDbPathInputText) || StringUtils.isEmpty(excelPathInputText) || StringUtils.isEmpty(targetPathInputText)) {
                System.out.println("有参数为空");
                MainWindow.consolePanel.addLog("有参数为空");
//                JOptionPane.showMessageDialog(MainWindow.frame, "有参数为空", "提示信息", JOptionPane.WARNING_MESSAGE);
            } else {
                final SwingWorker worker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() {
                        isRun = true;
                        System.out.println("开始转移数据...");
                        MainWindow.consolePanel.addLog("开始分拣数据...");
                        try {
                            FileCopy.copy(mvDbPathInputText, excelPathInputText, targetPathInputText);
                        } catch (Exception e) {
                            e.printStackTrace();
                            String message = e.getMessage();
                            JOptionPane.showMessageDialog(MainWindow.frame, message, "提示信息", JOptionPane.WARNING_MESSAGE);
                            return null;
                        }
                        return new Object();
                    }

                    @Override
                    protected void done() {
                        isRun = false;
                    }
                };

                if (isRun) {
                    System.out.println("已经有程序正在运行");
                    JOptionPane.showMessageDialog(MainWindow.frame, "已经有程序正在运行");
                } else {
                    worker.execute();
                }

            }
        }

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}