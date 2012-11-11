package de.davidbilge.cpc.dictionary;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Component
public class SimpleCachedDictionary implements Dictionary, Iterable<String> {

	@Resource
	private WordListProvider wordListProvider;

	private List<String> words;

	Random rnd = new Random();

	@PostConstruct
	public void init() {
		words = wordListProvider.getWordList();
	}

	@Override
	public String randomWord() {
		return Iterables.get(words, rnd.nextInt(words.size()));
	}

	@Override
	@Cacheable("filteredWords")
	public List<String> filter(String regex) {
		final Pattern pattern = Pattern.compile(regex);

		return Lists.newArrayList(Iterables.filter(words, new Predicate<String>() {
			@Override
			public boolean apply(String word) {
				return pattern.matcher(word).matches();
			}
		}));
	}

	@Override
	public Iterator<String> iterator() {
		return words.iterator();
	}
}
