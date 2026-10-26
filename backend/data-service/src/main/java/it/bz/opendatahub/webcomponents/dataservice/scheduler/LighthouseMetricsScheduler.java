// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.scheduler;

import it.bz.opendatahub.webcomponents.dataservice.application.port.in.UpdateWebcomponentVersionMetricsUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.ReadWebcomponentVersionPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LighthouseMetricsScheduler {
	private final ReadWebcomponentVersionPort readWebcomponentVersionPort;
	private final UpdateWebcomponentVersionMetricsUseCase updateWebcomponentVersionMetricsUseCase;

	public LighthouseMetricsScheduler(ReadWebcomponentVersionPort readWebcomponentVersionPort, UpdateWebcomponentVersionMetricsUseCase updateWebcomponentVersionMetricsUseCase) {
		this.readWebcomponentVersionPort = readWebcomponentVersionPort;
		this.updateWebcomponentVersionMetricsUseCase = updateWebcomponentVersionMetricsUseCase;
	}

	@Scheduled(fixedDelayString = "60000")
	public void updateLighthouseMetrics() throws InterruptedException {
		var allVersionsToUpdate = readWebcomponentVersionPort.getAllWithScheduledLighthouseUpdate();

 		for(var webcomponentVersion : allVersionsToUpdate) {
			updateWebcomponentVersionMetricsUseCase.updateMetricsForWebcomponentVersion(webcomponentVersion);
		}

		log.debug("all metrics up to date");
	}
}
