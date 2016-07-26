/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetrismyownafterlossingoriginalfilesandgithubisamess;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author justin
 */
import java.util.Arrays;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author justin
 */
public class BoardTest {

    Board board;
    Piece pyramid;
    Piece pyramid2;
    Piece pyramid3;
    Piece pyramid4;
    Piece square;
    Piece stick;
    Piece s2;
    Piece s22;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        board = new Board(3, 6);
        pyramid = new Piece(Piece.PYRAMID_STRING);
        //should I make "computeNextRotation()" method in "Piece" class private? 
        pyramid2 = pyramid.computeNextRotation();
        pyramid3 = pyramid2.computeNextRotation();
        pyramid4 = pyramid2.computeNextRotation().computeNextRotation();
        square = new Piece(Piece.SQUARE_STRING);
        stick = new Piece(Piece.STICK_STRING);
        s2 = new Piece(Piece.S2_STRING);
        s22 = s2.computeNextRotation();

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getWIDTH method, of class Board.
     */
    @Test
    public void testGetWIDTH() {
        System.out.println("getWIDTH");
        int expResult = 3;
        int result = board.getWIDTH();
        assertEquals(expResult, result);
        assertThat(result, is(equalTo(3)));

    }

    /**
     * Test of getHEIGHT method, of class Board.
     */
    @Test
    public void testGetHEIGHT() {
        System.out.println("getHEIGHT");
        int expResult = 6;
        int result = board.getHEIGHT();
        assertEquals(expResult, result);
        assertThat(result, is(equalTo(6)));
    }

