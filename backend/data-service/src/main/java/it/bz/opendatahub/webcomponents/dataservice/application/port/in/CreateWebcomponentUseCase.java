// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.port.in;

import it.bz.opendatahub.webcomponents.common.data.struct.Author;
import it.bz.opendatahub.webcomponents.dataservice.application.domain.Webcomponent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface CreateWebcomponentUseCase {
	Webcomponent createWebcomponent(@NonNull WebcomponentCreateRequest request);

	@Getter
	@Setter
	class WebcomponentCreateRequest {
		@NotBlank
		private String title;

		@NotBlank
		private String shortName;

		@NotBlank
		private String description;

		@NotBlank
		private String descriptionAbstract;

		@NotBlank
		private String license;

		@NotBlank
		private String repositoryUrl;

		@NotNull
		private List<Author> copyrightHolders;

		@NotBlank
		private String imagePngBase64;

		@NotNull
		private List<Author> authors;

		@NotNull
		private List<String> searchTags;
	}
}
