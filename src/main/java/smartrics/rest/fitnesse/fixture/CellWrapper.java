package smartrics.rest.fitnesse.fixture;

public interface CellWrapper<E> {

	E getWrapped();

	String text();

	void body(String string);

	String body();

	void addToBody(String string);
}
