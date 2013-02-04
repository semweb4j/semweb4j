package org.apache.maven.plugins.rdfreactor_generate;

/*
 * Copyright (c) 2008-2009, Steffen Ryll
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *  * Neither the name of the Hasso-Plattner-Institut, Potsdam, Germany nor 
 *    the names of its contributors may be used to endorse or promote products 
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.ontoware.rdf2go.Reasoning;
import org.ontoware.rdfreactor.generator.CodeGenerator;

import java.io.File;
import java.io.IOException;

/**
 * Goal which generates Java classes from an RDF Schema file. Source code generation
 * is realized by <a href ="http://semanticweb.org/wiki/RDFReactor">RDFReactor</a>.
 *
 * @goal rdfs-classes
 * @phase generate-sources
 * 
 * @author <a href="mailto:steffen.ryll@hpi.uni-potsdam.de">Steffen Ryll</a>
 * @version $Id$
 */
public class RdfSClassGenerator extends AbstractMojo
{

	/**
	 * Path to the file containing the RDF Schema to process.
	 * @parameter
	 * @required
	 */
	private File schemaFile;

	/**
	 * The directory where generated java code shall be written to.
	 * @parameter expression="${project.build.directory}/generated-sources/rdfs-classes"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * Package that the generated classes shall belong to.
	 * @parameter expression="${project.groupId}"
	 * @required
	 */
	private String packageName;

	/**
	 * Specifies whether implicitly existing RDF classes (contained in RDF or RDFS) shall be
	 * generated as well (false) or not (true).
	 * 
	 * @parameter default-value="true" 
	 */
	private boolean skipBuiltins;

	/**
	 * A prefix that will be included in all method names. This setting is useful to avoid surprising
	 * name collisions with pre-defined methods which would render the generated code uncompilable.
	 * A prefix is preferably short and starts with a capital latter.
	 *  
	 * @parameter default-value="" 
	 */
	private String methodPrefix;

	/**
	 * The Maven Project Object
	 *
	 * @parameter expression="${project}"
	 * @required
	 */
	private MavenProject project;

	/**
	 * Log file location of RDFReactor
	 * 
	 * @parameter default-value="target/rdfreactor.log"
	 * @readonly
	 */
    private File rdfReactorLogfile;

	public void execute()
		throws MojoExecutionException, MojoFailureException
	{
	    if (!schemaFile.exists())
	        getLog().error("Schema file (" + schemaFile.getAbsolutePath() + ") not found. Aborting.");

	    if (methodPrefix == null) {
	    	methodPrefix = "";
	    }
	    
	    if ( needsRegeneration() ) {
			generateCode();
		} else {
			getLog().info("Generated classes are up to date - not regenerating.");
		}

		// add generated code to list of files to be compiled
		if (project != null) {
			project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
		}
	}

    /**
     * Checks for indicators that source code should be regenerated from the
     * schema. Specifically, it checks modification time of the log file,
     * existence of <code>outputDirectory</code> and of package
     * sub-directories as well modification time of generated files.
     * 
     * @return <code>true</code> if source code must be regenerated,
     *         otherwise <code>false</code>
     */
    private boolean needsRegeneration() {
        boolean regenerationReq = false;
        regenerationReq |= !rdfReactorLogfile.exists();
        regenerationReq |= schemaFile.lastModified() >= rdfReactorLogfile.lastModified();
        regenerationReq |= !outputDirectory.exists();
            
        // split package name at dot separators
        String[] pkgComponents = packageName.split("\\.");
        String packageDirPath = outputDirectory.getAbsolutePath(); 
        for (int i=0; i< pkgComponents.length; i++) {
            if (regenerationReq)
                break;

            packageDirPath = packageDirPath + File.separator + pkgComponents[i]; 
            File subDir = new File(packageDirPath);

            regenerationReq |= !subDir.exists();
            
            if (i == pkgComponents.length-1) { // reached bottom of generated dir tree
                File[] fileList = subDir.listFiles();
                for (int j = 0; j < fileList.length; j++) {
                    File genFile = fileList[j];
                    // if schema file is newer than _any_ the generated files, regenerate!
                    if (genFile.isFile() 
                            && schemaFile.lastModified() >= genFile.lastModified()) {
                        regenerationReq = true;
                        break;
                    }
                }
            }
        }

        return regenerationReq;
    }

	private void generateCode() throws MojoExecutionException, MojoFailureException {
		try {
		    // make sure that directory for log file exists.
		    rdfReactorLogfile.getParentFile().mkdirs();
			
		    // configure logging infrastructure for RDFReactor
		    FileAppender logFileAppender = new FileAppender(new SimpleLayout(), rdfReactorLogfile.getAbsolutePath());
			BasicConfigurator.configure(logFileAppender);

		} catch (IOException ioe) {
			throw new MojoExecutionException("Cannot open log file for writing RDFReactor log messages", ioe);
		}

		getLog().info("Generating code from RDF schema file " + schemaFile + " into dir " + outputDirectory
				+ ". Classes will be in package " + packageName + " and with method prefix " + methodPrefix +". skipBuiltins is " + skipBuiltins + ".");
		getLog().info("RDFReactor's log messages are written to " + rdfReactorLogfile);


		try {
			CodeGenerator.generate(schemaFile.getAbsolutePath(), outputDirectory.getAbsolutePath(), packageName, Reasoning.rdfs, skipBuiltins, methodPrefix);
		} catch (Exception e) {
			e.printStackTrace();
		    throw new MojoFailureException(e, "RDFS processing error", "Could not generate code from the specified RDF schema file.");
		}
	}
}
