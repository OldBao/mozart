package edu.buaa.composer.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
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
import org.mindswap.owls.process.SplitJoin;
import org.mindswap.owls.process.variable.Binding;
import org.mindswap.owls.process.variable.Input;
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
import edu.buaa.mozart.conditions.MozartCondition;
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
import edu.buaa.mozart.notes.SplitJoinChord;
import edu.buaa.mozart.stub.MozartWebCodeFactory;
import edu.buaa.utils.IDFactory;
import edu.buaa.utils.QuickFactory;

public class Mozart extends Composer {
	private static Logger logger = Logger.getLogger(Mozart.class);
    
	PetriNet mNet;
	Page mPage; // 如果是层次Petri网，Page就不只一个了
	Set<MozartDataConstruct> mPerformSet; // 用来记录所有已经处理的Perform，用来转化过程中，根据Perform的ID查找Perform对象
	Map<ProcessVar, List<MozartDataConstruct>> mThisPerformMap;

	public Mozart() {
		mNet = QuickFactory.getPetriNet();
		mPage = QuickFactory.getPage(mNet, "Mozart Page");
		mPerformSet = new HashSet<MozartDataConstruct>();
		mThisPerformMap = new HashMap<ProcessVar, List<MozartDataConstruct>>();
		mPerformSet.add(OWLS.Process.ThisPerform);
	}

