// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.common.converter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BeanConverter<S, T> {
	protected Class<S> sourceType;
	protected Class<T> targetType;

	protected Constructor<?> sourceConstructor;
	protected Constructor<?> targetConstructor;

	public BeanConverter() {
		resolveTypes();

		resolveConstructors();
	}

	private void resolveTypes() {
		var parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();

		@SuppressWarnings("unchecked") final Class<S> dtoTypeArgument = (Class<S>) parameterizedType.getActualTypeArguments()[0];

		this.sourceType = dtoTypeArgument;

		@SuppressWarnings("unchecked") final Class<T> restTypeArgument = (Class<T>) parameterizedType.getActualTypeArguments()[1];

		this.targetType = restTypeArgument;
	}

	private void resolveConstructors() {
		try {
			this.sourceConstructor = sourceType.getConstructor();
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		try {
			this.targetConstructor = targetType.getConstructor();
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	protected Object createTargetObject(Constructor<?> constructor) {
		final Object targetObject;
		try {
			targetObject = constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		return targetObject;
	}

	public T convert(S source) {
		if(source == null) {
			return null;
		}

		var targetObject = createTargetObject(targetConstructor);

		ConverterUtils.copyProperties(source, targetObject);

		@SuppressWarnings("unchecked") var target = (T) targetObject;

		return target;
	}

	public List<T> convert(List<S> sources) {
		if(sources == null) {
			return null;
		}

		var resList = new ArrayList<T>();

		for(S source : sources) {
			resList.add(convert(source));
		}

		return resList;
	}

	public Set<T> convert(Set<S> sources) {
		if(sources == null) {
			return null;
		}

		var resSet = new HashSet<T>();

		for(S source : sources) {
			resSet.add(convert(source));
		}

		return resSet;
	}

	public Page<T> convert(Page<S> sources) {
		if(sources == null) {
			return null;
		}

		var resList = convert(sources.getContent());

		return new PageImpl<>(
			resList,
			sources.getPageable(),
			sources.getTotalElements()
		);
	}

	public Map<?, T> convert(Map<?, S> sources) {
		if (sources == null) {
			return null;
		}

		var targetMap = new HashMap<Object, T>();

		for (Map.Entry<?, S> entry : sources.entrySet()) {
			targetMap.put(entry.getKey(), convert(entry.getValue()));
		}

		return targetMap;
	}
}
