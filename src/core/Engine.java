package core;


import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;

import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import core.component.GameObject;
import core.component.Shade;
import core.component.Sprite;
import core.component.SpriteSheet;
import core.component.Texture2D;
import core.component.collisonHandler;
import core.editor.DeltaTimer;
import core.editor.Gui;
import core.events.MainLoop;
import core.game.Camera;
import core.game.ParticleSystem;
import core.game.Scene;
import core.game.Animation.Animation;
import core.game.Animation.Animator;
import core.game.Colliders.Box;
import core.game.Colliders.Polygon;
import core.rendering.CFont;
import core.rendering.FontReneder;
import core.rendering.Mesh;
import core.rendering.SceneWindow;
import core.rendering.Terrain;
import core.utils.FileLoader;
import core.window.Window;
import glm_.vec2.*;

import static org.lwjgl.glfw.GLFW.*;

public class Engine extends MainLoop {

    Window win = new Window();
    Terrain spirit;
    Shade shade;
    Texture2D texture;
    Texture2D textureBtf;
    Texture2D Tiles;
    Sprite object;
    int PixelX = 377;
    int PixelY = 377;
    Camera cam;
    double lastTime[];
    double nowTime[];
    CFont font;
    SpriteSheet sheet;
    List<Terrain> spr;
    FileLoader fl;
    FontReneder batch;
    GameObject Player;
    GameObject Box;
    SceneWindow sWindow;
    Gui gui;
    ParticleSystem ParticleSystem = new ParticleSystem();
    float fov = 10;
    private float delta[];
    private double fps;
        
    static public IntBuffer width, height;

    double m_secondCounter = 0;
    double m_tempFps = 0;

    DeltaTimer dt;
    Scene cScene;
    Terrain terrain;

    Engine() {

        // Texture Load //
        
        cam = new Camera(win);
        sWindow = new SceneWindow(Window.getWidth(),Window.getHeight());

        font = new CFont("Assets\\Fonts\\Playfair\\PlayfairDisplay-Regular.ttf",20);
        delta = new float[10];
        lastTime = new double[10];
        nowTime = new double[10];
        texture = new Texture2D("Assets/ice.png");
        Tiles = new Texture2D("Assets/Terraria/tiles.png");
        textureBtf = new Texture2D("Assets/btf.jpeg");

        dt = new DeltaTimer();

        fl = new FileLoader();

        shade = new Shade();
        shade.compileDefautl();

        shade.LinkShade();
        shade.Bind();

        shade.LinkShade();
   

        
        sheet = new SpriteSheet(Tiles);
        sheet.create(16, 16, 533, 0);
        fl.TSXLoader(new File("Assets/Terraria/tiles.tsx"));
        fl.FileReader(new File("Assets/Terraria/map2.tmj"));

        object = new Sprite(Tiles);

        spr = new ArrayList<>(2040);

    

        MainLoop();

    }

