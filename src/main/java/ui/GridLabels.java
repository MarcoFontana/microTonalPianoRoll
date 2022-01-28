package ui;

import javax.swing.*;
import java.awt.*;

public class GridLabels extends JLabel {

    private final int nCells;
    private final boolean even;

    public GridLabels(int nCells, boolean even) {
        super();

        //setBackground(Color.GRAY);
        this.nCells = nCells;
        this.even = even;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();
        Color color1;
        if (even){
            color1 = UIManager.getColor ( "Panel.background" );
        }
        else {
            color1 = new Color(195, 195, 203);
        }
        g2d.setPaint(color1);
        g2d.fillRect(0, 0, w, h);

        g2d.setColor(Color.BLACK);
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
        g2d.setStroke(dashed);
        int cellLength = w /nCells;
        for (int i = 1; i <= nCells; i++){
            g2d.drawLine((cellLength * i), h, (cellLength * i), 0);
        }
        g2d.drawLine(0, 0, w,0);

        super.paintComponent(g);
    }
}
