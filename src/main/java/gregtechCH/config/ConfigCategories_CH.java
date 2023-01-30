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
