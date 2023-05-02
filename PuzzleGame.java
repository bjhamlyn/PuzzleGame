import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.Image;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;

public class PuzzleGame extends JFrame 
{
    //set the min and max number of rows and columns allowed when playing the game
    final int MIN_ROWS = 2;
    final int MAX_ROWS = 6;
    final int MIN_COLS = 2;
    final int MAX_COLS = 6;
    final int HEIGHT = 400;
    int WIDTH = 100; //allow the width to be resized
    //set the starting row and column size for the game
    int NUMROWS = 4;
    int NUMCOLS = 3;
    JPanel mainPanel = new JPanel();
    ArrayList<FancyButton> allButtons = new ArrayList<FancyButton>();//will store references to the buttons
    BufferedImage imageSource;//holds the original image
    BufferedImage imageResized;//holds the original image adjusted to new height
    int movesCount = 0;//keeps track of how many moves the user has made
    JLabel moveCounterLabel = new JLabel();

    //method to update the move counter 
    public void updateMoveCounterLabel() {
        moveCounterLabel.setText("Moves: " + movesCount);
    }    

    //method to handle the button click event
    public void ClickEventHandler(ActionEvent e)
    {
        //whats' the button that was clicked?
        FancyButton btnClicked = (FancyButton) e.getSource();

        //what's the button that was clicked?
        int i = allButtons.indexOf(btnClicked);//find the button's index in the list
        int row = i/NUMCOLS;//find its row and column in the grid
        int col = i%NUMCOLS;

        int iempty = -1; //find the empty button in the list
        for(int j=0; j<allButtons.size(); j++)
        {
            if(allButtons.get(j).getIcon() ==null)
            {
                iempty = j;
                break;
            }
        }


        int rowEmpty = iempty / NUMCOLS; //find its row and column
        int colEmpty = iempty % NUMCOLS;

        //check if clicked button is adjacent (same row + adjacent cols, or same col + adjacent rows) to the empty one
        if((row==rowEmpty && Math.abs(col-colEmpty)==1) || (col == colEmpty && Math.abs(row-rowEmpty)==1))
        {
            //SWAP THE two buttons
            Collections.swap(allButtons,i , iempty);
            movesCount++; //increment moves counter
            updateMoveCounterLabel(); // Update the move counter label
            UpdateButtons();//update the buttons         
        }
    }

    //method to update the buttons on the game board
    public void UpdateButtons() 
    {
        mainPanel.removeAll();
        mainPanel.setLayout(new GridLayout(NUMROWS, NUMCOLS));
        for (var btn : allButtons) 
        {
            mainPanel.add(btn);
        }
        mainPanel.validate();
        
        //check if the puzzle is solved
        boolean isSolved = true;
        for (int bCount = 0; bCount < allButtons.size() - 1; bCount++) 
        {
            if (allButtons.get(bCount).getId() >= allButtons.get(bCount + 1).getId()) 
            {
                isSolved = false;
                break;
            }
        }
        if (isSolved) 
        {
            //display congratulation message if puzzle was solved
            String message = "Congratulations! You have completed the puzzle in " + movesCount + " moves!";
            JOptionPane.showMessageDialog(this, message);
        }
    }
    
    //method to reset the game
    public void ResetGame()
    {
        //create a new list to store the updated buttons
        ArrayList<FancyButton> newButtons = new ArrayList<FancyButton>(); // create a new list to store the new buttons
        for (int i = 0; i < NUMROWS; i++) 
        {
            for (int j = 0; j < NUMCOLS; j++) 
            {
                int index = i * NUMCOLS + j;

                //create an image slice of the resized image
                Image imageSlice = createImage(new FilteredImageSource
                (imageResized.getSource(), new CropImageFilter(j * WIDTH / NUMCOLS, i * HEIGHT / NUMROWS, WIDTH / NUMCOLS, HEIGHT / NUMROWS)));

                //create a new button and add it to the new list of buttons
                FancyButton btn = new FancyButton(index);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                newButtons.add(btn);

                //set the last button to have no borders or icon (our blank space)
                if ((i * NUMCOLS) + j == NUMROWS * NUMCOLS - 1) 
                {
                    btn.setBorderPainted(false);
                    btn.setContentAreaFilled(false);
                } 
                else 
                {
                    //add the click event handler to the button
                    btn.setIcon(new ImageIcon(imageSlice));
                }

                btn.addActionListener(e -> ClickEventHandler(e));
            }
        }

        //clear the old list of buttons and add the new list to it
        allButtons.clear(); // clear the old list
        allButtons.addAll(newButtons); // add the new buttons to the old list
        Collections.shuffle(allButtons); // shuffle the buttons
        movesCount = 0; // reset the moves counter
        UpdateButtons(); // update the main panel with the new buttons
    }

    //load an image from a .jpg file
    public BufferedImage LoadImage(String filePath) throws IOException
    {
        return ImageIO.read(new File(filePath));  
    }

