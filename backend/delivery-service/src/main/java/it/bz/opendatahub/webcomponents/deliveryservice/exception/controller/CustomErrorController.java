// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.deliveryservice.exception.controller;

import io.swagger.v3.oas.annotations.Hidden;
import it.bz.opendatahub.webcomponents.deliveryservice.exception.ErrorMessage;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@Hidden
public class CustomErrorController extends ExceptionHandlerController implements ErrorController {

    private static final String PATH = "/error";

    @GetMapping(value=PATH, produces="application/json")
    @ResponseBody
    public ResponseEntity<ErrorMessage> handleGet(HttpServletRequest request) {
        return handleRequest(request);
    }

    @PostMapping(value=PATH, produces="application/json")
    @ResponseBody
    public ResponseEntity<ErrorMessage> handlePost(HttpServletRequest request) {
        return handleRequest(request);
    }

    private ResponseEntity<ErrorMessage> handleRequest(HttpServletRequest request) {
        Integer code = (Integer)request.getAttribute("jakarta.servlet.error.status_code");
        String text = String.valueOf(request.getAttribute("jakarta.servlet.error.message"));

        ErrorMessage message = new ErrorMessage();
        message.setErrorCode(code);
        message.setMessage(text);

        return new ResponseEntity<>(message, HttpStatus.valueOf(code));
    }
}
