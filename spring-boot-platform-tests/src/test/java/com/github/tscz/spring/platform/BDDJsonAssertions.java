package com.github.tscz.spring.platform;

import net.javacrumbs.jsonunit.assertj.JsonAssert.ConfigurableJsonAssert;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;

public class BDDJsonAssertions {

	public static final BDDJsonAssertions and = new BDDJsonAssertions();

	public static ConfigurableJsonAssert then(Object actual) {
		return JsonAssertions.assertThatJson(actual);
	}

}
