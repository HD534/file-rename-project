package com.andy.demo.ui;

import com.alee.laf.WebLookAndFeel;

import javax.swing.*;
import java.awt.*;

import static com.andy.demo.Const.*;

public class MainWindow {

    public static final int WINDOW_WIDTH = 650;
    public static final int WINDOW_HEIGHT = 500;

    public static final int LABEL_WIDTH = 120;
    public static final int LABEL_HEIGHT = 25;

    public static final int INPUT_WIDTH = 380;
    public static final int INPUT_HEIGHT = 25;

    public static final int BUTTON_WIDTH = 50;
    public static final int BUTTON_HEIGHT = 25;

    public static final int LABEL_X_LOCATION = 10;
    public static final int INPUT_X_LOCATION = 120;
    public static final int BUTTON_X_LOCATION = 530;

    public static final int DB_PATH_Y_LOCATION = 20;
    public static final int EXCEL_PATH_Y_LOCATION = 50;
    public static final int TARGET_PATH_Y_LOCATION = 80;
    public static final int START_BUTTON_Y_LOCATION = 120;

    public static final int CONSOLE_PANEL_Y_LOCATION = 150;
    public static final int CONSOLE_PANEL_X_LOCATION = 10;
    public static final int CONSOLE_PANEL_WIDTH = WINDOW_WIDTH - 30;
    public static final int CONSOLE_PANEL_HEIGHT = WINDOW_HEIGHT - CONSOLE_PANEL_Y_LOCATION - 30;




    public static void main(String[] args) {
        // 显示应用 GUI
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    static JFrame frame;

    /**
     * {
     * 创建并显示GUI。出于线程安全的考虑，
     * 这个方法在事件调用线程中调用。
     */
    private static void createAndShowGUI() {
        // 确保一个漂亮的外观风格
        WebLookAndFeel.install();

        // 创建及设置窗口
        frame = new JFrame("分拣小星星");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        frame.setLocationRelativeTo(null);

        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);
        /*
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);

        // 设置界面可见
        frame.setVisible(true);
    }

    static JTextField mvDbPathInput;
    static JTextField excelPathInput;
    static JTextField targetPathInput;

    static ConsolePanel consolePanel;

    private static void placeComponents(JPanel panel) {

        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);


        JLabel mvDbPathLabel = getLabel(MV_DB_PATH_CHINESE_NAME, DB_PATH_Y_LOCATION);
        panel.add(mvDbPathLabel);

        mvDbPathInput = getInputTextField(DB_PATH_Y_LOCATION);
//        mvDbPathInput.addFocusListener(new JTextFieldHintListener("请填入歌曲数据库路径", mvDbPathInput));
        panel.add(mvDbPathInput);

        JButton mvDbSelectButton = getViewButton(DB_PATH_Y_LOCATION, mvDbPathInput);
        panel.add(mvDbSelectButton);

        // excel 路径
        JLabel excelPathLabel = getLabel(EXCEL_PATH_CHINESE_NAME, EXCEL_PATH_Y_LOCATION);
        panel.add(excelPathLabel);

//        excelPathInput.addFocusListener(new JTextFieldHintListener("请填入Excel文件路径", excelPathInput));
        excelPathInput = getInputTextField(EXCEL_PATH_Y_LOCATION);
        panel.add(excelPathInput);

        JButton excelPathButton = getViewButton(EXCEL_PATH_Y_LOCATION, excelPathInput);
        panel.add(excelPathButton);

        // 生成路径
        JLabel targetPathLabel = getLabel(TARGET_PATH_CHINESE_NAME, TARGET_PATH_Y_LOCATION);
        panel.add(targetPathLabel);


//        targetPathInput.addFocusListener(new JTextFieldHintListener("请填入文件生成路径", targetPathInput));
        targetPathInput = getInputTextField(TARGET_PATH_Y_LOCATION);
        panel.add(targetPathInput);

        JButton targetPathButton = getViewButton(TARGET_PATH_Y_LOCATION, targetPathInput);
        panel.add(targetPathButton);

        // 创建按钮
        JButton startButton = createStartButton();
        panel.add(startButton);

        //创建 console panel
        consolePanel = new ConsolePanel(
                new Rectangle(CONSOLE_PANEL_X_LOCATION, CONSOLE_PANEL_Y_LOCATION, CONSOLE_PANEL_WIDTH, CONSOLE_PANEL_HEIGHT)
        );
        panel.add(consolePanel.scrollPane);

    }

    private static JTextField getInputTextField(int inputYLocation) {
        JTextField mvDbPathInput = new JTextField(20);
        mvDbPathInput.setBounds(INPUT_X_LOCATION, inputYLocation, INPUT_WIDTH, INPUT_HEIGHT);
        return mvDbPathInput;
    }

    private static JLabel getLabel(String labelValue, int labelYLocation) {
        // 创建 JLabel
        /* 这个方法定义了组件的位置。
         * setBounds(x, y, width, height)
         * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
         */
        JLabel mvDbPathLabel = new JLabel(labelValue + ":");
        mvDbPathLabel.setBounds(LABEL_X_LOCATION, labelYLocation, LABEL_WIDTH, LABEL_HEIGHT);
        return mvDbPathLabel;
    }

    private static JButton getViewButton(int dbPathYLocation, JTextField mvDbPathInput) {
        JButton mvDbSelectButton = new JButton(VIEW_BUTTON_CHINESE_NAME);
        mvDbSelectButton.setBounds(BUTTON_X_LOCATION, dbPathYLocation, BUTTON_WIDTH, BUTTON_HEIGHT);
        mvDbSelectButton.addActionListener(new DirectoryChooserActionListener(mvDbPathInput));
        return mvDbSelectButton;
    }

    private static JButton createStartButton() {
        JButton startButton = new JButton("开始");
        startButton.setBounds(10, START_BUTTON_Y_LOCATION, 80, 25);
        startButton.setBackground(Color.BLUE);
        startButton.addMouseListener(new StartButtonMouseActionListener());
        return startButton;
    }

    public static void log(String s){
        consolePanel.addLog(s);
    }

}

