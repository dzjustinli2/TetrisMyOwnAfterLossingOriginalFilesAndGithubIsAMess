/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetrismyownafterlossingoriginalfilesandgithubisamess;

/**
 *
 * @author justin
 */
import java.util.Arrays;

/**
 *
 * @author justin
 */
class Board {

    private final int WIDTH;
    private final int HEIGHT;
    private boolean[][] grid;
    private int[] widthOfRows;
    private int[] heightOfColumes;
    /*TODO: where to initalise "maxHeight", where it is declared or where it is needed, or in the constructor? */
    private int maxHeight;

    //where should "backupWidthOfRows" and "backupHeightOfColumes" 
    private boolean[][] backupGrid;
    private int[] backupWidthOfRows;
    private int[] backupHeightOfColumes;
    private int backupMaxHeight;

    private boolean committed = true;

    private final boolean DEBUG = true;

    Board(int widthOfBoard, int heightOfBoard) {
        WIDTH = widthOfBoard;
        HEIGHT = heightOfBoard;
        grid = new boolean[widthOfBoard][heightOfBoard];

        widthOfRows = new int[heightOfBoard];
        heightOfColumes = new int[widthOfBoard];

        //this is where "maxHeight" should be intialised, because when the "Board" object
        //has just been intialised, the "maxHeight" should be 0 because no piece has been placed on the board
        maxHeight = 0;

        backupGrid = new boolean[widthOfBoard][heightOfBoard];
        backupWidthOfRows = new int[heightOfBoard];
        backupHeightOfColumes = new int[widthOfBoard];
        backupMaxHeight = 0;

    }

    static final int PLACE_OK = 0;
    static final int PLACE_ROW_FILLED = 1;
    static final int PLACE_OUT_OF_BOUNDS = 2;
    static final int PLACE_BAD = 3;

    int getWIDTH() {
        return WIDTH;
    }

    int getHEIGHT() {
        return HEIGHT;
    }

    boolean[][] getGrid() {
        return grid;
    }

    boolean getGridAtCoordinate(int xCoordinate, int yCoordinate) {
        return (xCoordinate < 0 || yCoordinate < 0
                || xCoordinate >= WIDTH || yCoordinate >= HEIGHT
                || grid[xCoordinate][yCoordinate]);
    }

    int[] getWidthOfRows() {
        return widthOfRows;
    }

    int[] getHeightOfColumes() {
        return heightOfColumes;
    }

    int getMaxHeight() {
        return maxHeight;
    }

    int place(Piece placedPiece, int xCoordinateOnBoard, int yCoordinateOnBoard) {

        if (committed == true) {
            committed = false;

        } else {
            throw new RuntimeException("board is in the state of uncommited when the place method is called");
        }

        backup();

        int result = PLACE_OK;
        int placedPieceXCoordinate;
        int placePieceYCoordinate;

        TPoint[] body = placedPiece.getBody();
        for (int i = 0; i < body.length; i++) {

            placedPieceXCoordinate = xCoordinateOnBoard + body[i].x;
            placePieceYCoordinate = yCoordinateOnBoard + body[i].y;

            if (placedPieceXCoordinate < 0 || placePieceYCoordinate < 0
                    || placedPieceXCoordinate >= WIDTH || placePieceYCoordinate >= HEIGHT) {
                result = PLACE_OUT_OF_BOUNDS;

                //TODO: what if instead of "break;", I used "return;" instead, does it make any difference? 
                break;
            }

            if (grid[placedPieceXCoordinate][placePieceYCoordinate]) {
                result = PLACE_BAD;

                //TODO: what if instead of "break;", I used "return;" instead, does it make any difference? 
                break;
            }

            grid[placedPieceXCoordinate][placePieceYCoordinate] = true;

            if (heightOfColumes[placedPieceXCoordinate] < placePieceYCoordinate + 1) {
                heightOfColumes[placedPieceXCoordinate] = placePieceYCoordinate + 1;
            }

            widthOfRows[placePieceYCoordinate]++;

            if (widthOfRows[placePieceYCoordinate] == WIDTH) {
                result = PLACE_ROW_FILLED;
            }
        }

        //"computeMaxHeight()" is called every time the "int place(Piece placedPiece, int xCoordinateOnBoard, int yCoordinateOnBoard) {}
        //is called, but instead of going through "heightOfColumes" every time, is there an more smarter, convineint and faster way 
        //to recalculate "maxHeight"? Or calling "computeMaxHeight()" is good enough in these aspects .
        computeMaxHeight();

        sanityCheck();

        return result;
    }

