package com.lesson.dg.tomago4e

import android.content.Context
import android.graphics.*
import android.view.Display
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import com.shooter.game.physics.Material
import com.shooter.game.physics.Word
import com.shooter.game.physics.physicsMath.Vector2
import com.shooter.game.physics.shape.Circle
import com.shooter.game.physics.shape.Polygon



enum class State(val v: Int) {
    GO(0),
    FALLS_ASLEEP(1),
    SLEEP(2),
    WAKE_UP(3)
}

class Ground(context: Context?) : SurfaceView(context), SurfaceHolder.Callback {

    class Player {
        // ball coordinates
        var x : Float = 100.0f
        var y : Float = 500.0f

        var height: Int = 0
        var width : Int = 0

        var body: Bitmap ?= null

        var eye: Bitmap ?= null
        var eyeApple: Bitmap ?= null

        // window size


    }

    var state = State.GO

    var thread: DrawThread?= null
    var scene = Word(1.0f / 60.0f, 10)

    var windowWidth : Int = 0
    var windowHeight : Int = 0

    var a: Bitmap ?= null
    var b: Bitmap ?= null
    var c: Bitmap ?= null
    var d: Bitmap ?= null


    var player = Player()


    var compassDegree = 0f

    init {
        // get references and sizes of the objects
        val display: Display = (getContext().
            getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val size = Point()
        display.getSize(size)



        windowWidth = size.x
        windowHeight = size.y



        player.body = BitmapFactory.decodeResource(resources, R.drawable.ball)
        player.height = player.body!!.height
        player.width = player.body!!.width

        //box
        var boxm = Material(0.01f, 0.6f, 0.3f, 0.4f)

        var poly1 = Polygon()
        poly1.SetBox(windowWidth.toFloat(), 20.0f)
        var b1 = scene.Add(poly1, 0, windowWidth, boxm)
        b1.SetStatic()
        b1.SetOrient(0,0f)
        val conf = Bitmap.Config.ARGB_8888
        a = Bitmap.createBitmap(windowWidth, 20, conf)

        var poly2 = Polygon()
        poly2.SetBox(windowWidth.toFloat(), 20.0f)
        var b2 = scene.Add(poly2, 0, 0, boxm)
        b2.SetStatic()
        b2.SetOrient(0,0f)
        b = Bitmap.createBitmap(windowWidth, 20, conf)

        var poly3 = Polygon()
        poly3.SetBox(20.0f, windowHeight.toFloat())
        var b3 = scene.Add(poly3, 0, 0, boxm)
        b3.SetStatic()
        b3.SetOrient(0,0f)
        c = Bitmap.createBitmap(20, windowHeight, conf)

        var poly4 = Polygon()
        poly4.SetBox(20.0f, windowHeight.toFloat())
        var b4 = scene.Add(poly4, windowWidth, 0, boxm)
        b4.SetStatic()
        b4.SetOrient(0,0f)
        d = Bitmap.createBitmap(20, windowHeight, conf)

        //player

        var r = player.height/2.0f
        var cc = Circle(r)
        cc.texture = player.body
        var m = Material(0.0001f, 0.99f, 0.4f, 0.3f)
        //m.SetRubber()
        var bb = scene.Add(cc, 150, 150, m)

        player.eye = Bitmap.createBitmap(60, 60, conf)
        player.eye?.eraseColor(android.graphics.Color.WHITE)

        player.eyeApple = Bitmap.createBitmap(20, 20, conf)
        player.eyeApple?.eraseColor(android.graphics.Color.RED)

        holder.addCallback(this)
        //create a thread
        createThread()
    }
    fun createThread() {
        thread = DrawThread(holder, this)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        println("CREATE")
        thread!!.setRunning(true)
        thread!!.start()
    }

    var __x1 = 0.0
    var __y1 = 0.0
    var __x2 = 0.0
    var __y2 = 0.0
    var isAngle = true

    var _x1 = 0f
    var _y1 = 0f
    var _x2 = 0f
    var _y2 = 0f

    override fun draw(canvas: Canvas?) {
        if (canvas == null) {
            println("NULL")
            return
        }
        super.draw(canvas)
        if (canvas != null){
            canvas.drawColor(Color.rgb(119, 255, 119))
            canvas.drawBitmap(player.body,player.x-player.height/2.0f,player.y-player.height/2.0f,null)

            var fi = 45.0
            var r = 70.0
            var x = r*Math.cos(Math.toRadians(90+fi))
            var y = r*Math.sin(Math.toRadians(90+fi))
            if (state == State.GO || state == State.FALLS_ASLEEP) {
                _x1 = player.x + x.toFloat()
                _y1 = player.y - y.toFloat()

                canvas.drawBitmap(getCroppedBitmap(player.eye!!), _x1 - 30, _y1 - 30, null)
                x = r * Math.cos(Math.toRadians(-fi+90))
                y = r * Math.sin(Math.toRadians(-fi+90))

                _x2 = player.x + x.toFloat()
                _y2 = player.y - y.toFloat()
                canvas.drawBitmap(getCroppedBitmap(player.eye!!), _x2 - 30, _y2 - 30, null)

                if (isAngle) {
                    fi = compassDegree.toDouble() // change every time
                    r = 12.0
                    __x1 = _x1 + r * Math.cos(Math.toRadians(fi)) - 6
                    __y1 = _y1 - r * Math.sin(Math.toRadians(fi)) - 6
                    __x2 = _x2 + r * Math.cos(Math.toRadians(fi)) - 6
                    __y2 = _y2 - r * Math.sin(Math.toRadians(fi)) - 6
                }
                canvas.drawBitmap(getCroppedBitmap(player.eyeApple!!), __x1.toFloat(), __y1.toFloat(), null)
                canvas.drawBitmap(getCroppedBitmap(player.eyeApple!!), __x2.toFloat(), __y2.toFloat(), null)

            }
            if (state == State.SLEEP || (state == State.WAKE_UP && timerSum < TIME_IN_THE_DARK)) {
                _x1 = player.x + x.toFloat()
                _y1 = player.y - y.toFloat()

                canvas.drawBitmap(getCroppedBitmapRect(player.eye!!), _x1 - 30, _y1 - 30, null)
                x = r * Math.cos(Math.toRadians(-fi+90))
                y = r * Math.sin(Math.toRadians(-fi+90))

                _x2 = player.x + x.toFloat()
                _y2 = player.y - y.toFloat()
                canvas.drawBitmap(getCroppedBitmapRect(player.eye!!), _x2 - 30, _y2 - 30, null)
            }
            if (state == State.WAKE_UP && timerSum > TIME_IN_THE_DARK) {
                if (((timerSum / 100).toInt() % 2) == 0) {
                    _x1 = player.x + x.toFloat()
                    _y1 = player.y - y.toFloat()

                    canvas.drawBitmap(getCroppedBitmap(player.eye!!), _x1 - 30, _y1 - 30, null)
                    x = r * Math.cos(Math.toRadians(-fi+90))
                    y = r * Math.sin(Math.toRadians(-fi+90))

                    _x2 = player.x + x.toFloat()
                    _y2 = player.y - y.toFloat()
                    canvas.drawBitmap(getCroppedBitmap(player.eye!!), _x2 - 30, _y2 - 30, null)

                    if (isAngle) {
                        fi = compassDegree.toDouble() // change every time
                        r = 12.0
                        __x1 = _x1 + r * Math.cos(Math.toRadians(fi+90)) - 6
                        __y1 = _y1 - r * Math.sin(Math.toRadians(fi+90)) - 6
                        __x2 = _x2 + r * Math.cos(Math.toRadians(fi+90)) - 6
                        __y2 = _y2 - r * Math.sin(Math.toRadians(fi+90)) - 6
                    }
                    canvas.drawBitmap(getCroppedBitmap(player.eyeApple!!), __x1.toFloat(), __y1.toFloat(), null)
                    canvas.drawBitmap(getCroppedBitmap(player.eyeApple!!), __x2.toFloat(), __y2.toFloat(), null)


                } else {
                    _x1 = player.x + x.toFloat()
                    _y1 = player.y - y.toFloat()

                    canvas.drawBitmap(getCroppedBitmapRect(player.eye!!), _x1 - 30, _y1 - 30, null)
                    x = r * Math.cos(Math.toRadians(-fi+90))
                    y = r * Math.sin(Math.toRadians(-fi+90))

                    _x2 = player.x + x.toFloat()
                    _y2 = player.y - y.toFloat()
                    canvas.drawBitmap(getCroppedBitmapRect(player.eye!!), _x2 - 30, _y2 - 30, null)
                }
            }

            canvas.drawBitmap(a, 0f, windowWidth.toFloat() - 20f, null)
            a?.eraseColor(android.graphics.Color.rgb(175, 137, 49))//.GREEN)
            canvas.drawBitmap(b, 0f, 0f, null)
            b?.eraseColor(android.graphics.Color.rgb(175, 137, 49))//.RED)
            canvas.drawBitmap(c, 0f, 0f, null)
            c?.eraseColor(android.graphics.Color.rgb(175, 137, 49))//.BLUE)
            canvas.drawBitmap(d, windowWidth.toFloat() - 20f, 0f, null)
            d?.eraseColor(android.graphics.Color.rgb(175, 137, 49))//.YELLOW)
        }
    }

    fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
            (bitmap.width / 2).toFloat(), paint
        )
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }

    fun getCroppedBitmapRect(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawRect(
            0.0f, (bitmap.height / 2).toFloat(),
            (bitmap.width).toFloat(), (bitmap.height / 2).toFloat()+20.0f, paint
        )
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }

    override public fun onDraw(canvas: Canvas?) {
/*
        if (canvas != null){
            canvas.drawColor(0xFFAAAAA)
            canvas.drawBitmap(player.body, player.x, player.y,null)
        }*/
    }
    fun updateCompass(currentDegree: Float) {
        compassDegree = currentDegree
        isAngle = true
    }
    fun updateTouch(x: Float, y: Float) {
        isAngle = false
        var flen = Math.sqrt(((x-_x1)*(x-_x1) + (y-_y1)*(y-_y1)).toDouble())

        val len = 12f
        __x1 = (_x1 + (x - _x1) * (len / flen))
        __y1 = (_y1 + (y - _y1) * (len / flen))

        flen = Math.sqrt(((x-_x2)*(x-_x2) + (y-_y2)*(y-_y2)).toDouble())
        __x2 = (_x2 + (x - _x2) * (len / flen))
        __y2 = (_y2 + (y - _y2) * (len / flen))
    }

    var orient = 0.0f

    fun updateMe(inx : Float , iny : Float){

        scene.Step()
        scene.setGravity(-iny, inx)

        //scene.bodies[scene.bodies.size-1].velocity = Vector2(0f,0f)

        var pos = scene.bodies[scene.bodies.size-1].position+scene.bodies[scene.bodies.size-1].shapes[0].localPos
        player.x = pos.x
        player.y = pos.y

        orient = scene.bodies[scene.bodies.size-1].orient


        invalidate()

    }

    fun updateBox() {
        scene.bodies[0].position = Vector2(0F, windowWidth.toFloat())
        scene.bodies[3].position = Vector2(windowWidth.toFloat(), 0F)
    }


    var timer = System.currentTimeMillis()
    var timerSum = 0.0

    fun stGo(light: Float) {
        if (light < 10) {
            state = State.FALLS_ASLEEP
            timer = System.currentTimeMillis()
            timerSum = 0.0
        } else {
            return
        }
    }


    val TIME_IN_THE_DARK = 3000

    fun stFallAsleep(light: Float) {
        if (light < 10) {
            val now = System.currentTimeMillis()
            timerSum += Math.abs(timer - now)
            timer = now

            if (timerSum >= TIME_IN_THE_DARK) {
                state = State.SLEEP
                timerSum = 0.0
            }

        } else {
            state = State.GO
            timerSum = 0.0
        }
    }

    fun stSleep(light: Float) {
        if (light < 10) {
            return
        } else {
            state = State.WAKE_UP
            timer = System.currentTimeMillis()
            timerSum = 0.0
        }
    }

    fun stWakeUp(light: Float) {
        if (light < 10) {
            state = State.SLEEP
            timerSum = 0.0
        } else {
            val now = System.currentTimeMillis()
            timerSum += Math.abs(timer - now)
            timer = now

            if (timerSum >= TIME_IN_THE_DARK*2) {
                state = State.GO
                timerSum = 0.0
            }
        }
    }

    val states: Map<State, (light : Float)->Unit> = mapOf(
        State.GO to ::stGo,
        State.FALLS_ASLEEP to ::stFallAsleep,
        State.SLEEP to ::stSleep,
        State.WAKE_UP to ::stWakeUp
    )



    fun updateLight(light : Float) {
        states[state]?.invoke(light)

        println(""+state.toString())
    }

    fun setRunning(run : Boolean){
        thread!!.setRunning(run)
    }
}