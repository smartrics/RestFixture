package smartrics.rest.fitnesse.fixture.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fit.TypeAdapter;

public abstract class RestDataTypeAdapter extends TypeAdapter{
	private List<String> errors = new ArrayList<String>();

	private Object actual;

	public String toString(){
		return toString(get());
	}

	public void set(Object actual){
		this.actual = actual;
	}

	public Object get(){
		return actual;
	}

	protected void addError(String e){
		errors.add(e);
	}

	public List<String> getErrors(){
		return Collections.unmodifiableList(errors);
	}

}