    private void MainLoop() 
    {
        glViewport(0, 0, Window.getWidth() , Window.getHeight());

        glClearColor(0.7f, 0.8f, 1.0f, 1.0f);
        glLoadIdentity();

        batch = new FontReneder();
        batch.generateShade();
        batch.font = font;
        batch.initBatch(win);

        cam.Update(delta[0]);
        cam.SetFov(fov);

  

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        cScene = new Scene();

        cScene.setCam(cam);
        gui = new Gui(sWindow, cScene);
        Mesh mesh = new Mesh(texture);
        mesh.setShade(shade);
        mesh.setShape(new Box(16, 16));

        Animator animator = new Animator();
        Animation anim = new Animation();
        anim.setName("Default");
        anim.addFrame(sheet.getSprite(2), 1);
        anim.addFrame(sheet.getSprite(3), 1);
        anim.addFrame(sheet.getSprite(4), 1);
        anim.addFrame(sheet.getSprite(5), 1);

        animator.addAnimation(anim);
        animator.setDefaultState("Default");

        Box = new GameObject("Box", "box", new Vec2(0, 0), 2, cScene);

        Polygon object2 = new Polygon(new Box(16, 16));
        object2.setMask((short) 3, false);

        
    

        ParticleSystem.setTexture(texture);
        ParticleSystem.setShade(shade);
        ParticleSystem.SetName("Particle_System");
        ParticleSystem.setSprite(sheet.getSprite(2));
        

        {

            Player = new GameObject("Player", "pl", new Vec2(0, 0), 2, cScene);
            Polygon object = new Polygon(new Box(16, 16));
            object.setMask((short) 0, false);
            terrain = new Terrain(shade, sheet, fl);
     
            Box.AddComponet(terrain);
            Box.AddComponet(object2);
            Player.AddComponet(ParticleSystem);
            Player.AddComponet(mesh);
            Player.AddComponet(object);
            Player.AddComponet(animator);

            Player.AddComponet(new collisonHandler(object));
          
        }

        



        shade.Bind();
        lastTime[0] = System.currentTimeMillis();
       
     

        this.init(win);
        gui.Init(window);
        cScene.Start();

        glfwSetWindowSizeCallback(window, this::windowSizeCallback);
        glDisable( GL_CULL_FACE );

        glDepthMask(false); 
       
      
    }


    private void windowSizeCallback(long window, int width, int height)
    {
        glViewport(0, 0, width, height);
        sWindow.rescaleWindow(width, height);
    }

    int Code;
    int number = 20;

    @Override
    public void draw() {
        lastTime[1] = System.currentTimeMillis();
        dt.setDelta(0);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );

        sWindow.Bind();
            
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
        cScene.DrawFromRender(delta[0]);
        
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        batch.addText(Double.toString(fps) + " ", 1, Window.getHeight()-5, 1f, 0x000000);
        batch.addText("Global: " + Double.toString(delta[0])+ " ms", 1, Window.getHeight() - 25, 1f, 0x000000);
        batch.addText("Draw: " + Double.toString(delta[1]) + " ms", 1, Window.getHeight() - 45 , 1f, 0x000000);
        batch.addText("Update: " + Double.toString(delta[3]) + " ms", 1, Window.getHeight() - 65, 1f, 0x000000);

        batch.flushBatch();

        sWindow.unBind();

    
        gui.run(cScene.getGameObject());

        getDelta(1);
      
        
        glfwSwapBuffers(window);

        dt.getFPS(0);
        dt.setDelta(0);
       
        GetFps(0);
        lastTime[0] = System.currentTimeMillis();
        
    }

    @Override
    public void event() {
        lastTime[2] = System.currentTimeMillis();
        dt.getDelta(0);
        glfwPollEvents();
        glfwSetScrollCallback(window, (win, dx, dy) -> {
            fov -= (float) dy;
            if (fov < 1.0f)
                fov = 1.0f;
            if (fov > 50.0f)
                fov = 50.0f;
        });

        cam.SetListener(Window.window);
        cam.freeCam((float) delta[0]);
        getDelta(2);
    }

    @Override
    public void update() {
        lastTime[3] = System.currentTimeMillis();
        cam.SetFov(fov);

        
        cScene.Update(delta[0]);

        Code = glGetError();
        if (Code > 0) {
            System.out.println("error Code: " + Code);
        }
        getDelta(3);
    }

    public void getDelta(int index) {
        nowTime[index] = System.currentTimeMillis();
        delta[index] = (float) (nowTime[index] - lastTime[index]);

    }
    private int time;
    public void GetFps(int index) {

        getDelta(index);
        if (m_secondCounter <= 1) {
            m_secondCounter += delta[index] / 1000;
            m_tempFps++;
        } else {
            while(time >= 0)
            {
                time -= delta[0] ; 
                return;
            }
            fps = m_tempFps;

            m_secondCounter = 0;
            m_tempFps = 0;
        }
    }
}
