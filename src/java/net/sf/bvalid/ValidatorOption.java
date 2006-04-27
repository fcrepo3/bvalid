package net.sf.bvalid;

/**
 * An option that can be used to configure a Validator.
 *
 * Options are passed to the <code>ValidatorFactory</code> when
 * obtaining a validator.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ValidatorOption {

    /**
     * Whether validation should fail when a schema referenced within the
     * instance document cannot be found.
     *
     * Valid values are "true" and "false".  Default value is "true".
     */
    public static final ValidatorOption FAIL_ON_MISSING_REFERENCED
            = new ValidatorOption(
                    "fail-on-missing-referenced",
                    "Whether validation should fail when a schema referenced "
                            + "within the instance document cannot be found.",
                    new String[] { "true", "false" },
                    "true");
                                  

    /**
     * Whether the validator should cache parsed grammars in memory after
     * they have been successfully used to validate an instance document.
     *
     * Valid values are "true" and "false".  Default value is "false".
     */
    public static final ValidatorOption CACHE_PARSED_GRAMMARS
            = new ValidatorOption(
                    "cache-parsed-grammars",
                    "Whether the validator should cache parsed grammars in "
                            + "memory after they have been successfully used "
                            + "to validate an instance document.",
                    new String[] { "true", "false" },
                    "false");

    private String _name;
    private String _description;
    private String[] _validValues;
    private String _defaultValue;

    protected ValidatorOption(String name,
                              String description,
                              String[] validValues,
                              String defaultValue) {
        _name = name;
        _description = description;
        _validValues = validValues;
        _defaultValue = defaultValue;
    }

    /**
     * Get the name of the option.
     *
     * This should be unique among options.
     * By convention, it contains all lowercase characters
     * and uses dashes (-) as word separators.
     */
    public String getName() {
        return _name;
    }

    /**
     * Get the description of the option.
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Get the set of valid values for this option.
     *
     * If valid values are not constrained to a list,
     * return <code>null</code>.
     */
    public String[] getValidValues() {
        return _validValues;
    }

    /**
     * Get the default value for this option.
     *
     * If there is no default value, return <code>null</code>.
     */
    public String getDefaultValue() {
        return _defaultValue;
    }

    /**
     * Tell whether the given value is valid for this option.
     */
    public boolean isValidValue(String value) {

        if (_validValues == null) {
            return true;
        } else {
            for (int i = 0; i < _validValues.length; i++) {
                if (value.equals(_validValues[i])) {
                    return true;
                }
            }
            return false;
        }
    }

}
