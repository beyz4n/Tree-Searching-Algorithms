import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {

    // Possible moves for the knight
    private static final int[][] MOVES = {
            { -2, 1 }, { -1, 2 }, { 1, 2 }, { 2, 1 },
            { 2, -1 }, { 1, -2 }, { -1, -2 }, { -2, -1 }
    };

    static int totalMoves;
    static int numberOfNodesExpanded = 0; // Number of nodes expanded
    private static boolean isSolutionWithBitSet = false; // If true, the solution is kept with chessBoard as BitSet
    private static boolean timeoutFlag = false;
    private static FileWriter writer = null;

    public static void main(String[] args) {

        boolean solutionFound = false; // Solution found flag

        // Open a file to write the moves of the knight in the solution
        try {
            writer = new FileWriter("moves.txt");
        } catch (Exception e) {
            System.out.println("Cannot open a file!");
            System.exit(0);
        }

        // Take the board size from the user
        Scanner input = new Scanner(System.in);
        System.out.println("Enter a board size:");
        int boardSize = input.nextInt();
        try {
            writer.write("N = " + boardSize + "\n");
        } catch (Exception e) {
            System.out.println("IO Error");
            System.exit(0);
        }

        // Take the search method from the user
        Scanner input2 = new Scanner(System.in);
        System.out.println("Enter a search method:(a-d)");
        String searchMethod = input2.next();

        // Take the user input to keep the chessboard in the state
        Scanner input3 = new Scanner(System.in);
        System.out.println("If you want to keep the chessboard in the state, enter '1' otherwise enter '0':");
        String bitSetInput = input3.next();

        if (bitSetInput.equals("1")) {
            isSolutionWithBitSet = true;
        } else {
            isSolutionWithBitSet = false;
        }

        // totalMoves is the number of moves in solution
        totalMoves = boardSize * boardSize;

        // Start position
        int startRow = 0;
        int startCol = 0;

        // Create a BitSet to keep the chessboard
        BitSet initialBoard = new BitSet(totalMoves);
        initialBoard.set(0);

        // Create the start state
        State startState = new State(startRow, startCol, null, initialBoard);

        // Time limit for the search: 15 minutes
        long duration = 900000;

        // Search method name
        String searchMethodName = switch (searchMethod) {
            case "a" -> "Breadth First Search";
            case "b" -> "Depth First Search";
            case "c" -> "Depth First Search with Node Selection Heuristic h1b in [1]";
            case "d" -> "Depth First Search with Node Selection Heuristic h2 in [1]";
            default -> "";
        };

        System.out.println("You selected the search method as " + searchMethodName + " and the time limit is "
                + duration / 60000 + " minutes.");
        long startTime = System.currentTimeMillis();

        // Start the search
        try {
            if (searchMethod.equals("a") || searchMethod.equals("b") || searchMethod.equals("c")
                    || searchMethod.equals("d")) {

                solutionFound = treeSearch(searchMethod, startState, boardSize, startTime, duration);
                long endTime = System.currentTimeMillis();
                float timeSpent = endTime - startTime;
                System.out.println("Time spent: " + timeSpent / 60000);

            } else {
                System.out.println("Give the search method as a,b,c,d.");
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Out of Memory");
            long endTime = System.currentTimeMillis();
            float timeSpent = endTime - startTime;
            System.out.println("Time spent: " + timeSpent / 60000);
            System.out.println("Number of nodes expanded: " + numberOfNodesExpanded);
            System.exit(0);
        }

        // Print the result
        if (solutionFound) {
            System.out.println("A solution found.");
            System.out.println("Number of nodes expanded: " + numberOfNodesExpanded);
        } else if (!solutionFound && !timeoutFlag) {
            System.out.println("No solution exists.");
            System.out.println("Number of nodes expanded: " + numberOfNodesExpanded);
        } else {
            System.out.println("Timeout.");
            System.out.println("Number of nodes expanded: " + numberOfNodesExpanded);
        }
        try {
            writer.close();
        } catch (Exception e) {
            System.out.println("IO Error");
            System.exit(0);
        }
    }

    // Tree search method
    public static boolean treeSearch(String strategy, State startState, int boardSize, long startTime, long duration) {

        // initializing frontier
        Queue<State> queueFrontier = null;
        Stack<State> stackFrontier = null;

        if (strategy.equals("a")) {

            // Initialize the frontier (nodes to be visited) using the initial state of problem
            queueFrontier = new LinkedList<>();
            queueFrontier.add(startState);

            if (isSolutionWithBitSet) {

                BitSet board; // Bitset to keep the chessboard

                while (!queueFrontier.isEmpty()) {

                    // Check the time limit
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime >= duration) {
                        float timeSpent = endTime - startTime;
                        System.out.println("Time spent: " + timeSpent / 60000);
                        timeoutFlag = true;
                        return false;
                    }

                    // Choose a leaf node for expansion according to strategy and remove it from the frontier
                    State current = queueFrontier.poll();
                    numberOfNodesExpanded++;

                    // If goal state then return the corresponding solution
                    if (goalTest(current, strategy)) {
                        return true;
                    }

                    // Expand the node and add the resulting nodes to the frontier
                    for (int[] move : MOVES) {
                        int newX = current.row + move[0];
                        int newY = current.col + move[1];

                        if (isSafeMove(newX, newY, current, boardSize)) {
                            board = (BitSet) current.chessBoard.clone();
                            board.set(newX * boardSize + newY);
                            State child = new State(newX, newY, current, board);
                            queueFrontier.add(child);
                        }
                    }

                }
            } else {

                while (!queueFrontier.isEmpty()) {

                    // Check the time limit
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime >= duration) {

                        timeoutFlag = true;
                        return false;
                    }

                    // Choose a leaf node for expansion according to strategy and remove it from the frontier
                    State current = queueFrontier.poll();
                    numberOfNodesExpanded++;

                    // If goal state then return the corresponding solution
                    if (goalTest(current, strategy)) {
                        return true;
                    }

                    // Expand the node and add the resulting nodes to the frontier
                    for (int[] move : MOVES) {
                        int newX = current.row + move[0];
                        int newY = current.col + move[1];

                        if (isAvailable(newX, newY, boardSize)) {
                            State newState = new State(newX, newY, current, new BitSet(0));
                            if (!isInThePath(newState)) {
                                queueFrontier.add(newState);
                            }
                        }

                    }
                }
            }

        } else if (strategy.equals("b")) {

            // Initialize the frontier (nodes to be visited) using the initial state of problem
            stackFrontier = new Stack<>();
            stackFrontier.push(startState);

            if (isSolutionWithBitSet) {

                // Bitset to keep the chessboard
                BitSet board;

                while (!stackFrontier.isEmpty()) {

                    // Check the time limit
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime >= duration) {

                        timeoutFlag = true;
                        return false;
                    }
                    // Choose a leaf node for expansion according to strategy and remove it from the frontier
                    State currentState = stackFrontier.pop();
                    numberOfNodesExpanded++;

                    // If goal state then return the corresponding solution
                    if (goalTest(currentState, strategy)) {
                        return true; // Solution found
                    }

                    // Expand the node and add the resulting nodes to the frontier
                    for (int[] move : MOVES) {
                        int nextRow = currentState.row + move[0];
                        int nextCol = currentState.col + move[1];

                        // Check validity of the move
                        if (isSafeMove(nextRow, nextCol, currentState, boardSize)) {
                            board = (BitSet) currentState.chessBoard.clone();
                            board.set(nextRow * boardSize + nextCol);
                            State child = new State(nextRow, nextCol, currentState, board);
                            stackFrontier.push(child);
                        }
                    }
                }
            } else {

                while (!stackFrontier.isEmpty()) {

                    // Check the time limit
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime >= duration) {

                        timeoutFlag = true;
                        return false;
                    }
                    // Choose a leaf node for expansion according to strategy and remove it from the frontier
                    State currentState = stackFrontier.pop();
                    numberOfNodesExpanded++;

                    // If goal state then return the corresponding solution
                    if (goalTest(currentState, strategy)) {
                        return true; // Solution found
                    }

                    // Expand current node and add resulting nodes to the frontier
                    for (int[] move : MOVES) {
                        int nextRow = currentState.row + move[0];
                        int nextCol = currentState.col + move[1];

                        // Check validity of the move
                        if (isAvailable(nextRow, nextCol, boardSize)) {
                            State child = new State(nextRow, nextCol, currentState, new BitSet(0));
                            if (!isInThePath(child)) { // Avoid revisiting
                                stackFrontier.push(child);
                            }
                        }
                    }
                }
            }
        }

        // If it is DFS with Heuristic h1b, then method is 1 and does the following
        else if (strategy.equals("c")) {

            // Initialize the frontier (nodes to be visited) using the initial state of problem
            stackFrontier = new Stack<>();
            stackFrontier.push(startState);

            if (isSolutionWithBitSet) {

                // Bitset to keep the chessboard
                BitSet board;

                while (!stackFrontier.isEmpty()) {

                    // Check the time limit
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime >= duration) {

                        timeoutFlag = true;
                        return false;
                    }
                    // Choose a leaf node for expansion according to strategy and remove it from the frontier
                    State currentState = stackFrontier.pop();
                    numberOfNodesExpanded++;

                    // If goal state then return the corresponding solution
                    if (goalTest(currentState, strategy)) {
                        return true; // Solution found
                    }

                    List<Object[]> h1bValues = new ArrayList<>();

                    // Expand the node and add the resulting nodes to the frontier
                    for (int[] move : MOVES) {
                        int nextRow = currentState.row + move[0];
                        int nextCol = currentState.col + move[1];

                        // Check validity of the move
                        if (isSafeMove(nextRow, nextCol, currentState, boardSize)) {
                            board = (BitSet) currentState.chessBoard.clone();
                            board.set(nextRow * boardSize + nextCol);
                            State child = new State(nextRow, nextCol, currentState, board);
                            if (!isInThePath(child)) { // Avoid revisiting
                                h1bValues.add(new Object[] { calculateH1b(child, boardSize, nextRow, nextCol), child });
                            }
                        }
                    }
                    h1bValues.sort((a, b) -> (Integer) b[0] - (Integer) a[0]);
                    for (int i = 0; i < h1bValues.size(); i++) {
                        stackFrontier.push((State) h1bValues.get(i)[1]);
                    }

                }
            } else {

                while (!stackFrontier.isEmpty()) {

                    // Check the time limit
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime >= duration) {

                        timeoutFlag = true;
                        return false;
                    }

                    // Choose a leaf node for expansion according to strategy and remove it from the frontier
                    State currentState = stackFrontier.pop();
                    numberOfNodesExpanded++;

                    // If goal state then return the corresponding solution
                    if (goalTest(currentState, strategy)) {
                        return true; // Solution found
                    }

                    List<Object[]> h1bValues = new ArrayList<>();

                    // Expand current node and add resulting nodes to the frontier
                    for (int[] move : MOVES) {
                        int nextRow = currentState.row + move[0];
                        int nextCol = currentState.col + move[1];
                        // Check validity of the move
                        if (isAvailable(nextRow, nextCol, boardSize)) {
                            State child = new State(nextRow, nextCol, currentState, new BitSet(0));
                            if (!isInThePath(child)) { // Avoid revisiting
                                h1bValues.add(new Object[] { calculateH1b(child, boardSize, nextRow, nextCol), child });
                            }
                        }
                    }
                    h1bValues.sort((a, b) -> (Integer) b[0] - (Integer) a[0]);
                    for (int i = 0; i < h1bValues.size(); i++) {
                        stackFrontier.push((State) h1bValues.get(i)[1]);
                    }
                }
            }

        }

        // If it is DFS with Heuristic h2, then method is 2 and does the following
        else if (strategy.equals("d")) {

            // Initialize the frontier (nodes to be visited) using the initial state of
            // problem
            stackFrontier = new Stack<>();
            stackFrontier.push(startState);

            if (isSolutionWithBitSet) {

                // Bitset to keep the chessboard
                BitSet board;

                while (!stackFrontier.isEmpty()) {

                    // Check the time limit
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime >= duration) {

                        timeoutFlag = true;
                        return false;
                    }

                    // Choose a leaf node for expansion according to strategy and remove it from the
                    // frontier
                    State currentState = stackFrontier.pop();
                    numberOfNodesExpanded++;

                    // If goal state then return the corresponding solution
                    if (goalTest(currentState, strategy)) {
                        return true; // Solution found
                    }
                    List<Object[]> h2Values = new ArrayList<>();

                    // Expand current node and add resulting nodes to the frontier
                    for (int[] move : MOVES) {
                        int nextRow = currentState.row + move[0];
                        int nextCol = currentState.col + move[1];

                        // Check validity of the move
                        if (isSafeMove(nextRow, nextCol, currentState, boardSize)) {
                            board = (BitSet) currentState.chessBoard.clone();
                            board.set(nextRow * boardSize + nextCol);
                            State child = new State(nextRow, nextCol, currentState, board);
                            if (!isInThePath(child)) { // Avoid revisiting
                                h2Values.add(new Object[] { calculateH1b(child, boardSize, nextRow, nextCol),
                                        calculateMinDistanceCorners(nextRow, nextCol, boardSize), child });
                            }
                        }
                    }
                    h2Values.sort((a, b) -> {
                        // First compare by the first integer value (descending order -> max first)
                        int firstComparison = (Integer) b[0] - (Integer) a[0]; // max first

                        // If first integers are equal, compare by the second integer value (descending
                        // order -> max first)
                        if (firstComparison == 0) {
                            return (Integer) b[1] - (Integer) a[1]; // max first
                        }
                        return firstComparison; // If first integers are not equal, return the result of first
                                                // comparison
                    });

                    for (int i = 0; i < h2Values.size(); i++) {
                        stackFrontier.push((State) h2Values.get(i)[2]);
                    }
                }

            } else {

                while (!stackFrontier.isEmpty()) {

                    // Check the time limit
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime >= duration) {

                        timeoutFlag = true;
                        return false;
                    }
                    // Choose a leaf node for expansion according to strategy and remove it from the
                    // frontier
                    State currentState = stackFrontier.pop();
                    numberOfNodesExpanded++;

                    // If goal state then return the corresponding solution
                    if (goalTest(currentState, strategy)) {
                        return true; // Solution found
                    }

                    List<Object[]> h2Values = new ArrayList<>();
                    // Expand current node and add resulting nodes to the frontier
                    for (int[] move : MOVES) {
                        int nextRow = currentState.row + move[0];
                        int nextCol = currentState.col + move[1];
                        // Check validity of the move
                        if (isAvailable(nextRow, nextCol, boardSize)) {
                            State child = new State(nextRow, nextCol, currentState, new BitSet(0));
                            if (!isInThePath(child)) { // Avoid revisiting
                                h2Values.add(new Object[] { calculateH1b(child, boardSize, nextRow, nextCol),
                                        calculateMinDistanceCorners(nextRow, nextCol, boardSize), child });
                            }
                        }
                    }
                    h2Values.sort((a, b) -> {
                        // First compare by the first integer value (descending order -> max first)
                        int firstComparison = (Integer) b[0] - (Integer) a[0]; // max first

                        // If first integers are equal, compare by the second integer value (descending
                        // order -> max first)
                        if (firstComparison == 0) {
                            return (Integer) b[1] - (Integer) a[1]; // max first
                        }
                        return firstComparison; // If first integers are not equal, return the result of first
                                                // comparison
                    });

                    for (int i = 0; i < h2Values.size(); i++) {
                        stackFrontier.push((State) h2Values.get(i)[2]);
                    }
                }
            }
        }

        return false; // No solution found
    }

    public static boolean goalTest(State currentState, String strategy) {

        if (strategy.equals("a")) {
            if (testPath(currentState)) {
                ArrayList<State> path = constructPath(currentState);
                printPath(path);
                return true;
            }
        } else {
            if (currentState.moveCount == totalMoves) {
                ArrayList<State> path = constructPath(currentState);
                printPath(path);
                return true;
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
            path.add(0, state);
            state = state.parent;
        }
        path.add(0, state);
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

    // Method that calculates the options
    private static int calculateH1b(State currentState, int boardSize, int row, int col) {
        int options = 0;
        for (int i = 0; i < MOVES.length; i++) {
            int nextRow = row + MOVES[i][0];
            int nextCol = col + MOVES[i][1];
            if (isAvailable(nextRow, nextCol, boardSize)) {
                State child = new State(nextRow, nextCol, currentState, new BitSet(0));
                if (!isInThePath(child)) { // Avoid revisiting
                    options++;
                }
            }
        }
        return options;
    }

    private static int calculateMinDistanceCorners(int nextRow, int nextCol, int boardSize) {
        // Calculate distances to corners 0,0 ; 0,N-1 ; N-1,0; N-1;N-1
        int[] cornerDistances = {
                Math.abs(nextRow) + Math.abs(nextCol), // Top-left corner
                Math.abs(nextRow) + Math.abs(nextCol - (boardSize - 1)), // Top-right corner
                Math.abs(nextRow - (boardSize - 1)) + Math.abs(nextCol), // Bottom-left corner
                Math.abs(nextRow - (boardSize - 1)) + Math.abs(nextCol - (boardSize - 1)) // Bottom-right corner
        };

        // Find the minimum distance to the corners
        int distanceToCorners = cornerDistances[0];
        for (int k = 1; k < cornerDistances.length; k++) {
            if (cornerDistances[k] < distanceToCorners) {
                distanceToCorners = cornerDistances[k];
            }
        }
        return distanceToCorners;
    }

    public static boolean isSafeMove(int row, int col, State current, int boardSize) {
        return row >= 0 && col >= 0 && row < boardSize && col < boardSize
                && !current.chessBoard.get(row * boardSize + col);
    }

    // Print the path
    private static void printPath(List<State> path) {
        System.out.println("Knight's Tour Path:");
        for (State state : path) {
            try {
                writer.write(state.row + ", " + state.col + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.printf("(%d, %d) -> ", state.row, state.col);
        }
        System.out.println();
    }
}
