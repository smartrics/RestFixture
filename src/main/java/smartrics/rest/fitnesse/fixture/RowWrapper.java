package smartrics.rest.fitnesse.fixture;

public interface RowWrapper<E> {

	CellWrapper<E> getCell(int c);

}
