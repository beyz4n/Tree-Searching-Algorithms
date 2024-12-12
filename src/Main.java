import java.io.BufferedWriter;
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
    private static boolean isSolutionWithBitSet = false;

    public static void main(String[] args) {

        boolean solutionFound = false;

        // Take the board size and search method from the user
        Scanner input = new Scanner(System.in);
        System.out.println("Enter a board size:");
        int boardSize = input.nextInt();

        Scanner input2 = new Scanner(System.in);
        System.out.println("Enter a search method:(a-d)");
        String searchMethod = input2.next();

        Scanner input3 = new Scanner(System.in);
        System.out.println("If you want to use BitSet, enter '1' otherwise enter '0':");
        String bitSetInput = input3.next();

        if (bitSetInput.equals("1")) {
            isSolutionWithBitSet = true;
        } else {
            isSolutionWithBitSet = false;
        }

        totalMoves = boardSize * boardSize;
        // Start position
        int startRow = 0;
        int startCol = 0;
        BitSet initialBoard = new BitSet(totalMoves);
        initialBoard.set(0);
        State startState = new State(startRow, startCol, null, initialBoard); // Create start state

        // Start the timer
        long startTime = System.nanoTime();

        try {
            if (searchMethod.equals("a") || searchMethod.equals("b") || searchMethod.equals("c")
                    || searchMethod.equals("d")) {
                solutionFound = treeSearch(searchMethod, startState, boardSize);
                System.out.println(solutionFound);
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

    public static boolean treeSearch(String strategy, State startState, int boardSize) {

        // initializing frontier
        Queue<State> queueFrontier = null;
        Stack<State> stackFrontier = null;

        if (strategy.equals("a")) {

            queueFrontier = new LinkedList<>();
            queueFrontier.add(startState);

            if (isSolutionWithBitSet) {

                BitSet board;

                while (!queueFrontier.isEmpty()) {

                    State current = queueFrontier.poll();

                    for (int[] move : MOVES) {
                        int newX = current.row + move[0];
                        int newY = current.col + move[1];

                        if (isSafeMoveForBitSet(newX, newY, current, boardSize)) {
                            board = (BitSet) current.chessBoard.clone();
                            board.set(newX * boardSize + newY);
                            State child = new State(newX, newY, current, board);
                            queueFrontier.add(child);
                        }
                    }
                    if (goalTest(current, strategy, boardSize)) {
                        return true;
                    }
                }
            }

            else {

                while (!queueFrontier.isEmpty()) {

                    State current = queueFrontier.poll();

                    for (int[] move : MOVES) {
                        int newX = current.row + move[0];
                        int newY = current.col + move[1];

                        if (isAvailable(newX, newY, boardSize)) {
                            State newState = new State(newX, newY, current, new BitSet(0));
                            if (!isInThePath(newState)) {
                                queueFrontier.add(newState);
                            }
                        }
                        if (goalTest(current, strategy, boardSize)) {
                            return true;
                        }
                    }
                }
            }

        }

        else if (strategy.equals("b")) {

            stackFrontier = new Stack<>();
            stackFrontier.push(startState);

            if (isSolutionWithBitSet) {

                BitSet board;

                while (!stackFrontier.isEmpty()) {
                    State currentState = stackFrontier.pop();

                    // Goal check
                    if (goalTest(currentState, strategy, boardSize)) {
                        return true; // Solution found
                    }

                    // Expand current node
                    for (int[] move : MOVES) {
                        int nextRow = currentState.row + move[0];
                        int nextCol = currentState.col + move[1];

                        // Check validity of the move
                        if (isSafeMoveForBitSet(nextRow, nextCol, currentState, boardSize)) {
                            board = (BitSet) currentState.chessBoard.clone();
                            board.set(nextRow * boardSize + nextCol);
                            State child = new State(nextRow, nextCol, currentState, board);
                            stackFrontier.push(child);
                        }
                    }
                }
            } else {

                while (!stackFrontier.isEmpty()) {
                    // Chose a leaf node and remove it from the frontier
                    State currentState = stackFrontier.pop();

                    // Goal state check
                    if (goalTest(currentState, strategy, boardSize)) {
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
            stackFrontier = new Stack<>();
            stackFrontier.push(startState);

            if (isSolutionWithBitSet) {

                BitSet board;

                while (!stackFrontier.isEmpty()) {
                    State currentState = stackFrontier.pop();

                    // Goal check
                    if (goalTest(currentState, strategy, boardSize)) {
                        return true; // Solution found
                    }

                    List<Object[]> h1bValues = new ArrayList<>();
                    // Expand current node
                    for (int[] move : MOVES) {
                        int nextRow = currentState.row + move[0];
                        int nextCol = currentState.col + move[1];

                        // Check validity of the move
                        if (isSafeMoveForBitSet(nextRow, nextCol, currentState, boardSize)) {
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
                    // Chose a leaf node and remove it from the frontier
                    State currentState = stackFrontier.pop();

                    // Goal state check
                    if (goalTest(currentState, strategy, boardSize)) {
                        return true; // Solution found
                    }

                    // Expand current node and add resulting nodes to the frontier
                    List<Object[]> h1bValues = new ArrayList<>();
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

            stackFrontier = new Stack<>();
            stackFrontier.push(startState);

            if (isSolutionWithBitSet) {
                BitSet board;

                while (!stackFrontier.isEmpty()) {
                    State currentState = stackFrontier.pop();

                    // Goal check
                    if (goalTest(currentState, strategy, boardSize)) {
                        return true; // Solution found
                    }
                    List<Object[]> h2Values = new ArrayList<>();
                    // Expand current node
                    for (int[] move : MOVES) {
                        int nextRow = currentState.row + move[0];
                        int nextCol = currentState.col + move[1];

                        // Check validity of the move
                        if (isSafeMoveForBitSet(nextRow, nextCol, currentState, boardSize)) {
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

            }

            else {

                while (!stackFrontier.isEmpty()) {
                    // Chose a leaf node and remove it from the frontier
                    State currentState = stackFrontier.pop();

                    // Goal state check
                    if (goalTest(currentState, strategy, boardSize)) {
                        return true; // Solution found
                    }

                    // Expand current node and add resulting nodes to the frontier
                    List<Object[]> h2Values = new ArrayList<>();
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

    public static boolean goalTest(State currentState, String strategy, int boardSize) {

        if (strategy.equals("a")) {
            if (testPath(currentState)) {
                ArrayList<State> path = constructPath(currentState);
                printPath(path, boardSize);
                return true;
            }
        } else {
            if (currentState.moveCount == totalMoves) {
                ArrayList<State> path = constructPath(currentState);
                printPath(path, boardSize);
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

    public static boolean isSafeMoveForBitSet(int row, int col, State current, int boardSize) {
        return row >= 0 && col >= 0 && row < boardSize && col < boardSize
                && !current.chessBoard.get(row * boardSize + col);
    }

    public static void printPath(List<State> path, int boardSize) {
    // Print to console
    System.out.println("Knight's Tour Path:");
    for (State state : path) {
        System.out.printf("(%d, %d) -> ", state.row, state.col);
    }
    System.out.println("END");

    // Write to a text file
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("moves.txt"))) {
        writer.write("N = " + boardSize + "\n");
        for (State state : path) {
            writer.write(state.row + ", " + state.col + "\n");
        }
    } catch (IOException e) {
        System.err.println("Error writing to file: " + e.getMessage());
    }
}

    // // Print the path
    // private static void printPath(List<State> path) {
    //     System.out.println("Knight's Tour Path:");
    //     for (State state : path) {
    //         System.out.printf("(%d, %d) -> ", state.row, state.col);
    //     }
    //     System.out.println("END");
    // }
}
