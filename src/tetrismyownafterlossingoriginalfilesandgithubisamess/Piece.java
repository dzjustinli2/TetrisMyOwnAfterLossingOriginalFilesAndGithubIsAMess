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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

//Review 1: Want to make "Piece next" variable "final", 
//but don't know how it can be initialised in the constructor
//Review 2: expect "testGetNextPiece2" and "testGetNextPiece3" in "PieceTest.java"
//to pass, but both test methods failed, but can't find anything wrong with my "Piece" class code
//Review 3: Testing edge cases I have missed 

class Piece {

    /**
     * array of "TPoints" that make up the current instance of "Piece",
     */
    private final TPoint[] body;
    /**
     * skirt is an int[] array with as many elements as the piece is wide,
     * the skirt stores the lowest y value that appears in the body for each x value in the piece,
     * the x values are the index into the array
     */
    private final int[] skirt;
    /**
     * the width of piece after been placed on the lower left hand of the board
     */
    private final int width;
    
    /**
     * the height of piece after been placed on the lower left hand of the board
     */
    private final int height;
    /**
     * the resulting piece after rotating the current piece in counter clock direction
     */
    private Piece next; 

    /**
     * array that represent all possible pieces
     */
    private static final Piece[] pieces;

    static {
        pieces = Piece.getPieces();
    }

    /**
     * construct a new piece object from a existing array of TPoint, 
     * @param points - array of TPoint that together make up the body of a Piece object
     */
    Piece(TPoint[] points) {
        
        body = new TPoint[points.length];
        for (int i = 0; i < points.length; i++) {
            body[i] = new TPoint(points[i]);
        }

        //calculate "width" and "height" of the piece using the "body" 
        int xMax = 0;
        int yMax = 0;
        for (TPoint point : body) {
            if (xMax < point.x) {
                xMax = point.x;
            }
            if (yMax < point.y) {
                yMax = point.y;
            }
        }
        
        //because coordinate of board starts from (x, y) where (0, 0) from the lower left corner,
        //therefore in order to calculate actual "width" and "height" of the piece,
        //have to add 1 to the x coordinate of the piece at the far right to get the width,
        //and add 1 to the y coordinate of the piece at the top to get the height
        width = xMax + 1;
        height = yMax + 1;

        skirt = calculateSkirt();
    }

    /**
     * convenient initializer, initialize a new Piece from a existing piece
     * @param piece 
     */
    Piece(Piece piece) {
        this(piece.body);
    }

    /**
     * convenient initializer, initialize a new Piece from a string that represent 
     * x and y coordinate of TPoints that make up the body of a piece 
     * @param points a string that contain x and y coordinates of TPoints in pair, 
     * e.g. "0 0   0 1" represents two TPoints at coordinate (0, 0) and (0, 1)          
     */
    Piece(String points) {
        this(parsePoints(points));
    }

    /**
     * public getter for the next piece after rotation 
     * @return the resulting piece after the current piece rotate 90 degrees in counter clock direction 
     */
     Piece getNextPiece() {
        return next;
    }

     int[] getSkirt() {
        return skirt;
    }

     int getHeight() {
        return height;
    }

     int getWidth() {
        return width;
    }
     
     TPoint[] getBody(){
         return body;
     }

    /**
     * goes through each TPoint that make up the current piece, 
     * for a given x coordinate,
     * find the piece located on this x coordinate that has the lowest y coordinate.
     * 
     * x coordinate of each TPoint can be thought of as index number in "int[] skirt"
     * @return an array that contain the lowest y coordinate in each x coordinate 
     */
    private int[] calculateSkirt() {

        int[] pieceSkirt = new int[width];
        Arrays.fill(pieceSkirt, height - 1);
        for (TPoint point : body) {
            if (point.y < pieceSkirt[point.x]) {
                pieceSkirt[point.x] = point.y;
            }
        }
        return pieceSkirt;
    }

    // String constants for the standard 7 tetris pieces
    static final String STICK_STRING = "0 0	0 1	 0 2  0 3";
    static final String L1_STRING = "0 0	0 1	 0 2  1 0";
    static final String L2_STRING = "0 0	1 0 1 1	 1 2";
    static final String S1_STRING = "0 0	1 0	 1 1  2 1";
    static final String S2_STRING = "0 1	1 1  1 0  2 0";
    static final String SQUARE_STRING = "0 0  0 1  1 0  1 1";
    static final String PYRAMID_STRING = "0 0  1 0  1 1  2 0";

