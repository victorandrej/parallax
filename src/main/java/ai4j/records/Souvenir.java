package ai4j.records;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import ai4j.classes.Primitive;

public record Souvenir(Constructor<?> constructor,Method method,Primitive primitive,Object key) {}
	