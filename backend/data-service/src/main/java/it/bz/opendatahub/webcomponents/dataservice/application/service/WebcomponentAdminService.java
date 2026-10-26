// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.service;

import it.bz.opendatahub.webcomponents.common.converter.ConverterUtils;
import it.bz.opendatahub.webcomponents.dataservice.application.domain.Webcomponent;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.CreateWebcomponentUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.DeleteWebcomponentUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.ReplaceWebcomponentLogoUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.UpdateWebcomponentUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.ReadWebcomponentPort;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.WriteWebcomponentPort;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.WriteWorkspacePort;
import it.bz.opendatahub.webcomponents.dataservice.exception.impl.NotFoundException;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class WebcomponentAdminService implements CreateWebcomponentUseCase, UpdateWebcomponentUseCase, DeleteWebcomponentUseCase, ReplaceWebcomponentLogoUseCase {
	public static final String WCS_LOGO_PNG = "wcs-logo.png";
	private final ReadWebcomponentPort readWebcomponentPort;
	private final WriteWebcomponentPort writeWebcomponentPort;

	private final WriteWorkspacePort writeWorkspacePort;

	public WebcomponentAdminService(ReadWebcomponentPort readWebcomponentPort, WriteWebcomponentPort writeWebcomponentPort, WriteWorkspacePort writeWorkspacePort) {
		this.readWebcomponentPort = readWebcomponentPort;
		this.writeWebcomponentPort = writeWebcomponentPort;
		this.writeWorkspacePort = writeWorkspacePort;
	}

	@Override
	@Transactional
	public Webcomponent createWebcomponent(@NonNull final WebcomponentCreateRequest request) {
		var webcomponent = new Webcomponent();

		webcomponent.setUuid(
			UUID.randomUUID().toString()
		);
		webcomponent.setImage(WCS_LOGO_PNG);
		webcomponent.setDeleted(false);
		webcomponent.setHighlighted(false);
		webcomponent.setTheme("dark");


		ConverterUtils.copyProperties(request, webcomponent);

		updateLogo(webcomponent, request.getImagePngBase64());

		webcomponent = writeWebcomponentPort.saveWebcomponent(webcomponent);

		return webcomponent;
	}

	@Override
	@Transactional
	public Webcomponent updateWebcomponent(@NonNull final String webcomponentUuid, @NonNull final WebcomponentUpdateRequest request) {
		var webcomponent = readWebcomponentPort.getWebcomponentById(webcomponentUuid);

		ConverterUtils.copyProperties(request, webcomponent);

		return writeWebcomponentPort.saveWebcomponent(webcomponent);
	}

	@Override
	@Transactional
	public void deleteWebcomponentById(@NonNull final String uuid) {
		try {
			var webcomponent = readWebcomponentPort.getWebcomponentById(uuid);
			webcomponent.setDeleted(true);
			writeWebcomponentPort.saveWebcomponent(webcomponent);
		}
		catch (NotFoundException ignored) {}
	}

	@Override
	@Transactional
	public Webcomponent replaceLogo(@NonNull String webcomponentUuid, @NonNull WebcomponentLogoReplaceRequest request) {
		var webcomponent = readWebcomponentPort.getWebcomponentById(webcomponentUuid);

		updateLogo(webcomponent, request.getLogoPngBase64());

		return writeWebcomponentPort.saveWebcomponent(webcomponent);
	}

	protected void updateLogo(Webcomponent webcomponent, String logoPngBase64) {
		var oldLogoPath = Paths.get(webcomponent.getUuid(), webcomponent.getImage());
		var newLogoPath = Paths.get(webcomponent.getUuid(), WCS_LOGO_PNG);

		writeWorkspacePort.wipe(oldLogoPath);
		writeWorkspacePort.wipe(newLogoPath);

		var decodedFile = Base64.getDecoder().decode(logoPngBase64);

		try {
			ImageIO.read(new ByteArrayInputStream(decodedFile));
		}
		catch (IOException e) {
			throw new IllegalArgumentException("not an image");
		}

		if(!isPng(decodedFile)) {
			throw new IllegalArgumentException("not a png");
		}

		writeWorkspacePort.writeFile(newLogoPath, decodedFile);

		webcomponent.setImage(WCS_LOGO_PNG);
	}

	private static boolean isPng(byte[] data) {
		try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
			return is.read() == 137;
		}
		catch (IOException e) {
			return false;
		}
	}
}
