import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;



public class Main {

    private static final int memoryLimit = 100;
    static int nodeVisited = 1;
   
    public static void main(String[] args) {
        boolean solutionFound = false;

        Scanner input = new Scanner(System.in);
        System.out.println("Enter a board size:");
        int boardSize = input.nextInt();  
        Scanner input2 = new Scanner(System.in);
        System.out.println("Enter a search method:(a-d)");
        String searchMethod = input2.next(); 
        System.out.println(searchMethod);
      
        int[][] board = new int[boardSize][boardSize];

        long startTime = System.nanoTime();

        if(searchMethod.equals("a") ){
          System.out.println("sel") ;
          solutionFound = knightTourSearchMethodOne(board,boardSize); 
          System.out.println(solutionFound) ;
        }

        long endTime = System.nanoTime();
        double timeSpent = (endTime - startTime) / 1e9;
        System.out.println(timeSpent);

        if (solutionFound) {
            System.out.println("A solution found.");
            printBoard(board);
        } else if (timeSpent >= 60) {
            System.out.println("Timeout.");
        } else if(nodeVisited >= memoryLimit){
            System.out.println("Out of memory.");

        }
        else{
            System.out.println("No solution exists.");
            printBoard(board);
        }
    }




    public static boolean knightTourSearchMethodOne(int[][]board, int boardSize){
       int[] dx = {-2, -1, 1, 2, 2, 1, -1, -2};
       int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};

       Queue<State> nodes = new LinkedList<>();
       ArrayList<State> initialPath = new ArrayList<>();
        

       for(int row = 0; row < boardSize ; row++){
          for(int column = 0; column < boardSize ; column++){
            board[row][column] = 0;
          }
       }
       board[0][0] = 1;

       int[] startingPosition = {0,0};

       initialPath.add(new State(startingPosition[0], startingPosition[1], null));
       nodes.add(new State(startingPosition[0], startingPosition[1], null));

       boolean solution = breadthFirstSearch(board,boardSize,dx,dy,nodes,initialPath); 
       return solution;
       }
           




       public static boolean breadthFirstSearch(int[][] board, int boardSize, int[] dx, int[] dy, Queue<State> queue, ArrayList<State> path ) {

        while (!queue.isEmpty()) {

            State current = queue.poll();
           
              
            for (int i = 0; i < 8; i++) {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];
             
                 if (isAvailable(board, newX, newY, boardSize ) ) {
                    State newState = new State(newX, newY, current);
                     if(!isInThePath(newState, path,board)){
                        queue.add(newState);
                        System.out.println(path.size());
                        if (path.size() == boardSize * boardSize) {
                            
                            return true;
                        }
                       
                     }
                     
                     path.subList(1, path.size()).clear();
                    
                     
                }
            }
        }
        return false; 
    }

      
    public static boolean isAvailable(int[][] board, int nextX, int nextY,int boardSize){  
        if( nextX >= 0 && nextY >= 0 && nextX < boardSize && nextY < boardSize && board[nextX][nextY] == 0 ){
        return true;
        }
        else{
            return false;
        }
    }


   

     public static boolean isInThePath(State newState, ArrayList<State> path, int[][] board){
        
        
        refreshBoard(board);
        int newX = newState.x;
        int newY = newState.y;
        int boardLength = board.length * board.length;

        while(newState.parent != null){
            if(newX == newState.parent.x && newY == newState.parent.y){
                return true;
            } 
            path.add(newState);
            board[newState.x][newState.y] = boardLength- path.size();
            newState = newState.parent;
           
          
        }
       
        

        return false;

     }


    public static void refreshBoard(int[][] board){
        int N = board.length;
          for(int i = 0; i<N ; i++){
            for(int j = 0; j<N; j++){
                board[i][j] = 0;
            }
          }
          board[0][0] = 1;
    }

    private static void printBoard(int[][] board) {
        int N = board.length;
        for (int i = 0; i < N; i++) { 
            System.out.print((char) ('a' + N - 1 - i ) + "" + (N-i) ); 
            for (int j = 0; j < N; j++) { 
                System.out.printf("%3d ", board[i][j]);
            }
            System.out.println();
        }
    
        System.out.print("  "); 
        for (int j = 0; j < N; j++) {
            System.out.print((char) ('a' + j ) + "" + (j+1) +  "  ");
        }
        System.out.println();
    }

    
}
