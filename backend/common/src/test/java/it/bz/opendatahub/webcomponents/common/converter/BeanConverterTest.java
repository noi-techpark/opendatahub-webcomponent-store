// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.common.converter;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class BeanConverterTest {
	@Getter
	@Setter
	public static class TypeA {
		private String propertyA;

		private double propertyB;

		private Boolean propertyC;

		private int propertyD;

		private Long someOtherA;

		private float onlyHere;
	}

	@Getter
	@Setter
	public static class TypeB {
		private String propertyA;

		private double propertyB;

		private Boolean propertyC;

		private int propertyD;

		private String someOtherA;

		private float onlyThere;
	}

	static class TestConverter extends BeanConverter<TypeA, TypeB> {

	}

	private static final TestConverter CONVERTER = new TestConverter();

	@Test
	void convertObjectReturnsNullOnNull() {
		assertThat(CONVERTER.convert((TypeA) null)).isNull();
	}

	@Test
	void convertObjectReturnsAnObject() {
		assertThat(CONVERTER.convert(new TypeA())).isInstanceOf(TypeB.class);
	}

	@Test
	void convertListReturnsNullOnNull() {
		assertThat(CONVERTER.convert((List<TypeA>) null)).isNull();
	}

	@Test
	void convertListReturnsANewList() {
		var original = new ArrayList<TypeA>();

		assertThat(CONVERTER.convert(original))
			.isInstanceOf(List.class)
			.isNotSameAs(original);
	}

	@Test
	void convertMapReturnsNullOnNull() {
		assertThat(CONVERTER.convert((Map<?,TypeA>) null)).isNull();
	}

	@Test
	void convertMapReturnsANewMap() {
		var original = new HashMap<Object, TypeA>();

		assertThat(CONVERTER.convert(original))
			.isInstanceOf(Map.class)
			.isNotSameAs(original);
	}

	@Test
	void convertSetReturnsNullOnNull() {
		assertThat(CONVERTER.convert((Set<TypeA>) null)).isNull();
	}

	@Test
	void convertSetReturnsANewSet() {
		var original = new HashSet<TypeA>();

		assertThat(CONVERTER.convert(original))
			.isInstanceOf(Set.class)
			.isNotSameAs(original);
	}

	@Test
	void convertPageReturnsNullOnNull() {
		assertThat(CONVERTER.convert((Page<TypeA>) null)).isNull();
	}

	@Test
	void convertPageReturnsANewPage() {
		var original = new PageImpl<TypeA>(new ArrayList<>());

		assertThat(CONVERTER.convert(original))
			.isInstanceOf(Page.class)
			.isNotSameAs(original);
	}
}
