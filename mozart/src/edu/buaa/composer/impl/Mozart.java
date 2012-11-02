package edu.buaa.composer.impl;

import java.util.HashSet;
import java.util.Set;

import org.cpntools.accesscpn.model.Node;
import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Transition;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;
import edu.buaa.mozart.notes.AtomicClef;
import edu.buaa.mozart.notes.ComposeException;
import edu.buaa.mozart.notes.ConcludeNotation;
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
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Produce;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.variable.Binding;
import org.mindswap.owls.process.variable.OutputBinding;
import org.mindswap.owls.process.variable.ParameterValue;
import org.mindswap.owls.process.variable.ProcessVar;
import org.mindswap.owls.vocabulary.OWLS;

public class Mozart extends Composer implements NotationDependence.Visitor{
	private Page page;
	private PetriNet net;
    private HashSet<Perform> mPerformSet;
	private PreludeNotation gPrelude;
	private ConcludeNotation gConclude;
    private NotationDependence mDep;
    
	public Mozart(){
        mPerformSet = new HashSet<Perform>();
        mDep 			   = new NotationDependence();
	}
    
	@Override
	public PetriNet Compose(Process process) throws ComposeException {
		net 		      = QuickFactory.getPetriNet();
		page        = QuickFactory.getPage(net, process.getLocalName());
	
        mPerformSet .add(OWLS.Process.ThisPerform);
        gPrelude =    new PreludeNotation(QuickFactory.getPlace(page, "Start"));
        gConclude = new ConcludeNotation(QuickFactory.getPlace(page, "Finish"));
        gPrelude.setIndividual(process);
        gConclude.setIndividual(process);
        mDep.addNotation(gPrelude);
        mDep.addNotation(gConclude);
        
        simple_compose(process);
        
        gConclude.compose(this, null);
        
        mDep.setVisitor(this);
        mDep.Visit(gPrelude);
        
		//TODO tuple for all inputs and outputs
		return net;
	}

	@Override
	public void composeAtomicClef(AtomicProcess process, AtomicClef clef, NotationContext context) {
		try {
		Transition wsTransition = 
				QuickFactory.getTransition(page, process.getLocalName());
		
		Place atomicInputPlace =
				QuickFactory.getPlace(page, "Input");
		Place atomicOutputPlace =
				QuickFactory.getPlace(page, "Output");
		
		QuickFactory.combine(page, atomicInputPlace, wsTransition);
		QuickFactory.combine(page, wsTransition, atomicOutputPlace);
		QuickFactory.combine(page, clef.getStartTransition(), atomicInputPlace);
		QuickFactory.combine(page, atomicOutputPlace, clef.getEndTransition());
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void composeSequenceChrod(Sequence sequenceProcess,
			SequenceChord chrod, NotationContext context) {
		OWLIndividualList<ControlConstruct> constructs = sequenceProcess.getConstructs();
		
		for (ControlConstruct construct : constructs) {
                try {
					simple_compose(construct);
				} catch (ComposeException e) {
					e.printStackTrace();
				}
		}
	}
    
	@Override
	public void composePerform(Perform perform,  PerformChord chord, NotationContext context)  {
		try {
            Process targetProcess = perform.getProcess();
			simple_compose(targetProcess);
            
            for (Binding<?> binding : perform.getAllBindings()){
                ParameterValue value   = binding.getValue();
                Perform  sourcePerform = value.getPerformsFromResults(mPerformSet);
                
                if (sourcePerform.equals(OWLS.Process.ThisPerform)){
                    addDep(binding, gPrelude, targetProcess.getMozartNotation());
                } else {
                    addDep(binding, sourcePerform.getProcess(true).getMozartNotation(), 
                    		targetProcess.getMozartNotation());
                }
             }//for
		} catch (ComposeException e) {
			e.printStackTrace();
		}
        mPerformSet.add(perform);
	}

	@Override
	public void visit_dep(Notation srcNotation, Notation dstNotation,
			Set<ProcessVar> vars) {
        try {
			if (srcNotation instanceof PreludeNotation || dstNotation instanceof ConcludeNotation) {
				Node srcNode, dstNode;
				if (srcNotation instanceof PreludeNotation) 
					srcNode =  ((PreludeNotation) srcNotation).getPlace();
				else
					srcNode = srcNotation.getEndTransition();
                if (dstNotation instanceof ConcludeNotation)
                	dstNode = ((ConcludeNotation) dstNotation).getPlace();
                else
                	dstNode = dstNotation.getStartTransition();
                
				QuickFactory.combine(page, srcNode, dstNode);
			}else {
				String varString = "(";
				for (ProcessVar var : vars) varString += var.getLocalName() + ",";
                varString +=")";
				Place place = QuickFactory.getPlace(page, "Var binding" + varString); 
			    QuickFactory.combine(page, srcNotation.getEndTransition(), place);
			    QuickFactory.combine(page, place, dstNotation.getStartTransition());
			}
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void composeConclude(OWLIndividualList<Result> results,
			NotationContext context) throws ComposeException {
        for (Result result : results) {
        	OWLIndividualList<OutputBinding> bindings  = result.getBindings();
            for (OutputBinding binding : bindings){ 
               Perform sourcePerform = binding.getValue().getPerformsFromResults(mPerformSet);
               
               if (sourcePerform.equals(OWLS.Process.ThisPerform)){
            	   addDep(binding, gPrelude, gConclude);
               } else {
            	   addDep(binding, sourcePerform.getProcess(true).getMozartNotation(), gConclude);
               }
            }
        }
	}

	@Override
	public void composeProduce(Produce produce, ProduceChord chrod,
			NotationContext context) {
			OWLIndividualList<Binding<?>> bindings = produce.getAllBindings();
            
            for (Binding binding : bindings) {
            	
            } 
	}
    
	private void dealWithBindings(OWLIndividualList<Binding<?>> bindings){
		
	}
	
    private void simple_compose(OWLIndividual individual) throws ComposeException{
        Notation notation = individual.getMozartNotation();
        notation.setCPNPage(page);
        
        NotationContext context = new NotationContext();
        notation.compose(this, context);
        mDep.addNotation(notation);
    } 
    
    private void addDep(Binding<?> b, Notation src, Notation dst) {
    	GraphArc arc = mDep.addDependency(src, dst);
        
    	ProcessVar var = b.getProcessVar();
        arc.addVar(var);
    }
}
