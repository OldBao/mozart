package edu.buaa.composer.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cpntools.accesscpn.model.Arc;
import org.cpntools.accesscpn.model.Node;
import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Sort;
import org.cpntools.accesscpn.model.Transition;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

import edu.buaa.mozart.notes.AtomicClef;
import edu.buaa.mozart.notes.ChoiceChord;
import edu.buaa.mozart.notes.ComposeException;
import edu.buaa.mozart.notes.CompositeClef;
import edu.buaa.mozart.notes.ConcludeNotation;
import edu.buaa.mozart.notes.DataChord;
import edu.buaa.mozart.notes.IfThenElseChord;
import edu.buaa.mozart.notes.Notation;
import edu.buaa.mozart.notes.PreludeNotation;
import edu.buaa.mozart.notes.ProduceChord;
import edu.buaa.mozart.notes.SequenceChord;
import edu.buaa.utils.QuickFactory;
import edu.buaa.mozart.notes.PerformChord;
import edu.buaa.mozart.notes.dependencies.NotationDependence;
import edu.buaa.mozart.notes.dependencies.NotationDependence.GraphArc;

import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.expression.Condition;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Choice;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.IfThenElse;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Produce;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.variable.Binding;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.LinkBinding;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.process.variable.OutputBinding;
import org.mindswap.owls.process.variable.ParameterValue;
import org.mindswap.owls.process.variable.ProcessVar;
import org.mindswap.owls.vocabulary.OWLS;

