package smartrics.rest.fitnesse.fixture;

import smartrics.rest.fitnesse.fixture.support.RestDataTypeAdapter;
import smartrics.rest.fitnesse.fixture.support.StringTypeAdapter;

public interface CellFormatter<E> {

	void exception(CellWrapper<E> cellWrapper, Throwable exception);

	void check(CellWrapper<E> valueCell, StringTypeAdapter adapter);

	String label(String string);

    void wrong(CellWrapper<E> expected, RestDataTypeAdapter typeAdapter);

	void wrong(CellWrapper<E> expected, String actual, RestDataTypeAdapter typeAdapter);

	void right(CellWrapper<E> expected, RestDataTypeAdapter typeAdapter);

	String gray(String string);

    void asLink(CellWrapper<E> cell, String link, String text);

    void setDisplayActual(boolean displayActual);
}
