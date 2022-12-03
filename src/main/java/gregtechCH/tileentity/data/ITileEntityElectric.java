package gregtechCH.tileentity.data;

import gregapi.tileentity.ITileEntityUnloadable;

public interface ITileEntityElectric  extends ITileEntityUnloadable {
    /** The Wattage this Object has right now. Measured in Liter per Tick. */
    public long getWattageValue(byte aSide);
    /** The Wattage this Object can have at most. Measured in Liter per Tick. */
    public long getWattageMax(byte aSide);
    /** The Voltage this Object has right now. Measured in Liter per Tick. */
    public long getVoltageValue(byte aSide);
    /** The Voltage this Object can have at most. Measured in Liter per Tick. */
    public long getVoltageMax(byte aSide);
    /** The Amperage this Object has right now. Measured in Liter per Tick. */
    public long getAmperageValue(byte aSide);
    /** The Amperage this Object can have at most. Measured in Liter per Tick. */
    public long getAmperageMax(byte aSide);
}
