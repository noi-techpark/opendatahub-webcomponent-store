// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.service;

import it.bz.opendatahub.webcomponents.dataservice.application.service.EmailService;
import it.bz.opendatahub.webcomponents.dataservice.config.MailerConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import static org.apache.commons.codec.CharEncoding.UTF_8;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceTest {
	private static final String VALID_TO = "test@test.test";
	private static final String VALID_FROM = "from@test.test";
	private static final String VALID_SUBJECT = "test";
	private static final String VALID_PAYLOAD = "test";

	private JavaMailSender javaMailSender;

	private EmailService emailService;

	private final MailerConfig mailerConfig = new MailerConfig();

	@BeforeEach
	void setUp() {
		mailerConfig.setFrom(VALID_FROM);
		javaMailSender = Mockito.mock(JavaMailSender.class);
		emailService = new EmailService(javaMailSender, mailerConfig);
	}

	@Test
	void sendMailThrowsIfToIsNull() {
		assertThatNullPointerException().isThrownBy(
			() -> emailService.sendPlaintextEmail(null, VALID_SUBJECT, VALID_PAYLOAD)
		);
	}

	@Test
	void sendMailThrowsIfToIsEmpty() {
		assertThatIllegalArgumentException().isThrownBy(
			() -> emailService.sendPlaintextEmail("", VALID_SUBJECT, VALID_PAYLOAD)
		);
	}

	@Test
	void sendMailThrowsIfToIsBlank() {
		assertThatIllegalArgumentException().isThrownBy(
			() -> emailService.sendPlaintextEmail("      ", VALID_SUBJECT, VALID_PAYLOAD)
		);
	}

	@ParameterizedTest
	@ValueSource(strings = {"sepp.bz", "@asd", "@asd.to", "ber t@bert.bert", "hans@ null", "hans@null bla", "lol", "hias@to.", "@.", "@", ".", "hans@bla@bla"})
	void sendMailThrowsIfToIsNotValidEmailFormat(String to) {
		assertThatIllegalArgumentException().isThrownBy(
			() -> emailService.sendPlaintextEmail(to, VALID_SUBJECT, VALID_PAYLOAD)
		);
	}

	@Test
	void sendMailThrowsIfSubjectIsNull() {
		assertThatNullPointerException().isThrownBy(
			() -> emailService.sendPlaintextEmail(VALID_TO, null, VALID_PAYLOAD)
		);
	}

	@Test
	void sendMailThrowsIfSubjectIsEmpty() {
		assertThatIllegalArgumentException().isThrownBy(
			() -> emailService.sendPlaintextEmail(VALID_TO, "", VALID_PAYLOAD)
		);
	}

	@Test
	void sendMailThrowsIfSubjectIsBlank() {
		assertThatIllegalArgumentException().isThrownBy(
			() -> emailService.sendPlaintextEmail(VALID_TO, "      ", VALID_PAYLOAD)
		);
	}

	@Test
	void sendMailThrowsIfPayloadIsNull() {
		assertThatNullPointerException().isThrownBy(
			() -> emailService.sendPlaintextEmail(VALID_TO, VALID_SUBJECT, null)
		);
	}

	@Test
	void sendMailThrowsIfPayloadIsEmpty() {
		assertThatIllegalArgumentException().isThrownBy(
			() -> emailService.sendPlaintextEmail(VALID_TO, VALID_SUBJECT, "")
		);
	}

	@Test
	void sendMailThrowsIfPayloadIsBlank() {
		assertThatIllegalArgumentException().isThrownBy(
			() -> emailService.sendPlaintextEmail(VALID_TO, VALID_SUBJECT, "      ")
		);
	}

	@Test
	void actualSendMethodGetsCalledExactlyOnce() {
		when(javaMailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

		emailService.sendPlaintextEmail(VALID_TO, VALID_SUBJECT, VALID_PAYLOAD);

		verify(javaMailSender, times(1)).send(any(MimeMessage.class));
	}

	@Test
	@SneakyThrows
	void actualSendMethodGetsCalledWithCorrectlySetMimeMessage() {
		var mimeMessage = Mockito.mock(MimeMessage.class);

		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		emailService.sendPlaintextEmail(VALID_TO, VALID_SUBJECT, VALID_PAYLOAD);

		verify(mimeMessage, times(1)).setRecipient(eq(Message.RecipientType.TO), any(InternetAddress.class));
		verify(mimeMessage, times(1)).setText(VALID_PAYLOAD, UTF_8);
		verify(mimeMessage, times(1)).setSubject(VALID_SUBJECT, UTF_8);
		verify(mimeMessage, times(1)).setFrom(any(InternetAddress.class));

		verify(javaMailSender, times(1)).send(mimeMessage);
	}
}
