import org.logicng.datastructures.Tristate;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;
import org.logicng.solvers.SolverState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Agent {
    final FormulaFactory f = new FormulaFactory();
    final PropositionalParser p = new PropositionalParser(f);
    final SATSolver miniSat = MiniSat.miniSat(f);

    static final int AGENT_ANIM_IMAGE = 4;
    private static final int ANIMATION_STEPS = 5;
    private static Image agentR,agentL;
    Board board;
    private int directionAgentX, directionAgentY;
    public int agentX, agentY;
    public int agentXOld, agentYOld;
    int additionAnimationY = 0, additionAnimationX = 0;
    int animationCount = 0;
    
    public Agent() throws ParserException {
        initKnowledgeBase();
    }

    public void tellInformation(String info) throws ParserException {
        miniSat.add(p.parse(info)); // не А = ~A

        /*Тут мы заполняем базу тем что сами узнали когда ходили по миру
        miniSat.add(f.variable("B21")); //= Ветер в точке 2, 1
        miniSat.add(f.not(f.variable("P11")));// = ямы нет в точке 1, 1
        miniSat.add(f.not(f.variable("B11")));// = Ветра нет в точке 1, 1
        miniSat.add(f.not(f.variable("B12")));// = ветра нет в точке 1, 2*/
    }

    private void initKnowledgeBase() throws ParserException {
//        miniSat.add(p.parse("B12 <=> (H11 | H22 | H13)"));
//        miniSat.add(p.parse("B13 <=> (H12 | H23 | H14)"));
//        miniSat.add(p.parse("B21 <=> (H11 | H22 | H31)"));
//        miniSat.add(p.parse("B11 <=> (H12 | H21)"));
//        miniSat.add(p.parse("H22 <=> (B12 & B21 & B23 & B32)"));


        int i, j;
        for (i = 0; i < board.getN(); i++) {
            for (j = 0; j < board.getM(); j++) {
                if (board.cells[i][j].value.contains("Stench")) fillingLogicalTable("S","|","V",i,j);
                if (board.cells[i][j].value.contains("Stench")) fillingLogicalTable("B","|","H",i,j);
                if (board.cells[i][j].value.contains("Stench")) fillingLogicalTable("V","&","S",i,j);
                if (board.cells[i][j].value.contains("Stench")) fillingLogicalTable("H","&","B",i,j);
            }
            }


        /*Тут мы заполняем изначальную базу которую должны знать.
        * Тип "Если веьер в (1, 2) то яма либо в (1,1) либо (2, 2) либо (1, 3)
        * Если яма в (2, 2) то ветер в (1, 2) и (2, 1) и в (2, 3) и в (3, 2)
        *
        * Тут будет много текста. Нужно навесить условия прям на все точки на все вариации.
        * Если там ветер, яма, запах, Вампул, блистит
        *
        */
    }

    private void fillingLogicalTable(String leftSymbol, String middleSymbol, String rightSymbol, int i, int j) throws ParserException {
        ArrayList<String> mates = new ArrayList<>();
        String leftSide = "";
        leftSide += leftSymbol + i + j + " <=> ";
        if (i >= 1)
            if (board.screenData[i - 1][j] != 1) {
                String c1 = "";
                c1 = rightSymbol + (i - 1) + j;
                mates.add(c1);
            }
        if (i < board.getN()-1)
            if(board.screenData[i+1][j]!=1) {
                String c1 = "";
                c1 = rightSymbol + (i+1) +j;
                mates.add(c1);
            }
        if (j >= 1)
            if(board.screenData[i][j-1]!=1) {
                String c1 = "";
                c1 = rightSymbol + i + (j-1);
                mates.add(c1);
            }
        if (j < board.getM())
            if(board.screenData[i][j+1]!=1) {
                String c1 = "";
                c1 = rightSymbol + i + (j+1);
                mates.add(c1);
            }
        String rightSide = "";
            int len = mates.size();
            if(len>1) rightSide += "(" + mates.get(0);
            for(int k = 1; k<len; k++){
                rightSide += middleSymbol + mates.get(k);
            }
            if(len>1) rightSide+=")";
            String logic = leftSide + rightSide;

            miniSat.add(p.parse(logic));
    }

    public boolean askInformation(String info) throws ParserException {
        SolverState state = miniSat.saveState();
        final SATSolver newSat = MiniSat.miniCard(f);
        newSat.loadState(state);
        newSat.add(p.parse(info));
        return newSat.sat() == Tristate.TRUE;
    }

    void initAgentImages() {
        agentR = new ImageIcon("images/spyR.png").getImage();
        agentL = new ImageIcon("images/spyL.png").getImage();
    }


    private void moveAgentTo(int j, int i) {
        directionAgentX = (i - agentX);
        directionAgentY = (j - agentY);
        agentXOld = agentX;
        agentYOld = agentY;
        agentX = i;
        agentY = j;
    }
    void animationMoveAgent() {
        Point point = null;
        if (point == null)
            board.stop();
        else
            moveAgentTo(point.y, point.x);
    }

    public void drawAgent(Graphics2D g2d) {
        additionAnimationX = -1 * directionAgentX * (ANIMATION_STEPS - animationCount) * (Board.BLOCK_SIZE / ANIMATION_STEPS);
        additionAnimationY = -1 * directionAgentY * (ANIMATION_STEPS - animationCount) * (Board.BLOCK_SIZE / ANIMATION_STEPS);

        int x = agentX * Board.BLOCK_SIZE + additionAnimationX;
        int y = agentY * Board.BLOCK_SIZE + additionAnimationY;
        if (directionAgentX == -1) {
            g2d.drawImage(agentL, x, y,Board.BLOCK_SIZE,Board.BLOCK_SIZE, board);
        } else if (directionAgentX == 1) {
            g2d.drawImage(agentR, x, y,Board.BLOCK_SIZE,Board.BLOCK_SIZE, board);
        }
    }

    public void step(Graphics2D g2d) {
        drawAgent(g2d);
        animationCount %= ANIMATION_STEPS;
        if (animationCount++ == 0) {
            animationMoveAgent();
        }
    }
}
