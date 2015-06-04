package hu.kovacsa.szakdolgozat.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.GraphProperty;
import org.springframework.data.neo4j.annotation.Indexed;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@NodeEntity
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@GraphId
	@JsonIgnore
	private Long id;

	@GraphProperty(propertyName = "user_id")
	private Long userId;

	@GraphProperty(propertyName = "parent_id")
	private Long parentId;

	@GraphProperty(propertyName = "date_of_birth")
	private Long dateOfBirth;

	@GraphProperty(propertyName = "last_year_income")
	private Integer lastYearIncome;

	@Indexed(fieldName = "state")
	private String state;

	@RelatedTo(type = ParentOf.PARENT_OF)
	private Iterable<User> related;

	@Override
	public String toString() {
		return "User [id=" + id + "]";
	}

}
