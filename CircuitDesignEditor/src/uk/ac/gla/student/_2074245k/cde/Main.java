package uk.ac.gla.student._2074245k.cde;

import java.awt.Dimension;

import javax.swing.JFrame;

import uk.ac.gla.student._2074245k.cde.gui.MainFrame;

public final class Main 
{
    public static void main(String[] args) 
    {    	
        // Create custom JFrame.        
        MainFrame frame = new MainFrame();

        // Scale frame to fit components
        frame.pack();
        
        // Display the frame.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(1200, 600));
        frame.setMinimumSize(new Dimension(400, 400));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);             
    }
}
