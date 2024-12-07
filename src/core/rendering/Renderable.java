package core.rendering;

import java.nio.FloatBuffer;

import core.component.Shade;
import core.component.Sprite;
import core.component.component;
import core.game.Camera;
import core.game.PrimitiveShape;
import glm_.mat4x4.Mat4;



public abstract class Renderable  extends component
{
    int scale = 377 * 2;
    float vertices[];

    int Vertex_Size = 7;
    protected static int BATCH_SIZE = 150;
    protected static int VERTEX_SIZE = 7;
    int VBO, EBO, VAO;
    Mat4 model;
    boolean update = false;
    Sprite sprite;
    PrimitiveShape Shape;
    Shade shade;
    Camera cam;
    FloatBuffer verticesBuffer;
    
    public abstract void Draw();
}
