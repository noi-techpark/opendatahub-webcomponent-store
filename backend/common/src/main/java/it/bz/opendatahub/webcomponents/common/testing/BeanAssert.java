// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.common.testing;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Arrays;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class BeanAssert extends AbstractAssert<BeanAssert, Object> {
	public BeanAssert(Object actual) {
		super(actual, BeanAssert.class);
	}

	public static BeanAssert assertThat(Object actual) {
		return new BeanAssert(actual);
	}

	public BeanAssert hasCopiedAllProperties(Object source, String... exceptions) {
		isNotNull();

		var properties = getPropertyListArray(source.getClass(), exceptions);

		Assertions.assertThat(actual).isEqualToComparingOnlyGivenFields(source, properties);

		return this;
	}

	private String[] getPropertyListArray(Class<?> c, String... exceptions) {
		var result = new ArrayList<String>();

		var exceptionsList = Arrays.asList(exceptions);

		var fields = c.getDeclaredFields();
		for(var field : fields) {
			if(!exceptionsList.contains(field.getName())) {
				result.add(field.getName());
			}
		}

		return result.toArray(new String[0]);
	}
}
