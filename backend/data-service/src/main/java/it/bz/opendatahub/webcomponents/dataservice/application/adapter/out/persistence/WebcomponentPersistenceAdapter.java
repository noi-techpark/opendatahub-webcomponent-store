// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.adapter.out.persistence;

import it.bz.opendatahub.webcomponents.common.converter.ConverterUtils;
import it.bz.opendatahub.webcomponents.common.data.model.WebcomponentModel;
import it.bz.opendatahub.webcomponents.common.stereotype.PersistenceAdapter;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.out.persistence.converter.WebcomponentPersistenceConverter;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.out.persistence.repository.WebcomponentRepository;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.out.persistence.repository.WebcomponentSearchRepository;
import it.bz.opendatahub.webcomponents.dataservice.application.domain.Webcomponent;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.ListWebcomponentUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.ReadWebcomponentPort;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.WriteWebcomponentPort;
import it.bz.opendatahub.webcomponents.dataservice.exception.impl.NotFoundException;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@PersistenceAdapter
public class WebcomponentPersistenceAdapter implements ReadWebcomponentPort, WriteWebcomponentPort {
	private final WebcomponentRepository webcomponentRepository;
	private final WebcomponentSearchRepository webcomponentSearchRepository;
	private final WebcomponentPersistenceConverter webcomponentConverter;

	public WebcomponentPersistenceAdapter(WebcomponentRepository webcomponentRepository, WebcomponentSearchRepository webcomponentSearchRepository, WebcomponentPersistenceConverter webcomponentConverter) {
		this.webcomponentRepository = webcomponentRepository;
		this.webcomponentSearchRepository = webcomponentSearchRepository;
		this.webcomponentConverter = webcomponentConverter;
	}

	@Override
	public Webcomponent getWebcomponentById(@NonNull String uuid) {
		var probe = webcomponentRepository.findById(uuid);

		if(probe.isPresent()) {
			return webcomponentConverter.convert(probe.get());
		}

		throw new NotFoundException("no such comp");
	}

	@Override
	public List<Webcomponent> listAll() {
		var result = webcomponentRepository.findAll();

		return webcomponentConverter.convert(result);
	}

	@Override
	public Page<Webcomponent> listPagedAndFiltered(@NonNull Pageable pageRequest, ListWebcomponentUseCase.@NonNull WebcomponentFilter filter) {
		var result = webcomponentSearchRepository.findBySearchTermAndTags(filter.getSearchTerm(), filter.getTags(), pageRequest);

		return webcomponentConverter.convert(result);
	}

	@Override
	public Webcomponent saveWebcomponent(@NonNull Webcomponent webcomponent) {
		var probe = webcomponentRepository.findById(webcomponent.getUuid());
		WebcomponentModel model;
		if(probe.isPresent()) {
			model = probe.get();
		}
		else {
			model = new WebcomponentModel();
		}

		ConverterUtils.copyProperties(webcomponent, model);

		model = webcomponentRepository.save(model);

		return webcomponentConverter.convert(model);
	}
}
