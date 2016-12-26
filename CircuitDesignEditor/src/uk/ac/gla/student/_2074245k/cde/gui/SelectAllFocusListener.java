package uk.ac.gla.student._2074245k.cde.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public final class SelectAllFocusListener implements FocusListener
{
	private JTextField component;
	
	public SelectAllFocusListener(final JTextField component)
	{		
		this.component = component;		
	}
	
	@Override
	public void focusGained(FocusEvent __) 
	{
		SwingUtilities.invokeLater(new Runnable() 
        {
            @Override
            public void run() { component.selectAll(); }
        });
	}

	@Override
	public void focusLost(FocusEvent __) 
	{
		SwingUtilities.invokeLater(new Runnable() 
        {
            @Override
            public void run() { component.select(0, 0); }
        });	
	}
}
