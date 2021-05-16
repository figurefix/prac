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

package figurefix.prac.jobs.alias;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import figurefix.prac.jobs.QuickJob;
import figurefix.prac.logging.SrcLog;

/**
 * dispatch reqeust to job class by alias.suffix
 */
public class AliasDispatcher {

	private static JobMap jobmap = null;
	
	private static synchronized JobMap getJobMap(ServletContext stx) throws DuplicateAliasException, IOException {
		if(jobmap==null) {
			jobmap = new JobMap();
			jobmap.lookupJobs(stx.getRealPath("/")+"WEB-INF"+File.separator+"classes", true);
		}
		return jobmap;
	}
	
	public static QuickJob dispatch(ServletContext context, HttpServletRequest request) throws ServletException {
		
		if(context==null) {
			throw new IllegalArgumentException(ServletContext.class.getName()+" null");
		}
		if(request==null) {
			throw new IllegalArgumentException(HttpServletRequest.class.getName()+" null");
		}
		
		Object o;
		try {
			JobMap thismap = getJobMap(context);
			String uri = request.getRequestURI();
			String jobname = uri.substring(uri.lastIndexOf('/')+1, uri.lastIndexOf('.')); //remove suffix
			String jobclass = thismap.getClassName(jobname);
			if(jobclass==null) {
				jobclass = jobname;
			}

			Class<?> c = Class.forName(jobclass);
			o = c.newInstance();
			return (QuickJob)o;
			
		} catch (Exception e) {
			SrcLog.error(e);
			throw new ServletException(e);
		}
	}

}
