import java.util.Enumeration;
import java.util.Properties;

public class ListProps {
	public static void main(String[] args) {
		// javax.net.ssl.trustStore
		// Get all system properties
		Properties props = System.getProperties();

		// Enumerate all system properties
		Enumeration enm = props.propertyNames();
		for (; enm.hasMoreElements();) {
			// Get property name
			String propName = (String) enm.nextElement();

			// Get property value
			String propValue = (String) props.get(propName);
			
			System.out.println(propName+" ==> "+propValue);
		}
	}
}
