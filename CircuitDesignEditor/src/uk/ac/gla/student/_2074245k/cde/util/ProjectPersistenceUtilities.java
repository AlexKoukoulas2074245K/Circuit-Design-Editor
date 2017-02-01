package uk.ac.gla.student._2074245k.cde.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import uk.ac.gla.student._2074245k.cde.Main;
import uk.ac.gla.student._2074245k.cde.components.BlackBoxComponent;
import uk.ac.gla.student._2074245k.cde.components.Component;
import uk.ac.gla.student._2074245k.cde.components.ComponentRectangleComparator;
import uk.ac.gla.student._2074245k.cde.components.GateComponent;
import uk.ac.gla.student._2074245k.cde.components.HingeComponent;
import uk.ac.gla.student._2074245k.cde.components.LineSegmentComponent;
import uk.ac.gla.student._2074245k.cde.components.TextBoxComponent;
import uk.ac.gla.student._2074245k.cde.components.WhiteBoxComponent;
import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.gui.PortView;

public final class ProjectPersistenceUtilities 
{		
	public static final String HOME_DIR_PATH = System.getProperty("user.home") + "/CircuitDesignEditor/";	
	public static final File HOME_DIR        = new File(HOME_DIR_PATH);
	public static final File TEMP_FILE       = new File(HOME_DIR_PATH + ".temp");
	public static final File LIB_DIR         = new File(HOME_DIR_PATH + "lib/");
	
	private static enum LoadingMode
	{
		TEXT_BOX, HINGE, LINE_SEGMENT, GATE, BLACK_BOX, WHITE_BOX
	}
	
	public static LoadingResult openProjectNonPersistent(final MainCanvas canvas)
	{
		
		if (TEMP_FILE.exists())
		{			
			LoadingResult result = openProject(TEMP_FILE, true, canvas);
			TEMP_FILE.delete();
			return result;		
		}
		else
		{
			return new LoadingResult();
		}
	}
	