    //resize the image and panel based on the dimensions of the original image to maintain the original aspect ratio
    public void resizePanelAndImage()
    {
        int sourceHeight = imageSource.getHeight();//obtain image height
        int sourceWidth = imageSource.getWidth();//obtain image width
        WIDTH = (int) (((double) sourceWidth * HEIGHT) / sourceHeight);
        imageResized = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);//resize image
        var g = imageResized.createGraphics();//generate resized image
        g.drawImage(imageSource, 0, 0, WIDTH, HEIGHT, null);
        g.dispose();
    }

    //create the buttons for the puzzle
    public void createButtons() 
    {
        //clear the list of all buttons and remove them from the main panel
        allButtons.clear();
        mainPanel.removeAll();
        //set a grid layout on the main panel with the number of rows and columns as defined by (NUMROWS, NUMCOLS)
        mainPanel.setLayout(new GridLayout(NUMROWS, NUMCOLS));
    
        //loop throuhg each cell of the grid
        for (int i = 0; i < NUMROWS; i++) {
            for (int j = 0; j < NUMCOLS; j++) 
            {
                //compute a unique ID for each button using its index
                int id = i * NUMCOLS + j;
                //create teh image for each button by cropping the image into grids in accordance with NUMCOLS and NUMROWS
                Image imageSlice = createImage(new FilteredImageSource(imageResized.getSource(), new CropImageFilter(j * WIDTH / NUMCOLS, i * HEIGHT / NUMROWS, WIDTH / NUMCOLS, HEIGHT / NUMROWS)));
    
                //create a new FancyButton object for each button
                FancyButton btn = new FancyButton(id);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                //add the button to the list of all buttons
                allButtons.add(btn);

                //if the current button is the last button in the grid, (our blank space), remove the border and background
                if (i == NUMROWS - 1 && j == NUMCOLS - 1) 
                {
                    btn.setBorderPainted(false);
                    btn.setContentAreaFilled(false);
                } 
                //otherwise set the button's icon to the image created earlier
                else 
                {
                    btn.setIcon(new ImageIcon(imageSlice));
                }

                //add an action event listener to handle button clicks
                btn.addActionListener(e -> ClickEventHandler(e));
                //add the button to the main panel
                mainPanel.add(btn);
            }
        }

    }

    //ctor for the PuzzleGame
    public PuzzleGame()
    {
        setContentPane(mainPanel);//adds the panel to the frame
        mainPanel.setLayout(new GridLayout(NUMROWS, NUMCOLS));

        //load the image and resize the panel and image to fit
        try 
        {
            imageSource = LoadImage("PuzzleImage.jpg");
            resizePanelAndImage();
        } 
        catch (Exception e) 
        {
            //if an error occurs, show an error message
            JOptionPane.showMessageDialog(this, e.getMessage(), "Critical Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //create the buttons and shuffle them randomly
        createButtons();
        Collections.shuffle(allButtons);//Shuffle the buttons 
        UpdateButtons();

        //add the menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        //create a reset option to start over and reshuffle the buttons
        JMenuItem resetItem = new JMenuItem("Reset");
        resetItem.addActionListener(e -> 
        {
            resizePanelAndImage();
            createButtons();
            ResetGame();
        });
        gameMenu.add(resetItem);
        //create an exit option 
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e ->
        {
            int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit the game?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);//ask the user if they are sure they want to exit the game
            if(response == JOptionPane.YES_OPTION)
            {
                setDefaultCloseOperation(EXIT_ON_CLOSE);
                dispose();
            }
            else
            {
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            }
        });

        //add the ability to change the number of rows and columns in the puzzle
        JMenu rowsMenu = new JMenu("Rows");
        for (int i = 2; i <= 6; i++) 
        {
            int num = i;
            JMenuItem itemRows = new JMenuItem("" + num);
            itemRows.addActionListener(e -> 
            {
                NUMROWS = num;
                resizePanelAndImage();
                createButtons();
                ResetGame();
            });
            rowsMenu.add(itemRows);
        }

        JMenu colsMenu = new JMenu("Columns");
        for (int i = MIN_COLS; i <= MAX_COLS; i++) 
        {
            int num = i;
            JMenuItem itemCols = new JMenuItem("" + num);
            itemCols.addActionListener(e -> 
            {
                NUMCOLS = num;
                resizePanelAndImage();
                createButtons();
                ResetGame();
            });
            colsMenu.add(itemCols);
        }

        //add the rows, columns, and exit options to the dropdown menu
        gameMenu.add(rowsMenu);
        gameMenu.add(colsMenu);
        gameMenu.add(exitItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
        setTitle("The Puzzle Game - CSC 205");
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(800, HEIGHT);
    }
}

// Custom JButton subclass for the puzzle buttons
class FancyButton extends JButton 
{
    // Private field to store the ID of the button
    private int id;

    // Constructor for the FancyButton class, which sets the ID of the button and adds mouse listeners to change its border color
    public FancyButton(int id) 
    {
        this.id = id;
        addMouseListener(new MouseAdapter() 
        {
            //change the border color when the mouse hovers over the button
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                setBorder(BorderFactory.createLineBorder(Color.YELLOW));
            }

            // change the border color back after the mouse leaves
            @Override
            public void mouseExited(MouseEvent e) 
            {
                setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
        });
    }

    // Getter method for the ID of the button
    public int getId() 
    {
        return id;
    }
}

