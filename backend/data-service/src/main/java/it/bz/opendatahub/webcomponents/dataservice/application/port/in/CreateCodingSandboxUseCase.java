// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.port.in;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

public interface CreateCodingSandboxUseCase {

	String createCodeSandbox(@NonNull CodeSandboxRequest codeSandboxRequest);

	@Getter
	@Setter
	class CodeSandboxRequest {
		@NotBlank
		public
		String codeSnippet;

	}
}
