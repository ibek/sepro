package org.jboss.sepro.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-06-25T14:52:34.118+0200")
@StaticMetamodel(MAcronym.class)
public class MAcronym_ {
	public static volatile SingularAttribute<MAcronym, Long> id;
	public static volatile SingularAttribute<MAcronym, String> abbreviation;
	public static volatile SingularAttribute<MAcronym, String> meaning;
	public static volatile SingularAttribute<MAcronym, String> owner;
}
