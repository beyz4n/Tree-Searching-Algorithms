import java.util.*;

public class Main {
    // Possible moves for the knight
    private static final int[][] MOVES = {
            {-2, 1}, {-1, 2}, {1, 2}, {2, 1},
            {2, -1}, {1, -2}, {-1, -2}, {-2, -1}
    };

    private static int totalMoves; // TODO: will be deleted
    private static int method;

    public static void main(String[] args) {
        
        boolean solutionFound = false;
        
        // Take the board size and search method from the user
        Scanner input = new Scanner(System.in);
        System.out.println("Enter a board size:");
        int boardSize = input.nextInt();

        Scanner input2 = new Scanner(System.in);
        System.out.println("Enter a search method:(a-d)");
        String searchMethod = input2.next();

       
        totalMoves = boardSize * boardSize;  // Calculate the total moves

        // Start position
        int startRow = 0;
        int startCol = 0;
        State startState = new State(startRow, startCol, null);

        // Start the timer
        long startTime = System.nanoTime();

        try {
            if (searchMethod.equals("a")) {
                solutionFound = knightTourSearchMethodOne(boardSize);
                System.out.println(solutionFound);
            } else if (searchMethod.equals("b")) {
                method = 0;
                solutionFound = preparationForDFS(boardSize, startState);

            } else if (searchMethod.equals("c")) {
                method = 1;
                solutionFound = preparationForDFS(boardSize, startState);

            } else if (searchMethod.equals("d")) {
                method = 2;
                solutionFound = preparationForDFS(boardSize, startState);
            } else {
                System.out.println("Give the search method as a,b,c,d.");
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Out of Memory");
            throw new OutOfMemoryError();
        }

        long endTime = System.nanoTime();
        double timeSpent = (endTime - startTime) / 1e9;
        System.out.println("Time spent for calculation: " + timeSpent);

        if (solutionFound) {
            System.out.println("A solution found.");
        } else if (timeSpent >= 60) {
            System.out.println("Timeout.");
        } else {
            System.out.println("No solution exists.");
        }
    }

    public static boolean knightTourSearchMethodOne(int boardSize) {
        int[] dx = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};

        Queue<State> nodes = new LinkedList<>();

        int[] startingPosition = {0, 0};
        nodes.add(new State(startingPosition[0], startingPosition[1], null));

        boolean solution = breadthFirstSearch(boardSize, dx, dy, nodes);
        return solution;
    }

    public static boolean breadthFirstSearch(int boardSize, int[] dx, int[] dy, Queue<State> queue) {

        while (!queue.isEmpty()) {
            State current = queue.poll();
            for (int i = 0; i < 8; i++) {
                int newX = current.row + dx[i];
                int newY = current.col + dy[i];
                if (isAvailable(newX, newY, boardSize)) {
                    State newState = new State(newX, newY, current);
                    if (!isInThePath(newState)) {
                        queue.add(newState);
                    }
                }
                if (testPath(current)) {
                    ArrayList<State> path = constructPath(current);
                    for (int j = path.size() - 1; j >= 0; j--)
                        System.out.println(path.get(j).row + ", " + path.get(j).col);
                    return true;
                }
            }
        }

        return false;
    }


