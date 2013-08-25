package klab.gui.show.show;

import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.slider.WebSlider;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.misiunas.klab.gui.show.ShowParticleTrack;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: karolis@misiunas.com
 * Date: 22/07/2013
 * Time: 02:08
 */
public class ShowParticleTrackFrame {

    private ShowParticleTrack spt;

    private JPanel panel1;
    private WebToggleButton playWebToggleButton;
    private JPanel processingPanel;
    private JLabel labelFrameIndicator;
    private JPanel containerForOptions;
    private JPanel animationControls;
    private WebButton buttonZoomIn;
    private WebButton buttonZoomOut;
    private WebButton buttonResetView;
    private WebButton buttonSaveImage;
    private WebCheckBox tickShowTimesteps;
    private WebSlider sliderFrameSelector;
    private JLabel labelTime;

    public void resizeSPT(int x, int y) {
        spt.updateSize(x, y);
    }

    public void updateSPT() {
        spt.redraw();
    }

    protected void prepareSlider() {
        sliderFrameSelector.setMinimum((int) spt.range()._1().t()); // todo: only works well fro frames with separation 1
        sliderFrameSelector.setValue((int) spt.range()._1().t());
        sliderFrameSelector.setMaximum((int) spt.range()._2().t());
    }

    protected void prepareTimeLabel() {
        labelTime.setText("[" + spt.range()._1().t() + " : " + spt.range()._2().t() + "] " + spt.units().head());
    }

    protected void sliderAction() {
        int frame = sliderFrameSelector.getValue();
        int minFrame = sliderFrameSelector.getMinimum();
        int maxFrame = sliderFrameSelector.getMaximum();
        labelFrameIndicator.setText((frame-minFrame) + " / " + (maxFrame-minFrame)); //todo
        labelTime.setText(frame + " " + spt.units().head());
        spt.drawFrames(frame);
        updateSPT();
    }

