package com.github.onlycrab.argParser.arguments;

import com.github.onlycrab.argParser.arguments.converter.ValueConverter;
import com.github.onlycrab.argParser.arguments.dependencies.Conflicts;
import com.github.onlycrab.argParser.arguments.dependencies.Requirements;
import com.github.onlycrab.argParser.arguments.dependencies.ReturnedBoolean;
import com.github.onlycrab.argParser.arguments.dependencies.Rule;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentNotFoundException;
import com.github.onlycrab.argParser.arguments.xml.XmlWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An object that store and control all arguments data, provides the following capabilities:<br>
 * 1.Define a list of possible arguments;<br>
 * 2.Set default value to argument if its value was not passed for parsing;<br>
 * 3.Checking argument definition;<br>
 * 4.Checking argument declarations;<br>
 * 5.Check whether arguments are filled;<br>
 * 6.Defining and checking a dependency of arguments (if the dependent argument is filled,
 * the argument that depends on must also be filled );<br>
 * 7.Defining and checking a conflict between arguments (only one of the conflicting can be
 * filled at a time);<br>
 * 8.Defining and checking a custom rules;<br>
 * 9.Determination of informational data about the project, which does not affect the parsing
 * process in any way;<br>
 * 10.Help display.
 *
 * <p>The following is more detailed information about the possibilities.</p>
 *
 * <p>1.In order to parse the list of arguments, you first need to configure this object:
 * specify all possible arguments and their dependencies among themselves. This can be done
 * in two ways:<br>
 * - Create an empty object using {@link ArgumentStorage#ArgumentStorage()}. Then you need to add information
 * about each possible argument one by one using {@link ArgumentStorage#add(Argument)}. Also, if necessary,
 * you need to specify all the dependencies of the arguments {@link ArgumentStorage#addDependence(String, String)}
 * and {@link ArgumentStorage#addConflict(String, String)}.<br>
 * - Create an empty object and then load data in XML format using {@link ArgumentStorage#read(File, String)} or
 * {@link ArgumentStorage#read(InputStream, String)} (XML data schema is located in the project under the path
 * /com/github/onlycrab/argParser/arguments/xml/ArgumentsSchema.xsd).<br>
 * Dependencies are described in clauses 6, 7.</p>
 *
 * <p>2.If any argument needs to be assigned a default value, use {@link ArgumentStorage#setArgumentValuesToDefaultIfEmpty()}
 * (must be called after parsing). Assignment will be performed if the argument has a default value and
 * during parsing its value is not assigned or is empty (zero-length string).</p>
 *
 * <p>3.To check if a particular argument is defined, use {@link ArgumentStorage#isDefined(String)}. An argument is considered
 * definite if it was passed to this object when it was created via XML data or by {@link ArgumentStorage#add(Argument)}.
 * In other words, all arguments specified in the object are considered to be definite.</p>
 *
 * <p>4.In order to check whether a specific argument is declared, use the {@link ArgumentStorage#isDeclared(String)}
 * (must be called after parsing).</p>
 *
 * <p>5.In order to check the filling of a specific argument, use {@link ArgumentStorage#isFilled(String)} (must be
 * called after parsing). An argument is considered filling if its value is not {@code null} and is not
 * a zero-length string.
 * The method {@link ArgumentStorage#isRequireFilled()} checks that all required arguments are filled in at once.
 * To get a text message about the result of the check, use {@link ArgumentStorage#getMessage()}.</p>
 *
 * <p>6.Arguments can be dependent on each other. If {@code arg_1} depends on {@code arg_2},
 * then filling in {@code arg_1} requires filling in {@code arg_2}. That is, if only
 * {@code arg_2} is filled, the method {@link ArgumentStorage#isRequireFilled()} will return {@code false}.
 * The opposite is not true: if the argument {@code arg_2} is filled, but the {@code arg_1}
 * is not filled, then {@link ArgumentStorage#isRequireFilled()} will return {@code true}.
 * <p>
 * Use {@link ArgumentStorage#addDependence(String, String)} to add a dependency.
 * <p>
 * If you want the arguments to be dependent on each other ({@code arg_1} from {@code arg_2} and
 * {@code arg_2} from {@code arg_1}), then call {@link ArgumentStorage#addDependence(String, String)} twice
 * for these arguments: {@code addDependence(arg_1, arg_2)} and {@code addDependence(arg_2, arg_1)}.
 * <p>
 * The method {@link ArgumentStorage#isRequireFilled()} checks that all the required arguments are filled
 * in at once, taking into account the dependencies (must be called after parsing).
 * To get a text message about the result of the check, use {@link ArgumentStorage#getMessage()}.</p>
 *
 * <p>7.Arguments can conflict with each other. If {@code arg_1} conflicts with {@code arg_2},
 * then filling them both will be a conflict.
 * {@link ArgumentStorage#isConflict()} checks for conflicts (must be called after parsing).
 * To get a text message about the result of the check, use {@link ArgumentStorage#getMessage()}.</p>
 *
 * <p>8.It is possible to create custom rules for arguments. First, create a class that extends {@link Rule}
 * and override the {@link Rule#isSatisfied()}. Then add to the rule the arguments that should execute it
 * by the method {@link Rule#addTarget(Argument)}. Then add rules to storage object by {@link ArgumentStorage#addRule(Rule)}.
 * After parsing, call the {@link ArgumentStorage#isRulesSatisfied()}: the result will be {@code false} if any rule
 * returns {@code false} from {@link Rule#isSatisfied()}, otherwise the result will be {@code true}.
 * To get a text message about the result if it is {@code false}, use {@link ArgumentStorage#getMessage()}</p>
 *
 * <p>9.If necessary, you can enter text information about the project. This does not affect
 * arguments or parsing process in any way. The information is added by methods
 * {@link ArgumentStorage#setDescription(String)}, {@link ArgumentStorage#setUsage(String)},
 * {@link ArgumentStorage#setExample(String)} or specified in the XML data.</p>
 *
 * <p>10.The method {@link ArgumentStorage#getHelp()} returns help about the project: general information
 * about the project and a description of the arguments. The method {@link ArgumentStorage#getHelp(String)}
 * returns a detailed description of a particular argument.
 * Also you can get help if and only if help argument is declared or filled : use {@link ArgumentStorage#getSystemHelp(String)}.</p>
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ArgumentStorage {
    /**
     * Short argument name for help
     */
    public static final String HELP_SHORT_NAME = "h";

    /**
     * Long argument name for help
     */
    public static final String HELP_LONG_NAME = "help";

    /**
     * Arguments data object
     */
    private final ArgumentList args;

    /**
     * Arguments requirements(dependencies)
     */
    private final Requirements reqLinks;

    /**
     * Arguments conflicts
     */
    private final Conflicts confLinks;

    /**
     * Custom rules for arguments
     */
    private final List<Rule> rules;

    /**
     * Project description text (used only for printing help info)
     */
    private String description;

    /**
     * Project usage text (used only for printing help info)
     */
    private String usage;

    /**
     * Project examples text (used only for printing help info)
     */
    private String example;

    /**
     * A template that is used to display help string for a specific argument.
     *
     * @see ArgumentStorage#buildParametersHelpTemplate
     */
    private String printArgumentTemplate = "";

    /**
     * A counters that are used to parse the help print template.
     *
     * @see ArgumentStorage#buildParametersHelpTemplate
     */
    private int maxShortNameLength = 1;
    private int maxLongNameLength = 5;
    private int maxArgNameLength = 1;

    /**
     * Last info message. The message is overwritten each time the following methods are called:
     * {@link ArgumentStorage#isCyclicDependencyExists()}, {@link ArgumentStorage#isRequireFilled()},
     * {@link ArgumentStorage#isConflict()}, {@link ArgumentStorage#isRulesSatisfied()}, {@link ArgumentStorage#clear()}.
     */
    private String lastMessage;

    /**
     * Create new empty storage object.
     */
    public ArgumentStorage() {
        this(false);
    }

    /**
     * Create new storage object.
     *
     * @param defaultHelp whether to create a standard help argument
     */
    public ArgumentStorage(boolean defaultHelp) {
        args = new ArgumentList();
        reqLinks = new Requirements();
        confLinks = new Conflicts();
        rules = new ArrayList<>();

        if (defaultHelp) {
            createSystemicArguments();
        }
    }

    /**
     * Set project description text (used only for printing help info).
     *
     * @param value project description text
     */
    public void setDescription(String value) {
        description = value;
    }

    /**
     * Set project usage text (used only for printing help info)
     *
     * @param value project usage text
     */
    public void setUsage(String value) {
        usage = value;
    }

    /**
     * Set project examples text (used only for printing help info)
     *
     * @param value project examples text
     */
    public void setExample(String value) {
        example = value;
    }

    /**
     * Assign the default value to the argument value if the argument value is empty.
     *
     * <p>If {@code value} is {@code null} or {@code value.length} is 0, sets {@code value = defaultValue}.
     * If {@code valueDefault} is {@code null} - do nothing.
     * If an argument is not declared - do nothing.</p>
     */
    public void setArgumentValuesToDefaultIfEmpty() {
        for (Argument arg : args) {
            arg.setValueToDefaultIfEmpty();
        }
    }

    /**
     * Returns the number of arguments in this object.
     *
     * @return the number of arguments in this object
     */
    public int size() {
        return args.size();
    }

    /**
     * Returns project description text (used only for printing help info).
     *
     * @return project description text.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns project usage text (used only for printing help info).
     *
     * @return project usage text
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Returns project examples text (used only for printing help info).
     *
     * @return project examples text
     */
    public String getExample() {
        return example;
    }

    /**
     * Returns the last info message. The message is overwritten each time the following methods are called:
     * {@link ArgumentStorage#isCyclicDependencyExists()}, {@link ArgumentStorage#isRequireFilled()},
     * {@link ArgumentStorage#isConflict()}, {@link ArgumentStorage#isRulesSatisfied()}, {@link ArgumentStorage#clear()}.
     *
     * @return last info message
     * @see ArgumentStorage#isRequireFilled()
     * @see ArgumentStorage#isConflict()
     */
    public String getMessage() {
        if (lastMessage != null) {
            return lastMessage;
        } else {
            return "";
        }
    }

    /**
     * Returns argument by short or long name.
     *
     * @param key short or long argument name
     * @return argument by short or long name
     * @throws ArgumentNotFoundException if {@code key} is null;
     *                                   if no argument is found.
     */
    @NotNull
    public Argument getArgument(String key) throws ArgumentNotFoundException {
        if (key == null) {
            throw new ArgumentNotFoundException("name", "null");
        }
        Argument arg = args.get(key);
        if (arg != null) {
            return arg;
        } else {
            throw new ArgumentNotFoundException("name", key);
        }
    }

    /**
     * Returns argument value as string.
     *
     * @param key short or long argument name
     * @return argument value as string
     * @throws ArgumentNotFoundException if {@code key} is null;
     *                                   if no argument is found.
     */
    public String getValue(String key) throws ArgumentNotFoundException {
        return getArgument(key).getValue();
    }

    /**
     * Returns instance of {@code ValueConverter} for argument.
     *
     * @param key short or long argument name
     * @return instance of {@code ValueConverter} for argument.
     * @throws ArgumentNotFoundException if {@code key} is null;
     *                                   if no argument is found.
     * @see ValueConverter
     */
    public ValueConverter getValueConverter(String key) throws ArgumentNotFoundException {
        return getArgument(key).getValueConverter();
    }

    /**
     * Returns value converter object. {@code booleanTrueCases} and {@code booleanFalseCases}
     * will override the default cases. By default:<br>
     * true cases  = {"true"}<br>
     * false cases = {"false"}
     *
     * @param key               short or long argument name
     * @param booleanTrueCases  true cases
     * @param booleanFalseCases false cases
     * @return value converter object
     * @see ValueConverter
     */
    public ValueConverter getValueConverter(String key, String[] booleanTrueCases, String[] booleanFalseCases) {
        return getArgument(key).getValueConverter(booleanTrueCases, booleanFalseCases);
    }

    /**
     * Create systemic arguments.
     */
    private void createSystemicArguments() {
        try {
            add(new Argument(ArgumentStorage.HELP_SHORT_NAME, ArgumentStorage.HELP_LONG_NAME,
                    null,
                    false,
                    false,
                    "STRING",
                    "Print help info.",
                    null
            ));
        } catch (ArgumentException ignored) {
        }
    }

    /**
     * Add argument to list. If the list previously contained an argument with specified argument short or long
     * name, the new argument will be absorbed (all fields of the new argument will be assigned to the old one).
     *
     * @param arg argument ot add
     * @return a reference to this object
     */
    public ArgumentStorage add(Argument arg) {
        if (arg != null) {
            if (args.add(arg)) {
                maxShortNameLength = Math.max(maxShortNameLength, arg.getShortName().length());
                maxArgNameLength = Math.max(maxArgNameLength, arg.getParameters().length());
                if (arg.getLongName() != null) {
                    maxLongNameLength = Math.max(maxLongNameLength, arg.getLongName().length());
                }
            }
        }
        return this;
    }

    /**
     * Add dependency of one argument on another.
     *
     * @param nameDependent short or long dependent argument name
     * @param nameOn        short or long argument name on which depends
     * @throws ArgumentNotFoundException if no argument found by name {@code nameDependent} or {@code nameOn}
     * @see ArgumentStorage#isRequireFilled()
     */
    @SuppressWarnings("Duplicates")
    public void addDependence(String nameDependent, String nameOn) throws ArgumentNotFoundException {
        if (nameDependent == null || nameOn == null) {
            throw new ArgumentNotFoundException("name", null);
        } else if (!args.containsKey(nameDependent)) {
            throw new ArgumentNotFoundException("name", nameDependent);
        } else if (!args.containsKey(nameOn)) {
            throw new ArgumentNotFoundException("name", nameOn);
        }
        reqLinks.addRequirement(args.get(nameDependent), args.get(nameOn));
    }

    /**
     * Add a conflict between two arguments.
     *
     * @param keyFirst  short or long first argument name
     * @param keySecond short or long second argument name
     * @throws ArgumentNotFoundException if no argument found by name {@code keyFirst} or {@code keySecond}
     * @see ArgumentStorage#isConflict()
     */
    @SuppressWarnings("Duplicates")
    public void addConflict(String keyFirst, String keySecond) throws ArgumentNotFoundException {
        if (keyFirst == null || keySecond == null) {
            throw new ArgumentNotFoundException("name", null);
        } else if (!args.containsKey(keyFirst)) {
            throw new ArgumentNotFoundException("name", keyFirst);
        } else if (!args.containsKey(keySecond)) {
            throw new ArgumentNotFoundException("name", keySecond);
        }
        confLinks.addConflict(args.get(keyFirst), args.get(keySecond));
    }

    /**
     * Add a custom rule.
     *
     * @param rule custom rule
     */
    public void addRule(Rule rule) {
        if (rule != null) {
            rules.add(rule);
        }
    }

    /**
     * Read argument data from XML file. New data will be added to existing one.
     *
     * @param file     the target file to read from
     * @param encoding the character encoding of the file
     * @throws ArgumentException  if an error occurs while creating a new argument
     * @throws IOException        if an I/O error occurs
     * @throws XMLStreamException if an XML reading error occurs
     */
    public void read(File file, @Nullable String encoding) throws ArgumentException, IOException, XMLStreamException {
        XmlWrapper xmlWrapper = new XmlWrapper();
        xmlWrapper.read(file, encoding);

        addXMLData(xmlWrapper);
    }

    /**
     * Read argument data from stream. Data must be stored in XML format.
     * New data will be added to existing one.
     *
     * @param targetStream the InputStream to read from
     * @param encoding     the character encoding of the stream
     * @throws ArgumentException  if an error occurs while creating a new argument
     * @throws IOException        if an I/O error occurs
     * @throws XMLStreamException if an XML reading error occurs
     */
    public void read(InputStream targetStream, @Nullable String encoding) throws ArgumentException, IOException, XMLStreamException {
        XmlWrapper xmlWrapper = new XmlWrapper();
        xmlWrapper.read(targetStream, encoding);

        addXMLData(xmlWrapper);
    }

    /**
     * Write arguments data from XmlWrapper.
     *
     * @param xmlWrapper the XmlWrapper to get data from
     * @throws ArgumentException if an error occurs while creating a new argument
     */
    private void addXMLData(@NotNull XmlWrapper xmlWrapper) throws ArgumentException {
        //Set project info
        for (Map.Entry<String, String> entry : xmlWrapper.getInfo().entrySet()) {
            switch (entry.getKey()) {
                case "description":
                    setDescription(processNewLineCharsXML(entry.getValue()));
                    break;
                case "usage":
                    setUsage(processNewLineCharsXML(entry.getValue()));
                    break;
                case "example":
                    setExample(processNewLineCharsXML(entry.getValue()));
                    break;
            }
        }

        //Add arguments
        for (Map<String, String> argumentData : xmlWrapper.getArguments()) {
            add(new Argument(
                    argumentData.get("shortName"),
                    argumentData.get("longName"),
                    argumentData.get("valueDefault"),
                    Boolean.parseBoolean(argumentData.get("isRequiredDeclared")),
                    Boolean.parseBoolean(argumentData.get("isRequiredFilled")),
                    argumentData.get("parameters"),
                    processNewLineCharsXML(argumentData.get("description")),
                    processNewLineCharsXML(argumentData.get("descriptionDetailed"))
            ));
        }

        //Add dependencies
        for (String[] pair : xmlWrapper.getDependencies()) {
            if (pair.length == 2) {
                addDependence(pair[0], pair[1]);
            }
        }

        //Add conflicts
        for (String[] pair : xmlWrapper.getConflicts()) {
            if (pair.length == 2) {
                addConflict(pair[0], pair[1]);
            }
        }
    }

    /**
     * Process new line characters.
     *
     * @param xmlString target XML string
     * @return string after processing
     */
    private String processNewLineCharsXML(String xmlString){
        if (xmlString == null){
            return null;
        }
        return xmlString.replace("\n", "")
                .replace("<br>", "\n")
                .replace("<br/>", "\n");
    }

    /**
     * Returns is argument defined.
     *
     * @param key short or long argument name
     * @return is argument defined
     */
    public boolean isDefined(String key) {
        return args.get(key) != null;
    }

    /**
     * Returns is argument was been declared.
     *
     * @param key short or long argument name
     * @return is argument was been declared
     */
    public boolean isDeclared(String key) {
        if (key == null) return false;
        if (args.containsKey(key)) {
            return args.get(key).isDeclared();
        } else {
            return false;
        }
    }

    /**
     * Is argument value filled.
     *
     * @param key short or long argument name
     * @return {@code true} if argument value is not {@code null} and argument value length is not 0
     */
    public boolean isFilled(String key) {
        if (isDeclared(key)) {
            Argument argument = args.get(key);
            if (argument == null) {
                return false;
            }
            return argument.isFilled();
        } else {
            return false;
        }
    }

    /**
     * Check for cyclic dependencies in arguments.
     *
     * @return {@code true} if there is at least one cyclic dependency exists, otherwise returns {@code false}
     */
    public boolean isCyclicDependencyExists() {
        ReturnedBoolean result;
        for (Argument arg : args) {
            result = reqLinks.isCyclicDependencyExists(arg, null);
            if (result.getResult()) {
                lastMessage = result.getMessage();
                return true;
            }
        }
        lastMessage = "";
        return false;
    }

    /**
     * Returns is require arguments values filled. If require arguments are not filed, saves an explanatory message
     * that can be obtained by the method {@link ArgumentStorage#getMessage()}.
     *
     * <p>This method checks each required argument for value filled. If argument is not required, but is declared and has
     * a dependent arguments, checks each dependent argument for value filled.
     * To add the dependency of one argument on another argument, use the {@link ArgumentStorage#addDependence(String, String)}.</p>
     *
     * @return {@code true} if is require arguments values filled, otherwise returns {@code false}
     */
    public boolean isRequireFilled() {
        if (isCyclicDependencyExists()) {
            lastMessage = "Impossible to define is require arguments filled : " + lastMessage;
            return false;
        }
        ReturnedBoolean result;
        StringBuilder sb = new StringBuilder();
        boolean ans = true;
        for (Argument arg : args) {
            result = reqLinks.isRequireFilled(arg, null);
            if (!result.getResult()) {
                sb.append(result.getMessage()).append(" ");
                ans = false;
            }
        }
        if (!ans) {
            lastMessage = sb.toString();
        } else {
            lastMessage = "";
        }
        return ans;
    }

    /**
     * Returns if there is at least one conflict in declared arguments. If there is some conflicts, saves an explanatory
     * message that can be obtained by the method {@link ArgumentStorage#getMessage()}.
     *
     * <p>This method checks each declared argument for a conflict with all other declared arguments in the conflict list.
     * To add a conflicting argument to an another argument, use the {@link ArgumentStorage#addConflict(String, String)}.</p>
     *
     * @return {@code true} if there is at least one conflict in declared arguments, otherwise returns {@code false}
     */
    public boolean isConflict() {
        ReturnedBoolean result = new ReturnedBoolean(false);
        for (Argument arg : args) {
            result = confLinks.isInConflict(arg, result);
        }
        if (result.getResult()) {
            lastMessage = result.getMessage();
            return true;
        } else {
            lastMessage = "";
            return false;
        }
    }

    /**
     * Returns if there all custom argument rules are passed.
     *
     * @return {@code true} if all custom argument rules are passed, otherwise returns {@code false}.
     */
    public boolean isRulesSatisfied() {
        StringBuilder sb = new StringBuilder();
        boolean result = true;
        for (Rule rule : rules) {
            if (!rule.isSatisfied()) {
                result = false;
                sb.append(rule.getMessage());
            }
        }
        lastMessage = sb.toString();
        return result;
    }

    /**
     * Set all arguments values to {@code null} and not declared.
     */
    public void clearValues() {
        for (Argument arg : args) {
            arg.setDeclared(false).setValue(null);
        }
    }

    /**
     * Removes all requirements.
     */
    public void clearDependencies() {
        reqLinks.clear();
    }

    /**
     * Removes all conflicts.
     */
    public void clearConflicts() {
        confLinks.clear();
    }

    /**
     * Removes all rules.
     */
    public void clearRules() {
        rules.clear();
    }

    /**
     * Removes all added data.
     */
    public void clear() {
        args.clear();
        clearDependencies();
        clearConflicts();
        clearRules();

        description = null;
        usage = null;
        example = null;

        maxShortNameLength = 1;
        maxLongNameLength = 5;
        maxArgNameLength = 1;

        printArgumentTemplate = "";

        lastMessage = null;
    }

    /**
     * Build parameter print template {@link ArgumentStorage#printArgumentTemplate}.
     */
    private void buildParametersHelpTemplate() {
        printArgumentTemplate = "  " + "%1s" + "%-" + (maxShortNameLength + 1) + "s " + "%2s" + "%-" + maxLongNameLength + "s %-" + maxArgNameLength + "s %s";
    }

    /**
     * Returns system made help info text depending on the state of the custom help argument.
     *
     * <p>If argument with short name {@code helpArgName} is defined and declared or filled - returns system made
     * help info text. Otherwise returns {@code null}.</p>
     * <p>If {@code helpArgName} is {@code null} - returned value depending on the state of the system help argument
     * {@link ArgumentStorage#HELP_SHORT_NAME}.</p>
     *
     * @param helpArgName help argument short name
     * @return help info text if argument {@code helpShortName} is declared, otherwise return {@code null}
     */
    public String getSystemHelp(String helpArgName) {
        String helpArg;
        if (helpArgName != null){
            helpArg = helpArgName;
        } else {
            helpArg = HELP_SHORT_NAME;
        }
        if (isDefined(helpArg)) {
            if (isFilled(helpArg)) {
                return getHelp(getArgument(helpArg).getValue());
            } else if (isDeclared(helpArg)) {
                return getHelp();
            }
        }
        return null;
    }

    /**
     * Returns help info string.
     *
     * @return help info string
     */
    public String getHelp() {
        StringBuilder builder = new StringBuilder();
        if (description != null) {
            builder.append(description).append("\n\n");
        }
        if (usage != null) {
            builder.append("Usage : \n").append(usage).append("\n\n");
        }
        if (example != null) {
            builder.append("Examples : \n").append(example).append("\n\n");
        }
        if (args.size() > 0) {
            buildParametersHelpTemplate();
            builder.append("Parameters : ").append('\n');

            for (Argument arg : args) {
                if (arg.getLongName() != null) {
                    builder.append(String.format(printArgumentTemplate, "-", arg.getShortName() +
                                    ",", "--", arg.getLongName(), arg.getParameters(),
                            (arg.isRequiredBeDeclared() ? "[Required] " : "") + arg.getDescription())).append("\n");
                } else {
                    builder.append(String.format(printArgumentTemplate, "-", arg.getShortName() +
                                    ",", "  ", "", arg.getParameters(),
                            (arg.isRequiredBeDeclared() ? "[Required] " : "") + arg.getDescription())).append("\n");
                }
            }
        }
        return builder.toString();
    }

    /**
     * Returns detailed description about argument.
     *
     * @param key argument short or long name
     * @return detailed description about argument
     */
    public String getHelp(String key) {
        Argument argument;
        try {
            argument = getArgument(key);
        } catch (ArgumentNotFoundException e) {
            return String.format("Argument with name <%s> not found.", key);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Short name : ").append(argument.getShortName()).append('\n')
                .append("Full name : ").append((argument.getLongName() != null ? argument.getLongName() : "")).append('\n')
                .append("Required : ").append((argument.isRequiredBeDeclared() ? "Yes" : "No")).append('\n');
        if (argument.getParameters().length() > 0) {
            builder.append("Parameter : ").append(argument.getParameters()).append('\n');
        }
        builder.append("Description : ");
        if (!argument.getDescriptionDetailed().equals("")) {
            builder.append(argument.getDescriptionDetailed());
        } else if (!argument.getDescription().equals("")) {
            builder.append(argument.getDescription());
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (!(obj instanceof ArgumentStorage)) {
            return false;
        }
        ArgumentStorage another = (ArgumentStorage) obj;
        if (!String.valueOf(getDescription()).equals(String.valueOf(another.getDescription()))) {
            return false;
        }
        if (!String.valueOf(getUsage()).equals(String.valueOf(another.getUsage()))) {
            return false;
        }
        if (!String.valueOf(getExample()).equals(String.valueOf(another.getExample()))) {
            return false;
        }
        if (!args.equals(another.args)) {
            return false;
        }
        if (!confLinks.equals(another.confLinks)) {
            return false;
        }
        return reqLinks.equals(another.reqLinks);
    }
}
