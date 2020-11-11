import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class Agent {
    private static final int ANIMATION_STEPS = 5;
    private static Image agentR, agentL;
    public int agentX, agentY;
    public boolean isVampusDead = false;
    Board board;
    int additionAnimationY = 0, additionAnimationX = 0;
    int animationCount = 0;
    private KnowledgeBase knowledgeBase;
    private int directionAgentX;
    private ArrayList<Point> listInfo;
    private Deque<Point> pointDeque = new ArrayDeque<>();

    public Agent(Board board, Point start) {
        this.board = board;
        knowledgeBase = new KnowledgeBase(board);
        initAgentImages();
        listInfo = new ArrayList<>();
        listInfo.add(start);
        moveAgentTo(start.y, start.x);
    }

    void initAgentImages() {
        agentR = new ImageIcon("images/spyR.png").getImage();
        agentL = new ImageIcon("images/spyL.png").getImage();
    }

    public void step(Graphics2D g2d) {
        drawAgent(g2d);
        animationCount %= ANIMATION_STEPS;
        if (animationCount++ == 0) {
            tellInformation();
            addPointsToQueue();
            askInformation();
            if (!isVampusDead) checkVampusPosition();
            animationMoveAgent();
        }
    }

    private void checkVampusPosition() {
        for (Point point : pointDeque)
            if (knowledgeBase.sureAskInformation("V" + point.x + "" + point.y))
                killVampus(point);
    }

    private void killVampus(Point pointVampus) {
        for (Point pointAgent : listInfo) {
            if (pointAgent.x == pointVampus.x || pointAgent.y == pointVampus.y) {
                moveAgentTo(pointAgent.y, pointAgent.x);
                shoot(pointVampus);
                break;
            }
        }
    }

    private void shoot(Point pointVampus) {
        if (pointVampus.x == agentX || pointVampus.y == agentY) {
            for(Cell[] cellArr: board.cells)
                for(Cell cell: cellArr)
                    cell.value.add(Value.Scream);
        }
    }

    private void askInformation() {
        Deque<Point> newPointDeque = new ArrayDeque<>();

        for (Point point : pointDeque) {
            if (knowledgeBase.sureAskInformation("H" + point.x + "" + point.y) ||
                    (!isVampusDead && knowledgeBase.askInformation("V" + point.x + "" + point.y)))
                newPointDeque.addLast(point);
            else
                newPointDeque.addFirst(point);
        }

        /*for (Point point : pointDeque) {
            if (knowledgeBase.sureAskInformation("G" + point.x + "" + point.y))
                newPointDeque.addFirst(point);
        }*/
        System.out.print("List before");
        pointDeque.forEach(System.out::print);
        System.out.print("\nList after");
        newPointDeque.forEach(System.out::print);
        System.out.print("\n\n");

        pointDeque = newPointDeque;
    }

    private boolean askAboutVampus(Point point) {
        boolean flag = knowledgeBase.sureAskInformation("V" + point.x + "" + point.y);
        return flag;
    }

    private void addPointsToQueue() {
        if (checkPosition(agentX + 1, agentY, board.screenData)) {
            Point p = new Point(agentX + 1, agentY);
            pointDeque.add(p);
            listInfo.add(p);
        }
        if (checkPosition(agentX - 1, agentY, board.screenData)) {
            Point p = new Point(agentX - 1, agentY);
            pointDeque.add(p);
            listInfo.add(p);
        }
        if (checkPosition(agentX, agentY + 1, board.screenData)) {
            Point p = new Point(agentX, agentY + 1);
            pointDeque.add(p);
            listInfo.add(p);
        }
        if (checkPosition(agentX, agentY - 1, board.screenData)) {
            Point p = new Point(agentX, agentY - 1);
            pointDeque.add(p);
            listInfo.add(p);
        }
    }

    private boolean checkPosition(int x, int y, short[][] screenData) {
        return (x < screenData.length && x >= 0 && y < screenData.length && y >= 0 &&
                screenData[y][x] != 1 && !listInfo.contains(new Point(x, y)));
    }

    private void tellInformation() {
        ArrayList<Value> values = board.cells[agentY][agentX].value;

        if (values.contains(Value.Breeze)) knowledgeBase.tellInformation("B" + agentX + "" + agentY);
        else knowledgeBase.tellInformation("~B" + agentX + "" + agentY);

        if (values.contains(Value.Stench)) knowledgeBase.tellInformation("S" + agentX + "" + agentY);
        else knowledgeBase.tellInformation("~S" + agentX + "" + agentY);

        if (values.contains(Value.Glitter)) knowledgeBase.tellInformation("G" + agentX + "" + agentY);
        else knowledgeBase.tellInformation("~G" + agentX + "" + agentY);

        if (values.contains(Value.Hole)) knowledgeBase.tellInformation("H" + agentX + "" + agentY);
        else knowledgeBase.tellInformation("~H" + agentX + "" + agentY);

        if (values.contains(Value.Vampus)) knowledgeBase.tellInformation("V" + agentX + "" + agentY);
        else knowledgeBase.tellInformation("~V" + agentX + "" + agentY);

        if (values.contains(Value.Scream)) isVampusDead = true;
    }

    void animationMoveAgent() {
        Point point = pointDeque.pollFirst();
        if (point == null) board.stop(); //END

        checkOnGameOver(point);
        if (point != null)
            moveAgentTo(point.y, point.x);
    }

    private void moveAgentTo(int y, int x) {
        directionAgentX = (x - agentX);
        agentX = x;
        agentY = y;
    }

    private void checkOnGameOver(Point point) {
        if (point == null) {
            System.out.println("You cannot find gold shrug");
            board.stop();
        }
        if (board.cells[agentY][agentX].value.contains(Value.Hole) || (board.cells[agentY][agentX].value.contains(Value.Vampus) && !isVampusDead)) {
            System.out.println("You are dead!");
            board.stop();
        }
        if (board.cells[agentY][agentX].value.contains(Value.Glitter)) {
            System.out.println("You are won!");
            board.stop();
        }
    }

    public void drawAgent(Graphics2D g2d) {
        //additionAnimationX = -1 * directionAgentX * (ANIMATION_STEPS - animationCount) * (Board.BLOCK_SIZE / ANIMATION_STEPS);
        //additionAnimationY = -1 * directionAgentY * (ANIMATION_STEPS - animationCount) * (Board.BLOCK_SIZE / ANIMATION_STEPS);

        int x = agentX * Board.BLOCK_SIZE + additionAnimationX;
        int y = agentY * Board.BLOCK_SIZE + additionAnimationY;
        if (directionAgentX == -1) {
            g2d.drawImage(agentL, x, y, Board.BLOCK_SIZE, Board.BLOCK_SIZE, board);
        } else {
            g2d.drawImage(agentR, x, y, Board.BLOCK_SIZE, Board.BLOCK_SIZE, board);
        }
    }


}
