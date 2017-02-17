package io;

import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang3.SystemUtils;

import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;
//import ch.qos.logback.classic.LoggerContext;
import configurations.ConnectionSettings;
import fr.lip6.move.pnml.framework.utils.exception.InvalidIDException;
import fr.lip6.move.pnml.framework.utils.exception.VoidRepositoryException;
import gonOutput.GONoutput;
import graph.ContainerSingelton;
import graph.GraphContainer;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MyPopUpSingleton;
import io.graphML.SaveGraphML;
import moOutput.MOoutput;
import petriNet.PNEdge;
//import org.slf4j.LoggerFactory;
import petriNet.Place;
import petriNet.Transition;
import xmlOutput.sbml.JSBMLoutput;
import xmlOutput.sbml.PNMLOutput;
import xmlOutput.sbml.VAMLoutput;

public class SaveDialog {

	private boolean sbmlBool = true;
	private boolean graphMLBool = true;
	private boolean moBool = true;
	private boolean gonBool = true;
	private boolean vaBool = true;
	private boolean txtBool = true;
	private boolean itxtBool = true;
	private boolean pnmlBool = true;
	private boolean csvBool = true;
	private boolean pngBool = true;
	private boolean yamlBool = true;
	private boolean ymlBool = true;

	private String fileFormat;
	private File file;

	private String sbmlDescription = "System Biology Markup Language (*.sbml)";
	private String sbml = "sbml";

	private String pnmlDescription = "Petri Net Markup Language (*.pnml)";
	private String pnml = "pnml";

	private String vamlDescription = "VANESA Markup Language (*.vaml)";
	private String vaml = "vaml";

	private String graphMlDescription = "Graph Markup Language (*.graphml)";
	private String graphMl = "graphml";

	private String moDescription = "Modelica Model (*.mo)";
	private String mo = "mo";

	private String csmlDescription = "Cell Illustrator (*.csml)";
	private String csml = "csml";

	private String txtDescription = "Graph Text File (*.txt)";
	private String txt = "txt";

	private String irinaDescription = "Irina Export File (*.itxt)";
	private String irinaTxt = "itxt";

	private String csvDescription = "CSV Result Export (*.csv)";
	private String csv = "csv";

	private String pngDescription = "PNG Image (*.png)";
	private String png = "png";

	private String yamlDescription = "YAML File (*.yaml)";
	private String yaml = "yaml";

	private JPanel p = null;
	private JFileChooser chooser;

	/*
	 * public SaveDialog(boolean sbml) { this(sbml?1:1); }//
	 */
	// use power of 2
	public static int FORMAT_SBML = 1;
	public static int FORMAT_GRAPHML = 2;
	public static int FORMAT_MO = 4;
	public static int FORMAT_GON = 8;
	public static int FORMAT_VA = 16;
	public static int FORMAT_TXT = 32;
	public static int FORMAT_ITXT = 64;
	public static int FORMAT_PNML = 128;
	public static int FORMAT_CSV = 256;
	public static int FORMAT_PNG = 512;
	public static int FORMAT_YAML = 1024;

