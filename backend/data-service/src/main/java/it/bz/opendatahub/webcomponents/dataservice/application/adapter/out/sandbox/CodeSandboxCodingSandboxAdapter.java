// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.adapter.out.sandbox;

import it.bz.opendatahub.webcomponents.common.stereotype.Adapter;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.CreateCodingSandboxUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.CodingSandboxPort;
import lombok.NonNull;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Adapter
public class CodeSandboxCodingSandboxAdapter implements CodingSandboxPort {

	@Override
	public String createCodeSandbox(CreateCodingSandboxUseCase.@NonNull CodeSandboxRequest codeSandboxRequest){
		try {
			String html = "<!DOCTYPE html>\n<html lang='en'>\n\t<head>\n\t\t<meta charset='UTF-8'>\n\t\t<title>Title</title>"+
				"\n\t</head>\n\t<body>\n\t\t"+
				codeSandboxRequest.codeSnippet +
				"\n\t</body>\n</html>";

			var body = Map.of("files", Map.of(
				"index.html", Map.of("content", html, "isBinary", false),
				"package.json", Map.of("content", Map.of(
					"main", "index.html",
					"scripts", Map.of(
						"start", "parcel index.html --open",
						"build", "parcel build index.html"),
					"devDependencies", Map.of("parcel-bundler", "^1.6.1")))));

			String uri = "https://codesandbox.io/api/v1/sandboxes/define?json=1";
			RestTemplate template = new RestTemplate();

			Map<?, ?> result = template.postForObject(uri, body, Map.class);
			return String.valueOf(result.get("sandbox_id"));
		} catch (Exception e) {
			return "";
		}
	}
}
