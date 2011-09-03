package uk.digitalsquid.ninepatcher;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * About dialog
 * @author william
 *
 */
public class AboutDialog extends JDialog implements WindowListener {

	private static final long serialVersionUID = 5211295405860753683L;

	public AboutDialog(JFrame owner) {
		super(owner);
		addWindowListener(this);
		setTitle("About");
		loadComponents();
		validate();
		pack();
	}
	
	private void loadComponents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		// Title
		{
			JPanel titlePanel = new JPanel();
			titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.LINE_AXIS));
			
			JLabel label = new JLabel("9patcher");
			label.setFont(label.getFont().deriveFont(24));
			titlePanel.add(label);
			
			panel.add(titlePanel);
		}
		
		// Desc1
		{
			JPanel descPanel = new JPanel();
			descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.LINE_AXIS));
			
			JLabel label = new JLabel("Alternative 9-patch creator");
			descPanel.add(label);
			
			panel.add(descPanel);
		}
		// Desc2
		{
			JPanel descPanel = new JPanel();
			descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.LINE_AXIS));
			
			JLabel label = new JLabel("for Android development");
			descPanel.add(label);
			
			panel.add(descPanel);
		}
		
		/**
		// Links
		{
			JPanel linksPanel = new JPanel();
			linksPanel.setLayout(new BoxLayout(linksPanel, BoxLayout.LINE_AXIS));
			
			JButton website = new JButton("Website");
			website.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				}
			});
			linksPanel.add(website);
			
			panel.add(linksPanel);
		}
		*/
		
		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(Box.createHorizontalStrut(10), BorderLayout.WEST);
		getContentPane().add(Box.createHorizontalStrut(10), BorderLayout.EAST);
		getContentPane().add(Box.createVerticalStrut(5), BorderLayout.NORTH);
		getContentPane().add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
	}

	@Override public void windowActivated(WindowEvent arg0) { }
	@Override public void windowClosed(WindowEvent e) { }
	@Override public void windowClosing(WindowEvent e) { }
	@Override public void windowDeactivated(WindowEvent e) { }
	@Override public void windowDeiconified(WindowEvent e) { }
	@Override public void windowIconified(WindowEvent e) { }
	@Override public void windowOpened(WindowEvent e) { }
}
