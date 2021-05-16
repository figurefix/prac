/*
 * Copyright (C) 2015-2020 figurefix <figurefix@outlook.com>
 * 
 * This file is part of prac, 
 * prac is free software released under the MIT license.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including 
 * without limitation the rights to use, copy, modify, merge, publish, 
 * distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to 
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION 
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */

package figurefix.prac.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <p>a logging utility that offers methods to locate source where logging is invoked</p>
 * <p>a "source package" prefix can be specified to tell this utility that 
 * the recorded logging source should match the specified</p>
 * <p>a "bypass package" prefix can be specified to tell this utility that
 * when locating source from thread stack, 
 * packages that match "bypass package" should by bypassed</p>
 * @author figurefix
 *
 */
public class SrcLog {
	
	private static final SrcLog slog = new SrcLog();
	
	public static SrcLog getDefaultSrcLog() {
		return slog;
	}
	
	public static void trace(Object ... msg) {
		slog.logTrace(msg);
	}
	
	public static void info(String msg) {
		slog.logInfo(msg);
	}
	
	public static void error(String msg) {
		slog.logError(msg);
	}
	
	public static void error(Throwable e) {
		slog.logError(e);
	}

	private Logger logger = null;
	private String sourcePkg = null;
	private String bypassPkg = null;
	private boolean tracing = false;
	
	/**
	 * log to console
	 */
	public SrcLog() {
		this.logger = new ConsoleLogger();
	}
	
	/**
	 * log to file
	 * @param file log file
	 */
	public SrcLog(String file) {
		this.logger = new FileLogger(file);
	}
	
	/**
	 * use a specific logger
	 * @param logger logger
	 */
	public SrcLog(Logger logger) {
		this.logger = logger;
	}
	
	/**
	 * use a specific logger, also specify logging source and bypass package 
	 * @param logger logger
	 * @param source source package
	 * @param bypass bypass package
	 */
	public SrcLog(Logger logger, String source, String bypass) {
		this.logger = logger;
		this.sourcePkg = source;
		this.bypassPkg = bypass;
	}
	
	/**
	 * set false to stop tracing, 
	 * the logger does not trace by default.
	 * @param trc trace or not
	 */
	public void setTracing(boolean trc) {
		this.tracing = trc;
	}
	
	public boolean isTracing() {
		return this.tracing;
	}
	
	/**
	 * change logger 
	 * @param log logger
	 */
	public void setLogger(Logger log) {
		this.logger = log;
	}
	
	/**
	 * get logger
	 * @return logger
	 */
	public Logger getLogger() {
		return this.logger;
	}
	
	/**
	 * change source package
	 * @param pkg source package
	 */
	public void setSource(String pkg) {
		this.sourcePkg = pkg;
	}
	
	/**
	 * change bypass package
	 * @param pkg bypass package
	 */
	public void setBypass(String pkg) {
		this.bypassPkg = pkg;
	}
	
	/**
	 * get source package
	 * @return source package
	 */
	public String getSource() {
		return this.sourcePkg;
	}
	
	/**
	 * get bypass package
	 * @return bypass package
	 */
	public String getBypass() {
		return this.bypassPkg;
	}
	
	/**
	 * create a new SourceLog instance with the specified source package
	 * @param srcpkg source package
	 * @return SourceLog instance
	 */
	public SrcLog source(String srcpkg) {
		SrcLog slog = new SrcLog(this.logger, srcpkg, this.bypassPkg);
		slog.setTracing(this.tracing);
		return slog;
	}
	
	/**
	 * create a new SourceLog instance with the specified source and bypass package
	 * @param srcpkg source package
	 * @param bpspkg bypass package
	 * @return SourceLog instance
	 */
	public SrcLog source(String srcpkg, String bpspkg) {
		SrcLog slog = new SrcLog(this.logger, srcpkg, bpspkg);
		slog.setTracing(this.tracing);
		return slog;
	}
	
	/**
	 * create a new SourceLog instance with the specified bypass package
	 * @param bpspkg bypass package
	 * @return SourceLog instance
	 */
	public SrcLog bypass(String bpspkg) {
		SrcLog slog = new SrcLog(this.logger, this.sourcePkg, bpspkg);
		slog.setTracing(this.tracing);
		return slog;
	}
	
	private static String getSource(String msg, String source, String bypass) {
		
		if(bypass==null) {
			bypass = SrcLog.class.getPackage().getName();			
		}

		StackTraceElement[] ste = new Throwable().getStackTrace();
		StackTraceElement trace = ste[0];
		boolean bypassing = false;
		for(int i=0; i<ste.length; i++) {
			StackTraceElement ss = ste[i];
			String clsname = ss.getClassName();
			if(clsname.startsWith(bypass) && !bypassing) {
				bypassing = true;
			} else if( ! clsname.startsWith(bypass) && bypassing) { // bypassed
				trace = ss;
				if(source==null) {
					break;
				}
			}
			if(source!=null && clsname.startsWith(source)) {
				trace = ss;
				break;
			}
		}
		
		StringBuilder built = new StringBuilder(msg);
		built.append(" @ ")
			.append(trace.getClassName())
			.append('.')
			.append(trace.getMethodName())
			.append('(')
			.append(trace.getFileName())
			.append(':')
			.append(trace.getLineNumber())
			.append(')');

		return built.toString();
	}
	
	public void logTrace(Object ... msgs) {
		if(this.tracing && this.logger!=null) {
			StringBuilder msg = new StringBuilder();
			for(int i=0; i<msgs.length; i++) {
				msg.append(msgs[i]);
			}
			this.logger.trace(getSource(msg.toString(), this.sourcePkg, this.bypassPkg));
		}
	}
	
	public void traceAnyway(Object ... msgs) {
		if(this.logger!=null) {
			StringBuilder msg = new StringBuilder();
			for(int i=0; i<msgs.length; i++) {
				msg.append(msgs[i]);
			}
			this.logger.trace(getSource(msg.toString(), this.sourcePkg, this.bypassPkg));
		}
	}

	public void logInfo(String msg) {
		if(this.logger!=null) {
			this.logger.info(getSource(msg, this.sourcePkg, this.bypassPkg));
		}
	}
	
	public void logError(String msg) {
		if(this.logger!=null) {
			this.logger.error(getSource(msg, this.sourcePkg, this.bypassPkg));
		}
	}
	
	public void logError(Throwable e) {
		if(this.logger!=null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			
			StringBuilder ss = new StringBuilder();
			ss.append(
				getSource(
					e.getMessage()!=null ? e.getMessage() : "...", 
					this.sourcePkg, 
					this.bypassPkg)
				)
				.append('\n').append(sw.toString());
			this.logger.error(ss.toString());
		}
	}
}