    /**
     * Test of getGrid method, of class Board.
     */
    @Test
    public void testGetGrid() {
        System.out.println("getGrid");

        board.place(pyramid, 0, 0);

        boolean[][] expResult = {
            {true, false, false, false, false, false},
            {true, true, false, false, false, false},
            {true, false, false, false, false, false}
        };
        boolean[][] result = board.getGrid();
        assertTrue(Arrays.deepEquals(expResult, result));
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetGridAfterPlacingPyramidAndPyramid2() {
        board.place(pyramid, 0, 0);
        board.commit();
        board.place(pyramid2, 1, 1);
        boolean[][] result = board.getGrid();

        boolean[][] expResult = {
            {true, false, false, false, false, false},
            {true, true, true, false, false, false},
            {true, true, true, true, false, false}
        };

        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetGridAfterPlacingPyramidAndPyramid2AndSquare() {
        board.place(pyramid, 0, 0);
        board.commit();
        board.place(pyramid4, 0, 1);
        board.commit();
        board.place(square, 1, 3);

        boolean[][] result = board.getGrid();

        boolean[][] expResult = {
            {true, true, true, true, false, false},
            {true, true, true, true, true, false},
            {true, false, false, true, true, false}
        };

        assertArrayEquals(expResult, result);
        assertTrue(Arrays.deepEquals(expResult, result));
        assertThat(expResult, is(equalTo(result)));
    }

    /**
     * Test of getWidthOfRows method, of class Board.
     */
    @Test
    public void testGetWidthOfRows() {
        System.out.println("getWidthOfRows");

        board.place(pyramid, 0, 0);
        board.commit();
        board.place(pyramid4, 0, 1);
        board.commit();
        board.place(square, 1, 3);

        int[] expResult = {3, 2, 2, 3, 2, 0};
        int[] result = board.getWidthOfRows();

        assertArrayEquals(expResult, result);

    }

    /**
     * Test of getHeightOfColumes method, of class Board.
     */
    @Test
    public void testGetHeightOfColumes() {
        System.out.println("getHeightOfColumes");

        board.place(stick, 0, 0);
        board.commit();
        board.place(square, 1, 0);

        int[] expResult = {4, 2, 2};
        int[] result = board.getHeightOfColumes();

        assertArrayEquals(expResult, result);
    }

    /**
     * Test of getMaxHeight method, of class Board.
     */
    @Test
    public void testGetMaxHeight() {
        System.out.println("getMaxHeight");

        board.place(stick, 0, 0);
        board.commit();
        board.place(square, 1, 0);

        int expResult = 4;
        int result = board.getMaxHeight();
        assertEquals(expResult, result);

    }

    /**
     * Test of place method, of class Board.
     */
    @Test
    public void testPlace() {
        System.out.println("place");
        int result = board.place(pyramid, 1, 0);
        assertEquals(2, result);

        board.undo();

        assertEquals(0, board.getMaxHeight());

        int[] expectedWithOfRows = {0, 0, 0, 0, 0, 0};
        int[] expectedHeightOfColumes = {0, 0, 0};

        assertArrayEquals(expectedHeightOfColumes, board.getHeightOfColumes());
        assertArrayEquals(expectedWithOfRows, board.getWidthOfRows());
    }

    /**
     * Test of clearRow method, of class Board.
     */
    @Test
    public void testClearRow() {
        System.out.println("clearRow");

        board.place(pyramid, 0, 0);

        board.clearRow();

        int[] expectedHeightOfColumes = {0, 1, 0};

        assertArrayEquals(expectedHeightOfColumes, board.getHeightOfColumes());
//        assertEquals(2, board.getMaxHeight());

    }

    @Test
    public void testClearRowAfterPlacingPyramidAndPyramid3() {
        board.place(pyramid, 0, 0);
        board.commit();
        board.place(pyramid3, 0, 2);
        board.clearRow();

        int[] expectedHeightOfColumes = {0, 2, 0};

        assertArrayEquals(expectedHeightOfColumes, board.getHeightOfColumes());
    }

    @Test
    public void testClearRowPyramidStickS2() {
        board.place(pyramid, 0, 0);
        board.commit();
        board.place(stick, 0, 1);
        board.commit();
        board.place(s22, 1, 2);

        board.clearRow();

        boolean[][] expResult = {
            {true, true, true, false, false, false},
            {true, true, false, false, false, false},
            {false, false, true, false, false, false}
        };
        
        int[] expectedWithOfRows = { 2, 2, 2, 0, 0, 0};
        int[] expectedHeightOfColumes = { 3, 2, 3};
        
        assertArrayEquals(expResult, board.getGrid());
        assertTrue(Arrays.deepEquals(expResult, board.getGrid()));
        
        assertArrayEquals(expectedWithOfRows, board.getWidthOfRows());
        assertArrayEquals(expectedHeightOfColumes, board.getHeightOfColumes()); 
        assertEquals(3, board.getMaxHeight());
    }

    /**
     * Test of dropHeight method, of class Board.
     */
    @Test
    public void testDropHeight() {
        System.out.println("dropHeight");
        
        board.place(pyramid, 0, 0);
        board.commit();

        int xCoordinateOfLowerLeftCornerOfThePieceOnBoard = 0;
        
        int result = board.dropHeight(pyramid3, xCoordinateOfLowerLeftCornerOfThePieceOnBoard);
        
        assertEquals(3, result);
    }
    
    @Test
    public void testDropHeight2(){
        board.place(stick, 1, 0);
        board.commit();
        
        int result = board.dropHeight(pyramid3, 0);
        
        assertEquals(5, result);
    }

    /**
     * Test of undo method, of class Board.
     */
    @Test
    public void testUndo() {
        System.out.println("undo");
        board.place(pyramid, 0, 0);
        board.clearRow();
        board.undo();
        
        int[] expectedWidthOfRows = {3, 1, 0, 0 ,0 ,0};
        int[] expectedHeightOfColumes = {1, 2, 1};
        int expectedMaxHeight = 2;
        
        assertArrayEquals(expectedHeightOfColumes, board.getHeightOfColumes());
        assertArrayEquals(expectedWidthOfRows, board.getWidthOfRows());
        assertEquals(expectedMaxHeight, board.getMaxHeight());
    }
    
    @Test 
    public void testUndoAfterPlacingPyramidAndStickAndS2AndCallingClearRow(){
        board.place(pyramid, 0, 0);
        board.commit();
        board.place(stick, 0, 1);
        board.commit();
        board.place(s2, 0, 4);
        board.clearRow();
        
        board.undo();
        
        int[] expectedWidthOfRows = { 3, 2, 1, 1, 3, 2};
        int[] expectedHeightOfColumes = {6, 6, 5};
        int expectedMaxHeight = 6;
        
        assertArrayEquals(expectedHeightOfColumes, board.getHeightOfColumes());
        assertArrayEquals(expectedWidthOfRows, board.getWidthOfRows());
        assertEquals(expectedMaxHeight, board.getMaxHeight());
        
    }
    
    @Test
    public void testUndoAfterPlacingPyramidAndPyramid3AndSquare(){
        board.place(pyramid, 0, 0);
        board.commit();
        board.place(pyramid3, 0, 2);
        board.commit();
        board.place(square, 1, 4);
        board.clearRow();
        
        board.undo();
        
        int[] expectedHeightOfColumes = {4, 6, 6};
        int[] expectedWidthOfRows = { 3, 1, 1, 3, 2, 2};
        int expectedMaxHeight = 6;
        
        assertArrayEquals(expectedHeightOfColumes, board.getHeightOfColumes());
        assertArrayEquals(expectedWidthOfRows, board.getWidthOfRows());
        assertEquals(expectedMaxHeight, board.getMaxHeight());
    }
}