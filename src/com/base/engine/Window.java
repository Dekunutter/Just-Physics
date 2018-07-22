package com.base.engine;

import com.base.engine.input.keyboard.Keyboard;
import com.base.engine.input.mouse.MouseCursor;
import com.base.engine.render.DisplaySettings;
import com.base.engine.render.GLFWSettings;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private final String title;
    private int width, height;
    private long handle;
    private boolean isResized, isVSyncEnabled, isFramerateCapped;
    private int framerateCap;

    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWFramebufferSizeCallback resizeCallback;

    public Window(String title, Vector2i dimensions, boolean isVSyncEnabled) {
        this(title, dimensions.x, dimensions.y, isVSyncEnabled);
    }

    public Window(String title, int width, int height, boolean isVSyncEnable) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.isVSyncEnabled = isVSyncEnabled;
        this.isFramerateCapped = DisplaySettings.isFramerateCapped();
        this.framerateCap = DisplaySettings.getTargetFramerate();
        this.isResized = false;
    }

    public void init() {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, GLFWSettings.getVersionMajor());
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, GLFWSettings.getVersionMinor());
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if(handle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        //callback to handle window resizing
        glfwSetFramebufferSizeCallback(handle, resizeCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int newWidth, int newHeight) {
                if(newWidth > 0 && newHeight > 0) {
                    width = newWidth;
                    height = newHeight;
                    setResized(true);
                }
            }
        });

        //initialize to the middle of the monitor
        GLFWVidMode vidMode = DisplaySettings.getMonitorResolution();
        glfwSetWindowPos(handle, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);

        glfwMakeContextCurrent(handle);

        setupVSync();

        glfwShowWindow(handle);

        GL.createCapabilities();

        glClearColor(0, 0, 0, 0);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public void setupWindowInput() {
        Keyboard.init(this);
        MouseCursor.init(this);
    }


    public void setupVSync() {
        if(!isVSyncEnabled) {
            glfwSwapInterval(0);
        } else {
            if(isFramerateCapped) {
                //TODO: Can't use floats but should have better interval values than looking for hardcoded 60 or 30 frames
                if(framerateCap == 60) {
                    glfwSwapInterval(1);
                } else if(framerateCap == 30) {
                    glfwSwapInterval(2);
                } else {
                    glfwSwapInterval(1);
                }
            } else {
                glfwSwapInterval(1);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getHandle() {
        return handle;
    }

    public boolean isResized() {
        return isResized;
    }

    public boolean isVSyncEnabled() {
        return isVSyncEnabled;
    }

    public void update() {
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }

    public void setResized(boolean value) {
        isResized = true;
    }

    public void cleanUp() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
        glfwTerminate();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void resizeIfNeeded() {
        if(isResized) {
            glViewport(0, 0, width, height);
            setResized(false);
        }
    }
}
