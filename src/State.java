import java.util.BitSet;

public class State {
    int row, col;
        int moveCount = 0;
        State parent;
        BitSet chessBoard; // Represents visited positions

        State(int row, int col, State parent, BitSet chessBoard) {
            this.row = row;
            this.col = col;
            this.parent = parent;
            this.moveCount = (parent == null) ? 1 : parent.moveCount + 1;
            this.chessBoard = chessBoard;
        }
}

