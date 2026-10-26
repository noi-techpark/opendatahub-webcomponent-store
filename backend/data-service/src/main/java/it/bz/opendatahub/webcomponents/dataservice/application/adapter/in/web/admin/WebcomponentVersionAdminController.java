// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web.admin;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.bz.opendatahub.webcomponents.common.stereotype.WebAdapter;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web.admin.converter.WebcomponentVersionAdminWebConverter;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web.admin.rest.WebcomponentVersionAdminRest;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.CreateWebcomponentVersionUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.DeleteWebcomponentVersionUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.ReplaceWebcomponentVersionUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.ScheduleWebcomponentVersionMetricsUpdateUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.UpdateWebcomponentVersionUseCase;
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
@RequestMapping("/admin/webcomponent/{uuid}")
public class WebcomponentVersionAdminController {
	private final CreateWebcomponentVersionUseCase createWebcomponentVersionUseCase;
	private final ReplaceWebcomponentVersionUseCase replaceWebcomponentVersionUseCase;
	private final UpdateWebcomponentVersionUseCase updateWebcomponentVersionUseCase;
	private final DeleteWebcomponentVersionUseCase deleteWebcomponentVersionUseCase;
	private final ScheduleWebcomponentVersionMetricsUpdateUseCase scheduleWebcomponentVersionMetricsUpdateUseCase;
	private final WebcomponentVersionAdminWebConverter webcomponentVersionWebConverter;

	public WebcomponentVersionAdminController(CreateWebcomponentVersionUseCase createWebcomponentVersionUseCase, ReplaceWebcomponentVersionUseCase replaceWebcomponentVersionUseCase, UpdateWebcomponentVersionUseCase updateWebcomponentVersionUseCase, DeleteWebcomponentVersionUseCase deleteWebcomponentVersionUseCase, ScheduleWebcomponentVersionMetricsUpdateUseCase scheduleWebcomponentVersionMetricsUpdateUseCase, WebcomponentVersionAdminWebConverter webcomponentVersionWebConverter) {
		this.createWebcomponentVersionUseCase = createWebcomponentVersionUseCase;
		this.replaceWebcomponentVersionUseCase = replaceWebcomponentVersionUseCase;
		this.updateWebcomponentVersionUseCase = updateWebcomponentVersionUseCase;
		this.deleteWebcomponentVersionUseCase = deleteWebcomponentVersionUseCase;
		this.scheduleWebcomponentVersionMetricsUpdateUseCase = scheduleWebcomponentVersionMetricsUpdateUseCase;
		this.webcomponentVersionWebConverter = webcomponentVersionWebConverter;
	}

	@PostMapping
	public WebcomponentVersionAdminRest createNewVersion(@PathVariable String uuid, @RequestBody @Valid CreateWebcomponentVersionUseCase.WebcomponentVersionCreateRequest request) {
		var result = createWebcomponentVersionUseCase.createWebcomponentVersion(uuid, request);

		return webcomponentVersionWebConverter.convert(result);
	}

	@PutMapping("/{versionTag}")
	public WebcomponentVersionAdminRest replaceVersion(@PathVariable String uuid, @PathVariable String versionTag, @RequestBody @Valid ReplaceWebcomponentVersionUseCase.WebcomponentVersionReplaceRequest request) {
		var result = replaceWebcomponentVersionUseCase.replaceWebcomponentVersion(uuid, versionTag, request);

		return webcomponentVersionWebConverter.convert(result);
	}

	@PatchMapping("/{versionTag}")
	public WebcomponentVersionAdminRest updateVersion(@PathVariable String uuid, @PathVariable String versionTag, @RequestBody @Valid UpdateWebcomponentVersionUseCase.WebcomponentVersionUpdateRequest request) {
		var result = updateWebcomponentVersionUseCase.updateWebcomponentVersion(uuid, versionTag, request);

		return webcomponentVersionWebConverter.convert(result);
	}

	@PatchMapping("/{versionTag}/metrics")
	public WebcomponentVersionAdminRest scheduleMetricsUpdate(@PathVariable String uuid, @PathVariable String versionTag) {
		var result = scheduleWebcomponentVersionMetricsUpdateUseCase.scheduleMetricsUpdate(uuid, versionTag);

		return webcomponentVersionWebConverter.convert(result);
	}

	@DeleteMapping("/{versionTag}")
	public void deleteVersion(@PathVariable String uuid, @PathVariable String versionTag) {
		deleteWebcomponentVersionUseCase.deleteWebcomponentVersionById(uuid, versionTag);
	}
}
