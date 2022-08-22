package parallax.test;

import org.junit.jupiter.api.Test;

import parallax.Parallax;
import parallax.annotations.Required;
import parallax.annotations.Triggerable;
import parallax.util.cloner.CloneType;

public class ParallaxTest {

	@Test
	void test() {
		Parallax parallax = new Parallax((type, error) -> {
			System.out.println(type.name() + ": " + error);
		},-1);

		parallax.register(Class1.class);
		parallax.register(Class2.class);
		parallax.register(Class3.class);
		parallax.register(Class4.class);
		parallax.register(Class5.class);
		parallax.register(Class6.class);
		parallax.trigger("foi KKK", CloneType.NONE, new Class<?>[] { Object.class });
		parallax.trigger("foi awdawd", CloneType.NONE,new Class<?>[] { Object.class });
		parallax.trigger("foi adadsawda", CloneType.NONE,new Class<?>[] { Object.class });
		parallax.trigger("foi awdawdwaadawd", CloneType.NONE, new Class<?>[] { Object.class });
		parallax.start();
	}
	


}

class Class1 {
	@Required()
	String message;

	public Class1() {
	}

	@Triggerable()
	public String message() throws InterruptedException {
		System.out.println(message +" class 1");
		return this.message ;

	}

}

class Class6 {
	@Required()
	String message;

	public Class6() {
	}

	@Triggerable()
	public String message() throws InterruptedException {
		System.out.println(message +" class 6");
		return this.message ;

	}

}


class Class2 {
	@Required(fromClass = Class1.class)
	String message;

	public Class2() {
	}

	@Triggerable
	public String method() {

		System.out.println(message  + " class 2");
		
		return message ;
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

		System.out.println(message + "|||||"+ message2 +  "  class 3");
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

		System.out.println(message + "|||||"+ message2 +  "  class 5");
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
