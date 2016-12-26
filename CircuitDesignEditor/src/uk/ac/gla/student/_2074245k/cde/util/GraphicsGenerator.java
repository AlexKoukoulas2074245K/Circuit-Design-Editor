package uk.ac.gla.student._2074245k.cde.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Stroke;

import uk.ac.gla.student._2074245k.cde.components.GateComponent;
import uk.ac.gla.student._2074245k.cde.gui.Strokes;

public final class GraphicsGenerator 
{
	private final Graphics2D gfx;
	public GraphicsGenerator(final Graphics2D gfx)
	{
		this.gfx = gfx;		
	}
	
	public void drawGate(final GateComponent.GateType gateType, final Rectangle rect, final Color color)
	{
		gfx.setStroke(Strokes.THIN_STROKE);
		gfx.setColor(color);
				
		switch (gateType)
		{
			case NAND_GATE:
				gfx.setColor(Color.white);
				gfx.fillRect(rect.x + rect.width, rect.y + rect.height/2 - 9, 18, 18);
				gfx.setColor(color);
				gfx.drawOval(rect.x + rect.width, rect.y + rect.height/2 - 9, 18, 18);
			case AND_GATE:
			{				
				int arcWidth = rect.width * 3/4;
				gfx.drawLine(rect.x, rect.y, rect.x + rect.width - arcWidth/2, rect.y);
				gfx.drawLine(rect.x, rect.y + rect.height, rect.x + rect.width - arcWidth/2, rect.y + rect.height);
				gfx.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height);
				gfx.drawArc(rect.x + rect.width - arcWidth, rect.y, arcWidth, rect.height, 270, 180);		
			} break;
			
			case NOR_GATE:
				gfx.setColor(Color.white);
				gfx.fillRect(rect.x + rect.width, rect.y + rect.height/2 - 9, 18, 18);
				gfx.setColor(color);
				gfx.drawOval(rect.x + rect.width, rect.y + rect.height/2 - 9, 18, 18);
			case OR_GATE:
			{
				int arcWidth = 2 * rect.width;				
				gfx.drawArc(rect.x - arcWidth/2, rect.y, arcWidth, rect.height, 0, 100);
				gfx.drawArc(rect.x - arcWidth/2, rect.y, arcWidth, rect.height, 0, -100);
				gfx.drawArc(rect.x - arcWidth/3 + 8, rect.y, 40, rect.height, 0, 100);
				gfx.drawArc(rect.x - arcWidth/3 + 8, rect.y, 40, rect.height, 0, -100);
			} break;
			
			case XNOR_GATE:
				gfx.setColor(Color.white);
				gfx.fillRect(rect.x + rect.width, rect.y + rect.height/2 - 9, 18, 18);
				gfx.setColor(color);
				gfx.drawOval(rect.x + rect.width, rect.y + rect.height/2 - 9, 18, 18);
			case XOR_GATE:
			{
				int arcWidth = 2 * rect.width;				
				gfx.drawArc(rect.x - arcWidth/2, rect.y, arcWidth, rect.height, 0, 100);
				gfx.drawArc(rect.x - arcWidth/2, rect.y, arcWidth, rect.height, 0, -100);				
				gfx.drawArc(rect.x - arcWidth/3 + 8, rect.y, 40, rect.height, 0, 100);
				gfx.drawArc(rect.x - arcWidth/3 + 8, rect.y, 40, rect.height, 0, -100);
				gfx.drawArc(rect.x - arcWidth/3, rect.y, 40, rect.height, 0, 100);
				gfx.drawArc(rect.x - arcWidth/3, rect.y, 40, rect.height, 0, -100);
			} break;
		}
	}
	
	public void setColor(final Color color) { gfx.setColor(color); }
	public void setStroke(final Stroke stroke) { gfx.setStroke(stroke); }

	public void drawImage(final Image image, final int x, final int y, final int dx, final int dy) { gfx.drawImage(image, x, y, dx, dy, null); }
	public void drawImage(final Image image, final int x, final int y) { gfx.drawImage(image, x, y, null); }   
	public void drawString(final String str, final int x, final int y) { gfx.drawString(str, x, y); }
	public void drawLine(final int x1, final int y1, final int x2, final int y2) { gfx.drawLine(x1, y1, x2, y2); }
	public void drawRect(final int x, final int y, final int width, final int height) { gfx.drawRect(x, y, width, height); }
	public void drawOval(final int x, final int y, final int width, final int height) { gfx.drawOval(x, y, width, height); }
	
	public void fillRect(final int x, final int y, final int width, final int height) { gfx.fillRect(x, y, width, height); }
	public void fillOval(final int x, final int y, final int width, final int height) { gfx.fillOval(x, y, width, height); }
	
	public FontMetrics getFontMetrics() { return gfx.getFontMetrics(); }
	public Graphics getCanvasContext() { return gfx; }
}