    public static boolean isAvailable(int nextX, int nextY, int boardSize) {
        if (nextX >= 0 && nextY >= 0 && nextX < boardSize && nextY < boardSize) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isInThePath(State newState) {
        int newX = newState.row;
        int newY = newState.col;
        while (newState.parent != null) {
            if (newX == newState.parent.row && newY == newState.parent.col) {
                return true;
            }
            newState = newState.parent;
        }
        return false;
    }

    public static ArrayList<State> constructPath(State state) {
        ArrayList<State> path = new ArrayList<>();
        while (state.parent != null) {
            path.add(state);
            state = state.parent;
        }
        path.add(state);
        return path;
    }

    public static boolean testPath(State state) {
        int i = totalMoves;
        while (state.parent != null) {
            state = state.parent;
            i--;
        }
        return i == 1;
    }

    public static boolean preparationForDFS(int boardSize, State startState) {

        boolean solutionFound = false;
        State result = DFS(boardSize, startState);

        if (result != null) {
            List<State> path = backtrace(result);
            printPath(path);
            solutionFound = true;
        } 
        return solutionFound; 
    }


    public static State DFS(int boardSize, State startState) {
        int moveCount = 1; // TODO: will be deleted
        State current = startState; // TODO: will be deleted
        int chessBoard[][] = new int[boardSize][boardSize]; // TODO: will be deleted

        // Initialize frontier with start state of the problem
        Stack<State> frontier = new Stack<>();
        frontier.push(startState);

        // If it is DFS, then method is 0 and does the following
        // Explore all possible moves
        if (method == 0) {
        while (!frontier.isEmpty()) {
            // Chose a leaf node and remove it from the frontier
            State currentState = frontier.pop(); 

            // Goal state check
            if (currentState.moveCount == totalMoves) {
                return currentState; // Solution found
            }

            // Expand current node and add resulting nodes to the frontier
            for (int[] move : MOVES) {
                int nextRow = currentState.row + move[0];
                int nextCol = currentState.col + move[1];

               // Check validity of the move
               if (isAvailable(nextRow, nextCol, boardSize)) {
                State child = new State(nextRow, nextCol, currentState);
                if (!isInThePath(child)) { // Avoid revisiting
                    frontier.push(child);
                }
            }
            }
        }


            // If it is DFS with Heuristic h1b, then method is 1 and does the following
        } else if (method == 1) {
            // Created a priority queue that has row, col, priority as a structure
            PriorityQueue<int[]> priorityQueue = new PriorityQueue<>((a, b) -> Integer.compare(a[2], b[2]));
            // In every moves
            for (int i = 0; i < MOVES.length; i++) {
                // Calculate next state
                int nextRow = current.row + MOVES[i][0];
                int nextCol = current.col + MOVES[i][1];

                // If safe
                if (isSafe(nextRow, nextCol, chessBoard, boardSize)) {
                    // Calculate h1b and put it to priority queue
                    int h1b = calculateH1b(chessBoard, nextRow, nextCol, boardSize);
                    priorityQueue.add(new int[]{nextRow, nextCol, h1b});
                }

            }
            // Loop in the queue till it is empty
            while (!priorityQueue.isEmpty()) {
                // Get the move
                int[] nextBestMove = priorityQueue.poll();
                int nextBestRow = nextBestMove[0];
                int nextBestCol = nextBestMove[1];

                // Make it visited
                chessBoard[nextBestRow][nextBestCol] = moveCount + 1;
                State child = new State(nextBestRow, nextBestCol, current); // Create new state with parent
                State result = DFS(boardSize, startState); // Recursive call
                if (result != null) {
                    return result; // Solution found
                }
                chessBoard[nextBestRow][nextBestCol] = 0; // Backtrack
            }
            // If it is DFS with Heuristic h2, then method is 2 and does the following
        } else if (method == 2) {
            // Created a priority queue that has row, col, priority, distance to corners as a structure
            PriorityQueue<int[]> priorityQueue = new PriorityQueue<>(new Comparator<int[]>() {
                public int compare(int[] a, int[] b) {
                    // compare h1b value first
                    int optionCompare = Integer.compare(a[2], b[2]);
                    // if not tie
                    if (optionCompare != 0) {
                        return optionCompare;
                    }
                    // If tie, choose the one closes to corner
                    return Integer.compare(a[3], b[3]);
                }
            });
            // In every moves
            for (int i = 0; i < MOVES.length; i++) {
                // Calculate next state
                int nextRow = current.row + MOVES[i][0];
                int nextCol = current.col + MOVES[i][1];

                // If safe
                if (isSafe(nextRow, nextCol, chessBoard, boardSize)) {
                    // Calculate h1b and add the distance to the corners; add it to priority queue
                    int h1b = calculateH1b(chessBoard, nextRow, nextCol, boardSize);

                    // Calculate distances to corners 0,0 ; 0,N-1 ; N-1,0; N-1;N-1
                    int[] cornerDistances = {
                            Math.abs(nextRow) + Math.abs(nextCol),              // Top-left corner
                            Math.abs(nextRow) + Math.abs(nextCol - (boardSize - 1)),        // Top-right corner
                            Math.abs(nextRow - (boardSize - 1)) + Math.abs(nextCol),        // Bottom-left corner
                            Math.abs(nextRow - (boardSize - 1)) + Math.abs(nextCol - (boardSize - 1))   // Bottom-right corner
                    };

                    // Find the minimum distance to the corners
                    int distanceToCorners = cornerDistances[0];
                    for (int k = 1; k < cornerDistances.length; k++) {
                        if (cornerDistances[k] < distanceToCorners) {
                            distanceToCorners = cornerDistances[k];
                        }
                    }

                    priorityQueue.add(new int[]{nextRow, nextCol, h1b, distanceToCorners});
                }

            }
            // Loop in the queue till it is empty
            while (!priorityQueue.isEmpty()) {
                // Get the move
                int[] nextBestMove = priorityQueue.poll();
                int nextBestRow = nextBestMove[0];
                int nextBestCol = nextBestMove[1];

                // Make it visited
                chessBoard[nextBestRow][nextBestCol] = moveCount + 1;
                State child = new State(nextBestRow, nextBestCol, current); // Create new state with parent
                State result = DFS(boardSize, startState); // Recursive call
                if (result != null) {
                    return result; // Solution found
                }
                chessBoard[nextBestRow][nextBestCol] = 0; // Backtrack
            }
        }
        return null; // No solution found
    }

    // Method that calculates the options
    private static int calculateH1b(int[][] chessBoard, int x, int y, int N) {
        int options = 0;
        for (int i = 0; i < MOVES.length; i++) {
            int nextX = x + MOVES[i][0];
            int nextY = y + MOVES[i][1];
            if (isSafe(nextX, nextY, chessBoard, N)) {
                options++;
            }
        }
        return options;
    }

    // Method that calculates the options and if there is a tie prefer the closer to the corners
    private static int calculateH2(int[][] chessBoard, int x, int y, int N) {
        int options = 0;
        for (int i = 0; i < MOVES.length; i++) {
            int nextX = x + MOVES[i][0];
            int nextY = y + MOVES[i][1];
            if (isSafe(nextX, nextY, chessBoard, N)) {
                options++;
            }
        }
        return options;
    }

    // TODO: isSafe method will be deleted
    // Check if a cell is within bounds and unvisited
    private static boolean isSafe(int row, int col, int[][] chessBoard, int N) {

        boolean isPositionInBoard = (row >= 0 && row < N && col >= 0 && col < N);
        if (isPositionInBoard == false) {
            return false;
        }
        boolean notVisited = (chessBoard[row][col] == 0);
        return isPositionInBoard && notVisited;
    }

    // Backtrack to find the path
    private static List<State> backtrace(State state) {
        List<State> path = new ArrayList<>();
        while (state != null) {
            path.add(0, state); 
            state = state.parent;
        }
        return path;
    }

   // Print the path 
   private static void printPath(List<State> path) {
    System.out.println("Knight's Tour Path:");
    for (State state : path) {
        System.out.printf("(%d, %d) -> ", state.row, state.col);
    }
    System.out.println("END");
}
}
    


