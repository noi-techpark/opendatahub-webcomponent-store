// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.bz.opendatahub.webcomponents.dataservice.application.adapter.in.web.ContactFormController;
import it.bz.opendatahub.webcomponents.dataservice.application.port.in.SendEmailUseCase;
import it.bz.opendatahub.webcomponents.dataservice.config.MailerConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ContactFormControllerTest {
	private static final String VALID_TO = "test@test.test";
	private static final String VALID_FROM = "from@test.test";
	private static final String VALID_SUBJECT = "testsubject";

	private SendEmailUseCase sendEmailUseCase;
	private ContactFormController contactFormController;

	private final MailerConfig mailerConfig = new MailerConfig();

	@BeforeEach
	void setUp() {
		mailerConfig.setFrom(VALID_FROM);
		mailerConfig.setTo(VALID_TO);
		mailerConfig.setSubject(VALID_SUBJECT);

		sendEmailUseCase = Mockito.mock(SendEmailUseCase.class);
		contactFormController = new ContactFormController(sendEmailUseCase, mailerConfig);
	}

	@Test
	void nullRequestThrows() {
		assertThatNullPointerException().isThrownBy(
			() -> contactFormController.sendContactForm(null)
		);
	}

	@Test
	void controllerCallsServiceOnce() {
		contactFormController.sendContactForm(new ContactFormController.ContactFormRequest());

		verify(sendEmailUseCase, times(1)).sendPlaintextEmail(anyString(), anyString(), anyString());
	}

	@Test
	void emailIsSentToConfiguredRecipient() {
		contactFormController.sendContactForm(new ContactFormController.ContactFormRequest());

		verify(sendEmailUseCase, times(1)).sendPlaintextEmail(eq(VALID_TO), anyString(), anyString());
	}

	@Test
	void emailSubjectIsSetToConfiguredValue() {
		contactFormController.sendContactForm(new ContactFormController.ContactFormRequest());

		verify(sendEmailUseCase, times(1)).sendPlaintextEmail(anyString(), eq(VALID_SUBJECT), anyString());
	}

	@Test
	@SneakyThrows
	void emailTextContainsRequestData() {
		var request = new ContactFormController.ContactFormRequest();
		request.setEmail("theMail");
		request.setCategory("theCategory");
		request.setPhone("thePhone");
		request.setNameFirst("theNameFirst");
		request.setNameLast("theNameLast");
		request.setText("theText");


		contactFormController.sendContactForm(request);

		Map<String, String> map = new ObjectMapper().convertValue(request, Map.class);

		//var requestAsText = new ObjectMapper().writeValueAsString(request);

		var captur = ArgumentCaptor.forClass(String.class);
		verify(sendEmailUseCase).sendPlaintextEmail(anyString(), anyString(), captur.capture());

		for(var field : map.entrySet()) {
			if(field.getValue() != null) {
				assertThat(captur.getValue()).contains(field.getValue());
			}
		}
	}
}
