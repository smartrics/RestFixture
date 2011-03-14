package smartrics.rest.fitnesse.fixture;

import smartrics.rest.fitnesse.fixture.support.StringTypeAdapter;

public interface CellFormatter<E> {

	void exception(CellWrapper<E> cellWrapper, Throwable exception);

	void check(CellWrapper<E> valueCell, StringTypeAdapter adapter);

	String label(String string);

	void wrong(CellWrapper<E> expected);

	void wrong(CellWrapper<E> expected, String actual);

	void right(CellWrapper<E> expected);

	String gray(String string);

}
