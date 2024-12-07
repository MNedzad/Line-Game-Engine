package core.editor;


import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import core.component.GameObject;
import core.component.component;
import core.game.Scene;
import core.game.Scene.State;
import core.rendering.Mesh;
import core.rendering.SceneWindow;
import core.rendering.Terrain;
import core.window.Window;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTabBarFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class Gui {
    private ImGuiImplGlfw glfw;
    private ImGuiImplGl3 gl3;
    private int VBO;
    private SceneWindow sWindow;
    private List<GameObject> gObjects;
    private int selectedGMO; 
    private Scene MainScene;
    float[] pos = new float[]{0,0,0};

    private int Flags = ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize
            | ImGuiWindowFlags.NoMove
            | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoBringToFrontOnFocus
            | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoDecoration;

    public Gui(SceneWindow window, Scene scene) 
    {
        glfw = new ImGuiImplGlfw();
        gl3 = new ImGuiImplGl3();
        VBO = glGenBuffers();
        MainScene = scene;
        sWindow = window;
    }

    public void Init(long win) {
        createContext();
        glfw.init(win, true);
        gl3.init("#version 130");

    }

    public void createContext() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
   
    }

    public void run(List<GameObject> gameObjects) {
        glBindBuffer(GL_ARRAY_BUFFER, VBO); // Bind the Vertex Buffer Object (VBO) to the GL_ARRAY_BUFFER target.

        glBufferData(GL_ARRAY_BUFFER, Float.BYTES // Allocate space for the buffer, considering the number of vertices
                                                  // to be handled (VERTEX_SIZE * BATCH_SIZE),
                * 7 * 220, GL_DYNAMIC_DRAW);

        gl3.newFrame();
        glfw.newFrame();
        ImGui.newFrame();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.f);
     
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, new ImVec2(0.f, 0.f));

        ImGui.getStyle().setWindowTitleAlign(0.5f, 0);

        ImGui.begin("Main", Flags);
        {
        
            ImGui.popStyleVar(3);
            ImGui.beginMainMenuBar();
            if(ImGui.button("Play"))
            {
                MainScene.setStatePlay();
            }
            ImGui.endMainMenuBar();
            ImGui.setWindowPos(ImGui.getMainViewport().getPosX(), ImGui.getMainViewport().getPosY()+18, ImGuiCond.Always);
            ImGui.setWindowSize(new ImVec2(Window.getWidth(), Window.getHeight()-18));

            ImGui.dockSpace(ImGui.getID("Dock_Space"), new ImVec2(0.0f, 0.0f), imgui.internal.flag.ImGuiDockNodeFlags.HiddenTabBar );
        }
        ImGui.end();
        
        ImGui.begin("Inspect", ImGuiWindowFlags.NoTitleBar  | ImGuiWindowFlags.NoNav| ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize| ImGuiWindowFlags.NoDecoration);
        {
            ImGui.text("Inspect");
            ImGui.separator();
    
            ImGui.newLine();

            gameObjects.forEach(g ->
            {
           
                if(selectedGMO == 0)
                {
                    return;
                }
                component cmp = g.getComponentByHashCode(selectedGMO);
                if(cmp == null)
                {
                    return;
                }
                ImGui.text(cmp.getClass().getSimpleName());
              
                ImGui.separator();
                if (cmp.getClass().isAssignableFrom(Mesh.class) || cmp.getClass().isAssignableFrom(Terrain.class)) 
                {
                    ImGui.text("Transform");
                    pos[0] = cmp.gameObject.getPosition().getX();
                    pos[1] = cmp.gameObject.getPosition().getY();
                    ImGui.dragFloat3("", pos);    
                    cmp.gameObject.SetPosition(pos[0], pos[1]);
                }
               
                //cmp.gameObject.SetPosition(GL_ARRAY_BUFFER, GL_DYNAMIC_DRAW);
            });
     
        }
        ImGui.end();

        ImGui.begin("Hierarchy");
        {
            ImGui.text("Hierarchy");
            ImGui.separator();
    
  

            gameObjects.forEach(g ->
            {
                
                if(ImGui.collapsingHeader(g.getName()))
                {
                        g.getComponents().forEach(o ->
                        {
                            if(ImGui.button(o.getClass().getSimpleName()))
                            {
                                selectedGMO = o.hashCode();
                            }
                        });
                }
        
            });
        }
        ImGui.end();
        ImGui.begin("Console");
        {
  
            ImGui.text("Console");
            ImGui.separator();
    
            ImGui.text("main");
 
        }
        ImGui.end();


        ImGui.begin("Assets");
        {
           
            ImGui.text("Assets");
            ImGui.separator();
    
            ImGui.text("main");
            
            if(ImGui.isWindowDocked())
            {

            }
        }
        ImGui.end();

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, new ImVec2(4.f, 4.f));

        ImGui.begin("Scene");
        {
            ImGui.text("Scene");
            ImGui.separator();
    
      
            ImGui.popStyleVar(3);
            ImGui.beginChild("GameRender",ImGuiWindowFlags.AlwaysAutoResize);         
                ImGui.image(sWindow.getTexture(), ImGui.getContentRegionAvail(),new ImVec2(0,1), new ImVec2(1, 0) );
            ImGui.endChild();
        }
        ImGui.end();

        ImGui.showDemoWindow();

        ImGui.render();

        final long backupCurrentContext = GLFW.glfwGetCurrentContext();
        ImGui.updatePlatformWindows();
        ImGui.renderPlatformWindowsDefault();
        GLFW.glfwMakeContextCurrent(backupCurrentContext);

        gl3.renderDrawData(ImGui.getDrawData());
    }
}
