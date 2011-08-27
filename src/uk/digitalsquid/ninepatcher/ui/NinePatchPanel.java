package uk.digitalsquid.ninepatcher.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import uk.digitalsquid.ninepatcher.util.Session;

/**
 * A panel that draws and allows changes to 9-patches.
 * @author william
 *
 */
public class NinePatchPanel extends ImagePanel implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -8794427011188729128L;
	
	private static final int BORDER_SIZE = 20;

	public NinePatchPanel(Session session) {
		super(session, BORDER_SIZE);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@Override public void mouseClicked(MouseEvent e) { }
	@Override public void mouseEntered(MouseEvent e) { }
	@Override public void mouseExited(MouseEvent e) { }
	@Override public void mouseDragged(MouseEvent e) { }

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
