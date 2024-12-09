
public class State {
        int row, col;
        int moveCount;
        State parent;
        
        State(int row, int col, State parent) {
            this.row = row;
            this.col = col;
            this.moveCount = parent.moveCount + 1;
            this.parent = parent;
        }

}

