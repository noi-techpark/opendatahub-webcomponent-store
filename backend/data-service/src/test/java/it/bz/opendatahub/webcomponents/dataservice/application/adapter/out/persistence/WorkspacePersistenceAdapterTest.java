// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.adapter.out.persistence;

import it.bz.opendatahub.webcomponents.dataservice.application.adapter.out.persistence.repository.WorkspaceRepository;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.out.persistence.repository.impl.WorkspaceRepositoryImpl;
import it.bz.opendatahub.webcomponents.dataservice.exception.impl.NotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class WorkspacePersistenceAdapterTest {
	private WorkspaceRepository workspaceRepository;

	private WorkspacePersistenceAdapter workspacePersistenceAdapter;

	@BeforeEach
	void setUp() {
		workspaceRepository = Mockito.mock(WorkspaceRepository.class);

		workspacePersistenceAdapter = new WorkspacePersistenceAdapter(workspaceRepository);
	}

	@Test
	@SneakyThrows(IOException.class)
	void readFileReturnsDataIfExisting() {
		var path = Paths.get("nope");
		var data = "theData".getBytes();

		when(workspaceRepository.readFile(path)).thenReturn(data);

		assertThat(workspacePersistenceAdapter.readFile(path)).isEqualTo(data);
	}

	@Test
	@SneakyThrows(IOException.class)
	void readFileThrowsIfNonExisting() {
		var path = Paths.get("nope");

		doThrow(new IOException())
			.when(workspaceRepository)
			.readFile(any());

		assertThatExceptionOfType(NotFoundException.class).isThrownBy(
			() -> workspacePersistenceAdapter.readFile(
				path
			)
		);
	}

	@Test
	@SneakyThrows(IOException.class)
	void writeFileDoesThrowRuntimeExceptionOnError() {
		var path = Paths.get("nope");

		doThrow(new IOException())
			.when(workspaceRepository)
			.writeFile(any(), any());

		assertThatExceptionOfType(RuntimeException.class).isThrownBy(
			() -> workspacePersistenceAdapter.writeFile(
				path, new byte[0]
			)
		);
	}
}
