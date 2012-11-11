package de.davidbilge.cpc.dictionary;

import java.util.List;

public interface Dictionary {

	String randomWord();

	List<String> filter(String regex);

}
