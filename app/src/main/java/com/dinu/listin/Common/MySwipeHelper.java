package com.dinu.listin.Common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.dinu.listin.CallBack.MyButtonClickListner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class MySwipeHelper extends ItemTouchHelper.SimpleCallback {

    int buttonWidth;
    private RecyclerView recyclerView;
    private List<MyButton> buttonList;
    private GestureDetector gestureDetector;
    private int swipePsition = -1;
    private float swipeTreshold=0.5f;
    private Map<Integer,List<MyButton>> buttonBuffer;
    private Queue<Integer> removeQueue;

    private GestureDetector.SimpleOnGestureListener gestureListener=new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (MyButton button : buttonList)
                if (button.onClick(e.getX(), e.getY()))
                    break;
            return true;
        }
    };

    private View.OnTouchListener onTouchListener= new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (swipePsition<0) return false;
            Point point=new Point((int)event.getRawX(),(int)event.getRawY());
            RecyclerView.ViewHolder swipeViewHolder=recyclerView.findViewHolderForAdapterPosition(swipePsition);
            View swipedItem=swipeViewHolder.itemView;
            Rect rect=new Rect();
            swipedItem.getGlobalVisibleRect(rect);

            if(event.getAction() == MotionEvent.ACTION_DOWN ||
                    event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction()== MotionEvent.ACTION_MOVE){

                if(rect.top < point.y && rect.bottom > point.y){
                    gestureDetector.onTouchEvent(event);

                }else{
                    removeQueue.add(swipePsition);
                    swipePsition=-1;
                }

            }
            return false;
        }

    };

    public MySwipeHelper(Context context,RecyclerView recyclerView,int buttonWidth) {
        super(0, ItemTouchHelper.LEFT);
        this.recyclerView = recyclerView;
        this.buttonList = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        this.buttonBuffer = new HashMap<>();
        this.buttonWidth = buttonWidth;

        removeQueue = new LinkedList<Integer>(){
            @Override
            public boolean add(Integer integer){
                if (contains(integer))
                    return false;
                else
                    return super.add(integer);
            }
    };
        attachedSwipe();
    }

    private void attachedSwipe() {
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private synchronized void recoverSwipedItem(){
        while (! removeQueue.isEmpty()){
            int pos=removeQueue.poll();
            if (pos > -1){
                recyclerView.getAdapter().notifyItemChanged(pos);
            }

        }
    }


    public class  MyButton{
         private String text;
         private int imageResid,txtSize,color,pos;
         private RectF clickregion;
         private MyButtonClickListner listner;
         private Context context;
         private Resources resources;

         public MyButton(Context context,String text, int txtSize, int imageResid, int color, MyButtonClickListner listner) {
             this.text = text;
             this.imageResid = imageResid;
             this.txtSize = txtSize;
             this.color = color;
             this.listner = listner;
             this.context = context;
         }

         public boolean onClick(float x, float y){
             if (clickregion != null && clickregion.contains(x,y)){
                 listner.onClick(pos);
                 return true;
             }
                return false;
         }

         public void onDraw(Canvas canvas,RectF rectF,int pos){
             Paint paint=new Paint();
             paint.setColor(color);
             canvas.drawRect(rectF,paint);

             //TEXT
             paint.setColor(Color.WHITE);
             paint.setTextSize(txtSize);

             Rect r=new Rect();
             Float cHeight=rectF.height();
             Float cWidth=rectF.width();
             paint.setTextAlign(Paint.Align.LEFT);
             paint.getTextBounds(text,0,text.length(),r);
             float x=0,y=0;

             if (imageResid==0){//If just show text
                 x=cWidth/2f-r.width()/2f-r.left;
                 y=cHeight/2f+r.height()/2f-r.bottom;
                 canvas.drawText(text,rectF.left+x,rectF.top+y,paint);
             }
             else {//if have image resource
                 Drawable d= ContextCompat.getDrawable(context,imageResid);
                 Bitmap bitmap=DrwableToBitMap(d);
                 canvas.drawBitmap(bitmap,(rectF.left+rectF.right)/2,(rectF.top+rectF.bottom)/2,paint);
             }
             clickregion=rectF;
             this.pos=pos;

         }

     }

    private Bitmap DrwableToBitMap(Drawable d) {
         if(d instanceof BitmapDrawable)
            return ((BitmapDrawable)d).getBitmap();
            Bitmap bitmap=Bitmap.createBitmap(d.getIntrinsicWidth(),d.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
            Canvas canvas=new Canvas(bitmap);
            d.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
            d.draw(canvas);
            return bitmap;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int pos=viewHolder.getAdapterPosition();
        if (swipePsition!= pos){
            removeQueue.add(swipePsition);
            swipePsition = pos;
            if(buttonBuffer.containsKey(swipePsition))
                buttonList=buttonBuffer.get(swipePsition);
            else
                buttonList.clear();
                buttonBuffer.clear();
                swipeTreshold=0.5f * buttonList.size() *buttonWidth;
                recoverSwipedItem();
        }

    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return swipeTreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 0.1f*defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 0.5f*defaultValue;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int pos=viewHolder.getAdapterPosition();
        float tranlationX=dX;
        View itemView = viewHolder.itemView;
        if (pos < 0){
            swipePsition = pos;
            return;
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            if (dX < 0){
                List<MyButton> buffer=new ArrayList<>();

                if (!buttonBuffer.containsKey(pos)){
                    initiateMyButton(viewHolder,buffer);
                    buttonBuffer.put(pos,buffer);
                }else{
                    buffer=buttonBuffer.get(pos);
                }
                tranlationX = dX*buffer.size()*buttonWidth/itemView.getWidth();
                drawButton(c,itemView,buffer,pos,tranlationX);
            }
        }
        super.onChildDraw(c,recyclerView,viewHolder,tranlationX,dY,actionState,isCurrentlyActive);
    }

    public abstract void initiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf);

    private void drawButton(Canvas c, View itemView, List<MyButton> buffer, int pos, float tranlationX) {
            float right=itemView.getRight();
            float dButtonWidth=-1*tranlationX/buffer.size();
            for (MyButton button : buffer)
            {
                float left= right-dButtonWidth;
                button.onDraw(c,new RectF( left,itemView.getTop(),right,itemView.getBottom()),pos);
                right=left;
            }
    }
}
