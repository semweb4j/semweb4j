/**
 * 
 */
package org.ontoware.rdfreactor.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.ontoware.rdfreactor.generator.CodeGenerator;

/**
 * Define an ant task for calling CodeGenerator.generate().
 * 
 * taskdef.properties in the base folder of the rdfreactor project defines 
 * the mapping from the task name "rdfreactor" to this class.
 * 
 * Use this ant task like this: 
 * 
 *  <target name="rdfreactor-run">
 *		<taskdef resource="taskdef.properties">
 *			<classpath>
 *				<!-- this has to include rdfreactor-<version>.jar and all 
 *					 libs from rdfreactor-<version>-libs.zip -->
 * 				<fileset dir="./rdfreactor" includes="**  slash *.jar"/>
 * 			</classpath>
 *		</taskdef>
 *		<rdfreactor schemafile="./ronny.owl.n3"
 *			outdir="./test-gen"
 *			packagename="org.werner.test"
 *			semantics="owl" 
 *			skipbuiltins="true"
 *			alwayswritetomodel="true"
 *		/>
 *	</target> 
 *
 * The last the paramaters are optional and have the following defaults: 
 * semantics="rdfs", skipbuiltins="true", alwayswritetomodel="true"
 *
 * @author behe
 */
public class GeneratorAntTask extends Task {

	// private attributes representing parameters of this task
	
	/**
	 * 	a path to an rdf or owl file in N3, NT or XML syntax. File
	 *  extension determines parsing. 
	 */
	private File schemaFile;
	/**
	 * Directory in which the generated java classes are going to be put 
	 * e.g './src' or './gen-src' 
	 */
	private File outDir;
	/**
	 * Package Name of the generated java files: e.g. 'org.ontoware.myname.reactor'
	 */
	private String packageName;
	/**
	 * specifies the semantics to which the generated classes conform, 
	 * may be one of 'rdfs', 'owl' or 'rdfs+owl' (experimental)
	 */
	private String semantics = "rdfs";
	/**
	 * if false, internal helper classes are re-generated. This is
	 * usually not needed. 
	 */
	private boolean skipbuiltins = true;
	/**
	 * if false, public contructors allow for the choice
	 */
	private boolean alwaysWriteToModel = true; 
	
	
	// setters for the attributes, these get called from ant 
	
	public void setSchemaFile(File schemaFile) {
		this.schemaFile = schemaFile;
	}

	public void setOutDir(File outDir) {
		this.outDir = outDir;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public void setSemantics(OutputSemanticsEnumeration semEnum) {
		semantics = semEnum.getValue();
	}
	
	/**
	 * This class enforces the use of one of the provided alternatives for
	 * the output semantics of the generator.
	 * 
	 * @author behe
	 */
	public static class OutputSemanticsEnumeration extends EnumeratedAttribute {
		public String[] getValues() {
				return new String[] {"rdfs", "owl", "rdfs+owl"};
		}
	}
	
	public void setSkipBuiltIns(boolean skipbuiltins){
		this.skipbuiltins = skipbuiltins;
	}
	
	public void setAlwaysWriteToModel(boolean alwaysWriteToModel) {
		this.alwaysWriteToModel = alwaysWriteToModel;
	}
	
	
	
	
	public void execute() {

		// validate the presence of all required parameters
		
		if (schemaFile == null) {
			throw new BuildException("The location of the schemafile is " +
					"required for the RDF Reactor Generator task." +
					"It is provided with the parameter schemafile=\"/location/to/file.n3\"");
		} else if (!schemaFile.isFile()) {
			throw new BuildException(schemaFile + " is not a valid file");
		}

		if (outDir == null) {
			throw new BuildException("The output directory is required for the RDF Reactor Generator task." +
					"It is provided with the parameter outdir=\"/location/of/outdir\"");
		} else if (!outDir.isDirectory()) {
			throw new BuildException(outDir + " is not a valid directory");
		}

		if ((packageName == null) || (packageName == "")) {
			throw new BuildException("The packagename is required for the RDF Reactor Generator task." +
					"It is provided with the parameter packagename=\"org.my.name\"");
		}

		log("calling RDF Reactor Generator with parameters:");
		log("\t schema file:\t" + schemaFile);
		log("\t out dir:\t" + outDir);
		log("\t package name:\t" + packageName);
		log("\t semantics:\t" + semantics);
		log("\t skip built ins:\t " + skipbuiltins);
		log("\t always write to model:\t " + alwaysWriteToModel);
		
		// IMPROVE: use methodnameprefix
		try {
			CodeGenerator.generate(schemaFile.toString(), outDir.toString(), packageName, semantics, skipbuiltins, alwaysWriteToModel,"");
		} catch (Exception e) {
			throw new BuildException(e);
		}
	}

}
