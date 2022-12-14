package com.restservice.controllers;

import com.restservice.dto.SensorDTO;
import com.restservice.models.Sensor;
import com.restservice.services.SensorService;
import com.restservice.util.exceptions.SensorAlreadyExistsException;
import com.restservice.util.errorresponces.ErrorResponse;
import com.restservice.util.exceptions.SensorRegistrationException;
import com.restservice.util.validators.SensorDTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorsController {

    private final SensorService sensorService;
    private final SensorDTOValidator sensorDTOValidator;

    @Autowired
    public SensorsController(SensorService sensorService, SensorDTOValidator sensorDTOValidator) {
        this.sensorService = sensorService;
        this.sensorDTOValidator = sensorDTOValidator;
    }

    @GetMapping("/all")
    public List<Sensor> getJSONWithAllSensors() {
        return sensorService.getAllSensors();
    }

    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> addNewSensor(@RequestBody @Valid SensorDTO sensorDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuffer errorMsg = new StringBuffer();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new SensorRegistrationException(errorMsg.toString());
        }
        sensorService.addNewSensor(convertToSensor(sensorDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(SensorAlreadyExistsException exception) {
        ErrorResponse response = new ErrorResponse(
                "This sensor already exists!!!", System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> registrationHandler(SensorRegistrationException exception) {
        ErrorResponse response = new ErrorResponse(exception.getMessage(), System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private Sensor convertToSensor(SensorDTO sensorDTO) {
        Sensor sensor = new Sensor();
        sensor.setName(sensorDTO.getName());
        return sensor;
    }
}