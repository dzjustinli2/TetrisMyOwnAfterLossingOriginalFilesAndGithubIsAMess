/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tetrismyownafterlossingoriginalfilesandgithubisamess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Justin
 */
public class JTetris extends JComponent {

    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;

    //extra blocks at the top for pieces to start.
    //if a piece is sticking up into this area 
    //when it has landed -- YOU ARE GAME OVER!
    public static final int TOP_SPACE = 4;
    
    //when this is true, plays a fixed sequence of 100 pieces
    boolean testMode = false;
    private final int TEST_LIMIT = 100;

    //state of the game 
    private boolean gameOn;
    int count;
    private Random random;
    double startTime;
    
    //draw optimise is default false, so debugging is easier 
    private boolean DRAW_OPTIMISE = false;

    //Board data structure 
    Board board;
    Piece[] pieces;
    
    //the current piece in play or null 
    Piece currentPiece;
    int currentX;
    int currentY;
    boolean moved;
    
    //the piece we are thinking about playing
    //that is set by computeNewPosition
    //eventhough storing this in variables is slightly questionable styel
    //TODO: find out why storing next piece in variable is of slightly questionalbe style
    private Piece newPiece;
    private int newX;
    private int newY;
    
    //controls
    Timer timer;
    int score;
    JLabel countLabel;
    JLabel scoreLabel;
    JCheckBox testButton;
    JButton stopButton;
    JButton startButton;
    JLabel timeLabel;
    JSlider speed;
    
    public final int DELAY = 400;

    static final int ROTATE = 0;
    static final int LEFT = 1;
    static final int RIGHT = 2;
    static final int DROP = 3;
    static final int DOWN = 4;

