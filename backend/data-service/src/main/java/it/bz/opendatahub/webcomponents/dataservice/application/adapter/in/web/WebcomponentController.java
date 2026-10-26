// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web;

import it.bz.opendatahub.webcomponents.common.converter.ConverterUtils;
import it.bz.opendatahub.webcomponents.common.stereotype.WebAdapter;
import it.bz.opendatahub.webcomponents.common.util.ThumbnailUtility;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web.converter.WebcomponentEntryWebConverter;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web.converter.WebcomponentWebConverter;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web.rest.WebcomponentConfigurationRest;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web.rest.WebcomponentEntryRest;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web.rest.WebcomponentRest;
import it.bz.opendatahub.webcomponents.dataservice.application.domain.WebcomponentConfiguration;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.CreateCodingSandboxUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.GetWebcomponentConfigurationUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.GetWebcomponentLogoUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.GetWebcomponentUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.ListWebcomponentUseCase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebAdapter
@RestController
@RequestMapping("/webcomponent")
public class WebcomponentController {

	@Value("${thumbnail.width}")
	private int thumbnailWidth;
	@Value("${thumbnail.height}")
	private int thumbnailHeight;

	private final GetWebcomponentUseCase getWebcomponentUseCase;
	private final ListWebcomponentUseCase listWebcomponentUseCase;
	private final GetWebcomponentConfigurationUseCase getWebcomponentConfigurationUseCase;
	private final GetWebcomponentLogoUseCase getWebcomponentLogoUseCase;
	private final WebcomponentWebConverter webcomponentWebConverter;
	private final WebcomponentEntryWebConverter webcomponentEntryWebConverter;
	private final CreateCodingSandboxUseCase createCodingSandboxUseCase;

	private final Map<String, byte[]> thumbCache = new HashMap<>();
	private final Map<String, Long> thumbCacheTimer = new HashMap<>();

	public WebcomponentController(GetWebcomponentUseCase getWebcomponentUseCase,
			ListWebcomponentUseCase listWebcomponentUseCase,
			GetWebcomponentConfigurationUseCase getWebcomponentConfigurationUseCase,
			GetWebcomponentLogoUseCase getWebcomponentLogoUseCase, WebcomponentWebConverter webcomponentWebConverter,
			WebcomponentEntryWebConverter webcomponentEntryWebConverter,
			CreateCodingSandboxUseCase createCodingSandboxUseCase) {
		this.getWebcomponentUseCase = getWebcomponentUseCase;
		this.listWebcomponentUseCase = listWebcomponentUseCase;
		this.getWebcomponentConfigurationUseCase = getWebcomponentConfigurationUseCase;
		this.getWebcomponentLogoUseCase = getWebcomponentLogoUseCase;
		this.webcomponentWebConverter = webcomponentWebConverter;
		this.webcomponentEntryWebConverter = webcomponentEntryWebConverter;
		this.createCodingSandboxUseCase = createCodingSandboxUseCase;
	}

	@GetMapping
	public Page<WebcomponentEntryRest> listPagedAndFiltered(
			@RequestParam(name = "tags", required = false) String[] tags,
			@RequestParam(name = "searchTerm", required = false, defaultValue = "") String searchTerm,
			@RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer requestPageNumber,
			@RequestParam(name = "pageSize", required = false, defaultValue = "20") Integer requestPageSize,
			@RequestParam(name = "latest", required = false, defaultValue = "false") Boolean latest) {

		Pageable pageRequest;
		if (latest) {
			pageRequest = PageRequest.of(requestPageNumber, requestPageSize, Sort.by("latest"));
		} else {
			pageRequest = PageRequest.of(requestPageNumber, requestPageSize);
		}

		List<String> tagList = Collections.emptyList();
		if (tags != null) {
			tagList = Arrays.asList(tags);
		}

		var filter = new ListWebcomponentUseCase.WebcomponentFilter();
		filter.setSearchTerm(searchTerm);
		filter.setTags(tagList);

		var resultPage = listWebcomponentUseCase.listPagedAndFiltered(pageRequest, filter);

		return webcomponentEntryWebConverter.convert(resultPage);
	}

	@GetMapping("/{uuid}")
	public WebcomponentRest getOne(@PathVariable String uuid) {
		return webcomponentWebConverter.convert(getWebcomponentUseCase.getWebcomponentById(uuid));
	}

	@GetMapping(value = { "/{uuid}/config", "/{uuid}/config/latest" })
	public WebcomponentConfigurationRest getConfiguration(@PathVariable String uuid) {
		var configuration = getWebcomponentConfigurationUseCase.getConfiguration(uuid);

		return toRest(configuration);
	}

	@GetMapping("/{uuid}/config/{versionTag}")
	public WebcomponentConfigurationRest getConfigurationForVersion(@PathVariable String uuid,
			@PathVariable String versionTag) {
		var configuration = getWebcomponentConfigurationUseCase.getConfiguration(uuid, versionTag);

		return toRest(configuration);
	}

	@GetMapping(value = "/{uuid}/logo", produces = "image/png") // TODO: actually, the mimetype is never checked. might
																// also be gif or webp
	public byte[] getLogoImage(@PathVariable String uuid) {
		return getWebcomponentLogoUseCase.getLogoImage(uuid);
	}

	@GetMapping(value = "/{uuid}/logo/thumb", produces = "image/jpg")
	public byte[] getLogoImageThumb(@PathVariable String uuid) throws IOException {
		if (thumbCacheTimer.containsKey(uuid) && thumbCacheTimer.get(uuid) < System.currentTimeMillis()) {
			thumbCacheTimer.remove(uuid);
			thumbCache.remove(uuid);
		}

		if (thumbCache.containsKey(uuid)) {
			return thumbCache.get(uuid);
		}

		var fullLogoData = getLogoImage(uuid);

		var image = ThumbnailUtility.createThumbnailForImage(new ByteArrayInputStream(fullLogoData), thumbnailWidth,
				thumbnailHeight);

		var data = ThumbnailUtility.toJpg(image).toByteArray();

		var max = 100;
		var min = 10;
		var range = max - min + 1;
		var rand = (int) (Math.random() * range) + min;

		thumbCacheTimer.put(uuid, System.currentTimeMillis() + 60 * 60 * 1000L + rand * 1000L);
		thumbCache.put(uuid, data);

		return data;
	}

	private WebcomponentConfigurationRest toRest(WebcomponentConfiguration domain) {
		var rest = new WebcomponentConfigurationRest();

		ConverterUtils.copyProperties(domain, rest);
		rest.setScriptSources(domain.getScriptSources()); // TODO: check if that is even needed

		return rest;
	}

	@PostMapping(value = "/createCodeSandbox")
	public String createCodeSandbox(@RequestBody @Valid CreateCodingSandboxUseCase.CodeSandboxRequest request)
			{
		return createCodingSandboxUseCase.createCodeSandbox(request);
	}
}
