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


/**
 * This class creates TPoint objects on a board,
 * where coordinate x = 0 and y = 0 is at the lower left corner on the board,
 * x axis increases from left to right,
 * y axis increases from bottom to top 
 * @author justin
 */
class TPoint {
    /**
     * x coordinate of the point
     */
     final int x;
     
     /**
      * y coordinate of the point 
      */
     final int y;
    
    /**
     * construct a TPoint from give xCoordinate and y position
     * @param xCoordinate the x coordinate 
     * @param yCoordinate y coordinate 
     */
    TPoint(int xCoordinate, int yCoordinate){
        x = xCoordinate;
        y = yCoordinate;
    }
    
    /**
     * initialize a TPoint object from another TPoint object
     * @param aPoint 
     */
    TPoint(TPoint aPoint){
        this(aPoint.x, aPoint.y);
    }
    

    @Override
    public boolean equals(Object other){
        //standard two checks for equals()
        if(this == other) return true;
        if(!(other instanceof TPoint)) return false;
        
        //check if "other" has same xCoordinate and yCoordinate as us
        TPoint point = (TPoint)other;
        return (x == point.x && y == point.y);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + this.x;
        hash = 41 * hash + this.y;
        return hash;
    }
    
    @Override
    public String toString(){
        return "x: " + x + " y: " + y;
    }
    
    
}
