package gregtechCH.tileentity;

/**
 * @author CHanzy
 */
public interface IMTEPaintable {
    int getRGBa();
    int getBottomRGB();
    int getOriginalRGB();

    boolean isPainted();
    void setIsPainted(boolean aIsPainted);
    int getPaint();

    boolean unpaint();
    boolean paint(int aRGB);
}
