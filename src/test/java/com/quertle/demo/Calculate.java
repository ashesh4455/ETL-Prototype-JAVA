package com.quertle.demo;

public interface Calculate {

	public Integer calculate(Integer a, Integer b);

	default Double multiplyToPI(Integer a) {
		return a * Math.PI;
	}

}
