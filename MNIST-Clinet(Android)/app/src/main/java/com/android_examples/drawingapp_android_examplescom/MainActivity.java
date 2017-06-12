package com.android_examples.drawingapp_android_examplescom;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;
    Paint paint;
    public View view;
    Path path2;
    public Bitmap bitmap;
    public Canvas canvas;
    Button button;
    String ImageData;
    String Data;
    JSONObject jsonobj;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout1);
        button = (Button)findViewById(R.id.button);
        view = new SketchSheetView(MainActivity.this);
        paint = new Paint();
        path2 = new Path();
        relativeLayout.addView(view, new LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        paint.setDither(true);
        paint.setColor(Color.parseColor("#000000"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(60);

        button.setOnClickListener(new View.OnClickListener() {
            ImageView img = (ImageView) findViewById(R.id.img);
            @Override
            public void onClick(View v) {
                Bitmap screenshot;
                view.setDrawingCacheEnabled(true);
                screenshot = Bitmap.createBitmap(view.getDrawingCache());
                view.setDrawingCacheEnabled(false);

                sendData senddata = new sendData();
                ImageData =  BitmapToString(screenshot);
                try {
                    URLEncoder.encode(ImageData,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                senddata.picture = ImageData;

                try {
                    Data = senddata.execute(null,null,null).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                try {
                    jsonobj = new JSONObject(Data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    number = jsonobj.getString("value");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch(number){
                    case "0": img.setBackgroundResource(R.drawable.zero);
                        break;
                    case "1": img.setBackgroundResource(R.drawable.one);
                        break;
                    case "2": img.setBackgroundResource(R.drawable.two);
                        break;
                    case "3": img.setBackgroundResource(R.drawable.three);
                        break;
                    case "4": img.setBackgroundResource(R.drawable.four);
                        break;
                    case "5": img.setBackgroundResource(R.drawable.five);
                        break;
                    case "6": img.setBackgroundResource(R.drawable.six);
                        break;
                    case "7": img.setBackgroundResource(R.drawable.seven);
                        break;
                    case "8": img.setBackgroundResource(R.drawable.eight);
                        break;
                    case "9": img.setBackgroundResource(R.drawable.nine);
                        break;
                    default:
                        break;
                }
                path2.reset();
                ImageData = "";
            }
        });
    }

    class SketchSheetView extends View {

        public SketchSheetView(Context context) {
            super(context);
            bitmap = Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_4444);
            canvas = new Canvas(bitmap);
            this.setBackgroundColor(Color.WHITE);
        }

        private ArrayList<DrawingClass> DrawingClassArrayList = new ArrayList<DrawingClass>();

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            DrawingClass pathWithPaint = new DrawingClass();
            canvas.drawPath(path2, paint);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                path2.moveTo(event.getX(), event.getY());
                path2.lineTo(event.getX(), event.getY());
            }
            else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                path2.lineTo(event.getX(), event.getY());
                pathWithPaint.setPath(path2);
                pathWithPaint.setPaint(paint);
                DrawingClassArrayList.add(pathWithPaint);
            }
            invalidate();
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (DrawingClassArrayList.size() > 0) {

                canvas.drawPath(
                        DrawingClassArrayList.get(DrawingClassArrayList.size() - 1).getPath(),

                        DrawingClassArrayList.get(DrawingClassArrayList.size() - 1).getPaint());
            }
        }
    }

    public class DrawingClass {
        Path DrawingClassPath;
        Paint DrawingClassPaint;
        public Path getPath() {
            return DrawingClassPath;
        }
        public void setPath(Path path) {
            this.DrawingClassPath = path;
        }
        public Paint getPaint() {
            return DrawingClassPaint;
        }
        public void setPaint(Paint paint) {
            this.DrawingClassPaint = paint;
        }
    }

    public static String BitmapToString(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch (NullPointerException e) {
            return null;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }




}