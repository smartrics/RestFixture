package smartrics.rest.fitnesse.fixture;


public class SlimCell implements CellWrapper<String> {

    private String cell;
    private String modCell;

	public SlimCell(String c) {
        this.cell = c;
        this.modCell = c;
	}

	@Override
	public String text() {
        return cell;
	}

	@Override
	public void body(String string) {
        modCell = string;
	}

	@Override
	public String body() {
        return modCell;
	}

	@Override
	public void addToBody(String string) {
        modCell = modCell + string;
	}

	@Override
    public String getWrapped() {
		return cell;
	}
}
