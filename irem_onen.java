import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Terrain {

    public static void moneyFlow(ArrayList<int[]> terrain, boolean[][] flooded) {
        boolean[][] visited;
        int N = terrain.size();
        int M = terrain.get(0).length;
        int[][] dir = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int n = 1; n < N - 1; n++) { // traversing the matrix
            for (int m = 1; m < M - 1; m++) {
                visited = new boolean[N][M];
                visited[n][m] = true;
                int[] initPos = new int[]{n, m};

                ArrayList<int[]> flow = new ArrayList<>(); //holds the indexes of the cells that money can flow to from each initial position separately
                flow.add(initPos);

                boolean spilled = false; //if flow reaches a boundary this boolean will be set to true showing that from the initial position a lake can not be formed
                while (flow.size() > 0) {
                    if (spilled) break;
                    // setting current cell to the last element of the flow
                    int n1 = flow.get(flow.size() - 1)[0];
                    int m1 = flow.get(flow.size() - 1)[1];
                    flow.remove(flow.size() - 1);
                    for (int i = 0; i < 8; i++) { // iterating through directions array and checking the neighbors of the current cell
                        if (terrain.get(n1 + dir[i][0])[m1 + dir[i][1]] <= terrain.get(initPos[0])[initPos[1]] && !visited[n1 + dir[i][0]][m1 + dir[i][1]]) {
                            if (n1 + dir[i][0] == 0 || n1 + dir[i][0] == N - 1 || m1 + dir[i][1] == 0 || m1 + dir[i][1] == M - 1) {
                                flooded[initPos[0]][initPos[1]] = false;
                                spilled = true; // a lake can not be formed since a boundary cell has been reached
                                break;
                            }
                            flow.add(new int[]{n1 + dir[i][0], m1 + dir[i][1]});
                            visited[n1 + dir[i][0]][m1 + dir[i][1]] = true;
                        }
                    }
                } // if a boundary cell has not been reached and the flow is emptied it means that from initial position a lake can be formed
                // only the initial cell is set as a lake
                if (!spilled) flooded[initPos[0]][initPos[1]] = true;

            }
        }
    }

    public static double volume(ArrayList<int[]> copyTerrain, ArrayList<int[]> terrain, boolean[][] flooded){ //calculates the final score
        int N = flooded.length;
        int M = flooded[0].length;
        int[][] dir = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        ArrayList<Integer> lakeCount = new ArrayList<Integer>();

        int minVal = Integer.MAX_VALUE;

        //if it is a lake its integer value in copyTerrain which represents its name is stored in the lakeCount
        for (int n = 1; n < N-1; n++){
            for (int m = 1; m < M; m++){
                if (flooded[n][m] && !lakeCount.contains(copyTerrain.get(n)[m])) lakeCount.add((copyTerrain.get(n)[m]));
            }
        }

        int[] smallestBorders = new int[lakeCount.size()];


        //traversing the matrix once again and holding the minimum value around each lake in smallestBorders
        for (int j = 0; j < lakeCount.size(); j++) {
            for (int n = 1; n < N-1; n++){
                for (int m = 1; m < M; m++){
                    if (copyTerrain.get(n)[m] == lakeCount.get(j) && flooded[n][m]){
                        for (int d = 0; d < 8; d++){
                            if (!flooded[n + dir[d][0]][m + dir[d][1]] && terrain.get(n + dir[d][0])[m + dir[d][1]] < minVal) {
                                smallestBorders[j] = terrain.get(n + dir[d][0])[m + dir[d][1]];
                                minVal = terrain.get(n + dir[d][0])[m + dir[d][1]];
                            }
                        }
                    }
                }
            }minVal = Integer.MAX_VALUE;
        }

        double[] volume = new double[lakeCount.size()];

        for (int j = 0; j < lakeCount.size(); j++) {
            int sum = 0;
            //for all the cells that are in a lake its value is decremented from the smallest value in its borders
            for (int n = 1; n < N-1; n++){
                for (int m = 1; m < M-1; m++){
                    if (lakeCount.get(j) == copyTerrain.get(n)[m] && flooded[n][m]){ //if current name in lakeCount is
                        // equal to the current cell's name then the single cell's volume should be added to sum.
                        // Single cell's volume is calculated by decrementing its height value from the smallest height
                        // value at the current lake's borders
                        sum += smallestBorders[j] - terrain.get(n)[m];
                    }
                }
            }
            //After finding the sum of the volumes of individual cells, the final sum's square root is added to the volume
            volume[j] = Math.pow(sum, 0.5);
        }

        double sum1 = 0;
        for(double j: volume){
            sum1 += j;
        }
        return sum1;}

    public static void namingLakes(ArrayList<int[]> copyTerrain, boolean[][] flooded) {
        int N = copyTerrain.size();
        int M = copyTerrain.get(0).length;
        boolean[][] visited = new boolean[N][M];
        int[][] dir = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
        int name = 0;

        for (int n = 1; n < N - 1; n++) {
            for (int m = 1; m < M - 1; m++) { // traversing the matrix
                if (!visited[n][m]) {
                    boolean in = false;
                    visited[n][m] = true;
                    ArrayList<int[]> flow = new ArrayList<>();
                    if (flooded[n][m]) { // if the initial position is a lake it is added to flow so that the while loop can work
                        flow.add(new int[]{n, m});
                        copyTerrain.get(n)[m] = name;
                    }
                    while (flow.size() > 0) {
                        in = true;
                        int n1 = flow.get(flow.size() - 1)[0];
                        int m1 = flow.get(flow.size() - 1)[1];
                        flow.remove(flow.size() - 1);
                        // for a single lake every cell of it is tagged with name with the similar algorithm in moneyFlow method
                        for (int i = 0; i < 8; i++) {
                            if (flooded[n1 + dir[i][0]][m1 + dir[i][1]] && !visited[n1 + dir[i][0]][m1 + dir[i][1]]) {
                                flow.add(new int[]{n1 + dir[i][0], m1 + dir[i][1]});
                                visited[n1 + dir[i][0]][m1 + dir[i][1]] = true;
                                copyTerrain.get(n1 + dir[i][0])[m1 + dir[i][1]] = name;
                            }
                        }
                    } // if while loop has once been entered it means the lake count is increased by one so the integer value name should be increased by one
                    if (in) name++;
                }
            }
        }
    }

    public static void increment(ArrayList<int[]> matrix, int modifiedRow, int modifiedCol, int move) { //to increment the specified values in the matrix
        if (move > 0) { // the parameters modifiedRow and modifiedCol are given by user
            matrix.get(modifiedRow)[modifiedCol] += 1;
            // increases the elements by one at the specified location
        }
    }

    public static void printFinalTerrain(ArrayList<int[]> copyTerrain,boolean[][] flooded) {

        int M = flooded[0].length;
        int N = flooded.length;

        for (int i = 0; i < N; i++){

            String numberStr = String.valueOf(i);
            int numDigits = numberStr.length();
            // printing out the row numbers with 3 spaces
            for (int z = numDigits; z < 3; z++) {
                System.out.print(" ");
            }
            System.out.print(i);

            for (int j = 0; j < M; j++){
                if (!flooded[i][j]){ // simply printing out the cells that are not lakes
                    numberStr = String.valueOf(copyTerrain.get(i)[j]);
                    numDigits = numberStr.length();
                    //the numbers that identify the row number should be printed after
                    for (int k = numDigits; k < 3; k++) {
                        System.out.print(" ");
                    }
                    System.out.print(copyTerrain.get(i)[j]);
                }
                else { // if it is a lake then its number from copyTerrain should be accessed
                    if (copyTerrain.get(i)[j] <= 25){ // for 0th lake character 'a' should be printed out whose ascii value 65 is
                        System.out.print("  " + (char) (copyTerrain.get(i)[j] + 65));
                    }
                    else  { // for printing out a name with two letters
                        char firstChar = (char) (copyTerrain.get(i)[j] / 26 + 64);
                        char secondChar = (char) (copyTerrain.get(i)[j] % 26 + 65);
                        System.out.print(" " + firstChar + secondChar);
                    }
                }
            }
            System.out.println();
        }
        //to print out the letters that identify column
        char column = 97;
        System.out.print("   ");
        for (int k = 0; k < M; k++) {
            if (k / 26 == 0) System.out.print("  " + column);
            else {
                char firstChar = (char) ((char) k / 26 + 96);
                System.out.print(" " + firstChar + column);
            }
            if (column < 122) column++;
            else column = 97;

        }
        System.out.println();
    }

    public static void printTerrain(ArrayList<int[]> matrix, int m, int move) { //to print out the terrain 2d array after every increment

        int row = 0;
        for (int[] oneRow : matrix) {
            String numberStr = String.valueOf(row);
            int numDigits = numberStr.length();
            for (int i = numDigits; i < 3; i++) // printing out the row numbers with 3 spaces
                System.out.print(" ");
            System.out.print(row);
            for (int oneNum : oneRow) {
                numberStr = String.valueOf(oneNum);
                numDigits = numberStr.length();
                for (int i = numDigits; i < 3; i++) // printing out the cells with 3 spaces
                    System.out.print(" ");
                System.out.print(oneNum);
            }
            row++;
            System.out.println();
        }
        //to print out the letters that identify column
        char column = 97;
        System.out.print("   ");
        for (int k = 0; k < m; k++) {
            if (k / 26 == 0) System.out.print("  " + column);
            else {
                char firstChar = (char) ((char) k / 26 + 96);
                System.out.print(" " + firstChar + column);
            }
            if (column < 122) column++;
            else column = 97;

        }
        System.out.println();
    }

}


