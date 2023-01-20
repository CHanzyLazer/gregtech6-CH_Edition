package gregtechCH.tileentity;

import gregapi.tileentity.ITileEntityErrorable;
import gregapi.tileentity.ITileEntityUnloadable;

/**
 * @author CHanzy
 */
public interface IMTEServerTickParallel extends ITileEntityUnloadable, ITileEntityErrorable {
    void onServerTickPar(boolean aFirst);
    void onUnregisterPar();
}
