package com.roster2026.flyingcar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/** Native touch HUD layered over the 3D scene. */
final class HudView extends View {
    private final GameState state;
    private final Paint p=new Paint(3);
    private float density,cx,cy,joyX,joyY;
    private int activePointer=-1;
    private final RectF liftRect=new RectF(),leftRect=new RectF(),rightRect=new RectF(),resetRect=new RectF();
    HudView(Context c,GameState s){super(c);state=s;density=getResources().getDisplayMetrics().density;setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    @Override protected void onDraw(Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight();p.setStyle(Paint.Style.FILL);p.setColor(0x230c1a28);c.drawRect(0,0,w,h,p);
        // Header capsule and speed gauge.
        p.setColor(0x77324150);c.drawRoundRect(22*density,24*density,208*density,70*density,24*density,24*density,p);
        p.setColor(Color.WHITE);p.setTextSize(15*density);p.setFakeBoldText(true);c.drawText("ROSTER 2026",38*density,53*density,p);p.setFakeBoldText(false);
        p.setTextSize(11*density);c.drawText("3D FLIGHT",39*density,66*density,p);
        p.setTextSize(36*density);c.drawText(Integer.toString(Math.round(state.speed)),w-78*density,53*density,p);p.setTextSize(10*density);c.drawText("KM/H",w-70*density,69*density,p);
        p.setColor(0x88ffffff);c.drawRoundRect(w-83*density,76*density,w-21*density,80*density,2*density,2*density,p);p.setColor(0xff5ca9ff);c.drawRoundRect(w-83*density,76*density,w-83*density+62*density*state.speed/180f,80*density,2*density,2*density,p);
        resetRect.set(w-62*density,18*density,w-16*density,66*density);button(c,resetRect,"↻",0x88324150,23*density);
        // Steering joystick.
        float radius=Math.min(78*density,w*.22f);cx=radius+25*density;cy=h-112*density;
        p.setColor(0x77303b4b);c.drawCircle(cx,cy,radius,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.5f*density);p.setColor(0x99ffffff);c.drawCircle(cx,cy,radius,p);p.setStyle(Paint.Style.FILL);
        p.setColor(0x55ffffff);p.setTextSize(21*density);c.drawText("‹",cx-radius*.72f,cy+7*density,p);c.drawText("›",cx+radius*.59f,cy+7*density,p);c.drawText("⌃",cx-6*density,cy-radius*.63f,p);
        float kx=cx+joyX*radius*.58f,ky=cy+joyY*radius*.58f;p.setColor(Color.WHITE);c.drawCircle(kx,ky,24*density,p);p.setColor(0xff3778ff);c.drawCircle(kx,ky,19*density,p);p.setColor(Color.WHITE);c.drawCircle(kx,ky,5*density,p);
        float br=38*density,bx=w-145*density,by=h-188*density;leftRect.set(bx-br,by-br,bx+br,by+br);rightRect.set(w-62*density-br,by-br,w-62*density+br,by+br);liftRect.set(w-143*density,h-133*density,w-31*density,h-21*density);
        button(c,leftRect,"◀\nLEFT",0x88313a47,12*density);button(c,rightRect,"RIGHT\n▶",0x88313a47,12*density);
        p.setColor(Color.WHITE);c.drawCircle(liftRect.centerX(),liftRect.centerY(),liftRect.width()/2,p);p.setColor(0xff152433);p.setTextSize(31*density);p.setFakeBoldText(true);c.drawText("↑",liftRect.centerX()-10*density,liftRect.centerY()-2*density,p);p.setTextSize(13*density);c.drawText("LIFT",liftRect.centerX()-16*density,liftRect.centerY()+25*density,p);p.setFakeBoldText(false);
        p.setColor(0xccffffff);p.setTextSize(11*density);c.drawText("ARCORE-FREE  •  3D GAME",w/2-91*density,32*density,p);
        postInvalidateDelayed(80);
    }
    private void button(Canvas c,RectF r,String label,int color,float size){p.setStyle(Paint.Style.FILL);p.setColor(color);c.drawRoundRect(r,28*density,28*density,p);p.setColor(Color.WHITE);p.setTextSize(size);p.setTextAlign(Paint.Align.CENTER);String[] lines=label.split("\\n");float y=r.centerY()-(lines.length-1)*size*.48f;for(String line:lines){c.drawText(line,r.centerX(),y,p);y+=size*1.05f;}p.setTextAlign(Paint.Align.LEFT);}
    @Override public boolean onTouchEvent(MotionEvent e){int action=e.getActionMasked(),idx=e.getActionIndex();float x=e.getX(idx),y=e.getY(idx);if(action==MotionEvent.ACTION_DOWN||action==MotionEvent.ACTION_POINTER_DOWN){int id=e.getPointerId(idx);if(liftRect.contains(x,y)){state.lift=true;activePointer=id;}else if(leftRect.contains(x,y)){state.turnLeft=true;activePointer=id;}else if(rightRect.contains(x,y)){state.turnRight=true;activePointer=id;}else if(resetRect.contains(x,y)){state.reset=true;activePointer=id;}else if(x<getWidth()*.48f&&y>getHeight()*.48f){activePointer=id;updateJoy(x,y);}return true;}if(action==MotionEvent.ACTION_MOVE){for(int i=0;i<e.getPointerCount();i++)if(e.getPointerId(i)==activePointer)updateJoy(e.getX(i),e.getY(i));return true;}if(action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_POINTER_UP||action==MotionEvent.ACTION_CANCEL){int id=e.getPointerId(idx);if(id==activePointer){state.lift=false;state.turnLeft=false;state.turnRight=false;state.steer=0;joyX=joyY=0;activePointer=-1;invalidate();}return true;}return true;}
    private void updateJoy(float x,float y){float r=Math.min(78*density,getWidth()*.22f);joyX=Math.max(-1,Math.min(1,(x-cx)/(r*.8f)));joyY=Math.max(-1,Math.min(1,(y-cy)/(r*.8f)));state.steer=joyX;invalidate();}
}
