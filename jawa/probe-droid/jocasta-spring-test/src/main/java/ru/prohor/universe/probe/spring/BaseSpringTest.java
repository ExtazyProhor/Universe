package ru.prohor.universe.probe.spring;

import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource("classpath:jocasta-spring-test.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public abstract class BaseSpringTest {}
