package bikerboys.protoproj.translation;

public class TranslationCube {

    private final String id;

    private float x0;
    private float y0;
    private float z0;

    private float w;
    private float h;
    private float d;

    private int xTexOffs;
    private int yTexOffs;

    public TranslationCube(
            String id,
            float x0, float y0, float z0,
            float w, float h, float d,
            int xTexOffs, int yTexOffs
    ) {
        this.id = id;
        this.x0 = x0;
        this.y0 = y0;
        this.z0 = z0;
        this.w = w;
        this.h = h;
        this.d = d;
        this.xTexOffs = xTexOffs;
        this.yTexOffs = yTexOffs;
    }

    public String getId() { return id; }

    public float getX0() { return x0; }
    public float getY0() { return y0; }
    public float getZ0() { return z0; }

    public float getW() { return w; }
    public float getH() { return h; }
    public float getD() { return d; }

    public int getXTexOffs() { return xTexOffs; }
    public int getYTexOffs() { return yTexOffs; }

    public void setX0(float x0) { this.x0 = x0; }
    public void setY0(float y0) { this.y0 = y0; }
    public void setZ0(float z0) { this.z0 = z0; }

    public void setW(int w) { this.w = w; }
    public void setH(int h) { this.h = h; }
    public void setD(int d) { this.d = d; }

    public void setTex(int x, int y) {
        this.xTexOffs = x;
        this.yTexOffs = y;
    }
}