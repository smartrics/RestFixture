package smartrics.rest.fitnesse.fixture.support;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

public class RestTypeAdapterTest {
	RestDataTypeAdapter adapter = new RestDataTypeAdapter(){

	};

	@Test
	public void mustAllowStoringOfTheInstanceOfTheCellContent(){
		adapter.set("data");
		assertEquals("data", adapter.get());
	}


	@Test
	public void mustReturnStringRepresentationOfTheCellContent(){
		adapter.set("");
		assertEquals("blank", adapter.toString());
		adapter.set(null);
		assertEquals("null", adapter.toString());
		adapter.set(new Object(){
			public String toString(){
				return "x45";
			}
		});
		assertEquals("x45", adapter.toString());
	}

	@Test
	public void mustAllowStoringOfErrors(){
		adapter.addError("error");
		assertEquals("error", adapter.getErrors().get(0));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void mustDisallowAddToTheErrorsList(){
		adapter.getErrors().add("i am not allowed");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void mustDisallowRemoveFromTheErrorsList(){
		adapter.addError("error");
		adapter.getErrors().remove(0);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void mustDisallowOpsOnTheErrorsList(){
		adapter.addError("error1");
		adapter.addError("error2");
		Collections.sort(adapter.getErrors());
	}

}
