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
    private static Image vampus, vampusDead, scream, gold, wind, smell, hole;
    public Timer timer;
    public short[][] screenData = {
            {0, 0, 0, 0, 0},
            {0, 1, 1, 0, 0},
            {0, 1, 0, 9, 0},
            {0, 0, 0, 1, 1},
            {8, 0, 0, 0, 0}
    };
    private final Point AGENT_START = new Point(0, screenData.length - 1);
    public Cell[][] cells = new Cell[screenData.length][screenData[0].length];
    private Random random = new Random();
    private Dimension d;
    private Color mazeColor;
    private Agent agent;

    public Board() {
        initVariables();
        fillScreenDataSTR();
        generateLevelData();
        initBoard();

        agent = new Agent(this, AGENT_START);
    }

    private void fillScreenDataSTR() {
        for (int i = 0; i < screenData.length; i++)
            for (int j = 0; j < screenData[i].length; j++) {
                cells[i][j] = new Cell(new ArrayList<>(), new Point(j, i));
                if (screenData[i][j] == 9) cells[i][j].value.add(Value.Glitter);
                else if (screenData[i][j] == 1) cells[i][j].value.add(Value.Wall);
            }

    }

    private void generateLevelData() {
        Point p;
        // holes
        for (int i = 0; i < DEFAULT_COUNT_OF_HOLES; i++) {
            p = searchEmptyPoint(screenData);
            screenData[p.y][p.x] += 2;
            cells[p.y][p.x].value.add(Value.Hole);
            createBreeze(p.x + 1, p.y);
            createBreeze(p.x - 1, p.y);
            createBreeze(p.x, p.y + 1);
            createBreeze(p.x, p.y - 1);
        }

        // vampus
        p = searchEmptyPoint(screenData);
        screenData[p.y][p.x] = 3;
        cells[p.y][p.x].value.add(Value.Vampus);
        createStench(p.x + 1, p.y);
        createStench(p.x - 1, p.y);
        createStench(p.x, p.y + 1);
        createStench(p.x, p.y - 1);
    }

    private void createBreeze(int x, int y) {
        if (y >= 0 && y < screenData.length && x < screenData[0].length && x >= 0 && isEmptyForBreeze(x, y))
            cells[y][x].value.add(Value.Breeze);
    }

    private void createStench(int x, int y) {
        if (y >= 0 && y < screenData.length && x < screenData[0].length && x >= 0 && isEmptyForStench(x, y))
            cells[y][x].value.add(Value.Stench);
    }

    private boolean isEmptyForBreeze(int x, int y) {
        if (cells[y][x].value.contains(Value.Hole)) return false;
        if (cells[y][x].value.contains(Value.Breeze)) return false;
        if (cells[y][x].value.contains(Value.Wall)) return false;
        return !cells[y][x].value.contains(Value.Glitter);
    }

    private boolean isEmptyForStench(int x, int y) {
        if (cells[y][x].value.contains(Value.Hole)) return false;
        if (cells[y][x].value.contains(Value.Stench)) return false;
        if (cells[y][x].value.contains(Value.Wall)) return false;
        return !cells[y][x].value.contains(Value.Glitter);
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
        cells = new Cell[screenData.length][screenData[0].length];
        mazeColor = new Color(5, 100, 5);
        d = new Dimension(400, 400);
        timer = new Timer(200, this);
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
                    if (agent.isVampusDead)
                        g2d.drawImage(vampusDead, x, y, BLOCK_SIZE, BLOCK_SIZE, this);
                    else
                        g2d.drawImage(vampus, x, y, BLOCK_SIZE, BLOCK_SIZE, this);
                }

                if (cells[i][j].value.contains(Value.Scream)) {
                    g2d.drawImage(scream, x, y, BLOCK_SIZE, BLOCK_SIZE, this);
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
        agent.step(g2d);

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
            randomPoint = new Point(random.nextInt(screenData[0].length), random.nextInt(screenData.length));
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