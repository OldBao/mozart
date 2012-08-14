package org.buaa.cpn;

import java.io.File;
import java.net.URL;

import org.cpntools.accesscpn.engine.highlevel.HighLevelSimulator;
import org.cpntools.accesscpn.engine.highlevel.checker.Checker;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.importer.DOMParser;
import org.buaa.cpn.StatespaceTool;


public class Test {
	public static void main(final String[] args) throws Exception {
		final String fileName = "/home/jian/kunjian/oa_net.cpn";// args[0];
		final String outFilename = "/home/jian/kunjian/cpnoutput.txt";
		final File outFile = new File(outFilename);
		final PetriNet petriNet = DOMParser.parse(new URL("file://" + fileName));
		final HighLevelSimulator s = HighLevelSimulator.getHighLevelSimulator();
		try {
			final Checker checker = new Checker(petriNet, outFile, s);
			checker.checkEntireModel();
			StatespaceTool st = new StatespaceTool();
			st.enterStatespace(s);
			st.calculateStatespace(s);
			st.calculateSccGraph(s);
			System.out.println(s.
					evaluate("use \"../statespacefiles/export/OGtoGraphviz.sml\";OGtoGraphviz.ExportStateSpace(\"/home/jian/oa.dot\")"));
			System.out.println("==================Dead Markings===================");
			System.out.println(s.
					evaluate("let fun ListDeadMarkings () = PredAllNodes (fn n => (OutArcs n) = [] ) in ListDeadMarkings() end"));
//			s.evaluate("use \"/home/jian/kunjian/simple-dfs.sml\"");

//			System.out.println(s
//	                .evaluate("OGSet.StopOptions{Nodes=0,Arcs=0,Secs=300,Predicate=fn _=>false};" +
//	                		"OGSet.BranchingOptions{TransInsts=0,Bindings=0,Predicate=fn _=>true};" +
//	                		"CalculateOccGraph()"));
//			System.out.println(s
//	                .evaluate("Reachable(1,4);"));

//			System.out.println(s.
//					evaluate("NodeDescriptor 4;"));
//			System.out
//			        .println(s
//			                .evaluate("let val (state, storage) = dfs dead (CPNToolsModel.getInitialStates()) in (state, HashTable.numItems storage) end"));
//			System.out
//	        .println(s
//	                .evaluate(" val (h::t) = CPNToolsModel.getInitialStates(); val (state, event) = h;CPNToolsModel.stateToString(state);"));
		} finally {
			s.destroy();
		}
	}
}