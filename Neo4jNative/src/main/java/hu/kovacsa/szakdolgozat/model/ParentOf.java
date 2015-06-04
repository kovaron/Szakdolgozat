package hu.kovacsa.szakdolgozat.model;

import org.neo4j.graphdb.RelationshipType;

public class ParentOf implements RelationshipType {

	public static final String PARENT_OF = "PARENT_OF";

	@Override
	public String name() {
		return PARENT_OF;
	}

}
