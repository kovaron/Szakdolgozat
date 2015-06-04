package hu.kovacsa.szakdolgozat.rest;

import hu.kovacsa.szakdolgozat.model.UserListWrapper;
import hu.kovacsa.szakdolgozat.model.UserQueryData;
import hu.kovacsa.szakdolgozat.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Aron Kovacs.
 */
@RestController
public class OracleSpringDataController implements Controller {

	private static final Logger LOG = LoggerFactory.getLogger(OracleSpringDataController.class);

	@Autowired
	private UserService userService;

	@Override
	@RequestMapping(value = "findAll")
	public UserListWrapper findAll() {
		final long start = System.nanoTime();
		final UserListWrapper ret = userService.findAll();
		final long duration = System.nanoTime() - start;
		LOG.info("findAll;" + ret.getCount() + ";" + String.valueOf(duration));
		return ret;
	}

	@Override
	@RequestMapping(value = "findById/{id}")
	public Object findById(@PathVariable final Long id) {
		final long start = System.nanoTime();
		final Object ret = userService.findById(id);
		final long duration = System.nanoTime() - start;
		LOG.info("findById;" + String.valueOf(duration));
		return ret;
	}

	@Override
	@RequestMapping(value = "findShortestPathBetween/{startId}-{endId}")
	public UserQueryData findShortestPathBetween(@PathVariable final Long startId, @PathVariable final Long endId,
			final Boolean needProperties) {
		return null;
	}
	// needproperties

	@Override
	@RequestMapping(value = "findByState/{state}")
	public UserListWrapper findByState(@PathVariable final String state) {
		final long start = System.nanoTime();
		final UserListWrapper ret = userService.findByState(state);
		final long duration = System.nanoTime() - start;
		LOG.info("findByState;" + ret.getCount() + ";" + String.valueOf(duration));
		return ret;
	}

	@Override
	@RequestMapping(value = "countAll")
	public UserListWrapper countAll() {
		final long start = System.nanoTime();
		final UserListWrapper ret = userService.countAll();
		final long duration = System.nanoTime() - start;
		LOG.info("countAll;" + ret.getCount() + ";" + String.valueOf(duration));
		return ret;
	}

	@Override
	@RequestMapping(value = "findRelated/{id}/{to}")
	public UserListWrapper findRelatedByLevel(@PathVariable final Long id, @PathVariable final Integer to) {
		final long start = System.nanoTime();
		final UserListWrapper ret = userService.findRelatedByLevel(id, to);
		final long duration = System.nanoTime() - start;
		LOG.info("findRelated;" + ret.getCount() + ";"+ String.valueOf(to) + ";" + String.valueOf(duration));
		return ret;
	}

}
