package sk.stuba.fei.uim.oop;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class Maze {
    @Getter private final MazePixel mazeRoot;
    @Getter private MazePixel start;
    @Getter private MazePixel finish;



    private final Random random = new Random();
    @Getter int mazeSize;//lowest acceptable minimum
    MazePixel[][] maze;
    JFrame debugView;
    Map<Directions, Directions> opposites = new HashMap<>(){{
        put(Directions.UP    ,Directions.DOWN);
        put(Directions.DOWN  ,Directions.UP);
        put(Directions.LEFT  ,Directions.RIGHT);
        put(Directions.RIGHT ,Directions.LEFT);
    }};

    public Maze(int mazeSize) { //main building
        this.mazeSize = mazeSize;
        mazeRoot = new MazePixel();
        maze = new MazePixel[mazeSize][mazeSize];
        connectAllPixelsInMaze();
        debugView = new JFrame();
        JPanel gridPanel = new JPanel();
        GridLayout layout = new GridLayout(mazeSize,mazeSize);
        gridPanel.setLayout(layout);

        for(var p: getPixelsInOrder())
            gridPanel.add(p);

        makeSubMaze(mazeRoot);

        ArrayList<MazePixel> leavePoints = getPixelsInOrder().stream(). //gets only 1-way Pixels
                filter(node -> node.getNeighs().size()==1)
                .collect(Collectors.toCollection(ArrayList::new));

        if(leavePoints.size()<2){
            System.out.println("couldn't find start and end");
            return;
        }
        MazePixel start = getRandomItem(leavePoints);
        leavePoints.remove(start);
        MazePixel finish = getRandomItem(leavePoints);

        start.setType(PIXEL_TYPE.START);
        finish.setType(PIXEL_TYPE.FINISH);
        start.setStandingOn(true);
        this.start = start;
        this.finish = finish;
    }

    public <E> E getRandomItem(List<E> items) {
        return items.get(new Random().nextInt(items.size()));
    }

    private void connectAllPixelsInMaze() {
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                maze[i][j] = new MazePixel();
            }
        }
        maze[0][0] = mazeRoot;

         for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                try{ maze[i][j].addNearby(Directions.UP,   maze[i-1][j]);}catch(IndexOutOfBoundsException ignored){}
                try{ maze[i][j].addNearby(Directions.DOWN, maze[i+1][j]);}catch(IndexOutOfBoundsException ignored){}
                try{ maze[i][j].addNearby(Directions.LEFT, maze[i][j-1]);}catch(IndexOutOfBoundsException ignored){}
                try{ maze[i][j].addNearby(Directions.RIGHT,maze[i][j+1]);}catch(IndexOutOfBoundsException ignored){}
            }
         }
    }

    public void makeSubMaze(MazePixel p) {
        p.setVisited(true);
        MazePixel nextFriend = getRandElem(p.getUnvisitedDir());
        while(nextFriend != null){
            connectPixels(p,nextFriend);
            makeSubMaze(nextFriend);
            nextFriend = getRandElem(p.getUnvisitedDir());
        }
    }
    private void connectPixels(MazePixel p1, MazePixel p2) {
        if(p1==null || p2==null)return;

        Directions dir = getKeyByValue(p1.getNearby(), p2);
        p1.addNeigh(dir,p2);
        p2.addNeigh(opposites.get(dir),p1);

    }

    // removes element from map by value
    private <T, E> void removeByValue(Map<T, E> map, E value) {
        if(map.isEmpty()) return;
        T[] keys = (T[]) map.keySet().toArray();
        for(T t : keys){
            if(map.get(t) == value)
                map.remove(t);
        }
    }
    // gets key from map by value
    private <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    // removes element from map by value
    private <T, E> E getRandElem(Map<T,E> map){
        if(map.isEmpty())return null;
        T[] keys = (T[]) map.keySet().toArray();
        T randomKey = keys[random.nextInt(keys.length)];

        return map.get(randomKey);
    }

    // este jedna metoda a uz by som si extendoval HashMapu sam


    // returns maze[][] as ArrayList
    public ArrayList<MazePixel> getPixelsInOrder(){
        ArrayList<MazePixel> lst = new ArrayList<>();
        for(var p: maze){
            lst.addAll(Arrays.asList(p));
        }
        return lst;
    }

}