public class irem_onen {
    public static void main(String[] args) throws FileNotFoundException {
        //getting the user input, opening the file and handling the error if file does not exist
        String fileName = "input2.txt";
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File can not be found");
            System.exit(0);
        }

        int M = 0;
        int N = 0;

        Scanner scanner = new Scanner(file);
        ArrayList<int[]> terrain = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] m = line.split(" ");

            if (m.length == 2) {
                M = Integer.parseInt(m[0]);
                N = Integer.parseInt(m[1]);
            }// if the number of elements in a row is equal to column number then this row should be stored as an array in the arraylist terrain
            else if (m.length == M) {
                int[] nums = new int[M];
                for (int i = 0; i < m.length; i++) {
                    nums[i] = Integer.parseInt(m[i]);
                }
                terrain.add(nums);
            }
        }
        int modifiedRow = 0;
        int modifiedCol = 0;
        int move = 0;
        boolean correctInput;
        //works while the moves are still being made
        while (move <= 10) {
            if (move == 0) {
                Terrain.printTerrain(terrain, M, move);
                move++;
            }

            System.out.printf("Add stone %d / 10 to coordinate:\n", move);
            Scanner scanner1 = new Scanner(System.in);
            String mod = scanner1.nextLine();

            //checking the correctness of the input
            if (mod.length() == 2) {
                if (mod.charAt(0) >= 97 && mod.charAt(0) <= 122 && mod.charAt(1) >= 48 && mod.charAt(1) <= 57) {
                    modifiedCol = mod.charAt(0) - 97;
                    modifiedRow = Integer.parseInt(mod.substring(1));
                    correctInput = true;
                } else correctInput = false;
            } else if (mod.length() == 3) {
                //first two are letters last one is a digit
                if (mod.charAt(0) >= 97 && mod.charAt(0) <= 122 && mod.charAt(1) >= 97 && mod.charAt(1) <= 122 && mod.charAt(2) >= 48 && mod.charAt(2) <= 57) {
                    modifiedCol = (mod.charAt(0) - 96) * 26 + mod.charAt(1) - 97;
                    modifiedRow = Integer.parseInt(mod.substring(2));
                    correctInput = true;

                } //first one is letter last two are digits
                else if (mod.charAt(0) >= 97 && mod.charAt(0) <= 122 && mod.charAt(1) > 48 && mod.charAt(1) <= 57 && mod.charAt(2) >= 48 && mod.charAt(2) <= 57) {
                    modifiedCol = mod.charAt(0) - 97;
                    modifiedRow = Integer.parseInt(mod.substring(1, 2)) * 10 + Integer.parseInt(mod.substring(2));
                    correctInput = true;

                } else correctInput = false;

            } else if (mod.length() == 4) {
                if (mod.charAt(0) >= 97 && mod.charAt(0) <= 122 && mod.charAt(1) >= 97 && mod.charAt(1) <= 122 && mod.charAt(2) > 48 && mod.charAt(2) <= 57 && mod.charAt(3) >= 48 && mod.charAt(3) <= 57) {
                    modifiedCol = (mod.charAt(0) - 96) * 26 + mod.charAt(1) - 97;
                    modifiedRow = Integer.parseInt(mod.substring(1, 2)) * 10 + Integer.parseInt(mod.substring(2));
                    correctInput = true;

                } else correctInput = false;
            } else correctInput = false;

            if (modifiedCol >= M || modifiedRow >= N)
                correctInput = false;

            if (correctInput) {
                move++;
                Terrain.increment(terrain, modifiedRow, modifiedCol, move);
                Terrain.printTerrain(terrain, M, move);
                if (move > 1) System.out.println("---------------");
            } else {
                System.out.println("Not a valid step!");
            }
        }


        boolean[][] flooded = new boolean[N][M]; //same as size as terrain the true elements in it will mark the lakes
        Terrain.moneyFlow(terrain, flooded); //determining where the lakes are at

        //deep copy of the terrain matrix
        ArrayList<int[]> copyTerrain = new ArrayList<>(terrain.size());
        for (int[] obj : terrain) {
            copyTerrain.add(obj.clone()); // Make a copy of each element
        }

        //giving the same integer value to cells that belong to the same lake then incrementing this value if another lake is found
        Terrain.namingLakes(copyTerrain, flooded);

        Terrain.printFinalTerrain(copyTerrain, flooded); //printing out the final version of terrain
        System.out.printf("Final score: %.2f", Terrain.volume(copyTerrain, terrain, flooded));

        scanner.close();
    }
}