//public class CopyOfMozart extends Composer implements NotationDependence.Visitor {
public class CopyOfMozart{
//	private Page page; // global page
//	private PetriNet net; // global net
//	private HashSet<Perform> mPerformSet;
//	private HashMap<Output, Notation> mOutputMap;
//	private PreludeNotation gPrelude;
//	private ConcludeNotation gConclude;
//	private NotationDependence mVarDep;
//	private NotationDependence mControlDep;
//
//	public CopyOfMozart() {
//		mControlDep = new NotationDependence();
//		mPerformSet = new HashSet<Perform>();
//		mVarDep = new NotationDependence();
//		mOutputMap = new HashMap<Output, Notation>();
//	}
//
//	@Override
//	public PetriNet Compose(Process process) throws ComposeException {
//		net = QuickFactory.getPetriNet();
//		page = QuickFactory.getPage(net, process.getLocalName());
//
//		mPerformSet.add(OWLS.Process.ThisPerform);
//		gPrelude = new PreludeNotation(QuickFactory.getPlace(page, "Start"));
//		gConclude = new ConcludeNotation(QuickFactory.getPlace(page, "Finish"));
//		gPrelude.setIndividual(process);
//		gConclude.setIndividual(process);
//		mVarDep.addNotation(gPrelude);
//		mVarDep.addNotation(gConclude);
//		mControlDep.addNotation(gConclude);
//		mControlDep.addNotation(gPrelude);
//
//		simple_compose(process);
//		addControlDep(gPrelude, process.getMozartNotation());
//		addControlDep(process.getMozartNotation(), gConclude);
//
//		gConclude.compose(this, null);
//
//		mVarDep.setVisitor(this);
//		mControlDep.setVisitor(this);
//
//		mVarDep.Visit(gPrelude);
//		mControlDep.Visit(gPrelude);
//
//		if (process instanceof AtomicProcess) {
//			// if process is single process ,it has no binding, so just combine
//			// conclde prelude to this process
//			OWLIndividualList<Input> inputs = process.getInputs();
//			OWLIndividualList<Output> outputs = process.getOutputs();
//
//			// TODO now just combine
//		}
//		return net;
//	}
//
//	@Override
//	public void composeAtomicClef(AtomicProcess process, AtomicClef clef,
//			NotationContext context) {
//		try {
//			Transition wsTransition = QuickFactory.getTransition(page,
//					process.getLocalName());
//
//			Place atomicInputPlace = QuickFactory.getPlace(page,
//					process.getLocalName() + "Input");
//			Place atomicOutputPlace = QuickFactory.getPlace(page,
//					process.getLocalName() + "Output");
//
//			QuickFactory.combine(page, atomicInputPlace, wsTransition);
//			QuickFactory.combine(page, wsTransition, atomicOutputPlace);
//			
//		} catch (ComposeException e) {
//			e.printStackTrace();
//		}
//
//		context.addOutputs(process.getOutputs());
//	}
//
//
//	@Override
//	public void composeSequenceChrod(Sequence sequenceProcess,
//			SequenceChord chrod, NotationContext context) {
//		OWLIndividualList<ControlConstruct> constructs = sequenceProcess
//				.getConstructs();
//
//		ControlConstruct prevCC = null;
//		for (ControlConstruct construct : constructs) {
//			try {
//				simple_compose(construct);
//				if (prevCC != null) {
//					addControlDep(prevCC.getMozartNotation(),
//							construct.getMozartNotation());
//				}
//				prevCC = construct;
//			} catch (ComposeException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	@Override
//	public void composeChoice(Choice choice, ChoiceChord choiceChord,
//			NotationContext context) throws ComposeException {
//		throw new ComposeException("what the fuck are choice doing?");
//	}
//
//	@Override
//	public void composeIfThenElse(IfThenElse ite, IfThenElseChord iteChord,
//			NotationContext context) {
//		Condition condition = ite.getCondition();
//		ControlConstruct thenCC = ite.getThen();
//		ControlConstruct elseCC = ite.getElse();
//
//		try {
//			// TODO CC is NULL? oh~~no
//			Place conditionPlace = QuickFactory.getPlace(page, "Condition");
//			Place mergePlace = QuickFactory.getPlace(page, "ConditionEnd");
//			simple_compose(thenCC);
//			Notation thenNotation = thenCC.getMozartNotation();
//			simple_compose(elseCC);
//			Notation elseNotation = elseCC.getMozartNotation();
//
//			// TODO set condition
//
//		} catch (ComposeException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void composePerform(Perform perform, PerformChord chord,
//			NotationContext context) {
//		try {
//			Process targetProcess = perform.getProcess(true);
//			simple_compose(targetProcess);
//			dealWithBindings(perform.getAllBindings(),
//					targetProcess.getMozartNotation());
//		} catch (ComposeException e) {
//			e.printStackTrace();
//		}
//		mPerformSet.add(perform);
//	}
//
//	@Override
//	public void composeConclude(OWLIndividualList<Result> results,
//			OWLIndividualList<Output> outputs, NotationContext context)
//			throws ComposeException {
//		for (Result result : results) {
//			dealWithBindings(result.getBindings(), gConclude);
//		}
//		// dealWithOutputs(outputs, gConclude);
//	}
//
//	// produce is not a single point, it's should insert between web service
//	// place and output binding
//	@Override
//	public void composeProduce(Produce produce, ProduceChord produceChrod,
//			NotationContext context) throws ComposeException {
//		Transition produceTrans = QuickFactory.getTransition(page,
//				"Produce trans");
//		Place producePlace = QuickFactory
//				.getPlace(page, produce.getLocalName());
//
//		OWLIndividualList<OutputBinding> outputBindings = produce
//				.getOutputBindings();
//		OWLIndividualList<LinkBinding> linkBindings = produce.getLinkBindings();
//
//		if (linkBindings.size() != 0) {
//			throw new ComposeException("link binding is not support now");
//		}
//
//		for (OutputBinding outputBinding : outputBindings) {
//			ProcessVar var = outputBinding.getProcessVar();
//			ParameterValue value = outputBinding.getValue();
//			Perform perform = value.getPerformsFromResults(mPerformSet);
//			if (perform == null)
//				throw new ComposeException("not found perform for " + var
//						+ "in produce" + produce);
//			else {
//				// refactor process output binding
//				Notation alterNotation;
//				if (perform.equals(OWLS.Process.ThisPerform)) {
//					Transition innerTrans = QuickFactory.getTransition(page,
//							"Produce binding");
//					QuickFactory.combine(page, gPrelude.getPlace(), innerTrans);
//					QuickFactory.combine(page, innerTrans, producePlace);
//					QuickFactory.combine(page, producePlace, produceTrans);
//				} else {
//					alterNotation = perform.getProcess(true)
//							.getMozartNotation();
//
//
//					// combine oldtrans to produce place
//					QuickFactory.combine(page, producePlace, produceTrans);
//
//					// alter new end trans for notation
//				}
//
//				context.addOutput((Output) var);
//			}
//		}
//	}
//
//    
//	@Override
//	public void composeCompositeChord(CompositeProcess process,
//			CompositeClef chord, NotationContext context) {
//
//	}
//    
//	@Override
//	public void visit_dep(Notation srcNotation, Notation dstNotation,
//			Set<ProcessVar> vars) {
//		System.out.println("Visit dep " + srcNotation + " ==> " + dstNotation);
//		if (srcNotation instanceof PreludeNotation
//				|| dstNotation instanceof ConcludeNotation) {
//			Node srcNode = null, dstNode = null;
//			if (srcNotation instanceof PreludeNotation)
//				srcNode = ((PreludeNotation) srcNotation).getPlace();
//			else
//		        ;
//			if (dstNotation instanceof ConcludeNotation)
//				dstNode = ((ConcludeNotation) dstNotation).getPlace();
//			else
//				;
//			QuickFactory.combine(page, srcNode, dstNode);
//		} else {
//			String varString = "(";
//			for (ProcessVar var : vars)
//				varString += var.getLocalName() + ",";
//			varString += ")";
//			Place place = QuickFactory.getPlace(page, "Var binding"
//					+ varString);
//		}
//	}
//
//	private void dealWithOutputs(OWLIndividualList<Output> outputs,
//			Notation targetNotation) throws ComposeException {
//		for (Output output : outputs) {
//			Notation sourceNotation = mOutputMap.get(output);
//			if (sourceNotation == null) {
//				throw new ComposeException(
//						"a reference output can't found in map");
//			}
//			if (targetNotation instanceof ConcludeNotation)
//                ;
//			else {
//				Place p = QuickFactory.getPlace(page, "output binding");
//			}
//		}
//	}
//
//	private <T extends Binding> void dealWithBindings(
//			OWLIndividualList<T> bindings, Notation targetNotation)
//			throws ComposeException {
//		for (Binding binding : bindings) {
//			ParameterValue value = binding.getValue();
//			Perform sourcePerform = value.getPerformsFromResults(mPerformSet);
//
//			if (sourcePerform.equals(OWLS.Process.ThisPerform)) {
//				addDep(binding, gPrelude, targetNotation);
//			} else {
//				addDep(binding, sourcePerform.getProcess(true)
//						.getMozartNotation(), targetNotation);
//			}
//		}
//	}
//
//	private void simple_compose(OWLIndividual individual)
//			throws ComposeException {
//		if (individual == null)
//			return;
//		Notation notation = individual.getMozartNotation();
//
//		NotationContext context = new NotationContext();
//		notation.compose(this, context);
//		for (Output output : context.getOutputs()) {
//			this.mOutputMap.put(output, notation);
//		}
//
//		mVarDep.addNotation(notation);
//		// mControlDep.addNotation(notation);
//	}
//
//	// control dep
//	private void addControlDep(Notation src, Notation dst) {
//		mControlDep.addDependency(src, dst);
//	}
//
//	// value dep
//	private void addDep(Binding<?> b, Notation src, Notation dst) {
//		GraphArc arc = mVarDep.addDependency(src, dst);
//
//		ProcessVar var = b.getProcessVar();
//		arc.addVar(var);
//	}
}
