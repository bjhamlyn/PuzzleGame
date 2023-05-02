import javax.swing.JOptionPane;

public class Test 
{

    public static void main(String[] args) 
    {
        PuzzleGame game = new PuzzleGame();

        //add a window listener to handle the closing of the window
        game.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent windowEvent)
            {
                int response = JOptionPane.showConfirmDialog(game, "Are you sure you want to exit the game?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(response == JOptionPane.YES_OPTION)
                {
                    System.exit(0);
                }
            }
        });
        
    }
}