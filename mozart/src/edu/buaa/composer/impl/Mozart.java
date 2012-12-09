package edu.buaa.composer.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cpntools.accesscpn.model.Code;
import org.cpntools.accesscpn.model.HLAnnotation;
import org.cpntools.accesscpn.model.HLDeclaration;
import org.cpntools.accesscpn.model.ModelFactory;
import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Sort;
import org.cpntools.accesscpn.model.Transition;
import org.cpntools.accesscpn.model.declaration.DeclarationFactory;
import org.cpntools.accesscpn.model.declaration.MLDeclaration;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.expression.Condition;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Choice;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.IfThenElse;
import org.mindswap.owls.process.MozartDataConstruct;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Produce;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.variable.Binding;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.process.variable.OutputBinding;
import org.mindswap.owls.process.variable.ParameterValue;
import org.mindswap.owls.process.variable.ProcessVar;
import org.mindswap.owls.process.variable.ValueOf;
import org.mindswap.owls.vocabulary.OWLS;

import edu.buaa.composer.Composer;
import edu.buaa.composer.ComposerConfig;
import edu.buaa.composer.NotationContext;
import edu.buaa.mozart.ML.CodeSegment;
import edu.buaa.mozart.ML.Tuple;
import edu.buaa.mozart.color.Color;
import edu.buaa.mozart.color.ColorFactory;
import edu.buaa.mozart.color.Var;
import edu.buaa.mozart.color.VarFactory;
import edu.buaa.mozart.conditions.ConditionConverter;
import edu.buaa.mozart.notes.AtomicClef;
import edu.buaa.mozart.notes.ChoiceChord;
import edu.buaa.mozart.notes.Chord;
import edu.buaa.mozart.notes.Clef;
import edu.buaa.mozart.notes.ComposeException;
import edu.buaa.mozart.notes.CompositeClef;
import edu.buaa.mozart.notes.Conclude;
import edu.buaa.mozart.notes.DataChord;
import edu.buaa.mozart.notes.IfThenElseChord;
import edu.buaa.mozart.notes.Notation;
import edu.buaa.mozart.notes.PerformChord;
import edu.buaa.mozart.notes.Prelude;
import edu.buaa.mozart.notes.ProduceChord;
import edu.buaa.mozart.notes.SequenceChord;
import edu.buaa.mozart.stub.MozartWebCodeFactory;
import edu.buaa.utils.IDFactory;
import edu.buaa.utils.QuickFactory;

public class Mozart extends Composer{

	PetriNet mNet;
	Page mPage; // 如果是层次Petri网，Page就不只一个了
	Set<MozartDataConstruct> mPerformSet; // 用来记录所有已经处理的Perform，用来转化过程中，根据Perform的ID查找Perform对象
	Prelude gPrelude;
	Conclude gConclude;
	Map<Output, MozartDataConstruct> mOutputs;

	public Mozart() {
		mNet = QuickFactory.getPetriNet();
		mPage = QuickFactory.getPage(mNet, "Mozart Page");
		mPerformSet = new HashSet<MozartDataConstruct>();
		gPrelude = new Prelude();
		gConclude = new Conclude();
		mOutputs = new HashMap<Output, MozartDataConstruct>();

		mPerformSet.add(OWLS.Process.ThisPerform);
	}

