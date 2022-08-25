![parallax-effect-banner](https://user-images.githubusercontent.com/67382564/186742000-fdb88e50-8aeb-453e-a35d-2c42a9234bc5.gif)

## a new way to create parallel projects

this project is destined to create a parallel project in a way more simple.


to start a parallax you need to invoke the static method startApplication, they receive a Log interface whos represents the system log, and max threads to be used, if max thread will be set lower to 0 the framework will assume unlimited threads.

![image](https://user-images.githubusercontent.com/67382564/186215265-221ac41e-c158-4015-9fb8-ba199b569145.png)

to finalize the application you need to request the parallax instance and call quit method, the only whay to get the instance is requiring in triggerable class;

![image](https://user-images.githubusercontent.com/67382564/186216573-5b42cddb-c307-4a6e-9b0d-1d6ae51d1afa.png)

a new register is possible too, you just need to call register in parallax instance

![image](https://user-images.githubusercontent.com/67382564/186217467-150c128b-033f-42ee-a152-7632aba8e8c8.png)

calling the trigger method you will trigger an object o anothers class, they just need the object, the type of clone, and who will receives, if the toClass is a empty array all classes will receive the object

![image](https://user-images.githubusercontent.com/67382564/186218468-8ded174f-b05e-4116-8934-4de472a5d830.png)


the 'Entry' annotation  is the start up of application , this will trigger all methods in class when the application starts
![image](https://user-images.githubusercontent.com/67382564/186214369-584f50c9-a4d2-4a78-ad99-625587150c19.png)


the 'cloneType' define who type of clone, the framework will use, can be Shallow,Deep,None the default clone type is Deep

![image](https://user-images.githubusercontent.com/67382564/186212867-cad16b22-d011-4117-8536-b1de980498b1.png)

Singleton annotation creates a singleton instance of the class,  when the instance is requisite, the instance is singleton to a specific class not to all classes

![image](https://user-images.githubusercontent.com/67382564/186206736-84dc15da-b54a-4182-9aac-618a0400e1cc.png)

the exceptions are treated in the same way of the remainder application

![image](https://user-images.githubusercontent.com/67382564/186207647-0f5d4e7c-2e62-4d7a-906d-6a6378aa0006.png)

the parallax not compare the classes, but yes the instance.

![image](https://user-images.githubusercontent.com/67382564/186208362-3f45e9dd-60f1-4f88-993c-6002a47e57af.png)

when fields of a class is completely populed automaticaly the framwork will trigger the specifyed methods.

![image](https://user-images.githubusercontent.com/67382564/186205220-b2937696-031e-403c-89b5-c7c84341befb.png)

fields with no 'Required' annotation is invisible to fremework

![image](https://user-images.githubusercontent.com/67382564/186221708-d345756a-25b0-445c-b5fc-03795ee9c3d3.png)


you can specify who will receive and  who you receive

![image](https://user-images.githubusercontent.com/67382564/186206204-3463713c-e5e7-4658-ba0c-2635fdad0997.png)

 
the 'async' flag when false make a execution of method sync blocking start of another methods in this class until this method be finished

![image](https://user-images.githubusercontent.com/67382564/186211635-0c47931b-479a-4fd8-8ab7-5f87098ddc85.png)

classes with no fields are called aways creating a loop;

![image](https://user-images.githubusercontent.com/67382564/186222837-cdcf0fc5-6f62-4e13-a115-bec8b84d1283.png)


![diagrama](https://user-images.githubusercontent.com/67382564/186203859-3a7e8651-1f90-432c-9ff4-25f8173531bf.png)



https://sourceforge.net/projects/parallax/
