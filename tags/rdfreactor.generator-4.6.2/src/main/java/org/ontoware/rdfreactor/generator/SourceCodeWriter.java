package org.ontoware.rdfreactor.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.ontoware.rdfreactor.generator.java.JClass;
import org.ontoware.rdfreactor.generator.java.JModel;
import org.ontoware.rdfreactor.generator.java.JPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Write the JModel via Velocity templates to Java source code.
 * 
 * @author $Author: xamde $
 * @version $Id: SourceCodeWriter.java,v 1.9 2006/07/01 10:35:09 xamde Exp $
 * 
 */
public class SourceCodeWriter {

	public static final String TEMPLATE_CLASS = "class.vm";

	private static Logger log = LoggerFactory.getLogger(SourceCodeWriter.class);

	public static String memreport() {
		return "memory free: " + Runtime.getRuntime().freeMemory() / 1024
				+ " KB; total: " + Runtime.getRuntime().totalMemory() / 1024
				+ " KB; max: " + Runtime.getRuntime().maxMemory() / 1024
				+ " KB";
	}

	/**
	 * Writes model 'jm' to 'outdir' creating sub-directories for packages as
	 * needed. Uses the default template. Prefixes all methods with 'prefix',
	 * e.g. with prefix="Sioc" one gets "getSiocName"
	 * 
	 * @param jm
	 * @param outdir
	 * @param methodnamePrefix
	 * @throws IOException
	 */
	public static void write(JModel jm, File outdir, String methodnamePrefix)
			throws Exception {
		write(jm, outdir, TEMPLATE_CLASS, methodnamePrefix);
	}

	/**
	 * Writes model 'jm' to 'outdir' creating sub-directories for packages as
	 * needed. Uses 'templateName'. Prefixes all methods with 'prefix', e.g.
	 * with prefix="Sioc" one gets "getSiocName"
	 * 
	 * @param jm
	 * @param outdir
	 * @param templateName
	 *            in resource-syntax, e.g. "com/example/myname.vm"
	 * @param methodnamePrefix
	 * @throws IOException
	 */
	public static void write(JModel jm, File outdir, String templateName,
			String methodnamePrefix) throws IOException {
		assert templateName != null;

		log.info("Adding inverse properties");
		jm.addInverseProperties();

		assert jm.isConsistent() : "java package is not consistent";

		outdir.mkdirs();
		File modelFile = new File(outdir, "rdfreactor.model.log");
		FileWriter fw = new FileWriter(modelFile);
		fw.write(jm.toString());
		fw.close();
		log
				.info("Wrote RDFReactors interpretation of the ontology into a readable textfile at "
						+ modelFile.getAbsolutePath());

		// //////////////
		// template

		log.info("prepare for writing " + jm.getPackages().size()
				+ " packages using template " + templateName);

		Calendar now = Calendar.getInstance();
		SourceCodeWriter sourceCodeWriter = new SourceCodeWriter(jm,
				methodnamePrefix, now, templateName, outdir);
		sourceCodeWriter.initEngine();
		sourceCodeWriter.initTemplate();
		sourceCodeWriter.initContext();
		sourceCodeWriter.writeModel();
	}

	private JModel jm;

	private String methodnamePrefix;

	private Calendar now;

	private File outdir;

	private Template template;

	private String templateName;

	private VelocityContext velocityContext = null;

	private VelocityEngine velocityEngine = null;

	public SourceCodeWriter(JModel jm, String methodnamePrefix, Calendar now,
			String templateName, File outdir) {
		this.jm = jm;
		this.methodnamePrefix = methodnamePrefix;
		this.now = now;
		this.templateName = templateName;
		this.outdir = outdir;
	}

	private void initContext() {
		this.velocityContext = new VelocityContext();
		this.velocityContext.put("methodnameprefix", this.methodnamePrefix);
		// for debug
		this.velocityContext.put("now", DateFormat.getInstance().format(
				this.now.getTime()));
		this.velocityContext.put("root", this.jm.getRoot());
		this.velocityContext.put("generatorVersion",
				CodeGenerator.GENERATOR_VERSION);
		this.velocityContext.put("utils", new Utils());

	}

	private void initEngine() {
		this.velocityEngine = new VelocityEngine();
		log.debug("Free memory: " + Runtime.getRuntime().freeMemory());

		try {
			this.velocityEngine.setProperty("resource.loader", "class");
			this.velocityEngine
					.setProperty("class.resource.loader.class",
							"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

			// see http://minaret.biz/tips/tomcatLogging.html
			this.velocityEngine.setProperty("runtime.log.logsystem.class",
					"org.apache.velocity.runtime.log.SimpleLog4JLogSystem");

			this.velocityEngine.init();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void initTemplate() {
		if (!this.velocityEngine.resourceExists(this.templateName))
			throw new RuntimeException("template " + this.templateName
					+ " does not exist with resource loader "
					+ RuntimeConstants.RESOURCE_LOADER);

		try {
			this.template = this.velocityEngine.getTemplate(this.templateName);
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		} catch (ParseErrorException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		assert this.template != null;

		log.debug("Initialised template. Free memory: "
				+ Runtime.getRuntime().freeMemory());
	}

	private void writeModel() throws IOException {
		for (JPackage jp : this.jm.getPackages())
			writePackage(jp);
	}

	private void writeClass(JPackage jp, JClass jc, File packageOutdir) throws IOException {
		assert this.template != null;
		assert jc != null;
		assert jc.getName() != null;
		assert jc.getSuperclass() != null;
		this.velocityContext.put("package", jp);
		this.velocityContext.put("class", jc);
		File outfile = new File(packageOutdir, jc.getName() + ".java");
		log.info("Generating " + outfile.getAbsolutePath());
		FileWriter fw = new FileWriter(outfile);
		BufferedWriter bw = new BufferedWriter(fw);

		try {
			log.debug("Before template merge.    " + memreport());
			this.template.merge(this.velocityContext, bw);
			log.debug("After template merge.     " + memreport());
			// context has to be re-initialised every time to work around an
			// ever-growing cache in velocity
			initContext();
			log.debug("After context re-init     " + memreport());
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		} catch (ParseErrorException e) {
			throw new RuntimeException(e);
		} catch (MethodInvocationException e) {
			throw new RuntimeException(e);
		}
		bw.close();
		// re-enable if a newer version of velocity has no longer the
		// cache-grow bug
		// this.velocityContext.remove("class");
	}

	private void writePackage(JPackage jp) throws IOException {
		log.info("prepare for writing " + jp.getClasses().size() + " classes");
		File packageOutdir = new File(this.outdir, jp.getName().replaceAll(
				"\\.", "\\/"));
		log.info("Out dir      " + packageOutdir.getAbsolutePath());
		if (!packageOutdir.exists())
			packageOutdir.mkdirs();

		// package
		assert jp.isConsistent();
		jp.sortClasses();
		for (JClass jc : jp.getClasses()) {
			assert jc != null;
			writeClass(jp,jc, packageOutdir);
		}
		// re-enable if a newer version of velocity has no longer the
		// cache-grow bug
		// this.velocityContext.remove("package");
	}
}
