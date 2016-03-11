package com.nottesla.learncv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by tesla on 3/1/16.
 */
public class ProgressBar extends JPanel implements ActionListener, PropertyChangeListener {
    private JProgressBar progressBar;
    private JButton startButton;
    private JTextArea taskOutput;

    public boolean isRun() {
        return run;
    }

    private boolean run;
    private int total;
    private int progress;
    private JFrame frame;

    public ProgressBar(int total) {
        super(new BorderLayout());
        this.run = false;
        this.total = total;
        this.progress = 0;


        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);

        progressBar = new JProgressBar(0, this.total);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(progressBar);

        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        frame = new JFrame("Vision Processor");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JComponent newContentPane = this;
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);
        frame.pack();
        frame.setVisible(true);

    }

    public void setProgress(int progress) {
        System.out.println(progress);
        progressBar.setValue(progress);
        int percent = (100 * this.progress) / this.total;
        taskOutput.append(String.format("Completed %d%% of processing\n", percent));
    }

    public void actionPerformed(ActionEvent evt) {
        this.run = true;
        startButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }


}
