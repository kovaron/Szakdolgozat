package hu.kovacsa.szakdolgozat.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User implements Serializable {

	public static final String LABEL_NAME = "User";
	public static final String USER_ID = "USER_ID";
	public static final String PARENT_ID = "PARENT_ID";
	public static final String DATE_OF_BIRTH = "DATE_OF_BIRTH";
	public static final String LAST_YEAR_INCOME = "LAST_YEAR_INCOME";
	public static final String STATE = "STATE";
	public static final String RELATION = "ParentOf";

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private String id;

	private Long userId;

	private Long parentId;

	private Object dateOfBirth;

	private Object lastYearIncome;

	private String state;

	@Override
	public String toString() {
		return "User [id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (dateOfBirth == null ? 0 : dateOfBirth.hashCode());
		result = prime * result + (lastYearIncome == null ? 0 : lastYearIncome.hashCode());
		result = prime * result + (parentId == null ? 0 : parentId.hashCode());
		result = prime * result + (state == null ? 0 : state.hashCode());
		result = prime * result + (userId == null ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final User other = (User) obj;
		if (dateOfBirth == null) {
			if (other.dateOfBirth != null) {
				return false;
			}
		} else if (!dateOfBirth.equals(other.dateOfBirth)) {
			return false;
		}
		if (lastYearIncome == null) {
			if (other.lastYearIncome != null) {
				return false;
			}
		} else if (!lastYearIncome.equals(other.lastYearIncome)) {
			return false;
		}
		if (parentId == null) {
			if (other.parentId != null) {
				return false;
			}
		} else if (!parentId.equals(other.parentId)) {
			return false;
		}
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		return true;
	}

}
