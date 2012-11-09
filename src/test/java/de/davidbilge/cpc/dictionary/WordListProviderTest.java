package de.davidbilge.cpc.dictionary;

import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration({ "/application-context.xml" })
public class WordListProviderTest extends AbstractJUnit4SpringContextTests {

	@Resource
	private WordListProvider wordListProvider;

	private static final Logger LOG = LoggerFactory.getLogger(WordListProviderTest.class);

	@Test
	public void testWordListProviderLoading() {
		long start = System.currentTimeMillis();
		List<String> wordList = wordListProvider.getWordList();
		long finish = System.currentTimeMillis();

		LOG.debug("Loading word list took " + (finish - start) + " milliseconds");

		Assert.assertNotNull(wordList);
		Assert.assertTrue(wordList.size() > 1000);
	}
}
