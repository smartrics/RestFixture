package smartrics.rest.fitnesse.fixture;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SlimRow implements RowWrapper<String> {

    private static Log LOG = LogFactory.getLog(SlimRow.class);

	private final List<CellWrapper<String>> row;

    public SlimRow(List<String> rawRow) {
        this.row = new ArrayList<CellWrapper<String>>();
        for (String r : rawRow) {
            this.row.add(new SlimCell(r));
		}
	}

	public CellWrapper<String> getCell(int c) {
        if (c < this.row.size()) {
            return this.row.get(c);
		}
		return null;
	}

    public List<String> asList() {
        List<String> ret = new ArrayList<String>();
        for (CellWrapper<String> w : row) {
            ret.add(w.body());
        }
        return ret;
    }

}
