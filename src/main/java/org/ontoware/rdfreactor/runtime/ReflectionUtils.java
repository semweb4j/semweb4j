package org.ontoware.rdfreactor.runtime;

public class ReflectionUtils {

	/**
	 * @param classA
	 * @param classB
	 * @return true if classA is equal to or has as a direct or indirect
	 *         superclass classB. Or if it directely or indirectly implements B.
	 */
	public static boolean hasSuperClass(Class<?> classA, Class<?> classB) {
		// end recursion
		if (classA.equals(classB))
			return true;

		boolean superclass = false;

		// check superclass
		Class superClass = classA.getSuperclass();
		if (superClass != null) {
			if (superClass.equals(classB))
				superclass = true;
			else
				// recursion
				superclass = hasSuperClass(superClass, classB);
		}

		if (!superclass) {
			// check interfaces, recursively
			Class[] interfaces = classA.getInterfaces();
			for (Class interfaze : interfaces) {
				if (interfaze.equals(classB))
					return true;
				else {
					return hasSuperClass(interfaze, classB);
				}
			}
		}

		return superclass;

	}
}
