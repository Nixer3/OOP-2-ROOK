package sk.stuba.fei.uim.oop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MazePanel extends JPanel implements MouseListener {

    int mazeSize;
    MazePixel standingOn;
    ActionListener actionControl;

    Maze maze;
    public MazePanel(int mazeSize, ActionListener mainControl) {
        super();
        this.actionControl = mainControl;
        this.mazeSize = mazeSize;
        reset();
        this.setLayout(new GridLayout(mazeSize,mazeSize));
        this.setBorder(BorderFactory.createLineBorder(Color.black));

    }

    @Override
    public void paint(Graphics g) {
         super.paint(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }


    public void move(Directions direction) {
        if(standingOn == maze.getFinish()) return;//can't move from finish

        MazePixel nextPos = standingOn.getNeighs().get(direction);
        if(nextPos != null) {
            standingOn.setStandingOn(false);
            nextPos.setStandingOn(true);
            standingOn = nextPos;
        }

        if(standingOn == maze.getFinish()) {
            actionControl.actionPerformed(new ActionEvent(this, 5, "GAME_WON"));
        }
    }
    public void reset(){
        this.removeAll();
        maze = new Maze(this.mazeSize);
        for(var p : maze.getPixelsInOrder()){
            p.addMouseListener(this);
            add(p);
        }

        this.standingOn = maze.getStart();
        this.revalidate();
    }

    private boolean someoneIsMarked;
    private MazePixel lastStanding;
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        MazePixel pixel = (MazePixel)e.getSource();
        boolean imMarked = !pixel.getMarkedPaths().isEmpty();

        if( pixel.isStandingOn() && !someoneIsMarked ){
            //clicked on player, and no one is marked
            lastStanding = pixel;
            pixel.setStandingOn(false);
            pixel.markRookiePath(true);
        }

        else if(imMarked){
            lastStanding.markRookiePath(false);
            pixel.setStandingOn(true);
            standingOn = pixel;
            someoneIsMarked = false;
            if(pixel == maze.getFinish())
                actionControl.actionPerformed(new ActionEvent(this,5,Actions.GAME_WON.toString()));
        }
        revalidate();
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        MazePixel pixel = (MazePixel)e.getSource();
        pixel.setHover(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        MazePixel pixel = (MazePixel)e.getSource();
        pixel.setHover(false);
    }
}