	@Override
	public void composeAtomicClef(AtomicProcess process, AtomicClef clef,
			NotationContext context) {
		try {
            logger.info("转化原子服务: " + process.getLocalName());
			String baseName = context.getConstruct().getLocalName();

			Place inputPlace = QuickFactory.getPlace(mPage, baseName + INPUT);
			Color inputColor = ColorFactory.getInstance().getColorWithControl(
					process.getInputs(), baseName + "_INPUT");
			Sort inputSort = QuickFactory.getSort(mNet,
					inputColor.getTypeName());
			inputPlace.setSort(inputSort);

			Place outputPlace = QuickFactory.getPlace(mPage, baseName + OUTPUT);
			Color outputColor = ColorFactory.getInstance().getColorWithControl(
					process.getOutputs(), baseName + "_OUTPUT");
			Sort outputSort = QuickFactory.getSort(mNet,
					outputColor.getTypeName());
			outputPlace.setSort(outputSort);

			Transition wsTransition = QuickFactory.getTransition(mPage,
					baseName + process.getLocalName());
			// TODO change getProcessCode to CodeSegment
			Code code = MozartWebCodeFactory.getInstance().getProcessCode(
					process, context.getConstruct(), mNet);
			wsTransition.setCode(code);
			// wsTransition.setCondition(QuickFactory.getCondition(mNet,
			// "control=true");

			HLAnnotation inArcAnno = getAnnoFromVars(context.getConstruct(),
					process.getInputs(), true);
			QuickFactory.combine(mPage, inputPlace, wsTransition, inArcAnno);
			HLAnnotation outArcAnno = getAnnoFromVars(context.getConstruct(),
					process.getOutputs(), true);
			QuickFactory.combine(mPage, wsTransition, outputPlace, outArcAnno);

			clef.setInputPlace(inputPlace);
			clef.setOutputPlace(outputPlace);
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void composeCompositeChord(CompositeProcess process,
			CompositeClef chord, NotationContext context) {
		try {
            logger.info("转化组合服务 " + process.getLocalName());
			internalCompose(process, context);
			chord.setInputPlace(context.getPrelude().getDataplace());
			chord.setOutputPlace(context.getConclude().getDataplace());
		} catch (ComposeException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void composeSequenceChrod(Sequence sequenceProcess,
			SequenceChord chord, NotationContext context) {
		try {
            logger.info("转化Sequence " + sequenceProcess.getLocalName());
			OWLIndividualList<ControlConstruct> constructs = sequenceProcess
					.getConstructs();
			ControlConstruct prevCC = null;
			for (ControlConstruct construct : constructs) {
				recursive_compose(construct, context);
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
			chord.setInputTransition(((Chord) constructs.get(0)
					.getMozartNotation()).getInputTransition());
			chord.setOutputTransition(((Chord) constructs.get(
					constructs.size() - 1).getMozartNotation())
					.getOutputTransition());
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void composePerform(Perform perform, PerformChord chord,
			NotationContext context) throws ComposeException {
		try {
			NotationContext tmpContext = new NotationContext();
			tmpContext.setConstruct(perform);

			Process process = perform.getProcess();
			recursive_compose(process, tmpContext);

			Clef clef = (Clef) process.getMozartNotation();
			Transition inTransition, outTransition;
			inTransition = QuickFactory.getTransition(mPage,
					process.getLocalName() + INPUT + BINDING);
			outTransition = QuickFactory.getTransition(mPage,
					process.getLocalName() + OUTPUT + BINDING);
			chord.setInputTransition(inTransition);
			chord.setOutputTransition(outTransition);
			HLAnnotation inArcAnno = getAnnoFromVars(perform,
					process.getInputs(), true);
			HLAnnotation outArcAnno = getAnnoFromVars(perform,
					process.getOutputs(), true);
			QuickFactory.combine(mPage, inTransition, clef.getInputPlace(),
					inArcAnno);
			QuickFactory.combine(mPage, clef.getOutputPlace(), outTransition,
					outArcAnno);

			CodeSegment cs = this.getInitialCode((MozartDataConstruct) perform,
					process.getOutputs());
			chord.setCodeSegment(cs);

	//		context.setConstruct(perform);
			param_binding(perform.getBindings(), perform, context);
		} catch (ComposeException e) {
			e.printStackTrace();
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

			List<ProcessVar> list = OWLFactory.createIndividualList();
			for (OutputBinding binding : produce.getOutputBindings()) {
				ProcessVar var = binding.getProcessVar();
				ParameterValue value = binding.getValue();

				list.add(var);
				addOutputMap((Output) var, (MozartDataConstruct) produce);
			}

			Color produceColor = ColorFactory.getInstance()
					.getColorWithControl(list,
							produce.getLocalName().toUpperCase());
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

			CodeSegment cs = this.getInitialCode(produce, list);
			chord.setCodeSegment(cs);

			param_binding(produce.getAllBindings(),produce, context);
		} catch (ComposeException e) {
			e.printStackTrace();
		}
		mPerformSet.add(produce);
	}

	@Override
	public void composeChoice(Choice choice, ChoiceChord choiceChord,
			NotationContext context) throws ComposeException {

	}

	@Override
	public void composeIfThenElse(IfThenElse ite, IfThenElseChord iteChord,
			NotationContext context) throws ComposeException {
		try {
            logger.info("转化 If-Then-Else " + ite.getLocalName());
			Condition ifCondition = ite.getCondition();
			ControlConstruct thenCC = ite.getThen();
			ControlConstruct elseCC = ite.getElse();
			recursive_compose(thenCC, context);
			recursive_compose(elseCC, context);

			DataChord thenDC = (DataChord) thenCC.getMozartNotation();
			DataChord elseDC = (DataChord) elseCC.getMozartNotation();
			Transition thenTransition = thenDC.getInputTransition();
			Transition elseTransition = elseDC.getInputTransition();

			Transition inputTransition = QuickFactory.getTransition(mPage, ITE
					+ CONTROL);
			Place controlPlace = QuickFactory.getPlace(mPage, ITE);
			Color controlColor = ColorFactory.getInstance().getControlColor();
			controlPlace.setSort(QuickFactory.getSort(mNet,
					controlColor.getTypeName()));

			HLAnnotation controlAnno = getControlAnno();
			QuickFactory.combine(mPage, inputTransition, controlPlace,
					controlAnno);
			controlAnno = getControlAnno();
			QuickFactory.combine(mPage, controlPlace, thenTransition,
					controlAnno);
			controlAnno = getControlAnno();
			QuickFactory.combine(mPage, controlPlace, elseTransition,
					controlAnno);

			Transition conditionTrans = QuickFactory.getTransition(mPage, "");
			condition_binding(ifCondition, conditionTrans, context);
			HLAnnotation anno = getConditionAnno();
			Place thenConditionPlace = QuickFactory.getPlace(mPage, "");
			Place elseConditionPlace = QuickFactory.getPlace(mPage, "");
			Color conditionColor = ColorFactory.getInstance()
					.getConditionColor();
			Sort sort = QuickFactory
					.getSort(mNet, conditionColor.getTypeName());
			thenConditionPlace.setSort(sort);
			elseConditionPlace.setSort(sort);

			QuickFactory.combine(mPage, conditionTrans, thenConditionPlace,
					anno);
			QuickFactory.combine(mPage, conditionTrans, elseConditionPlace,
					anno);
			QuickFactory.combine(mPage, thenConditionPlace, thenTransition,
					anno);
			QuickFactory.combine(mPage, elseConditionPlace, elseTransition,
					anno);

			Var conditionVar = VarFactory.getInstance().getConditionVar();
			thenTransition.setCondition(QuickFactory.getCondition(mNet,
					conditionVar.getVarName()));
			elseTransition.setCondition(QuickFactory.getCondition(mNet, "not "
					+ conditionVar.getVarName()));

			iteChord.setInputTransition(inputTransition);
		} catch (ComposeException e) {
			e.printStackTrace();
		}

	}
    
	@Override
	public void composeSplitJoin(SplitJoin sj, SplitJoinChord sjChord,
			NotationContext context) throws ComposeException {
		try {
            logger.info("转化 Split+Join " + sj.getLocalName());
            
			Transition inputTransition = QuickFactory.getTransition(mPage, ITE
					+ CONTROL);
            Transition outputTransition = QuickFactory.getTransition(mPage, "");
			Color controlColor = ColorFactory.getInstance().getControlColor();
            HLAnnotation controlAnno = getControlAnno();
            
            for (ControlConstruct CC : sj.getConstructs()){
                Place splitControlPlace = QuickFactory.getPlace(mPage, ITE);
                Place joinControlPlace = QuickFactory.getPlace(mPage, ITE);
                
                splitControlPlace.setSort(QuickFactory.getSort(mNet,
					controlColor.getTypeName()));
                joinControlPlace.setSort(QuickFactory.getSort(mNet,
					controlColor.getTypeName()));
                
            	DataChord chord = (DataChord) CC.getMozartNotation();
            	recursive_compose(CC,context);
            	Transition inTransition = chord.getInputTransition();
                Transition outTransition = chord.getOutputTransition();
                QuickFactory.combine(mPage, inputTransition, splitControlPlace, controlAnno);
                QuickFactory.combine(mPage, splitControlPlace, inTransition,
                					controlAnno);
                QuickFactory.combine(mPage, outTransition, joinControlPlace, controlAnno);
                QuickFactory.combine(mPage, joinControlPlace, outputTransition,controlAnno);
            }
            sjChord.setInputTransition(inputTransition);
            sjChord.setOutputTransition(outputTransition);
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void composePrelude(Process process, Prelude prelude,
			NotationContext context) {
		try {
			Place gStartPlace = QuickFactory.getPlace(mPage, G_START);
			Color color = ColorFactory.getInstance()
					.getColorWithControl(process.getInputs(),
							process.getLocalName() + "_" + G_START);
			Sort sort = QuickFactory.getSort(mNet, color.getTypeName());
			gStartPlace.setSort(sort);

			Transition outputTransition = QuickFactory.getTransition(mPage,
					G_START + " " + BINDING);
			Tuple inputTuple = getTupleStringFromVars(context.getConstruct(),
					process.getInputs(), false);
			CodeSegment code = MozartWebCodeFactory.getInstance().getInitCode(
					inputTuple);
			prelude.setCodeSegment(code);

			HLAnnotation inArcAnno = getAnnoFromVars(context.getConstruct(),
					process.getInputs(), true);
			QuickFactory.combine(mPage, gStartPlace, outputTransition,
					inArcAnno);

			prelude.setOutputTransition(outputTransition);
			prelude.setDataPlace(gStartPlace);

			for (Input input : process.getInputs()) {
				addOutputMap(input, OWLS.Process.ThisPerform);
			}
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void composeConclude(Process process, Conclude conclude,
			NotationContext context) throws ComposeException {
		Place gFinishPlace = QuickFactory.getPlace(mPage, G_FINISH);
		Color color = ColorFactory.getInstance().getColorWithControl(
				process.getOutputs(), process.getLocalName()+"_"+G_FINISH);
		gFinishPlace.setSort(QuickFactory.getSort(mNet, color.getTypeName()));
		conclude.setDataPlace(gFinishPlace);
		if (process instanceof CompositeProcess) {
			List<Output> outputs = process.getOutputs();
			for (Output output : outputs) {
				for (MozartDataConstruct dfcc : mThisPerformMap.get(output)) {
					DataChord chord = (DataChord) dfcc.getMozartNotation();
					if (chord == null)
						throw new ComposeException("output" + output
								+ " none exists in context");
					else {
						Transition trans = chord.getOutputTransition();

						HLAnnotation transAnno = getAnnoFromVar(
								context.getConstruct(), output, true);

						Var fromVar = VarFactory.getInstance()
								.getVarFromProcessVar(dfcc, output);
						Var toVar = VarFactory.getInstance()
								.getVarFromProcessVar(context.getConstruct(),
										output);
						CodeSegment fromcs = chord.getCodeSegment();
						fromcs.addInput(fromVar.getVarName());
						fromcs.addTransferMap(fromVar.getVarName(),
								toVar.getVarName());
						QuickFactory.combine(mPage, trans, gFinishPlace,
								transAnno);
						//TODO: this line may cause multiple close
						conclude.setInputTransition(trans);
					}
				}
			}
		}else {
			 Transition inputTransition = QuickFactory.getTransition(mPage,
			 G_FINISH + " " + BINDING);
			
			 HLAnnotation inArcAnno = getAnnoFromVars(context.getConstruct(),
			 process.getOutputs(), true);
			 QuickFactory.combine(mPage, inputTransition, gFinishPlace,
			 inArcAnno);
			
			 conclude.setInputTransition(inputTransition);			
		}
        
		CodeSegment cs = new CodeSegment();
		conclude.setCodeSegment(cs);
		
	}

	// @Override
	// public void composeConclude(Process process, Conclude conclude,
	// NotationContext context) throws ComposeException {
	// try {
	// Place gFinishPlace = QuickFactory.getPlace(mPage, G_FINISH);
	// Color color = ColorFactory.getInstance().getColorWithoutControl(
	// process.getOutputs(), G_FINISH);
	// Sort sort = QuickFactory.getSort(mNet, color.getTypeName());
	// gFinishPlace.setSort(sort);
	//
	// Transition inputTransition = QuickFactory.getTransition(mPage,
	// G_FINISH + " " + BINDING);
	// CodeSegment cs = MozartWebCodeFactory.getInstance().getExitCode();
	// conclude.setCodeSegment(cs);
	//
	// HLAnnotation inArcAnno = getAnnoFromVars(context.getConstruct(),
	// process.getOutputs(), false);
	// QuickFactory.combine(mPage, inputTransition, gFinishPlace,
	// inArcAnno);
	//
	// conclude.setInputTransition(inputTransition);
	// conclude.setDataPlace(gFinishPlace);
	//
	// if (process instanceof CompositeProcess) {
	// List<Output> outputs = process.getOutputs();
	// for (Output output : outputs) {
	// for (MozartDataConstruct dfcc : mThisPerformMap.get(output)) {
	// DataChord chord = (DataChord) dfcc.getMozartNotation();
	// if (chord == null)
	// throw new ComposeException("output" + output
	// + " none exists in context");
	// else {
	// Transition trans = chord.getOutputTransition();
	// Place bindingPlace = QuickFactory.getPlace(mPage,
	// PRODUCE + PARAM_BINDING);
	// Color bindingColor = ColorFactory.getInstance()
	// .getBasicColor(output);
	// bindingPlace.setSort(QuickFactory.getSort(mNet,
	// bindingColor.getTypeName()));
	//
	// HLAnnotation transAnno = getAnnoFromVar(
	// context.getConstruct(), output, false);
	// QuickFactory.combine(mPage, trans, bindingPlace,
	// transAnno);
	// QuickFactory.combine(mPage, bindingPlace,
	// inputTransition, transAnno);
	//
	// Var fromVar = VarFactory.getInstance()
	// .getVarFromProcessVar(dfcc, output);
	// Var toVar = VarFactory.getInstance()
	// .getVarFromProcessVar(
	// context.getConstruct(), output);
	// CodeSegment fromcs = chord.getCodeSegment();
	// fromcs.addInput(fromVar.getVarName());
	// fromcs.addTransferMap(fromVar.getVarName(),
	// toVar.getVarName());
	// }
	// }
	// }
	// }
	//
	// } catch (ComposeException e) {
	// e.printStackTrace();
	// }
	// }

	@Override
	public PetriNet Compose(Process process) throws ComposeException {
        logger.info("Mozart => CPN Start...");
		NotationContext context = new NotationContext();
		context.setConstruct(OWLS.Process.ThisPerform);
		internalCompose(process, context);

		context.getPrelude()
				.getCodeSegment()
				.addAction(
						"openConnection(\"" + ComposerConfig.CONN_NAME + "\","
								+ "\"" + ComposerConfig.SERVER_ADDR + "\","
								+ ComposerConfig.WS_STUB_PORT + ")");
		context.getConclude()
				.getCodeSegment()
				.addAction(
						"closeConnection(\"" + ComposerConfig.CONN_NAME + "\")");
		QuickFactory.addCode(mNet, context.getPrelude().getOutputTransition(),
				context.getPrelude().getCodeSegment());
//		QuickFactory.addCode(mNet, context.getConclude().getInputTransition(),
//				context.getConclude().getCodeSegment());
		addAllDecl();
        logger.info("Mozart => CPN End...");
		return mNet;
	}

	private void internalCompose(Process process, NotationContext context)
			throws ComposeException {
		context.getPrelude().setIndividual(process);
		context.getConclude().setIndividual(process);

		// compose non owl-S architecture
		context.getPrelude().compose(this, context);

		if (process instanceof AtomicProcess) {
			recursive_compose(process, context);
			context.getConclude().compose(this, context);
			// single process compose
			AtomicClef clef = (AtomicClef) process.getMozartNotation();

			Transition trans = context.getPrelude().getOutputTransition();
			HLAnnotation inArcAnno = getAnnoFromVars(context.getConstruct(),
					process.getInputs(), true);
			QuickFactory.combine(mPage, trans, clef.getInputPlace(), inArcAnno);

			trans = context.getConclude().getInputTransition();
			HLAnnotation outArcAnno = getAnnoFromVars(context.getConstruct(),
					process.getOutputs(), true);
			QuickFactory.combine(mPage, clef.getOutputPlace(), trans,
					outArcAnno);
		} else {
			MozartDataConstruct tmp = context.getConstruct();
			ControlConstruct cc = ((CompositeProcess) process).getComposedOf();
			recursive_compose(cc, context);
			context.setConstruct(tmp);
			addControlBetween(context.getPrelude(),
					(Chord) cc.getMozartNotation());
			context.getConclude().compose(this, context);
		}
		QuickFactory.addCode(mNet, context.getPrelude().getOutputTransition(),
				context.getPrelude().getCodeSegment());
//		QuickFactory.addCode(mNet, context.getConclude().getInputTransition(),
//				context.getConclude().getCodeSegment());
	}

	// 必须最后调用, 因为模型转化中间过程可能生成新的color
	private void addAllDecl() {
		addBooleanED();
		ColorFactory.getInstance().addAllColorToNet(mNet);
		VarFactory.getInstance().addAllVarToNet(mNet);
		addCodes();
	}

	private void addControlBetween(Chord from, Chord to)
			throws ComposeException {
		Place controlPlace = QuickFactory.getPlace(mPage, "");
		Color color = ColorFactory.getInstance().getControlColor();
		Sort sort = QuickFactory.getSort(mNet, color.getTypeName());
		controlPlace.setSort(sort);
		HLAnnotation anno = getControlAnno();
		QuickFactory.combine(mPage, from.getOutputTransition(), controlPlace,
				anno);
		anno = getControlAnno();
		QuickFactory
				.combine(mPage, controlPlace, to.getInputTransition(), anno);
	}

	private void addBooleanED() {
		MLDeclaration mlDecl = DeclarationFactory.INSTANCE
				.createMLDeclaration();
		mlDecl.setCode("use \"" + ComposerConfig.BOOL_DECL_PATH + "\";");

		HLDeclaration hlyDecl = ModelFactory.INSTANCE.createHLDeclaration();
		hlyDecl.setStructure(mlDecl);
		hlyDecl.setId(IDFactory.getInstance().getRandomId());
		hlyDecl.setParent(mNet);

		mlDecl = DeclarationFactory.INSTANCE.createMLDeclaration();
		mlDecl.setCode("use \"" + ComposerConfig.SWRL_DECL_PATH + "\";");
		hlyDecl = ModelFactory.INSTANCE.createHLDeclaration();
		hlyDecl.setStructure(mlDecl);
		hlyDecl.setId(IDFactory.getInstance().getRandomId());
		hlyDecl.setParent(mNet);
	}

	private void addCodes() {
		try {
			for (MozartDataConstruct perform : mPerformSet) {
				if (perform.equals(OWLS.Process.ThisPerform))
					continue;
				DataChord dc = (DataChord) perform.getMozartNotation();
				QuickFactory.addCode(mNet, dc.getOutputTransition(),
						dc.getCodeSegment());
			}
		} catch (ComposeException e) {
			e.printStackTrace();
		}
	}

	private void recursive_compose(OWLIndividual individual,
			NotationContext context) throws ComposeException {
		if (individual == null)
			return;
		Notation notation = individual.getMozartNotation();

		// NotationContext context = new NotationContext();

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

	private HLAnnotation getConditionAnno() {
		HLAnnotation anno = ModelFactory.INSTANCE.createHLAnnotation();
		String annotxt = "("
				+ VarFactory.getInstance().getConditionVar().getVarName() + ")";
		anno.setText(annotxt);
		anno.setParent(mNet);
		return anno;
	}

	private <P extends ProcessVar> HLAnnotation getAnnoFromVar(
			MozartDataConstruct dfcc, P var, boolean hasControl)
			throws ComposeException {
		List<P> list = OWLFactory.createIndividualList();
		list.add(var);
		return getAnnoFromVars(dfcc, list, hasControl);
	}

	// 从变量中获取HL表达式
	// 注意：control约定在最后一个，也就是说必须为(...,control)的形式
	private <P extends ProcessVar> HLAnnotation getAnnoFromVars(
			MozartDataConstruct dfcc, List<P> vars, boolean hasControl)
			throws ComposeException {
		HLAnnotation anno = ModelFactory.INSTANCE.createHLAnnotation();

		anno.setText(getTupleStringFromVars(dfcc, vars, hasControl).toString());
		anno.setParent(mNet);

		return anno;
	}

	private <T extends Binding> void param_binding(List<T> bindings,
			MozartDataConstruct targetConstruct, NotationContext context) throws ComposeException {
		DataChord target = (DataChord) targetConstruct.getMozartNotation();
		for (T binding : bindings) {
			ParameterValue value = binding.getValue();
			ProcessVar var = binding.getProcessVar();
			Perform sourcePerform = value.getPerformsFromResults(mPerformSet);
			ProcessVar fromVar = null;
			if (value instanceof ValueOf) {
				fromVar = ((ValueOf) value).getTheVar();
			}

			DataChord dc;
            MozartDataConstruct sourceConstruct = sourcePerform; 
			if (sourcePerform.equals(OWLS.Process.ThisPerform)) {
                sourceConstruct = context.getConstruct();
				dc = context.getPrelude();
			} else {
				dc = (DataChord) sourcePerform.getMozartNotation();
			}
			CodeSegment cs = dc.getCodeSegment();
			Var localvar = VarFactory.getInstance().getVarFromProcessVar(
					targetConstruct, var);
			Var localfromvar = VarFactory.getInstance().getVarFromProcessVar(
					sourceConstruct, fromVar);
			cs.addTransferMap(localfromvar.toString(), localvar.toString());

			Place paramBindingPlace = QuickFactory.getPlace(mPage,
					PARAM_BINDING);
			Color color = ColorFactory.getInstance().getBasicColor(var);
			paramBindingPlace.setSort(QuickFactory.getSort(mNet,
					color.getTypeName()));
			Transition outBindingTrans = dc.getOutputTransition();
			Transition inBindingTrans = target.getInputTransition();

			HLAnnotation inArcAnno = getAnnoFromVar(targetConstruct,
					var, false);
			QuickFactory.combine(mPage, outBindingTrans, paramBindingPlace,
					inArcAnno);
			HLAnnotation outArcAnno = getAnnoFromVar(targetConstruct,
					var, false);
			QuickFactory.combine(mPage, paramBindingPlace, inBindingTrans,
					outArcAnno);
			if (binding instanceof OutputBinding)
				addOutputMap(var, targetConstruct);
		}
	}

	private <P extends ProcessVar> CodeSegment getInitialCode(
			MozartDataConstruct perform, List<P> vars) throws ComposeException {
		CodeSegment cs = new CodeSegment();
		for (P var : vars) {
			String varName = VarFactory.getInstance()
					.getVarFromProcessVar(perform, var).getVarName();
			cs.addInput(varName);
		}
		return cs;

	}

	private <V extends ProcessVar> Tuple getTupleStringFromVars(
			MozartDataConstruct dfcc, List<V> vars, boolean hasControl)
			throws ComposeException {
		Tuple tuple = new Tuple();
		for (V v : vars) {
			Var pVar = VarFactory.getInstance().getVarFromProcessVar(dfcc, v);
			tuple.addVar(pVar.toString());
		}
		if (hasControl) {
			tuple.addVar(VarFactory.getInstance().getControlVar().getVarName());
		}

		return tuple;
	}

	private void addOutputMap(ProcessVar var, MozartDataConstruct chord) {
		if (!mThisPerformMap.containsKey(var)) {
			mThisPerformMap.put(var, new ArrayList<MozartDataConstruct>());
		}
		for (MozartDataConstruct s : mThisPerformMap.get(var)) {
			if (s.equals(chord))
				return;
		}
		mThisPerformMap.get(var).add(chord);
	}

	private void condition_binding(Condition originCondition,
			Transition conditionTransition, NotationContext context)
			throws ComposeException {
		Place conditionPlace = QuickFactory.getPlace(mPage, "");
		Transition inTransition = QuickFactory.getTransition(mPage, "");

		MozartCondition condition = ConditionConverter.getMozartCondition(
				originCondition, mThisPerformMap.keySet());

		List<ProcessVar> depVars = condition.getDependVars(mThisPerformMap
				.keySet());
		Color conditionColor = ColorFactory
				.getInstance()
				.getColorWithoutControl(depVars, originCondition.getLocalName());
		Sort sort = QuickFactory.getSort(mNet, conditionColor.getTypeName());
		conditionPlace.setSort(sort);
		List<Var> conditionVars = new ArrayList<Var>();
		CodeSegment cs = new CodeSegment();
        
        //处理绑定，这块不能直接调用param binding，未来也是改进点
		for (ProcessVar var : condition.getDependVars(mThisPerformMap.keySet())) {
			List<MozartDataConstruct> sources = mThisPerformMap.get(var);
			MozartDataConstruct mdc = sources.get(0);
            
            //参数绑定
			Place parambindingPlace = QuickFactory.getPlace(mPage, "");
			Color paramColor = ColorFactory.getInstance().getBasicColor(var);
			Sort paramSort = QuickFactory.getSort(mNet,
					paramColor.getTypeName());
			parambindingPlace.setSort(paramSort);
            
            MozartDataConstruct fromConstruct = mdc;
            DataChord chord = (DataChord) mdc.getMozartNotation();
            if (mdc.equals(OWLS.Process.ThisPerform)){
                chord = context.getPrelude();
                fromConstruct = context.getConstruct();
            }
   			Var localvar = VarFactory.getInstance()
   					.getVarFromProcessVar(fromConstruct, var);
    		conditionVars.add(localvar);
    		cs.addInput(localvar.getVarName());
    		HLAnnotation arcAnno = 
    				getAnnoFromVar(fromConstruct, var, false);
         
    		QuickFactory.combine(mPage,chord.getOutputTransition(), 
    				parambindingPlace, arcAnno);
			QuickFactory.combine(mPage, parambindingPlace, inTransition,
					arcAnno);
		}
        
        //设置参数绑定内部的参数传递
		Tuple tuple = new Tuple();
		for (Var v : conditionVars) {
			tuple.addVar(v.getVarName());
		}
		HLAnnotation inAnno = ModelFactory.INSTANCE.createHLAnnotation();
		inAnno.setText(tuple.toString());
		inAnno.setParent(mNet);
		QuickFactory
				.combine(mPage, conditionPlace, conditionTransition, inAnno);
		QuickFactory.combine(mPage, inTransition, conditionPlace, inAnno);
		Var conditionVar = VarFactory.getInstance().getConditionVar();
		cs.addTransferAction(conditionVar.getVarName(),
				condition.translateToML(conditionVars));
		QuickFactory.addCode(mNet, conditionTransition, cs);
	}

	private static String G_START = "Mozart_Start";
	private static String BINDING = "_BINDING";
	private static String INPUT = "_INPUT";
	private static String OUTPUT = "_OUTPUT";
	private static String G_FINISH = "Mozart_Finish";
	private static String PARAM_BINDING = "_PARAM_BINDING";
	private static String CONTROL = "CONTROL";
	private static String ITE = "If-Then-Else";
	private static String PRODUCE = "PRODUCE";



}