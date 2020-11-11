import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class VampusVisual extends JFrame {
    private Agent agent;
    private Random random = new Random();

    public VampusVisual() {
        initUI();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            var ex = new VampusVisual();
            ex.setVisible(true);
        });
    }

    private void initUI() {
        Board board = new Board();
        add(board);
        setTitle("Vampus");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize((board.screenData[0].length + 1) * Board.BLOCK_SIZE, (board.screenData.length + 1) * Board.BLOCK_SIZE);
        setLocationRelativeTo(null);
    }
}
