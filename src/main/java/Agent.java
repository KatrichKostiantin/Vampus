import org.logicng.datastructures.Tristate;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;
import org.logicng.solvers.SolverState;

import javax.swing.*;
import java.awt.*;

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
        miniSat.add(f.variable("B21")); = Ветер в точке 2, 1
        miniSat.add(f.not(f.variable("P11"))); = ямы нет в точке 1, 1
        miniSat.add(f.not(f.variable("B11"))); = Ветра нет в точке 1, 1
        miniSat.add(f.not(f.variable("B12"))); = ветра нет в точке 1, 2*/
    }

    private void initKnowledgeBase() throws ParserException {
        miniSat.add(p.parse("B12 <=> (P11 | P22 | P13)"));
        miniSat.add(p.parse("B21 <=> (P11 | P22 | P31)"));
        miniSat.add(p.parse("B11 <=> (P12 | P21)"));
        miniSat.add(p.parse("P22 <=> (B12 & B21 & B23 & B32)"));

        /*Тут мы заполняем изначальную базу которую должны знать.
        * Тип "Если веьер в (1, 2) то яма либо в (1,1) либо (2, 2) либо (1, 3)
        * Если яма в (2, 2) то ветер в (1, 2) и (2, 1) и в (2, 3) и в (3, 2)
        *
        * Тут будет много текста. Нужно навесить условия прям на все точки на все вариации.
        * Если там ветер, яма, запах, Вампул, блистит
        *
        * */
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
