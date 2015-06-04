package hu.kovacsa.szakdolgozat.service.impl;

import hu.kovacsa.szakdolgozat.model.User;
import hu.kovacsa.szakdolgozat.model.UserListWrapper;
import hu.kovacsa.szakdolgozat.model.UserQueryData;
import hu.kovacsa.szakdolgozat.repository.OracleUserRepository;
import hu.kovacsa.szakdolgozat.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link UserService} implementation for Oracle database.
 *
 * @author Aron Kovacs
 */
@Service
@Transactional
public class OracleUserServiceImpl implements UserService {

	@Autowired
	private OracleUserRepository repository;

	@Override
	public UserListWrapper findAll() {
		final UserListWrapper ret = new UserListWrapper();
		final long start = System.nanoTime();
		final List<User> results = repository.findAll();
		final long duration = System.nanoTime() - start;
		ret.setTime(duration);
		ret.setResults(new ArrayList<>(results));
		ret.setCount(ret.getResults().size());
		return ret;
	}

	@Override
	public Object findById(final Long id) {
		final long start = System.nanoTime();
		final Object o = repository.findOne(id);
		final long duration = System.nanoTime() - start;
		return new UserListWrapper(duration, Arrays.asList(o));
	}

	@Override
	public UserListWrapper findByState(final String state) {
		final long start = System.nanoTime();
		final List<User> users = repository.findByState(state);
		final long duration = System.nanoTime() - start;
		return new UserListWrapper(duration, users);
	}

	@Override
	public UserQueryData shortestPathBetween(final Long startUserId, final Long endUserId, final Boolean needProperties) {
		// TODO implement
		return null;
	}

	@Override
	public UserListWrapper countAll() {
		final long start = System.nanoTime();
		final long count = repository.count();
		final long duration = System.nanoTime() - start;
		final UserListWrapper ret = new UserListWrapper();
		ret.setCount((int) count);
		ret.setTime(duration);
		return ret;
	}

	@Override
	public UserListWrapper findRelatedByLevel(final Long id, final Integer to) {
		final long start = System.nanoTime();
		final List<User> users = repository.findRelatedByLevel(to, id);
		final long duration = System.nanoTime() - start;
		return new UserListWrapper(duration, users);
	}

}
