package uk.digitalsquid.ninepatcher.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.digitalsquid.ninepatcher.util.Session;

public final class ExportDialog extends JDialog implements WindowListener {

	private static final long serialVersionUID = -5624193207054979643L;
	
	private final Session session;
	
	private JSpinner sizexText, sizeyText;
	
	private int sizex, sizey;
	
	private boolean keepAspect = true;
	
	/**
	 * When true, spinner updates are ignored. Used to stop spinners re-updating each other.
	 */
	private boolean codeGeneratedChange = false;

	public ExportDialog(Session session) {
		this.session = session;
		setTitle("Export images");
		addWindowListener(this);
		loadComponents();
	}
	
	private void loadComponents() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		// Destination selector
		{
			JPanel destPanel = new JPanel();
			destPanel.setLayout(new BoxLayout(destPanel, BoxLayout.LINE_AXIS));
			
			final JLabel dest = new JLabel();
			
			JButton setDest = new JButton("Set destination resource folder (<project>/res/)");
			setDest.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					if(chooser.showOpenDialog(ExportDialog.this) == JFileChooser.APPROVE_OPTION) {
						session.setDestination(chooser.getSelectedFile().getAbsolutePath());
						dest.setText(chooser.getSelectedFile().getAbsolutePath());
					}
				}
			});
			
			destPanel.add(setDest);
			destPanel.add(dest);
			
			panel.add(destPanel);
		}
		// Size (dip)
		{
			JPanel sizePanel = new JPanel();
			sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.LINE_AXIS));
			
			Dimension origSize = session.getLoader().getSize();
			sizexText = new JSpinner(new SpinnerNumberModel(origSize.width, 1, Integer.MAX_VALUE, 1));
			sizexText.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					sizex = (Integer)sizexText.getValue();
					
					if(keepAspect) {
						Dimension aspectRatio = session.getLoader().getSize();
						sizey = aspectRatio.height * sizex / aspectRatio.width;
						if(!codeGeneratedChange) {
							codeGeneratedChange = true;
							sizeyText.setValue(sizey);
							codeGeneratedChange = false;
						}
					}
				}
			});
			sizex = session.getLoader().getSize().width;
			sizexText.setValue(sizex);
			
			sizeyText = new JSpinner(new SpinnerNumberModel(origSize.height, 1, Integer.MAX_VALUE, 1));
			sizeyText.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					sizey = (Integer)sizeyText.getValue();
					
					if(keepAspect) {
						Dimension aspectRatio = session.getLoader().getSize();
						sizex = aspectRatio.width * sizey / aspectRatio.height;
						if(!codeGeneratedChange) {
							codeGeneratedChange = true;
							sizexText.setValue(sizex);
							codeGeneratedChange = false;
						}
					}
				}
			});
			sizey = session.getLoader().getSize().height;
			sizeyText.setValue(sizey);
			
			
			final JToggleButton aspect = new JToggleButton("Lock", keepAspect);
			aspect.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					keepAspect = aspect.isSelected();
				}
			});
			
			sizePanel.add(new JLabel("Image size (dip):"));
			sizePanel.add(sizexText);
			sizePanel.add(new JLabel("x"));
			sizePanel.add(sizeyText);
			sizePanel.add(aspect);
			sizePanel.add(Box.createHorizontalGlue());
			
			panel.add(sizePanel);
		}
		
		// Sizes to generate
		{
			JPanel ldpiPanel = new JPanel();
			ldpiPanel.setLayout(new BoxLayout(ldpiPanel, BoxLayout.LINE_AXIS));
			JPanel mdpiPanel = new JPanel();
			mdpiPanel.setLayout(new BoxLayout(mdpiPanel, BoxLayout.LINE_AXIS));
			JPanel hdpiPanel = new JPanel();
			hdpiPanel.setLayout(new BoxLayout(hdpiPanel, BoxLayout.LINE_AXIS));
			JPanel xdpiPanel = new JPanel();
			xdpiPanel.setLayout(new BoxLayout(xdpiPanel, BoxLayout.LINE_AXIS));
			
			JCheckBox ldpiBox = new JCheckBox("Generate low DPI image");
			JCheckBox mdpiBox = new JCheckBox("Generate medium DPI image");
			JCheckBox hdpiBox = new JCheckBox("Generate high DPI image");
			JCheckBox xdpiBox = new JCheckBox("Generate extra-high DPI image");
			
			panel.add(ldpiPanel);
		}
		
		getContentPane().add(panel, BorderLayout.CENTER);
		pack();
	}

	@Override public void windowActivated(WindowEvent e) { }
	@Override public void windowDeactivated(WindowEvent e) { }
	@Override public void windowDeiconified(WindowEvent e) { }
	@Override public void windowIconified(WindowEvent e) { }
	@Override public void windowOpened(WindowEvent e) { }
	@Override public void windowClosed(WindowEvent e) { }

	@Override
	public void windowClosing(WindowEvent e) {
		dispose();
	}
}
