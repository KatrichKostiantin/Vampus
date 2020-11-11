import org.logicng.datastructures.Tristate;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.ArrayList;

public class KnowledgeBase {
    final FormulaFactory f = new FormulaFactory();
    final PropositionalParser p = new PropositionalParser(f);
    final SATSolver miniSat = MiniSat.miniSat(f);
    private Board board;
    private ArrayList<String> tellInformation = new ArrayList<>();

    public KnowledgeBase(Board board) {
        this.board = board;
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
        if (i < board.screenData.length - 1)
            if (board.screenData[i + 1][j] != 1) {
                String c1 = "";
                c1 = rightSymbol + (i + 1) + j;
                mates.add(c1);
            }
        if (j >= 1)
            if (board.screenData[i][j - 1] != 1) {
                String c1 = "";
                c1 = rightSymbol + i + (j - 1);
                mates.add(c1);
            }
        if (j < board.screenData[0].length - 1)
            if (board.screenData[i][j + 1] != 1) {
                String c1 = "";
                c1 = rightSymbol + i + (j + 1);
                mates.add(c1);
            }
        String rightSide = "";
        int len = mates.size();
        if (len > 1) {
            rightSide += "(" + mates.get(0);
            for (int k = 1; k < len; k++) {
                rightSide += middleSymbol + mates.get(k);
            }
            rightSide += ")";
        } else
            rightSide = mates.get(0);

        String logic = leftSide + rightSide;
        miniSat.add(p.parse(logic));
    }

    public void tellInformation(String info) {
        tellInformation.add(info);
    }

    public boolean askInformation(String info) {
        try {
            miniSat.reset();
            addAxioms();
            addTellInformation(miniSat);
            miniSat.add(p.parse(info));
            return miniSat.sat() == Tristate.TRUE;
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void addTellInformation(SATSolver miniSat) {
        try {
            for (String str : tellInformation) {
                miniSat.add(p.parse(str));
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    private void addAxioms() {
        try {
            for (int i = 0; i < board.screenData.length; i++) {
                for (int j = 0; j < board.screenData[i].length; j++) {
                    if (board.screenData[i][j] != 1) {
                        fillingLogicalTable("S", " | ", "V", i, j);
                        fillingLogicalTable("B", " | ", "H", i, j);
                        fillingLogicalTable("V", " & ", "S", i, j);
                        fillingLogicalTable("H", " & ", "B", i, j);

                    }
                }
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    public boolean sureAskInformation(String info) {
        return askInformation(info) && !askInformation("~" + info);
    }
}
