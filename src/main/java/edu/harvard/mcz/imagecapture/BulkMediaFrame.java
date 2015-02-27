/**
 * BulkMediaFrame.java
 * edu.harvard.mcz.imagecapture
 * Copyright © 2015 President and Fellows of Harvard College
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of Version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Author: Paul J. Morris
 */
package edu.harvard.mcz.imagecapture;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.JMenuBar;
import javax.swing.JMenu;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import java.awt.event.InputEvent;

import javax.swing.JButton;

import java.awt.Dimension;

import edu.harvard.mcz.imagecapture.data.BulkMedia;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.SwingConstants;

/**
 * Frame to manage creating BulkMedia files from image files.
 * 
 * @author mole
 *
 */
public class BulkMediaFrame extends JFrame implements PropertyChangeListener  {
	private static final long serialVersionUID = 1307585080820001695L;
	
	private static final Log log = LogFactory.getLog(BulkMediaFrame.class);

	public static final String BULKFILENAME = "media_bulkload.csv";
	
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JProgressBar progressBar;
	private BulkMediaFrame thisFrame;
	private ScanDirectoryTask task;

	/**
	 * Create the frame.
	 */
	public BulkMediaFrame() {
		init();
	} 
	
	private void done() { 
		thisFrame.setVisible(false);
		System.exit(0);
	}
	
	private void init() { 
		thisFrame = this;
		setTitle("BulkMedia Preparation");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 902, 174);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic(KeyEvent.VK_F);
		menuBar.add(mnFile);
		
