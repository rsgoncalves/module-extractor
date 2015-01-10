package uk.ac.manchester.cs.modularity.owlme;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.VersionInfo;

import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

/**
 * @author Rafael S. Goncalves <br>
 * Information Management Group (IMG) <br>
 * School of Computer Science <br>
 * University of Manchester <br>
 */
public class ModuleExtractorView {
	private Map<Integer,Long> timers = new HashMap<Integer, Long>();
	private JFrame frmModuleExtractor;
	private JTextField ontUri;
	private JLabel lblSignatureFile;
	private JTextField sigFilePath;
	private JLabel lblModuleUri;
	private JTextField modUri;
	private JLabel lblModuleType;
	private JTextArea log;
	private JScrollPane scrollPane;
	private JProgressBar progressBar;
	@SuppressWarnings("rawtypes")
	private JComboBox comboBox;
	private boolean loaded;
	private boolean ontUriChanged;
	private OWLOntology currentOntology;
	private String modLog;
	private int modCounter;
	private OntologyWorker ontLoader;
	private boolean windows;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ModuleExtractorView window = new ModuleExtractorView();
					window.frmModuleExtractor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ModuleExtractorView() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize() {
		frmModuleExtractor = new JFrame();
		frmModuleExtractor.setTitle("OWL-ME: OWL Module Extractor");
		frmModuleExtractor.setBounds(100, 100, 650, 500);
		frmModuleExtractor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ontUriChanged = false;
		ontUri = new JTextField();
		ontUri.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				// text was changed
				ontUriChanged = true;
			}
			public void removeUpdate(DocumentEvent e) {
				// text was deleted
				ontUriChanged = true;
			}
			public void insertUpdate(DocumentEvent e) {
				// text was inserted
				ontUriChanged = true;
			}
		});

		ontUri.setColumns(10);
		
		JLabel lblOntology = new JLabel("Ontology URI:");
		lblOntology.setFont(new Font("Arial", Font.PLAIN, 13));
		
		JButton openOnt = new JButton("Browse");
		openOnt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		openOnt.setFont(new Font("Arial", Font.PLAIN, 13));
		openOnt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				openButtonMouseClicked(e);
			}
		});
		
		lblSignatureFile = new JLabel("Signature File:");
		lblSignatureFile.setFont(new Font("Arial", Font.PLAIN, 13));
		
		sigFilePath = new JTextField();
		sigFilePath.setColumns(10);
		
		JButton openSig = new JButton("Browse");
		openSig.setFont(new Font("Arial", Font.PLAIN, 13));
		openSig.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				openSignatureFile(e);
			}
		});
		
		lblModuleUri = new JLabel("Save Location:");
		lblModuleUri.setFont(new Font("Arial", Font.PLAIN, 13));
		
		modUri = new JTextField();
		modUri.setColumns(10);
		
		modCounter = 0;
		ontLoader = null;
		
		lblModuleType = new JLabel("Module Type:");
		lblModuleType.setFont(new Font("Arial", Font.PLAIN, 13));
		
		String[] modTypes = {"STAR", "BOTTOM", "TOP"};
		comboBox = new JComboBox(modTypes);
		comboBox.setFont(new Font("Arial", Font.PLAIN, 13));
		comboBox.setSelectedIndex(0);
		
		JButton btnExtractModule = new JButton("Extract Module");
		btnExtractModule.setFont(new Font("Arial", Font.PLAIN, 13));
		btnExtractModule.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				extractModuleButtonClicked(e);
			}
		});
		
		JButton modPath = new JButton("Browse");
		modPath.setFont(new Font("Arial", Font.PLAIN, 13));
		modPath.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				saveLocationButton(e);
			}
		});
		
		JLabel lblLog = new JLabel("Log");
		lblLog.setFont(new Font("Arial", Font.PLAIN, 13));
		
		log = new JTextArea();
		log.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		log.setWrapStyleWord(true);
		log.setLineWrap(true);
		log.setEditable(true);
		
		scrollPane = new JScrollPane(log);
		progressBar = new JProgressBar();
		
		JLabel lbl = new JLabel("Powered by the OWL API v" + VersionInfo.getVersionInfo().getVersion().trim());
		GroupLayout groupLayout = new GroupLayout(frmModuleExtractor.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblOntology)
								.addComponent(lblSignatureFile)
								.addComponent(lblModuleUri))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(ontUri, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
								.addComponent(sigFilePath, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
								.addComponent(modUri, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(openOnt)
								.addComponent(openSig, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
								.addComponent(modPath)))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblModuleType)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE))
								.addComponent(lblLog))
							.addPreferredGap(ComponentPlacement.RELATED, 279, Short.MAX_VALUE)
							.addComponent(btnExtractModule))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 95, Short.MAX_VALUE)
							.addComponent(lbl)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(30)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblOntology)
						.addComponent(ontUri, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(openOnt))
					.addGap(21)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSignatureFile)
						.addComponent(sigFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(openSig))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblModuleUri)
						.addComponent(modUri, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(modPath))
					.addGap(21)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnExtractModule, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblModuleType)
								.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblLog)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lbl))
					.addContainerGap())
		);
		frmModuleExtractor.getContentPane().setLayout(groupLayout);
	}
	
	/**
	 * Browse or enter ontology file path
	 */
	private void openButtonMouseClicked(java.awt.event.MouseEvent evt) {
		JFrame frame = new JFrame();
        String filename = File.separator+"tmp";
        JFileChooser fc = new JFileChooser(new File(filename));
        
        String pathOfOnt = ontUri.getText();
        if(!pathOfOnt.equals("")) {
        	String dirOfOnt = pathOfOnt.substring(0, pathOfOnt.lastIndexOf("/"));
        	fc.setCurrentDirectory(new File(dirOfOnt));
        }
        else
        	fc.setCurrentDirectory(new File("."));
        
        // Show open dialog; this method does not return until the dialog is closed
        fc.showOpenDialog(frame);
        File selFile = fc.getSelectedFile();
        if(selFile != null) {
        	String ontPath = selFile.getAbsolutePath();
        	
        	// Windows file system compatibility tweak
        	if(ontPath.contains("\\")) {
        		ontPath = ontPath.replace("\\",	"/");
        		windows = true;
        	}
        	
        	ontUri.setText(ontPath);
        	log.append("Selected Ontology: " + ontPath + "\n");
        	log.repaint();
        }
	}
	
	/**
	 * Browse or enter signature file path
	 */
	private void openSignatureFile(java.awt.event.MouseEvent evt) {
		JFrame frame = new JFrame();
        String filename = File.separator+"tmp";
        JFileChooser fc = new JFileChooser(new File(filename));
        
        String pathOfOnt = ontUri.getText();
        if(!pathOfOnt.equals("")) {
        	String dirOfOnt = pathOfOnt.substring(0, pathOfOnt.lastIndexOf("/"));
        	fc.setCurrentDirectory(new File(dirOfOnt));
        }
        else {
        	fc.setCurrentDirectory(new File("."));
        }
        
        // Show open dialog; this method does not return until the dialog is closed
        fc.showOpenDialog(frame);
        File selFile = fc.getSelectedFile();
        if(selFile != null) {
        	String sigPath = selFile.getAbsolutePath();
        	
        	// Windows file system compatibility tweak
        	if(sigPath.contains("\\"))
        		sigPath = sigPath.replace("\\",	"/"); 
        	
        	sigFilePath.setText(sigPath);
        	
        	log.append("Selected Signature: " + sigPath + "\n");
        	log.updateUI();
        }
	}
	
	/**
	 * Browse or enter module save path
	 */
	private void saveLocationButton(java.awt.event.MouseEvent evt) {
		JFrame frame = new JFrame();
        String filename = File.separator+"tmp";
        JFileChooser fc = new JFileChooser(new File(filename));
        
        String pathOfOnt = ontUri.getText();
        if(!pathOfOnt.equals("")) {
        	String dirOfOnt = pathOfOnt.substring(0, pathOfOnt.lastIndexOf("/"));
        	fc.setCurrentDirectory(new File(dirOfOnt));
        }
        else
        	fc.setCurrentDirectory(new File("."));

        // Show open dialog; this method does not return until the dialog is closed
        fc.showSaveDialog(frame);
        File selFile = fc.getSelectedFile();
        if(selFile != null) {	
        	String savePath = selFile.getAbsolutePath();
        	
        	// Windows file system compatibility tweak
        	if(savePath.contains("\\"))
        		savePath = savePath.replace("\\",	"/"); 
        	
        	if(!savePath.endsWith(".owl"))
        		savePath += ".owl";
        	
        	modUri.setText(savePath);
        	log.append("Saving Module as: " + savePath + "\n");
        	log.updateUI();
        }
	}
	
	
	class OntologyWorker extends SwingWorker<Void, String> {
		private JTextArea opLog;
		
		public OntologyWorker(JTextArea opLog) {
			this.opLog = opLog;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
		  	// Load ontology
			OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		  	progressBar.setIndeterminate(true);
		  	if(loaded && ontUriChanged) {
		  		man.removeOntology(currentOntology);
		  		publish("\n" + prefix("LOADING ONTOLOGY  " + ontUri.getText() + "..."));
		  		currentOntology = loadOntology(man, ontUri.getText());	  		
		  	}
		  	else if(!loaded) {
		  		publish("\n" + prefix("LOADING ONTOLOGY  " + ontUri.getText() + "..."));
		  		currentOntology = loadOntology(man, ontUri.getText());
		  		if(currentOntology != null) loaded = true;
		  	}
		  	else if(loaded && !ontUriChanged && currentOntology != null)
		  		publish("\n" + prefix("Using previously loaded ontology: " + ontUri.getText()));
		  
		  	// Load signature file
			String sigFile = sigFilePath.getText();
			Set<OWLEntity> signature = null;
			BufferedReader file = null;
			try {
				file = new BufferedReader(new InputStreamReader(new FileInputStream(sigFile)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			// Get terms in the signature file
			boolean ontOk = false;
			if(currentOntology != null) {
				ontOk = true;
				publish("\n\n" + suffix());
				publish(prefix("GETTING SIGNATURE ...\n"));
				if(file != null) {
					signature = ModuleExtractor.getSignature(currentOntology, file);
					publish(ModuleExtractor.getSignatureParseLog());
					publish("\n\n" + suffix());
				}
			}
			else {
				JOptionPane.showMessageDialog(null, "Invalid Ontology");
				publish("\n\nFailed to load ontology");
				publish("\n\n" + suffix());
			}
			
			if(ontOk && !signature.isEmpty()) {
				OWLOntology mod = null;
				publish("\n" + prefix("EXTRACTING MODULE ..."));
				
				String moduleName = currentOntology.getOntologyID().getOntologyIRI().toString();
				if(moduleName.contains(".owl"))
					moduleName = moduleName.substring(0, moduleName.indexOf(".owl")) + "_module" + modCounter + ".owl";
				else
					moduleName = moduleName + "module"  + modCounter;

				publish("\n\n" + "Module URI: " + moduleName);
				String outputDir = "";
				if(!windows)
					outputDir = "file:" + modUri.getText().substring(0, modUri.getText().indexOf(".owl"));
				else
					outputDir = "file:///" + modUri.getText().substring(0, modUri.getText().indexOf(".owl"));
				
				File f = new File(modUri.getText());
	        	if(f.exists()) {
	        		outputDir += modCounter + ".owl";
	        	}
	        	else {
	        		outputDir += ".owl";
	        	}
				
				int m = comboBox.getSelectedIndex();
				ModuleType modType = null;
				
				switch(m) {
				case 0: modType = ModuleType.STAR; break;
				case 1: modType = ModuleType.BOT; break;
				case 2: modType = ModuleType.TOP; break;
				}
				
				if(modType != null)
				try {
					mod = ModuleExtractor.extractModule(signature, currentOntology, moduleName, modType);
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}
				
				if(mod != null) {
					publish("\n\n" + suffix());
					
					// Saving module
					publish("\n" + prefix("SAVING MODULE ..."));
					publish("\n\nModule saved as: " + outputDir);
					
		            try {
		            	man.saveOntology(mod, IRI.create(outputDir));
					} catch (OWLOntologyStorageException e) {
						e.printStackTrace();
					}
		            publish("\n\n" + suffix());
		            publish("\nDONE");
				}
			}
			else if (ontOk && !(file.read() == -1)){
				publish("\nThe terms in the signature file were not found. The module extractor accepts signature files where entity names " +
						"are delimited via commas ',' white spaces (including tabs), vertical bars '|', or new lines.\n");
			}
			
			return null;
		}
		
		@Override
		protected void process(List<String> chunks) {
			for(String s : chunks) {
				modLog += s;
				opLog.append(s);
				opLog.setCaretPosition(opLog.getText().length());
			}
		}

		@Override
		protected void done() {
			progressBar.setIndeterminate(false);
			
			String path = modUri.getText();
			String filePath = path.substring(path.indexOf("/"), path.lastIndexOf("."));
			File logFile = new File(filePath + ".txt");
			BufferedWriter out = null;
			
			try {
				if(!modLog.equals("")) {
					out = new BufferedWriter(new FileWriter(logFile)); 
					out.write(modLog);
				}
				else {
					opLog.append("mod Log empty!!");
					opLog.updateUI();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			opLog.append("\n\nOperation log saved as: " + logFile + "\n");
			opLog.setCaretPosition(opLog.getText().length());
			
			modCounter ++;
			ontUriChanged = false;
		}
	}
	
	// Extract module from ontology, for given signature, and to specified module URI
	private void extractModuleButtonClicked(java.awt.event.MouseEvent evt) {
		modLog = "";
		ontLoader = new OntologyWorker(log);
		ontLoader.execute();
	}
	
	
	private OWLOntology loadOntology(OWLOntologyManager man, String input) {
		OWLOntology ont = null;
		try {
			ont = man.loadOntologyFromOntologyDocument(new File(input));
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		return ont;
	}
	
	public String prefix(String text) {
		startTime();
		return "----------------------------------------------------------------------------\n" + text;
	}

	public String suffix() {
		return "Time elapsed: " + stopTime() + "\n";
	}

	public void startTime(){
		startTime(0);
	}

	public void startTime(int timerNumber) {
		long time = System.currentTimeMillis();
		timers.put(timerNumber, time);
	}

	public String stopTime() {
		return stopTime(0);
	}

	public String stopTime(int timerNumber) {
		if(timers.containsKey(timerNumber)) {
			long time2 = System.currentTimeMillis() - timers.get(timerNumber);
			String centis = "" + (time2 / 10) % 100;
			String secs = "" + (time2 / 1000) % 60;
			String mins = "" + time2 / 60000;
			if (centis.length() < 2) centis = "0" + centis;
			if (secs.length() < 2) secs = "0" + secs;
			if (mins.length() < 2) mins = "0" + mins;
			return mins + "m" + secs + "." + centis + "s";
		}
		else
			return "Timer number " + timerNumber + " has not been started.";
	}
}