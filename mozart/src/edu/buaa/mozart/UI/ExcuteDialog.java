package edu.buaa.mozart.UI;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mindswap.owls.service.Service;

public class ExcuteDialog extends Dialog {

	protected Object result;
	protected Shell shell;
    protected Service mService;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ExcuteDialog(Shell parent, int style, Service service) {
		super(parent, style);
        mService = service;
		setText("Web 服务执行");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText(getText());
	}

}
