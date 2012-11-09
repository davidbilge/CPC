package de.davidbilge.cpc.dictionary;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

@Component
public class Dictionary {

	@Resource
	private WordListProvider wordListProvider;

	private List<String> words;

	Random rnd = new Random();

	@PostConstruct
	public void init() {
		words = wordListProvider.getWordList();
	}

	public String randomWord() {
		return Iterables.get(words, rnd.nextInt(words.size()));
	}

	public Collection<String> filter(String regex) {
		final Pattern pattern = Pattern.compile(regex);

		return Collections2.filter(words, new Predicate<String>() {
			@Override
			public boolean apply(String word) {
				return pattern.matcher(word).matches();
			}
		});
	}
}
