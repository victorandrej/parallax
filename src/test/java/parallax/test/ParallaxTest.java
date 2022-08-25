package parallax.test;

import org.junit.jupiter.api.Test;

import parallax.Parallax;
import parallax.annotations.Entry;
import parallax.annotations.Required;
import parallax.annotations.Triggerable;

public class ParallaxTest {


	@Test
	void startTest() {

		Parallax.startApplication(Class1.class, (t,m)->System.out.println(m), 10);
		
	}


}
@Entry
class Class9 {

	public Class9() {
	}

	@Triggerable(async = false)
	public void message() throws InterruptedException {
		Thread.sleep(1000);
		System.out.println("iniciou");
		
	}
	
	@Triggerable(async = false)
	public void messagea() throws InterruptedException {
		System.out.println("iniciou2");

	}

}


class Class1 {
	@Required()
	String message;

	public Class1() {
	}

	@Triggerable()
	public String message() throws InterruptedException {
		System.out.println(message + " class 1");
		return this.message;

	}

}
class Class6 {
	@Required()
	String message;

	public Class6() {
	}

	@Triggerable()
	public String message() throws InterruptedException {
		System.out.println(message + " class 6");
		return this.message;

	}

}

class Class2 {
	@Required(fromClass = Class1.class)
	String message;

	public Class2() {
	}

	@Triggerable
	public String method() {

		System.out.println(message + " class 2");

		return message;
	}

}

class Class3 {
	@Required(fromClass = Class1.class)
	String message;

	@Required(fromClass = Class2.class)
	String message2;

	public Class3() {
	}

	@Triggerable
	public void method() {

		System.out.println(message + "|||||" + message2 + "  class 3");
	}

}

class Class5 {
	@Required(fromClass = Class1.class)
	String message;

	@Required(fromClass = Class2.class)
	String message2;

	public Class5() {
	}

	@Triggerable
	public void method() {

		System.out.println(message + "|||||" + message2 + "  class 5");
	}

}

class Class4 {
	@Required(fromClass = ParallaxTest.class)
	String message;

	public Class4() {
	}

	@Triggerable
	public void method() throws InterruptedException {

		System.out.println(message + " Class4");

	}

}
