package uk.ac.gla.student._2074245k.cde.util;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import uk.ac.gla.student._2074245k.cde.components.Component;

public class LoadingResult
{
	public final Set<Component> loadedComponents;
	public final Dimension canvasDimension;
	
	public LoadingResult(final Set<Component> loadedComponents, final Dimension canvasDimension)
	{
		this.loadedComponents = loadedComponents;
		this.canvasDimension  = canvasDimension;
	}
	
	public LoadingResult()
	{
		loadedComponents = new HashSet<Component>();
		canvasDimension  = new Dimension();
	}
}
