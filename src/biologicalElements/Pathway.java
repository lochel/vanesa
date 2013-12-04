package biologicalElements;

import graph.filter.FilterSettings;
import graph.jung.classes.MyGraph;
import gui.GraphTab;
import gui.MainWindow;
import gui.MainWindowSingelton;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import petriNet.ContinuousTransition;
import petriNet.DiscreteTransition;
import petriNet.PNEdge;
import petriNet.Place;
import petriNet.StochasticTransition;
import biologicalObjects.edges.Activation;
import biologicalObjects.edges.BindingAssociation;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.edges.Compound;
import biologicalObjects.edges.Dephosphorylation;
import biologicalObjects.edges.Dissociation;
import biologicalObjects.edges.Expression;
import biologicalObjects.edges.Glycosylation;
import biologicalObjects.edges.HiddenCompound;
import biologicalObjects.edges.IndirectEffect;
import biologicalObjects.edges.Inhibition;
import biologicalObjects.edges.Methylation;
import biologicalObjects.edges.Phosphorylation;
import biologicalObjects.edges.PhysicalInteraction;
import biologicalObjects.edges.ReactionEdge;
import biologicalObjects.edges.ReactionPair;
import biologicalObjects.edges.Repression;
import biologicalObjects.edges.StateChange;
import biologicalObjects.edges.Ubiquitination;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.CollectorNode;
import biologicalObjects.nodes.Complex;
import biologicalObjects.nodes.CompoundNode;
import biologicalObjects.nodes.DNA;
import biologicalObjects.nodes.Degraded;
import biologicalObjects.nodes.Disease;
import biologicalObjects.nodes.Drug;
import biologicalObjects.nodes.Enzyme;
import biologicalObjects.nodes.Gene;
import biologicalObjects.nodes.GeneOntology;
import biologicalObjects.nodes.Glycan;
import biologicalObjects.nodes.HomodimerFormation;
import biologicalObjects.nodes.LigandBinding;
import biologicalObjects.nodes.MRNA;
import biologicalObjects.nodes.MembraneChannel;
import biologicalObjects.nodes.MembraneReceptor;
import biologicalObjects.nodes.OrthologGroup;
import biologicalObjects.nodes.Other;
import biologicalObjects.nodes.PathwayMap;
import biologicalObjects.nodes.Protein;
import biologicalObjects.nodes.Reaction;
import biologicalObjects.nodes.Receptor;
import biologicalObjects.nodes.SRNA;
import biologicalObjects.nodes.SmallMolecule;
import biologicalObjects.nodes.SolubleReceptor;
import biologicalObjects.nodes.TranscriptionFactor;
import database.dawis.DAWISTree;
import edu.uci.ics.jung.graph.util.Pair;
//import edu.uci.ics.jung.graph.Vertex;
//import edu.uci.ics.jung.utils.Pair;

public class Pathway implements Cloneable{

	private File filename = null;

	private String name = "";

	private String version = "";

	private String date = "";

	private String title = "";

	private String author = "";

	private String referenceNumber = "";

	private String link = "";

	private String ImagePath = "";

	private String organism = "";

	private boolean orgSpecification;

	private String description = "";

	private String number = "";

	private Hashtable<String, Integer> nodeDescription = new Hashtable<String, Integer>();

	private boolean[] settings = new boolean[11];

	private boolean isPetriNet = false;

	private boolean isPetriNetSimulation = false;

	private final PetriNet petriNet = new PetriNet();

	public PetriNet getPetriNet() {
		return petriNet;
	}

	public boolean isPetriNet() {
		return isPetriNet;
	}

	public void setPetriNet(boolean isPetriNet) {
		this.isPetriNet = isPetriNet;
	}

	private final HashMap<String, GraphElementAbstract> biologicalElements = new HashMap<String, GraphElementAbstract>();

	// private HashSet <Vertex> set = new HashSet <Vertex> ();

	private HashSet<BiologicalNodeAbstract> set = new HashSet<BiologicalNodeAbstract>();

	private HashMap<Pair<BiologicalNodeAbstract>, BiologicalEdgeAbstract> edges = new HashMap<Pair<BiologicalNodeAbstract>, BiologicalEdgeAbstract>();

	private MyGraph graph;

	public void setGraph(MyGraph graph) {
		this.graph = graph;
	}

	private final GraphTab tab;

	private final FilterSettings filterSettings;

	private final InternalGraphRepresentation graphRepresentation = new InternalGraphRepresentation();

