package ru.prohor.universe.padawan.scripts;


import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import ru.prohor.universe.jocasta.string.SimpleTemplateEngine;
import ru.prohor.universe.jocasta.utils.NamingStyleUtils;
import ru.prohor.universe.jocasta.utils.SystemUtils;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ModulesManager {
    public static void main(String[] args) throws Exception {
        String s = Files.readString(Path.of(ModulesManager.class.getResource("").toURI()));
        System.out.println(s);
        System.out.println("\n\n\n\n");
        String s2 = new SimpleTemplateEngine(
                Set.of("spring"),
                Map.of("main", "MainTest", "package", "ru.prohor.universe.pkg")
        ).process(s);
        System.out.println(s2);
        System.out.println("\n\n\n\n");
        String s3 = new SimpleTemplateEngine(
                Set.of(),
                Map.of("main", "MainTest2", "package", "ru.prohor.universe.pkg2")
        ).process(s);
        System.out.println(s3);


        //generateProject("star killer", "1.0.1", true, false);
    }

    private static final OutputFormat XML_OUTPUT_FORMAT = OutputFormat.createPrettyPrint();
    private static final String POM_XML = "pom.xml";
    private static final String MODULES = "modules";
    private static final String MODULE = "module";
    private static final String JOCASTA = "jocasta";
    private static final String VERSION = "version";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String NAME = "name";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String DEPENDENCIES = "dependencies";
    private static final String DEPENDENCY = "dependency";
    private static final String ATTRIBUTES = "attributes";
    private static final String ATTRIBUTE = "attribute";
    private static final String BUILD = "build";
    private static final String UNIVERSE_VALUE = "universeValue";
    private static final String UNIVERSE_IF = "universeIf";
    private static final String POM_XML_TEMPLATE = "/pom-template.xml";
    private static final String TEMPLATE_JAVA_MAIN = "/main-template.java";
    private static final String USE_JOCASTA = "jocastaUsed";
    private static final String USE_BUILD = "buildUsed";
    private static final String USE_SPRING = "springUsed";
    private static final String MAIN_CLASS = "mainClass";
    private static final String MAIN_CLASS_SUFFIX = " main";
    private static final String[] PACKAGE_DIRS = {
            "ru", "prohor", "universe"
    };
    private static final Path SRC_PATH = Paths.get(
            "src", "main", "java", "ru", "prohor", "universe"
    );

    static {
        XML_OUTPUT_FORMAT.setIndentSize(4);
        XML_OUTPUT_FORMAT.setNewlines(true);
        XML_OUTPUT_FORMAT.setTrimText(true);
        XML_OUTPUT_FORMAT.setSuppressDeclaration(false);
        XML_OUTPUT_FORMAT.setPadText(true);
    }

    private static void addChildModuleToJawaPom(String moduleName) throws Exception {
        File pomFile = SystemUtils.userDirPath().resolve(POM_XML).toFile();
        Document document = new SAXReader().read(pomFile);
        Element root = document.getRootElement();
        Namespace ns = root.getNamespace();

        Element modules = root.element(QName.get(MODULES, ns));
        List<String> names = new ArrayList<>(
                modules.elements(QName.get(MODULE, ns))
                        .stream()
                        .map(Element::getTextTrim)
                        .toList()
        );
        names.add(moduleName);
        Collections.sort(names);
        modules.clearContent();
        names.forEach(name -> modules.addElement(QName.get(MODULE, ns)).setText(name));

        XMLWriter writer = new XMLWriter(new FileWriter(pomFile), XML_OUTPUT_FORMAT);
        writer.write(document);
        writer.close();
    }

    private static String getJocastaVersion() throws Exception {
        Document document = new SAXReader().read(SystemUtils.userDirPath().resolve(JOCASTA).resolve(POM_XML).toFile());
        Element root = document.getRootElement();
        Namespace ns = root.getNamespace();

        Element version = root.element(QName.get(VERSION, ns));
        return version.getTextTrim();
    }

    private static void createPom(
            File pomFile,
            String name,
            String kebabName,
            String version,
            boolean useJocasta,
            boolean useBuilding)
            throws Exception
    {


        Document document = new SAXReader().read(ModulesManager.class.getResourceAsStream(POM_XML_TEMPLATE));
        Element root = document.getRootElement();
        Namespace ns = root.getNamespace();

        setElementText(root, ARTIFACT_ID, kebabName, ns);
        setElementText(root, VERSION, version, ns);
        setElementText(root, NAME, name, ns);

        Element dependencies = root.element(QName.get(DEPENDENCIES, ns));
        if (useJocasta)
            setElementText(dependencies.element(QName.get(DEPENDENCY, ns)), VERSION, getJocastaVersion(), ns);
        else
            root.remove(dependencies);

        if (!useBuilding)
            root.remove(root.element(QName.get(BUILD, ns)));

        XMLWriter writer = new XMLWriter(
                new FileWriter(pomFile),
                XML_OUTPUT_FORMAT
        );
        writer.write(document);
        writer.close();
    }

    private static void createPom(
            File pomFile,
            Map<String, String> values,
            Set<String> retained)
            throws Exception
    {
        Document document = new SAXReader().read(ModulesManager.class.getResourceAsStream(POM_XML_TEMPLATE));
        Element root = document.getRootElement();

        Optional.ofNullable(root.element(ATTRIBUTES)).ifPresent(attributes -> {
            attributes.elementIterator(ATTRIBUTE).forEachRemaining(
                    attribute -> root.addAttribute(
                            attribute.elementTextTrim(KEY),
                            attribute.elementTextTrim(VALUE)
                    )
            );
            root.remove(attributes);
        });
        remove(root, retained);
        replace(root, values);

        XMLWriter writer = new XMLWriter(
                new FileWriter(pomFile),
                XML_OUTPUT_FORMAT
        );
        writer.write(document);
        writer.close();
    }

    private static void replace(Element element, Map<String, String> values) {
        if (element.attribute(UNIVERSE_VALUE) != null) {
            Optional.ofNullable(values.get(element.attributeValue(UNIVERSE_VALUE)))
                    .ifPresent(element::setText);
            element.remove(element.attribute(UNIVERSE_VALUE));
        }
        element.elementIterator().forEachRemaining(next -> replace(next, values));
    }

    private static void remove(Element element, Set<String> retained) {
        List<Element> toRemove = new ArrayList<>();
        element.elementIterator().forEachRemaining(child -> {
            if (Optional.ofNullable(child.attributeValue(UNIVERSE_IF)).map(condition -> {
                if (!retained.contains(condition)) {
                    toRemove.add(child);
                    return Boolean.FALSE;
                }
                child.remove(child.attribute(UNIVERSE_IF));
                return Boolean.TRUE;
            }).orElse(Boolean.TRUE))
            {
                remove(child, retained);
            }
        });
        toRemove.forEach(element::remove);
    }

    private static void setElementText(Element root, String name, String text, Namespace ns) {
        Optional.ofNullable(root.element(QName.get(name, ns)))
                .ifPresent(element -> element.setText(text));
    }

    public static void generateProject(
            String name,
            String version,
            boolean useJocasta,
            boolean useBuilding)
            throws Exception
    {
        String kebabName = NamingStyleUtils.changeStyle(
                NamingStyleUtils.NamingStyle.LOWER_CASE,
                NamingStyleUtils.NamingStyle.KEBAB_CASE,
                name
        );
        addChildModuleToJawaPom(kebabName);
        Path moduleDir = SystemUtils.userDirPath().resolve(kebabName);
        Files.createDirectories(moduleDir);
        Files.createDirectories(moduleDir.resolve(SRC_PATH).resolve(
                NamingStyleUtils.changeStyle(
                        NamingStyleUtils.NamingStyle.LOWER_CASE,
                        NamingStyleUtils.NamingStyle.CAMEL_CASE,
                        name
                )
        ));

        createPom(
                moduleDir.resolve(POM_XML).toFile(),
                name,
                kebabName,
                version,
                useJocasta,
                useBuilding
        );
    }

    public static void generateProject(
            String name,
            String version,
            boolean useJocasta,
            boolean useBuilding,
            boolean useSpring)
            throws Exception
    {
        String kebabName = NamingStyleUtils.changeStyle(
                NamingStyleUtils.NamingStyle.LOWER_CASE,
                NamingStyleUtils.NamingStyle.KEBAB_CASE,
                name
        );
        String pascalName = NamingStyleUtils.changeStyle(
                NamingStyleUtils.NamingStyle.LOWER_CASE,
                NamingStyleUtils.NamingStyle.PASCAL_CASE,
                name
        );
        String camelName = NamingStyleUtils.changeStyle(
                NamingStyleUtils.NamingStyle.LOWER_CASE,
                NamingStyleUtils.NamingStyle.CAMEL_CASE,
                name
        );

        addChildModuleToJawaPom(kebabName);
        Path moduleDir = SystemUtils.userDirPath().resolve(kebabName);
        Files.createDirectories(moduleDir);
        Path srcDir = moduleDir.resolve(SRC_PATH).resolve(camelName);
        Files.createDirectories(srcDir);

        Set<String> retained = Map.of(
                        USE_JOCASTA, useJocasta,
                        USE_BUILD, useBuilding,
                        USE_SPRING, useSpring
                )
                .entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Map<String, String> values = new HashMap<>();
        values.put(ARTIFACT_ID, "");
        values.put(VERSION, "");
        values.put(NAME, "");
        values.put(MAIN_CLASS, "");

        createPom(
                moduleDir.resolve(POM_XML).toFile(),
                name,
                kebabName,
                version,
                useJocasta,
                useBuilding
        );

        SimpleTemplateEngine templateEngine = new SimpleTemplateEngine(retained, values);

    }
}