	@Override
	public void composeAtomicClef(AtomicProcess process, AtomicClef clef,
			NotationContext context) {
		try {
			String clefName = process.getLocalName();

			Place inputPlace = QuickFactory.getPlace(mPage, clefName + INPUT);
			Color inputColor = ColorFactory.getInstance().getColorWithControl(
					process.getInputs(), clefName.toUpperCase() + "_INPUT");
			Sort inputSort = QuickFactory.getSort(mNet,
					inputColor.getTypeName());
			inputPlace.setSort(inputSort);

			Place outputPlace = QuickFactory.getPlace(mPage, clefName + OUTPUT);
			Color outputColor = ColorFactory.getInstance().getColorWithControl(
					process.getOutputs(), clefName.toUpperCase() + "_OUTPUT");
			Sort outputSort = QuickFactory.getSort(mNet,
					outputColor.getTypeName());
			outputPlace.setSort(outputSort);

			Transition wsTransition = QuickFactory.getTransition(mPage,
					clefName);
            //TODO change getProcessCode to CodeSegment
			Code code = MozartWebCodeFactory.getInstance().getProcessCode(
					process, context.getConsturct(),mNet);
			wsTransition.setCode(code);
			// wsTransition.setCondition(QuickFactory.getCondition(mNet,
			// "control=true");

			HLAnnotation inArcAnno = getAnnoFromVars(context.getConsturct(),process.getInputs(), true);
			QuickFactory.combine(mPage, inputPlace, wsTransition, inArcAnno);
			HLAnnotation outArcAnno = getAnnoFromVars(context.getConsturct(),process.getOutputs(),
					true);
			QuickFactory.combine(mPage, wsTransition, outputPlace, outArcAnno);

			clef.setInputPlace(inputPlace);
			clef.setOutputPlace(outputPlace);
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void composeCompositeChord(CompositeProcess process,
			CompositeClef chord, NotationContext context)  
	{
		try {
			String clefName = process.getLocalName();

			Place inputPlace = QuickFactory.getPlace(mPage, clefName + INPUT);
			Color inputColor = ColorFactory.getInstance().getColorWithControl(
					process.getInputs(), clefName.toUpperCase() + "_INPUT");
			Sort inputSort = QuickFactory.getSort(mNet,
					inputColor.getTypeName());
			inputPlace.setSort(inputSort);

			Place outputPlace = QuickFactory.getPlace(mPage, clefName + OUTPUT);
			Color outputColor = ColorFactory.getInstance().getColorWithControl(
					process.getOutputs(), clefName.toUpperCase() + "_OUTPUT");
			Sort outputSort = QuickFactory.getSort(mNet,
					outputColor.getTypeName());
			outputPlace.setSort(outputSort);
            
            recursive_compose(process.getComposedOf(), context);
			chord.setInputPlace(inputPlace);
			chord.setOutputPlace(outputPlace);
		} catch (ComposeException e) {
			e.printStackTrace();
		}
        
	}            
            
    private static int sControlIndex = 1;
	@Override
	public void composeSequenceChrod(Sequence sequenceProcess,
			SequenceChord chrod, NotationContext context) {
		try {
			OWLIndividualList<ControlConstruct> constructs = sequenceProcess
					.getConstructs();

			ControlConstruct prevCC = null;
			for (ControlConstruct construct : constructs) {
				recursive_compose(construct, context);
				if (prevCC != null) {
					Chord prevChord = (Chord) prevCC.getMozartNotation();
					Chord curChord = (Chord) construct.getMozartNotation();
					Place controlPlace = QuickFactory.getPlace(mPage, "CONTROL"
						+ sControlIndex++);
					Color color = ColorFactory.getInstance().getControlColor();
					Sort sort = QuickFactory.getSort(mNet, color.getTypeName());
					controlPlace.setSort(sort);

					HLAnnotation anno = getControlAnno();
					QuickFactory
							.combine(mPage, prevChord.getOutputTransition(),
									controlPlace, anno);
					anno = getControlAnno();
					QuickFactory.combine(mPage, controlPlace,
							curChord.getInputTransition(), anno);
				}
				prevCC = construct;
			}
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void composePerform(Perform perform, PerformChord chord,
			NotationContext context) throws ComposeException {
		try {
			Process process = perform.getProcess();
			recursive_compose(process, context);

			Clef clef = (Clef) process.getMozartNotation();
			Transition inTransition, outTransition;
			inTransition = QuickFactory.getTransition(mPage,
					process.getLocalName() + INPUT + BINDING);
			outTransition = QuickFactory.getTransition(mPage,
					process.getLocalName() + OUTPUT + BINDING);
			chord.setInputTransition(inTransition);
			chord.setOutputTransition(outTransition);
			HLAnnotation inArcAnno = getAnnoFromVars(perform, process.getInputs(), true);
			HLAnnotation outArcAnno = getAnnoFromVars(perform, process.getOutputs(),
					true);
			QuickFactory.combine(mPage, inTransition, clef.getInputPlace(),
					inArcAnno);
			QuickFactory.combine(mPage, clef.getOutputPlace(), outTransition,
					outArcAnno);

            CodeSegment cs = this.getInitialCode((MozartDataConstruct)perform, process.getOutputs());
            chord.setCodeSegment(cs);
            
			param_binding(perform.getAllBindings(), perform);
		} catch (ComposeException e) {
            throw e;
		}
		mPerformSet.add(perform);
	}

	// TODO produce is function normally, I can't handle that now
	@Override
	public void composeProduce(Produce produce, ProduceChord chord,
			NotationContext context) throws ComposeException {
		try {
			Place producePlace = QuickFactory.getPlace(mPage, PRODUCE);
			Transition produceInBindingTrans = QuickFactory.getTransition(
					mPage, PRODUCE + INPUT + BINDING);
			Transition produceOutBindingTrans = QuickFactory.getTransition(
					mPage, PRODUCE + OUTPUT + BINDING);

			OWLIndividualList<ProcessVar> list = OWLFactory
					.createIndividualList();
			for (OutputBinding binding : produce.getOutputBindings()) {
				ProcessVar var = binding.getProcessVar();
				list.add(var);
				context.addOutput((Output) var);
			}
           // produceInBindingTrans.setCode(getInitialCode(list));
            
			Color produceColor = ColorFactory.getInstance()
					.getColorWithControl(list, produce.getLocalName().toUpperCase());
			Sort sort = QuickFactory.getSort(mNet, produceColor.getTypeName());
			producePlace.setSort(sort);
			HLAnnotation prduceArcAnno = getAnnoFromVars(produce, list, true);
			QuickFactory.combine(mPage, produceInBindingTrans, producePlace,
					prduceArcAnno);
			prduceArcAnno = getAnnoFromVars(produce, list, true);
			QuickFactory.combine(mPage, producePlace, produceOutBindingTrans,
					prduceArcAnno);
			chord.setInputTransition(produceInBindingTrans);
			chord.setOutputTransition(produceOutBindingTrans);
            
			param_binding(produce.getAllBindings(), (MozartDataConstruct)produce);
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void composeChoice(Choice choice, ChoiceChord choiceChord,
			NotationContext context) throws ComposeException {

	}

	@Override
	public void composeIfThenElse(IfThenElse ite, IfThenElseChord iteChord,
			NotationContext context) throws ComposeException {
        try {
			Condition condition = ite.getCondition();
			ControlConstruct thenCC = ite.getThen();
			ControlConstruct elseCC = ite.getElse();
			recursive_compose(thenCC, context);
			recursive_compose(elseCC, context);
			String conditionString = ConditionConverter.getInstance().convert(condition);
            
            DataChord thenDC = (DataChord) thenCC.getMozartNotation();
            DataChord elseDC = (DataChord) thenCC.getMozartNotation();
            Transition thenTransition = thenDC.getInputTransition();
            Transition elseTransition = elseDC.getInputTransition();
            
            Transition inputTransition = QuickFactory.getTransition(mPage, ITE + CONTROL);
            Place controlPlace = QuickFactory.getPlace(mPage, ITE);
            Color controlColor = ColorFactory.getInstance().getControlColor();
            controlPlace.setSort(QuickFactory.getSort(mNet, controlColor.getTypeName()));
            
            HLAnnotation controlAnno = getControlAnno();
            QuickFactory.combine(mPage, inputTransition, controlPlace, controlAnno);
            controlAnno = getControlAnno();
            QuickFactory.combine(mPage, controlPlace, thenTransition, controlAnno);
            controlAnno = getControlAnno();
            QuickFactory.combine(mPage, controlPlace, elseTransition, controlAnno);
            
            org.cpntools.accesscpn.model.Condition thenCondition = QuickFactory.getCondition(mNet, conditionString);
            thenTransition.setCondition(thenCondition);
            org.cpntools.accesscpn.model.Condition elseCondition = QuickFactory.getCondition(mNet, "not " + conditionString);
            elseTransition.setCondition(elseCondition);
            
            iteChord.setInputTransition(inputTransition);
		} catch (ComposeException e) {
			e.printStackTrace();
		}
        
	}

	@Override
	public void composePrelude(Process process, Prelude prelude,
			NotationContext context) {
		try {
			Place gStartPlace = QuickFactory.getPlace(mPage, G_START);
			Color color = ColorFactory.getInstance().getColorWithControl(
					process.getInputs(), G_START);
			Sort sort = QuickFactory.getSort(mNet, color.getTypeName());
			gStartPlace.setSort(sort);

            
			Transition outputTransition = QuickFactory.getTransition(mPage,
					G_START + " " + BINDING);
			Tuple inputTuple = getTupleStringFromVars(context.getConsturct(),process.getInputs(),
					false);
			CodeSegment code = MozartWebCodeFactory.getInstance().getInitCode(inputTuple);
            prelude.setCodeSegment(code);

			HLAnnotation inArcAnno = getAnnoFromVars(context.getConsturct(),process.getInputs(), true);
			QuickFactory.combine(mPage, gStartPlace, outputTransition,
					inArcAnno);

			prelude.setOutputTransition(outputTransition);
			prelude.setDataPlace(gStartPlace);
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void composeConclude(Process process, Conclude conclude,
			NotationContext context) throws ComposeException {
		try {
			Place gFinishPlace = QuickFactory.getPlace(mPage, G_FINISH);
			Color color = ColorFactory.getInstance().getColorWithoutControl(
					process.getOutputs(), G_FINISH);
			Sort sort = QuickFactory.getSort(mNet, color.getTypeName());
			gFinishPlace.setSort(sort);

			Transition inputTransition = QuickFactory.getTransition(mPage,
					G_FINISH + " " + BINDING);
			CodeSegment cs = MozartWebCodeFactory.getInstance().getExitCode();
            conclude.setCodeSegment(cs);

			HLAnnotation inArcAnno = getAnnoFromVars(context.getConsturct(),process.getOutputs(),
					false);
			QuickFactory.combine(mPage, inputTransition, gFinishPlace,
					inArcAnno);

			conclude.setInputTransition(inputTransition);
			conclude.setDataPlace(gFinishPlace);

            if (process instanceof CompositeProcess){
            	List<Output> outputs = process.getOutputs();
            	for (Output output : outputs) {
            		MozartDataConstruct dfcc = mOutputs.get(output);
                    DataChord chord = (DataChord) dfcc.getMozartNotation();
            		if (chord == null)
            			throw new ComposeException("output" + output
							+ " none exists in context");
            		else {
            			Transition trans = chord.getOutputTransition();
            			Place bindingPlace = QuickFactory.getPlace(mPage, PRODUCE
							+ PARAM_BINDING);
            			Color bindingColor = ColorFactory.getInstance()
							.getBasicColor(output);
            			bindingPlace.setSort(QuickFactory.getSort(mNet,
							bindingColor.getTypeName()));

            			HLAnnotation transAnno = getAnnoFromVar(dfcc, output, false);
            			QuickFactory.combine(mPage, trans, bindingPlace, transAnno);
            			transAnno = getAnnoFromVar(dfcc, output, false);
            			QuickFactory.combine(mPage, bindingPlace, inputTransition,
							transAnno);
                        
                        Var fromVar = VarFactory.getInstance().getVarFromProcessVar(dfcc, output);
                        Var toVar   = VarFactory.getInstance().getVarFromProcessVar(OWLS.Process.ThisPerform,output);
                        cs.addInput(fromVar.getVarName());
                        cs.addTransferMap(fromVar.getVarName(), toVar.getVarName());
            		}
            	}
			}

		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}
    
	@Override
	public PetriNet Compose(Process process) throws ComposeException {
		NotationContext context = new NotationContext();
        context.setConsturct(OWLS.Process.ThisPerform);

		gPrelude.setIndividual(process);
		gConclude.setIndividual(process);

		// compose non owl-S architecture
		gPrelude.compose(this, context);

		if (process instanceof AtomicProcess) {
			recursive_compose(process, context);
            gConclude.compose(this, context);
			// single process compose
			AtomicClef clef = (AtomicClef) process.getMozartNotation();

			Transition trans = gPrelude.getOutputTransition();
			HLAnnotation inArcAnno = getAnnoFromVars(context.getConsturct(),process.getInputs(), true);
			QuickFactory.combine(mPage, trans, clef.getInputPlace(), inArcAnno);

			trans = gConclude.getInputTransition();
			HLAnnotation outArcAnno = getAnnoFromVars(context.getConsturct(),process.getOutputs(),
					true);
			QuickFactory.combine(mPage, clef.getOutputPlace(), trans,
					outArcAnno);
		} else {
            recursive_compose(((CompositeProcess)process).getComposedOf(), context);
			gConclude.compose(this, context);
		}
		addAllDecl();
		return mNet;
	}

	// 必须最后调用, 因为模型转化中间过程可能生成新的color
	private void addAllDecl() {
		addBooleanED();
		ColorFactory.getInstance().addAllColorToNet(mNet);
		VarFactory.getInstance().addAllVarToNet(mNet);
        addCodes();
	}

	private void addBooleanED() {
		MLDeclaration mlDecl = DeclarationFactory.INSTANCE
				.createMLDeclaration();
		mlDecl.setCode("use \"" + ComposerConfig.BOOL_DECL_PATH + "\";");

		HLDeclaration hlyDecl = ModelFactory.INSTANCE.createHLDeclaration();
		hlyDecl.setStructure(mlDecl);
		hlyDecl.setId(IDFactory.getInstance().getRandomId());
		hlyDecl.setParent(mNet);
	}

    private void addCodes(){
        try {
			QuickFactory.addCode(mNet, gPrelude.getOutputTransition(), gPrelude.getCodeSegment());
			QuickFactory.addCode(mNet, gConclude.getInputTransition(), gConclude.getCodeSegment());
			
			for (MozartDataConstruct perform : mPerformSet){
                if (perform.equals(OWLS.Process.ThisPerform))
                	continue;
			    DataChord dc = (DataChord) perform.getMozartNotation();
				QuickFactory.addCode(mNet, dc.getOutputTransition(), dc.getCodeSegment());
			}
		} catch (ComposeException e) {
			e.printStackTrace();
		}
        
    }
	

	private void recursive_compose(OWLIndividual individual, NotationContext context)
			throws ComposeException {
		if (individual == null)
			return;
		Notation notation = individual.getMozartNotation();

	//	NotationContext context = new NotationContext();
        
		notation.compose(this, context);

		for (Output output : context.getOutputs()) {
			mOutputs.put(output, context.getConsturct());
		}
	}

	private HLAnnotation getControlAnno() {
		HLAnnotation anno = ModelFactory.INSTANCE.createHLAnnotation();
		String annotxt = "("
				+ VarFactory.getInstance().getControlVar().getVarName() + ")";
		anno.setText(annotxt);
		anno.setParent(mNet);
		return anno;
	}

	private <P extends ProcessVar> HLAnnotation getAnnoFromVar(
			MozartDataConstruct dfcc,
			P var,
			boolean hasControl) throws ComposeException {
		List<P> list = OWLFactory.createIndividualList();
		list.add(var);
		return getAnnoFromVars(dfcc, list, hasControl);
	}

	// 从变量中获取HL表达式
	// 注意：control约定在最后一个，也就是说必须为(...,control)的形式
	private <P extends ProcessVar> HLAnnotation getAnnoFromVars(MozartDataConstruct dfcc,List<P> vars,
			boolean hasControl) throws ComposeException {
		HLAnnotation anno = ModelFactory.INSTANCE.createHLAnnotation();

		anno.setText(getTupleStringFromVars(dfcc, vars, hasControl).toString());
		anno.setParent(mNet);

		return anno;
	}

	private <T extends Binding> void param_binding(
			OWLIndividualList<T> bindings, MozartDataConstruct dfcc)
			throws ComposeException{
		DataChord target = (DataChord) dfcc.getMozartNotation();
		for (Binding binding : bindings) {
			ParameterValue value = binding.getValue();
			ProcessVar var = binding.getProcessVar();
			Perform sourcePerform = value.getPerformsFromResults(mPerformSet);
			ProcessVar fromVar = null;
			if (value instanceof ValueOf) {
				fromVar = ((ValueOf) value).getTheVar();
			}

			DataChord dc;
			if (sourcePerform.equals(OWLS.Process.ThisPerform)) {
				dc = gPrelude;
			} else {
				dc = (DataChord) sourcePerform.getMozartNotation();
			}
            CodeSegment cs = dc.getCodeSegment();
            Var localvar = VarFactory.getInstance().getVarFromProcessVar(dfcc,var);
            Var localfromvar  = VarFactory.getInstance().getVarFromProcessVar(sourcePerform, fromVar);
            cs.addTransferMap(localfromvar.toString(), localvar.toString());
            
        	Place paramBindingPlace = QuickFactory.getPlace(mPage, PARAM_BINDING);
        	Color color = ColorFactory.getInstance().getBasicColor(var);
        	paramBindingPlace.setSort(QuickFactory.getSort(mNet,
				color.getTypeName()));
        	Transition outBindingTrans 	= dc.getOutputTransition();
        	Transition inBindingTrans 	= target.getInputTransition();

        	HLAnnotation inArcAnno = getAnnoFromVar(dfcc, var, false);
        	QuickFactory.combine(mPage, outBindingTrans, paramBindingPlace,
				inArcAnno);
        	HLAnnotation outArcAnno = getAnnoFromVar(dfcc, var, false);
        	QuickFactory.combine(mPage, paramBindingPlace, inBindingTrans,
				outArcAnno);
		}
	}


    private <P extends ProcessVar> CodeSegment
    getInitialCode(MozartDataConstruct perform, List<P> vars) throws ComposeException
    {
    	CodeSegment cs = new CodeSegment();
        for(P var : vars) {
        	String varName = 
            		VarFactory.getInstance().getVarFromProcessVar(perform, var).getVarName();
        	cs.addInput(varName);
        }
        return cs;
        
    }

	private <V extends ProcessVar> 
	Tuple getTupleStringFromVars(MozartDataConstruct dfcc, List<V> vars,
			boolean hasControl) throws ComposeException {
        Tuple tuple = new Tuple();
		for (V v : vars) {
			Var pVar = VarFactory.getInstance().getVarFromProcessVar(dfcc, v);
            tuple.addVar(pVar.toString());
		}
		if (hasControl) {
			tuple.addVar(VarFactory.getInstance().getControlVar()
					.getVarName());
		}
        
        return tuple;
	}

	private static String G_START = "Mozart_Start";
	private static String BINDING = "_BINDING";
	private static String INPUT = "_INPUT";
	private static String OUTPUT = "_OUTPUT";
	private static String G_FINISH = "Mozart_Finish";
	private static String PARAM_BINDING = "_PARAM_BINDING";
	private static String CONTROL = "CONTROL";
    private static String ITE		= "If-Then-Else";
	private static String PRODUCE = "PRODUCE";



}