	public static LoadingResult openProject(final File file, final boolean nonPersistMode, final MainCanvas canvas)
	{
		if (!HOME_DIR.exists())
			HOME_DIR.mkdir();
				
		List<LineSegmentComponent> lineSegmentComponents = new ArrayList<LineSegmentComponent>();
		List<HingeComponent> hingeComponents             = new ArrayList<HingeComponent>();
		List<GateComponent> gateComponents               = new ArrayList<GateComponent>();
		List<BlackBoxComponent> blackBoxComponents       = new ArrayList<BlackBoxComponent>();
		List<WhiteBoxComponent> whiteBoxComponents       = new ArrayList<WhiteBoxComponent>();
		List<TextBoxComponent> textBoxComponents         = new ArrayList<TextBoxComponent>();
		Set<Component> loadedComponents                  = new HashSet<Component>();
		Dimension canvasDimension                        = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(file)))
		{	
			// Skip version comment
			String line = br.readLine();
			
			// Show warning on previous version
			if (!(line = br.readLine()).equals(Main.VERSION))
			{
				JOptionPane.showMessageDialog(null, "The save file was saved with a previous version of the software (" + line + ").\n" +
			                                        "The current version of the software is: " + Main.VERSION + ".\n"+
						                            "Errors might occurr when using different versions of the software", "IO Error", JOptionPane.WARNING_MESSAGE);
			}
			
			// Skip canvas dimensions comment
			line = br.readLine();
			
			// Read canvas dimensions
			line = br.readLine();
			canvasDimension = new Dimension(Integer.parseInt(line.split(",")[0]), Integer.parseInt(line.split(",")[1]));
			
			line = br.readLine();
			LoadingMode loadingMode = LoadingMode.TEXT_BOX;
			
			while ((line = br.readLine()) != null)
			{				
				switch (loadingMode)
				{
				    case TEXT_BOX:
				    {	
				    	if (line.startsWith("#"))
				    	{
				    		loadingMode = LoadingMode.HINGE;
				    		continue;
				    	}
				    	
				    	String[] lineComponents = line.split(",");
				    	Rectangle textBoxRect = new Rectangle(Integer.parseInt(lineComponents[1]),
				    			                              Integer.parseInt(lineComponents[2]),
				    			                              Integer.parseInt(lineComponents[3]),
				    			                              Integer.parseInt(lineComponents[4]));
				    			                             
				    	Color tbColor = new Color(Integer.parseInt(lineComponents[5]),
									    		  Integer.parseInt(lineComponents[6]),
									    		  Integer.parseInt(lineComponents[7]),
									    		  Integer.parseInt(lineComponents[8]));
				    	
				    	String textsString = lineComponents[9];
				    	textsString = textsString.substring(1, textsString.length() - 1);
				    	String[] texts = textsString.split("-");
				    	List<String> correctedTexts = new ArrayList<String>();
				    	for (String text: texts)
				    	{
				    		correctedTexts.add(text.replace("@", " "));
				    	}
				    	
				    	TextBoxComponent tb = new TextBoxComponent(canvas, "", 0, 0);
				    	tb.setRectangle(textBoxRect);
				    	tb.setTexts(correctedTexts);
				    	tb.setColor(tbColor);
				    	textBoxComponents.add(tb);
				    } break;
				    
					case HINGE:
					{
						if (line.startsWith("#"))
						{
							loadingMode = LoadingMode.LINE_SEGMENT;
							continue;
						}
						
						String[] lineComponents = line.split(",");
						
						int x = Integer.parseInt(lineComponents[1]);
						int y = Integer.parseInt(lineComponents[2]);
						
						Color hingeColor = new Color(Integer.parseInt(lineComponents[3]),
					    		                     Integer.parseInt(lineComponents[4]),
					    		                     Integer.parseInt(lineComponents[5]),
					    		                     Integer.parseInt(lineComponents[6]));
						
						boolean hasNub = lineComponents[7].equals("true");
						boolean isMovable = lineComponents[8].equals("true");
						boolean isInternal = lineComponents[9].equals("true");
						boolean isInverted = lineComponents[10].equals("true");
						
						PortView.PortLocation loc = PortView.PortLocation.valueOf(lineComponents[11]);
						PortView.PortResultDirectionality resultDir = PortView.PortResultDirectionality.NEUTRAL;
						String name = "";
						
						// Backwards compatibility assert
						if (lineComponents.length > 13)
						{							
							resultDir = PortView.PortResultDirectionality.valueOf(lineComponents[12]);
							name = lineComponents[13].replaceAll("@", " ");
						}
						else
						{
							name = lineComponents[12].replaceAll("@", " ");
						}
						
						HingeComponent hingeComponent = new HingeComponent(canvas, x, y, isMovable);								                                           					
						hingeComponent.setHasNub(hasNub);
						hingeComponent.setIsInverted(isInverted);
						hingeComponent.setColor(hingeColor);
						if (isInternal)
						{								
							hingeComponent.setInternalHingeInfo(loc, name);
						}
						hingeComponent.setExternalHingeInfo(loc, resultDir);
						hingeComponents.add(hingeComponent);
						
					} break;
					
					case LINE_SEGMENT: 
					{
						if (line.startsWith("#"))
						{
							for (int i = 0; i < 3; ++i)
								br.readLine();
							
							loadingMode = LoadingMode.GATE;
							continue;
						}
						
						String[] lineComponents = line.split(",");
						HingeComponent startPoint = hingeComponents.get(Integer.parseInt(lineComponents[1]));
						HingeComponent endPoint = hingeComponents.get(Integer.parseInt(lineComponents[2]));
																		
						boolean isMovable = lineComponents[3].equals("true");
						
						Color lsColor = new Color(Integer.parseInt(lineComponents[4]),
									    		  Integer.parseInt(lineComponents[5]),
									    		  Integer.parseInt(lineComponents[6]),
									    		  Integer.parseInt(lineComponents[7]));
						
						LineSegmentComponent ls = new LineSegmentComponent(canvas, startPoint, endPoint, isMovable);
						ls.setColor(lsColor);
						lineSegmentComponents.add(ls);
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
						
						
						String[] lineComponents = line.split(",");						
						GateComponent.GateType gateType = GateComponent.GateType.valueOf(lineComponents[1]);
						int x = Integer.parseInt(lineComponents[2]);
						int y = Integer.parseInt(lineComponents[3]);
						
						Color gateColor = new Color(Integer.parseInt(lineComponents[4]),
									      		    Integer.parseInt(lineComponents[5]),
									    		    Integer.parseInt(lineComponents[6]),
									    		    Integer.parseInt(lineComponents[7]));
						
						GateComponent gateComponent = new GateComponent(canvas, gateType, true, x, y);
						gateComponent.setColor(gateColor);
						
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
						
						gateComponents.add(gateComponent);
					} break;
					
					case BLACK_BOX:
					{
						if (line.startsWith("#"))
						{
							for (int i = 0; i < 4; ++i)
								br.readLine();
							
							loadingMode = LoadingMode.WHITE_BOX;
							continue;						
						}
												
						String[] coreComponentInfo = line.split(",");
						Rectangle componentRect = new Rectangle(Integer.parseInt(coreComponentInfo[1]),
								                                Integer.parseInt(coreComponentInfo[2]),
								                                Integer.parseInt(coreComponentInfo[3]),
								                                Integer.parseInt(coreComponentInfo[4]));
						
						Color bbColor = new Color(Integer.parseInt(coreComponentInfo[5]),
									    		  Integer.parseInt(coreComponentInfo[6]),
									    		  Integer.parseInt(coreComponentInfo[7]),
									    		  Integer.parseInt(coreComponentInfo[8]));
						
						String componentName = coreComponentInfo[9];
						int nameXOffset = Integer.parseInt(coreComponentInfo[10]);
						int nameYOffset = Integer.parseInt(coreComponentInfo[11]);
						
						BlackBoxComponent blackBox = new BlackBoxComponent(canvas, componentRect, componentName, nameXOffset, nameYOffset);
						blackBox.setColor(bbColor);
						
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
																		
						blackBoxComponents.add(blackBox);
					} break;
					
					case WHITE_BOX:
					{
						if (line.startsWith("#"))
						{							
							continue;						
						}
												
						String[] coreComponentInfo = line.split(",");
						Rectangle componentRect = new Rectangle(Integer.parseInt(coreComponentInfo[1]),
								                                Integer.parseInt(coreComponentInfo[2]),
								                                Integer.parseInt(coreComponentInfo[3]),
								                                Integer.parseInt(coreComponentInfo[4]));
						
						Color wbColor = new Color(Integer.parseInt(coreComponentInfo[5]),
									    		  Integer.parseInt(coreComponentInfo[6]),
									    		  Integer.parseInt(coreComponentInfo[7]),
									    		  Integer.parseInt(coreComponentInfo[8]));
									    		  
						String componentName = coreComponentInfo[9];
						int nameXOffset = Integer.parseInt(coreComponentInfo[10]);
						int nameYOffset = Integer.parseInt(coreComponentInfo[11]);
						
						boolean isOpaque = false;
						
						// Backwards compatibility assert
						if (coreComponentInfo.length > 12)
						{	
							isOpaque = coreComponentInfo[12].equals("true");							
						}
							
						
						WhiteBoxComponent whiteBox = new WhiteBoxComponent(canvas, componentRect, null, componentName, nameXOffset, nameYOffset);
						whiteBox.setColor(wbColor);
						whiteBox.setOpaque(isOpaque);
						
						line = br.readLine();
						if (line.length() > 0)
						{							
							String[] portIndices = line.split(",");
							for (String portIndex: portIndices)
							{
								whiteBox.addPort(lineSegmentComponents.get(Integer.parseInt(portIndex)));
							}
						}
						
						line = br.readLine();												
						if (line.length() > 0)
						{							
							String[] internalHorHingeIndices = line.split(",");						
							for (String internalHorHingeIndex: internalHorHingeIndices)
							{
								whiteBox.addInternalHorHinge(hingeComponents.get(Integer.parseInt(internalHorHingeIndex)));							
							}
						}
						
						line = br.readLine();						
						if (line.length() > 0)
						{
							String[] internalVerHingeIndices = line.split(",");
							for (String internalVerHingeIndex: internalVerHingeIndices)
							{
								whiteBox.addInternalVerHinge(hingeComponents.get(Integer.parseInt(internalVerHingeIndex)));							
							}							
						}
						
						line = br.readLine();
						if (line.length() > 0)
						{
							String[] innerComponents = line.split(",");
							
							for (String innerComponentString: innerComponents)
							{
								Component.ComponentType innerComponentType = Component.ComponentType.valueOf(innerComponentString.split("-")[0]);
								int innerComponentBucketIndex = Integer.parseInt(innerComponentString.split("-")[1]);
								
								switch (innerComponentType)
								{
									case HINGE:        whiteBox.addInnerComponentExternally(hingeComponents.get(innerComponentBucketIndex));break;
									case LINE_SEGMENT: whiteBox.addInnerComponentExternally(lineSegmentComponents.get(innerComponentBucketIndex)); break;
									case GATE:         whiteBox.addInnerComponentExternally(gateComponents.get(innerComponentBucketIndex)); break;
									case BLACK_BOX:    whiteBox.addInnerComponentExternally(blackBoxComponents.get(innerComponentBucketIndex));break;
									case WHITE_BOX:    whiteBox.addInnerComponentExternally(whiteBoxComponents.get(innerComponentBucketIndex));break;
									case TEXT_BOX:     whiteBox.addInnerComponentExternally(textBoxComponents.get(innerComponentBucketIndex));break;
								}
							}
						}
						whiteBoxComponents.add(whiteBox);
					}
				}						
			}
						
			for (Component comp: textBoxComponents)
			{
				if (!nonPersistMode)
				{
					canvas.addComponentToCanvas(comp);
				}
				loadedComponents.add(comp);
			}
			for (Component comp: hingeComponents)
			{
				if (!nonPersistMode)				
				{
					canvas.addComponentToCanvas(comp);					
				}
				loadedComponents.add(comp);
			}
			
			for (Component comp: lineSegmentComponents)
			{
				if (!nonPersistMode)				
				{
					canvas.addComponentToCanvas(comp);					
				}
				loadedComponents.add(comp);
			}
			
			for (Component comp: gateComponents)
			{
				if (!nonPersistMode)				
				{
					canvas.addComponentToCanvas(comp);					
				}
				loadedComponents.add(comp);
			}						
			
			for (Component comp: blackBoxComponents)
			{
				if (!nonPersistMode)
				{
					canvas.addComponentToCanvas(comp);
				}
				loadedComponents.add(comp);
			}
			
			for (Component comp: whiteBoxComponents)
			{
				if (!nonPersistMode)
				{
					canvas.addComponentToCanvas(comp);
				}
				loadedComponents.add(comp);
			}					
		}
		catch (Exception __)
		{					
			JOptionPane.showMessageDialog(null, "An error has occurred while loading project from file", "IO Error", JOptionPane.ERROR_MESSAGE);			
		}	
		
		return new LoadingResult(loadedComponents, canvasDimension);
	}
	
	public static void saveProjectNonPersistent(final Set<Component> components, final Dimension canvasDimension)
	{
		saveProject(TEMP_FILE, true, components, canvasDimension, false);
	}
	
	public static void saveAsLibrary(final File libFile, final Set<Component> components, final Dimension canvasDimension)
	{
		saveProject(libFile, false, components, canvasDimension, false);
	}
	
	public static void saveProject(final File file, final boolean nonPersistMode, final Set<Component> components, final Dimension canvasDimension, final boolean promptOnCompletion)
	{				
		if (!HOME_DIR.exists())
			HOME_DIR.mkdir();
						
		List<LineSegmentComponent> lineSegmentComponents = new ArrayList<LineSegmentComponent>();
		List<HingeComponent> hingeComponents             = new ArrayList<HingeComponent>();
		List<GateComponent> gateComponents               = new ArrayList<GateComponent>();
		List<BlackBoxComponent> blackBoxComponents       = new ArrayList<BlackBoxComponent>();
		List<WhiteBoxComponent> whiteBoxComponents       = new ArrayList<WhiteBoxComponent>();
		List<TextBoxComponent> textBoxComponents         = new ArrayList<TextBoxComponent>();
		
		for (Component component: components)
		{
			switch (component.getComponentType())
			{
				case LINE_SEGMENT: lineSegmentComponents.add((LineSegmentComponent)component); break;
				case HINGE:        hingeComponents.add((HingeComponent)component); break;
				case GATE:         gateComponents.add((GateComponent)component); break;
				case BLACK_BOX:    blackBoxComponents.add((BlackBoxComponent)component); break;
				case WHITE_BOX:    whiteBoxComponents.add((WhiteBoxComponent)component); break;
				case TEXT_BOX:     textBoxComponents.add((TextBoxComponent)component); break;
			}			
		}
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file)))
		{
			bw.write("#CDE File Version");
			bw.newLine();
			
			bw.write(Main.VERSION);
			bw.newLine();
			
			bw.write("#Canvas Dimensions width,height");
			bw.newLine();
						
			bw.write(canvasDimension.width + "," + canvasDimension.height);
			bw.newLine();
			
			bw.write("#TextBoxes: textboxIndex,textboxX,textboxY,textboxWidth,textboxHeight,red,green,blue,alpha,[textboxText1-textboxText2-...-textboxTextN-1]");
			bw.newLine();
			
			for (int i = 0; i < textBoxComponents.size(); ++i)
			{
				bw.write(i + "," + textBoxComponents.get(i).serialize());
				bw.newLine();
			}
			
			bw.write("#Hinges: hingeIndex,x,y,red,green,blue,alpha,hasNub,isMovable,isInternal,isInverted,location,resultDir,name");
			bw.newLine();
			
			for (int i = 0; i < hingeComponents.size(); ++i)
			{				
				bw.write(i + "," + hingeComponents.get(i).serialize()); 
				bw.newLine();				
			}
			
			bw.write("#Line Segments: lineSegmentIndex,startHingeIndex,endHingeIndex,isMovable,red,green,blue,alpha");
			bw.newLine();
			
			for (int i = 0; i < lineSegmentComponents.size(); ++i)
			{	
				bw.write(i + "," + 
			             hingeComponents.indexOf(lineSegmentComponents.get(i).getChildren().get(0)) + "," +
						 hingeComponents.indexOf(lineSegmentComponents.get(i).getChildren().get(1)) + "," + 
			             lineSegmentComponents.get(i).serialize());
				bw.newLine();
			}
			
			bw.write("#Gate First Line: gateIndex,gateType,x,y,red,green,blue,alpha\n" + 
					 "#Gate Second Line:  port0Index,port1Index,port2Index, ... ,portN-1Index\n" +
					 "#Gate Thrid Line: internalHorHinge0Index,internalHorHinge1Index, ... ,internalHorHingeN-1Index\n" +
					 "#Gate Fourth Line:  internalVerHinge0Index,internalVerHinge1Index, ... ,internalVerHingeN-1Index");
			bw.newLine();
			
			for (int i = 0; i < gateComponents.size(); ++i)
			{
				GateComponent gate = gateComponents.get(i);
				
				bw.write(i + "," + gate.serialize());
				bw.newLine();
				
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
			
			bw.write("#Black Box First Line:  blackBoxIndex,x,y,width,height,red,green,blue,alpha,name,nameXOffset,nameYOffset\n" +					 
					 "#Black Box Second Line: port0Index,port1Index,port2Index, ... ,portN-1Index\n"                          +
					 "#Black Box Third Line:  internalHorHinge0Index,internalHorHinge1Index, ... ,internalHorHingeN-1Index\n" +					 					 
					 "#Black Box Fourth Line: internalVerHinge0Index,internalVerHinge1Index, ... ,internalVerHingeN-1Index");			
			bw.newLine();
			
			for (int i = 0; i < blackBoxComponents.size(); ++i)
			{
				BlackBoxComponent bb = blackBoxComponents.get(i);
				bw.write(i + "," + bb.serialize());
				bw.newLine();
				
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
			
			bw.write("#White Box First Line:  whiteBoxIndex,x,y,width,height,red,green,blue,alpha,name,nameXOffset,nameYOffset,isOpaque\n" +					 
					 "#White Box Second Line: port0Index,port1Index,port2Index, ... ,portN-1Index\n"                          +
					 "#White Box Third Line:  internalHorHinge0Index,internalHorHinge1Index, ... ,internalHorHingeN-1Index\n" +					 					 
					 "#White Box Fourth Line: internalVerHinge0Index,internalVerHinge1Index, ... ,internalVerHingeN-1Index\n" +
					 "#White Box Fifth Line:  innerComponent0Index,innerComponent1Index, ... ,innerComponentN-1Index");			
			bw.newLine();
			
			try { whiteBoxComponents.sort(new ComponentRectangleComparator(false)); }
			catch (Exception e) {}
			
			for (int i = 0; i < whiteBoxComponents.size(); ++i)
			{
				WhiteBoxComponent wb = whiteBoxComponents.get(i);
				bw.write(i + "," + wb.serialize());
				bw.newLine();
				
				Iterator<Component> portsIter = wb.getPortsIterator();
				while (portsIter.hasNext())
				{
					Component port = portsIter.next();
					bw.write(String.valueOf(lineSegmentComponents.indexOf(port)));
					
					if (portsIter.hasNext())
						bw.write(",");
				}				
				bw.newLine();
				
				Iterator<Component> internalHorIter = wb.getInternalHorHingeIterator();
				while (internalHorIter.hasNext())
				{
					Component internalHorHinge = internalHorIter.next();
					bw.write(String.valueOf(hingeComponents.indexOf(internalHorHinge)));
					
					if (internalHorIter.hasNext())
						bw.write(",");
				}
				bw.newLine();				
				
				Iterator<Component> internalVerIter = wb.getInternalVerHingeIterator();
				while (internalVerIter.hasNext())
				{
					Component internalVerHinge = internalVerIter.next();
					bw.write(String.valueOf(hingeComponents.indexOf(internalVerHinge)));
					
					if (internalVerIter.hasNext())
						bw.write(",");
				}
				bw.newLine();				
				
				Iterator<Component> innerComponentsIter = wb.getInnerComponentsIter();
				while (innerComponentsIter.hasNext())
				{
					Component innerComponent = innerComponentsIter.next();
					bw.write(innerComponent.getComponentType() + "-");
					
					switch (innerComponent.getComponentType())
					{
						case HINGE:        bw.write(String.valueOf(hingeComponents.indexOf(innerComponent)));break;
						case LINE_SEGMENT: bw.write(String.valueOf(lineSegmentComponents.indexOf(innerComponent)));break;
						case GATE:         bw.write(String.valueOf(gateComponents.indexOf(innerComponent)));break;
						case BLACK_BOX:    bw.write(String.valueOf(blackBoxComponents.indexOf(innerComponent)));break;
						case WHITE_BOX:    bw.write(String.valueOf(whiteBoxComponents.indexOf(innerComponent)));break;
						case TEXT_BOX:     bw.write(String.valueOf(textBoxComponents.indexOf(innerComponent)));break;
					}
					
					if (innerComponentsIter.hasNext())
						bw.write(",");
				}
				
				if (i <= whiteBoxComponents.size() - 1)
					bw.newLine();
			}
			
			if (!nonPersistMode && promptOnCompletion)
			{
				JOptionPane.showMessageDialog(null, "Saved project successfully");								
			}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "Error writing project to file", "IO Error", JOptionPane.ERROR_MESSAGE);		
		}	
	}
}
