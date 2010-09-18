package org.jgrasstools.hortonmachine.externals.epanet.core;

@SuppressWarnings("nls")
public enum OptionParameterCodes {
    UNITS(-1, "UNITS", "Units of flow rates", "LPS"), //
    HEADLOSS(-1, "HEADLOSS", "Headloss formula for flow through pipe", "H-W"), //
    HYDRAULICS(-1, "HYDRAULICS", "Save the current solution or use a previously saved solution", ""), //
    QUALITY(-1, "QUALITY", "Type of water quality analysis", "NONE"), //
    VISCOSITY(-1, "VISCOSITY", "Cinematic viscosity at 20 celcius degrees", "1.0"), //
    DIFFUSIVITY(-1, "DIFFUSIVITY", "Molecular diffusivity in water", "1.0"), //
    SPECIFICGRAVITY(-1, "SPECIFIC GRAVITY", "Ratio of density of the fluid to that of water at 4 degree", "1.0"), //
    TRIALS(0, "TRIALS", "Maximum number of trials to solve hydraulics", "40"), //
    ACCURACY(1, "ACCURACY", "Accuracy convergence for the solution", "0.001"), //
    UNBALANCED(-1, "UNBALANCED", "Defines what happens if the hydraulics solution cannot be reached", "STOP"), //
    PATTERN(-1, "PATTERN", "ID of the default demand pattern for junctions where no demand pattern was specified", "1"), //
    TOLERANCE(2, "TOLERANCE", "Tolerance for water quality.", "0.01"), //
    EMITEXPON(3, "EMITTER EXPONENT", "The power to which the pressure at a junction is raised.", "0.5"), //
    DEMANDMULTIPLIER(4, "DEMAND MULTIPLIER", "Multiplier for the demand values.", "1");

    private int code;
    private String key;
    private String description;
    private String defaultValue;
    OptionParameterCodes( int code, String key, String description, String defaultValue ) {
        this.code = code;
        this.key = key;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public int getCode() {
        return code;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public String getDefault() {
        return this.defaultValue;
    }

    public static OptionParameterCodes forCode( int i ) {
        OptionParameterCodes[] values = values();
        for( OptionParameterCodes type : values ) {
            if (type.code == i) {
                return type;
            }
        }
        throw new IllegalArgumentException("No type for the given code: " + i);
    }

    public static OptionParameterCodes forKey( String key ) {
        OptionParameterCodes[] values = values();
        for( OptionParameterCodes type : values ) {
            if (type.key.equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No type for the given key: " + key);
    }
}
