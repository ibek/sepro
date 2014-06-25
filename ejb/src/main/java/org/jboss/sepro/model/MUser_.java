package org.jboss.sepro.model;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-06-25T14:52:34.227+0200")
@StaticMetamodel(MUser.class)
public class MUser_ {
	public static volatile SingularAttribute<MUser, Long> id;
	public static volatile SingularAttribute<MUser, String> username;
	public static volatile SingularAttribute<MUser, String> password;
	public static volatile SingularAttribute<MUser, String> plainPassword;
}
