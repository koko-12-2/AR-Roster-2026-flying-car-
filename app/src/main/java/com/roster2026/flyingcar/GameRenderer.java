package com.roster2026.flyingcar;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/** Procedural low-poly 3D city and flying car renderer using native OpenGL ES. */
final class GameRenderer implements GLSurfaceView.Renderer {
    private final GameState state;
    private int program, positionHandle, colorHandle, mvpHandle;
    private FloatBuffer cube;
    private final float[] projection = new float[16], view = new float[16], model = new float[16], mvp = new float[16], temp = new float[16];
    private final float[] carTransform = new float[16], partTransform = new float[16];
    private long previous;
    private float carX, carY = 2.2f, carZ, yaw;

    GameRenderer(GameState state) { this.state = state; }

    @Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.55f, 0.72f, 0.82f, 1f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        String vs = "attribute vec3 aPosition; uniform mat4 uMvp; void main(){ gl_Position=uMvp*vec4(aPosition,1.0); }";
        String fs = "precision mediump float; uniform vec4 uColor; void main(){ gl_FragColor=uColor; }";
        int v = shader(GLES20.GL_VERTEX_SHADER, vs), f = shader(GLES20.GL_FRAGMENT_SHADER, fs);
        program = GLES20.glCreateProgram(); GLES20.glAttachShader(program, v); GLES20.glAttachShader(program, f); GLES20.glLinkProgram(program);
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        colorHandle = GLES20.glGetUniformLocation(program, "uColor");
        mvpHandle = GLES20.glGetUniformLocation(program, "uMvp");
        float[] vertices = {
            -1,-1,1, 1,-1,1, 1,1,1, -1,-1,1, 1,1,1, -1,1,1,
            1,-1,-1, -1,-1,-1, -1,1,-1, 1,-1,-1, -1,1,-1, 1,1,-1,
            -1,-1,-1, -1,-1,1, -1,1,1, -1,-1,-1, -1,1,1, -1,1,-1,
            1,-1,1, 1,-1,-1, 1,1,-1, 1,-1,1, 1,1,-1, 1,1,1,
            -1,1,1, 1,1,1, 1,1,-1, -1,1,1, 1,1,-1, -1,1,-1,
            -1,-1,-1, 1,-1,-1, 1,-1,1, -1,-1,-1, 1,-1,1, -1,-1,1
        };
        cube = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer(); cube.put(vertices).position(0);
        previous = System.nanoTime();
    }

    private int shader(int type, String src) { int s=GLES20.glCreateShader(type); GLES20.glShaderSource(s,src); GLES20.glCompileShader(s); return s; }
    @Override public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0,0,width,height); float ratio=(float)width/Math.max(1,height);
        Matrix.perspectiveM(projection,0,58f,ratio,0.1f,180f);
    }

    @Override public void onDrawFrame(GL10 gl) {
        long now=System.nanoTime(); float dt=Math.min((now-previous)/1_000_000_000f,0.05f); previous=now;
        if(state.reset){carX=0;carY=2.2f;carZ=0;yaw=0;state.speed=0;state.altitude=0;state.distance=0;state.reset=false;}
        float turn=state.steer + (state.turnLeft?-0.8f:0f) + (state.turnRight?0.8f:0f);
        turn=Math.max(-1f,Math.min(1f,turn)); yaw += turn*dt*0.9f;
        state.speed=Math.max(0,Math.min(180,state.speed+(state.lift?23f:-5f)*dt));
        carY=Math.max(1.7f,Math.min(14f,carY+(state.lift?4.5f:-1.6f)*dt));
        carX+= (float)Math.sin(yaw)*state.speed*dt*0.012f;
        carZ-=state.speed*dt*0.018f;
        state.altitude=carY;state.distance+=state.speed*dt*0.02f;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(view,0,carX,carY+4.8f,carZ+15.5f,carX,carY+0.4f,carZ-11f,0,1,0);
        GLES20.glUseProgram(program); GLES20.glEnableVertexAttribArray(positionHandle);
        cube.position(0); GLES20.glVertexAttribPointer(positionHandle,3,GLES20.GL_FLOAT,false,0,cube);
        drawWorld(); drawCar();
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private void drawWorld() {
        box(0,-0.65f,-42,90,0.5f,125,0.27f,0.34f,0.34f,1);
        box(0,-0.37f,-42,8,0.08f,125,0.19f,0.22f,0.23f,1);
        for(int i=0;i<18;i++){
            float z=-8-i*7.0f;
            box(0,-0.30f,z,0.12f,0.04f,2.1f,0.88f,0.79f,0.56f,1);
        }
        float[] palette={0.69f,0.70f,0.66f,0.73f,0.64f,0.61f,0.53f,0.65f,0.64f,0.74f,0.72f,0.67f};
        for(int i=0;i<24;i++){
            float z=-9-i*5.7f;
            for(int side=-1;side<=1;side+=2){
                float width=3.0f+(i%3)*0.55f, height=3.5f+(i*7%9)*0.75f;
                float x=side*(7.0f+(i%4)*2.5f);
                float r=palette[(i*3)%palette.length],g=palette[(i*3+1)%palette.length],b=palette[(i*3+2)%palette.length];
                box(x,height/2-0.25f,z,width,height,4.2f,r,g,b,1);
                box(x,height-0.18f,z,width+0.15f,0.25f,4.35f,r*0.76f,g*0.76f,b*0.76f,1);
                for(int row=1;row<Math.min(5,(int)(height/1.3f));row++){
                    float wy=row*1.25f;
                    box(x,wy,z+2.13f,width*0.72f,0.12f,0.035f,0.54f,0.72f,0.76f,1);
                    box(x+side*(width/2+0.025f),wy,z,width*0.035f,0.11f,2.6f,0.48f,0.65f,0.70f,1);
                }
                box(x+0.5f,height+0.25f,z,0.45f,0.45f,0.55f,0.38f,0.42f,0.42f,1);
            }
        }
        for(int side=-1;side<=1;side+=2){
            box(side*9f,0.0f,5f,15f,0.35f,12f,0.47f,0.50f,0.48f,1);
            for(int j=0;j<7;j++) box(side*(2.5f+j*2.1f),0.23f,1f,0.08f,0.025f,11f,0.68f,0.70f,0.67f,1);
            for(int j=0;j<3;j++) box(side*(5f+j*3f),0.5f,3f,0.7f,0.55f,0.7f,0.37f,0.4f,0.4f,1);
        }
    }

    private void drawCar() {
        Matrix.setIdentityM(carTransform,0); Matrix.translateM(carTransform,0,carX,carY,carZ); Matrix.rotateM(carTransform,0,(float)Math.toDegrees(yaw),0,1,0);
        localBox(0,0,0,2.6f,0.62f,4.6f,0.88f,0.91f,0.91f,1);
        localBox(0,0.48f,-0.35f,1.65f,0.65f,1.95f,0.16f,0.23f,0.28f,1);
        localBox(0,-0.12f,-0.25f,5.6f,0.16f,1.35f,0.75f,0.78f,0.78f,1);
        localBox(0,0.13f,1.7f,2.6f,0.14f,0.75f,0.76f,0.78f,0.78f,1);
        for(int side=-1;side<=1;side+=2){
            localBox(side*0.88f,-0.48f,0.35f,0.25f,0.42f,0.28f,0.12f,0.15f,0.17f,1);
            localBox(side*1.0f,-0.5f,-1.05f,0.33f,0.22f,0.65f,0.10f,0.13f,0.15f,1);
            localBox(side*1.0f,-0.5f,-1.05f,0.12f,0.12f,0.16f,0.20f,0.62f,1f,1);
        }
    }

    private void box(float x,float y,float z,float sx,float sy,float sz,float r,float g,float b,float a){
        Matrix.setIdentityM(model,0);Matrix.translateM(model,0,x,y,z);Matrix.scaleM(model,0,sx/2,sy/2,sz/2);submit(r,g,b,a);
    }
    private void localBox(float x,float y,float z,float sx,float sy,float sz,float r,float g,float b,float a){
        Matrix.setIdentityM(partTransform,0);
        Matrix.translateM(partTransform,0,x,y,z);
        Matrix.scaleM(partTransform,0,sx/2,sy/2,sz/2);
        Matrix.multiplyMM(model,0,carTransform,0,partTransform,0);
        submit(r,g,b,a);
    }
    private void submit(float r,float g,float b,float a){
        Matrix.multiplyMM(temp,0,view,0,model,0);Matrix.multiplyMM(mvp,0,projection,0,temp,0);
        GLES20.glUniformMatrix4fv(mvpHandle,1,false,mvp,0);GLES20.glUniform4f(colorHandle,r,g,b,a);GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,36);
    }
}
