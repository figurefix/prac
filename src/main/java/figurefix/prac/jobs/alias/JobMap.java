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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import figurefix.prac.jobs.QuickJob;

public final class JobMap {

	private HashMap<String, String> jobmap = new HashMap<String, String>();
	
	public JobMap() {
		
	}
	
	public void put(Class<? extends QuickJob> cls) throws DuplicateAliasException {
		this.putOrNot(cls);
	}
	
	private synchronized void putOrNot(Class<?> cls) throws DuplicateAliasException {
		Alias jobano = cls.getAnnotation(Alias.class);
		if(jobano != null 
		&& jobano.value() != null 
		&& jobano.value().trim().length() > 0
		) {
			String alias = jobano.value().trim();
			String clsname = cls.getName();
			if(jobmap.containsKey(alias) && !clsname.equals(jobmap.get(alias))) {
				throw new DuplicateAliasException(alias, jobmap.get(alias), clsname);
			} else if(!jobmap.containsKey(alias)) {
				jobmap.put(alias, clsname);
			}			
		}
	}

	public String getClassName(String alias) {
		return jobmap.get(alias);
	}

	public synchronized void lookupJobs(String path, boolean recursive, String ... pkgs) throws DuplicateAliasException, IOException {
		
		if(path==null || path.trim().length()==0) {
			return;
		}

		if(path.toLowerCase().endsWith(".jar")) {
			ZipFile zfile = new ZipFile(path);
			this.lookupInJar(zfile, recursive, pkgs);
		} else {
			if(pkgs==null || pkgs.length==0) {
				this.lookupInDir(path, recursive, null);
			} else {
				String dir = (path.endsWith("/") || path.endsWith("\\")) ? path.substring(0, path.length()-1) : path;
				for(int i=0; i<pkgs.length; i++) {
					String subdir = dir+File.separator+pkgs[i].replace('.', File.separatorChar);
					this.lookupInDir(subdir, recursive, pkgs[i]);
				}
			}
		}
	}
	
	private void lookupInJar(ZipFile zfile, boolean recursive, String[] pkgs) throws DuplicateAliasException {
		Enumeration<? extends ZipEntry> en = zfile.entries();
		while(en.hasMoreElements()) {
			ZipEntry ze = en.nextElement();
			if ( ! ze.isDirectory() ) {
				String name = ze.getName();
				if(name.toLowerCase().endsWith(".class")) {
					String clsname = name.substring(0, name.length()-6).replace('/', '.').replace('\\', '.');
					int idx = clsname.lastIndexOf('.');
					String clspkg = idx==-1 ? "" : clsname.substring(0, idx);
					for(int i=0; i<pkgs.length; i++) {
						if ( (recursive ? clsname.startsWith(pkgs[i]) : clspkg.equals(pkgs[i]) )) {
							try {
								Class<?> cls = Class.forName(clsname);
								this.putOrNot(cls);
							} catch (ClassNotFoundException e) {
								continue;
							}
						}
					}
				}
			}
		}
	}
	
	private void lookupInDir(String dir, boolean recursive, String pkg) throws DuplicateAliasException {

		File fdir = new File(dir);
		if ( ! fdir.isDirectory()) {
			return;
		}
		
		File[] list = fdir.listFiles();
		
		for(int i=0; i<list.length; i++) {
			File f = list[i];
			String fname = f.getName();
			if(f.isDirectory()) {
				if(recursive) {
					String subdir = dir+File.separator+fname;
					String subpkg = (pkg==null ? fname : (pkg+"."+fname));
					lookupInDir(subdir, recursive, subpkg);
				}
			} else if(fname.endsWith(".class")) {
				try {
					String clsname = (pkg==null ? "" : (pkg+"."))+fname.substring(0, fname.length()-6);
					Class<?> cls = Class.forName(clsname);
					this.putOrNot(cls);
				} catch (ClassNotFoundException e) {
					continue;
				}
			}
		}
	}
}
