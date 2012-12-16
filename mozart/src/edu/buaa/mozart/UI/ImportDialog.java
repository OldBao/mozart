package edu.buaa.mozart.UI;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ImportDialog extends Dialog {

	protected URI result;
	protected Shell shell;
	private Text txtFile;
	private Text txtUrl;
    private boolean isFile = true;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ImportDialog(Shell parent, int style) {
		super(parent, style);
		setText("导入模型");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public URI open() {
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
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(446, 201);
		shell.setText("模型导入");
		shell.setLayout(null);
		
		Button radioBtnFile = new Button(shell, SWT.RADIO);
		radioBtnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                isFile = true;
                txtFile.setEnabled(isFile);
                txtUrl.setEnabled(!isFile);
			}
		});
		radioBtnFile.setSelection(true);
		radioBtnFile.setBounds(5, 5, 101, 24);
		radioBtnFile.setText("从文件导入");
		
		txtFile = new Text(shell, SWT.BORDER);
		txtFile.setBounds(5, 35, 409, 27);
		
		Button btnFile = new Button(shell, SWT.NONE);
		btnFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                FileDialog dlg = new FileDialog(shell, SWT.SINGLE);
                dlg.setFilterExtensions(new String[]{"*.owl", "*.*"});
                dlg.setFilterNames(new String[]{"Services（*.owl）", "All Files"});
                String file = dlg.open();
                if (file != null){
                	txtFile.setText(file);
                } 
			}
		});
		btnFile.setBounds(419, 34, 24, 29);
		btnFile.setText("...");
		
		Button radioBtnURL = new Button(shell, SWT.RADIO);
		radioBtnURL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                isFile = false;
                txtFile.setEnabled(isFile);
                txtUrl.setEnabled(!isFile);
			}
		});
		radioBtnURL.setBounds(5, 68, 114, 24);
		radioBtnURL.setText("从URL导入");
		
		txtUrl = new Text(shell, SWT.BORDER);
		txtUrl.setEnabled(false);
		txtUrl.setBounds(5, 98, 437, 27);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(5, 131, 437, 60);
		composite.setLayout(null);
		
		Button btnConfirm = new Button(composite, SWT.NONE);
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                try {
					if (isFile) {
						result = new URI("file://" + txtFile.getText());
					} else {
						result = new URI(txtUrl.getText());
					}
                    shell.dispose();
				} catch (URISyntaxException e1) {
                    MessageDialog.openError(shell, "模型导入错误", "文件名或URI有错");
					e1.printStackTrace();
				}
			}
		});
		btnConfirm.setBounds(249, 10, 91, 29);
		btnConfirm.setText("确定");
		
		Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                shell.dispose();
			}
		});
		btnCancel.setBounds(346, 10, 91, 29);
		btnCancel.setText("取消");

	}
}
