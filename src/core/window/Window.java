package core.window;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;




import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.IntBuffer;


public class Window {
    

    private String tile;
    static public IntBuffer width, height;
   static public long window;


    public Window() {
        this.tile = "Line Game engine";
        width = BufferUtils.createIntBuffer(1);
        height = BufferUtils.createIntBuffer(1);

        init();
    }

    public  long getWindow() {
        return window;
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        
        
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");
            
        if(!glfwVulkanSupported())
            throw new IllegalStateException("Unable to initialize Vulkan");

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        
        
        window = glfwCreateWindow(700, 400, tile, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

       
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        
        // Make the window visible
        glfwShowWindow(window);

        glfwGetWindowSize(window, width, height);

       

        GL.createCapabilities();
    }
    public static int getWidth() {
        if(width == null)
            return 0;
        
        return width.get(0);
    }



    public static int getHeight() 
    {
        if(height == null)
            return 0;
        

        return height.get(0);
    }
}