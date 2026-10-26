// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.service;

import it.bz.opendatahub.webcomponents.dataservice.application.domain.Webcomponent;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.CreateCodingSandboxUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.GetWebcomponentLogoUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.GetWebcomponentUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.ListWebcomponentUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.CodingSandboxPort;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.ReadWebcomponentPort;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.ReadWebcomponentVersionPort;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.ReadWorkspacePort;
import it.bz.opendatahub.webcomponents.dataservice.exception.impl.NotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;

@Service
public class WebcomponentService implements GetWebcomponentUseCase, ListWebcomponentUseCase, GetWebcomponentLogoUseCase, CreateCodingSandboxUseCase {
	private final ReadWebcomponentPort readWebcomponentPort;
	private final ReadWorkspacePort readWorkspacePort;
	private final ReadWebcomponentVersionPort readWebcomponentVersionPort;
	private final CodingSandboxPort codingSandboxPort;

	public WebcomponentService(ReadWebcomponentPort readWebcomponentPort, ReadWorkspacePort readWorkspacePort, ReadWebcomponentVersionPort readWebcomponentVersionPort, @Qualifier("codeSandboxCodingSandboxAdapter") CodingSandboxPort codingSandboxPort) {
		this.readWebcomponentPort = readWebcomponentPort;
		this.readWorkspacePort = readWorkspacePort;
		this.readWebcomponentVersionPort = readWebcomponentVersionPort;
		this.codingSandboxPort = codingSandboxPort;
	}

	@Override
	public String createCodeSandbox(CreateCodingSandboxUseCase.@NonNull CodeSandboxRequest codeSandboxRequest) {
		return codingSandboxPort.createCodeSandbox(codeSandboxRequest);
	}

	@Override
	public Webcomponent getWebcomponentById(@NonNull String uuid) {
		return readWebcomponentPort.getWebcomponentById(uuid);
	}

	@Override
	public List<Webcomponent> listAll() {
		return readWebcomponentPort.listAll();
	}

	@Override
	public Page<Webcomponent> listPagedAndFiltered(@NonNull Pageable pageRequest, @NonNull WebcomponentFilter filter) {
		return readWebcomponentPort.listPagedAndFiltered(pageRequest, filter);
	}

	@Override
	public byte[] getLogoImage(@NonNull String uuid) {
		var webcomponent = readWebcomponentPort.getWebcomponentById(uuid);

		try {
			return readWorkspacePort.readFile(Paths.get(uuid, webcomponent.getImage()));
		}
		catch (NotFoundException e) {
			var latestVersion = readWebcomponentVersionPort.getLatestVersionOfWebcomponent(uuid);

			return readWorkspacePort.readFile(Paths.get(uuid, latestVersion.getVersionTag(), webcomponent.getImage()));
		}
	}
}
