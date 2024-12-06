import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;



public class Main {

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

}



    

