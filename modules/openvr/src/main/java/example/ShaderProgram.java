package example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    // OpenGL handle that will point to the executable shader program
    // that can later be used for rendering
    private int programId;
    protected static FloatBuffer buf16Pool;

    public void init(String vertexShaderFilename, String fragmentShaderFilename) {
        // create the shader program. If OK, create vertex and fragment shaders
        programId = glCreateProgram();

        // load and compile the two shaders
        int vertShader = loadAndCompileShader(vertexShaderFilename, GL_VERTEX_SHADER);
        int fragShader = loadAndCompileShader(fragmentShaderFilename, GL_FRAGMENT_SHADER);

        // attach the compiled shaders to the program
        glAttachShader(programId, vertShader);
        glAttachShader(programId, fragShader);

        // now link the program
        glLinkProgram(programId);

        // validate linking
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("could not link shader. Reason: " + glGetProgramInfoLog(programId, 1000));
        }

        // perform general validation that the program is usable
        glValidateProgram(programId);

        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            throw new RuntimeException("could not validate shader. Reason: " + glGetProgramInfoLog(programId, 1000));
        }
    }

    /*
     * With the exception of syntax, setting up vertex and fragment shaders
     * is the same.
     * @param the name and path to the vertex shader
     */
    private int loadAndCompileShader(String filename, int shaderType) {
        //vertShader will be non zero if succefully created
        int handle = glCreateShader(shaderType);

        if (handle == 0) {
            throw new RuntimeException("could not created shader of type " + shaderType + " for file " + filename + ". " + glGetProgramInfoLog(programId, 1000));
        }

        // load code from file into String
        String code = loadFile(filename);

        // upload code to OpenGL and associate code with shader
        glShaderSource(handle, code);

        // compile source code into binary
        glCompileShader(handle);

        // acquire compilation status
        int shaderStatus = glGetShaderi(handle, GL20.GL_COMPILE_STATUS);

        // check whether compilation was successful
        if (shaderStatus == GL11.GL_FALSE) {
            throw new IllegalStateException("compilation error for shader [" + filename + "]. Reason: " + glGetShaderInfoLog(handle, 1000));
        }

        return handle;
    }

    /**
     * Load a text file and return it as a String.
     */
    private String loadFile(String filename) {
        StringBuilder vertexCode = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                vertexCode.append(line);
                vertexCode.append('\n');
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("unable to load shader from file [" + filename + "]", e);
        }

        return vertexCode.toString();
    }

    /**
     * Gets the location of the specified uniform name.
     *
     * @param str the name of the uniform
     * @return the location of the uniform in this program
     */
    public int getUniformLocation(String str) {
        return glGetUniformLocation(programId, str);
    }


    public int getProgramId() {
        return programId;
    }

    /**
     * Sets the uniform data at the specified location (the uniform type may be int, bool or sampler2D).
     *
     * @param loc the location of the int/bool/sampler2D uniform
     * @param i   the value to set
     */
    public void setUniformi(int loc, int i) {
        if (loc == -1) return;
        glUniform1i(loc, i);
    }

    public void setUniformf(int loc, float f) {
        if (loc == -1) return;
        glUniform1f(loc, f);
    }

    /**
     * Sends a 4x4 matrix to the shader program.
     *
     * @param loc        the location of the mat4 uniform
     * @param transposed whether the matrix should be transposed
     * @param mat        the matrix to send
     */
    public void setUniformMatrix(int loc, boolean transposed, Matrix4f mat) {
        if (loc == -1) return;
        if (buf16Pool == null)
            buf16Pool = BufferUtils.createFloatBuffer(16);
        buf16Pool.clear();
        mat.get(buf16Pool);
        glUniformMatrix4fv(loc, transposed, buf16Pool);
    }

    public boolean setUniformi(String name, int i) {
        int loc = getUniformLocation(name);
        if (loc >= 0)
            setUniformi(loc, i);
        else {
            System.out.println("Warning: tried to set an invalid integer uniform.");
            return false;
        }
        return true;
    }

    public boolean setUniformf(String name, float f) {
        int loc = getUniformLocation(name);
        if (loc >= 0)
            setUniformf(loc, f);
        else {
            System.out.println("Warning: tried to set an invalid float uniform.");
            return false;
        }
        return true;
    }

    public boolean setUniformMatrix(String name, boolean transposed, Matrix4f mat) {
        int loc = getUniformLocation(name);
        if (loc >= 0) {
            setUniformMatrix(loc, transposed, mat);
        } else {
            System.out.println("Warning: tried to set an invalid uniform matrix.");
            return false;
        }
        return true;
    }
}

