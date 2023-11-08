// İrem Önen 2022400279
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

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