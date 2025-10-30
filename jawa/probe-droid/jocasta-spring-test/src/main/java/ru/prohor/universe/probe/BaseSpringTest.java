package ru.prohor.universe.probe;

import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource("classpath:test.properties")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public abstract class BaseSpringTest {}
