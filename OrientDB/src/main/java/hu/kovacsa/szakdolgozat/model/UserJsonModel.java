package hu.kovacsa.szakdolgozat.model;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserJsonModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	@JsonProperty("ID")
	private Long userId;

	@JsonProperty("PARENT_ID")
	private Long parentId;

	@JsonProperty("DATE_OF_BIRTH")
	private Long dateOfBirth;

	@JsonProperty("LAST_YEAR_INCOME")
	private Long lastYearIncome;

	@JsonProperty("STATE")
	private String state;

	@JsonProperty("out_ParentOf")
	private List<OutParentOf> children = Lists.newArrayList();

	public UserJsonModel() {
	}

	public UserJsonModel(final String id){
		this.id = id;
	}

	@Override
	public String toString() {
		return "UserJsonModel [\"id\"=" + id + ", \"userId\"=" + userId + ", \"parentId\"=" + parentId + ", \"dateOfBirth\"="
				+ dateOfBirth + ", \"lastYearIncome=\"" + lastYearIncome + ", \"state\"=" + state + ", \"children\"=" + children
				+ "]";
	}

	@Getter @Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	@SuppressWarnings("unused")
	public static class OutParentOf implements Serializable {

		private static final long serialVersionUID = 1L;

		public OutParentOf() {
		}

		public OutParentOf(final String s) {
		}


		public OutParentOf(final List<UserJsonModel> model) {
			nodes = model;
		}

		@JsonProperty("in")
		private List<UserJsonModel> nodes = Lists.newArrayList();

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			for(final UserJsonModel u : nodes){
				sb.append(u);
			}
			return sb.toString();
		}

	}

}
