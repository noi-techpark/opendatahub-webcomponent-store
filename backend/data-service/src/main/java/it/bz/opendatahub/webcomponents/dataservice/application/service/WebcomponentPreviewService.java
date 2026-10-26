// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.service;

import it.bz.opendatahub.webcomponents.dataservice.application.domain.WebcomponentVersion;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.GetDefaultPreviewSnippetUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.ReadWebcomponentVersionPort;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@Service
public class WebcomponentPreviewService implements GetDefaultPreviewSnippetUseCase {
	private final ReadWebcomponentVersionPort readWebcomponentVersionPort;

	public WebcomponentPreviewService(ReadWebcomponentVersionPort readWebcomponentVersionPort) {
		this.readWebcomponentVersionPort = readWebcomponentVersionPort;
	}

	@Override
	public String getDefaultPreviewSnippet(@NonNull String webcomponentUuid, @NonNull String versionTag) {
		var webcomponentVersion = readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(
			webcomponentUuid,
			versionTag
		);

		return getAttributesFromConfig(webcomponentVersion);
	}

	@Override
	public String getDefaultPreviewSnippet(@NonNull String webcomponentUuid) {
		var webcomponentVersion = readWebcomponentVersionPort.getLatestVersionOfWebcomponent(
			webcomponentUuid
		);

		return getAttributesFromConfig(webcomponentVersion);
	}

	private String getAttributesFromConfig(WebcomponentVersion webcomponentVersion) {
		var options = webcomponentVersion.getConfiguration().getOptions();
		var tagName = webcomponentVersion.getConfiguration().getTagName();

		var attribs = new ArrayList<String>();

		for(var option : options) {
			if(!(option.getOptions() instanceof LinkedHashMap)) {
				continue;
			}

			@SuppressWarnings("unchecked")
			var innerOptions = (LinkedHashMap<String, Object>)option.getOptions();

			if(optionIsRequiredOrHasDefaultValue(option, innerOptions)) {
				var quotes = "\"";
				if(innerOptions.get("default") instanceof String && ((String)innerOptions.get("default")).contains(quotes)) {
					quotes = "'";
				}

				if(option.getType().equals("null")) {
					attribs.add(option.getKey());
				}
				else {
					attribs.add(
						option.getKey() + "=" + quotes + innerOptions.get("default") + quotes
					);
				}
			}
		}

		return  "<"+tagName+" " +  String.join(" ", attribs) + "></"+tagName+">";
	}

	private boolean optionIsRequiredOrHasDefaultValue(it.bz.opendatahub.webcomponents.common.data.struct.Configuration.Options option, LinkedHashMap<String, Object> innerOptions) {
		return (option.getRequired() != null && option.getRequired()) || innerOptions.containsKey("default");
	}
}
