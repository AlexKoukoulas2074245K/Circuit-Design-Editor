package uk.ac.gla.student._2074245k.cde;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;

import uk.ac.gla.student._2074245k.cde.gui.MainFrame;

public final class Main 
{
    public static void main(String[] args) 
    {    	
        // Create a new JFrame.
        JFrame f = new JFrame("C.D.E (Circuit Design Editor)");
        MainFrame frame = new MainFrame();

        // Add components to the frame.
        try { f.getContentPane().add(frame.createComponents(f)); }
        catch (IOException e) { e.printStackTrace(); }
        
        // Scale frame to fit components
        f.pack();
        
        // Display the frame.
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(new Dimension(1200, 600));
        f.setMinimumSize(new Dimension(400, 400));
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        
        // Begin main execution
        frame.begin(f);
    }
}
