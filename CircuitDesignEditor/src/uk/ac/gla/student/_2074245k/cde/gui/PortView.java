package uk.ac.gla.student._2074245k.cde.gui;

public final class PortView
{
	public enum PortLocation
	{
		LEFT, RIGHT, TOP, BOTTOM 
	}

	public PortLocation portLocation;
	public Integer actualPosition;
	public Float normalizedPosition;
	public String portName;
	public boolean isInverted;
	
	public PortView (final PortLocation portLocation,
				     final Integer actualPosition,
			         final Float normalizedPosition, 
			         final String portName)
	{
		this.portLocation       = portLocation;
		this.actualPosition     = actualPosition;
		this.normalizedPosition = normalizedPosition;
		this.portName           = portName;
		this.isInverted         = false;
	}
}