package ui;

import Score.Note;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class NoteLabel extends JLabel {

    private final Note note;
    private final int time;

    public NoteLabel(Note note, int time) {
        super();
        this.note = note;
        this.time = time;
    }

    public Note getNote() {
        return note;
    }

    public int getTime() {
        return time;
    }

    @Override
    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints( new RenderingHints(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON));

        int w = getWidth();
        int h = getHeight();
        Color baseColor = new Color(24, 80, 201);
        /*Image baseImg = getToolkit().getImage("/img/Notes.png");
        BufferedImage bufferImg = new BufferedImage( w, h, BufferedImage.TYPE_INT_RGB);
        bufferImg.createGraphics().drawImage(baseImg,0,0,this);
        Rectangle2D rect = new Rectangle2D.Float(0,0,w, h);
        g2d.setPaint(new TexturePaint(bufferImg, rect));
        g2d.fill(new Rectangle2D.Float(0,0,w, h));*/

        Color shadowColor = new Color(0, 0, 0, 60);
        g2d.setColor(shadowColor);
        g2d.fillRoundRect(0, 0, w, h, w/10,h/2);


        g2d.setPaint(baseColor);
        g2d.fillRoundRect(1,1, w-2, h-2, w/10, h/2);

        super.paintComponent(g);
    }
}
