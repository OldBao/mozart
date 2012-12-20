package edu.buaa.mozart.UI.core;

import org.apache.log4j.Logger;
import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.execution.DefaultProcessMonitor;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.query.ValueMap;

public class ExecutionMonitor extends DefaultProcessMonitor {
	private static Logger logger = Logger.getLogger(ExecutionMonitor.class);

	public ExecutionMonitor() {
		super();
	}

	private void print(final String msg) {
		logger.info(msg);
	}

	private void println(final String msg) {
		logger.info(msg);
	}

	@Override
	public void executionStarted() {
        print("服务开始执行");
	}

	@Override
	public void executionFinished() {
		print("服务执行成功");
	}

	@Override
	public void executionStarted(final Process process,
			final ValueMap<Input, OWLValue> inputs) {
        print("开始执行Process " + process.getLocalName());
		println("输入: ");
		for (Input input : process.getInputs()) {
			OWLValue value = inputs.getValue(input);
			if (value.isDataValue())
				print(input.getLocalName() + " = " + value.toString());
			else {
				OWLIndividual ind = (OWLIndividual) value;
				if (ind.isAnon())
					println(input.getLocalName() + " = " + ind.toRDF(false, false));
				else
					println(input.getLocalName() + " = " + value.toString());
			}
		}
	}

	@Override
	public void executionFinished(final Process process,
			final ValueMap<Input, OWLValue> inputs,
			final ValueMap<Output, OWLValue> outputs) {
		println("Process" + process.getLocalName() + "执行完成");
		println("输出: ");
		for (Output output : process.getOutputs()) {
			OWLValue value = outputs.getValue(output);
			if (value.isDataValue())
				println(output.getLocalName() + " = " +value.toString());
			else {
				OWLIndividual ind = (OWLIndividual) value;
				if (ind.isAnon())
					println(output.getLocalName() + " = " +ind.toRDF(false, false));
				else
					println(output.getLocalName() + " = " +value.toString());
			}
		}
	}

	@Override
	public void executionFailed(final ExecutionException e) {
		println("服务执行出现错误");
		println(e.toString());
		e.printStackTrace();
	}
}
