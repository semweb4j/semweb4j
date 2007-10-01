/*
 * LICENSE INFORMATION
 * Copyright 2005-2007 by FZI (http://www.fzi.de).
 * Licensed under a BSD license (http://www.opensource.org/licenses/bsd-license.php)
 * <OWNER> = Max Völkel
 * <ORGANIZATION> = FZI Forschungszentrum Informatik Karlsruhe, Karlsruhe, Germany
 * <YEAR> = 2007
 * 
 * Project information at http://semweb4j.org/rdf2go
 */
package org.ontoware.rdf2go.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.ModelSet;
import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.Syntax;

/**
 * Find a lot of convenience functions that are slow but nice and didn't make it
 * to the Model API.
 * 
 * @author voelkel
 * 
 */
public class ModelUtils {

	/**
	 * Count statements in iterable 'it'
	 * 
	 * @param it
	 *            the iterable
	 * @return the size
	 */
	public static int size(Iterable<?> it) {
		int count = 0;
		Iterator<?> i = it.iterator();
		while (i.hasNext()) {
			i.next();
			count++;
		}
		return count;
	}

	/**
	 * Count statements in iterator 'it'
	 * 
	 * @param i
	 *            the iterator
	 * @return the size
	 */
	public static int size(Iterator<?> i) {
		int count = 0;
		while (i.hasNext()) {
			i.next();
			count++;
		}
		return count;
	}

	/**
	 * Copy data from the source modelset to the target modelset. Iterates
	 * through all named models of the source and adds each to the target. If a
	 * named graph already exists in the target, the data will be added to it,
	 * target models will not be replaced.
	 * 
	 * @param source
	 *            the source, data from here is taken
	 * @param target
	 *            the target, data will be put here.
	 * @throws ModelRuntimeException
	 *             if the copying process has an error
	 */
	public static void copy(ModelSet source, ModelSet target)
			throws ModelRuntimeException {
		for (Iterator<? extends Model> i = source.getModels(); i.hasNext();) {
			Model m = i.next();
			m.open();
			Model tm = target.getModel(m.getContextURI());
			tm.open();
			tm.addAll(m.iterator());
		}
		// copy default model
		Model m = source.getDefaultModel();
		m.open();
		Model tm = target.getDefaultModel();
		tm.open();
		tm.addAll(m.iterator());
	}

	/**
	 * Remove data that is listed in the source modelset from the target
	 * modelset. Iterates through all named models of the source and removes the
	 * listed triples from the target.
	 * 
	 * @param source
	 *            the source, data from here is evaluated
	 * @param target
	 *            the target, data contained in source and target are removed
	 *            from here
	 * @throws ModelRuntimeException
	 *             if the deleting process has an error
	 */
	public static void removeFrom(ModelSet source, ModelSet target)
			throws ModelRuntimeException {
		// remove
		ClosableIterator<? extends Model> it = source.getModels();

		while (it.hasNext()) {
			Model m = it.next();
			ClosableIterator<Statement> modelIt = m.iterator();
			target.getModel(m.getContextURI()).removeAll(modelIt);
			modelIt.close();
		}
		it.close();
		// remove stuff contained in default model
		ClosableIterator<Statement> modelIt = source.getDefaultModel()
				.iterator();
		target.getDefaultModel().removeAll(modelIt);
		modelIt.close();
	}

	/**
	 * @param a
	 * @param b
	 * @param result
	 * @return the result after executing the intersection
	 * @throws ModelRuntimeException
	 */
	public static Model intersection(Model a, Model b, Model result)
			throws ModelRuntimeException {
		for (Statement s : a) {
			if (b.contains(s)) {
				result.addStatement(s);
			}
		}
		return result;
	}

	/**
	 * @param a
	 * @param b
	 * @param result
	 * @return the result after executing the union
	 * @throws ModelRuntimeException
	 */
	public static Model union(Model a, Model b, Model result)
			throws ModelRuntimeException {
		ClosableIterator<Statement> it = a.iterator();
		result.addAll(it);
		it.close();
		it = b.iterator();
		result.addAll(it);
		it.close();
		return result;
	}

	/**
	 * @param a
	 * @param b
	 * @param result
	 * @return the result after executing the complement of b in a (a\b)
	 * @throws ModelRuntimeException
	 */
	public static Model complement(Model a, Model b, Model result)
			throws ModelRuntimeException {
		ClosableIterator<Statement> it = a.iterator();
		result.addAll(it);
		it.close();
		it = b.iterator();
		result.removeAll(it);
		it.close();
		return result;
	}

	/**
	 * Convert the input file, interpreted in the inputSyntax to outputFile, in
	 * outputSyntax.
	 * 
	 * @param in
	 *            input File
	 * @param inSyntax
	 * @param out
	 *            output File
	 * @param outSyntax
	 * @throws FileNotFoundException
	 */
	public static void convert(File in, Syntax inSyntax, File out,
			Syntax outSyntax) throws FileNotFoundException {
		if (!in.exists())
			throw new FileNotFoundException("Input file "
					+ in.getAbsolutePath() + " not found");

		if (!out.getParentFile().exists())
			out.getParentFile().mkdirs();

		Model m = RDF2Go.getModelFactory().createModel();
		m.open();
		FileReader fr = null;
		FileWriter fw = null;
		try {
			fr = new FileReader(in);
			m.readFrom(fr, inSyntax);
			fw = new FileWriter(out);
			m.writeTo(fw, outSyntax);

		} catch (ModelRuntimeException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (fr != null)
				try {
					fr.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			if (fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		}
		m.close();
	}

	/**
	 * @param in
	 * @param inSyntax
	 * @return a model with the content from 'in'. Model is open.
	 * @throws ModelRuntimeException
	 * @throws IOException
	 */
	public static Model loadFromFile(File in, Syntax inSyntax)
			throws ModelRuntimeException, IOException {
		Model model = RDF2Go.getModelFactory().createModel();
		model.open();
		FileInputStream fin = new FileInputStream(in);
		model.readFrom(fin, inSyntax);
		return model;
	}

	public static void writeToFile(Model model, File outFile, Syntax outSyntax)
			throws ModelRuntimeException, IOException {
		FileOutputStream fout = new FileOutputStream(outFile);
		model.writeTo(fout, outSyntax);
	}

	/**
	 * Merge all input files into one model and export to outfile in output
	 * syntax
	 * 
	 * @throws IOException
	 * @throws ModelRuntimeException
	 */
	public static void convert(File[] inFiles, Syntax[] inSyntax, File out,
			Syntax outSyntax) throws ModelRuntimeException, IOException {
		Model merged = RDF2Go.getModelFactory().createModel();

		for (int i = 0; i < inFiles.length; i++) {
			Model inModel = loadFromFile(inFiles[i], inSyntax[i]);
			ClosableIterator<Statement> it = inModel.iterator();
			merged.addAll(it);
			it.close();
		}
		writeToFile(merged, out, outSyntax);
	}
}
