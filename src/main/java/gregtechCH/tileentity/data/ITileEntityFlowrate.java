package gregtechCH.tileentity.data;

import gregapi.tileentity.ITileEntityUnloadable;


public interface ITileEntityFlowrate extends ITileEntityUnloadable {
	/** The Fluid Flow Rate this Object has right now. Measured in Liter per Tick. */
	public long getFlowrateValue(byte aSide);
	/** The Fluid Flow Rate this Object can have at most. Measured in Liter per Tick. */
	public long getFlowrateMax(byte aSide);
}
