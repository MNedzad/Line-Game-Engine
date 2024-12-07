package core.rendering;

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;

import static org.lwjgl.opengl.GL11.glGenTextures;

import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.geotools.api.style.StyleFactory;
import org.geotools.styling.StyleBuilder;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.APIUtil.Encoder;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FT_Memory;
import org.lwjgl.util.freetype.FreeType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.util.Map;

public class CFont {
    private String filepath;
    private int fontSize;

    private Map<Integer, CharInfo> characterMap;

    public int textureId;
    private int width;
    private int height;
    private int lineHeight;

    public int getFontSize() {
        return fontSize;
    }

    public CFont(String filepath, int fontSize) {

        glActiveTexture(GL_TEXTURE0);
        this.filepath = filepath;
        this.fontSize = fontSize;
        this.characterMap = new HashMap<>();

        generateBitmap();
    }

    public CharInfo getCharacter(int codepoint) {
        return characterMap.getOrDefault(codepoint, new CharInfo(0, 0, 0, 0));
    }

    public void generateBitmap() {
        Font font;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(filepath));
            font = font.deriveFont(Font.PLAIN,fontSize);

            // Create fake image to get font information
            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setFont(font);
            FontMetrics fontMetrics = g2d.getFontMetrics();

       
            width = 0;
            height = fontMetrics.getHeight() ;
            lineHeight = fontMetrics.getHeight() ;
            int x = 0;
            int y = (int) (fontMetrics.getHeight());

            for (int i = 0; i < font.getNumGlyphs(); i++) {
                if (font.canDisplay(i)) {
                    // Get the sizes for each codepoint glyph, and update the actual image width and
                    // height
                    
                    CharInfo charInfo = new CharInfo(x, y, fontMetrics.charWidth(i), fontMetrics.getHeight());
                    characterMap.put(i, charInfo);
                    width = Math.max(x + fontMetrics.charWidth(i), width);

                    x += charInfo.width + fontSize;

                }
            }
            height += fontMetrics.getHeight() ;
            g2d.dispose();

            // Create the real texture
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            g2d = img.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(font);
            g2d.setColor(Color.WHITE);
            for (int i = 0; i < font.getNumGlyphs(); i++) {
                if (font.canDisplay(i)) {
                    CharInfo info = characterMap.get(i);
                    info.calculateTextureCoordinates(width, height );
                    g2d.drawString("" + (char) i, info.sourceX, info.sourceY);
                }
            }
            g2d.dispose();

            uploadTexture(img);

        } catch (FontFormatException | IOException e) {

            e.printStackTrace();
        }

    }

    private void uploadTexture(BufferedImage image) {

        int[] pixels = new int[image.getHeight() * image.getWidth()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                byte alphaComponent = (byte) ((pixel >> 24) & 0xFF);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
                buffer.put(alphaComponent);
            }
        }
        buffer.flip();

        textureId = glGenTextures();

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(),
                0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        buffer.clear();
    }
}