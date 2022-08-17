package parallax.test;

import org.junit.jupiter.api.Test;

import ai4j.annotations.CloneType;
import ai4j.classes.Cloner;

import org.junit.Assert;

public class ClonerTest {
	 @Test
	 public void deepCloningTest() {
		 Test1 principal = new Test1();
		 
		 Test1 clone = Cloner.clone(principal, CloneType.DEEP);
		 
		 Assert.assertEquals(principal, clone);
	 }

	class Test1 extends Test2{

		int x =1;
		String z = "adad";
		Test3 teste3 = new Test3();
	}
	
	class Test2{
		int ada =123123;
		String nome = "dawdawdawd";
				
	}
	
	class Test3{
		String jogo ="minecraft";
		String idade = "19";
				
	}
}


