package de.davidbilge.cpc.dictionary;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;

import de.davidbilge.cpc.CPCException;

@Component
public class FlatFileWordListProvider implements WordListProvider {

	@Value("${wordlist.location}")
	private File wordlist;

	@Override
	public List<String> getWordList() {
		try {
			List<String> linesFromFile = Files.readLines(wordlist, Charset.forName("UTF-8"));

			return ImmutableList.copyOf(Iterables.transform(linesFromFile, new Function<String, String>() {
				@Override
				public String apply(String input) {
					input = StringUtils.lowerCase(input);
					input = StringUtils.replaceChars(input, 'ä', 'a');
					input = StringUtils.replaceChars(input, 'ö', 'a');
					input = StringUtils.replaceChars(input, 'ü', 'a');
					input = StringUtils.replaceChars(input, 'ß', 'a');
					return input;
				}
			}));

		} catch (IOException e) {
			throw new CPCException("Unable to read word list", e);
		}
	}

}
