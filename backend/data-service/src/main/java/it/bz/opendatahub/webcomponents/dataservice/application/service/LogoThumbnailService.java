// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.service;

import it.bz.opendatahub.webcomponents.common.util.ThumbnailUtility;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.GetWebcomponentLogoUseCase;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.ListWebcomponentUseCase;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Creates and caches the jpg thumbnails of the webcomponent logos.
 *
 * All thumbnails are created in the background on startup and refreshed periodically,
 * so the first visitor of the store does not have to wait for them to be rendered.
 */
@Slf4j
@Service
public class LogoThumbnailService {
	private static final long TTL_MS = 60 * 60 * 1000L;

	private record Entry(byte[] data, long expiresAt) {}

	private final GetWebcomponentLogoUseCase getWebcomponentLogoUseCase;
	private final ListWebcomponentUseCase listWebcomponentUseCase;
	private final int width;
	private final int height;

	private final Map<String, Entry> cache = new ConcurrentHashMap<>();
	private final ExecutorService warmUpExecutor = Executors.newSingleThreadExecutor(r -> {
		var thread = new Thread(r, "thumbnail-warmup");
		thread.setDaemon(true);
		return thread;
	});

	public LogoThumbnailService(GetWebcomponentLogoUseCase getWebcomponentLogoUseCase,
								ListWebcomponentUseCase listWebcomponentUseCase,
								@Value("${thumbnail.width}") int width,
								@Value("${thumbnail.height}") int height) {
		this.getWebcomponentLogoUseCase = getWebcomponentLogoUseCase;
		this.listWebcomponentUseCase = listWebcomponentUseCase;
		this.width = width;
		this.height = height;
	}

	public byte[] getThumbnail(String uuid) throws IOException {
		var entry = cache.get(uuid);

		if (entry == null || entry.expiresAt() < System.currentTimeMillis()) {
			entry = render(uuid);
			cache.put(uuid, entry);
		}

		return entry.data();
	}

	@EventListener(ApplicationReadyEvent.class)
	public void warmUpOnStartup() {
		warmUpExecutor.submit(this::warmUp);
	}

	/** Runs well within the ttl, so thumbnails never expire between two visits. */
	@Scheduled(initialDelay = TTL_MS / 2, fixedDelay = TTL_MS / 2)
	public void refresh() {
		warmUpExecutor.submit(this::warmUp);
	}

	@PreDestroy
	void shutdown() {
		warmUpExecutor.shutdownNow();
	}

	private void warmUp() {
		var start = System.currentTimeMillis();
		var count = 0;

		try {
			for (var webcomponent : listWebcomponentUseCase.listAll()) {
				try {
					cache.put(webcomponent.getUuid(), render(webcomponent.getUuid()));
					count++;
				} catch (Exception e) {
					log.warn("could not create thumbnail for {}: {}", webcomponent.getUuid(), e.toString());
				}
			}
		} catch (Exception e) {
			log.warn("thumbnail warm-up failed", e);
			return;
		}

		log.info("created {} thumbnails in {} ms", count, System.currentTimeMillis() - start);
	}

	private Entry render(String uuid) throws IOException {
		var logo = getWebcomponentLogoUseCase.getLogoImage(uuid);
		var image = ThumbnailUtility.createThumbnailForImage(new ByteArrayInputStream(logo), width, height);

		// spread expiry a bit so entries rendered on demand do not all expire together
		var jitter = (long) (Math.random() * 90_000) + 10_000;
		return new Entry(ThumbnailUtility.toJpg(image).toByteArray(), System.currentTimeMillis() + TTL_MS + jitter);
	}
}
