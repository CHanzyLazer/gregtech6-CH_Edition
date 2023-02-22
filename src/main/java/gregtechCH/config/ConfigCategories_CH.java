package gregtechCH.config;

/**
 * @author CHanzy
 */
public enum ConfigCategories_CH {
    general,
    loader,
    colour,
    optimize,
    multithread;
    
    public enum Generate {
        deposit;
    }
    public enum Machines {
        basic,
        boiler,
        generatorMotor,
        rotation;
    }

    public enum Reactors {
        adjustemission;
    }
}
