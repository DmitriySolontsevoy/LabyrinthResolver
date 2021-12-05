package parser;

import lombok.SneakyThrows;

import java.io.File;
import java.util.Scanner;

public class InputMazeParser {

    @SneakyThrows
    public char[][] parseNotWeightedNotOrientedMaze() {
//        var scanner = new Scanner(new File(".\\src\\main\\resources\\_maze_large"));

        var scanner = new Scanner(new File(".\\src\\main\\resources\\straight_line_maze"));
//        var scanner = new Scanner(new File(".\\src\\main\\resources\\corner_maze"));
//        var scanner = new Scanner(new File(".\\src\\main\\resources\\open_field_maze"));
//        var scanner = new Scanner(new File(".\\src\\main\\resources\\net_trap_maze"));
//        var scanner = new Scanner(new File(".\\src\\main\\resources\\snowflake_maze"));
//        var scanner = new Scanner(new File(".\\src\\main\\resources\\pillar_room_maze"));

        var size = scanner.nextLine().split("\\s");

        var readArray = new char[Integer.parseInt(size[0])][Integer.parseInt(size[1])*3];

        for (int i = 0; i < Integer.parseInt(size[0]); i++) {
            readArray[i] = scanner.nextLine().toCharArray();
        }

        var array = new char[Integer.parseInt(size[0])][Integer.parseInt(size[1])];

        for (int i = 0; i < Integer.parseInt(size[0]); i++) {
            for (int j = 0; j < Integer.parseInt(size[1]); j++) {
                array[i][j] = readArray[i][j*3];
            }
        }

        return array;
    }

    @SneakyThrows
    public char[][] parseWeightedNotOrientedMaze() {
        var scanner = new Scanner(new File(".\\src\\main\\resources\\straight_line_maze_w"));
//        var scanner = new Scanner(new File(".\\src\\main\\resources\\corner_maze_w"));
//        var scanner = new Scanner(new File(".\\src\\main\\resources\\open_field_maze_w"));
//        var scanner = new Scanner(new File(".\\src\\main\\resources\\net_trap_maze_w"));
//        var scanner = new Scanner(new File(".\\src\\main\\resources\\snowflake_maze_w"));
//        var scanner = new Scanner(new File(".\\src\\main\\resources\\pillar_room_maze_w"));
        var size = scanner.nextLine().split("\\s");

        var readArray = new char[Integer.parseInt(size[0])*2 - 1][Integer.parseInt(size[1])*6];

        for (int i = 0; i < Integer.parseInt(size[0])*2 - 1; i++) {
            readArray[i] = scanner.nextLine().toCharArray();
        }

        var array = new char[Integer.parseInt(size[0])*2 - 1][Integer.parseInt(size[1])*2];

        for (int i = 0; i < Integer.parseInt(size[0])*2 - 1; i++) {
            for (int j = 0; j < Integer.parseInt(size[1])*2 - 1; j++) {
                array[i][j] = readArray[i][j*3];
            }
        }

        return array;
    }
}
