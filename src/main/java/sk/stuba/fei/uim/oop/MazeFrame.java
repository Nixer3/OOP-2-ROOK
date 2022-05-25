package sk.stuba.fei.uim.oop;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * Narazil som na zaujimave koncepty v jazyku, tak som ich chcel implementovat, aj kde neboli nutne
 * stream(). a lambdy
 *  variabilny datovy <T,Y,P>
 *  a try/catch na vyhnutie sa dlhemu C-like kodu, co musi vsetko ifovat
 *  Pred pouzitim Enum claas som eventy switchoval stringami, ale pre "Game Won" som mal problem trackovat akym stylom som to napisal,(game_won,GAME_WON,GameWon..), tak som si stanovil na to enum, a takisto aj na smery lebo 4 stanovene smery, ...chybaju mi makra z C... a bez statickych claas aspon takto.
 *
 */

public class MazeFrame extends JFrame implements ActionListener, KeyListener {
    private int solvedMazes=0;
    private final int mazeSize = 13;
    private final int pixelSizeIni = 50;
    private final Dimension iniFrameSize = new Dimension(mazeSize* pixelSizeIni, mazeSize* pixelSizeIni +100);

    //all
    private final JPanel p = new JPanel();

    //center
    MazePanel mazePanel = new MazePanel(mazeSize, this);

    //south
    ControlPanel controlPanel = new ControlPanel(this);

    private void init(){

        // FRAME
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(false);
        this.setSize(iniFrameSize);

        this.setResizable(true);
        this.addKeyListener(this);
        this.setFocusable(true);
        // SOUTH
            //setup
            //adding

        //CENTER
            //setup
        mazePanel.setLayout(new GridLayout(mazeSize,mazeSize));
        mazePanel.setSize(this.getWidth()-20,(int)(this.getHeight()*0.9));
            //adding


        //all -adding
        p.setLayout(new BorderLayout());
        p.add(mazePanel,BorderLayout.CENTER);
        p.add(controlPanel,BorderLayout.SOUTH);
        this.add(p);
    }

    public MazeFrame() {
        super("Maze Game");
        init();
        mazePanel.reset();

        repaint();
        this.setVisible(true);
        //this.setResizable(false);

    }


    //catching moving from all over the place
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        //System.out.println(command);
        boolean doneCmd = false;
        try{//direction commands
            Directions dir = Directions.valueOf(command);
            mazePanel.move(dir);
            doneCmd=true;
        }catch (IllegalArgumentException ex){
            //not a direction
        }
        if(!doneCmd)
        try { //others action commands
            Actions act = Actions.valueOf(command);
            if (act == Actions.RESET) {
                mazePanel.reset();
            }
            if (act == Actions.GAME_WON) {
                solvedMazes++;
                controlPanel.getGamesWon().setText(solvedMazes + " games won");
                mazePanel.reset();
            }
        }catch (IllegalArgumentException ex){
            //not an action
        }

        mazePanel.repaint();
        controlPanel.repaint();


    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        ActionEvent act;
        //System.out.println("input ["+e.getKeyCode()+"]");
        switch (e.getKeyCode()){
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:   act = new ActionEvent(this,1,Directions.UP   .name());break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN: act = new ActionEvent(this,2,Directions.DOWN .name());break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT: act = new ActionEvent(this,3,Directions.LEFT .name());break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:act = new ActionEvent(this,4,Directions.RIGHT.name());break;
            case KeyEvent.VK_R:
            case KeyEvent.VK_ESCAPE:act = new ActionEvent(this,0,Actions.RESET.name());break;
            default:
                System.out.println("unexpected input ["+e.getKeyCode()+"]");
                return;
        }
        this.actionPerformed(act);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
