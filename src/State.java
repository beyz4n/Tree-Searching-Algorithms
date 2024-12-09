
public class State {
    int row, col;
    int moveCount = 0;
    State parent;

    State(int row, int col, State parent) {
        this.row = row;
        this.col = col;
        this.parent = parent;
        this.moveCount = (parent == null) ? 1 : parent.moveCount + 1; // Update move count
    }
}

