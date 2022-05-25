package sk.stuba.fei.uim.oop;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;


public class MazePixel extends JButton {

    @Getter @Setter private PIXEL_TYPE type;
    @Getter @Setter private boolean standingOn;
    @Getter @Setter private boolean visited;
    @Getter @Setter private boolean hover;
    @Getter private final Map<Directions,MazePixel> nearby; // points to nearby pixels
    @Getter private final Map<Directions,MazePixel> neighs;
    @Getter private final HashSet<Directions> markedPaths;
    private final Map<Directions, Directions> opposites = new HashMap<>(){{
        put(Directions.UP    ,Directions.DOWN);
        put(Directions.DOWN  ,Directions.UP);
        put(Directions.LEFT  ,Directions.RIGHT);
        put(Directions.RIGHT ,Directions.LEFT);
    }};

    Color siluetCol = new Color(0, 20, 128, 255);

    Color background =  new Color(20, 144, 200, 255);//light blue
    Color platformCol = new Color(255,200,30,255); //light orange to yellow

    Color startPlatformCol = new Color(50, 200, 50, 255); //green
    Color endPlatFormCol = new Color(255, 30, 180, 218); //pink

    Color path1 = new Color(240,150,30,255); //orange
    Color path2 =siluetCol;//new Color(0,128,62,255); //green

    private final float platformRatio = 1.0f/2;
    private final float pathRatio = 1.0f/8;
    private final float siluetRatio = 2.0f/7;

    public MazePixel() {
        super();
        this.setFocusable(false);
        this.setBorderPainted(false);
        this.setFocusPainted(false);
        this.setContentAreaFilled(false);

        this.type = PIXEL_TYPE.PATH;
        neighs = new HashMap<>();
        nearby =new HashMap<>();
        markedPaths =new HashSet<>();
    }

    public Map<Directions,MazePixel> getUnvisitedDir(){
        Map<Directions,MazePixel> out;
        out = nearby.entrySet().stream()
                .filter(map -> !map.getValue().isVisited())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return out;
    }

    /**
     *@return itself, for chaining
     */
    public MazePixel addNearby(Directions dir, MazePixel neighbour) {
        this.nearby.put(dir,neighbour);
        return this;
    }

    public void addNeigh(Directions dir, MazePixel neighbour) {
        this.neighs.put(dir,neighbour);
    }
    public MazePixel getNeigh(Directions dir) {
        return neighs.get(dir);
    }

    public boolean markPath(Directions dir) {
        if(!neighs.containsKey(dir))return false;
        this.markedPaths.add(dir);
        return true;
    }
    public boolean unmarkPath(Directions dir) {
        if(!neighs.containsKey(dir)
        || !markedPaths.contains(dir))
            return false;

        this.markedPaths.remove(dir);
        return true;
    }


    public void markRookiePath(boolean mode){
        for(var entry : neighs.entrySet()){
            //markRookRec(entry.getKey(),this,mode);
            this.markRookRec(entry.getKey(),mode);
        }
    }

    private void markRookRec(Directions dir,boolean mode){
        MazePixel tmp = this.getNeighs().get(dir);
        if( tmp != null){
            if(mode){
                this.markPath(dir);
                tmp.markPath(opposites.get(dir));}
            else{
                this.unmarkPath(dir);
                tmp.unmarkPath(opposites.get(dir));}
            tmp.markRookRec(dir, mode);
        }
    }

    private int width;
    private int height;
    @Override
    public void paint(Graphics g){
        g.setColor(background); //background
        width = this.getSize().width;
        height = this.getSize().height;

        g.fillRect(0,0,width, height);

        drawOuterCircle(g);
        drawPathToFriends(g);
        drawInnerCircle(g);
        if(standingOn ||
                (!markedPaths.isEmpty() && hover)) {
            drawSiluet(g);
        }
    }

    private void drawSiluet(Graphics g) {
        Color tmpSil = new Color(siluetCol.getRGB());
        if(hover) {
            tmpSil = new Color(tmpSil.getRed(), tmpSil.getGreen(), tmpSil.getBlue(), tmpSil.getAlpha() / 2);
        }
        g.setColor(tmpSil);
        //head
        g.fillOval((int)  (width/2-siluetRatio*width/2),//x = mid-halfHeadWidth
                   (int)  (0),                      //y = from TOP
                   (int)  (width*siluetRatio),
                   (int)  (height*siluetRatio));
        //body
        float bodyRatio = 1.5f*siluetRatio;
        g.fillOval((int) (width/2-bodyRatio*width/2),//x = mid-halfBodyWidth
                   (int)  (height*platformRatio/2),     //y = platform beginning
                   (int)  (width*bodyRatio),
                   (int)  (height*bodyRatio));

    }

    private void drawPathToFriends(Graphics g) {
        Directions dir;

        dir = Directions.LEFT;
        if(getNeigh(dir)!=null){
            if (markedPaths.contains(dir)) {
                g.setColor(path2);
            } else {
                g.setColor(path1);
            }
            g.fillRect(0,                           //x = beginning
                    (int) (height/2-pathRatio*height/2),//y = mid-halfPathWidth
                    (int) (width/2),                 //width = from left to middle
                    (int) (height*pathRatio));        //height = fraction of size
        }
        dir = Directions.RIGHT;
        if(getNeigh(dir)!=null){
            if (markedPaths.contains(dir)) {
                g.setColor(path2);
            } else {
                g.setColor(path1);
            }

            g.fillRect((int) (width/2),                 //x = from MIDDLE
                       (int) (height/2-pathRatio*height/2),//y = mid-halfPathWidth
                       (int) (width/2),                 //width = from MIDDLE to RIGHT
                       (int) (height*pathRatio));        //height = fraction of size
        }
        dir = Directions.DOWN;
        if(getNeigh(dir)!=null){
            if (markedPaths.contains(dir)) {
                g.setColor(path2);
            } else {
                g.setColor(path1);
            }

            g.fillRect((int) (width/2-pathRatio*width/2),//x = mid-halfPathWidth
                       (int) (height/2),                 //y = from MIDDLE
                       (int) (width*pathRatio),         //width = fraction of size
                       (int) (height/2));                //height = from middle to BOTTOM
        }
        dir = Directions.UP;
        if(getNeigh(dir)!=null){
            if (markedPaths.contains(dir)) {
                g.setColor(path2);
            } else {
                g.setColor(path1);
            }

            g.fillRect((int) (width/2-pathRatio*width/2),//x = mid-halfPathWidth
                       (int) (0),                      //y = from TOP
                       (int) (width*pathRatio),         //width = fraction of size
                       (int) (height/2));                //height = from MIDDLE to TOP
        }
    }
    private void drawOuterCircle(Graphics g){
        switch (type){
            case START: g.setColor(startPlatformCol);break;
            case FINISH: g.setColor(endPlatFormCol);break;
            case PATH: g.setColor(platformCol); break;
        }//base platform
        g.fillOval((int) (width*platformRatio/2),
                (int) (height*platformRatio/2),
                (int) (width*platformRatio),
                (int) (height*platformRatio));

    }
    private void drawInnerCircle(Graphics g){
        if(markedPaths.isEmpty())g.setColor(path1);  //inner circle
        else g.setColor(path2);  //inner circle

        g.fillOval(
                (int) (width*platformRatio/2+width*platformRatio/4),
                (int) (height*platformRatio/2+height*platformRatio/4),
                (int) (width*platformRatio/2),
                (int) (height*platformRatio/2));
    }
}