    // Indexes for the standard 7 pieces in the pieces array
    static final int STICK = 0;
    static final int L1 = 1;
    static final int L2 = 2;
    static final int S1 = 3;
    static final int S2 = 4;
    static final int SQUARE = 5;
    static final int PYRAMID = 6;

    /**
     * calculate all possible pieces in all possible orientation
     * @return array of all possible pieces in its original position, 
     *      each piece contain a variable "next", which is a pointer to the piece 
     *      after the current piece rotate 90 degrees in counter clock direction 
     */
    static Piece[] getPieces() {
        //lazy evaluation -- create static array when needed
        if (Piece.pieces == null) {
            Piece[] piecesCreated = new Piece[]{
                makeFastRotation(new Piece(STICK_STRING)),
                makeFastRotation(new Piece(L1_STRING)),
                makeFastRotation(new Piece(L2_STRING)),
                makeFastRotation(new Piece(S1_STRING)),
                makeFastRotation(new Piece(S2_STRING)),
                makeFastRotation(new Piece(SQUARE_STRING)),
                makeFastRotation(new Piece(PYRAMID_STRING))
            };
            return piecesCreated;
        } else {
            return Piece.pieces;
        }
    }

    /**
     * calculate all possible position of a piece
     * @param passedInPiece the piece in its original position
     * @return the passed in original piece, but now this piece has a variable "next",
     * that is initialized and is pointing to the piece after rotation was completed,
     * this forms a circle of points (i.e. variable "next") which eventually points back to the original piece.
     */
    private static Piece makeFastRotation(Piece passedInPiece) {
        Piece currentPiece = new Piece(passedInPiece);

        Piece myRootPiece = currentPiece;

        while (true) {
            Piece nextRotation = currentPiece.computeNextRotation();
            if (nextRotation.equals(myRootPiece)) {
                currentPiece.next = myRootPiece;
                break;
            }
            currentPiece.next = nextRotation;
            currentPiece = currentPiece.next;
        }

        return myRootPiece;
    }

    //should I make "computeNextRotation()" method in "Piece" class private? 
    /**
     * calculate the position of next piece after the current piece rotate 90 degrees 
     * in counter clock direction
     * @return the position of next piece after rotation 
     */
    Piece computeNextRotation() {
        TPoint[] rotatedBody = new TPoint[body.length];

        for (int i = 0; i < body.length; i++) {
            rotatedBody[i] = new TPoint(height - 1 - body[i].y, body[i].x);
        }

        Piece computedNext = new Piece(rotatedBody);
        return computedNext;
    }

    @Override
    public boolean equals(Object obj) {
        //standard equals() technique 1
        if (this == obj) {
            return true;
        }
        //standard equals() technique 2
        if (!(obj instanceof Piece)) {
            return false;
        }

        Piece other = (Piece) obj;

        if (other.body.length != body.length) {
            return false;
        }

        List<TPoint> myPoints = Arrays.asList(body);
        List<TPoint> otherPoints = Arrays.asList(other.body);

        return myPoints.containsAll(otherPoints);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Arrays.deepHashCode(this.body);
        hash = 37 * hash + Arrays.hashCode(this.skirt);
        hash = 37 * hash + this.width;
        hash = 37 * hash + this.height;
        hash = 37 * hash + Objects.hashCode(this.next);
        return hash;
    }

    /**
     * convert a string that represents the x and y coordinate of an array of TPoints 
     * to an actual array of TPoints
     * @param string a string that represents the x and y coordinate of an array of TPoints
     * @return array of TPoints that represents the body location of a piece 
     */
    private static TPoint[] parsePoints(String string) {
        List<TPoint> points = new ArrayList<TPoint>();
        StringTokenizer tok = new StringTokenizer(string);
        try {
            while (tok.hasMoreTokens()) {
                int x = Integer.parseInt(tok.nextToken());
                int y = Integer.parseInt(tok.nextToken());

                points.add(new TPoint(x, y));
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Could not parse x,y string:" + string);
        }

        // Make an array out of the collection
        TPoint[] array = points.toArray(new TPoint[0]);
        return array;
    }

}