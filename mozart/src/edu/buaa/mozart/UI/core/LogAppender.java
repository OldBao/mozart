package edu.buaa.mozart.UI.core;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Shell;

public class LogAppender extends WriterAppender{
    StyledText mStyledText;
    Shell mShell;
    
	public LogAppender(Shell shell, StyledText text) {
		mStyledText = text;
        mShell = shell;
        mErrorStyle = new StyleRange();
        mErrorStyle.foreground = shell.getDisplay().getSystemColor(SWT.COLOR_DARK_RED);
        mWarnStyle = new StyleRange();
        mWarnStyle.foreground = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
        mInfoStyle  = new StyleRange();
        mInfoStyle.foreground = shell.getDisplay().getSystemColor(SWT.COLOR_GREEN);
        mDefaultStyle = new StyleRange();
        mDefaultStyle.foreground = shell.getDisplay().getSystemColor(SWT.COLOR_BLUE);
	}
    
    @Override
    public void append(final LoggingEvent event) {
        mShell.getDisplay().asyncExec(new Runnable(){
			@Override
			public void run() {
				final StyleRange range = getStyleByLevel(event.getLevel());
				final String txt = getLayout().format(event);
				range.start = mStyledText.getText().length();
				range.length = txt.length();
				mStyledText.append(txt);
				mStyledText.setStyleRange(range);
			}
        });
    }
    
    @Override
    public boolean requiresLayout(){
    	return true;
    }
    
    @Override
    public void setLayout(Layout layout){
    	super.setLayout(layout);
    }
    
    @Override
    protected final void closeWriter(){
    	
    }
    
    private StyleRange mErrorStyle, mInfoStyle, mWarnStyle, mDefaultStyle;
    
    private StyleRange getStyleByLevel(Level loglevel) {
    	switch (loglevel.toInt()){
    	case Level.ERROR_INT:
    		return mErrorStyle;
    	case Level.WARN_INT:
    		return mWarnStyle;
    	case Level.INFO_INT:
    		return mInfoStyle;
    	default:
    		return mDefaultStyle;
    	}
    }
}
