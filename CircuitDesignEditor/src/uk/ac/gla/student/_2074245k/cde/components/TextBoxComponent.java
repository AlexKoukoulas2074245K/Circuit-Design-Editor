package uk.ac.gla.student._2074245k.cde.components;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import uk.ac.gla.student._2074245k.cde.gui.Colors;
import uk.ac.gla.student._2074245k.cde.gui.MainCanvas;
import uk.ac.gla.student._2074245k.cde.gui.Strokes;
import uk.ac.gla.student._2074245k.cde.util.GraphicsGenerator;

public final class TextBoxComponent extends Component
{	
	private static final int CURSOR_DELAY = 100;
	
	private List<String> texts;	
	private Rectangle rect;
	private Font font;
	private boolean editMode;
	private int cursorDelay;
	private boolean shouldDisplayCursor;
	
	public TextBoxComponent(final MainCanvas canvas, final String text, final int x, final int y)
	{
		super(canvas, true);
		this.texts = new ArrayList<String>();
		this.texts.add(text);
		
		editMode = false;
		rect = new Rectangle(x - 15, y - 18, 0, 0);
		cursorDelay = CURSOR_DELAY;
		shouldDisplayCursor = true;
	}
	
	@Override
	public AlignedComponentsList moveTo(final int x, final int y) 
	{
		editMode = MainCanvas.mouse.doubleClick();
		
		if (!editMode)
		{
			rect.x = x;
			rect.y = y;			
		}
		return new AlignedComponentsList();
	}

	@Override
	public void finalizeMovement(final AlignedComponentsList alignedComponents)
	{
		
	}

	@Override
	public boolean mouseIntersection(final int mouseX, final int mouseY) 
	{	
	    return rect.contains(mouseX, mouseY); 
	}

	@Override
	public void render(final GraphicsGenerator g, final boolean highlighted, final boolean selected, final boolean inMultiSelectionMovement) 
	{								
		if (font == null)
		{
			font = g.getCanvasContext().getFont().deriveFont(16.0f);
		}
		
		Font prevFont = g.getCanvasContext().getFont();
		g.setFont(font);
		updateTextboxRect(g);
		
		if (selected)
		{
			g.setStroke(Strokes.THIN_STROKE);
			g.setColor(Colors.SELECTION_COLOR);							
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
		}		
		else
		{
			editMode = false;		
			
			if (texts.size() == 0)
			{
				delete();
				return;
			}					
		}
		
		g.setColor(customColor);	
		
		
		for (String text: texts)
		{
			Rectangle2D textRect = g.getFontMetrics().getStringBounds(text, g.getCanvasContext());
			g.drawString(text, rect.x + 10, (int)textRect.getHeight() + rect.y + (texts.indexOf(text) * (int)textRect.getHeight()));			
		}
		
		if (editMode)
		{						
			int cursorX = rect.x + 10;
			int cursorY = rect.y + 14;
			
			if (texts.size() > 0)
			{
				Rectangle2D textRect = g.getFontMetrics().getStringBounds(texts.get(texts.size() - 1), g.getCanvasContext());
				cursorX += textRect.getWidth();
				cursorY = rect.y + texts.size() * (int)textRect.getHeight();
			}
			
			cursorDelay--;
			if (cursorDelay <= 0)
			{
				cursorDelay = CURSOR_DELAY;
				shouldDisplayCursor = !shouldDisplayCursor;
			}
			
			if (shouldDisplayCursor)
			{
				g.drawString("|", cursorX, cursorY);				
			}			
		}
		
		g.setFont(prevFont);
		
	}

	@Override
	public void renderAligned(final GraphicsGenerator g) 
	{
		
	}

	@Override
	public void delete() 
	{
		canvas.removeComponentFromCanvas(this);
	}

	@Override
	public List<Component> getParents() 
	{
		return new ArrayList<Component>();
	}

	@Override
	public List<Component> getChildren() 
	{
		return new ArrayList<Component>();
	}

	@Override
	public void removeChild(final Component component) 
	{
		
	}

	@Override
	public String serialize() 
	{
		String serialString = rect.x + "," + rect.y + "," + rect.width + "," + rect.height + "," + 
				              customColor.getRed() + "," + customColor.getGreen() + "," + customColor.getBlue() + "," + customColor.getAlpha() + 
				              "," + "[";
		for (int i = 0; i < texts.size(); ++i)
		{
			serialString += texts.get(i).replaceAll(" ", "@");
			
			if (i != texts.size() - 1)
			{
				serialString += "-";
			}				
		}
		serialString += "]";
		
		return serialString;
	}

	@Override
	public Rectangle getRectangle() 
	{
		return rect;
	}

	@Override
	public void setPosition(final int x, final int y) 
	{
		rect.x = x;
		rect.y = y;
	}

	@Override
	public ComponentType getComponentType() 
	{
		return Component.ComponentType.TEXT_BOX;
	}
	
	public void setTexts(final List<String> texts)
	{
		this.texts = texts;
	}
	
	public void setRectangle(final Rectangle rect)
	{
		this.rect = rect;
	}
	
	public boolean isInEditMode()
	{
		return editMode;
	}
	
	public void updateText(final KeyEvent evt)
	{
		if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE)
		{	
			if (texts.size() > 0)
			{
				String lastText = texts.get(texts.size() - 1);
				if (lastText.length() > 0)
				{
					texts.set(texts.size() - 1, lastText.substring(0, lastText.length() - 1));		
					
					if (texts.get(texts.size() - 1).length() == 0)
					{
						texts.remove(texts.size() - 1);
					}								
				}
				else
				{
					texts.remove(texts.size() - 1);
				}
			}			
		}
		else if (evt.getKeyCode() == KeyEvent.VK_SPACE)
		{
			if (texts.size() == 0)
			{
				texts.add(new String());
			}
			
			texts.set(texts.size() - 1, texts.get(texts.size() - 1) + " ");
		}
		else if (evt.getKeyCode() == KeyEvent.VK_ENTER)
		{
			texts.add(new String());
		}
		else
		{
			if (Character.isLetterOrDigit(evt.getKeyChar()))
			{
				if (texts.size() == 0)
				{
					texts.add(new String());
				}
				
				texts.set(texts.size() - 1, texts.get(texts.size() - 1) + evt.getKeyChar());				
			}
		}				
	}	
	
	private void updateTextboxRect(final GraphicsGenerator g)
	{
		int maxWidth = 0;
		int maxHeight = 0;
		
		for (String text: texts)
		{
			Rectangle2D textRect = g.getFontMetrics().getStringBounds(text, g.getCanvasContext());
			if (textRect.getWidth() > maxWidth)
				maxWidth = (int)textRect.getWidth();
			if (textRect.getHeight() > maxHeight)
				maxHeight = (int)textRect.getHeight();
		}
		
		rect.width  = maxWidth + 20;
		rect.height = 20 + maxHeight * texts.size();
	}
}
