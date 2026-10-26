// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.service;
import it.bz.opendatahub.webcomponents.common.data.struct.Dist;
import java.time.LocalDateTime;

import it.bz.opendatahub.webcomponents.common.data.struct.Configuration;

import java.util.ArrayList;
import java.util.Date;

import it.bz.opendatahub.webcomponents.common.testing.BeanAssert;
import it.bz.opendatahub.webcomponents.dataservice.application.domain.Webcomponent;
import it.bz.opendatahub.webcomponents.dataservice.application.domain.WebcomponentVersion;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.CreateWebcomponentUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.CreateWebcomponentVersionUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.UpdateWebcomponentVersionUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.ReadWebcomponentPort;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.ReadWebcomponentVersionPort;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.ReadWorkspacePort;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.WriteWebcomponentVersionPort;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.WriteWorkspacePort;
import it.bz.opendatahub.webcomponents.dataservice.exception.impl.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebcomponentVersionAdminServiceTest {
	private ReadWebcomponentVersionPort readWebcomponentVersionPort;
	private WriteWebcomponentVersionPort writeWebcomponentVersionPort;

	private ReadWorkspacePort readWorkspacePort;
	private WriteWorkspacePort writeWorkspacePort;

	private ReadWebcomponentPort readWebcomponentPort;

	private WebcomponentVersionAdminService webcomponentVersionAdminService;

	private static final Webcomponent WEBCOMPONENT_A = new Webcomponent();
	private static final WebcomponentVersion WEBCOMPONENT_VERSION_A = new WebcomponentVersion();

	private static final String NON_EXISTING_WEBCOMPONENT_ID = "Z";
	private static final String NON_EXISTING_TAG_ID = "ZZ";

	@BeforeEach
	void initMocks() {
		WEBCOMPONENT_A.setUuid("A");
		WEBCOMPONENT_A.setImage("image.png");
		WEBCOMPONENT_A.setDeleted(false);

		WEBCOMPONENT_VERSION_A.setWebcomponentUuid("A");
		WEBCOMPONENT_VERSION_A.setVersionTag("AA");
		WEBCOMPONENT_VERSION_A.setDeleted(false);
		WEBCOMPONENT_VERSION_A.setReleaseTimestamp(new Date());
		WEBCOMPONENT_VERSION_A.setReadMe("theReadme");
		WEBCOMPONENT_VERSION_A.setLicenseAgreement("theLicense");
		WEBCOMPONENT_VERSION_A.setConfiguration(new Configuration());

		readWebcomponentPort = Mockito.mock(ReadWebcomponentPort.class);
		readWebcomponentVersionPort = Mockito.mock(ReadWebcomponentVersionPort.class);
		writeWebcomponentVersionPort = Mockito.mock(WriteWebcomponentVersionPort.class);
		readWorkspacePort = Mockito.mock(ReadWorkspacePort.class);
		writeWorkspacePort = Mockito.mock(WriteWorkspacePort.class);

		webcomponentVersionAdminService = Mockito.spy(new WebcomponentVersionAdminService(readWebcomponentPort, readWebcomponentVersionPort, writeWebcomponentVersionPort, writeWorkspacePort, readWorkspacePort));
	}

	@Test
	void createWebcomponentVersionThrowsIfUuidNull() {
		assertThatNullPointerException().isThrownBy(
			() -> webcomponentVersionAdminService.createWebcomponentVersion(null, new CreateWebcomponentVersionUseCase.WebcomponentVersionCreateRequest())
		);
	}

	@Test
	void createWebcomponentVersionThrowsIfRequestNull() {
		assertThatNullPointerException().isThrownBy(
			() -> webcomponentVersionAdminService.createWebcomponentVersion("any", null)
		);
	}

	@Test
	void createWebcomponentVersionThrowsIfWebcomponentDoesNotExist() {
		when(readWebcomponentPort.getWebcomponentById(NON_EXISTING_WEBCOMPONENT_ID)).thenThrow(new NotFoundException(""));

		var request = new CreateWebcomponentVersionUseCase.WebcomponentVersionCreateRequest();

		assertThatExceptionOfType(NotFoundException.class).isThrownBy(
			() -> webcomponentVersionAdminService.createWebcomponentVersion(NON_EXISTING_WEBCOMPONENT_ID, request)
		);
	}

	@Test
	void createWebcomponentVersionChecksForDuplicates() {
		when(readWebcomponentPort.getWebcomponentById(WEBCOMPONENT_A.getUuid())).thenReturn(WEBCOMPONENT_A);
		when(readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(WEBCOMPONENT_A.getUuid(), NON_EXISTING_TAG_ID)).thenThrow(new NotFoundException(""));

		var request = new CreateWebcomponentVersionUseCase.WebcomponentVersionCreateRequest();
		request.setVersionTag(NON_EXISTING_TAG_ID);
		request.setReleaseTimestamp(new Date());
		request.setDistFiles(new ArrayList<>());
		request.setConfiguration(new Configuration());

		webcomponentVersionAdminService.createWebcomponentVersion(WEBCOMPONENT_A.getUuid(), request);

		verify(webcomponentVersionAdminService, times(1)).checkForDuplicates(any(), any());
	}

	@Test
	void createWebcomponentVersionDoesStoreDistFiles() {
		when(readWebcomponentPort.getWebcomponentById(WEBCOMPONENT_A.getUuid())).thenReturn(WEBCOMPONENT_A);
		when(readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(any(), any())).thenThrow(new NotFoundException(""));

		var request = new CreateWebcomponentVersionUseCase.WebcomponentVersionCreateRequest();
		request.setVersionTag("TAG");
		request.setReleaseTimestamp(new Date());
		request.setDistFiles(new ArrayList<>());
		request.setConfiguration(new Configuration());

		webcomponentVersionAdminService.createWebcomponentVersion(WEBCOMPONENT_A.getUuid(), request);

		verify(webcomponentVersionAdminService, times(1)).storeDistFiles(any(), any(), any());
	}

	@Test
	void createWebcomponentVersionDoesReadDistSize() {
		when(readWebcomponentPort.getWebcomponentById(WEBCOMPONENT_A.getUuid())).thenReturn(WEBCOMPONENT_A);
		when(readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(any(), any())).thenThrow(new NotFoundException(""));

		var request = new CreateWebcomponentVersionUseCase.WebcomponentVersionCreateRequest();
		request.setVersionTag("TAG");
		request.setReleaseTimestamp(new Date());
		request.setDistFiles(new ArrayList<>());
		request.setConfiguration(new Configuration());

		webcomponentVersionAdminService.createWebcomponentVersion(WEBCOMPONENT_A.getUuid(), request);

		verify(readWorkspacePort, times(1)).getDirectorySizeInBytes(any());
	}

	@Test
	void createWebcomponentVersionDoesSaveToPort() {
		when(readWebcomponentPort.getWebcomponentById(WEBCOMPONENT_A.getUuid())).thenReturn(WEBCOMPONENT_A);
		when(readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(any(), any())).thenThrow(new NotFoundException(""));

		var request = new CreateWebcomponentVersionUseCase.WebcomponentVersionCreateRequest();
		request.setVersionTag("TAG");
		request.setReleaseTimestamp(new Date());
		request.setDistFiles(new ArrayList<>());
		request.setConfiguration(new Configuration());

		webcomponentVersionAdminService.createWebcomponentVersion(WEBCOMPONENT_A.getUuid(), request);

		verify(writeWebcomponentVersionPort, times(1)).saveWebcomponentVersion(any());
	}

	@Test
	void createWebcomponentVersionDoesHaveCorrectProperties() {
		when(readWebcomponentPort.getWebcomponentById(WEBCOMPONENT_A.getUuid())).thenReturn(WEBCOMPONENT_A);
		when(readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(any(), any())).thenThrow(new NotFoundException(""));
		when(writeWebcomponentVersionPort.saveWebcomponentVersion(any())).thenAnswer(i -> i.getArguments()[0]);
		when(readWorkspacePort.getDirectorySizeInBytes(any())).thenReturn(44*1024L);

		var desired = new WebcomponentVersion();
		desired.setWebcomponentUuid(WEBCOMPONENT_A.getUuid());
		desired.setVersionTag("TAG");
		desired.setReleaseTimestamp(new Date());
		desired.setDist(new Dist());
		desired.setConfiguration(new Configuration());
		desired.setDeleted(false);
		desired.setDistSizeTotalKb(44);
		desired.setLighthouseMetricsMobileData(null);
		desired.setLighthouseMetricsMobileDatetime(null);
		desired.setLighthouseMobilePerformanceRating(0);
		desired.setLighthouseMetricsDesktopData(null);
		desired.setLighthouseMetricsDesktopDatetime(null);
		desired.setLighthouseDesktopPerformanceRating(0);
		desired.setLighthouseUpdateRequired(true);
		desired.setReadMe("some readme");
		desired.setLicenseAgreement("some license");

		var request = new CreateWebcomponentVersionUseCase.WebcomponentVersionCreateRequest();
		request.setVersionTag(desired.getVersionTag());
		request.setReleaseTimestamp(desired.getReleaseTimestamp());
		request.setDist(desired.getDist());
		request.setDistFiles(new ArrayList<>());
		request.setConfiguration(desired.getConfiguration());
		request.setReadMe(desired.getReadMe());
		request.setLicenseAgreement(desired.getLicenseAgreement());

		var result = webcomponentVersionAdminService.createWebcomponentVersion(WEBCOMPONENT_A.getUuid(), request);

		assertThat(result).isEqualToIgnoringGivenFields(desired);
	}

	@Test
	void deleteWebcomponentByIdThrowsIfUuidIsNull() {
		assertThatNullPointerException().isThrownBy(
			() -> webcomponentVersionAdminService.deleteWebcomponentVersionById(null, "")
		);
	}

	@Test
	void deleteWebcomponentByIdThrowsIfTagIsNull() {
		assertThatNullPointerException().isThrownBy(
			() -> webcomponentVersionAdminService.deleteWebcomponentVersionById("", null)
		);
	}

	@Test
	void deleteWebcomponentByIdDoesNotThrowIfNotExists() {
		when(readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(NON_EXISTING_WEBCOMPONENT_ID, NON_EXISTING_TAG_ID)).thenThrow(new NotFoundException(""));

		assertThatCode(
			() -> webcomponentVersionAdminService.deleteWebcomponentVersionById(NON_EXISTING_WEBCOMPONENT_ID, NON_EXISTING_TAG_ID)
		).doesNotThrowAnyException();
	}

	@Test
	void deleteWebcomponentByIdDoesNotThrowIfAlreadyDeleted() {
		when(readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(WEBCOMPONENT_VERSION_A.getWebcomponentUuid(), WEBCOMPONENT_VERSION_A.getVersionTag())).thenReturn(WEBCOMPONENT_VERSION_A);
		WEBCOMPONENT_A.setDeleted(true);

		assertThatCode(
			() -> webcomponentVersionAdminService.deleteWebcomponentVersionById(WEBCOMPONENT_VERSION_A.getWebcomponentUuid(), WEBCOMPONENT_VERSION_A.getVersionTag())
		).doesNotThrowAnyException();
	}

	@Test
	void deleteWebcomponentByIdDoesSetDeletedFlag() {
		when(readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(WEBCOMPONENT_VERSION_A.getWebcomponentUuid(), WEBCOMPONENT_VERSION_A.getVersionTag())).thenReturn(WEBCOMPONENT_VERSION_A);

		webcomponentVersionAdminService.deleteWebcomponentVersionById(WEBCOMPONENT_VERSION_A.getWebcomponentUuid(), WEBCOMPONENT_VERSION_A.getVersionTag());

		assertThat(WEBCOMPONENT_VERSION_A.getDeleted()).isTrue();
	}

	@Test
	void deleteWebcomponentByIdDoesSave() {
		when(readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(WEBCOMPONENT_VERSION_A.getWebcomponentUuid(), WEBCOMPONENT_VERSION_A.getVersionTag())).thenReturn(WEBCOMPONENT_VERSION_A);

		webcomponentVersionAdminService.deleteWebcomponentVersionById(WEBCOMPONENT_VERSION_A.getWebcomponentUuid(), WEBCOMPONENT_VERSION_A.getVersionTag());

		verify(writeWebcomponentVersionPort, times(1)).saveWebcomponentVersion(WEBCOMPONENT_VERSION_A);
	}

	@Test
	void updateWebcomponentVersionThrowsIfUuidNull() {
		var request = new UpdateWebcomponentVersionUseCase.WebcomponentVersionUpdateRequest();

		assertThatNullPointerException().isThrownBy(
			() -> webcomponentVersionAdminService.updateWebcomponentVersion(null, "TAG", request)
		);
	}

	@Test
	void updateWebcomponentVersionThrowsIfVersionTagIsNull() {
		var request = new UpdateWebcomponentVersionUseCase.WebcomponentVersionUpdateRequest();

		assertThatNullPointerException().isThrownBy(
			() -> webcomponentVersionAdminService.updateWebcomponentVersion("UUID", null, request)
		);
	}

	@Test
	void updateWebcomponentVersionThrowsIfRequestIsNull() {
		var request = new UpdateWebcomponentVersionUseCase.WebcomponentVersionUpdateRequest();

		assertThatNullPointerException().isThrownBy(
			() -> webcomponentVersionAdminService.updateWebcomponentVersion("UUID", "TAG", null)
		);
	}

	@Test
	void updateWebcomponentVersionThrowsIfSpecifiedVersionNotExists() {
		when(readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(NON_EXISTING_WEBCOMPONENT_ID, NON_EXISTING_TAG_ID)).thenThrow(new NotFoundException(""));

		var request = new UpdateWebcomponentVersionUseCase.WebcomponentVersionUpdateRequest();

		assertThatExceptionOfType(NotFoundException.class).isThrownBy(
			() -> webcomponentVersionAdminService.updateWebcomponentVersion(NON_EXISTING_WEBCOMPONENT_ID, NON_EXISTING_TAG_ID, request)
		);
	}

	@Test
	void updateWebcomponentVersionUpdatesAllDataFields() {
		when(writeWebcomponentVersionPort.saveWebcomponentVersion(any())).thenAnswer(i -> i.getArguments()[0]);
		when(readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(WEBCOMPONENT_VERSION_A.getWebcomponentUuid(), WEBCOMPONENT_VERSION_A.getVersionTag())).thenReturn(WEBCOMPONENT_VERSION_A);

		var request = new UpdateWebcomponentVersionUseCase.WebcomponentVersionUpdateRequest();
		request.setReleaseTimestamp(new Date());
		request.setConfiguration(new Configuration());
		request.setReadMe("newReadMe");
		request.setLicenseAgreement("newLicense");

		var result = webcomponentVersionAdminService.updateWebcomponentVersion(WEBCOMPONENT_VERSION_A.getWebcomponentUuid(), WEBCOMPONENT_VERSION_A.getVersionTag(), request);

		BeanAssert.assertThat(result).hasCopiedAllProperties(request);
	}

	@Test
	void updateWebcomponentVersionDoesActuallySave() {
		when(readWebcomponentVersionPort.getSpecificVersionOfWebcomponent(WEBCOMPONENT_VERSION_A.getWebcomponentUuid(), WEBCOMPONENT_VERSION_A.getVersionTag())).thenReturn(WEBCOMPONENT_VERSION_A);

		webcomponentVersionAdminService.updateWebcomponentVersion(WEBCOMPONENT_VERSION_A.getWebcomponentUuid(), WEBCOMPONENT_VERSION_A.getVersionTag(), new UpdateWebcomponentVersionUseCase.WebcomponentVersionUpdateRequest());

		verify(writeWebcomponentVersionPort, times(1)).saveWebcomponentVersion(WEBCOMPONENT_VERSION_A);
	}
}
