package edu.buaa.composer.impl;

import java.util.HashSet;
import java.util.List;
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
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Choice;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.IfThenElse;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Produce;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.variable.Binding;
import org.mindswap.owls.process.variable.ParameterValue;
import org.mindswap.owls.process.variable.ProcessVar;
import org.mindswap.owls.vocabulary.OWLS;

import edu.buaa.composer.Composer;
import edu.buaa.composer.ComposerConfig;
import edu.buaa.composer.NotationContext;
import edu.buaa.mozart.color.Color;
import edu.buaa.mozart.color.ColorFactory;
import edu.buaa.mozart.color.Var;
import edu.buaa.mozart.color.VarFactory;
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

public class Mozart extends Composer {

	PetriNet mNet;
	Page mPage; // 如果是层次Petri网，Page就不只一个了
	Set<Perform> mPerformSet; // 用来记录所有已经处理的Perform，用来转化过程中，根据Perform的ID查找Perform对象
	Prelude gPrelude;
	Conclude gConclude;

	public Mozart() {
		mNet = QuickFactory.getPetriNet();
		mPage = QuickFactory.getPage(mNet, "Mozart Page");
		mPerformSet = new HashSet<Perform>();
		gPrelude = new Prelude();
		gConclude = new Conclude();
        
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
			Code code = MozartWebCodeFactory.getInstance().getProcessCode(
					process, mNet);
			wsTransition.setCode(code);
			// wsTransition.setCondition(QuickFactory.getCondition(mNet,
			// "control=true");

			HLAnnotation inArcAnno = getAnnoFromVars(process.getInputs(), true);
			QuickFactory.combine(mPage, inputPlace, wsTransition, inArcAnno);
			HLAnnotation outArcAnno = getAnnoFromVars(process.getOutputs(),
					true);
			QuickFactory.combine(mPage, wsTransition, outputPlace, outArcAnno);

			clef.setInputPlace(inputPlace);
			clef.setOutputPlace(outputPlace);
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void composeSequenceChrod(Sequence sequenceProcess,
			SequenceChord chrod, NotationContext context) {
		try {
			OWLIndividualList<ControlConstruct> constructs = sequenceProcess
					.getConstructs();

			ControlConstruct prevCC = null;
			for (ControlConstruct construct : constructs) {
				recursive_compose(construct);
				if (prevCC != null) {
					Chord prevChord = (Chord) prevCC.getMozartNotation();
					Chord curChord = (Chord) construct.getMozartNotation();
					Place controlPlace = QuickFactory
							.getPlace(mPage, "CONTROL");
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
	public void composeCompositeChord(CompositeProcess process,
			CompositeClef chord, NotationContext context) {

	}

	@Override
	public void composePerform(Perform perform, PerformChord chord,
			NotationContext context) {
		try {
			Process process = perform.getProcess();
			recursive_compose(process);
            
			Clef clef = (Clef) process.getMozartNotation();
			Transition inTransition, outTransition;
			inTransition = QuickFactory.getTransition(mPage,
					process.getLocalName() + INPUT + BINDING);
			outTransition = QuickFactory.getTransition(mPage,
					process.getLocalName() + OUTPUT + BINDING);
			chord.setInputTransition(inTransition);
			chord.setOutputTransition(outTransition);
			HLAnnotation inArcAnno = getAnnoFromVars(process.getInputs(), true);
			HLAnnotation outArcAnno = getAnnoFromVars(process.getOutputs(),
					true);
			QuickFactory.combine(mPage, inTransition, clef.getInputPlace(),
					inArcAnno);
			QuickFactory.combine(mPage, clef.getOutputPlace(), outTransition,
					outArcAnno);


			param_binding(perform.getAllBindings(), chord);
		} catch (ComposeException e) {
			e.printStackTrace();
		}
		mPerformSet.add(perform);
	}

    //TODO produce is function normally, I can't handle that now
	@Override
	public void composeProduce(Produce produce, ProduceChord chord,
			NotationContext context) throws ComposeException {
        chord.setInputTransition(gConclude.getInputTransition());
//		try {
//            Place producePlace = QuickFactory.getPlace(mPage, PRODUCE);
//			Transition produceOutBindingTrans = QuickFactory.getTransition(mPage, PRODUCE + BINDING);
//            
//            for (Binding binding : produce.getAllBindings()){
//            	ParameterValue value = binding.getValue();
//            	ProcessVar var = binding.getProcessVar();
//            	Perform sourcePerform = value.getPerformsFromResults(mPerformSet);
//
//            	DataChord dc;
//            	if (sourcePerform.equals(OWLS.Process.ThisPerform)) {
//            		dc = gPrelude;
//            	} else {
//            		dc = (DataChord) sourcePerform.getMozartNotation();
//            	}
//
//            	Transition outBindingTrans = dc.getOutputTransition();
//
//            	HLAnnotation inArcAnno = getAnnoFromVar(var, false);
//            	QuickFactory.combine(mPage, outBindingTrans, producePlace,
//            			inArcAnno);
//            	HLAnnotation outArcAnno = getAnnoFromVar(var, false);
//            	QuickFactory.combine(mPage, producePlace, produceOutBindingTrans,
//            			outArcAnno);
//    	}
//        chord.setOutputTransition(produceOutBindingTrans);
//
//		} catch (ComposeException e){
//			e.printStackTrace();
//		}
	}

	@Override
	public void composeChoice(Choice choice, ChoiceChord choiceChord,
			NotationContext context) throws ComposeException {

	}

	@Override
	public void composeIfThenElse(IfThenElse ite, IfThenElseChord iteChord,
			NotationContext context) throws ComposeException {

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
			Code code = MozartWebCodeFactory.getInstance().getInitCode(mNet);
			outputTransition.setCode(code);

			HLAnnotation inArcAnno = getAnnoFromVars(process.getInputs(), true);
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
			Color color = ColorFactory.getInstance().getColorWithControl(
					process.getOutputs(), G_FINISH);
			Sort sort = QuickFactory.getSort(mNet, color.getTypeName());
			gFinishPlace.setSort(sort);

			Transition inputTransition = QuickFactory.getTransition(mPage,
					G_FINISH + " " + BINDING);
			Code code = MozartWebCodeFactory.getInstance().getExitCode(mNet);
			inputTransition.setCode(code);

			HLAnnotation inArcAnno = getAnnoFromVars(process.getOutputs(), true);
			QuickFactory.combine(mPage, inputTransition, gFinishPlace,
					inArcAnno);

			conclude.setInputTransition(inputTransition);
			conclude.setDataPlace(gFinishPlace);
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public PetriNet Compose(Process process) throws ComposeException {
		NotationContext context = new NotationContext();

		gPrelude.setIndividual(process);
		gConclude.setIndividual(process);

		// compose non owl-S architecture
		gPrelude.compose(this, context);
		gConclude.compose(this, context);

		recursive_compose(process);
		if (process instanceof AtomicProcess) {
			// single process compose
			AtomicClef clef = (AtomicClef) process.getMozartNotation();

			Transition trans = gPrelude.getOutputTransition();
			HLAnnotation inArcAnno = getAnnoFromVars(process.getInputs(), true);
			QuickFactory.combine(mPage, trans, clef.getInputPlace(), inArcAnno);

			trans = gConclude.getInputTransition();
			HLAnnotation outArcAnno = getAnnoFromVars(process.getOutputs(),
					true);
			QuickFactory.combine(mPage, clef.getOutputPlace(), trans,
					outArcAnno);
		}

		addAllDecl();
		return mNet;
	}

	// 必须最后调用, 因为模型转化中间过程可能生成新的color
	private void addAllDecl() {
		addBooleanED();
		ColorFactory.getInstance().addAllColorToNet(mNet);
		VarFactory.getInstance().addAllVarToNet(mNet);
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

	private void addBooleanED_raw() {
		String booleanDecl = "fun boolEncode (to_send) = \n"
				+ "Byte.stringToBytes(Bool.toString(to_send))\n"
				+ "fun boolDecode (received) = \n"
				+ "case Bool.fromString(Byte.bytesToString(received)) of\n"
				+ "SOME received =>\n"
				+ "(  print(Bool.toString(received));\n"
				+ "received\n"
				+ ")\n"
				+ "| NONE =>\n"
				+ "raise InvalidDataExn(\"Failure receiving data: Boolean expected\");\n";
	}

	private void recursive_compose(OWLIndividual individual)
			throws ComposeException {
		if (individual == null)
			return;
		Notation notation = individual.getMozartNotation();

		NotationContext context = new NotationContext();
		notation.compose(this, context);
	}

	private HLAnnotation getControlAnno() {
		HLAnnotation anno = ModelFactory.INSTANCE.createHLAnnotation();
		String annotxt = "("
				+ VarFactory.getInstance().getControlVar().getVarName() + ")";
		anno.setText(annotxt);
		anno.setParent(mNet);
		return anno;
	}

	private <P extends ProcessVar> HLAnnotation getAnnoFromVar(P var,
			boolean hasControl) throws ComposeException {
		List<P> list = OWLFactory.createIndividualList();
		list.add(var);
		return getAnnoFromVars(list, hasControl);
	}

	// 从变量中获取HL表达式
	// 注意：control约定在最后一个，也就是说必须为(...,control)的形式
	private <P extends ProcessVar> HLAnnotation getAnnoFromVars(List<P> vars,
			boolean hasControl) throws ComposeException {
		HLAnnotation anno = ModelFactory.INSTANCE.createHLAnnotation();
		StringBuilder annoBuilder = new StringBuilder();
		annoBuilder.append("(");

		for (P v : vars) {
			Var pVar = VarFactory.getInstance().getVarFromProcessVar(v);
			annoBuilder.append(pVar.getVarName());
			annoBuilder.append(",");
		}
		if (hasControl) {
			annoBuilder.append(VarFactory.getInstance().getControlVar()
					.getVarName());
			annoBuilder.append(")");
		} else {
			annoBuilder.replace(annoBuilder.length() - 1, annoBuilder.length(),
					")");
		}
		anno.setText(annoBuilder.toString());
		anno.setParent(mNet);

		return anno;
	}

	private <T extends Binding> void param_binding(
			OWLIndividualList<T> bindings, DataChord target)
			throws ComposeException {
		for (Binding binding : bindings) {
			ParameterValue value = binding.getValue();
			ProcessVar var = binding.getProcessVar();
			Perform sourcePerform = value.getPerformsFromResults(mPerformSet);

			DataChord dc;
			if (sourcePerform.equals(OWLS.Process.ThisPerform)) {
				dc = gPrelude;
			} else {
				dc = (DataChord) sourcePerform.getMozartNotation();
			}

			Place paramBindingPlace = QuickFactory.getPlace(mPage,
					var.getLocalName() + PARAM_BINDING);
			Transition outBindingTrans = dc.getOutputTransition();
			Transition inBindingTrans = target.getInputTransition();

			HLAnnotation inArcAnno = getAnnoFromVar(var, false);
			QuickFactory.combine(mPage, outBindingTrans, paramBindingPlace,
					inArcAnno);
			HLAnnotation outArcAnno = getAnnoFromVar(var, false);
			QuickFactory.combine(mPage, paramBindingPlace, inBindingTrans,
					outArcAnno);
		}
	}

	private static String G_START = "Mozart_Start";
	private static String BINDING = "_BINDING";
	private static String INPUT = "_INPUT";
	private static String OUTPUT = "_OUTPUT";
	private static String G_FINISH = "Mozart_Finish";
	private static String PARAM_BINDING = "_PARAM_BINDING";
	private static String CONTROL = "CONTROL";
    private static String PRODUCE = "PRODUCE";

}