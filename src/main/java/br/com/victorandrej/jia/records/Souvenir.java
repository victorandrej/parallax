package br.com.victorandrej.jia.records;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import br.com.victorandrej.jia.classes.Primitive;

public record Souvenir(Constructor<?> constructor,Method method,Primitive primitive,Object key) {}
	