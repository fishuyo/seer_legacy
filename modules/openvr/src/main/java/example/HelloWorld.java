// package example;

// import openvrprovider.OpenVRProvider;
// import openvrprovider.OpenVRStereoRenderer;
// import org.joml.Matrix4f;
// import org.lwjgl.BufferUtils;
// import org.lwjgl.Sys;
// import org.lwjgl.glfw.*;
// import org.lwjgl.opengl.*;

// import java.nio.ByteBuffer;
// import java.nio.FloatBuffer;

// import static org.lwjgl.glfw.Callbacks.*;
// import static org.lwjgl.glfw.GLFW.*;
// import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
// import static org.lwjgl.opengl.GL11.*;
// import static org.lwjgl.system.MemoryUtil.*;

// /* Just an example demonstrating how everything works. */
// public class HelloWorld {
//     // Window stuff
//     private GLFWErrorCallback errorCallback;
//     private GLFWKeyCallback keyCallback;
//     private long window;
//     private OpenVRProvider vrProvider;
//     private OpenVRStereoRenderer vrRenderer;
//     static final int WIDTH = 1280;
//     static final int HEIGHT = 720;

//     public void setVRProvider(OpenVRProvider _vrProvider) {
//         vrProvider = _vrProvider;
//     }

//     public void run() {
//         System.out.println("LWJGL " + Sys.getVersion() + "!");
//         try {
//             init();
//             loop();
//             glfwDestroyWindow(window);
//             keyCallback.release();
//         } finally {
//             glfwTerminate();
//             errorCallback.release();
//         }
//     }

//     private void init() {
//         // Window init.
//         glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
//         if (glfwInit() != GL11.GL_TRUE)
//             throw new IllegalStateException("Unable to initialize GLFW");
//         glfwDefaultWindowHints(); // optional, the current window hints are already the default
//         glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
//         glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
//         glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//         glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
//         glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//         glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
//         window = glfwCreateWindow(WIDTH, HEIGHT, "OpenVR wrapper test", NULL, NULL);
//         if (window == NULL)
//             throw new RuntimeException("Failed to create the GLFW window");
//         glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
//             @Override
//             public void invoke(long window, int key, int scancode, int action, int mods) {
//                 if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
//                     glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
//             }
//         });
//         ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//         glfwSetWindowPos(
//                 window,
//                 (GLFWvidmode.width(vidmode) - WIDTH) / 2,
//                 (GLFWvidmode.height(vidmode) - HEIGHT) / 2
//         );
//         glfwMakeContextCurrent(window);

//         // OPENVR: Disable v-sync. This is important for VR to get high frame rates.
//         glfwSwapInterval(0);
//         glfwShowWindow(window);
//     }

//     public double getTime() {
//         return System.nanoTime() / 1000000000;
//     }

//     private void loop() {
//         // Create the OpenGL context
//         GLContext.createFromCurrent();

//         // Need to bind VAO before linking shader
//         int vaoHandle = constructVertexArrayObject();
//         GL30.glBindVertexArray(vaoHandle);
//         GL20.glEnableVertexAttribArray(0); // VertexPosition
//         GL20.glEnableVertexAttribArray(1); // VertexColor

//         // Create and link shader
//         ShaderProgram shader = new ShaderProgram();
//         String userDir = System.getProperty("user.dir");
//         shader.init(userDir + "/shaders/HelloWorld.glslv", userDir + "/shaders/HelloWorld.glslf");

//         // OPENVR: Initialize OpenVR. This must be done before creating your OpenVRStereoRenderer.
//         vrProvider.init();

//         // OPENVR: create the rendering context for the eyes.
//         // This object must be constructed after a valid GLContext exists.
//         vrRenderer = new OpenVRStereoRenderer(vrProvider, WIDTH, HEIGHT);

//         long nFrames = 0;
//         double fTime = getTime();
//         while (glfwWindowShouldClose(window) == GL_FALSE) {
//             // Show FPS
//             nFrames++;
//             double fps = nFrames / (getTime() - fTime);
//             if (nFrames % 1000 == 0)
//                 System.out.println("FPS: " + fps);

