package graph.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import biologicalElements.GraphElementAbstract;
import biologicalElements.Pathway;
import biologicalObjects.nodes.BiologicalNodeAbstract;
import biologicalObjects.nodes.DynamicNode;
import graph.ChangedFlags;
import graph.GraphInstance;
import gui.MainWindow;
import gui.MyPopUp;
import net.miginfocom.swing.MigLayout;

public class ParameterWindow implements ActionListener, DocumentListener {

	private JPanel panel;
	private JOptionPane pane;
	private JTextField name = new JTextField("");
	private GraphInstance graphInstance = new GraphInstance();
	private Pathway pw = graphInstance.getPathway();
	private JTextField value = new JTextField("");
	private JTextField unit = new JTextField("");
	private JButton add;
	private GraphElementAbstract gea;
	private FormularPanel fp;
	private JTextPane formular;

	private boolean editMode = false;

	private JDialog dialog;

	// private HashMap<JButton, Parameter> parameters = new HashMap<JButton,
	// Parameter>();

	public ParameterWindow(GraphElementAbstract gea) {
		this.gea = gea;
		// System.out.println("constr.");

		MigLayout layout = new MigLayout("", "[left]");

		// DefaultComboBoxModel<String> dcbm = new
		// DefaultComboBoxModel<String>(ElementNamesSingelton.getInstance().getEnzymes());
		// elementNames.setEditable(true);
		// elementNames.setModel(dcbm);

		// elementNames.setMaximumSize(new Dimension(250,40));
		// elementNames.setSelectedItem(" ");
		// AutoCompleteDecorator.decorate(elementNames);

		panel = new JPanel(layout);
		formular = new JTextPane();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				AutoSuggestor autoSuggestor = new AutoSuggestor(formular, dialog, null, Color.WHITE.brighter(),
						Color.BLUE, Color.RED, 0.75f) {
					@Override
					boolean wordTyped(String typedWord) {

						// create list for dictionary this in your case might be
						// done via calling a method which queries db and
						// returns results as arraylist
						ArrayList<String> words = new ArrayList<>();

						// pw.getAllNodeLabels();
						// HashSet<String> set = new HashSet<String>();
						Iterator<BiologicalNodeAbstract> it = pw.getAllGraphNodes().iterator();// biologicalElements.values().iterator();
						BiologicalNodeAbstract bna;
						while (it.hasNext()) {
							bna = it.next();
							if (!bna.hasRef()) {
								// words.add(bna.getRef().getLink());
								words.add(bna.getLabel());
							}
						}

						for (int i = 0; i < gea.getParameters().size(); i++) {
							words.add(gea.getParameters().get(i).getName());
						}

						setDictionary(words);
						// addToDictionary("bye");//adds a single word

						return super.wordTyped(typedWord);// now call super to
															// check for any
															// matches against
															// newest dictionary
					}
				};
			}
		});

		if (gea instanceof DynamicNode) {
			fp = new FormularPanel(formular, ((DynamicNode) gea).getMaximumSpeed());
			fp.setVisible(true);
			panel.add(fp);
		}

		name.getDocument().addDocumentListener(this);
		add = new JButton("add");
		add.setActionCommand("add");
		add.addActionListener(this);

		pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);

		dialog = pane.createDialog(null, "Parameters");
		this.repaint();
		dialog.setResizable(true);
		dialog.setLocationRelativeTo(MainWindow.getInstance());
		dialog.pack();
		// dialog.show();
		dialog.setVisible(true);

		if (pane.getValue() != null && (int) pane.getValue() == JOptionPane.OK_OPTION) {
			if (gea instanceof DynamicNode) {
				DynamicNode dn = (DynamicNode) gea;
				String formular = fp.getFormular();
				String formularClean = formular.replaceAll("\\s", "");
				// System.out.println(":"+formularClean+":");
				String orgClean = dn.getMaximumSpeed().replaceAll("\\s", "");

				if (!orgClean.equals(formularClean)) {
					dn.setMaximumSpeed(formular);
					pw.handleChangeFlags(ChangedFlags.PNPROPERTIES_CHANGED);
				}
			}
		}
	}

	private void listParameters() {
		Parameter p;
		panel.add(new JLabel("Name"), "span 1, gaptop 2");
		panel.add(new JLabel("Value"), "span 1, gapright 4");
		panel.add(new JLabel("Unit"), "span 1, gapright 4, wrap");

		for (int i = 0; i < gea.getParameters().size(); i++) {
			p = gea.getParameters().get(i);
			panel.add(new JLabel(p.getName()), "span 1, gaptop 2");

			panel.add(new JLabel(p.getValue() + ""), "span 1, gapright 4");

			panel.add(new JLabel(p.getUnit()), "span 1, gapright 4");

			JButton edit = new JButton("✎");
			edit.setActionCommand("edit" + i);
			edit.addActionListener(this);
			edit.setToolTipText("edit entry");
			edit.setMaximumSize(edit.getMinimumSize());

			JButton del = new JButton("✖");
			del.setBackground(Color.RED);
			del.setActionCommand("del" + i);
			del.setToolTipText("delete entry");

			del.addActionListener(this);

			JButton up = new JButton("↑");
			up.setActionCommand("up" + i);
			up.addActionListener(this);
			up.setToolTipText("move up");

			JButton down = new JButton("↓");
			down.setActionCommand("down" + i);
			down.addActionListener(this);
			down.setToolTipText("move down");

			if (gea.getParameters().size() > 1) {
				if (i == 0) {
					panel.add(down, "skip, span 1");
				} else if (i == gea.getParameters().size() - 1) {
					panel.add(up, "span 1, gapright 4");
				} else {
					panel.add(up, "span 1, gapright 4");
					panel.add(down, "span 1");
				}
			}
			if (i == gea.getParameters().size() - 1) {
				panel.add(edit, "skip, span 1");
			} else {
				panel.add(edit, "span 1");
			}
			if (gea.getParameters().size() == 1) {
				panel.add(del, "wrap");
			} else {
				panel.add(del, "span 1, wrap");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println(e.getActionCommand());
		if ("add".equals(e.getActionCommand())) {
			Parameter p;
			for (int i = 0; i < gea.getParameters().size(); i++) {
				p = gea.getParameters().get(i);
				if (p.getName().equals(name.getText())) {
					if (this.editMode && name.getText().trim().length() > 0) {
						try {
							p.setValue(Double.valueOf(this.value.getText()));
							p.setUnit(unit.getText());
							// TODO handling of rebuild
							pw.handleChangeFlags(ChangedFlags.NODE_CHANGED);
							this.editMode = false;
							this.add.setText("add");
							this.repaint();
						} catch (NumberFormatException nfe) {
							MyPopUp.getInstance().show("Parameter", "Parameter not correct. Value not a number or empty?");
						}
					} else {
						// System.out.println("schon vorhanden");
						JOptionPane.showMessageDialog(dialog,
								"Parameter with same name already exists! Use edit button to edit parameter",
								"Parameter warning", JOptionPane.WARNING_MESSAGE);
					}
					return;
				}
			}
			if(name.getText().trim().length() > 0){
			try {
				p = new Parameter(name.getText(), Double.valueOf(value.getText()), unit.getText());
				gea.getParameters().add(p);
				pw.getChangedParameters().put(p, gea);
				pw.handleChangeFlags(ChangedFlags.PARAMETER_CHANGED);
				panel.add(new JLabel(name.getText()), "span 1, gaptop 2 ");
				panel.add(new JLabel(value.getText()), "span 1, gapright 4");
				panel.add(new JLabel(unit.getText()), "span 1, gapright 4, wrap");
				this.repaint();
			} catch (NumberFormatException nfx) {
				MyPopUp.getInstance().show("Parameter", "Parameter not correct. Value not a number or empty?");
			}
		}else{
			MyPopUp.getInstance().show("Parameter", "Name is empty!");
		}

		} else if (e.getActionCommand().startsWith("del")) {
			pw.handleChangeFlags(ChangedFlags.PARAMETER_CHANGED);
			int idx = Integer.parseInt(e.getActionCommand().substring(3));
			pw.getChangedParameters().remove(gea.getParameters().get(idx));
			this.gea.getParameters().remove(idx);
			this.repaint();
		} else if (e.getActionCommand().startsWith("down")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(4));
			Parameter p = gea.getParameters().get(idx);
			gea.getParameters().set(idx, gea.getParameters().get(idx + 1));
			gea.getParameters().set(idx + 1, p);
			this.repaint();
		} else if (e.getActionCommand().startsWith("up")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(2));
			Parameter p = gea.getParameters().get(idx);
			gea.getParameters().set(idx, gea.getParameters().get(idx - 1));
			gea.getParameters().set(idx - 1, p);
			this.repaint();
		} else if (e.getActionCommand().startsWith("edit")) {
			int idx = Integer.parseInt(e.getActionCommand().substring(4));
			Parameter p = gea.getParameters().get(idx);
			this.name.setText(p.getName());
			this.value.setText(p.getValue() + "");
			this.unit.setText(p.getUnit());
			this.add.setText("Override");
			this.editMode = true;
			this.repaint();
		}
	}

	private void repaint() {
		panel.removeAll();
		if (gea instanceof DynamicNode) {
			panel.add(fp, "span 20, wrap");
		}

		panel.add(new JLabel("Name"), "span 1, gaptop 2 ");
		panel.add(name, "span,wrap,growx ,gap 10, gaptop 2");

		panel.add(new JLabel("Value"), "span 1, gapright 4");
		panel.add(value, "span,wrap,growx ,gap 10, gaptop 2");

		panel.add(new JLabel("Unit"), "span 1, gapright 4");
		panel.add(unit, "span,wrap,growx ,gap 10, gaptop 2");

		panel.add(add, "wrap");
		this.listParameters();
		panel.repaint();
		dialog.pack();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		handleChangedName();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		handleChangedName();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	private void handleChangedName() {
		if (editMode) {
			this.editMode = false;
			this.add.setText("add");
		}
	}
}
