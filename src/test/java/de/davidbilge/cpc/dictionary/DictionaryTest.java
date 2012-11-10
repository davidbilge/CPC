package de.davidbilge.cpc.dictionary;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@ContextConfiguration({ "/application-context.xml" })
public class DictionaryTest extends AbstractJUnit4SpringContextTests {

	@Resource
	private Dictionary dictionary;

	@Test
	public void testRandomWord() {
		String randomWord = dictionary.randomWord();

		Assert.assertNotNull(randomWord);
		Assert.assertFalse(StringUtils.isEmpty(randomWord));
	}

	@Test
	public void testFilter() {
		Iterable<String> filtered = dictionary.filter("...e");

		Assert.assertNotNull(filtered);
		Assert.assertTrue(filtered.iterator().hasNext());

		boolean conditionsSatisfied = Iterables.all(filtered, new Predicate<String>() {
			@Override
			public boolean apply(String word) {
				return word.length() == 4 && word.endsWith("e");
			}
		});

		Assert.assertTrue(conditionsSatisfied);
	}
}
