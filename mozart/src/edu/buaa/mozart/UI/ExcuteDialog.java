package edu.buaa.mozart.UI;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.service.Service;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.mindswap.owls.process.Process;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ExcuteDialog extends Dialog {

    public class Result{
    	String[] inputs;
        boolean confirmed;
    };
	protected Result result = new Result();
	protected Shell shell;
    protected Service mService;
    protected Text[] mInputText;
    Process mProcess;
    

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ExcuteDialog(Shell parent, int style, Service service) {
		super(parent, style);
        mService = service;
        mProcess = mService.getProcess();
        mInputText = new Text[mProcess.getInputs().size()];
        result.inputs = new String[mProcess.getInputs().size()];
		setText("Web 服务执行");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Result open() {
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
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE);
		shell.setSize(299, 340);
		shell.setText(getText());
		shell.setLayout(new FormLayout());
		
		Composite composite = new Composite(shell, SWT.NONE);
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(100, -57);
		fd_composite.bottom = new FormAttachment(100, -15);
		fd_composite.right = new FormAttachment(100, -10);
		composite.setLayoutData(fd_composite);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button btnConfirm = new Button(composite, SWT.NONE);
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i;
				for (i = 0; i < result.inputs.length; i++) {
					if (mInputText[i].getText().trim().length() == 0){
						MessageBox dlg = new MessageBox(shell);
						dlg.setMessage("请设置所有参数");
                        dlg.open();
                        return;
					} else {
						result.inputs[i] = mInputText[i].getText();
					}
				}
                result.confirmed = true;
                shell.dispose();
			}
		});
		btnConfirm.setText("确定");
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                result.confirmed = false;
                shell.dispose();
			}
		});
		btnCancel.setText("取消");
		
		Label inputLabel = new Label(shell, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0);
		fd_lblNewLabel.left = new FormAttachment(0);
		inputLabel.setLayoutData(fd_lblNewLabel);
		inputLabel.setText("输入");
		
        
        int shift = 20;
        int shift_offset = 30;
        int index = 0;
        for(Input input : mProcess.getInputs()){
    		fd_composite = new FormData();
    		fd_composite.top = new FormAttachment(inputLabel, shift);
            Label nameLabel = new Label(shell, SWT.NONE);
            nameLabel.setText(input.getLocalName());
            nameLabel.setLayoutData(fd_composite);
            
        	mInputText[index] = new Text(shell, SWT.BORDER | SWT.SINGLE); 
            fd_composite = new FormData();
            fd_composite.left = new FormAttachment(nameLabel, 20);
            fd_composite.top  = new FormAttachment(inputLabel, shift);
            mInputText[index].setLayoutData(fd_composite);
            shift += shift_offset;
            index++;
        }
        
	}
}
