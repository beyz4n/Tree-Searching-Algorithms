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
        } else if (timeSpent >= 15) {
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
       Queue<int[]> queue = new LinkedList<>();

       for(int row = 0; row < boardSize ; row++){
          for(int column = 0; column < boardSize ; column++){
            board[row][column] = 0;
          }
       }
       board[0][0] = 1;

       int[] startingPosition = {0,0};
       queue.add(new int[]{startingPosition[0], startingPosition[1]});
       boolean solution = breadthFirstSearch(board,boardSize,dx,dy,queue); 
       return solution;
       }
           



       public static boolean breadthFirstSearch (int[][] board, int boardSize, int[] dx, int[] dy, Queue<int[]> queue){
        
        boolean allVisited = false;
        
   
        while(!queue.isEmpty()){
             
              System.out.println(nodeVisited);
            if (allVisited) {
                
                return true;
            }

        int[] current = queue.poll();

        
        for(int i = 0 ; i<8 ; i++){
            int newX = current[0]+dx[i];
            int newY = current[1]+dy[i];
            if(isAvailable(board,newX,newY,boardSize)){
                nodeVisited += 1;;
                queue.add(new int[]{newX, newY});
                board[newX][newY] =1 ;
               
            }
        } 
     
        if(nodeVisited  == boardSize * boardSize)
        allVisited = true;
    }
   
    return false;
    }



    public static boolean isAvailable(int[][] board, int nextX, int nextY,int boardSize){  
        if( nextX >= 0 && nextY >= 0 && nextX < boardSize && nextY < boardSize && board[nextX][nextY] == 0){
        return true;
        }
        else{
            return false;
        }
    }




    private static void printBoard(int[][] board) {
        int N = board.length;
        for (int i = 0; i < N; i++) { // Alt satırdan yukarı çık
            System.out.print((char) ('a' + N - 1 - i ) + "" + (N-i) ); // Harfler
            for (int j = 0; j < N; j++) { // Soldan sağa hareket
                System.out.printf("%3d ", board[i][j]);
            }
            System.out.println();
        }
    
        // Alt kısma sütun numaralarını ekle
        System.out.print("  "); // Boşluk hizalaması
        for (int j = 0; j < N; j++) {
            System.out.print((char) ('a' + j ) + "" + (j+1) +  "  ");
        }
        System.out.println();
    }

    
}
