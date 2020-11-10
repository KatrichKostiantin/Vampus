
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class Board extends JPanel implements ActionListener {
    static final int BLOCK_SIZE = 64;
    private static final int DEFAULT_COUNT_OF_HOLES = 2;
    private int curNumHoles = 0;
    private int curNumWalls = 5;
    public Timer timer;


    public short[][] screenData = {
            {0, 0, 0, 0, 0},
            {0, 1, 1, 0, 0},
            {0, 1, 0, 9, 0},
            {0, 0, 0, 1, 1},
            {8, 0, 0, 0, 0}
    };
    public Cell[][] cells = new Cell[n][m];
    private static int n = 5;
    private static int m = 5;
    private Random random = new Random();
    private Dimension d;
    private Color mazeColor;

    private static Image vampus, gold, wind, smell, hole;
    private int level = 0;
    private int scope = 0;

    private Point Agent_START = new Point(0, n-1);

    public Board() {
        initVariables();
        fillScreenDataSTR();
        generateLevelData();
        initBoard();
    }

    private void fillScreenDataSTR() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++){
                cells[i][j] = new Cell(new ArrayList<Value>(),new Point(j, i));
                if(screenData[i][j]==9) cells[i][j].value.add(Value.Glitter);
                else  if(screenData[i][j]==1) cells[i][j].value.add(Value.Wall);
            }

    }

    private void generateLevelData() {
        Point p;
        // holes
        curNumHoles = Math.round((n * m - 6) * 2 / 10);
        for (int i = 0; i < curNumHoles; i++) {
            p = searchEmptyPoint(screenData);
            screenData[p.y][p.x] += 2;
            cells[p.y][p.x].value.add(Value.Hole);
            if(p.y+1<n) if(isEmptyForBreeze(new Point(p.x,p.y+1))) cells[p.y+1][p.x].value.add(Value.Breeze);
            if(p.x+1<m) if(isEmptyForBreeze(new Point(p.x+1,p.y)))cells[p.y][p.x+1].value.add(Value.Breeze);
            if(p.y-1>=0) if(isEmptyForBreeze(new Point(p.x,p.y-1))) cells[p.y-1][p.x].value.add(Value.Breeze);
            if(p.x-1>=0) if(isEmptyForBreeze(new Point(p.x-1,p.y))) cells[p.y][p.x-1].value.add(Value.Breeze);
        }

        // vampus
        p = searchEmptyPoint(screenData);
        screenData[p.y][p.x] = 3;
        cells[p.y][p.x].value.add(Value.Vampus);
        if(p.y+1<n) if(isEmptyForStench(new Point(p.x,p.y+1))) cells[p.y+1][p.x].value.add(Value.Stench);
        if(p.x+1<m) if(isEmptyForStench(new Point(p.x+1,p.y))) cells[p.y][p.x+1].value.add(Value.Stench);
        if(p.y-1>=0) if(isEmptyForStench(new Point(p.x,p.y-1))) cells[p.y-1][p.x].value.add(Value.Stench);
        if(p.x-1>=0) if(isEmptyForStench(new Point(p.x-1,p.y))) cells[p.y][p.x-1].value.add(Value.Stench);
    }

    private boolean isEmptyForBreeze(Point p){
        if(cells[p.y][p.x].value.contains(Value.Hole)) return false;
       if(cells[p.y][p.x].value.contains(Value.Breeze)) return false;
       if(cells[p.y][p.x].value.contains(Value.Wall)) return false;
       if(cells[p.y][p.x].value.contains(Value.Glitter)) return false;
       return true;
    }

    private boolean isEmptyForStench(Point p){
        if(cells[p.y][p.x].value.contains(Value.Hole)) return false;
        if(cells[p.y][p.x].value.contains(Value.Stench)) return false;
        if(cells[p.y][p.x].value.contains(Value.Wall)) return false;
        if(cells[p.y][p.x].value.contains(Value.Glitter)) return false;
        return true;
    }
    private void initBoard() {
        setFocusable(true);
        setBackground(Color.darkGray);
        initImages();
    }

    private void initImages() {
        vampus = new ImageIcon("images/vampus.png").getImage();
        gold = new ImageIcon("images/gold.png").getImage();
        hole = new ImageIcon("images/hole.png").getImage();
        smell = new ImageIcon("images/smell.png").getImage();
        wind = new ImageIcon("images/wind.png").getImage();
    }

    private void initVariables() {
        mazeColor = new Color(5, 100, 5);
        d = new Dimension(400, 400);

        timer = new Timer(20, this);
        timer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
    }


    private void drawMaze(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(mazeColor);
        g2d.drawRect(0, 0, screenData[0].length * BLOCK_SIZE, screenData.length * BLOCK_SIZE);
        for (int i = 0; i < screenData.length; i++) {
            for (int j = 0; j < screenData[i].length; j++) {
                int x = j * BLOCK_SIZE;
                int y = i * BLOCK_SIZE;
                g2d.setColor(mazeColor);

                Color roads = new Color(189, 199, 206);
                g2d.setColor(roads);
                g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                g2d.setColor(mazeColor);


                if (cells[i][j].value.contains(Value.Wall)) {
                    Color walls = new Color(49, 72, 79);
                    g2d.setColor(walls);
                    g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                }

                if (cells[i][j].value.contains(Value.Hole)) {
                    g2d.drawImage(hole, x, y, BLOCK_SIZE, BLOCK_SIZE, this);
                }

                if (cells[i][j].value.contains(Value.Breeze)) {
                    g2d.drawImage(wind, x, y, BLOCK_SIZE, BLOCK_SIZE, this);
                }

                if (cells[i][j].value.contains(Value.Stench)) {
                    g2d.drawImage(smell, x, y, BLOCK_SIZE, BLOCK_SIZE, this);
                }


                if (cells[i][j].value.contains(Value.Glitter)) {
                    g2d.drawImage(gold, x, y, BLOCK_SIZE, BLOCK_SIZE, this);
                }

                if (cells[i][j].value.contains(Value.Vampus)) {
                    g2d.drawImage(vampus, x, y, BLOCK_SIZE, BLOCK_SIZE, this);
                }

            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }


    private void doDrawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);

        drawMaze(g2d);
        //  pacman.step(g2d);
        // ghosts.forEach(ghost -> ghost.step(g2d));
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }


    public void stop() {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    private Point searchEmptyPoint(short[][] levelData) {
        Point randomPoint;
        do {
            randomPoint = new Point(random.nextInt(5), random.nextInt(5));
            if (levelData[randomPoint.y][randomPoint.x] != 0 && levelData[randomPoint.y][randomPoint.x] != 9)
                continue;
            break;
        } while (true);
        return randomPoint;
    }

    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_PAUSE || key == KeyEvent.VK_ESCAPE)
                stop();
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }
}