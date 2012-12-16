package edu.buaa.mozart.UI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.exporter.DOMGenerator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.Process;

import edu.buaa.composer.Composer;
import edu.buaa.composer.ComposerConfig;
import edu.buaa.composer.impl.Mozart;
import edu.buaa.mozart.UI.core.LogAppender;
import edu.buaa.mozart.UI.core.Model;
import edu.buaa.mozart.notes.ComposeException;
import edu.buaa.mozart.stub.ServiceStub;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Canvas;

public class MozartMain {
	protected Shell shlMozart;
    protected Model coreModel;
    protected Display mDisplay;
    
    private ToolItem excuteItem;
    private List serviceListView;
    private List processListView;
    private ToolItem composeItem;
    private StyledText styledText;
    private Logger logger;
    
    private java.util.List<Service> mServices;
    private java.util.List<Process> mProcesses;
    private Map<Process, String> mCPNModels;
    private ServiceStub mServiceStub;
    private Text txtService;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MozartMain window = new MozartMain();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
        coreModel = new Model();
        mCPNModels = new HashMap<Process, String>();
        mDisplay = Display.getDefault();
		createContents();
        initLogger();
		shlMozart.open();
		shlMozart.layout();
		while (!shlMozart.isDisposed()) {
			if (!mDisplay.readAndDispatch()) {
				mDisplay.sleep();
			}
		}
	}
    
    private void initLogger(){
        WriterAppender appender = new LogAppender(shlMozart, styledText);
        appender.setImmediateFlush(true);
        appender.setLayout(new PatternLayout("%m%n"));
        logger = Logger.getRootLogger();
        logger.addAppender(appender);
    }

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlMozart = new Shell();
		shlMozart.setSize(616, 510);
		shlMozart.setText("Mozart");
		shlMozart.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel = new Label(shlMozart, SWT.NONE);
		lblNewLabel.setText("服务列表");
		
		Label label = new Label(shlMozart, SWT.NONE);
		label.setText("服务描述");
		
		ToolBar toolBar_1 = new ToolBar(shlMozart, SWT.FLAT | SWT.RIGHT);
		
		excuteItem = new ToolItem(toolBar_1, SWT.NONE);
		excuteItem.setEnabled(false);
		excuteItem.setToolTipText("执行");
		excuteItem.setImage(SWTResourceManager.getImage(MozartMain.class, "/edu/buaa/mozart/UI/Execute.png"));
		excuteItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Service selectedService = changeService();
				logger.info("excute service " + selectedService.getLocalName() + " ...");
				new ExcuteDialog(shlMozart, 0, selectedService).open();
			}
		});
		
		txtService = new Text(shlMozart, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		txtService.setEditable(false);
		GridData gd_txtService = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 2);
		gd_txtService.heightHint = 147;
		txtService.setLayoutData(gd_txtService);
		
		serviceListView = new List(shlMozart, SWT.BORDER | SWT.V_SCROLL);
		serviceListView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeService();
			}
		});
		GridData gd_serviceListView = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_serviceListView.widthHint = 109;
		gd_serviceListView.heightHint = 116;
		serviceListView.setLayoutData(gd_serviceListView);
		
		Label lblNewLabel_1 = new Label(shlMozart, SWT.NONE);
		lblNewLabel_1.setText("过程模型列表");
		new Label(shlMozart, SWT.NONE);
		
		ToolBar toolBar = new ToolBar(shlMozart, SWT.FLAT | SWT.RIGHT);
		
		composeItem = new ToolItem(toolBar, SWT.NONE);
		composeItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportCPN();
			}});
		composeItem.setEnabled(false);
		composeItem.setToolTipText("转化为CPN模型");
		composeItem.setImage(SWTResourceManager.getImage(MozartMain.class, "/edu/buaa/mozart/UI/convert.png"));
		
		ToolItem toolItem = new ToolItem(toolBar, SWT.NONE);
		toolItem.setToolTipText("打开CPN工具");
		toolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                int idx = processListView.getSelectionIndex();
                if(idx < 0 || idx > processListView.getItemCount()){
                	return;
                }
                Process process = mProcesses.get(idx);
                openCPN(process);
			}
		});
		toolItem.setImage(SWTResourceManager.getImage(MozartMain.class, "/edu/buaa/mozart/UI/GraphView.png"));
		
		Label label_1 = new Label(shlMozart, SWT.NONE);
		label_1.setText("输出窗口");
		
		processListView = new List(shlMozart, SWT.BORDER | SWT.V_SCROLL);
		processListView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                int idx = processListView.getSelectionIndex();
                if (idx < 0 || idx >= processListView.getItemCount()){
                	composeItem.setEnabled(false);
                }
                composeItem.setEnabled(true);
			}
		});
		GridData gd_processListView = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_processListView.widthHint = 112;
		gd_processListView.heightHint = 169;
		processListView.setLayoutData(gd_processListView);
		
		Menu menu = new Menu(shlMozart, SWT.BAR);
		shlMozart.setMenuBar(menu);
		
		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("文件");
		
		Menu menu_1 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_1);
		
		MenuItem importMenuItem = new MenuItem(menu_1, SWT.NONE);
		importMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                ImportDialog dlg = new ImportDialog(shlMozart, 0);
                URI target = dlg.open();
                MessageBox box = new MessageBox(shlMozart);
                if (target != null) {
                	try {
                        coreModel.setURI(target);
                		mServices = coreModel.getServices();
                        
                        box.setMessage("导入模型成功");
                        String[] serviceStrings = new String[mServices.size()];
                        for (int i = 0; i < mServices.size(); i++) {
                            serviceStrings[i] = mServices.get(i).getLocalName();
                        }
                        
                        serviceListView.setItems(serviceStrings);
                        changeService();
					} catch (IOException e1) {
                        box.setMessage("文件" + target + "error:" + e1.getMessage());
                        box.open();
						e1.printStackTrace();
					}
                }
			}
		});
		importMenuItem.setText("导入服务");
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		MenuItem mntmNewItem = new MenuItem(menu_1, SWT.NONE);
		mntmNewItem.setText("退出");
		
		MenuItem menuItem_1 = new MenuItem(menu, SWT.CASCADE);
		menuItem_1.setText("模型");
		
		Menu menu_2 = new Menu(menuItem_1);
		menuItem_1.setMenu(menu_2);
		
		MenuItem mntmcpn = new MenuItem(menu_2, SWT.NONE);
		mntmcpn.setText("转化为CPN");
		
		MenuItem menuItem_2 = new MenuItem(menu, SWT.CASCADE);
		menuItem_2.setText("服务器");
		
		Menu menu_3 = new Menu(menuItem_2);
		menuItem_2.setMenu(menu_3);
		
		MenuItem mntmstub = new MenuItem(menu_3, SWT.NONE);
		mntmstub.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
                MessageBox dlg = new MessageBox(shlMozart);
				if (mServiceStub == null){
					try {
						mServiceStub = new ServiceStub();
					} catch (IOException e1) {
						dlg.setMessage("启动服务失败 : "+ e1.getMessage());
                        dlg.open();
						return;
					}
				}
                if (!mServiceStub.isRun()){
                    Thread thread = new Thread(mServiceStub);
                    thread.start();
                }
                dlg.setMessage("服务已经启动");
                dlg.open();
			}
		});
		mntmstub.setText("启动Stub实例");
		
		MenuItem menuItem_3 = new MenuItem(menu, SWT.CASCADE);
		menuItem_3.setText("帮助");
		
		Menu menu_4 = new Menu(menuItem_3);
		menuItem_3.setMenu(menu_4);
		
		MenuItem menuItem_4 = new MenuItem(menu_4, SWT.NONE);
		menuItem_4.setText("关于");
		
		styledText = new StyledText(shlMozart, SWT.BORDER | SWT.READ_ONLY);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

	}
    
    private void importProcesses(Service service){
    	Process process = service.getProcess();
        
        if (process instanceof CompositeProcess){
            mProcesses = ((CompositeProcess)process).getComposedOf().getAllProcesses(true);
            mProcesses.add(process);
        	String[] processString = new String[mProcesses.size()];
        	
        	for (int i = 0; i < mProcesses.size(); i++){
        		processString[i] = mProcesses.get(i).getLocalName();
        	}
        	processListView.setItems(processString);
        } else{
            mProcesses = new ArrayList<Process>();
            mProcesses.add(process);
        	processListView.setItems(new String[]{process.getLocalName()});
        }
    }
    
    private void openCPN(Process process){
        logger.info("opening "+ ComposerConfig.CPN_PATH + "...");
        Runtime run = Runtime.getRuntime();
        try {
            run.exec(ComposerConfig.CPN_PATH+" " + mCPNModels.get(process));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
    
    private void exportCPN(){
        FileDialog fileDlg = new FileDialog(shlMozart,SWT.SAVE);
        fileDlg.setFilterExtensions(new String[]{"*.cpn", "*.*"});
        fileDlg.setFilterNames(new String[]{"CPN files", "All Files"});
        
        String targetFile = fileDlg.open();
        Composer composer = new Mozart();
       
        int idx = processListView.getSelectionIndex();
        if (idx < 0 || idx > mProcesses.size()){
            logger.info("请选择需要转化的process");
        	return;
        } else{
        	Process process = mProcesses.get(idx);
            try {
				PetriNet net = composer.Compose(process);
				DOMGenerator.export(net, targetFile);
                logger.info("生成CPN模型成功");
                mCPNModels.put(process, targetFile);
			} catch (ComposeException e1) {
                logger.error(e1.getMessage());
			} catch (FileNotFoundException e1) {
                logger.error(e1.getMessage());
			} catch (OperationNotSupportedException e1) {
                logger.error(e1.getMessage());
			} catch (TransformerException e1) {
                logger.error(e1.getMessage());
			} catch (ParserConfigurationException e1) {
                logger.error(e1.getMessage());
			}
        }
    }
    private Service changeService(){
		int idx = serviceListView.getSelectionIndex();
		if (idx < mServices.size() && idx >= 0){
			Service service = mServices.get(idx);
		    importProcesses(service);
            excuteItem.setEnabled(true);
            txtService.setText(service.toString());
            return service;
		}else{
			excuteItem.setEnabled(false);
			txtService.setText("");
		}
		return null;
    }
}
