// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.adapter.out.persistence.repository.impl;

import it.bz.opendatahub.webcomponents.dataservice.config.WorkspaceConfiguration;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.*;

class WorkspaceRepositoryImplTest {
	private static final Path TESTDATA_PATH = Paths.get(".", "testdata");

	private WorkspaceRepositoryImpl workspaceRepository;

	@BeforeEach
	void setUp() throws IOException {
		FileUtils.deleteQuietly(TESTDATA_PATH.toFile());
		FileUtils.forceMkdir(TESTDATA_PATH.toFile());

		var config = new WorkspaceConfiguration();
		config.setPath(TESTDATA_PATH.toString());

		workspaceRepository = new WorkspaceRepositoryImpl(config);
	}

	@Test
	void readFileThrowsIfNullArgument() {
		assertThatNullPointerException().isThrownBy(
			() -> workspaceRepository.readFile(null)
		);
	}

	@Test
	void readFileThrowsIfNonExistingFile() {
		var thePath = Paths.get(TESTDATA_PATH.toString(), "readFileThrowsIfNonExistingFile");

		assertThatIOException().isThrownBy(
			() -> workspaceRepository.readFile(thePath)
		);
	}

	@Test
	@SneakyThrows(IOException.class)
	void readFileReturnsDataForExistingfile() {
		var thePath = Paths.get(TESTDATA_PATH.toString(), "readFileReturnsDataForExistingfile");

		FileUtils.writeByteArrayToFile(thePath.toFile(), "theContent".getBytes());

		assertThat(new String(workspaceRepository.readFile(Paths.get("readFileReturnsDataForExistingfile")))).isEqualTo("theContent");
	}

	@Test
	void writeFileThrowsIfNullArgument() {
		assertThatNullPointerException().isThrownBy(
			() -> workspaceRepository.writeFile(null, new byte[0])
		);
	}

	@Test
	@SneakyThrows(IOException.class)
	void writeFileDoesNotThrowIfAlreadyExists() {
		var thePath = Paths.get(TESTDATA_PATH.toString(), "writeFileDoesNotThrowIfAlreadyExists");

		FileUtils.writeByteArrayToFile(thePath.toFile(), "theContent".getBytes());

		assertThatCode(
			() -> workspaceRepository.writeFile(Paths.get("writeFileDoesNotThrowIfAlreadyExists"), "theNewContent".getBytes())
		).doesNotThrowAnyException();
	}

	@Test
	@SneakyThrows(IOException.class)
	void writeFileDoesActuallyWriteTheFile() {
		var thePath = Paths.get(TESTDATA_PATH.toString(), "writeFileDoesActuallyWriteTheFile");

		FileUtils.writeByteArrayToFile(thePath.toFile(), "theContent".getBytes());

		workspaceRepository.writeFile(Paths.get("writeFileDoesActuallyWriteTheFile"), "theNewContent".getBytes());

		assertThat(new String(FileUtils.readFileToByteArray(thePath.toFile()))).isEqualTo("theNewContent");
	}

	@Test
	void deletePathThrowsIfNullArgument() {
		assertThatNullPointerException().isThrownBy(
			() -> workspaceRepository.deletePath(null)
		);
	}

	@Test
	void deletePathDoesNotThrowIfNotExists() {
		assertThatCode(
			() -> workspaceRepository.deletePath(Paths.get("deletePathDoesNotThrowIfNotExists"))
		).doesNotThrowAnyException();
	}

	@Test
	@SneakyThrows(IOException.class)
	void deletePathDoesActuallyDeleteThePathIfPathIsAFile() {
		var thePath = Paths.get(TESTDATA_PATH.toString(), "deletePathDoesActuallyDeleteThePathIfPathIsAFile");

		FileUtils.writeByteArrayToFile(thePath.toFile(), "theContent".getBytes());

		assertThat(thePath.toFile().exists()).isTrue();

		workspaceRepository.deletePath(Paths.get("deletePathDoesActuallyDeleteThePathIfPathIsAFile"));

		assertThat(thePath.toFile().exists()).isFalse();
	}

	@Test
	@SneakyThrows(IOException.class)
	void deletePathDoesActuallyDeleteThePathIfPathIsADirectory() {
		var thePath = Paths.get(TESTDATA_PATH.toString(), "deletePathDoesActuallyDeleteThePathIfPathIsADirectory");

		FileUtils.forceMkdir(thePath.toFile());

		assertThat(thePath.toFile().exists()).isTrue();

		workspaceRepository.deletePath(Paths.get("deletePathDoesActuallyDeleteThePathIfPathIsADirectory"));

		assertThat(thePath.toFile().exists()).isFalse();
	}

	@Test
	void getDirectorySizeInBytesThrowsIfNullArgument() {
		assertThatNullPointerException().isThrownBy(
			() -> workspaceRepository.getDirectorySizeInBytes(null)
		);
	}

	@Test
	@SneakyThrows(IOException.class)
	void getDirectorySizeInBytesDoesReturnSizeForExistingDirectory() {
		var thePath = Paths.get(TESTDATA_PATH.toString(), "getDirectorySizeInBytesDoesReturnSizeForExistingDirectory");

		FileUtils.forceMkdir(thePath.toFile());

		FileUtils.writeByteArrayToFile(Paths.get(thePath.toString(), "a.file").toFile(), "theContent".getBytes());

		assertThat(workspaceRepository.getDirectorySizeInBytes(Paths.get("getDirectorySizeInBytesDoesReturnSizeForExistingDirectory"))).isEqualTo(10);
	}

	@Test
	void getDirectorySizeInBytesDoesReturnZeroIfPathDoesNotExist() {
		assertThat(workspaceRepository.getDirectorySizeInBytes(Paths.get("getDirectorySizeInBytesDoesReturnZeroIfPathDoesNotExist"))).isZero();
	}

	@Test
	@SneakyThrows(IOException.class)
	void getDirectorySizeInBytesThrowsIfPathIsAFile() {
		var thePath = Paths.get(TESTDATA_PATH.toString(), "getDirectorySizeInBytesThrowsIfPathIsAFile");

		FileUtils.forceMkdir(thePath.toFile());

		FileUtils.writeByteArrayToFile(Paths.get(thePath.toString(), "a.file").toFile(), "theContent".getBytes());

		assertThatIllegalArgumentException().isThrownBy(
			() -> workspaceRepository.getDirectorySizeInBytes(Paths.get("getDirectorySizeInBytesThrowsIfPathIsAFile", "a.file"))
		);
	}
}
