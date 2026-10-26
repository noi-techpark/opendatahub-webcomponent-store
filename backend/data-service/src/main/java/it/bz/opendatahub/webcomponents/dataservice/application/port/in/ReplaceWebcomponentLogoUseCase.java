// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.port.in;

import it.bz.opendatahub.webcomponents.dataservice.application.domain.Webcomponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

public interface ReplaceWebcomponentLogoUseCase {
	Webcomponent replaceLogo(@NonNull String webcomponentUuid, @NonNull WebcomponentLogoReplaceRequest request);

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	class WebcomponentLogoReplaceRequest {
		@NotBlank
		private String logoPngBase64;
	}
}