    JTetris(int pixels) {
        super();
        
        setPreferredSize(new Dimension((WIDTH * pixels) + 2,
                (HEIGHT + TOP_SPACE) * pixels + 2));
        gameOn = false;

        pieces = Piece.getPieces();
        board = new Board(WIDTH, HEIGHT + TOP_SPACE);

        registerKeyboardAction(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick(LEFT);
            }
        },
                "left",
                KeyStroke.getKeyStroke('4'),
                WHEN_IN_FOCUSED_WINDOW
        );
        
        registerKeyboardAction(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick(RIGHT);
            }
        }, 
                "right", 
                KeyStroke.getKeyStroke('6'), 
                WHEN_IN_FOCUSED_WINDOW
        );
        
        registerKeyboardAction(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick(ROTATE);
            }
        }, 
                "rotate", 
                KeyStroke.getKeyStroke('5'), 
                WHEN_IN_FOCUSED_WINDOW
        );
        
        registerKeyboardAction(
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick(DROP);
            }
        }, 
                "drop", 
                KeyStroke.getKeyStroke('r'), 
                WHEN_IN_FOCUSED_WINDOW
        );
        
        timer = new Timer(
                DELAY,
                new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick(DOWN);
            }
        }
        );
    }
    
    /**
     * set the interal state and start the timer so the game begins
     */
    public void startGame(){
        board = new Board(WIDTH, HEIGHT + TOP_SPACE);
        
        repaint();
        
        count = 0;
        score = 0;
        updateCounter();
        gameOn = true;
        
        //set mode based on checkbox at start of game 
        testMode = testButton.isSelected();
        
        if(testMode){
            random = new Random(0);
        } else {
            random = new Random();
        }
        
        toggleStartAndStopButtons();
        
        timeLabel.setText(" ");
        
        addNewPiece();
        
        timer.start();
        startTime = System.currentTimeMillis();
        
    }
    
    /**
     * updates the count/score labels with the latest values
     */
    private void updateCounter(){
        countLabel.setText("Pieces " + count);
        scoreLabel.setText("Score " + score);
    }
    
    private void toggleStartAndStopButtons(){
        startButton.setEnabled(!gameOn);
        stopButton.setEnabled(gameOn);
    }
    
    private void addNewPiece(){
        count++;
        score++;
        
        //TODO: more code here, dont know what it does 
//        if(testMode )

        //commit things the way they are 
        board.commit();
        currentPiece = null;
        
        Piece piece = pickNextPiece();
        
        int positionX = (board.getWIDTH() - piece.getWidth()) / 2;
        int positionY = board.getHEIGHT() - piece.getHeight();
        
        //add new piece to be in play 
        int result = setCurrent(piece, positionX, positionY);
        
        //TODO: understand why this code is included, but will probably never happens
        if(result > Board.PLACE_ROW_FILLED){
            stopGame();
        }
        
        updateCounter();
    }
    
    
    /**
     * select the next piece to use using the random generator 
     * set in startGame();
     * @return 
     */
    private Piece pickNextPiece(){
        int pieceNumber;
        pieceNumber = (int)(pieces.length * random.nextDouble());
        Piece piece = pieces[pieceNumber];
        return piece;
    }
    
    private int setCurrent(Piece piece, int x, int y){
        int result = board.place(piece, x, y);
        
        if(result <= Board.PLACE_ROW_FILLED){//success
            //repaints the rect where it used to be 
            //TODO: I dont really get the purpose of "if(currentPiece != null){}"
            if(currentPiece != null){
                repaintPiece(currentPiece, currentX, currentY);
            }
            currentPiece = piece;
            currentX = x;
            currentY = y;
            
            //repaint the rect where is it now 
            repaintPiece(currentPiece, currentX, currentY);
        } else {
            board.undo();
        }
        
        return result;
    }
    
    private void repaintPiece(Piece piece, int x, int y){
        if(DRAW_OPTIMISE){
            int positionX = xPixel(x);
            //dont understand how "yPixel" are calculated, dont understand the chain of methods 
            //called to calculate "positionY"
            int positionY = yPixel(y + piece.getHeight() - 1);
            int positionWidth = xPixel(x + piece.getWidth()) - positionX;
            int positionHeight = yPixel(y - 1) - positionY;
            
            repaint(positionX, positionY, positionWidth, positionHeight);
        } else {
            //not optimised -- rather than repaint
            //just the piece rect, repaint the whole board 
            repaint();
        }
    }
    
    private final int xPixel(int x){
        //TODO: why is "Math.round()" function needed here 
        return (Math.round(1 + (x * widthInPixelOfABlock())));
    }
    
    private final int yPixel(int y){
        return (Math.round(getHeight() - 1 - (y + 1) * heightInPixelsOfABlock()));
    }
    
    private final float widthInPixelOfABlock(){
        return (((float)(getWidth() - 2)) / board.getWIDTH());
    }
    
    private final float heightInPixelsOfABlock(){
        return (((float)(getHeight() - 2)) / board.getHEIGHT());
    }
    
    private void stopGame(){
        gameOn = false;
        toggleStartAndStopButtons();
        timer.stop();
        
        double timeTakenInMilliSecond = (System.currentTimeMillis() - startTime) / 10;
        timeLabel.setText(Double.toString(timeTakenInMilliSecond / 1000) + " seconds");
    }
    
    void computeNewPosition(int verb){
        newPiece = currentPiece;
        newX = currentX;
        newY = currentY;
        
        switch(verb){
            case LEFT:
                newX--;
                break;
            case RIGHT:
                newX++;
                break;
            case ROTATE:
                newPiece = newPiece.getNextPiece();
                //TODO: understand this code 
                //tricky:; make the piece appear to rotate about its center
                //can't just leave it at the same lower left origin as previous piece
                newX = newX + (currentPiece.getWidth() - newPiece.getWidth()) / 2;
                newY = newY + (currentPiece.getHeight() - newPiece.getWidth()) / 2;
                break;
            case DOWN:
                newY--;
                break;
            case DROP:
                newY = board.dropHeight(newPiece, newX);
                
                //TODO: understand this code 
                //trick: avoid the case where the drop would cause 
                //the piece to appear to move up 
                if(newY > currentY){
                    newY = currentY;
                }
                break;
            default:
                throw new RuntimeException("command does not exist");
        }
    }
    
    
    /**
     * called to change the position of current piece.
     * each key press calls this once with verbs 
     * "LEFT, RIGHT, ROTATE, DROP" for the user moves, 
     * and the timers calls it with the verb "DOWN" to move
     * the piece down one space
     * 
     * before this is called, the piece is at some location 
     * in the board. This advances the piece to be at its next location
     * 
     * NOTE: "tick()" method is overriden by smart brain class when the brain class control the game and autoplay the game 
     * @param verb command on current piece 
     */
    void tick(int verb){
        if(!gameOn){
            return;
        }
        
        //TODO: dont understand this if statement 
        if(currentPiece != null){
            board.undo();
        }
        
        computeNewPosition(verb);
        
        //try out the new position( rolls back if it doesn't work)
        int result = setCurrent(newPiece, newX, newY);
        
        //if row clearing is going to happen, draw the whole
        //board so the green row shows up
        //TODO: what green row? where is it set?
        if(result == Board.PLACE_ROW_FILLED){
            repaint();
        }
        
        boolean failed = (result >= Board.PLACE_OUT_OF_BOUNDS);
        
        //if it didn't work, put it back the way it was
        //TODO: dont know exactly how it works 
        if(failed){
            if(currentPiece != null){
                board.place(currentPiece, currentX, currentY);
            }
            repaintPiece(currentPiece, currentX, currentY);
        }
        
        //TODO: understand the condition "failed && verb == DOWN && !moved"
        if(failed && verb == DOWN && !moved){
            int cleared = board.clearRow();
            if(cleared > 0){
                //score goes up by 5, 10, 20, 40 for row 1, 2, 3, 4 row cleared respectively
                //clearing 4 gets you a beep!
                switch (cleared){
                    case 1:
                        score += 5;
                        break;
                    case 2:
                        score += 10;
                        break;
                    case 3: 
                        score += 20;
                        break;
                    case 4:
                        score += 40;
                        Toolkit.getDefaultToolkit().beep();
                        break;
                    default: //default cases could happen with non standard piece
                        score += 0;
                }
                updateCounter();
                repaint();
            }
            
            if(board.getMaxHeight() > board.getHEIGHT() - TOP_SPACE){
                stopGame();
            } else {
                addNewPiece();
            }
        }
        
        moved = (!failed && verb != DOWN);
    }
    
    @Override 
    public void paintComponent(Graphics g){
        //TODO: why "- 1" from both width and height 
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        
        //draw the line separating the top
        int spacerY = yPixel(board.getHEIGHT() - TOP_SPACE - 1);
        g.drawLine(0, spacerY, getWidth() - 1, spacerY);
        
        //check if we are drawing with clipping 
        //Shape shape = g.getClip()
        Rectangle clip = null;
        if(DRAW_OPTIMISE){
            clip = g.getClipBounds();
        }
        
        //factor a few things out to help the optimizer 
        final int dx = Math.round(widthInPixelOfABlock() - 2);
        final int dy = Math.round(heightInPixelsOfABlock() - 2);
        final int boardWidth = board.getWIDTH();
        
        int x, y;
        //loop through and draw all the blocks
        //left to right, bottom to top
        for(x = 0; x < boardWidth; x++){
            int left = xPixel(x); //the left pixel 
            
            //right pixel (useful to clip optimisation)
            int right = xPixel(x + 1) - 1;
            
            //skip this x if it is outside the clip rect 
            if(DRAW_OPTIMISE && clip != null){
                if((right < clip.x) || (left >= (clip.x + clip.width))){
                    continue;
                }
            }
            
            //draw from 0 up to the colume height 
            final int yHeight = board.getHeightAtColume(x);
            for(y = 0; y < yHeight; y++){
                if(board.getGridAtCoordinate(x, y)){
                    boolean filled = (board.getWidthAtRow(y) == boardWidth);
                    if(filled){
                        g.setColor(Color.green);
                    }
                    
                    g.fillRect(left + 1, yPixel(y) + 1, dx, dy);
                    
                    if(filled){
                        g.setColor(Color.BLACK);
                    }
                }
            }
        }
    }
    
    public void updateTimer(){
        double value = ((double)speed.getValue() / speed.getMaximum());
        timer.setDelay((int)(DELAY - value * DELAY));
    }
    
    public JComponent createControlPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        //count 
        countLabel = new JLabel("0");
        panel.add(countLabel);
        
        //score
        scoreLabel = new JLabel("0");
        panel.add(scoreLabel);
        
        //time
        timeLabel = new JLabel(" ");
        panel.add(timeLabel);
        
        panel.add(Box.createVerticalStrut(12));
        
        //start button
        startButton = new JButton("Start");
        panel.add(startButton);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        
        stopButton = new JButton("Stop");
        panel.add(stopButton);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopGame();
            }
        });
        
        toggleStartAndStopButtons();
        
        JPanel row = new JPanel();
        
        //speed slider 
        panel.add(Box.createVerticalStrut(12));
        row.add(new JLabel("Speed"));
        speed = new JSlider(0, 200, 100);
        speed.setPreferredSize(new Dimension(100, 15));
        
        updateTimer();
        row.add(speed);
        
        panel.add(row);
        speed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateTimer();
            }
        });
        
        testButton = new JCheckBox("Test sequence");
        panel.add(testButton);
        
        return panel;
       
    }
    
    public static JFrame createFrame(JTetris tetris){
        JFrame frame = new JFrame("Tetris");
        JComponent container = (JComponent)frame.getContentPane();
        container.setLayout(new BorderLayout());
        
        //install the passed in JTetris in the center 
        container.add(tetris, BorderLayout.CENTER);
        
        //create and install the panel of control
        JComponent controls = tetris.createControlPanel();
        container.add(controls, BorderLayout.EAST);
        
        //add the quit button last so it's at hte bottom 
        controls.add(Box.createVerticalStrut(12));
        JButton quit = new JButton("Quit");
        controls.add(quit);
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        
        return frame;
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored){
            
        }
        
        JTetris tetirs = new JTetris(16);
        JFrame frame = JTetris.createFrame(tetirs);
        frame.setVisible(true);
    }
    
}
