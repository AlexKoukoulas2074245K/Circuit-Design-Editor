package uk.ac.gla.student._2074245k.cde.util;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import uk.ac.gla.student._2074245k.cde.components.BlackBoxComponent;
import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.Component.ComponentType;
import uk.ac.gla.student._2074245k.cde.components.ConcreteComponent;
import uk.ac.gla.student._2074245k.cde.components.GateComponent;
import uk.ac.gla.student._2074245k.cde.components.HingeComponent;
import uk.ac.gla.student._2074245k.cde.components.LineSegmentComponent;
import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.gui.PortView;

public final class ProjectPersistenceUtilities 
{
	private static enum LoadingMode
	{
		HINGE, LINE_SEGMENT, GATE, BLACK_BOX
	}
	
	public static void openProject(final File file, final MainCanvas canvas)
	{		
		List<LineSegmentComponent> lineSegmentComponents = new ArrayList<LineSegmentComponent>();
		List<HingeComponent> hingeComponents             = new ArrayList<HingeComponent>();
		List<ConcreteComponent> concreteComponents       = new ArrayList<ConcreteComponent>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file)))
		{	
			String line = br.readLine();
			LoadingMode loadingMode = LoadingMode.HINGE;
			
			while ((line = br.readLine()) != null)
			{				
				switch (loadingMode)
				{
					case HINGE:
					{
						if (line.startsWith("#"))
						{
							loadingMode = LoadingMode.LINE_SEGMENT;
							continue;
						}
						
						String[] lineComponents = (line.split("\\s+")[2]).split(",");
						
						int x = Integer.parseInt(lineComponents[0]);
						int y = Integer.parseInt(lineComponents[1]);
						boolean hasNub = lineComponents[2].equals("true");
						boolean isMovable = lineComponents[3].equals("true");
						boolean isInternal = lineComponents[4].equals("true");
						boolean isInverted = lineComponents[5].equals("true");
						
						PortView.PortLocation loc = PortView.PortLocation.valueOf(lineComponents[6]);
						
						String name = lineComponents[7].replaceAll("@", " ");
						
						HingeComponent hingeComponent = new HingeComponent(canvas, x, y, isMovable);								                                           					
						hingeComponent.setHasNub(hasNub);
						hingeComponent.setIsInverted(isInverted);
						if (isInternal)
						{								
							hingeComponent.addInternalHingeInfo(loc, name);
						}
							
						hingeComponents.add(hingeComponent);
						
					} break;
					
					case LINE_SEGMENT: 
					{
						if (line.startsWith("#"))
						{
							for (int i = 0; i < 4; ++i)
								br.readLine();
							
							loadingMode = LoadingMode.GATE;
							continue;
						}
						
						String[] lineComponents = (line.split("\\s+")[3]).split(",");
						HingeComponent startPoint = hingeComponents.get(Integer.parseInt(lineComponents[0]));
						HingeComponent endPoint = hingeComponents.get(Integer.parseInt(lineComponents[1]));
																		
						boolean isMovable = lineComponents[2].equals("true");
						
						lineSegmentComponents.add(new LineSegmentComponent(canvas, startPoint, endPoint, isMovable));
					} break;
					
					case GATE:
					{
						if (line.startsWith("#"))
						{
							for (int i = 0; i < 3; ++i)
								br.readLine();
							
							loadingMode = LoadingMode.BLACK_BOX;
							continue;						
						}											
						
						GateComponent.GateType gateType = GateComponent.GateType.valueOf(line);
						
						line = br.readLine();
						
						String[] posComponents = line.split(",");
						int x = Integer.parseInt(posComponents[0]);
						int y = Integer.parseInt(posComponents[1]);
						
						GateComponent gateComponent = new GateComponent(canvas, gateType, true, x, y);
												
						line = br.readLine();
						if (line.length() > 0)
						{							
							String[] portIndices = line.split(",");
							for (String portIndex: portIndices)
							{
								gateComponent.addPort(lineSegmentComponents.get(Integer.parseInt(portIndex)));
							}
						}
						
						line = br.readLine();												
						if (line.length() > 0)
						{							
							String[] internalHorHingeIndices = line.split(",");						
							for (String internalHorHingeIndex: internalHorHingeIndices)
							{
								gateComponent.addInternalHorHinge(hingeComponents.get(Integer.parseInt(internalHorHingeIndex)));							
							}
						}
						
						line = br.readLine();						
						if (line.length() > 0)
						{
							String[] internalVerHingeIndices = line.split(",");
							for (String internalVerHingeIndex: internalVerHingeIndices)
							{
								gateComponent.addInternalVerHinge(hingeComponents.get(Integer.parseInt(internalVerHingeIndex)));							
							}							
						}
						
						concreteComponents.add(gateComponent);
					} break;
					
					case BLACK_BOX:
					{
						if (line.startsWith("#"))
						{
							continue;						
						}
												
						String[] coreComponentInfo = line.split(",");
						Rectangle componentRect = new Rectangle(Integer.parseInt(coreComponentInfo[0]),
								                                Integer.parseInt(coreComponentInfo[1]),
								                                Integer.parseInt(coreComponentInfo[2]),
								                                Integer.parseInt(coreComponentInfo[3]));
						String componentName = coreComponentInfo[4];
						int nameXOffset = Integer.parseInt(coreComponentInfo[5]);
						int nameYOffset = Integer.parseInt(coreComponentInfo[6]);
						
						BlackBoxComponent blackBox = new BlackBoxComponent(canvas, componentRect, componentName, nameXOffset, nameYOffset);
						
						line = br.readLine();
						if (line.length() > 0)
						{							
							String[] portIndices = line.split(",");
							for (String portIndex: portIndices)
							{
								blackBox.addPort(lineSegmentComponents.get(Integer.parseInt(portIndex)));
							}
						}
						
						line = br.readLine();												
						if (line.length() > 0)
						{							
							String[] internalHorHingeIndices = line.split(",");						
							for (String internalHorHingeIndex: internalHorHingeIndices)
							{
								blackBox.addInternalHorHinge(hingeComponents.get(Integer.parseInt(internalHorHingeIndex)));							
							}
						}
						
						line = br.readLine();						
						if (line.length() > 0)
						{
							String[] internalVerHingeIndices = line.split(",");
							for (String internalVerHingeIndex: internalVerHingeIndices)
							{
								blackBox.addInternalVerHinge(hingeComponents.get(Integer.parseInt(internalVerHingeIndex)));							
							}							
						}
																		
						concreteComponents.add(blackBox);
					}
				}						
			}
						
			for (Component comp: hingeComponents)
			{
				canvas.addNewComponent(comp);
			}
			
			for (Component comp: lineSegmentComponents)
			{
				canvas.addNewComponent(comp);
			}
			
			for (Component comp: concreteComponents)
			{
				canvas.addNewComponent(comp);
			}						
			
			JOptionPane.showMessageDialog(null, "Loaded project successfully");
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "An error has occurred while loading project from file", "IO Error", JOptionPane.ERROR_MESSAGE);			
		}	
	}
	
	public static void saveProject(final File file, final List<Component> components)
	{
		List<LineSegmentComponent> lineSegmentComponents = new ArrayList<LineSegmentComponent>();
		List<HingeComponent> hingeComponents             = new ArrayList<HingeComponent>();
		List<ConcreteComponent> concreteComponents           = new ArrayList<ConcreteComponent>();
		
		for (Component component: components)
		{
			switch (component.getComponentType())
			{
				case LINE_SEGMENT: lineSegmentComponents.add((LineSegmentComponent)component); break;
				case HINGE:        hingeComponents.add((HingeComponent)component); break;
				case GATE: case BLACK_BOX: concreteComponents.add((ConcreteComponent)component); break;
			}
		}
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file)))
		{
			bw.write("#Hinges: Hinge Index:<Index> x,y,hasNub,isMovable,isInternal,isInverted,location,name");
			bw.newLine();
			
			for (int i = 0; i < hingeComponents.size(); ++i)
			{				
				bw.write("Hinge Index:" + i + " " + hingeComponents.get(i).serialize()); 
				bw.newLine();				
			}
			
			bw.write("#Line Segments: Line Segment Index:<Index> startHingeIndex,endHingeIndex,isMovable");
			bw.newLine();
			
			for (int i = 0; i < lineSegmentComponents.size(); ++i)
			{	
				bw.write("Line Segment Index:" + i + " " + 
			             hingeComponents.indexOf(lineSegmentComponents.get(i).getStartPoint()) + "," +
						 hingeComponents.indexOf(lineSegmentComponents.get(i).getEndPoint()) + "," + 
			             lineSegmentComponents.get(i).serialize());
				bw.newLine();
			}
			
			bw.write("#Gate First Line:  gateType\n" +
					 "#Gate Second Line: x,y\n" + 
					 "#Gate Third Line:  port0Index,port1Index,port2Index, ... ,portN-1Index\n" +
					 "#Gate Fourth Line: internalHorHinge0Index,internalHorHinge1Index, ... ,internalHorHingeN-1Index\n" +
					 "#Gate Fifth Line:  internalVerHinge0Index,internalVerHinge1Index, ... ,internalVerHingeN-1Index");
			bw.newLine();
			
			for (ConcreteComponent gate: concreteComponents)
			{
				if (gate.getComponentType() != ComponentType.GATE)
					continue;
				
				bw.write(gate.serialize());
				
				Iterator<Component> portIter = gate.getPortsIterator();
				while (portIter.hasNext())
				{
					Component port = portIter.next();
					bw.write(String.valueOf(lineSegmentComponents.indexOf(port)));
					
					if (portIter.hasNext())
						bw.write(",");
				}
				bw.newLine();
				
				Iterator<Component> internalHorIter = gate.getInternalHorHingeIterator();
				while (internalHorIter.hasNext())
				{
					Component internalHorHinge = internalHorIter.next();
					bw.write(String.valueOf(hingeComponents.indexOf(internalHorHinge)));
					
					if (internalHorIter.hasNext())
						bw.write(",");
				}
				bw.newLine();
				
				Iterator<Component> internalVerIter = gate.getInternalVerHingeIterator();
				while (internalVerIter.hasNext())
				{
					Component internalVerHinge = internalVerIter.next();
					bw.write(String.valueOf(hingeComponents.indexOf(internalVerHinge)));
					
					if (internalVerIter.hasNext())
						bw.write(",");
				}
				bw.newLine();										
			}
			
			bw.write("#Black Box First Line:  x,y,width,height,name,nameXOffset,nameYOffset\n"                                +					 
					 "#Black Box Second Line: port0Index,port1Index,port2Index, ... ,portN-1Index\n"                          +
					 "#Black Box Third Line:  internalHorHinge0Index,internalHorHinge1Index, ... ,internalHorHingeN-1Index\n" +					 					 
					 "#Black Box Fourth Line: internalVerHinge0Index,internalVerHinge1Index, ... ,internalVerHingeN-1Index");
			
			bw.newLine();
			
			for (ConcreteComponent bb: concreteComponents)
			{
				if (bb.getComponentType() != ComponentType.BLACK_BOX)
					continue;
				
				bw.write(bb.serialize());
				
				Iterator<Component> portsIter = bb.getPortsIterator();
				while (portsIter.hasNext())
				{
					Component port = portsIter.next();
					bw.write(String.valueOf(lineSegmentComponents.indexOf(port)));
					
					if (portsIter.hasNext())
						bw.write(",");
				}				
				bw.newLine();
				
				Iterator<Component> internalHorIter = bb.getInternalHorHingeIterator();
				while (internalHorIter.hasNext())
				{
					Component internalHorHinge = internalHorIter.next();
					bw.write(String.valueOf(hingeComponents.indexOf(internalHorHinge)));
					
					if (internalHorIter.hasNext())
						bw.write(",");
				}
				bw.newLine();				
				
				Iterator<Component> internalVerIter = bb.getInternalVerHingeIterator();
				while (internalVerIter.hasNext())
				{
					Component internalVerHinge = internalVerIter.next();
					bw.write(String.valueOf(hingeComponents.indexOf(internalVerHinge)));
					
					if (internalVerIter.hasNext())
						bw.write(",");
				}
				bw.newLine();				
			}
			
			JOptionPane.showMessageDialog(null, "Saved project successfully");				
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "Error writing project to file", "IO Error", JOptionPane.ERROR_MESSAGE);		
		}	
	}
}
