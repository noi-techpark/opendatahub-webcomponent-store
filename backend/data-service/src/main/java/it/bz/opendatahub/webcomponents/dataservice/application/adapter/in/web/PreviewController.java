// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web;

import it.bz.opendatahub.webcomponents.common.stereotype.WebAdapter;
import it.bz.opendatahub.webcomponents.dataservice.application.domain.WebcomponentConfiguration;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.GetDefaultPreviewSnippetUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.GetWebcomponentConfigurationUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.GetWebcomponentVersionUseCase;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@WebAdapter
@Controller
@RequestMapping("/preview")
public class PreviewController {
	private final GetWebcomponentConfigurationUseCase getWebcomponentConfigurationUseCase;
	private final GetDefaultPreviewSnippetUseCase getDefaultPreviewSnippetUseCase;

	public PreviewController(GetWebcomponentConfigurationUseCase getWebcomponentConfigurationUseCase, GetDefaultPreviewSnippetUseCase getDefaultPreviewSnippetUseCase) {
		this.getWebcomponentConfigurationUseCase = getWebcomponentConfigurationUseCase;
		this.getDefaultPreviewSnippetUseCase = getDefaultPreviewSnippetUseCase;
	}

	@GetMapping(value = "/{uuid}/{versionTag}")
	public String getPreview(@PathVariable final String uuid, @PathVariable(required = true) final String versionTag,
			@RequestParam(defaultValue = "") String style, @RequestParam(defaultValue = "") String slot,
			@RequestParam(defaultValue = "") String attribs, Model model) throws UnsupportedEncodingException {

		WebcomponentConfiguration conf;
		if ("latest".equalsIgnoreCase(versionTag)) {
			conf = getWebcomponentConfigurationUseCase.getConfiguration(uuid);
		} else {
			conf = getWebcomponentConfigurationUseCase.getConfiguration(uuid, versionTag);
		}

		//val decoded = URLDecoder.decode(attribs);

		String snippet;
		if(!attribs.isEmpty()) {
			snippet = new String(Base64.getDecoder().decode(attribs), StandardCharsets.UTF_8);
		}
		else {
			if ("latest".equalsIgnoreCase(versionTag)){
				snippet = getDefaultPreviewSnippetUseCase.getDefaultPreviewSnippet(uuid);
			}
			else {
				snippet = getDefaultPreviewSnippetUseCase.getDefaultPreviewSnippet(uuid, versionTag);
			}
		}

		if (!style.isEmpty()) {
			var firstSpaceIndex = snippet.indexOf(" ");
			snippet = snippet.substring(0, firstSpaceIndex) +" style=\"" + style + "\"" + snippet.substring(firstSpaceIndex);
		}

		if(!slot.isEmpty()) {
			var slotIndex = snippet.lastIndexOf("></") + 1;
			snippet = snippet.substring(0, slotIndex) + slot + snippet.substring(slotIndex);
		}

		model.addAttribute("snippetCode", snippet);
		model.addAttribute("snippetScripts", conf.getScriptSources());

		return "preview";
	}
}
