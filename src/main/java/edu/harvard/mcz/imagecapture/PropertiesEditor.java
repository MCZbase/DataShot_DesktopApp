/**
 * 
 */
package edu.harvard.mcz.imagecapture;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JFrame;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.TableModel;

import edu.harvard.mcz.imagecapture.interfaces.BarcodeBuilder;
import edu.harvard.mcz.imagecapture.interfaces.BarcodeMatcher;

/**
 * Frame to display list of property key/value pairs where the values for each 
 * key can be edited and the changes can be saved to the imagecapture.properties
 * file.
 * 
 * @author Paul J. Morris
 *
 */
public class PropertiesEditor extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JButton jButton = null;
	private JButton jButtonSave = null;
	private JTextField jTextField = null;
	private ImageCaptureProperties properties = null;
	private PropertiesEditor thisEditor = null;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;

	/**
	 * This is the default constructor
	 */
	public PropertiesEditor() {
		super();
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		properties = new ImageCaptureProperties();
		try {
			properties.loadProperties();
		} catch (Exception e) {
			System.out.println("Unable to load properties file.");
		}
		thisEditor = this; // make this visible to anonymous methods of button action events.
		initialize();
		jScrollPane.setPreferredSize(new Dimension(669, 347));
	}

	/**
	 * This method initializes this, an instance of PropertiesEditor setting up the frame.
	 *
	 */
	private void initialize() {
		this.setSize(669, 347);
		this.setContentPane(getJContentPane());
		this.setTitle(ImageCaptureApp.APP_NAME + " Preferences");
		
		jTextField.setText(Singleton.getSingletonInstance().getProperties().getPropertiesSource());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJTextField(), BorderLayout.NORTH);
			jContentPane.add(getJPanel(), BorderLayout.CENTER);
			jContentPane.add(getJPanel1(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.fill = GridBagConstraints.BOTH;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.gridx = 0;
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.add(getJScrollPane(), gridBagConstraints);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(getJButton(), new GridBagConstraints());
			jPanel1.add(getJButtonSave(), new GridBagConstraints());
		}
		return jPanel1;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Cancel");
			jButton.setMnemonic(KeyEvent.VK_C);
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					thisEditor.setVisible(false);
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonSave() {
		if (jButtonSave == null) {
			jButtonSave = new JButton();
			jButtonSave.setText("Save");
			jButtonSave.setMnemonic(KeyEvent.VK_S);
			jButtonSave.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						if (jTable.isEditing()) { 
						    jTable.getCellEditor().stopCellEditing();
						}
						Singleton.getSingletonInstance().setProperties(((ImageCaptureProperties)jTable.getModel()));
						Singleton.getSingletonInstance().getProperties().saveProperties();
						
						// Set up a barcode (text read from barcode label for pin) matcher/builder
						if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_COLLECTION).equals(ImageCaptureProperties.COLLECTION_MCZENT)) { 
							// ** Configured for the MCZ Entomology Collection, use MCZ assumptions.
						    MCZENTBarcode barcodeTextBuilderMatcher = new MCZENTBarcode();
						    Singleton.getSingletonInstance().setBarcodeBuilder((BarcodeBuilder)barcodeTextBuilderMatcher);
						    Singleton.getSingletonInstance().setBarcodeMatcher((BarcodeMatcher)barcodeTextBuilderMatcher);
						} else if (Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_COLLECTION).equals(ImageCaptureProperties.COLLECTION_ETHZENT)) { 
							// ** Configured for the ETHZ Entomology Collection, use MCZ assumptions.
						    ETHZBarcode barcodeTextBuilderMatcher = new ETHZBarcode();
						    Singleton.getSingletonInstance().setBarcodeBuilder((BarcodeBuilder)barcodeTextBuilderMatcher);
						    Singleton.getSingletonInstance().setBarcodeMatcher((BarcodeMatcher)barcodeTextBuilderMatcher);
						} else { 
							throw new Exception("Configured collection not recognized.");
						}						
						Singleton.getSingletonInstance().getMainFrame().updateTitle();
						
						thisEditor.dispose();
					} catch (Exception e1) {
						System.out.println("Save Failed");
					}
				}
			});
		}
		return jButtonSave;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setEditable(false);
		}
		return jTextField;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable((TableModel)properties);
		}
		return jTable;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
