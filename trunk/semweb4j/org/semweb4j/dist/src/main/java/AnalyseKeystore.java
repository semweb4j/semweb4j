import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;

public class AnalyseKeystore {

	public static void main(String[] args) throws KeyStoreException {
		KeyStore ks = KeyStore.getInstance(
				KeyStore.getDefaultType());
		Enumeration<String> enu = ks.aliases();
		while (enu.hasMoreElements()) {
			String s = enu.nextElement();
			System.out.println("Contains alias " + s);
		}
	}
}
