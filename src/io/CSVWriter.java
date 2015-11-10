package io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import petriNet.Place;
import petriNet.SimulationResult;
import petriNet.SimulationResultController;
import petriNet.Transition;
import biologicalElements.Pathway;
import biologicalObjects.edges.BiologicalEdgeAbstract;
import biologicalObjects.nodes.BiologicalNodeAbstract;

public class CSVWriter {

	public CSVWriter(OutputStream os, Pathway pw) {

		List<BiologicalNodeAbstract> nodes = pw.getAllGraphNodesSortedAlphabetically();
		List<Place> places = new ArrayList<Place>();
		List<Transition> transitions = new ArrayList<Transition>();
		List<BiologicalEdgeAbstract> edges = new ArrayList<BiologicalEdgeAbstract>();

		try {

			SimulationResult simRes = pw.getSimResController().get();
			if (simRes != null) {
				// BufferedWriter out = new BufferedWriter(new
				// FileWriter(file));

				StringBuffer buff = new StringBuffer();
				Iterator<BiologicalNodeAbstract> it = nodes.iterator();
				BiologicalNodeAbstract bna;
				buff.append("Time;");
				while (it.hasNext()) {
					bna = it.next();
					if (bna instanceof Place && !bna.hasRef()) {
						places.add((Place) bna);
					} else if (bna instanceof Transition && !bna.hasRef()) {
						transitions.add((Transition) bna);
					}
				}
				Iterator<BiologicalEdgeAbstract> it2 = pw.getAllEdgesSorted().iterator();
				BiologicalEdgeAbstract bea;
				while (it2.hasNext()) {
					bea = it2.next();
					edges.add(bea);
				}

				for (int i = 0; i < places.size(); i++) {
					buff.append(places.get(i).getName() + ";");
				}
				for (int i = 0; i < transitions.size(); i++) {
					buff.append(transitions.get(i).getName() + "-fire;");
					buff.append(transitions.get(i).getName() + "-speed;");
				}
				for (int i = 0; i < edges.size(); i++) {
					bea = edges.get(i);
					buff.append((bea.getFrom().getName() + "-" + bea.getTo().getName() + "-token;"));
					buff.append((bea.getFrom().getName() + "-" + bea.getTo().getName() + "-tokenSum;"));
				}
				buff.append("\r\n");

				for (int t = 0; t < simRes.getTime().size(); t++) {
					buff.append(simRes.getTime().get(t) + ";");

					for (int i = 0; i < places.size(); i++) {
						// buff.append(places.get(i).getName() + ";");
						buff.append(simRes.get(places.get(i), SimulationResultController.SIM_TOKEN).get(t) + ";");
					}
					for (int i = 0; i < transitions.size(); i++) {
						buff.append(simRes.get(transitions.get(i), SimulationResultController.SIM_FIRE).get(t) + ";");
						buff.append(simRes.get(transitions.get(i), SimulationResultController.SIM_ACTUAL_FIRING_SPEED).get(t) + ";");
					}
					for (int i = 0; i < edges.size(); i++) {
						buff.append(simRes.get(edges.get(i), SimulationResultController.SIM_ACTUAL_TOKEN_FLOW).get(t) + ";");
						buff.append(simRes.get(edges.get(i), SimulationResultController.SIM_SUM_OF_TOKEN).get(t) + ";");
					}

					buff.append("\r\n");
				}

				os.write(buff.toString().getBytes());

				os.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
