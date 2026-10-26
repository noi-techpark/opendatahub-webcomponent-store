// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.adapter.out.lighthouse;

import it.bz.opendatahub.webcomponents.common.stereotype.Adapter;
import it.bz.opendatahub.webcomponents.dataservice.application.domain.GoogleLighthouseMetrics;
import it.bz.opendatahub.webcomponents.dataservice.application.port.out.GoogleLighthousePort;
import it.bz.opendatahub.webcomponents.dataservice.exception.impl.MetricsErrorException;
import it.bz.opendatahub.webcomponents.dataservice.exception.impl.MetricsInvalidRequestException;
import it.bz.opendatahub.webcomponents.dataservice.exception.impl.MetricsUnavailableException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Adapter
@Slf4j
public class GoogleLighthouseAdapter implements GoogleLighthousePort {
	@Value("${application.google.lighthouse.api-key:null}")
	private String apiKey;

	private final RestTemplate restTemplate;

	public GoogleLighthouseAdapter(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public GoogleLighthouseMetrics getMetricsForUrl(@NonNull String url) throws MetricsInvalidRequestException, MetricsUnavailableException, MetricsErrorException {
		if(url.trim().isEmpty()) {
			throw new IllegalArgumentException();
		}

		var lighthouseUrl = "https://www.googleapis.com/pagespeedonline/v5/runPagespeed?url=" + url + "&key=" + apiKey;

		log.debug(lighthouseUrl);

		var desktop = fetchMetricsFromApi(lighthouseUrl + "&strategy=desktop");
		var mobile = fetchMetricsFromApi(lighthouseUrl + "&strategy=mobile");

		return new GoogleLighthouseMetrics(desktop, mobile);
	}

	private String fetchMetricsFromApi(@NonNull String url) throws MetricsInvalidRequestException, MetricsUnavailableException, MetricsErrorException {
		try {
			return restTemplate.getForObject(url, String.class);
		}
		catch (HttpClientErrorException clientErrorException) {
			throw new MetricsInvalidRequestException(clientErrorException);
		}
		catch (HttpServerErrorException httpServerErrorException) {
			if(httpServerErrorException.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
				throw new MetricsUnavailableException(httpServerErrorException);
			}

			throw new MetricsErrorException(httpServerErrorException);
		}
	}
}
