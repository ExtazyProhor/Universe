package ru.prohor.universe.jocasta.spring.features;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;

public class PrettyJsonPrinter extends DefaultPrettyPrinter {
    public PrettyJsonPrinter() {
        indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        indentObjectsWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
    }

    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(": ");
    }

    @Override
    public DefaultPrettyPrinter createInstance() {
        return new PrettyJsonPrinter();
    }
}
