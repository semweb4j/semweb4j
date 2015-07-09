How to use semweb4j with Maven
==============================

Semweb4j contains many optional packages. To avoid keeping their version numbers in sync in your POM, include the following BOM (bill of materials) using Maven's `<dependencyManagement>` replacing or assigning `${semweb4j.version}` with the number from Maven Central you would like to use. This information is inherited so it can also be provided in any parent POM of your project.

```xml
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.semweb4j</groupId>
				<artifactId>bom</artifactId>
				<version>${semweb4j.version}</version>
				<scope>import</scope>
				<type>pom</type>
			</dependency>
		</dependencies>
	</dependencyManagement>
```

Afterwards include the needed packages using `<dependencies>` in your POM without worrying about matching versions:

```xml
	<dependencies>
		<dependency>
			<groupId>org.semweb4j</groupId>
			<artifactId>rdfreactor.runtime</artifactId>
		</dependency>
		<dependency>
			<groupId>org.semweb4j</groupId>
			<artifactId>rdf2go.impl.jena</artifactId>
		</dependency>
	</dependencies>
```

