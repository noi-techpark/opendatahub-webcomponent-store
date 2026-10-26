// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.service;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import it.bz.opendatahub.webcomponents.dataservice.application.domain.GoogleLighthouseMetrics;
import it.bz.opendatahub.webcomponents.dataservice.application.domain.WebcomponentVersion;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.UpdateWebcomponentVersionMetricsUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.GoogleLighthousePort;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.WriteWebcomponentVersionPort;
import it.bz.opendatahub.webcomponents.dataservice.exception.impl.MetricsErrorException;
import it.bz.opendatahub.webcomponents.dataservice.exception.impl.MetricsInvalidRequestException;
import it.bz.opendatahub.webcomponents.dataservice.exception.impl.MetricsUnavailableException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
public class WebcomponentVersionMetricsService implements UpdateWebcomponentVersionMetricsUseCase {
	@Value("${application.previewBaseUrl}")
	private String previewBaseUrl;

	private final GoogleLighthousePort googleLighthousePort;
	private final WriteWebcomponentVersionPort writeWebcomponentVersionPort;

	public WebcomponentVersionMetricsService(GoogleLighthousePort googleLighthousePort, WriteWebcomponentVersionPort writeWebcomponentVersionPort) {
		this.googleLighthousePort = googleLighthousePort;
		this.writeWebcomponentVersionPort = writeWebcomponentVersionPort;
	}

	@Override
	public WebcomponentVersion updateMetricsForWebcomponentVersion(@NonNull WebcomponentVersion webcomponentVersion) {
		log.debug(
			"updating metrics for: {}/{}",
			webcomponentVersion.getWebcomponentUuid(),
			webcomponentVersion.getVersionTag()
		);

		var previewUrl = getPreviewUrlForWebcomponentVersion(webcomponentVersion);

		try {
			loadAndUpdateMetricsData(webcomponentVersion, previewUrl);

			log.debug("metrics gathered");

			return writeWebcomponentVersionPort.saveWebcomponentVersion(
				webcomponentVersion
			);
		}
		catch (MetricsErrorException e) {
			log.error(
				"metrics failed for: {}/{}; {}",
				webcomponentVersion.getWebcomponentUuid(),
				webcomponentVersion.getVersionTag(),
				e.getMessage()
			);

			return webcomponentVersion;
		}
	}

	private void loadAndUpdateMetricsData(WebcomponentVersion webcomponentVersion, String previewUrl) throws MetricsErrorException {
		try {
			var metrics = fetchMetrics(previewUrl);

			webcomponentVersion.setLighthouseMetricsMobileData(metrics.getResultMobile());
			webcomponentVersion.setLighthouseMetricsDesktopData(metrics.getResultMobile());

			updateRatings(webcomponentVersion, metrics);

			webcomponentVersion.setLighthouseMetricsDesktopDatetime(LocalDateTime.now());
			webcomponentVersion.setLighthouseMetricsMobileDatetime(LocalDateTime.now());

			webcomponentVersion.setLighthouseUpdateRequired(false);
		}
		catch (MetricsUnavailableException | MetricsInvalidRequestException e) {
			webcomponentVersion.setLighthouseMetricsMobileData(null);
			webcomponentVersion.setLighthouseMetricsDesktopData(null);

			webcomponentVersion.setLighthouseDesktopPerformanceRating(0);
			webcomponentVersion.setLighthouseMobilePerformanceRating(0);

			webcomponentVersion.setLighthouseMetricsDesktopDatetime(LocalDateTime.now());
			webcomponentVersion.setLighthouseMetricsMobileDatetime(LocalDateTime.now());

			webcomponentVersion.setLighthouseUpdateRequired(false);

			log.error(
				"metrics unobtainable for: {}/{}; {}",
				webcomponentVersion.getWebcomponentUuid(),
				webcomponentVersion.getVersionTag(),
				e.getMessage()
			);
		}
	}

	private void updateRatings(WebcomponentVersion webcomponentVersion, GoogleLighthouseMetrics metrics) {
		try {
			webcomponentVersion.setLighthouseMobilePerformanceRating(
				extractPerformanceRating(
					metrics.getResultMobile()
				)
			);
		} catch (MetricsParseException e) {
			webcomponentVersion.setLighthouseMobilePerformanceRating(0);
		}

		try {
			webcomponentVersion.setLighthouseDesktopPerformanceRating(
				extractPerformanceRating(
					metrics.getResultDesktop()
				)
			);
		} catch (MetricsParseException e) {
			webcomponentVersion.setLighthouseDesktopPerformanceRating(0);
		}
	}

	private GoogleLighthouseMetrics fetchMetrics(String previewUrl) throws MetricsUnavailableException, MetricsInvalidRequestException, MetricsErrorException {
		return googleLighthousePort.getMetricsForUrl(previewUrl);
	}

	private String getPreviewUrlForWebcomponentVersion(WebcomponentVersion webcomponentVersion) {
		return previewBaseUrl + "/" + webcomponentVersion.getWebcomponentUuid() + "/" + webcomponentVersion.getVersionTag();
	}

	private int extractPerformanceRating(String json) throws MetricsParseException {
		Number rating;
		try {
			rating = JsonPath.read(json, "$.lighthouseResult.categories.performance.score");
		}
		catch (PathNotFoundException e) {
			throw new MetricsParseException();
		}

		if(rating == null) {
			throw new MetricsParseException();
		}

		return (int) (rating.doubleValue() * 100);
	}

	private static class MetricsParseException extends Exception {}
}
