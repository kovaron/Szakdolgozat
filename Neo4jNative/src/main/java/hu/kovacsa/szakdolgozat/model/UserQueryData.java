package hu.kovacsa.szakdolgozat.model;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Shortest Path representation. Created by Aron Kovacs.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserQueryData {

	private Long searchTime;
	private Integer length;
	private Object start;
	private Object end;
	private Collection<Object> nodes;

	public Long getSearchTime() {
		return searchTime;
	}

	public void setSearchTime(final Long searchTime) {
		this.searchTime = searchTime;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(final Integer length) {
		this.length = length;
	}

	public Object getStart() {
		return start;
	}

	public void setStart(final Object start) {
		this.start = start;
	}

	public Object getEnd() {
		return end;
	}

	public void setEnd(final Object end) {
		this.end = end;
	}

	public Collection<Object> getNodes() {
		return nodes;
	}

	public void setNodes(final Collection<Object> nodes) {
		this.nodes = nodes;
	}

}
