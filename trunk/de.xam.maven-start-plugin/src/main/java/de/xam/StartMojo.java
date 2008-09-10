package de.xam;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author voelkel
 * @goal start
 */
public class StartMojo extends AbstractMojo {

	/**
	 * Be verbose in the debug log-level?
	 * 
	 * @parameter expression="${start.mainclass}"
	 */
	private String mainclass;

	/** @parameter expression="${project}" */
	private org.apache.maven.project.MavenProject mavenProject;

	public void execute() throws MojoExecutionException {
		// TODO Auto-generated method stub
		System.out.println("I ran! Mainclass = " + this.mainclass);

		File windowsBatchFile = new File(this.mavenProject.getBasedir(),
				"run_win.bat");
		windowsBatchFile.delete();
		try {
			FileWriter fileWriter = new FileWriter(windowsBatchFile);
			fileWriter.write("java -cp ");
			try {
				// TODO add current artifact, too
				for (Object o : this.mavenProject.getCompileClasspathElements()) {
					String classPathElement = (String) o;
					fileWriter.write(classPathElement + ";");
				}
			} catch (DependencyResolutionRequiredException e) {
				throw new MojoExecutionException("unknown cause",e);
			}
			fileWriter.write(" "+this.mainclass);
			fileWriter.close();
		} catch (IOException e) {
			throw new MojoExecutionException("I/O",e);
		}
	}

}
