package de.davidbilge.cpc.dictionary;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.io.Files;

import de.davidbilge.cpc.CPCException;

@Component
public class FlatFileWordListProvider implements WordListProvider {

	@Value("${wordlist.location}")
	private File wordlist;

	@Override
	public List<String> getWordList() {
		try {
			return Files.readLines(wordlist, Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new CPCException("Unable to read word list", e);
		}
	}

}
