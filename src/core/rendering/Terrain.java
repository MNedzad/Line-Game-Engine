package core.rendering;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;

import static org.lwjgl.opengl.GL11.glDrawElements;

import static org.lwjgl.opengl.GL11.glGetError;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import org.lwjgl.opengl.GL15;

import core.game.Camera;
import core.game.Scene;
import core.game.Colliders.Box;
import core.utils.FileLoader;
import core.utils.MapClass;
import core.component.Shade;
import core.component.SpriteSheet;
import core.component.Texture2D;
import core.component.component;
import glm_.glm;
import glm_.mat4x4.Mat4;
import glm_.vec2.Vec2;
import glm_.vec3.Vec3;

public class Terrain extends Renderable {
    int Type;
    int shaderProgram;
    boolean update = true;
    int texCoord0Loc;
    boolean dirty = true;
    int VBO;
    int VAO;

    Mat4 model;
    int code;
    glm GLM = glm.INSTANCE;
    Scene scene;
    Vec2 sizePIX;
    Texture2D tex;
    Camera cam;
    public Shade shade;
    FloatBuffer verticesBuffer;
    MapClass map;
    SpriteSheet sheet;
    FileLoader fl;

    int Size = 16;
    long dc;
    long glrc;
    long glrc1;
    int sizebtc;

    boolean first = true;
    private int[] indices = {
            0, 1, 3,
            1, 2, 3
    };

    public Terrain(Shade shade, SpriteSheet sheet, FileLoader fl) {
        super();
        this.shade = shade;
        this.sheet = sheet;
        this.fl = fl;

        sizePIX = new Vec2(16, 16);
        vertices = new float[BATCH_SIZE * Vertex_Size];

    }

