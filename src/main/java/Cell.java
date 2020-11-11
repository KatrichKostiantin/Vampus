import java.awt.*;
import java.util.ArrayList;


public class Cell {
    ArrayList<Value> value;
    private Point point;

    public Cell(ArrayList<Value> value, Point point) {
        this.value = value;
        this.point = point;
    }
}
