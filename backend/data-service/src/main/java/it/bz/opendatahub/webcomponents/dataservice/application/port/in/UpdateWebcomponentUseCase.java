// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.port.in;

import it.bz.opendatahub.webcomponents.common.data.struct.Author;
import it.bz.opendatahub.webcomponents.dataservice.application.domain.Webcomponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface UpdateWebcomponentUseCase {
	Webcomponent updateWebcomponent(@NonNull String webcomponentUuid, @NonNull WebcomponentUpdateRequest request);

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	class WebcomponentUpdateRequest {
		@NotBlank
		private String title;

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

		@NotNull
		private List<Author> authors;

		@NotNull
		private List<String> searchTags;
	}
}