	public SaveDialog(int format, JPanel p) {

		this.p = p;

		// Get working directory
		String pathWorkingDirectory = null;
		if (SystemUtils.IS_OS_WINDOWS) {
			pathWorkingDirectory = System.getenv("APPDATA");
		} else {
			pathWorkingDirectory = System.getenv("HOME");
		}
		pathWorkingDirectory += File.separator + "vanesa";

		sbmlBool = (format & FORMAT_SBML) == FORMAT_SBML;
		moBool = (format & FORMAT_MO) == FORMAT_MO;
		graphMLBool = (format & FORMAT_GRAPHML) == FORMAT_GRAPHML;
		gonBool = (format & FORMAT_GON) == FORMAT_GON;
		vaBool = (format & FORMAT_VA) == FORMAT_VA;
		txtBool = (format & FORMAT_TXT) == FORMAT_TXT;
		itxtBool = (format & FORMAT_ITXT) == FORMAT_ITXT;
		pnmlBool = (format & FORMAT_PNML) == FORMAT_PNML;
		csvBool = (format & FORMAT_CSV) == FORMAT_CSV;
		pngBool = (format & FORMAT_PNG) == FORMAT_PNG;
		yamlBool = (format & FORMAT_YAML) == FORMAT_YAML;

		if (ConnectionSettings.getFileDirectory() != null) {
			chooser = new JFileChooser(ConnectionSettings.getFileDirectory());
		} else {
			// Use the path that was used last time
			String path = "";

			try {
				XMLConfiguration xmlSettings = new XMLConfiguration(pathWorkingDirectory + File.separator + "settings.xml");
				path = xmlSettings.getString("SaveDialog-Path");
			}
			catch(ConfigurationException e)
			{
				System.out.println("There is probably no " + pathWorkingDirectory + File.separator + "settings.xml yet.");
			}
			chooser = new JFileChooser(path);
		}

		chooser.setAcceptAllFileFilterUsed(false);

		if (sbmlBool)
			chooser.addChoosableFileFilter(new MyFileFilter(sbml,
					sbmlDescription));

		/*
		 * if (itxtBool) chooser.addChoosableFileFilter(new
		 * MyFileFilter(irinaTxt, irinaDescription));
		 */
		if (moBool)
			chooser.addChoosableFileFilter(new MyFileFilter(mo, moDescription));
		
		if (graphMLBool)
			chooser.addChoosableFileFilter(new MyFileFilter(graphMl,
					graphMlDescription));

		if (gonBool)
			chooser.addChoosableFileFilter(new MyFileFilter(csml,
					csmlDescription));
		if (pnmlBool)
			chooser.addChoosableFileFilter(new MyFileFilter(pnml,
					pnmlDescription));
		if (vaBool)
			chooser.addChoosableFileFilter(new MyFileFilter(vaml,
					vamlDescription));
		if (txtBool)
			chooser.addChoosableFileFilter(new MyFileFilter(txt, txtDescription));
		if (csvBool)
			chooser.addChoosableFileFilter(new MyFileFilter(csv, csvDescription));
		if (pngBool)
			chooser.addChoosableFileFilter(new MyFileFilter(png, pngDescription));
		if(yamlBool)
			chooser.addChoosableFileFilter(new MyFileFilter(yaml, yamlDescription));

		int option = chooser.showSaveDialog(MainWindow.getInstance());
		if (option == JFileChooser.APPROVE_OPTION) {
			// Save path to settings.xml
			File fileDir = chooser.getCurrentDirectory();
			XMLConfiguration xmlSettings = null;
			File f = new File(pathWorkingDirectory + File.separator + "settings.xml");
			try {
				if(f.exists()){
					xmlSettings = new XMLConfiguration(pathWorkingDirectory + File.separator + "settings.xml");
				}else{
					xmlSettings = new XMLConfiguration();
					xmlSettings.setFileName(pathWorkingDirectory + File.separator + "settings.xml");
				}
				xmlSettings.setProperty("SaveDialog-Path", fileDir.getAbsolutePath());
				xmlSettings.save();
			}
			catch(ConfigurationException e)
			{
				e.printStackTrace();
			}

			fileFormat = chooser.getFileFilter().getDescription();
			file = chooser.getSelectedFile();
			boolean overwrite = true;
			if (file.exists()) {
				int response = JOptionPane.showConfirmDialog(
						MainWindow.getInstance(),
						"Overwrite existing file?", "Confirm Overwrite",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.CANCEL_OPTION) {
					overwrite = false;
				}
			}
			if (overwrite) {
				try {
					write();
				} catch (FileNotFoundException | HeadlessException | XMLStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	public SaveDialog(int format) {
		this(format, null);
	}

	private void getCorrectFile(String ending) {

		String extension = file.getPath();
		int i = extension.lastIndexOf('.');
		if (i > 0 && i < extension.length() - 1) {
		} else {
			file = new File(file.getAbsolutePath() + "." + ending);
		}

	}

	private void write() throws FileNotFoundException, HeadlessException, XMLStreamException {

		ConnectionSettings.setFileDirectory(file.getAbsolutePath());
		if (fileFormat.equals(sbmlDescription)) {
			getCorrectFile(sbml);
			// create a sbmlOutput object
			// SBMLoutputNoWS sbmlOutput = new SBMLoutputNoWS(file, new
			// GraphInstance()
			// .getPathway());
			// //if (sbmlOutput.generateSBMLDocument())
			// JOptionPane.showMessageDialog(
			// MainWindowSingelton.getInstance(), sbmlDescription
			// + sbmlOutput.generateSBMLDocument());
			JSBMLoutput jsbmlOutput = new JSBMLoutput(
					new FileOutputStream(file),
					new GraphInstance().getPathway());
			
			String out = jsbmlOutput.generateSBMLDocument();
			if(out.length() > 0){
			JOptionPane.showMessageDialog(MainWindow.getInstance(),
					sbmlDescription + out);
			}else{
				MyPopUpSingleton.getInstance().show("JSbml export", "Saving was successful!");
			}

			// else
			// JOptionPane.showMessageDialog(
			// MainWindowSingelton.getInstance(), sbmlDescription
			// + " File not saved");
		} else if (fileFormat.equals(graphMlDescription)) {
			getCorrectFile(graphMl);
			new SaveGraphML(new FileOutputStream(file));
			JOptionPane.showMessageDialog(MainWindow.getInstance(),
					graphMlDescription + " File saved");
		} else if (fileFormat.equals(irinaDescription)) {
			getCorrectFile(irinaTxt);
			new IrinaGraphTextWriter(new FileOutputStream(file),
					new GraphInstance().getPathway());
			JOptionPane.showMessageDialog(MainWindow.getInstance(),
					irinaDescription + " File saved");

		} else if (fileFormat.equals(moDescription)) {
			getCorrectFile(mo);
			new MOoutput(new FileOutputStream(file),
					new GraphInstance().getPathway(), false);
			String path_colored = file.getAbsolutePath();
			if (path_colored.endsWith(".mo")) {
				path_colored = path_colored.substring(0, path_colored.length()-3);
			}
	
			new MOoutput(new FileOutputStream(new File(path_colored+"_colored.mo")),
					new GraphInstance().getPathway(), true);
			
			MyPopUpSingleton.getInstance().show("Modelica export", moDescription + " File saved");
			//JOptionPane.showMessageDialog(MainWindowSingleton.getInstance(),
				//	moDescription + " File saved");

		} else if (fileFormat.equals(txtDescription)) {
			getCorrectFile(txt);
			new GraphTextWriter(new FileOutputStream(file),
					new GraphInstance().getPathway());
			JOptionPane.showMessageDialog(MainWindow.getInstance(),
					txtDescription + " File saved");

		} else if (fileFormat.equals(csvDescription)) {
			getCorrectFile(csv);
			String result = new CSVWriter().write(new FileOutputStream(file),
					new GraphInstance().getPathway());
			
			if(result.length() > 0){
			JOptionPane.showMessageDialog(MainWindow.getInstance(),
					csvDescription + result);
			}else{
				MyPopUpSingleton.getInstance().show("csv", "Saving was successful!");
			}

		} else if (fileFormat.equals(pnmlDescription)) {
			GraphContainer con = ContainerSingelton.getInstance();
			MainWindow w = MainWindow.getInstance();
			if (!con.isPetriView()) {
				/*
				 * ConvertToPetriNet convertToPetriNet = new
				 * ConvertToPetriNet(); getCorrectFile(pnml); PNMLOutput
				 * pnmlOutput = new PNMLOutput(file,
				 * convertToPetriNet.getBiologicalEdges
				 * (),convertToPetriNet.getBiologicalNodes
				 * (),convertToPetriNet.getBiologicalTransitions());
				 *
				 * try { JOptionPane.showMessageDialog(
				 * MainWindowSingelton.getInstance(),pnmlDescription +
				 * pnmlOutput.generatePNMLDocument() + " File saved"); } catch
				 * (HeadlessException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); } catch (InvalidIDException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); } catch
				 * (VoidRepositoryException e) { // TODO Auto-generated catch
				 * block e.printStackTrace(); }
				 */
			} else {

				getCorrectFile(pnml);
				BiologicalEdgeAbstract bea;
				ArrayList<PNEdge> edgeList = new ArrayList<PNEdge>();
				Iterator<BiologicalEdgeAbstract> itEdge = con
						.getPathway(w.getCurrentPathway()).getAllEdges()
						.iterator();
				while (itEdge.hasNext()) {
					bea = itEdge.next();
					if (bea instanceof PNEdge) {
						edgeList.add((PNEdge) bea);
					}

				}
				BiologicalNodeAbstract bna;
				ArrayList<Place> nodeList = new ArrayList<Place>();
				ArrayList<Transition> transitionList = new ArrayList<Transition>();
				Iterator<BiologicalNodeAbstract> itNode = con
						.getPathway(w.getCurrentPathway()).getAllGraphNodes()
						.iterator();

				while (itNode.hasNext()) {
					bna = itNode.next();
					if (bna instanceof Place) {
						nodeList.add((Place) bna);
					} else if (bna instanceof Transition) {
						transitionList.add((Transition) bna);
					}
				}
				// only output on file system possible
				PNMLOutput pnmlOutput = new PNMLOutput(file, edgeList,
						nodeList, transitionList);

				try {
					String result = pnmlOutput.generatePNMLDocument();
					
					if(result.length() > 0){
						JOptionPane.showMessageDialog(
								MainWindow.getInstance(), pnmlDescription
										+ "an error occured: "+ result);
					}else{
						MyPopUpSingleton.getInstance().show("PNML export", "Saving was successful!");
					}
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidIDException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (VoidRepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else if (fileFormat.equals(csmlDescription)) {
			getCorrectFile(csml);
			new GONoutput(new FileOutputStream(file),
					new GraphInstance().getPathway());
			JOptionPane.showMessageDialog(MainWindow.getInstance(),
					csmlDescription + " File saved");

		} else if (fileFormat.equals(vamlDescription)) {
			getCorrectFile(vaml);
			try {
				new VAMLoutput(new FileOutputStream(file),
						new GraphInstance().getPathway());
				JOptionPane.showMessageDialog(
						MainWindow.getInstance(), vamlDescription
								+ " File saved");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (fileFormat.equals(pngDescription)) {
			getCorrectFile(png);
			if (p != null) {
				try {
					BufferedImage bi = new BufferedImage(p.getWidth(),
							p.getHeight(), BufferedImage.TYPE_INT_BGR);
					Graphics2D graphics = bi.createGraphics();
					p.paint(graphics);
					graphics.dispose();
					ImageIO.write(bi, "png", file);
					MyPopUpSingleton.getInstance().show("PNG", "Picture saved successfully!");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if(fileFormat.equals(yamlDescription)){
			getCorrectFile(yaml);
			try{
				String exportPath = chooser.getSelectedFile().getPath();

				if(exportPath.contains(".yaml") == false){
					exportPath = exportPath + ".yaml";
				}

				InputStream internYaml = getClass().getClassLoader().getResourceAsStream("resource/NodeProperties.yaml");
				FileOutputStream exportYaml = null;
				File exportFile = new File(exportPath);
				exportYaml = new FileOutputStream(exportFile);
				byte [] buffer = new byte[4096];
				int bytesRead = -1;
				bytesRead = internYaml.read(buffer);
				while(bytesRead != -1){
					exportYaml.write(buffer, 0 ,bytesRead);
					bytesRead = internYaml.read(buffer);
				};
					internYaml.close();
					exportYaml.close();
				JOptionPane.showMessageDialog(
						MainWindow.getInstance(), yamlDescription
									+ " File exported");
				MainWindow.getInstance().setLoadedYaml(exportPath);
			} catch (IOException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
