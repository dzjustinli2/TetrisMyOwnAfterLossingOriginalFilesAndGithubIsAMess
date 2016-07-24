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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author justin
 */
public class PieceTest {
    
    private Piece pyramid1, pyramid2, pyramid3, pyramid4;
    private Piece square1, square2, square3;
    private Piece stick1, stick2, stick3;
    private Piece L21, L22, L24;
    private Piece s, sRotated;
    private Piece[] pieces;

    
    @Before
    public void setUp() {
        pyramid1 = new Piece(Piece.PYRAMID_STRING);
        pyramid2 = pyramid1.computeNextRotation();
        pyramid3 = pyramid2.computeNextRotation();
        pyramid4 = pyramid3.computeNextRotation();
        
        square1 = new Piece(Piece.SQUARE_STRING);
        square2 = square1.computeNextRotation();
        square3 = square2.computeNextRotation();
        
        stick1 = new Piece(Piece.STICK_STRING);
        stick2 = stick1.computeNextRotation();
        stick3 = stick2.computeNextRotation();
        
        L21 = new Piece(Piece.L2_STRING);
        L22 = L21.computeNextRotation();
        L24 = L22.computeNextRotation().computeNextRotation();
        
        s = new Piece(Piece.S1_STRING);
        sRotated = s.computeNextRotation();
        
        pieces = Piece.getPieces();
    }

    /**
     * Test of getNextPiece method, of class Piece.
     */
    @Test
    public void testGetNextPiece() {
        System.out.println("getNextPiece");
        assertTrue(pyramid1.equals(pieces[Piece.PYRAMID]));
        assertTrue(pyramid2.equals(pieces[Piece.PYRAMID].getNextPiece()));
    }
    
    @Test
    public void testGetNextPiece2(){
        System.out.println("getNextPiece2");
        assertTrue(pyramid2.equals(pyramid1.getNextPiece()));
    }
    
    @Test
    public void testGetNextPiece3(){
        System.out.println("getNextPiece3");
        assertEquals(pyramid2, pyramid1.getNextPiece());

    }

    /**
     * Test of getSkirt method, of class Piece.
     */
    @Test
    public void testGetSkirt() {
        System.out.println("getSkirt");
        assertArrayEquals(new int[]{0, 1}, pyramid4.getSkirt());
    }

    /**
     * Test of getHeight method, of class Piece.
     */
    @Test
    public void testGetHeight() {
        System.out.println("getHeight");
        assertEquals(2, pyramid1.getHeight());
    }

    /**
     * Test of getWidth method, of class Piece.
     */
    @Test
    public void testGetWidth() {
        System.out.println("getWidth");
        assertEquals(3, pyramid1.getWidth());
    }

    /**
     * Test of equals method, of class Piece.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        boolean expResult = true;
        boolean result = pyramid1.equals(pyramid4.computeNextRotation());
        assertEquals(expResult, result);
    }
    
    @Test
    public void testEquals2(){
        System.out.println("equals2");
        boolean expectedResult = true;
        Piece rootPiece = pieces[Piece.PYRAMID].getNextPiece().getNextPiece().getNextPiece().getNextPiece();
        boolean result = pyramid1.equals(rootPiece);
        assertEquals(expectedResult, result);
    }

//    /**
//     * Test of hashCode method, of class Piece.
//     */
//    @Test
//    public void testHashCode() {
//        System.out.println("hashCode");
//        Piece instance = null;
//        int expResult = 0;
//        int result = instance.hashCode();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getPieces method, of class Piece.
     */
    @Test
    public void testGetPieces() {
        System.out.println("getPieces");
        Piece[] expResult = new Piece[]{
            new Piece(Piece.STICK_STRING),
            new Piece(Piece.L1_STRING),
            new Piece(Piece.L2_STRING),
            new Piece(Piece.S1_STRING),
            new Piece(Piece.S2_STRING),
            new Piece(Piece.SQUARE_STRING),
            new Piece(Piece.PYRAMID_STRING)
        };
        Piece[] result = Piece.getPieces();
        assertArrayEquals(expResult, result);

    }

    /**
     * Test of computeNextRotation method, of class Piece.
     */
    @Test
    public void testComputeNextRotation() {
        System.out.println("computeNextRotation");
        TPoint[] points = new TPoint[]{
            new TPoint(0, 1),
            new TPoint(1, 0),
            new TPoint(1, 1),
            new TPoint(1, 2)
        };
        Piece expResult = new Piece(points);
        Piece result = pyramid1.computeNextRotation();
        assertEquals(expResult, result);
    }
    
}