    int getHeightAtColume(int xCoordinate) {
        return heightOfColumes[xCoordinate];
    }

    int getWidthAtRow(int yCoordiante) {
        return widthOfRows[yCoordiante];
    }

    private void backup() {
        System.arraycopy(widthOfRows, 0, backupWidthOfRows, 0, HEIGHT);
        System.arraycopy(heightOfColumes, 0, backupHeightOfColumes, 0, WIDTH);
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(grid[i], 0, backupGrid[i], 0, grid[i].length);
        }
        backupMaxHeight = maxHeight;
    }

    int clearRow() {

        if (committed) {
            committed = false;
            backup();
        }

        //"numberOfRowsCleared" is used recalculate "maxHeight",
        //also, should I return the "numberOfRowscleared"? 
        int numberOfRowsCleared = 0;
        //is "hasFilledRow" redundant 
        boolean hasFilledRow = false;
        int rowTo, rowFrom;

        // when "rowFrom < maxHeight", simply fill all rows above "rowTo" to false 
        //note: "rowFrom" is index and starts from 0, "maxHeight" starts at 1
        for (rowTo = 0, rowFrom = 1; rowFrom < maxHeight; rowTo++, rowFrom++) {
            if (widthOfRows[rowTo] == WIDTH) {
                //this if statment should only ever be executed once, i.e. when the first filled row
                //is detected, which in turn means that "hasFilledRow" should still be false 
                if (!hasFilledRow) {
                    hasFilledRow = true;
                    numberOfRowsCleared++;
                }
                while (widthOfRows[rowFrom] == WIDTH) {
                    numberOfRowsCleared++;
                    rowFrom++;
                }
            }

            while (hasFilledRow && widthOfRows[rowFrom] == WIDTH) {
                rowFrom++;
            }

            if (hasFilledRow) {
                copyRow(rowTo, rowFrom);
            }
        }

        fillEmptyRows(rowTo, rowFrom);

        // Rather than iterating through the grid every time to compute the heights
        // we do the following.
        // Case 1) Usually the height of every column decreases by the no of rows cleared
        // Case 2) The exception to this happens when there is a gap below the cleared out row.
        // Here we compute the height of the column using a for loop in O(n) time.
        // Example
        // ++++++  <-- row cleared out
        // +++ ++  <-- gap in the 4th column makes updated height
        // +++ ++  <-- of the 4th column using case 1 invalid
        // + ++++
        for (int i = 0; i < heightOfColumes.length; i++) {
            heightOfColumes[i] -= numberOfRowsCleared;
            if (heightOfColumes[i] > 0 && !grid[i][heightOfColumes[i] - 1]) {
                heightOfColumes[i] = 0;
                for (int j = 0; j < maxHeight; j++) {
                    if (grid[i][j]) {
                        heightOfColumes[i] = j + 1;
                    }
                }
            }
        }

        //TODO: need to recompute "maxHeight" here, can simply use "computeMaxHeight()" method
        //or come up with a quicker way to recompute "maxHeight" 
        computeMaxHeight();

        sanityCheck();

        return numberOfRowsCleared;
    }

    private int computeMaxHeight() {
//        //TODO: initalise "maxHeight" here is too late, but where should it be initialised then
        maxHeight = 0;

        for (int i = 0; i < heightOfColumes.length; i++) {
            if (maxHeight < heightOfColumes[i]) {
                maxHeight = heightOfColumes[i];
            }
        }
        return maxHeight;
    }

    private void copyRow(int rowTo, int rowFrom) {
        for (int i = 0; i < WIDTH; i++) {
            grid[i][rowTo] = grid[i][rowFrom];
            widthOfRows[rowTo] = widthOfRows[rowFrom];
        }
    }

    private void fillEmptyRows(int rowTo, int rowFrom) {
        //"rowTo += 1" here because the original "rowTo" is copied from the original "rowFrom",
        //therefore we need to add 1 to "rowTo" so we are filling rows all rows that are 
        //previously occupied but is empty now 
        for (; rowTo < rowFrom; rowTo++) {
            for (int i = 0; i < WIDTH; i++) {
                grid[i][rowTo] = false;
            }
            widthOfRows[rowTo] = 0;
        }
    }

    int dropHeight(Piece piece, int xCoordinateOfLowerLeftCornerOfThePieceOnBoard) {

        int pieceDropStopHeight = 0;

        int[] skirtOfPiece = piece.getSkirt();
        for (int i = 0; i < skirtOfPiece.length; i++) {
            int heightOfColumeAtXCoordinate = heightOfColumes[xCoordinateOfLowerLeftCornerOfThePieceOnBoard + i];
            //add 1 to "heightOfColumeAtXCoordinate because the piece is going to stop one level above the 
            //existing height of this colume 
            int stopHeight = heightOfColumeAtXCoordinate + 1;
            if (stopHeight > pieceDropStopHeight) {
                pieceDropStopHeight = stopHeight;
            }
        }

        return pieceDropStopHeight;
    }

