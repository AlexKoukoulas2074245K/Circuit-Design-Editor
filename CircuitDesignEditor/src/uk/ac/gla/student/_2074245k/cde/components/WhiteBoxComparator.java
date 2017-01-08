package uk.ac.gla.student._2074245k.cde.components;

import java.util.Comparator;

public class WhiteBoxComparator implements Comparator<Component>
{
	private final boolean reversed;
	
	public WhiteBoxComparator(final boolean reversed)
	{
		this.reversed = reversed;
	}
	
	/* This is an approximate white box comparator. It will
	   compare two white boxes based on their rectangles.
	   A white box a with a smaller rectangle than white box b
	   cannot contain white box b and hence the result will
	   be based on that comparison */
	@Override
	public int compare(Component a, Component b) 
	{
		if (b.getRectangle().contains(a.getRectangle()))
		{
			return reversed ? 1 : -1;
		}
		else if (a.getRectangle().contains(b.getRectangle()))
		{
			return reversed ? -1 : 1;
		}
		else
		{
			long aArea = a.getRectangle().width * a.getRectangle().height;
			long bArea = b.getRectangle().width * b.getRectangle().height;
			
			if (aArea < bArea)
			{
				return reversed ? 1 : -1;				
			}
			else
			{
				return reversed ? -1 : 1;
			}
		}
	}		
}
