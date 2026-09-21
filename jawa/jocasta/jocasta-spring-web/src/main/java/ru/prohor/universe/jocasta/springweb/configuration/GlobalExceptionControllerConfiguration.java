package ru.prohor.universe.jocasta.springweb.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.prohor.universe.jocasta.springweb.controllers.GlobalExceptionController;

@Configuration
@Import(GlobalExceptionController.class)
public class GlobalExceptionControllerConfiguration {}
