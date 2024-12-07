import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
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

private static final int memoryLimit = 100;
static int nodeVisited = 1;
static int depth = 0;


    public static void main(String[] args) {
        boolean solutionFound = false;

        Scanner input = new Scanner(System.in);
        System.out.println("Enter a board size:");
        int boardSize = input.nextInt();  
        Scanner input2 = new Scanner(System.in);
        System.out.println("Enter a search method:(a-d)");
        String searchMethod = input2.next();
        long startTime = System.nanoTime();
        try {
            if (searchMethod.equals("a")) {
                solutionFound = knightTourSearchMethodOne(boardSize);
                System.out.println(solutionFound);
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Out of Memory");
            throw new OutOfMemoryError();
        }

        long endTime = System.nanoTime();
        double timeSpent = (endTime - startTime) / 1e9;
        System.out.println(timeSpent);

        if (solutionFound) {
            System.out.println("A solution found.");
        } else if (timeSpent >= 60) {
            System.out.println("Timeout.");
        } else if(nodeVisited >= memoryLimit){
            System.out.println("Out of memory.");

        }
        else{
            System.out.println("No solution exists.");
        }


        N = 8; // 8x8 chessboard
        TOTAL_MOVES = N * N;
        int[][] chessBoard = new int[N][N];
        // boolean solutionFound = false;
   
        //for (int startRow = 0; startRow < N; startRow++) {
        //    for (int startCol = 0; startCol < N; startCol++) {

        int startRow = 0;
        int startCol = 0;

        chessBoard[startRow][startCol] = 1; // Start position marked as visited (1)

        State startState = new State(startRow, startCol, null);

        State result = DFS(chessBoard, startState, 1);

        if (result != null) {
            List<State> path = backtrace(result);
            printPath(path);
            printBoard(chessBoard);
            // solutionFound = true;
            // break;
        } else {
            System.out.println("No solution found.");
        }
    
    // if (solutionFound) {
    //     break;
    // }
    }




    public static boolean knightTourSearchMethodOne(int boardSize){
       int[] dx = {-2, -1, 1, 2, 2, 1, -1, -2};
       int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};

       Queue<State> nodes = new LinkedList<>();

       int[] startingPosition = {0,0};
       nodes.add(new State(startingPosition[0], startingPosition[1], null));

       boolean solution = breadthFirstSearch(boardSize,dx,dy,nodes);
       return solution;
       }
           




       public static boolean breadthFirstSearch(int boardSize, int[] dx, int[] dy, Queue<State> queue ) {

        while (!queue.isEmpty()) {
            State current = queue.poll();
            for (int i = 0; i < 8; i++) {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];
                 if (isAvailable(newX, newY, boardSize )  ) {
                    State newState = new State(newX, newY, current);
                    if(!isInThePath(newState)){
                        queue.add(newState);
                    }
                }
                 if(testPath(current, boardSize)){
                     ArrayList <State> path = constructPath(current);
                     for(int j = path.size() -1 ;  j >= 0 ;j--)
                         System.out.println(path.get(j).x + ", " + path.get(j).y);
                     return true;
                 }
                 }
            }

        return false; 
    }

      
    public static boolean isAvailable(int nextX, int nextY,int boardSize){
        if( nextX >= 0 && nextY >= 0 && nextX < boardSize && nextY < boardSize ){
        return true;
        }
        else{
            return false;
        }
    }


   

     public static boolean isInThePath(State newState){
        int newX = newState.x;
        int newY = newState.y;
         while (newState.parent != null) {
             if (newX == newState.parent.x && newY == newState.parent.y) {
                 return true;
             }
             newState = newState.parent;
         }
        return false;
     }

     public static ArrayList<State> constructPath(State state){
         ArrayList<State> path = new ArrayList<>();
         while(state.parent != null){
             path.add(state);
             state = state.parent;
         }
         path.add(state);
         return path;
     }

    public static boolean testPath(State state, int boardSize){
        int i = boardSize * boardSize;
        while(state.parent != null ) {
            state = state.parent;
            i--;
        }

        return i == 1;
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
    





}



    

