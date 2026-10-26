// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web.admin;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.bz.opendatahub.webcomponents.common.stereotype.WebAdapter;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web.converter.WebcomponentWebConverter;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web.rest.WebcomponentRest;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.CreateWebcomponentUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.DeleteWebcomponentUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.RecalculateAllDistSizesUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.RefetchAllLighthouseMetricsUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.ReplaceWebcomponentLogoUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.UpdateWebcomponentUseCase;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@SecurityRequirement(name = "bearerAuth")
@WebAdapter
@RestController
@RequestMapping("/admin/webcomponent")
public class WebcomponentAdminController {
	private final UpdateWebcomponentUseCase updateWebcomponentUseCase;
	private final DeleteWebcomponentUseCase deleteWebcomponentUseCase;
	private final CreateWebcomponentUseCase createWebcomponentUseCase;
	private final WebcomponentWebConverter webcomponentWebConverter;
	private final RecalculateAllDistSizesUseCase recalculateAllDistSizesUseCase;
	private final ReplaceWebcomponentLogoUseCase replaceWebcomponentLogoUseCase;
	private final RefetchAllLighthouseMetricsUseCase refetchAllLighthouseMetricsUseCase;

	public WebcomponentAdminController(UpdateWebcomponentUseCase updateWebcomponentUseCase, DeleteWebcomponentUseCase deleteWebcomponentUseCase, CreateWebcomponentUseCase createWebcomponentUseCase, WebcomponentWebConverter webcomponentWebConverter, RecalculateAllDistSizesUseCase recalculateAllDistSizesUseCase, ReplaceWebcomponentLogoUseCase replaceWebcomponentLogoUseCase, RefetchAllLighthouseMetricsUseCase refetchAllLighthouseMetricsUseCase) {
		this.updateWebcomponentUseCase = updateWebcomponentUseCase;
		this.deleteWebcomponentUseCase = deleteWebcomponentUseCase;
		this.createWebcomponentUseCase = createWebcomponentUseCase;
		this.webcomponentWebConverter = webcomponentWebConverter;
		this.recalculateAllDistSizesUseCase = recalculateAllDistSizesUseCase;
		this.replaceWebcomponentLogoUseCase = replaceWebcomponentLogoUseCase;
		this.refetchAllLighthouseMetricsUseCase = refetchAllLighthouseMetricsUseCase;
	}

	@PostMapping
	public WebcomponentRest createWebcomponent(@RequestBody @Valid CreateWebcomponentUseCase.WebcomponentCreateRequest request) {
		var result = createWebcomponentUseCase.createWebcomponent(request);

		return webcomponentWebConverter.convert(result);
	}

	@PatchMapping("/recalculate-size")
	public void recalculateAllDistSizes() {
		recalculateAllDistSizesUseCase.recalculateAllDistSizes();
	}

	@PatchMapping("/refetch-lighthouse")
	public void refetchAllLighthouseMetrics() {
		refetchAllLighthouseMetricsUseCase.refetchAllLighthouseMetrics();
	}

	@PutMapping("/{uuid}")
	public WebcomponentRest updateWebcomponent(@PathVariable String uuid, @RequestBody @Valid UpdateWebcomponentUseCase.WebcomponentUpdateRequest request) {
		var result = updateWebcomponentUseCase.updateWebcomponent(uuid, request);

		return webcomponentWebConverter.convert(result);
	}

	@PatchMapping("/{uuid}/logo")
	public WebcomponentRest updateWebcomponent(@PathVariable String uuid, @RequestBody @Valid ReplaceWebcomponentLogoUseCase.WebcomponentLogoReplaceRequest request) {
		var result = replaceWebcomponentLogoUseCase.replaceLogo(uuid, request);

		return webcomponentWebConverter.convert(result);
	}

	@DeleteMapping("/{uuid}")
	public void deleteWebcomponent(@PathVariable String uuid) {
		deleteWebcomponentUseCase.deleteWebcomponentById(uuid);
	}
}
