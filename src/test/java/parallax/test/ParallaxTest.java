package parallax.test;
import org.junit.jupiter.api.Test;

import ai4j.classes.Parallax;

public class ParallaxTest {

	@Test
	public void test() {
		Parallax ai = new Parallax();
		
		ai.trigger(new Object());
	}
	
	public String test(String a) {
		Parallax ai = new Parallax();
		
		ai.trigger(new Object());
		return a;
	}
}