		JMenuItem mntmPrepareDirectory = new JMenuItem("Prepare Directory");
		mntmPrepareDirectory.setMnemonic(KeyEvent.VK_D);
		mntmPrepareDirectory.addActionListener(new PrepareDirectoryAction());
		mnFile.add(mntmPrepareDirectory);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Exit");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				done();
			}
		});
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Edit Properties");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PropertiesEditor p = new PropertiesEditor();
				p.pack();
				p.setVisible(true);
			}
		});
		mnFile.add(mntmNewMenuItem_1);
		mntmNewMenuItem.setMnemonic(KeyEvent.VK_X);
		mntmNewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		mnFile.add(mntmNewMenuItem);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(3,2,0,0));
		
		JLabel lblBaseUri = new JLabel("Base URI (first part of path to images on the web)");
		lblBaseUri.setHorizontalAlignment(SwingConstants.TRAILING);
		panel.add(lblBaseUri, "2, 2");
		
		textField = new JTextField();
		textField.setEditable(false);
		textField.setText(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASEURI));
		panel.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Local Path To Base (local mount path that maps to base URI)");
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		panel.add(lblNewLabel);
		
		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setText(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE));
		panel.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblBeforeExitingWait = new JLabel("Before exiting wait for both the Done and Thumbnails Built messages.");
		panel.add(lblBeforeExitingWait);
		
		JLabel lblThumbnailGenerationIs = new JLabel("Thumbnail generation is not reported on the progress bar.");
		panel.add(lblThumbnailGenerationIs);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(false);
		contentPane.add(progressBar, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		
		JButton btnPrepareDirectory = new JButton("Run");
		btnPrepareDirectory.setToolTipText("Select a directory and prepare a bulk media file for images therein.");
		panel_1.add(btnPrepareDirectory);
		btnPrepareDirectory.addActionListener(new PrepareDirectoryAction());
		btnPrepareDirectory.setPreferredSize(new Dimension(80, 25));
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				done();
			}
		});
		panel_1.add(btnExit);
	}
	
	class PrepareDirectoryAction implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				task = new ScanDirectoryTask();
				task.addPropertyChangeListener(thisFrame);
				task.execute();	
			}
	}
	
    class ScanDirectoryTask extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
        	int progress = 0;
            setProgress(progress);
            final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			File startAt = null;
			try { 
			   startAt = new File(Singleton.getSingletonInstance().getProperties().getProperties().getProperty(ImageCaptureProperties.KEY_IMAGEBASE));
			} catch (Exception e) { 
				log.debug(e.getMessage());
			}
			fileChooser.setCurrentDirectory(startAt);  
			fileChooser.setDialogTitle("Pick a directory of image files to check for barcodes.");
			int returnValue = fileChooser.showOpenDialog(Singleton.getSingletonInstance().getMainFrame());
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File directory = fileChooser.getSelectedFile();
				log.debug("Selected base directory: " + directory.getName() + ".");
				File[] files = directory.listFiles();
				int fileCount = files.length;
				int matchCount = 0;
				if (fileCount>0) {
					// create thumbnails in a separate thread
					(new Thread(new ThumbnailBuilder(directory))).start();
				}
				if (fileCount>0) { 
					int position = 0;
					boolean okToRun = false;
					File output = new File(directory.getAbsolutePath() + File.separatorChar + BulkMediaFrame.BULKFILENAME);
					log.debug(output.getPath());
					if (output.exists()) { 
						int option =JOptionPane.showConfirmDialog(thisFrame,"Output file " + output.getName() + " already exists in selected directory.  Overwrite it?.", "File Exists.  Overwrite?", JOptionPane.WARNING_MESSAGE);
						if (option== JOptionPane.OK_OPTION) { 
							// no need to take action to truncate output file, PrintWriter will truncate rather than appending.
							okToRun = true;
						}
					}
					if (!output.exists()) { 
						okToRun = true;
					}
					if (okToRun) {
						try {
							CSVFormat format = CSVFormat.RFC4180.withDelimiter(',').withQuote('"').withQuoteMode(QuoteMode.ALL);
							PrintWriter writer = new PrintWriter(output);
							CSVPrinter printer = new CSVPrinter(writer, format);
							printer.printRecord(BulkMedia.getHeaders());
							for (File candidate : files) {
								progress = Math.round((float)position/(float)fileCount *100.0f);
								log.debug(progress);
                                setProgress(progress);
								if (candidate.getName().matches(ImageCaptureApp.REGEX_IMAGEFILE)) { 
									try {
										BulkMedia line = CandidateImageFile.parseOneFileToBulkMedia(candidate.getCanonicalPath());
										log.debug(line.getData());
										printer.printRecord((Iterable<String>)line.getData());
										matchCount++;
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								position++;
							} 
							printer.close();
							writer.close();
						} catch (FileNotFoundException e1) {
	                        JOptionPane.showMessageDialog(thisFrame,"Output file " + output.getName() + " was not writable or could not be created.", "File Not writable.", JOptionPane.ERROR_MESSAGE);	
	                        log.error(e1.getMessage());
						} catch (IOException e1) {
	                        JOptionPane.showMessageDialog(thisFrame,"Error writing to file " + output.getName() + ". "  + e1.getMessage(), "File write error.", JOptionPane.ERROR_MESSAGE);	
							log.error(e1.getMessage(),e1);
						}
					} else { 
						log.error(output.exists());
	                    JOptionPane.showMessageDialog(thisFrame,"Output file " + output.getName() + " already exists in selected directory.", "File Exists", JOptionPane.ERROR_MESSAGE);	
					}
				} else { 
					log.error("No files in selected directory. " + directory.toString());
				}
                setProgress(100);
                JOptionPane.showMessageDialog(null,"Done.  Checked " + matchCount +"  files out of " + fileCount + ".\nResult is in " + BulkMediaFrame.BULKFILENAME, "Done.", JOptionPane.INFORMATION_MESSAGE);	
			} else {
				log.error("Directory selection cancelled by user.");
			}
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
        	progressBar.setString("Done");
        }
    }

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		log.debug(evt.getPropertyName());
		if (evt.getPropertyName()=="progress") {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
            progressBar.setStringPainted(true);
            log.debug(progress);
		}
	}
}
