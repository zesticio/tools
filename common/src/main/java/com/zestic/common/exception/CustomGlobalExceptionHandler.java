/*
 * Version:  1.0.0
 *
 * Authors:  Kumar <Deebendu Kumar>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zestic.common.exception;

import com.zestic.common.entity.Result;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CustomGlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        Result<List<String>> result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        result.setData(errors);
        return new ResponseEntity<>(result, headers, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Result> constraintViolationException(ApplicationRuntimeException ex, WebRequest request) throws IOException {
        logger.error("Constraint violation exception", ex);
        Result<Void> result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        ResponseEntity<Result> response = new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        return response;
    }

    @ExceptionHandler(ApplicationRuntimeException.class)
    public final ResponseEntity<Result> applicationRuntimeExceptionHandler(ApplicationRuntimeException ex, WebRequest request) {
        logger.error("Application runtime exception", ex);
        Result<Void> result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        ResponseEntity<Result> response = new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        return response;
    }

    @ExceptionHandler(ApplicationException.class)
    public final ResponseEntity<Result> applicationExceptionHandler(ApplicationException ex, WebRequest request) {
        logger.error("Application exception", ex);
        Result<Void> result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        ResponseEntity<Result> response = new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        return response;
    }

    @ExceptionHandler(Exception.class)
    @Order(Ordered.LOWEST_PRECEDENCE)
    public final ResponseEntity<Result> exceptionHandler(Exception ex, WebRequest request) {
        logger.error("Internal error.", ex);
        Result<Void> result = new Result(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        ResponseEntity<Result> response = new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        return response;
    }
}
