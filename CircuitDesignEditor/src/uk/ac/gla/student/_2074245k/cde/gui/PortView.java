package uk.ac.gla.student._2074245k.cde.gui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
* The PortView class provides a visual representation of ports 
* owned by ConcreteComponent and their various attributes 
*
* @author  Alexios Koukoulas
* @version 0.40
* @since   2/2/2017 
*/
public final class PortView
{
    public static final int ARROW_HALF_LEN = 4;
    
    public enum PortLocation
    {
        LEFT, RIGHT, TOP, BOTTOM 
    }

    public enum PortResultDirectionality
    {
        NEUTRAL, INPUT, OUTPUT
    }
    
    public PortLocation portLocation;
    public PortResultDirectionality portResultDir;
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
        this.portResultDir      = PortResultDirectionality.NEUTRAL;
        this.actualPosition     = actualPosition;
        this.normalizedPosition = normalizedPosition;
        this.portName           = portName;
        this.isInverted         = false;
    }
    
    public void render(final Graphics g, final int viewWidth, final int viewHeight, final Rectangle componentRect)
    {
        switch (portLocation)
        {
            case LEFT:
            {
                g.drawLine(0, (int)(normalizedPosition * viewHeight), componentRect.x - (isInverted ? ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR : 0), (int)(normalizedPosition * viewHeight));
                
                if (isInverted)
                {                        
                    g.drawOval(componentRect.x - ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR,
                               (int)(normalizedPosition * viewHeight) - ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2,
                               ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR, 
                               ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR);
                }
                
                if (portResultDir == PortResultDirectionality.INPUT)
                {    
                    g.setColor(Colors.CANVAS_BKG_COLOR);
                    g.drawLine(0, (int)(normalizedPosition * viewHeight), ARROW_HALF_LEN, (int)(normalizedPosition * viewHeight));
                    
                    g.setColor(Colors.DEFAULT_COLOR);
                    g.drawLine(ARROW_HALF_LEN, (int)(normalizedPosition * viewHeight), 0, (int)(normalizedPosition * viewHeight) - ARROW_HALF_LEN);
                    g.drawLine(ARROW_HALF_LEN, (int)(normalizedPosition * viewHeight), 0, (int)(normalizedPosition * viewHeight) + ARROW_HALF_LEN);
                }
                else if (portResultDir == PortResultDirectionality.OUTPUT)
                {
                    g.drawLine(0, (int)(normalizedPosition * viewHeight), ARROW_HALF_LEN, (int)(normalizedPosition * viewHeight) - ARROW_HALF_LEN);
                    g.drawLine(0, (int)(normalizedPosition * viewHeight), ARROW_HALF_LEN, (int)(normalizedPosition * viewHeight) + ARROW_HALF_LEN);
                }
                
                g.drawString(portName, componentRect.x + 5, (int)(normalizedPosition * viewHeight) + ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2);
            } break;
            
            case RIGHT:
            {
                g.drawLine(componentRect.x + componentRect.width + (isInverted ? ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR : 0), (int)(normalizedPosition * viewHeight), viewWidth, (int)(normalizedPosition * viewHeight));
                
                if (isInverted)
                {                        
                    g.drawOval(componentRect.x + componentRect.width,
                               (int)(normalizedPosition * viewHeight) - ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2,
                               ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR, 
                               ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR);
                }
                
                if (portResultDir == PortResultDirectionality.INPUT)
                {                        
                    g.setColor(Colors.CANVAS_BKG_COLOR);
                    g.drawLine(viewWidth - ARROW_HALF_LEN, (int)(normalizedPosition * viewHeight), viewWidth, (int)(normalizedPosition * viewHeight));
                    
                    g.setColor(Colors.DEFAULT_COLOR);
                    g.drawLine(viewWidth - ARROW_HALF_LEN, (int)(normalizedPosition * viewHeight), viewWidth, (int)(normalizedPosition * viewHeight) - ARROW_HALF_LEN);
                    g.drawLine(viewWidth - ARROW_HALF_LEN, (int)(normalizedPosition * viewHeight), viewWidth, (int)(normalizedPosition * viewHeight) + ARROW_HALF_LEN);
                }
                else if (portResultDir == PortResultDirectionality.OUTPUT)
                {
                    g.drawLine(viewWidth, (int)(normalizedPosition * viewHeight), viewWidth - ARROW_HALF_LEN, (int)(normalizedPosition * viewHeight) - ARROW_HALF_LEN);
                    g.drawLine(viewWidth, (int)(normalizedPosition * viewHeight), viewWidth - ARROW_HALF_LEN, (int)(normalizedPosition * viewHeight) + ARROW_HALF_LEN);
                }
                
                Rectangle2D nameBounds = g.getFontMetrics().getStringBounds(portName, g);
                g.drawString(portName, componentRect.x + componentRect.width - 5 - (int)nameBounds.getWidth(), (int)(normalizedPosition * viewHeight) + ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2);
            } break;
            
            case TOP:
            {
                g.drawLine((int)(normalizedPosition * viewWidth), 0, (int)(normalizedPosition * viewWidth), componentRect.y - (isInverted ? ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR : 0));
                
                if (isInverted)
                {                        
                    g.drawOval((int)(normalizedPosition * viewWidth) - ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2,
                               componentRect.y - ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR,
                               ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR, 
                               ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR);
                }
                
                if (portResultDir == PortResultDirectionality.INPUT)
                {                        
                    g.setColor(Colors.CANVAS_BKG_COLOR);
                    g.drawLine((int)(normalizedPosition * viewWidth), ARROW_HALF_LEN, (int)(normalizedPosition * viewWidth), 0);
                    
                    g.setColor(Colors.DEFAULT_COLOR);
                    g.drawLine((int)(normalizedPosition * viewWidth), ARROW_HALF_LEN, (int)(normalizedPosition * viewWidth) - ARROW_HALF_LEN, 0);
                    g.drawLine((int)(normalizedPosition * viewWidth), ARROW_HALF_LEN, (int)(normalizedPosition * viewWidth) + ARROW_HALF_LEN, 0);
                }
                else if (portResultDir == PortResultDirectionality.OUTPUT)
                {
                    g.drawLine((int)(normalizedPosition * viewWidth), 0, (int)(normalizedPosition * viewWidth) - ARROW_HALF_LEN, ARROW_HALF_LEN);
                    g.drawLine((int)(normalizedPosition * viewWidth), 0, (int)(normalizedPosition * viewWidth) + ARROW_HALF_LEN, ARROW_HALF_LEN);
                }
                
                Rectangle2D nameBounds = g.getFontMetrics().getStringBounds(portName, g);
                g.drawString(portName, (int)(normalizedPosition * viewWidth) - (int)nameBounds.getWidth()/2, componentRect.y + ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR + 5);
            } break;
            
            case BOTTOM:
            {
                g.drawLine((int)(normalizedPosition * viewWidth), componentRect.y + componentRect.height + (isInverted ? ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR : 0), (int)(normalizedPosition * viewWidth), viewHeight);
                
                if (isInverted)
                {                        
                    g.drawOval((int)(normalizedPosition * viewWidth) - ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR/2,
                               componentRect.y + componentRect.height,
                               ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR, 
                               ConcreteComponentBuilderPanel.MARGIN_DENOMINATOR);
                }
                
                if (portResultDir == PortResultDirectionality.INPUT)
                {            
                    g.setColor(Colors.CANVAS_BKG_COLOR);
                    g.drawLine((int)(normalizedPosition * viewWidth), viewHeight - ARROW_HALF_LEN, (int)(normalizedPosition * viewWidth), viewHeight);
                    
                    g.setColor(Colors.DEFAULT_COLOR);
                    g.drawLine((int)(normalizedPosition * viewWidth), viewHeight - ARROW_HALF_LEN, (int)(normalizedPosition * viewWidth) - ARROW_HALF_LEN, viewHeight);
                    g.drawLine((int)(normalizedPosition * viewWidth), viewHeight - ARROW_HALF_LEN, (int)(normalizedPosition * viewWidth) + ARROW_HALF_LEN, viewHeight);
                }
                else if (portResultDir == PortResultDirectionality.OUTPUT)
                {
                    g.drawLine((int)(normalizedPosition * viewWidth), viewHeight, (int)(normalizedPosition * viewWidth) - ARROW_HALF_LEN, viewHeight - ARROW_HALF_LEN);
                    g.drawLine((int)(normalizedPosition * viewWidth), viewHeight, (int)(normalizedPosition * viewWidth) + ARROW_HALF_LEN, viewHeight - ARROW_HALF_LEN);
                }
                
                Rectangle2D nameBounds = g.getFontMetrics().getStringBounds(portName, g);
                g.drawString(portName, (int)(normalizedPosition * viewWidth) - (int)nameBounds.getWidth()/2, componentRect.y + componentRect.height - 5);                                                                  
            } break;
        }                                    
    }
}