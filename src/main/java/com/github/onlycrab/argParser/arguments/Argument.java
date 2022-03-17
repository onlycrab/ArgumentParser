package com.github.onlycrab.argParser.arguments;

import com.github.onlycrab.argParser.arguments.converter.ValueConverter;
import com.github.onlycrab.argParser.arguments.dependencies.Rule;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object that stores information about the command line argument.
 *
 * <p>The main fields of an argument are a key pair (short and long name)
 * and a value. Any of the names can be used to uniquely identify the argument.</p>
 *
 * <p>The value is stored in string format. You cannot explicitly set a value,
 * it is assigned only as a result of parsing. It is possible to specify a default
 * value for the argument. If the argument value is empty, you can assign it the
 * default value by calling {@link Argument#setValueToDefaultIfEmpty()}.</p>
 *
 * <p>Each argument has descriptive fields {@code description} and {@code descriptionDetailed}.
 * These fields are used only when displaying help information.</p>
 *
 * <p>The argument can be in 2 states: <b>declared</b> and <b>not declared</b>
 * (by default). The argument becomes declared when its name is passed to the
 * parser {@link ArgumentParser#parse(ArgumentStorage, String[])} (it doesn't matter with or without value).</p>
 *
 * <p>The following restrictions can be imposed on an argument:<br>
 * 1.Dependence on another argument;<br>
 * If {@code argument1} depends on {@code argument2}, then checking whether {@code argument1}
 * is filled or declared will trigger a similar check on {@code argument2}.
 * 2.Conflict with another argument;<br>
 * If 2 arguments conflict with each other, then when you declare both of them, the method
 * {@link com.github.onlycrab.argParser.arguments.dependencies.Conflicts#isInConflict(Argument)}
 * will return {@code true}.
 * 3.Condition of the mandatory declaration;<br>
 * This condition means that the argument must be declared (the value may not be assigned).
 * If the argument is not declared, then it will not affect the result of
 * {@link com.github.onlycrab.argParser.arguments.dependencies.Conflicts#isInConflict(Argument)}.
 * The check is performed by calling {@link com.github.onlycrab.argParser.arguments.dependencies.Requirements#isRequireFilled(Argument)}}.
 * 4.Condition of the mandatory filling;<br>
 * This condition means that the argument must be filled. The check is performed by calling
 * {@link com.github.onlycrab.argParser.arguments.dependencies.Requirements#isRequireFilled(Argument)}}.
 * 5.Custom rule;<br>
 * It is possible to impose on the argument the requirement to execute a custom rule {@link Rule}.
 * To check if the values of the arguments match their rules after parsing, you need to use {@link ArgumentStorage#isRulesSatisfied()}.</p>
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Argument {
    /**
     * Short argument name
     */
    private String shortName;

    /**
     * Long argument name
     */
    private String longName;

    /**
     * Default argument value. This value is assigned to the argument if the argument value
     * is {@code null} after parsing.
     */
    private String valueDefault;

    /**
     * Argument required be filled flag
     */
    private boolean isRequiredBeFilled;

    /**
     * Argument required be declared flag
     */
    private boolean isRequiredBeDeclared;

    /**
     * Argument text parameters info (used only for printing help info)
     */
    private String parameters;

    /**
     * Argument text description (used only for printing help info)
     */
    private String description;

    /**
     * Argument detailed description (used only for printing help info)
     */
    private String descriptionDetailed;

    /**
     * Argument value
     */
    private String value;

    /**
     * Argument declaration indicator.
     * {@code true} if the name of this argument was passed during parsing, else - {@code false}.
     */
    private boolean isDeclared;

    /**
     * Create new argument.
     *
     * @param shortName argument short name
     * @param longName  argument long name
     * @throws ArgumentException if shortName is {@code null} or empty;
     *                           if longName is not null, but empty;
     *                           if shortName or longName is reserved
     */
    public Argument(String shortName, @Nullable String longName) throws ArgumentException {
        this(shortName, longName, null, false, false, null, null, null);
    }

    /**
     * Create new argument.
     *
     * @param shortName            argument short name
     * @param longName             argument long name
     * @param valueDefault         argument default value
     * @param isRequiredBeDeclared is argument required be declared
     * @param isRequiredBeFilled   is argument required be filled
     * @param parameters           argument parameters info (used only for printing help info)
     * @param description          argument description (used only for printing help info)
     * @param descriptionDetailed  argument detailed description (used only for printing help info)
     * @throws ArgumentException if shortName is {@code null} or empty;
     *                           if longName is not null, but empty;
     *                           if shortName or longName is reserved
     */
    public Argument(String shortName, @Nullable String longName, @Nullable String valueDefault, boolean isRequiredBeDeclared, boolean isRequiredBeFilled, @Nullable String parameters, @Nullable String description, @Nullable String descriptionDetailed) throws ArgumentException {
        if (shortName == null) throw new ArgumentException("Parameter <shortName> is null.");
        if (shortName.trim().length() == 0) throw new ArgumentException("Parameter <shortName> is empty.");
        if (longName != null) {
            if (longName.trim().length() == 0) throw new ArgumentException("Parameter <longName> is empty.");
        }

        this.shortName = shortName;
        this.longName = longName;
        this.valueDefault = valueDefault;
        this.isRequiredBeFilled = isRequiredBeFilled;
        this.isRequiredBeDeclared = isRequiredBeDeclared;
        this.parameters = parameters;
        if (description != null) {
            this.description = description;
        } else {
            this.description = "";
        }
        if (descriptionDetailed != null) {
            this.descriptionDetailed = descriptionDetailed;
        } else {
            this.descriptionDetailed = this.description;
        }
        isDeclared = false;
        value = null;
    }

    /**
     * Returns argument short name.
     *
     * @return argument short name
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Returns argument long name.
     *
     * @return argument long name
     */
    @Nullable
    public String getLongName() {
        return longName;
    }

    /**
     * Returns argument name.
     *
     * @return argument name
     */
    public String getName() {
        if (longName != null) {
            return String.format("<%s(%s)>", shortName, longName);
        } else {
            return String.format("<%s>", shortName);
        }
    }

    /**
     * Returns argument parameters info (used only for printing help info)
     *
     * @return argument parameters info
     */
    @NotNull
    public String getParameters() {
        if (parameters != null) {
            return parameters;
        } else {
            return "";
        }
    }

    /**
     * Set parameters info.
     *
     * @param value parameters info string
     * @return a reference to this object
     */
    public Argument setParameters(@Nullable String value) {
        parameters = value;
        return this;
    }

    /**
     * Returns argument description (used only for printing help info)
     *
     * @return argument description
     */
    @NotNull
    public String getDescription() {
        if (description != null) {
            return description;
        } else {
            return "";
        }
    }

    /**
     * Set description text.
     *
     * @param value description text
     * @return a reference to this object
     */
    public Argument setDescription(@Nullable String value) {
        description = value;
        return this;
    }

    /**
     * Returns argument detailed description (used only for printing help info).
     *
     * @return argument detailed description
     */
    @NotNull
    public String getDescriptionDetailed() {
        if (descriptionDetailed != null) {
            return descriptionDetailed;
        } else {
            return getDescription();
        }
    }

    /**
     * Set detailed description text.
     *
     * @param value detailed description text
     * @return a reference to this object
     */
    public Argument setDescriptionDetailed(@Nullable String value) {
        if (value != null) {
            descriptionDetailed = value;
        } else {
            descriptionDetailed = "";
        }
        return this;
    }

    /**
     * Returns argument value. If value is {@code null} - returns empty string.
     *
     * @return argument value
     */
    @NotNull
    public String getValue() {
        if (value != null) {
            return value;
        } else {
            return "";
        }
    }

    /**
     * Set argument value. If {@code value} is not {@code null}, also sets {@code declared} to {@code true}.
     *
     * @param value argument value
     */
    protected void setValue(String value) {
        this.value = value;
        if (value != null) {
            setDeclared(true);
        }
    }

    /**
     * Returns argument default value. Can be {@code null}.
     *
     * @return argument default value
     */
    @Nullable
    public String getValueDefault() {
        return valueDefault;
    }

    /**
     * Set default value. A default values uses in {@link ArgumentStorage#setArgumentValuesToDefaultIfEmpty()}.
     *
     * @param valueDefault value to be default
     * @return a reference to this object
     */
    public Argument setValueDefault(@Nullable String valueDefault) {
        this.valueDefault = valueDefault;
        return this;
    }

    /**
     * Returns value converter object.
     *
     * @return value converter object
     * @see ValueConverter
     */
    @NotNull
    public ValueConverter getValueConverter() {
        return new ValueConverter(value);
    }

    /**
     * Returns value converter object. {@code booleanTrueCases} and {@code booleanFalseCases}
     * will override the default cases. By default:<br>
     * true cases  = {"true"}<br>
     * false cases = {"false"}
     *
     * @param booleanTrueCases  true cases
     * @param booleanFalseCases false cases
     * @return value converter object
     * @see ValueConverter
     */
    @NotNull
    public ValueConverter getValueConverter(String[] booleanTrueCases, String[] booleanFalseCases) {
        return new ValueConverter(value, booleanTrueCases, booleanFalseCases);
    }

    /**
     * Is argument required to be filled.
     *
     * @return if argument is required to be filled - {@code true}, else - {@code false}
     */
    public boolean isRequiredBeFilled() {
        return isRequiredBeFilled;
    }

    /**
     * Set is argument required to be filled.
     *
     * @param value is argument required to be filled
     * @return a reference to this object
     */
    public Argument setRequiredBeFilled(boolean value) {
        isRequiredBeFilled = value;
        return this;
    }

    /**
     * Is argument required to be declared.
     *
     * @return if argument is required to be declared - {@code true}, else - {@code false}
     */
    public boolean isRequiredBeDeclared() {
        return isRequiredBeDeclared;
    }

    /**
     * Set is argument required to be declared.
     *
     * @param value is argument required to be declared
     * @return a reference to this object
     */
    public Argument setRequiredBeDeclared(boolean value) {
        isRequiredBeDeclared = value;
        return this;
    }

    /**
     * Is argument declared.
     *
     * @return if argument is declared - {@code true}, else - {@code false}
     */
    public boolean isDeclared() {
        return isDeclared;
    }

    /**
     * Declare argument.
     *
     * @param value if {@code true} - argument will be declared; if {@code false} -
     *              argument will be not declared
     * @return a reference to this object
     */
    protected Argument setDeclared(boolean value) {
        isDeclared = value;
        return this;
    }

    /**
     * If this {@code value} is {@code null} or {@code value.length} is 0, sets
     * {@code value = defaultValue}.
     * If {@code valueDefault} is {@code null} - do nothing.
     * If this argument is not declared - do nothing.
     */
    void setValueToDefaultIfEmpty() {
        if (valueDefault != null && isDeclared) {
            if (value == null) {
                value = valueDefault;
            } else if (value.length() == 0) {
                value = valueDefault;
            }
        }
    }

    /**
     * Is argument filled.
     *
     * @return {@code true} if {@code value} is not {@code null} and {@code value.length} is not 0
     */
    public boolean isFilled() {
        if (value != null) {
            return value.length() > 0;
        } else {
            return false;
        }
    }

    /**
     * Returns {@code true} if this argument's key equals the specified key.
     *
     * @param obj object with key whose equality is to be tested
     * @return {@code true} if this argument's key equals the specified key, otherwise returns {@code false}
     */
    public boolean isKeyEquals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof Argument) {
            Argument another = (Argument) obj;
            if (shortName.equals(another.shortName) || shortName.equals(another.longName)) {
                return true;
            } else if (longName != null || another.longName != null) {
                if (longName != null) {
                    if (longName.equals(another.shortName) || longName.equals(another.longName)) {
                        return true;
                    }
                }
                if (another.longName != null) {
                    return another.longName.equals(shortName);
                }
            }
            return false;
        } else if (obj instanceof String) {
            String s = (String) obj;
            if (longName != null) {
                return shortName.equals(s) || longName.equals(s);
            } else {
                return shortName.equals(s);
            }
        } else {
            return false;
        }
    }

    /**
     * Absorb the argument {@code another}. All {@code another} fields (with the exception of those assigned as
     * a result of parsing: {@link Argument#value} and {@link Argument#isDeclared} will be assigned to the this argument.
     *
     * @param another argument from which the data will be absorbed
     * @return {@code true} if the operation was successful, otherwise returns {@code false}
     */
    public boolean absorb(Argument another) {
        if (another == this) {
            return false;
        } else if (!isKeyEquals(another)) {
            return false;
        }
        shortName = another.shortName;
        longName = another.longName;
        valueDefault = another.valueDefault;
        isRequiredBeDeclared = another.isRequiredBeDeclared;
        isRequiredBeFilled = another.isRequiredBeFilled;
        description = another.description;
        descriptionDetailed = another.descriptionDetailed;
        parameters = another.parameters;
        return true;
    }

    @Override
    public int hashCode() {
        return (shortName + (longName != null ? longName : "")).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (!(obj instanceof Argument)) {
            return false;
        }

        Argument another = (Argument) obj;
        if (!shortName.equals(another.shortName)) {
            return false;
        }
        if (longName != null || another.longName != null) {
            if (longName != null && another.longName != null) {
                if (!longName.equals(another.longName)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        if ((valueDefault == null || another.valueDefault == null) && (valueDefault != null || another.valueDefault != null)) {
            return false;
        } else if (valueDefault != null) {
            if (!valueDefault.equals(another.valueDefault)) {
                return false;
            }
        }
        if (isRequiredBeFilled != another.isRequiredBeFilled) {
            return false;
        }
        if (isRequiredBeDeclared != another.isRequiredBeDeclared) {
            return false;
        }
        if (!getParameters().equals(another.getParameters())) {
            return false;
        }
        if (!getDescription().equals(another.getDescription())) {
            return false;
        }
        if (!getDescriptionDetailed().equals(another.getDescriptionDetailed())) {
            return false;
        }
        if (!getValue().equals(another.getValue())) {
            return false;
        }
        return isDeclared == another.isDeclared;
    }
}
