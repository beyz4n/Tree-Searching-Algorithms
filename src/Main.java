import java.util.ArrayList;
import java.util.List;

public class Main {

    static int N; // size of the board for now
    private static int TOTAL_MOVES;
   
    // Possible moves for the knight
    private static final int[][] MOVES = {
        {-2, 1}, {-1, 2}, {1, 2}, {2, 1},
        {2, -1}, {1, -2}, {-1, -2}, {-2, -1}
    };

    // State representation
    static class State {
        int row;    
        int column;
        State parent;

        State(int row, int column, State parent) {
            this.row = row;
            this.column = column;
            this.parent = parent;
        }
    }

    public static void main(String[] args) {
        N = 7; // 8x8 chessboard
        TOTAL_MOVES = N * N;
        int[][] chessBoard = new int[N][N];
        boolean solutionFound = false;

        // TODO: loopa gerek olmayabilir de
        for (int startRow = 0; startRow < N; startRow++) {
            for (int startCol = 0; startCol < N; startCol++) {

        // int startRow = 0;
        // int startCol = 0;

                chessBoard[startRow][startCol] = 1; // Start position marked as visited (1)

                State startState = new State(startRow, startCol, null);

                State result = DFS(chessBoard, startState, 1);

                if (result != null) {
                    List<State> path = backtrace(result);
                    printPath(path);
                    printBoard(chessBoard);
                    solutionFound = true;
                    break;
                } else {
                    System.out.println("No solution found.");
                }
            }
            if (solutionFound) {
                break;
            }
        } 
    }

    public static State DFS(int[][] chessBoard, State current, int moveCount) {

        // Check if goal state
        if (moveCount == TOTAL_MOVES) {
            return current; // All cells are visited, solution found
        }
        // Explore all possible moves
        for (int i = 0; i < MOVES.length; i++) {
            int nextRow = current.row + MOVES[i][0];
            int nextCol = current.column + MOVES[i][1];
        
            if (isSafe(nextRow, nextCol, chessBoard)) {
                // Mark the move as visited
                chessBoard[nextRow][nextCol] = moveCount + 1;
                State child = new State(nextRow, nextCol, current); // Create new state with parent
                State result = DFS(chessBoard, child, moveCount + 1);
                if (result != null) {
                    return result; // Solution found
                }
                chessBoard[nextRow][nextCol] = 0; // Backtrack

            }
        }

        return null; // No solution found
    }

    // Check if a cell is within bounds and unvisited
    private static boolean isSafe(int row, int col, int[][] chessBoard) {

        boolean isPositionInBoard = (row >= 0 && row < N && col >= 0 && col < N);
        if (isPositionInBoard == false) {
            return false;
        }
        boolean notVisited = (chessBoard[row][col] == 0);
        return isPositionInBoard && notVisited;
    }

    private static List<State> backtrace(State state) {
        List<State> path = new ArrayList<>();
        while (state != null) {
            path.add(0, state); // Add to the beginning to reverse order
            state = state.parent;
        }
        return path;
    }

    private static void printPath(List<State> path) {
        System.out.println("Knight's Tour Path:");
        for (State state : path) {
            System.out.printf("(%d, %d) -> ", state.row, state.column);
        }
        System.out.println("END");
    }

    // Print the board
    public static void printBoard(int[][] chessBoard) {
        for (int[] row : chessBoard) {
            for (int cell : row) {
                System.out.printf("%2d ", cell);
            }
            System.out.println();
        }
    }
}
    



