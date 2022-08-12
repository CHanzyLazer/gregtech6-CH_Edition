package gregtechCH.tileentity;

public interface ITEPaintable_CH {
    int getBottomRGB();
    int getOriginalRGB();

    boolean isPainted();
    void setIsPainted(boolean aIsPainted);
    int getPaint();

    boolean unpaint();
    boolean paint(int aRGB);
}
