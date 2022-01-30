package ui;

import javax.swing.*;
import java.awt.*;

public class PianoRoll extends JLayeredPane {

    private final int maxFreq;
    private final int minFreq;
    private final int nRows;
    private final int nCols;

    public PianoRoll(int maxFreq, int minFreq, int nRows, int nCols) {
        super();
        this.maxFreq = maxFreq;
        this.minFreq = minFreq;
        this.nRows = nRows;
        this.nCols = nCols;
        setLayout(new GridBagLayout());
        createGrid();
    }

    private void createGrid() {

        int segments = 4;
        double freqStep = (double) (maxFreq - minFreq) / (nRows - 1);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double noteWidth = screenSize.getWidth() / 20;
        double noteHeight = noteWidth / 4;

        Dimension rollLength = new Dimension((int)((nCols * noteWidth) + (noteWidth / 5)), (int)((nRows * noteHeight) + (noteHeight)));

        setPreferredSize(rollLength);

        GridBagConstraints cellConstraints;
        cellConstraints = new GridBagConstraints();

        cellConstraints.weightx = 4;
        cellConstraints.weighty = 1;

        cellConstraints.gridx = 0;

        cellConstraints.gridheight = 1;
        cellConstraints.gridy = 0;
        for (int col = 1; col < nCols*segments; col++) {

            JSeparator seg = new JSeparator();
            cellConstraints.gridx = col;

            add(seg, cellConstraints, 1);

        }
        cellConstraints.gridx = 0;
        cellConstraints.fill =  GridBagConstraints.BOTH;
        for (int row = 0; row < nRows; row++) {
            JLabel seg = new JLabel();
            seg.setHorizontalAlignment(SwingConstants.CENTER);
            seg.setBorder(BorderFactory.createLineBorder(Color.red));
            seg.setText(String.valueOf((double) Math.round((maxFreq - (freqStep * row)) * 100) / 100));
            cellConstraints.gridy = row;

            add(seg,cellConstraints, 1);
        }

        cellConstraints.gridx = 1;
        cellConstraints.gridwidth = (nCols*segments) - 1;
        for (int row = 0; row < nRows; row++) {
            GridLabels backgroundLabel = new GridLabels(cellConstraints.gridwidth, row % 2 == 0, row == (nRows-1));
            cellConstraints.gridy = row;
            add(backgroundLabel,cellConstraints, 1);
        }


    }

}
