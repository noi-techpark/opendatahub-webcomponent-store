// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.service;

import it.bz.opendatahub.webcomponents.dataservice.application.port.in.SendEmailUseCase;
import it.bz.opendatahub.webcomponents.dataservice.config.MailerConfig;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static org.apache.commons.codec.CharEncoding.UTF_8;

@Service
public class EmailService implements SendEmailUseCase {
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
		Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	private final JavaMailSender emailSender;
	private final MailerConfig mailerConfig;

	public EmailService(JavaMailSender emailSender, MailerConfig mailerConfig) {
		this.emailSender = emailSender;
		this.mailerConfig = mailerConfig;
	}

	@Override
	@SneakyThrows
	public void sendPlaintextEmail(@NonNull String to, @NonNull String subject, @NonNull String payload) {
		if(to.trim().isEmpty()) {
			throw new IllegalArgumentException();
		}

		if(subject.trim().isEmpty()) {
			throw new IllegalArgumentException();
		}

		if(payload.trim().isEmpty()) {
			throw new IllegalArgumentException();
		}

		if(!validateEmail(to)) {
			throw new IllegalArgumentException();
		}

		var mimeMessage = emailSender.createMimeMessage();
		var helper = new MimeMessageHelper(mimeMessage, false, UTF_8);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(payload);
		helper.setFrom(mailerConfig.getFrom());

		emailSender.send(mimeMessage);
	}

	public static boolean validateEmail(String emailAddress) {
		var matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress);

		return matcher.find();
	}
}
