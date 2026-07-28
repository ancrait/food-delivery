package com.sorokaandriy.restaurant_service.exception;

import com.sorokaandriy.restaurant_service.dto.ErrorResponseDto;
import com.sorokaandriy.restaurant_service.dto.ErrorValidationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleRestaurantNotFoundException(RestaurantNotFoundException e){

        var errorDto = new ErrorResponseDto(
                e.getMessage(),
                Instant.now());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDto);
    }


    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleMenuItemNotFoundException(MenuItemNotFoundException e){

        var errorDto = new ErrorResponseDto(
                e.getMessage(),
                Instant.now());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDto);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e){

        var errorDto = new ErrorResponseDto(
                e.getMessage(),
                Instant.now());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDto);
    }


    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCategoryNotFoundException(CategoryNotFoundException e){

        var errorDto = new ErrorResponseDto(
                e.getMessage(),
                Instant.now());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorDto);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception e){

        var errorDto = new ErrorResponseDto(
                e.getMessage(),
                Instant.now());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorDto);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorValidationResponse> handleValidationException(MethodArgumentNotValidException e){

        Map<String,String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var fieldName = ((FieldError)error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.put(fieldName,errorMessage);
                });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorValidationResponse(errors));
    }



}
