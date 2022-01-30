package ui;

import javax.swing.*;
import java.awt.*;

public class GridLabels extends JLabel {

    private final int nCells;
    private final boolean even;
    private final boolean isLast;

    public GridLabels(int nCells, boolean even, boolean isLast) {
        super();
        this.nCells = nCells;
        this.even = even;
        this.isLast = isLast;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int w = getWidth();
        int h = getHeight();
        Color backgroundColor;
        if (even){
            backgroundColor = UIManager.getColor ( "Panel.background" );
        }
        else {
            backgroundColor = new Color(195, 195, 203);
        }
        g2d.setPaint(backgroundColor);
        g2d.fillRect(0, 0, w, h);

        g2d.setColor(Color.BLACK);
        Stroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
        Stroke baldStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
        int cellLength = w /nCells;
        for (int i = 1; i <= nCells; i++){
            if (i % 4 > 0){
                g2d.setStroke(stroke);
            }
            else {
                g2d.setStroke(baldStroke);
            }
            g2d.drawLine((cellLength * i), h, (cellLength * i), 0);
        }
        g2d.drawLine(0, 0, w,0);
        if (isLast){
            g2d.drawLine(0, h - 1, w, h - 1);
        }
        super.paintComponent(g);
    }
}
