package org.ontoware.aifbcommons.macro;

import java.io.IOException;

import org.ontoware.aifbcommons.xml.XmlOut;

/**
 * Records method calls as XML
 * @author voelkel
 *
 */
public class MacroRecorder {
	
	private XmlOut xo;

	public MacroRecorder( XmlOut xo ) throws IOException {
		this.xo = xo;
		xo.open("macro");
		xo.open("block");
		xo.attribute("count",""+(recordCount/100));
	}

	int recordCount = 1;
	
	/**
	 * <pre>
	 * 
	 * <command>
	 *   <name>method name</name>
	 *   <parameter>
	 *      <type>String</type>
	 *      <value>Hello World</value>
	 *   </parameter>
	 *   <parameter>
	 *      <type>MyValue</type>
	 *      <value>the value to String</value>
	 *   </parameter>
	 * </command>     
	 * 
	 * </pre>
	 * @param command
	 * @param params
	 */
	public void record(String command, Object[] params) {
		try {
			if (recordCount % 100 == 0) {
				xo.close("block");
				xo.open("block");
				xo.attribute("count",""+(recordCount/100));
			}
			xo.open("command");
			xo.open("name");
			xo.content(command);
			xo.close("name");
			if (params != null)
				for (Object param : params) {
					xo.open("parameter");
					xo.open("type");
					xo.content(param.getClass().getName());
					xo.close("type");
					xo.open("value");
					xo.content(param.toString());
					xo.close("value");
					xo.close("parameter");
				}

			xo.close("command");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		recordCount++;
	}
	
	public void flush() throws IOException {
		xo.flush();
	}

	public void close() throws IOException {
		xo.close("block");
		xo.close("macro");
		flush();
		xo.close();
	}
}
