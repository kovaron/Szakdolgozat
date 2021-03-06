package hu.kovacsa.szakdolgozat.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "users_13")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NamedNativeQuery(name = "User.findRelatedByLevel", query = "select * from users_5 where LEVEL <= ?1 start with user_id = ?2 connect by prior user_id = parent_id")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "USER_ID", nullable = true)
	@JsonProperty("USER_ID")
	private Long id;

	@Column(name = "PARENT_ID", nullable = true)
	@JsonProperty("PARENT_ID")
	private Long parentId;

	@Column(name = "DATE_OF_BIRTH", nullable = true)
	@JsonProperty("DATE_OF_BIRTH")
	private Long dateOfBirth;

	@Column(name = "LAST_YEAR_INCOME", nullable = true)
	@JsonProperty("LAST_YEAR_INCOME")
	private Integer lastYearIncome;

	@Column(name = "STATE", nullable = true)
	@JsonProperty("STATE")
	private String state;

	/**
	 * Gets the date of birth as java.util.Date
	 *
	 * @return
	 */
	public Date getDateOfBirth() {
		if (dateOfBirth != null) {
			return new Date(dateOfBirth);
		}
		return null;
	}

	@Override
	public String toString() {
		return "User [id=" + id + "]";
	}

}