	private DAWISTree dawisTree = null;

	private boolean isDAWISProject = false;

	private Pathway parent;

	private SortedSet<Integer> ids = new TreeSet<Integer>();

	public void changeBackground(String color) {
		if (color.equals("black")) {
			graph.getVisualizationViewer().setBackground(Color.BLACK);
			graph.getVisualizationViewer().repaint();

			graph.getSatelliteView().setBackground(Color.WHITE);
			graph.getSatelliteView().repaint();

		} else if (color.equals("white")) {
			graph.getVisualizationViewer().setBackground(Color.WHITE);
			graph.getSatelliteView().setBackground(Color.WHITE);

			graph.getVisualizationViewer().repaint();

			graph.getSatelliteView().repaint();
		}
	}

	public void setDAWISProject() {
		isDAWISProject = true;
	}

	public boolean isDAWISProject() {
		return isDAWISProject;
	}

	public String getImagePath() {
		return ImagePath;
	}

	public void setImagePath(String imagePath) {
		ImagePath = imagePath;
	}

	public File getFilename() {
		return filename;
	}

	public void setFilename(File filename) {
		this.filename = filename;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		tab.setTitle(name);
	}

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Pathway(String name) {
		this.title = name;
		graph = new MyGraph(this);
		tab = new GraphTab(name, graph.getGraphVisualization());
		filterSettings = new FilterSettings();
	}

