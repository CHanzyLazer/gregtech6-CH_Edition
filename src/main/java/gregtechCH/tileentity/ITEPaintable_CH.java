package gregtechCH.tileentity;

public interface ITEPaintable_CH {
    int getBottomRGB();
    int getOriginalRGB();

    boolean isPainted();
    int getPaint();

    boolean unpaint();
    boolean paint(int aRGB);
}