    public void generateEbo() {
        int elementSize = BATCH_SIZE * 3;
        int[] elementBuffer = new int[elementSize];

        for (int i = 0; i < elementSize; i++) {
            elementBuffer[i] = indices[(i % 6)] + ((i / 6) * 4);
        }

        // Gen Buffers
        int ebo = glGenBuffers();

        // Bind th EBO to the GL_ELEMENT_ARRAY_BUFFER target.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

        // Bind Indicate on elementBuffer
        glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);
    }

    @Override
    public void start() {

        cam = gameObject.getScene().getCam(); // Get the camera object from the current scene associated with this game object
        camPos = cam.getCameraPos(); // Get Camera Position
        map = fl.getMap();// Get the map object
        ChunkLenght = fl.getMap().getChunkLenght(); // Get the length of the chunk, presumably to control the rendering
        sheet.getSprite(map.getData(0, 0)).getTexture().bind(); // Bind texture to the GL_TEXTURE_2D
        init();
        Shape = new Box(sizePIX.getX(), sizePIX.getY());        // Create box shape of object dimension
    }

    public void init() {
        // Create a new 4x4 matrix to represent the transformation model
        model = new Mat4();

        // Apply a translation transformation to the model matrix
        model = GLM.translate(model, new Vec3((0) / scale, (0) / scale, 0.0f));

        // Generate vertex arrays
        VAO = glGenVertexArrays();

        // Bind vertex array object
        glBindVertexArray(VAO);

        // Generate Vertex Buffer Object
        VBO = glGenBuffers();

        // Bind the Vertex Buffer Object (VBO) to the GL_ARRAY_BUFFER target.
        glBindBuffer(GL_ARRAY_BUFFER, VBO);

        // Allocate space for the buffer, considering the number of vertices to be
        // handled (VERTEX_SIZE * BATCH_SIZE),
        glBufferData(GL_ARRAY_BUFFER, Float.BYTES * VERTEX_SIZE * BATCH_SIZE, GL_DYNAMIC_DRAW);

        // Generate and Bind Indicate to the element buffer
        generateEbo();

        // Construct a direct native-order floatbuffer with the specified number of
        // elements.
        verticesBuffer = BufferUtils.createFloatBuffer(Float.BYTES * VERTEX_SIZE * BATCH_SIZE);

        // Define stride of attribut pointer
        int stride = 7 * Float.BYTES;

        // Set up vertex attribute pointer for mesh coordinates
        glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0); // Enable attribute 0

        // Set up vertex attribute pointer for color
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 2 * Float.BYTES);
        glEnableVertexAttribArray(1); // Enable attribute 1

        // Set up vertex attribute pointer for UV
        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 5 * Float.BYTES);
        glEnableVertexAttribArray(2);// Enable attribute 2

        {
            // bind the shader program
            shade.Bind();

            // Get the location of the unifrom variable in the shader program.
            int projection = glGetUniformLocation(shade.getShaderProgram(), "projection");
            int view = glGetUniformLocation(shade.getShaderProgram(), "view");
            int transform = glGetUniformLocation(shade.getShaderProgram(), "transform");

            // Upload camera view and projectiom to shader program.
            glUniformMatrix4fv(projection, false,
                    cam.GetProjection().toBuffer().asFloatBuffer());
            glUniformMatrix4fv(view, false,
                    cam.getView().toBuffer().asFloatBuffer());

            // Enable the vertex attribute for texture coordinates
            glEnableVertexAttribArray(glGetAttribLocation(shade.getShaderProgram(), "InTexCoord"));

            // Upload transform to shader program
            glUniformMatrix4fv(transform, false, model.toBuffer().asFloatBuffer());
        }
        shade.unBind();

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void bind() 
    {
        verticesBuffer.put(vertices).flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBindVertexArray(VAO);
        glBufferData(GL_ARRAY_BUFFER, Float.BYTES
                * VERTEX_SIZE * BATCH_SIZE, GL_DYNAMIC_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        sizebtc = 0;

    }

    public void flushBatch() 
    {
        {
            shade.Bind();
            glBindVertexArray(VAO);
            glActiveTexture(GL_TEXTURE0); // Activate gl_texture0
            sheet.getSprite(fl.getMap().getData(0, 0)).getTexture().bind(); // Bind the texture of the sprite that
                                                                            // corresponds to the specific data in the
                                                                            // map.
            glDrawElements(GL_TRIANGLES, sizebtc * 6, GL_UNSIGNED_INT, 0); // Render triangles using the indices stored
                                                                           // in the currently bound index buffer.
            glBindVertexArray(0); //
            if (glGetError() > 0) { // Check for OpenGL errors after the draw call.
                System.err.println("Something Wrong With Rendering");
            }
        }
    }

    int index = 0;
    int Data;
    int xs;
    int ys;
    int ChunkLenght;
    Vec3 camPos;
    float tx;
    float ty;

    @Override
    public void update(float dt) {
        shade.Bind(); // Bind the shader program
        int location = glGetUniformLocation(shade.getShaderProgram(), "view"); // Get the location of the 'view' in the
                                                                               // shader program
        glUniformMatrix4fv(location, false,
                cam.getView().toBuffer().asFloatBuffer()); // Upload the 'view' variable in the shader program
        camPos = cam.getCameraPos(); // Get Camera Position

    }

    private void setVertices(int x, int y, int chunks) {

        Data = map.getData(16 * y + x, chunks); // Retrieve the data from the map at the specified position
        if (sizebtc >= BATCH_SIZE - 4) // Check if the current size of the batch (sizebtc) is greater than or equal to
                                       // the threshold
        {
            bind();
        }

        if (Data == 0) // Return if Data is equal to 0
            return;
        Shape.setPosition(16.f * xs, -16.f * ys); // Set position for collision
        Shape.updatePosition(); // Upload position

        index = sizebtc * 7; // Calculate the starting index in the vertices array based on the current batch
                             // size (sizebtc)
                             // Each entry in this batch contributes 7 floats in the vertices array.
        for (int i = 0; i < 4; i++) { // Loop through four verticesw
            vertices[index] = (gameObject.getPosition().getX() + (float) Shape.getMesh().getCoordinates()[i].x) / scale; // Set position of points with scale
            vertices[index + 1] = (gameObject.getPosition().getY() +(float) Shape.getMesh().getCoordinates()[i].y) / scale;   

            vertices[index + 2] = sheet.getSprite(Data).Color[i].getX();
            vertices[index + 3] = sheet.getSprite(Data).Color[i].getY();
            vertices[index + 4] = sheet.getSprite(Data).Color[i].getZ();

            vertices[index + 5] = sheet.getSprite(Data - 1).texCoords[i].getX();
            vertices[index + 6] = sheet.getSprite(Data - 1).texCoords[i].getY();
            index += 7;
        }

        sizebtc += 4; // Increament the batch size counter 'sizebtc' by 4

    }

    private boolean isOnScreen(int x) {
        return x * Size >= (camPos.getX() * scale) + (Size * 8) * 1.8
                || x * Size <= (camPos.getX() * scale) - (Size * 8) * 1.8;
    }

    private boolean isOnScreenY(int y) {
        return y * Size >= -(camPos.getY() * scale) + (Size * 8) * 1.8
                || y * Size <= -(camPos.getY() * scale) - (Size * 8) * 1.8;
    }

    @Override
    public int compareTo(component o) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'compareTo'");
    }

    @Override
    public void Draw() {
        for (int c = 0; c < ChunkLenght; c++) {
            ty = map.getChunksY(c);
            for (int y = 0; y < 16; y++) {
                ys = y + (int) ty;
                if (isOnScreenY(ys))
                    continue;
                tx = map.getChunksX(c);
                for (int x = 0; x < 16; x++) {
                    xs = x + (int) tx;
                    if (isOnScreen(xs))
                        continue;

                    setVertices(x, y, c);
                    flushBatch();
                }

            }
            flushBatch();
        }
    }
}
