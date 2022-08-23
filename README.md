# parallax
a new way to create parallel projects

this project is destined to create a parallel project in a way more simple.

when fields of a class is completely populed automaticaly the framwork will trigger the specifyed methods.

![image](https://user-images.githubusercontent.com/67382564/186205220-b2937696-031e-403c-89b5-c7c84341befb.png)

you can specify who will receive and  who you receive

![image](https://user-images.githubusercontent.com/67382564/186206204-3463713c-e5e7-4658-ba0c-2635fdad0997.png)

when a field is populed and the 'trigger' flag is equals  true, autoamatically this class will be triggered  ignoring unpopuleds fields

![image](https://user-images.githubusercontent.com/67382564/186209596-eee350d9-f093-4b55-b1d0-2cd541b745b9.png)

specifying 'methodTrigger' only the method with this order will be triggered

![image](https://user-images.githubusercontent.com/67382564/186209910-7438ec89-c3d5-4594-8ce5-847ee8085214.png)

the 'async' flag when false make a execution of method sync blocking start of another methods in this class until this method be finished

![image](https://user-images.githubusercontent.com/67382564/186211635-0c47931b-479a-4fd8-8ab7-5f87098ddc85.png)

the 'cloneType' define who type of clone, the framework will use, can be Shallow,Deep,None

![image](https://user-images.githubusercontent.com/67382564/186212867-cad16b22-d011-4117-8536-b1de980498b1.png)

all classes are recreated before trigger, but you can create a singleton instance of class

![image](https://user-images.githubusercontent.com/67382564/186206736-84dc15da-b54a-4182-9aac-618a0400e1cc.png)

the exceptions are treated in the same way

![image](https://user-images.githubusercontent.com/67382564/186207647-0f5d4e7c-2e62-4d7a-906d-6a6378aa0006.png)

the parallax not compare the classes, but yes the instance.

![image](https://user-images.githubusercontent.com/67382564/186208362-3f45e9dd-60f1-4f88-993c-6002a47e57af.png)


![diagrama](https://user-images.githubusercontent.com/67382564/186203859-3a7e8651-1f90-432c-9ff4-25f8173531bf.png)



https://sourceforge.net/projects/parallax/