    /**
     * add listners for elements here
     */
    public ShowParticleTrackFrame() {
        $$$setupUI$$$();
        buttonSaveImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spt.saveSnapshot();
            }
        });
        buttonZoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spt.zoom(1.5f);
                updateSPT();
            }
        });
        buttonZoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spt.zoom(1 / 1.5f);
                updateSPT();
            }
        });
        buttonResetView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spt.resetView();
                updateSPT();
            }
        });
        tickShowTimesteps.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playWebToggleButton.setEnabled(tickShowTimesteps.isSelected());
                sliderFrameSelector.setEnabled(tickShowTimesteps.isSelected());
                if (!tickShowTimesteps.isSelected()) {
                    playWebToggleButton.setSelected(false);
                    labelFrameIndicator.setText("All frames");
                    spt.drawAllFrames();
                    updateSPT();
                    prepareTimeLabel();
                } else {
                    prepareSlider();
                    sliderAction();
                }
            }
        });
        // Slider was moved?
        sliderFrameSelector.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sliderAction();
            }
        });
    }

    public void show(ShowParticleTrack s) {
        this.spt = s;
        JFrame frame = new JFrame("Show( ParticleTrack )");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        processingPanel.add(spt);
        spt.init();
        frame.setVisible(true);
        frame.getContentPane().addHierarchyBoundsListener(new HierarchyBoundsListener() {
            @Override
            public void ancestorMoved(HierarchyEvent e) {
            }

            @Override
            public void ancestorResized(HierarchyEvent e) {
                resizeSPT((int) processingPanel.getSize().getWidth() - 10, (int) processingPanel.getSize().getHeight() - 10);
            }
        });
        processingPanel.revalidate();
        processingPanel.repaint();
        updateSPT();
        prepareTimeLabel();
        frame.setTitle("Show( " + spt.toString() + " )");
    }

    private void createUIComponents() {
        // place custom component creation code here
        processingPanel = new JPanel();
        labelTime = new JLabel();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:p:grow", "fill:p:grow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow"));
        processingPanel.setPreferredSize(new Dimension(600, 400));
        CellConstraints cc = new CellConstraints();
        panel1.add(processingPanel, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.FILL));
        containerForOptions = new JPanel();
        containerForOptions.setLayout(new FormLayout("fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:grow", "center:max(d;4px):noGrow"));
        panel1.add(containerForOptions, cc.xy(1, 3));
        buttonZoomIn = new WebButton();
        buttonZoomIn.setIcon(new ImageIcon(getClass().getResource("/icons/search.png")));
        buttonZoomIn.setText("Zoom In");
        buttonZoomIn.setToolTipText("Zooms into the image by 1.5");
        buttonZoomIn.setUndecorated(false);
        containerForOptions.add(buttonZoomIn, cc.xy(1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        buttonZoomOut = new WebButton();
        buttonZoomOut.setIcon(new ImageIcon(getClass().getResource("/icons/search.png")));
        buttonZoomOut.setText("Zoom Out");
        buttonZoomOut.setToolTipText("Image will zoom out by ratio 1.5 until it is full screen");
        containerForOptions.add(buttonZoomOut, cc.xy(3, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        buttonResetView = new WebButton();
        buttonResetView.setIcon(new ImageIcon(getClass().getResource("/icons/resize-shrink.png")));
        buttonResetView.setText("Reset View");
        buttonResetView.setToolTipText("Resets position and zoom level");
        containerForOptions.add(buttonResetView, cc.xy(5, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        buttonSaveImage = new WebButton();
        buttonSaveImage.setIcon(new ImageIcon(getClass().getResource("/icons/camera.png")));
        buttonSaveImage.setText("Save Image");
        buttonSaveImage.setToolTipText("Saves image to a file");
        containerForOptions.add(buttonSaveImage, cc.xy(7, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        tickShowTimesteps = new WebCheckBox();
        tickShowTimesteps.setText("Show Timesteps");
        tickShowTimesteps.setToolTipText("Show 20 last positions from selected frame");
        containerForOptions.add(tickShowTimesteps, cc.xy(9, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        containerForOptions.add(spacer1, cc.xy(11, 1, CellConstraints.FILL, CellConstraints.DEFAULT));
        animationControls = new JPanel();
        animationControls.setLayout(new FormLayout("fill:m:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:5dlu:noGrow,center:max(p;100px):grow(10.0),left:4dlu:noGrow,fill:max(m;4px):noGrow", "center:d:grow,top:3dlu:noGrow,center:max(d;4px):noGrow"));
        panel1.add(animationControls, cc.xy(1, 5));
        playWebToggleButton = new WebToggleButton();
        playWebToggleButton.setBorderPainted(false);
        playWebToggleButton.setDoubleBuffered(false);
        playWebToggleButton.setDrawBottom(true);
        playWebToggleButton.setDrawRight(true);
        playWebToggleButton.setDrawRightLine(false);
        playWebToggleButton.setDrawTopLine(false);
        playWebToggleButton.setEnabled(false);
        playWebToggleButton.setFocusTraversalPolicyProvider(false);
        playWebToggleButton.setFocusable(true);
        playWebToggleButton.setHideActionText(false);
        playWebToggleButton.setIcon(new ImageIcon(getClass().getResource("/icons/iMac.png")));
        playWebToggleButton.setOpaque(false);
        playWebToggleButton.setRolloverDarkBorderOnly(false);
        playWebToggleButton.setRolloverDecoratedOnly(false);
        playWebToggleButton.setRolloverShadeOnly(false);
        playWebToggleButton.setRolloverShine(false);
        playWebToggleButton.setSelected(false);
        playWebToggleButton.setShowDisabledShade(false);
        playWebToggleButton.setText("Play");
        playWebToggleButton.setToolTipText("Toggle animation");
        playWebToggleButton.setUndecorated(false);
        playWebToggleButton.setVisible(true);
        playWebToggleButton.putClientProperty("html.disable", Boolean.FALSE);
        animationControls.add(playWebToggleButton, cc.xy(1, 3, CellConstraints.LEFT, CellConstraints.DEFAULT));
        sliderFrameSelector = new WebSlider();
        sliderFrameSelector.setEnabled(false);
        sliderFrameSelector.setMinimum(1);
        sliderFrameSelector.setValue(1);
        animationControls.add(sliderFrameSelector, cc.xy(5, 3, CellConstraints.FILL, CellConstraints.CENTER));
        labelFrameIndicator = new JLabel();
        labelFrameIndicator.setText("All frames");
        labelFrameIndicator.setToolTipText("Frame number / Total frames");
        animationControls.add(labelFrameIndicator, cc.xy(7, 3));
        labelTime.setText("[interval]");
        labelTime.setToolTipText("The current frame time stamp");
        animationControls.add(labelTime, cc.xy(3, 3));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
