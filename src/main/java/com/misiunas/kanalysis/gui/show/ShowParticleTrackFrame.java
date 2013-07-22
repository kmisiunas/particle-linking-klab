package com.misiunas.kanalysis.gui.show;

import processing.core.PApplet;

import javax.swing.*;

/**
 * User: karolis@misiunas.com
 * Date: 22/07/2013
 * Time: 00:27
 */
public class ShowParticleTrackFrame {
    private JPanel panelMain;
    private JPanel processingPanel;
    private JLabel frameIndicatorLabel;

    void addProcessing(PApplet pa) {
        processingPanel.add(pa);
    }

    public void show() {
        JFrame frame = new JFrame("ShowParticleTrackFrame");
        frame.setContentPane(new ShowParticleTrackFrame().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