//Wrong logic, but interesting to know why    
//    int dropHeight(Piece piece, int xCoordinate){
//        int dropStopHeight = 0;
//        TPoint[] body = piece.getBody();
//        for(int i = 0; i < body.length; i++){
//            int xCoordinateOfTPointOnBoard = body[i].x + xCoordinate;
//            int heightOfColumeAtXCoordinateAfterDrop = heightOfColumes[xCoordinateOfTPointOnBoard] + 1;
//            if(heightOfColumeAtXCoordinateAfterDrop > dropStopHeight){
//                dropStopHeight = heightOfColumeAtXCoordinateAfterDrop;
//            }
//        }
//        return dropStopHeight;
//    }
//    
    void undo() {
        if (committed) {
            return;
        }

        grid = backupGrid;
        widthOfRows = backupWidthOfRows;
        heightOfColumes = backupHeightOfColumes;
        maxHeight = backupMaxHeight;

        commit();

        sanityCheck();

    }

    void commit() {
        committed = true;
    }

    private void sanityCheck() {
        if (!DEBUG) {
            return;
        }

        int checkMaxHeight = 0;
        int[] checkHeightOfColumes = new int[WIDTH];
        int[] checkWidthOfRows = new int[HEIGHT];
        //TODO: simply checking for the width of each row may not be enought,
        //because it does not take into account where expected width and actual width of a row
        //is the same but where this row is filled is different 
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (grid[i][j]) {
                    checkWidthOfRows[j] += 1;
                    checkHeightOfColumes[i] = j + 1;
                }
            }
            if (checkHeightOfColumes[i] > checkMaxHeight) {
                checkMaxHeight = checkHeightOfColumes[i];
            }
        }

        if (!Arrays.equals(widthOfRows, checkWidthOfRows)) {
            throw new RuntimeException("Actual width of row and checked width of rows does not match");
        }

        if (!Arrays.equals(heightOfColumes, checkHeightOfColumes)) {
            throw new RuntimeException("Actual height of columes and checked height of columes does not match");
        }

        if (maxHeight != checkMaxHeight) {
            throw new RuntimeException("Actual max height does not equal to checked max height");
        }
    }

}
