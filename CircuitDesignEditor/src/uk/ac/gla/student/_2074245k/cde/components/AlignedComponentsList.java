package uk.ac.gla.student._2074245k.cde.components;

import java.util.ArrayList;
import java.util.List;

public final class AlignedComponentsList 
{
	private List<Component> horAlignedComponents, verAlignedComponents;
	
	public AlignedComponentsList()
	{
		horAlignedComponents = new ArrayList<Component>();
		verAlignedComponents = new ArrayList<Component>();
	}
	
	public void addHorAlignedComponent(final Component component) { addHorAlignedComponentOrReplaceFirst(component); }
	public void addVerAlignedComponent(final Component component) { addVerAlignedComponentOrReplaceFirst(component); }
	
	public void addHorAlignedComponentOrReplaceFirst(final Component component)
	{
		if (horAlignedComponents.size() == 0)
			horAlignedComponents.add(component);
		else
			horAlignedComponents.set(0, component);
	}
	
	public void addVerAlignedComponentOrReplaceFirst(final Component component)
	{
		if (verAlignedComponents.size() == 0)
			verAlignedComponents.add(component);
		else
			verAlignedComponents.set(0, component);
	}
	
	public List<Component> getHorAlignedComponents() { return horAlignedComponents; }
	public List<Component> getVerAlignedComponents() { return verAlignedComponents; }
	
	public boolean hasHorAlignedComponents() { return horAlignedComponents.size() > 0; }
	public boolean hasVerAlignedComponents() { return verAlignedComponents.size() > 0; }
}
