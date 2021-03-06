/*******************************************************************************
 * Copyright (c) 2016 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.commons.maven.java;

import java.io.File;
import java.nio.file.Path;

import org.springframework.ide.vscode.commons.java.AbstractJavaProject;
import org.springframework.ide.vscode.commons.java.DelegatingCachedClasspath;
import org.springframework.ide.vscode.commons.maven.MavenCore;
import org.springframework.ide.vscode.commons.util.Log;

/**
 * Wrapper for Maven Core project
 * 
 * @author Alex Boyko
 *
 */
public class MavenJavaProject extends AbstractJavaProject {
	
	private DelegatingCachedClasspath<MavenProjectClasspath> classpath;
	private File pom;
	
	public MavenJavaProject(MavenCore maven, File pom, Path projectDataCache) {
		super(projectDataCache);
		this.pom = pom;
		this.classpath = new DelegatingCachedClasspath<>(
				() -> new MavenProjectClasspath(maven, pom),
				projectDataCache == null ? null : projectDataCache.resolve(DelegatingCachedClasspath.CLASSPATH_DATA_CACHE_FILE).toFile()
			);
	}
	
	public MavenJavaProject(MavenCore maven, File pom) {
		this(maven, pom, null);
		if (!classpath.isCached()) {
			try {
				classpath.update();
			} catch (Exception e) {
				Log.log(e);
			}
		}
	}
	
	@Override
	public String getElementName() {
		if (classpath.getName() == null) {
			return pom.getParentFile().getName();
		} else {
			return super.getElementName();
		}
	}
	
	@Override
	public DelegatingCachedClasspath<MavenProjectClasspath> getClasspath() {
		return classpath;
	}
	
	boolean update() throws Exception {
		return classpath.update();
	}
	
	public File pom() {
		return pom;
	}
	
	@Override
	public String toString() {
		return "MavenJavaProject("+classpath.getName()+")";
	}

}
