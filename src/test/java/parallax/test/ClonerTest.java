package parallax.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import parallax.util.cloner.CloneType;
import parallax.util.cloner.Cloner;

public class ClonerTest {
	 @Test
	 public void deepCloningTest() {
		 Test1 principal = new Test1();
		 
		 Test1 clone = Cloner.clone(principal, CloneType.DEEP);
		 
		 Assertions.assertNotEquals(principal.teste3, clone.teste3);
	 }
	 
	 @Test
	 public void shalowCloningTest() {
		 Test1 principal = new Test1();
		 
		 Test1 clone = Cloner.clone(principal, CloneType.SHALLOW);
		 
		 Assertions.assertEquals(principal.teste3, clone.teste3);
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