	public Pathway(String name, Pathway parent) {
		this(name);
		this.parent = parent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String name) {
		this.title = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public MyGraph getGraph() {
		return graph;
	}

	public GraphTab getTab() {
		return tab;
	}

	public void setTree(DAWISTree tree) {
		this.dawisTree = tree;
	}

	public DAWISTree getTree() {
		return this.dawisTree;
	}

	public BiologicalNodeAbstract addVertex(String name, String label,
			String elementDecleration, String compartment, Point2D p) {
		BiologicalNodeAbstract bna = null;

		if (elementDecleration.equals(Elementdeclerations.protein))
			bna = new Protein(label, name);
		else if (elementDecleration.equals(Elementdeclerations.enzyme))
			bna = new Enzyme(label, name);
		else if (elementDecleration
				.equals(Elementdeclerations.homodimerFormation))
			bna = new HomodimerFormation(label, name);
		else if (elementDecleration.equals(Elementdeclerations.degraded))
			bna = new Degraded(label, name);
		else if (elementDecleration.equals(Elementdeclerations.pathwayMap))
			bna = new PathwayMap(label, name);
		else if (elementDecleration.equals(Elementdeclerations.smallMolecule))
			bna = new SmallMolecule(label, name);
		else if (elementDecleration.equals(Elementdeclerations.solubleReceptor))
			bna = new SolubleReceptor(label, name);
		else if (elementDecleration.equals(Elementdeclerations.sRNA))
			bna = new SRNA(label, name);
		else if (elementDecleration.equals(Elementdeclerations.dna))
			bna = new DNA(label, name);
		else if (elementDecleration.equals(Elementdeclerations.complex))
			bna = new Complex(label, name);
		else if (elementDecleration.equals(Elementdeclerations.ligandBinding))
			bna = new LigandBinding(label, name);
		else if (elementDecleration.equals(Elementdeclerations.membraneChannel))
			bna = new MembraneChannel(label, name);
		else if (elementDecleration
				.equals(Elementdeclerations.membraneReceptor))
			bna = new MembraneReceptor(label, name);
		else if (elementDecleration.equals(Elementdeclerations.mRNA))
			bna = new MRNA(label, name);
		else if (elementDecleration.equals(Elementdeclerations.receptor))
			bna = new Receptor(label, name);
		else if (elementDecleration.equals(Elementdeclerations.place))
			bna = new Place(label, name, 0, true);
		else if (elementDecleration.equals(Elementdeclerations.s_place))
			bna = new Place(label, name, 0, false);
		// Transkription Factor node will be generated here
		else if (elementDecleration
				.equals(Elementdeclerations.transcriptionFactor)) {
			bna = new TranscriptionFactor(label, name);
		} else if (elementDecleration.equals(Elementdeclerations.glycan))
			bna = new Glycan(label, name);
		else if (elementDecleration.equals(Elementdeclerations.others))
			bna = new Other(label, name);
		else if (elementDecleration.equals(Elementdeclerations.orthologGroup))
			bna = new OrthologGroup(label, name);
		else if (elementDecleration.equals(Elementdeclerations.compound))
			bna = new CompoundNode(label, name);
		else if (elementDecleration.equals(Elementdeclerations.disease))
			bna = new Disease(label, name);
		else if (elementDecleration.equals(Elementdeclerations.drug))
			bna = new Drug(label, name);
		else if (elementDecleration.equals(Elementdeclerations.go))
			bna = new GeneOntology(label, name);
		else if (elementDecleration.equals(Elementdeclerations.gene))
			bna = new Gene(label, name);
		else if (elementDecleration.equals(Elementdeclerations.reaction))
			bna = new Reaction(label, name);
		else if (elementDecleration.equals(Elementdeclerations.collector))
			bna = new CollectorNode(label, name);
		else if (elementDecleration
				.equals(Elementdeclerations.stochasticTransition))
			bna = new StochasticTransition(label, name);
		else if (elementDecleration
				.equals(Elementdeclerations.discreteTransition))
			bna = new DiscreteTransition(label, name);
		else if (elementDecleration
				.equals(Elementdeclerations.continuousTransition))
			bna = new ContinuousTransition(label, name);
		bna.setCompartment(compartment);
		return addVertex(bna, p);
	}

	public BiologicalNodeAbstract addVertex(BiologicalNodeAbstract bna,
			Point2D p) {
		// System.out.println("add");
		// System.out.println(bna.getClass().getName());
		// Object graphElement = element;
		// GraphElementAbstract gea = (GraphElementAbstract) element;
		bna.setLabel(bna.getLabel().trim());
		bna.setName(bna.getName().trim());

		/*if (bna.isAbstract()) {
			if (bna.getBiologicalElement().equals(Elementdeclerations.protein))
				bna = new Protein(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.enzyme))
				bna = new Enzyme(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.homodimerFormation))
				bna = new HomodimerFormation(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.degraded))
				bna = new Degraded(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.pathwayMap))
				bna = new PathwayMap(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.smallMolecule))
				bna = new SmallMolecule(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.solubleReceptor))
				bna = new SolubleReceptor(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement()
					.equals(Elementdeclerations.sRNA))
				bna = new SRNA(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(Elementdeclerations.dna))
				bna = new DNA(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.complex))
				bna = new Complex(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.ligandBinding))
				bna = new LigandBinding(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.membraneChannel))
				bna = new MembraneChannel(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.membraneReceptor))
				bna = new MembraneReceptor(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement()
					.equals(Elementdeclerations.mRNA))
				bna = new MRNA(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.receptor))
				bna = new Receptor(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.place))
				bna = new Place(bna.getLabel(), bna.getName(), 0, true);
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.s_place))
				bna = new Place(bna.getLabel(), bna.getName(), 0, false);
			// Transkription Factor node will be generated here
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.transcriptionFactor)) {
				bna = new TranscriptionFactor(bna.getLabel(), bna.getName());
			} else if (bna.getBiologicalElement().equals(
					Elementdeclerations.glycan))
				bna = new Glycan(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.others))
				bna = new Other(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.orthologGroup))
				bna = new OrthologGroup(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.compound))
				bna = new CompoundNode(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.disease))
				bna = new Disease(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement()
					.equals(Elementdeclerations.drug))
				bna = new Drug(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(Elementdeclerations.go))
				bna = new GeneOntology(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement()
					.equals(Elementdeclerations.gene))
				bna = new Gene(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.reaction))
				bna = new Reaction(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.collector))
				bna = new CollectorNode(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.stochasticTransition))
				bna = new StochasticTransition(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.discreteTransition))
				bna = new DiscreteTransition(bna.getLabel(), bna.getName());
			else if (bna.getBiologicalElement().equals(
					Elementdeclerations.continuousTransition))
				bna = new ContinuousTransition(bna.getLabel(), bna.getName());

		}*/
		(bna).setCompartment(bna.getCompartment());
		biologicalElements.put(bna.getID() + "", bna);
		// System.out.println(biologicalElements.size());
		graphRepresentation.addVertex(bna);
		graph.addVertex(bna, p);
		// System.out.println("node eingefuegt");

		if (!nodeDescription.containsKey(bna.getBiologicalElement())) {
			nodeDescription.put(bna.getBiologicalElement(), 1);
		} else {
			Integer temp = nodeDescription.get(bna.getBiologicalElement()) + 1;
			nodeDescription.remove(bna.getBiologicalElement());
			nodeDescription.put(bna.getBiologicalElement(), temp);
		}
		bna.setID();
		//System.out.println(this.graph.getAllVertices().size());
		//bna.setNodesize(this.graph.getAllVertices().size());
		return bna;
	}

	public BiologicalEdgeAbstract addEdge(String label, String name,
			BiologicalNodeAbstract from, BiologicalNodeAbstract to,
			String element, boolean directed) {

		BiologicalEdgeAbstract bea = null;

		if (element.equals(Elementdeclerations.compoundEdge)) {

			bea = new Compound(label, name, from, to);

		} else if (element.equals(Elementdeclerations.hiddenCompoundEdge)) {

			bea = new HiddenCompound(label, name, from, to);

		} else if (element.equals(Elementdeclerations.reactionEdge)) {
			// System.out.println("drin");
			bea = new ReactionEdge(label, name, from, to);

		} else if (element.equals(Elementdeclerations.activationEdge)) {

			bea = new Activation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.inhibitionEdge)) {

			bea = new Inhibition(label, name, from, to);

		} else if (element.equals(Elementdeclerations.expressionEdge)) {

			bea = new Expression(label, name, from, to);

		} else if (element.equals(Elementdeclerations.repressionEdge)) {

			bea = new Repression(label, name, from, to);

		} else if (element.equals(Elementdeclerations.indirectEffectEdge)) {

			bea = new IndirectEffect(label, name, from, to);

		} else if (element.equals(Elementdeclerations.stateChangeEdge)) {

			bea = new StateChange(label, name, from, to);

		} else if (element.equals(Elementdeclerations.bindingEdge)) {

			bea = new BindingAssociation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.dissociationEdge)) {

			bea = new Dissociation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.phosphorylationEdge)) {

			bea = new Phosphorylation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.dephosphorylationEdge)) {

			bea = new Dephosphorylation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.glycosylationEdge)) {

			bea = new Glycosylation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.ubiquitinationEdge)) {

			bea = new Ubiquitination(label, name, from, to);

		} else if (element.equals(Elementdeclerations.methylationEdge)) {

			bea = new Methylation(label, name, from, to);

		} else if (element.equals(Elementdeclerations.reactionPairEdge)) {

			bea = new ReactionPair(label, name, from, to);

		} else if (element.equals(Elementdeclerations.physicalInteraction)) {

			bea = new PhysicalInteraction(label, name, from, to);

		} else if (element.equals(Elementdeclerations.pnDiscreteEdge)) {
			String tokens = "1";
			boolean wasUndirected = false;
			double UpperBoundary = 0.0;
			double LowerBoundary = 0.0;
			double ActivationProbability = 1.0;
			/*if (bea instanceof PNEdge) {
				PNEdge e = (PNEdge) bea;
				tokens = e.getFunction();
				wasUndirected = e.wasUndirected();
				UpperBoundary = e.getUpperBoundary();
				LowerBoundary = e.getLowerBoundary();
				ActivationProbability = e.getActivationProbability();
			}*/
			bea = new PNEdge(from, to, label, name, "discrete", label);
			((PNEdge) bea).wasUndirected(wasUndirected);
			((PNEdge) bea).setLowerBoundary(LowerBoundary);
			((PNEdge) bea).setUpperBoundary(UpperBoundary);
			((PNEdge) bea).setActivationProbability(ActivationProbability);
			((PNEdge) bea).setBiologicalElement(bea.getBiologicalElement());
			// System.out.println("discrete kante pw hinzugefuegt");

		} else if (element.equals(Elementdeclerations.pnContinuousEdge)) {
			String tokens = "1";
			boolean wasUndirected = false;
			double UpperBoundary = 0.0;
			double LowerBoundary = 0.0;
			double ActivationProbability = 1.0;
			/*if (bea instanceof PNEdge) {
				PNEdge e = (PNEdge) bea;
				tokens = e.getFunction();
				wasUndirected = e.wasUndirected();
				UpperBoundary = e.getUpperBoundary();
				LowerBoundary = e.getLowerBoundary();
				ActivationProbability = e.getActivationProbability();
			}*/
			bea = new PNEdge(from, to, label, name,
					Elementdeclerations.pnContinuousEdge, label);
			((PNEdge) bea).wasUndirected(wasUndirected);
			((PNEdge) bea).setLowerBoundary(LowerBoundary);
			((PNEdge) bea).setUpperBoundary(UpperBoundary);
			((PNEdge) bea).setActivationProbability(ActivationProbability);
			((PNEdge) bea).setBiologicalElement(bea.getBiologicalElement());
			// System.out.println("continuous kante pw hinzugefuegt");

		} else if (element.equals(Elementdeclerations.pnInhibitionEdge)) {
			String tokens = "1";
			boolean wasUndirected = false;
			double UpperBoundary = 0.0;
			double LowerBoundary = 0.0;
			double ActivationProbability = 1.0;
			/*if (bea instanceof PNEdge) {
				PNEdge e = (PNEdge) bea;
				tokens = e.getFunction();
				wasUndirected = e.wasUndirected();
				UpperBoundary = e.getUpperBoundary();
				LowerBoundary = e.getLowerBoundary();
				ActivationProbability = e.getActivationProbability();
			}*/
			bea = new PNEdge(from, to, label, name,
					Elementdeclerations.pnInhibitionEdge, label);
			((PNEdge) bea).wasUndirected(wasUndirected);
			((PNEdge) bea).setLowerBoundary(LowerBoundary);
			((PNEdge) bea).setUpperBoundary(UpperBoundary);
			((PNEdge) bea).setActivationProbability(ActivationProbability);
			((PNEdge) bea).setBiologicalElement(bea.getBiologicalElement());
			// System.out.println("inhibition kante pw hinzugefuegt");

		}
		bea.setDirected(directed);
		return this.addEdge(bea);
	}

	public BiologicalEdgeAbstract addEdge(BiologicalEdgeAbstract bea) {
		// System.out.println("pw edge adden "+bea.getID());
		// System.out.println(bea.isAbstract());
		// BiologicalEdgeAbstract bea = null;
		// System.out.println(bea.isAbstract());
		// System.out.println(bea.getBiologicalElement());
		/*if (bea.isAbstract()) {

			if (bea.getBiologicalElement().equals(
					Elementdeclerations.compoundEdge)) {

				bea = new Compound(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.hiddenCompoundEdge)) {

				bea = new HiddenCompound(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.reactionEdge)) {
				// System.out.println("drin");
				bea = new ReactionEdge(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.activationEdge)) {

				bea = new Activation(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.inhibitionEdge)) {

				bea = new Inhibition(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.expressionEdge)) {

				bea = new Expression(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.repressionEdge)) {

				bea = new Repression(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.indirectEffectEdge)) {

				bea = new IndirectEffect(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.stateChangeEdge)) {

				bea = new StateChange(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.bindingEdge)) {

				bea = new BindingAssociation(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.dissociationEdge)) {

				bea = new Dissociation(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.phosphorylationEdge)) {

				bea = new Phosphorylation(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.dephosphorylationEdge)) {

				bea = new Dephosphorylation(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.glycosylationEdge)) {

				bea = new Glycosylation(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.ubiquitinationEdge)) {

				bea = new Ubiquitination(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.methylationEdge)) {

				bea = new Methylation(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.reactionPairEdge)) {

				bea = new ReactionPair(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.physicalInteraction)) {

				bea = new PhysicalInteraction(bea.getLabel(), bea.getName(),
						bea.getFrom(), bea.getTo());

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.pnDiscreteEdge)) {
				String tokens = "1";
				boolean wasUndirected = false;
				double UpperBoundary = 0.0;
				double LowerBoundary = 0.0;
				double ActivationProbability = 1.0;
				if (bea instanceof PNEdge) {
					PNEdge e = (PNEdge) bea;
					tokens = e.getFunction();
					wasUndirected = e.wasUndirected();
					UpperBoundary = e.getUpperBoundary();
					LowerBoundary = e.getLowerBoundary();
					ActivationProbability = e.getActivationProbability();
				}
				bea = new PNEdge(bea.getFrom(), bea.getTo(), bea.getLabel(),
						bea.getName(), "discrete", tokens);
				((PNEdge) bea).wasUndirected(wasUndirected);
				((PNEdge) bea).setLowerBoundary(LowerBoundary);
				((PNEdge) bea).setUpperBoundary(UpperBoundary);
				((PNEdge) bea).setActivationProbability(ActivationProbability);
				((PNEdge) bea).setBiologicalElement(bea.getBiologicalElement());
				// System.out.println("discrete kante pw hinzugefuegt");

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.pnContinuousEdge)) {
				String tokens = "1";
				boolean wasUndirected = false;
				double UpperBoundary = 0.0;
				double LowerBoundary = 0.0;
				double ActivationProbability = 1.0;
				if (bea instanceof PNEdge) {
					PNEdge e = (PNEdge) bea;
					tokens = e.getFunction();
					wasUndirected = e.wasUndirected();
					UpperBoundary = e.getUpperBoundary();
					LowerBoundary = e.getLowerBoundary();
					ActivationProbability = e.getActivationProbability();
				}
				bea = new PNEdge(bea.getFrom(), bea.getTo(), bea.getLabel(),
						bea.getName(), Elementdeclerations.pnContinuousEdge,
						tokens);
				((PNEdge) bea).wasUndirected(wasUndirected);
				((PNEdge) bea).setLowerBoundary(LowerBoundary);
				((PNEdge) bea).setUpperBoundary(UpperBoundary);
				((PNEdge) bea).setActivationProbability(ActivationProbability);
				((PNEdge) bea).setBiologicalElement(bea.getBiologicalElement());
				// System.out.println("continuous kante pw hinzugefuegt");

			} else if (bea.getBiologicalElement().equals(
					Elementdeclerations.pnInhibitionEdge)) {
				String tokens = "1";
				boolean wasUndirected = false;
				double UpperBoundary = 0.0;
				double LowerBoundary = 0.0;
				double ActivationProbability = 1.0;
				if (bea instanceof PNEdge) {
					PNEdge e = (PNEdge) bea;
					tokens = e.getFunction();
					wasUndirected = e.wasUndirected();
					UpperBoundary = e.getUpperBoundary();
					LowerBoundary = e.getLowerBoundary();
					ActivationProbability = e.getActivationProbability();
				}
				bea = new PNEdge(bea.getFrom(), bea.getTo(), bea.getLabel(),
						bea.getName(), Elementdeclerations.pnInhibitionEdge,
						tokens);
				((PNEdge) bea).wasUndirected(wasUndirected);
				((PNEdge) bea).setLowerBoundary(LowerBoundary);
				((PNEdge) bea).setUpperBoundary(UpperBoundary);
				((PNEdge) bea).setActivationProbability(ActivationProbability);
				((PNEdge) bea).setBiologicalElement(bea.getBiologicalElement());
				// System.out.println("inhibition kante pw hinzugefuegt");

			} else {
				// System.out.println("else");
				graph.addEdge(bea);
			}

		}*/

		if (bea != null) {
			bea.setDirected(bea.isDirected());
		}

		// System.out.println("id in pw: "+bea.getID());
		// System.out.println("edge hinzugefuegt");
		biologicalElements.put(bea.getID() + "", bea);
		edges.put(new Pair<BiologicalNodeAbstract>(bea.getFrom(), bea.getTo()),
				bea);
		// System.out.println(biologicalElements.size());
		// Pair p = bea.getEdge().getEndpoints();
		graphRepresentation.addEdge(bea);
		graph.addEdge(bea);
		bea.setID();
		return bea;
	}

	public void removeElement(GraphElementAbstract element) {
		if (element != null) {
			if (element.isVertex()) {
				// System.out.println(biologicalElements.size());
				// System.out.println("drin");
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) element;
				graphRepresentation.removeVertex(bna);
				graph.removeVertex(bna);
				// System.out.println("durch");
				if (!nodeDescription.containsKey(bna.getBiologicalElement())) {
				} else {
					Integer temp = nodeDescription.get(bna
							.getBiologicalElement());
					// System.out.println(temp);
					if (temp == 1) {
						nodeDescription.remove(bna.getBiologicalElement());
					} else {
						nodeDescription.remove(bna.getBiologicalElement());
						nodeDescription.put(bna.getBiologicalElement(),
								temp - 1);
					}
				}

			} else {
				BiologicalEdgeAbstract bea = (BiologicalEdgeAbstract) element;
				// Pair p = bea.getEdge().getEndpoints();
				// System.out.println(edges.size());
				edges.remove(new Pair<BiologicalNodeAbstract>(bea.getFrom(),
						bea.getTo()));
				graphRepresentation.removeEdge(bea);
				graph.removeEdge(bea);
				// System.out.println(edges.size());
			}
			ids.remove(element.getID());
			// System.out.println(biologicalElements.size());
			biologicalElements.remove(element.getID() + "");
			// System.out.println(biologicalElements.size());
		}
	}

	/*
	 * public Object getElement(Object graphElement) {
	 * 
	 * if (biologicalElements.get(graphElement) != null) { return
	 * biologicalElements.get(graphElement); } else return null; }
	 */

	public boolean existEdge(BiologicalNodeAbstract from,
			BiologicalNodeAbstract to) {

		return edges.containsKey(new Pair<BiologicalNodeAbstract>(from, to));
	}

	public boolean containsElement(Object graphElement) {
		return biologicalElements.containsValue(graphElement);
	}

	public Set<String> getAllNodeDescriptions() {

		return nodeDescription.keySet();
	}

	public HashSet<String> getAllNodeLabels() {

		HashSet<String> set = new HashSet<String>();
		Iterator<BiologicalNodeAbstract> it = this.getAllNodes().iterator();// biologicalElements.values().iterator();

		while (it.hasNext()) {
			set.add(it.next().getLabel());
		}
		return set;
	}

	public boolean hasGotAtLeastOneElement() {
		if (biologicalElements.size() > 0)
			return true;
		else
			return false;
	}

	public BiologicalNodeAbstract getNodeByName(String name) {

		Iterator<GraphElementAbstract> it = biologicalElements.values().iterator();
		GraphElementAbstract gea;
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			gea = it.next();
			if (gea.isVertex()) {
				bna = (BiologicalNodeAbstract) gea;
				if (bna.getName().equals(name)) {
					return bna;
				}
			}
		}
		return null;
	}

	/*
	 * public BiologicalNodeAbstract getNodeByVertexID(String vertexID) {
	 * 
	 * Iterator it = biologicalElements.values().iterator();
	 * 
	 * while (it.hasNext()) { Object obj = it.next(); GraphElementAbstract gea =
	 * (GraphElementAbstract) obj; if (gea.isVertex()) { BiologicalNodeAbstract
	 * bna = (BiologicalNodeAbstract) obj; if
	 * (bna.getVertex().toString().equals(vertexID)) { return bna; } } } return
	 * null; }
	 */

	public BiologicalNodeAbstract getNodeByLabel(String label) {

		Iterator<GraphElementAbstract> it = biologicalElements.values().iterator();

		GraphElementAbstract gea;
		BiologicalNodeAbstract bna;
		while (it.hasNext()) {
			gea = it.next();
			if (gea.isVertex()) {
				bna = (BiologicalNodeAbstract) gea;
				if (bna.getLabel().equals(label)) {
					return bna;
				}
			}
		}
		return null;
	}

	public Object getNodeByKEGGEntryID(String id) {

		Iterator it = biologicalElements.values().iterator();

		while (it.hasNext()) {
			Object obj = it.next();
			GraphElementAbstract gea = (GraphElementAbstract) obj;
			if (gea.isVertex()) {
				BiologicalNodeAbstract bna = (BiologicalNodeAbstract) obj;
				if (bna.getKEGGnode().getKEGGentryID().equals(id)) {
					return obj;
				}
			}
		}
		return null;
	}

	public HashSet getAllKEGGEdges() {

		Iterator it = biologicalElements.values().iterator();
		HashSet set = new HashSet();

		while (it.hasNext()) {
			Object obj = it.next();
			GraphElementAbstract gea = (GraphElementAbstract) obj;
			if (gea.isEdge() && gea.hasKEGGEdge()) {
				set.add(obj);
			}
		}
		return set;
	}

	public HashSet getAllReactionPairEdges() {

		Iterator it = biologicalElements.values().iterator();
		HashSet set = new HashSet();

		while (it.hasNext()) {
			Object obj = it.next();
			GraphElementAbstract gea = (GraphElementAbstract) obj;
			if (gea.isEdge() && gea.hasReactionPairEdge()) {
				set.add(obj);
			}
		}
		return set;
	}

	/*
	 * public boolean existEdge(Vertex vertex1, Vertex vertex2) { for (Iterator
	 * i = getAllEdges().iterator(); i.hasNext();) { BiologicalEdgeAbstract bea
	 * = (BiologicalEdgeAbstract) i.next(); if
	 * (bea.getEdge().getEndpoints().getFirst().equals(vertex1) &&
	 * bea.getEdge().getEndpoints().getSecond().equals(vertex2)) return true; }
	 * return false; }
	 */

	public Collection<BiologicalEdgeAbstract> getAllEdges() {

		/*
		 * Iterator<GraphElementAbstract> it =
		 * biologicalElements.values().iterator();
		 * HashSet<BiologicalEdgeAbstract> set = new
		 * HashSet<BiologicalEdgeAbstract>();
		 * 
		 * GraphElementAbstract gea; while (it.hasNext()) { gea = it.next(); if
		 * (gea instanceof BiologicalEdgeAbstract) {
		 * set.add((BiologicalEdgeAbstract)gea); } } return set;
		 */
		return this.graph.getAllEdges();
	}

	public Collection<BiologicalNodeAbstract> getAllNodes() {

		/*
		 * Iterator<GraphElementAbstract> it =
		 * biologicalElements.values().iterator();
		 * HashSet<BiologicalNodeAbstract> set = new
		 * HashSet<BiologicalNodeAbstract>();
		 * 
		 * while (it.hasNext()) { Object obj = it.next(); GraphElementAbstract
		 * gea = (GraphElementAbstract) obj; if (gea.isVertex()) {
		 * set.add((BiologicalNodeAbstract)obj); } }
		 * 
		 * return set;
		 */
		return this.graph.getAllVertices();
	}

	public Vector getAllNodesAsVector() {

		Iterator it = biologicalElements.values().iterator();
		Vector set = new Vector();

		while (it.hasNext()) {
			Object obj = it.next();
			GraphElementAbstract gea = (GraphElementAbstract) obj;
			if (gea.isVertex()) {
				set.add(obj);
			}
		}

		return set;
	}

	public Vector getAllEdgesAsVector() {

		Iterator it = biologicalElements.values().iterator();
		Vector set = new Vector();

		while (it.hasNext()) {
			Object obj = it.next();
			GraphElementAbstract gea = (GraphElementAbstract) obj;
			if (gea.isEdge()) {
				set.add(obj);
			}
		}

		return set;
	}

	public HashMap<String, GraphElementAbstract> getBiologicalElements() {
		return biologicalElements;
	}

	public FilterSettings getFilterSettings() {
		return filterSettings;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public InternalGraphRepresentation getGraphRepresentation() {
		return graphRepresentation;
	}

	public void setSettings(boolean[] set) {
		settings = set;
	}

	public String[] getSettingsAsString() {
		String[] set = new String[11];
		for (int i = 0; i < 11; i++) {
			set[i] = settings[i] + "";
		}
		return set;
	}

	public boolean[] getSettings() {
		return settings;
	}

	public void setNewLoadedNodes(HashSet<BiologicalNodeAbstract> loadedElements) {
		set = loadedElements;
	}

	public HashSet<BiologicalNodeAbstract> getNewLoadedNodes() {
		return set;
	}

	public void setSpecification(boolean organismSpecific) {
		orgSpecification = organismSpecific;
	}

	public boolean getSpecification() {
		return orgSpecification;
	}

	public void setSpecification(String organismSpecific) {
		if (organismSpecific.equalsIgnoreCase("true")) {
			orgSpecification = true;
		} else {
			orgSpecification = false;
		}
	}

	public String getSpecificationAsString() {
		return orgSpecification + "";
	}

	public Vector<BiologicalNodeAbstract> getSelectedNodes() {
		Vector<BiologicalNodeAbstract> ve = new Vector<BiologicalNodeAbstract>();
		Iterator<BiologicalNodeAbstract> it = graph.getVisualizationViewer()
				.getPickedVertexState().getPicked().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract v = it.next();
			ve.add(v);
		}
		return ve;
	}

	public int countNodes() {
		return getAllNodesAsVector().size();
	}

	public int countEdges() {
		return getAllEdgesAsVector().size();
	}

	public void setPetriNetSimulation(boolean isPetriNetSimulation) {
		this.isPetriNetSimulation = isPetriNetSimulation;
	}

	public boolean isPetriNetSimulation() {
		return isPetriNetSimulation;
	}

	public void setParent(Pathway parent) {
		this.parent = parent;
	}

	public Pathway getParent() {
		return parent;
	}

	public Pathway getRootPathway() {
		if (getParent() == null)
			return this;
		else
			return parent.getRootPathway();
	}

	public ArrayList<Pathway> getChilds() {
		ArrayList<Pathway> result = new ArrayList<Pathway>();
		Iterator<BiologicalNodeAbstract> it = getAllNodes().iterator();
		while (it.hasNext()) {
			BiologicalNodeAbstract bna = it.next();
			if (bna instanceof PathwayMap
					&& ((PathwayMap) bna).getPathwayLink() != null)
				result.add(((PathwayMap) bna).getPathwayLink());
		}
		return result;
	}

	public SortedSet<Integer> getIdSet() {
		return this.ids;
	}
	
	@Override
	  public Pathway clone()
	  {
	    try
	    {
	      return (Pathway) super.clone();
	    }
	    catch ( CloneNotSupportedException e ) {
	      // Kann eigentlich nicht passieren, da Cloneable
	      throw new InternalError();
	    }
	  }

}
