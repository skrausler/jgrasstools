package org.jgrasstools.hortonmachine.externals.epanet.core;

@SuppressWarnings("nls")
public enum TimeParameterCodes {
    DURATION(0, "DURATION", "Simulation duration"), //
    HYDSTEP(1, "HYDRAULIC TIMESTEP", "Hydraulic time step"), //
    QUALSTEP(2, "QUALITY TIMESTEP", "Water quality time step"), //
    PATTERNSTEP(3, "PATTERN TIMESTEP", "Time pattern time step"), //
    PATTERNSTART(4, "PATTERN START", "Time pattern start time"), //
    REPORTSTEP(5, "REPORT TIMESTEP", "Reporting time step"), //
    REPORTSTART(6, "REPORT START", "Report starting time"), //
    RULESTEP(7, "RULE TIMESTEP", "Time step for evaluating rule-based controls"), //
    STATISTIC(8, "STATISTIC", "Type of time series post-processing to use"), //
    STARTCLOCKTIME(-1, "START CLOCKTIME", "The time of the day at which the simulation begins"), //
    PERIODS(9, "", "Number of reporting periods saved to binary output file");

    private int code;
    private String key;
    private String description;
    TimeParameterCodes( int code, String key, String description ) {
        this.code = code;
        this.key = key;
        this.description = description;
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

    public static TimeParameterCodes forCode( int i ) {
        TimeParameterCodes[] values = values();
        for( TimeParameterCodes type : values ) {
            if (type.code == i) {
                return type;
            }
        }
        throw new IllegalArgumentException("No type for the given code: " + i);
    }

    public static TimeParameterCodes forKey( String key ) {
        TimeParameterCodes[] values = values();
        for( TimeParameterCodes type : values ) {
            if (type.key.equals(key)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No type for the given key: " + key);
    }
}