//             // OPENVR: call updateState(), force the info stored in the vrProvider to update.
//             // The upstream SDK recommends this sequence, and if you don't do things in this
//             // order frame submission can be glitchy.
//             vrProvider.updateState();
//             //System.out.println("Left eye\n" + vrProvider.vrState.getEyePose(0));
//             //System.out.println("RIght eye\n" + vrProvider.vrState.getEyePose(1));
//             for (int nEye = 0; nEye < 2; nEye++) {
//                 // OPENVR: bind the FBO associated with the target eye
//                 EXTFramebufferObject.glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, vrRenderer.getTextureHandleForEyeFramebuffer(nEye));

//                 // tell OpenGL to use the shader
//                 GL20.glUseProgram(shader.getProgramId());

//                 // Clear color
//                 glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//                 glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

//                 // OPENVR: get rendering transformations
//                 // Get projection and pose matrices from OpenVR.
//                 Matrix4f eyePose = vrProvider.vrState.getEyePose(nEye);
//                 Matrix4f matView = new Matrix4f(eyePose).invert();
//                 Matrix4f eyeProjection = vrProvider.vrState.getEyeProjectionMatrix(nEye);
//                 Matrix4f matMVP = eyeProjection.mul(matView);
//                 shader.setUniformMatrix("MVP", false, matMVP);

//                 // bind vertex and color data
//                 GL30.glBindVertexArray(vaoHandle);
//                 GL20.glEnableVertexAttribArray(0); // VertexPosition
//                 GL20.glEnableVertexAttribArray(1); // VertexColor

//                 // draw VAO
//                 GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

//                 // Window management
//                 glfwPollEvents();
//             }
//             // OPENVR: submit frame to compositor
//             vrProvider.submitFrame();
//             glFinish();
//         }
//     }

//     private int constructVertexArrayObject() {
//         float[] positionData = new float[]{
//                 1f, -1f, 0f,
//                 -1f, 0f, 0f,
//                 0f, 1f, 0f
//         };
//         float[] colorData = new float[]{
//                 0f, 0f, 1f,
//                 1f, 0f, 0f,
//                 0f, 1f, 0f
//         };
//         FloatBuffer positionBuffer = BufferUtils.createFloatBuffer(positionData.length);
//         positionBuffer.put(positionData);
//         positionBuffer.flip();
//         FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colorData.length);
//         colorBuffer.put(colorData);
//         colorBuffer.flip();
//         int positionBufferHandle = GL15.glGenBuffers();
//         GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
//         GL15.glBufferData(GL15.GL_ARRAY_BUFFER, positionBuffer, GL15.GL_STATIC_DRAW);
//         int colorBufferHandle = GL15.glGenBuffers();
//         GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
//         GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
//         GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//         int vaoHandle = GL30.glGenVertexArrays();
//         GL30.glBindVertexArray(vaoHandle);
//         GL20.glEnableVertexAttribArray(0);
//         GL20.glEnableVertexAttribArray(1);
//         GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, positionBufferHandle);
//         GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
//         GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferHandle);
//         GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 0, 0);
//         GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
//         return vaoHandle;
//     }

//     public static void main(String[] args) {
//         // OPENVR: object initialization.
//         OpenVRProvider provider = new OpenVRProvider();
//         try {
//             //provider.vrState.addControllerListener(new SampleControllerListener());
//             //Thread vrPoller = new Thread(provider, "vrPoller");
//             //vrPoller.start();
//             SharedLibraryLoader.load();
//             HelloWorld app = new HelloWorld();
//             app.setVRProvider(provider);
//             app.run();
//             //vrPoller.join();
//         } catch (Exception e) {
//             System.out.println("Unhandled exception: " + e.toString());
//             e.printStackTrace();
//         }
//         System.out.println("Exited normally.");
//     }
// }
