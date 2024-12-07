package core.rendering;

import glm_.vec2.Vec2;

public class CharInfo {
    public int sourceX;
    public int sourceY;
    public int width;
    public int height;

    public Vec2[] textureCoordinates = new Vec2[4];

    public CharInfo(int sourceX, int sourceY, int width, int height) {
        this.sourceX = sourceX;
        this.sourceY = sourceY;
        this.width = width;
        this.height = height;
    }

    public void calculateTextureCoordinates(int fontWidth, int fontHeight) {
        float x0 = (float)sourceX / (float)fontWidth;
        float x1 = (float)(sourceX + width) / (float)fontWidth;
        float y0 = (float)(sourceY - height ) / (float)fontHeight;
        float y1 = (float)(sourceY ) / (float)fontHeight;

        textureCoordinates[0] = new Vec2(x0, y1);
        textureCoordinates[1] = new Vec2(x1, y0);
    
    }
}