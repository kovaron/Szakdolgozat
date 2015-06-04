package hu.kovacsa.szakdolgozat.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Users-by-state query data wrapper
 *
 * @author kovaron
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserListWrapper {

	private int count;
	private long time;
	private List<Object> results;

	public UserListWrapper() {

	}

	public UserListWrapper(final long time, final List results) {
		this.time = time;
		this.results = results;
		count = results == null ? 0 : results.size();
	}

}
