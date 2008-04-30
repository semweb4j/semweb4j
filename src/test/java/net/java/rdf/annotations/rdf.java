package net.java.rdf.annotations;


import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation to annotate classes, methods and arguments with a uri which is meant to be interpreted
 * as:<ul>
 * <li>for a class: the rdf:type of the class
 * <li>for a method: the relation between an instance of the class and the argument(s) of the method. If the
 * method takes a number of argument then it relates the ordered list of those arguments.
 * <li>for an argument: the name of a relation between an instance of a class and that value of the argument
 * <li>for a field: the field is thought of as a relation between an instance of the class and the value of the field
 * </ul>
 * The value must a full URI, by default it is owl:sameAs ie, the identity relation.
 * If it is the inverse of the relation that is intended, set inverse to true.
 *
 * Created by Henry Story
 * Date: Aug 28, 2005
 * Time: 11:51:43 PM
 */
@Retention(RUNTIME)
@Target({TYPE,CONSTRUCTOR, METHOD, FIELD, PARAMETER})
public @interface rdf {
    /** help compose xsd literal ranges */
    String xsd="http://www.w3.org/2001/XMLSchema#";

    /** the String must be a full URI */
    String value() default "http://www.w3.org/2002/07/owl#sameAs";

    /**
     * @return true if it is the inverse of the named relation (value()) that this relation represents
     */
    boolean inverse() default false;

    /**
     * @return the range uri (for literal types). Value is "" for default.
     */
    String range() default "";

}