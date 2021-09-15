package com.dqc.puppymoney

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dqc.puppymoney.bean.PomSendData
import com.dqc.puppymoney.service.PomodoroService
import com.dqc.puppymoney.ui.activity.BaseActivity
import com.plattysoft.leonids.ParticleSystem
import kotlinx.android.synthetic.main.activity_demo.*
import java.util.*
import kotlin.collections.ArrayList


class DemoActivity : BaseActivity() {

    private lateinit var ps: ParticleSystem
    private var isStop = false
    private var mPomodoroService: PomodoroService? = null

    private var mServideConnect: ServiceConnection = object :ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            var mbinder = service as PomodoroService.PomodoroBinder
            mPomodoroService = mbinder.service
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }

    fun bindService() {
        var mPomIntent = Intent(this, PomodoroService::class.java)

        //退出程序后依然能播放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(mPomIntent)
        } else {
            startService(mPomIntent)
        }

        bindService(mPomIntent, mServideConnect, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()

        //将播放的服务提升至前台服务
        val playIntent = Intent(this, PomodoroService::class.java)
        //Android 8.0以上
        //Android 8.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(playIntent)
        } else {
            startService(playIntent)
        }
    }

    class PomReceiver: BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action.equals("com.dqc.puppymoney")) {
                val data = intent?.getSerializableExtra("pomdata") as PomSendData
                Log.d("onReceiver", " " + data.mCurMills + " " + data.mCurCountDownStatus + " " + data.mMaxMinute + " " + data.mCurMode)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

//        bindService()
//
//        var pomReceivr = PomReceiver()
//        var intentFilter = IntentFilter()
//        intentFilter.addAction("com.dqc.puppymoney")
//        registerReceiver(pomReceivr, intentFilter)
//
//        btn_1.setOnClickListener {
//            mPomodoroService?.startPomodoro()
//        }
//
//        btn_2.setOnClickListener {
//            mPomodoroService?.pausePomodoro()
//
//        }
//
//        btn_3.setOnClickListener {
//            mPomodoroService?.cancelPomodoro()
//
//        }

//        btn.setOnClickListener {
//            isStop = !isStop
//            if (isStop) {
//                w1.stopWava()
//                w2.stopWava()
//                w3.stopWava()
//            } else {
//                w1.startWava()
//                w2.startWava()
//                w3.startWava()
//            }
//
//        }

//
//        Glide.with(this)
//            .load(R.mipmap.night)
//            .bitmapTransform(BlurTransformation(this, 14, 6))
//            .into(blur_bg)
//        Log.d("ASDSADAD", " file ")
//        initPermission()

//        try {
//            var file = File("sdcard/PuppyMoney")
//            Log.d("CCCCC", "!file.exists()" + !file.exists())
//
//            if (!file.exists()) {
//                val mkdir = file.mkdir()
//                Log.d("CCCCC", "file.mkdir()" + mkdir)
//            }
//            val input = FileInputStream("/sdcard/a.jpg") //可替换为任何路径何和文件名
//            val output = FileOutputStream("sdcard/PuppyMoney/a.jpg") //可替换为任何路径何和文件名
//            Log.d("ASDSADAD", " file " + input.toString())
//            var inp = input.read()
//            while (inp != -1) {
//                output.write(inp)
//                Log.d("DDQQCC", " `in` ${inp}")
//                inp = input.read()
//            }
//            val buf = ByteArray(1024)
//            var bytesRead = 0
//            while (input.read(buf).also({ bytesRead = it }) > 0) {
//                output.write(buf, 0, bytesRead)
//            }
//            input.close()
//            output.close()
//        } catch (e: IOException) {
//            Log.d("ASDSADAD", " e " + e.toString())
//        }


//        try {
//            val cw = ContextWrapper(applicationContext)
//            val directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
//
//            var targetFile = File(directory, "123.txt")
//            val rf = RandomAccessFile(targetFile, "rw")
//            rf.seek(targetFile.length())
//            rf.write("123123".toByteArray())
//            rf.close()
//            Log.d("CCCC", " e close")
//
//        } catch (e: Exception) {
//            Log.d("CCCC", " e " + e.toString())
//        }


//        ParticleSystem(this, 1000, R.mipmap.ic_launcher, 3000)
//                .setSpeedModuleAndAngleRange(0.05f, 0.2f, 0, 360)
//                .setRotationSpeed(30f)
//                .setAcceleration(0f, 90).oneShot(btn, 200)
//        btn.setOnClickListener {
//            var mCustomDialog = VerifiedGeneralDialog.Builder(this)
//            mCustomDialog?.setMessage("SADSADASDAS")
//            mCustomDialog?.setTitle("ASDASDASDASD")
//            mCustomDialog?.setPositiveButton("AA", { dialog, which ->
//                dialog.dismiss()
//            })
//            mCustomDialog?.setNegativeButton("BB", { dialog, which ->
//                dialog.dismiss()
//            })
//
//            mCustomDialog?.create().show()
//            VerifiedGeneralDialog.Builder(this)
//                    .setMessage("ASDSADASDa00")
//                    .setTitle("ASDSAFSAD")
//                    .setPositiveButton("21312313",  { dialog, which ->
//                        dialog.dismiss()
//                    }).setNegativeButton("BRG",  { dialog, which ->
//                        dialog.dismiss()
//                    }).create()
//                    .show()
//            for (i in 0..6) {
//                ParticleSystem(this, 16, R.drawable.complete_ic_black, 700)
//                        .setSpeedModuleAndAngleRange(0.10f, 0.10f, 60 * i, 60 * i)
//                        .setRotationSpeed(60f)
//                        .setFadeOut(100)
//                        .emit(applicationContext.resources.displayMetrics.widthPixels / 2, applicationContext.resources.displayMetrics.heightPixels / 2,
//                                1, 1000);
//            }

//            ParticleSystem(this, 500, R.drawable.wish_selected_point_ic, 5000)
//                    .setAcceleration(0.00003f, 270)
//                    .addModifier(ScaleModifier(0f, 1.2f, 1000, 4000))
//                    .setFadeOut(5000)
//                    .setRotationSpeedRange(0f, 180f)
//                    .emit(btn, 50)
//            ParticleSystem(this, 80, R.drawable.wish_selected_point_ic, 10000)
//                    .setSpeedModuleAndAngleRange(0f, 0.3f, 180, 180)
//                    .setRotationSpeed(144f)
//                    .setAcceleration(0.00005f, 90)
//                    .emit(findViewById(R.id.btn), 8);

//            ParticleSystem(this, 100, R.drawable.wish_selected_point_ic, 5000)
//                    .setSpeedRange(0.1f, 0.25f)
//                    .setRotationSpeedRange(90f, 180f)
//                    .setInitialRotationRange(0, 30)
//                    .oneShot(btn, 100);

//            ParticleSystem(this, 4, R.drawable.wish_selected_point_ic, 3000)
//                    .setSpeedByComponentsRange(-0.025f, 0.025f, -0.06f, -0.08f)
//                    .setAcceleration(0.00001f, 30)
//                    .setInitialRotationRange(0, 360)
//                    .addModifier( AlphaModifier(255, 0, 1000, 3000))
//            .addModifier( ScaleModifier(0.5f, 1f, 0, 1000))
//            .oneShot(btn, 4)

//            ParticleSystem(this, 50, R.drawable.wish_selected_point_ic, 1000, R.id.line)
//                    .setSpeedRange(0.1f, 0.25f)
//                    .emit(btn, 100);
//            var ps = ParticleSystem(this, 100, R.drawable.wish_selected_point_ic, 1000);
//            ps.setScaleRange(0.7f, 1.3f);
//            ps.setSpeedModuleAndAngleRange(0.07f, 0.16f, 0, 180);
//            ps.setRotationSpeedRange(90f, 180f);
//            ps.setAcceleration(0.00013f, 90);
//            ps.setFadeOut(200,  AccelerateInterpolator());
//            ps.emit(btn, 100);

//            ParticleSystem(this, 30, R.drawable.wish_selected_point_ic_big, 1000 * 30)
//                    .setAcceleration(0.000013f, 180)
//                    .setSpeedByComponentsRange(0.1f, 0.2f, -0.1f, -0.15f)
//                    .addModifier(AlphaModifier(255, 0, 1000, 1000 * 10))
//                    .addModifier(ScaleModifier(0.5f, 1f, 0, 1000 * 10))
//                    .emit(btn, Gravity.TOP, 30);

//            var ps = ParticleSystem(this, 100, R.drawable.wish_selected_point_ic, 800);
//            ps.setScaleRange(0.7f, 1.3f);
//            ps.setSpeedRange(0.1f, 0.25f);
//            ps.setRotationSpeedRange(90f, 180f);
//            ps.setFadeOut(200, AccelerateInterpolator());
//            ps.oneShot(btn, 70);
//
//            var ps2 = ParticleSystem(this, 100, R.drawable.wish_selected_point_ic, 800);
//            ps2.setScaleRange(0.7f, 1.3f);
//            ps2.setSpeedRange(0.1f, 0.25f);
//            ps.setRotationSpeedRange(90f, 180f);
//            ps2.setFadeOut(200, AccelerateInterpolator());
//            ps2.oneShot(btn, 70);

//            var ps = ParticleSystem(this, 100, R.drawable.wish_selected_point_ic, 800);
//            ps.setScaleRange(0.7f, 1.3f);
//            ps.setSpeedRange(0.1f, 0.25f);
//            ps.setAcceleration(0.0001f, 90);
//            ps.setRotationSpeedRange(90f, 180f);
//            ps.setFadeOut(200, AccelerateInterpolator());
//            ps.oneShot(btn, 100)

//            ParticleSystem(this, 100, R.drawable.wish_selected_point_ic, 800)
//                    .setSpeedRange(0.1f, 0.25f)
//                    .oneShot(btn, 100);


//            ParticleSystem(this, 10, R.drawable.wish_selected_point_ic, 3000)
//                    .setSpeedByComponentsRange(-0.1f, 0.1f, -0.1f, 0.02f)
//                    .setAcceleration(0.000003f, 90)
//                    .setInitialRotationRange(0, 360)
//                    .setRotationSpeed(120f)
//                    .setFadeOut(2000)
//                    .addModifier(ScaleModifier(0f, 1.5f, 0, 1500))
//            .oneShot(btn, 10)

//            startActivity(Intent(this, BActivity::class.java))

    }



//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        when(event?.action) {
//            MotionEvent.ACTION_DOWN-> {
//                ps = ParticleSystem(this, 100, R.drawable.wish_selected_point_ic, 800);
//                ps.setScaleRange(0.7f, 1.3f);
//                ps.setSpeedRange(0.05f, 0.1f);
//                ps.setRotationSpeedRange(90f, 180f);
//                ps.setFadeOut(200,  AccelerateInterpolator());
//                ps.emit(event.getX().toInt(), event.getY().toInt(), 40);
//            }
//            MotionEvent.ACTION_MOVE-> {
//                ps.updateEmitPoint( event.getX().toInt(), event.getY().toInt());
//            }
//            MotionEvent.ACTION_UP -> {
//                ps.stopEmitting();
//            }
//
//        }
//        return super.onTouchEvent(event)
//    }


    private fun initPermission() {
        var permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)

        var toApplyList = ArrayList<String>()

        for (i in 0..permission.size - 1) {
            if (PackageManager.PERMISSION_GRANTED !=
                    ContextCompat.checkSelfPermission(Objects.requireNonNull(this), permission[i])) {
                toApplyList.add(permission[i])
            }
        }
        val arrayOfNulls = arrayOfNulls<String>(permission.size)
        ActivityCompat.requestPermissions(this, toApplyList.toArray(arrayOfNulls), 1)
    }

    override fun onResume() {
        super.onResume()
//        blur_bg.setImageResource(R.mipmap.night)
//        DisplayUtil.makeBlur(blur_bg, this)
    }
//
//    public  void copyFile(String oldPathFile, String newPathFile) {
//        int byteread = 0;
//
//        int byteCount = 0;
//
//        File oldfile = new File(oldPathFile);
//
////用spilit方法实际是在用正则表达式进行匹配，java中//由于转义符代表为/，所以实际为//，又在正则表达式中//代表/所以表示/
//
////而在不需要正则表达式的方法如String的replace则可以用//表示/这点需要注意
//
//        String[] fileName = oldPathFile.split("");
//
//        String name = fileName[fileName.length - 1];
//
//        boolean isSuccessful = true;
//
//        if (oldfile.exists()) { // 文件存在时
//
//            try {
//                InputStream inStream = new FileInputStream(oldPathFile); // 读入原文件
//
//                BufferedInputStream bufferedInputStream = new BufferedInputStream(inStream);
//
////复制到的文件必须是具体的文件不能是文件夹，如果为文件夹则出现拒绝访问的异常，所以加上name
//
//                FileOutputStream fs = new FileOutputStream(newPathFile + "//" + name);
//
//                BufferedOutputStream bufferedOutPutStream = new BufferedOutputStream(fs);
//
//                byte[] buffer = new byte[1024 * 5];
//
//                while ((byteread = bufferedInputStream.read(buffer)) != -1) {
//                    byteCount += byteread;
//
//                    bufferedOutPutStream.write(buffer, 0, byteread);
//
//                }
//
//                inStream.close();
//
//                bufferedInputStream.close();
//
//                fs.close();
//
//                bufferedOutPutStream.close();
//
//            } catch (IOException e) {
//                System.out.println("读取文件出错！原因" + e.getMessage());
//
//                isSuccessful = false;
//
//            }
//
//        } else {
//            System.out.println("地址" + oldPathFile + "不存在文件");
//
//            isSuccessful = false;
//
//        }
//
//        if (isSuccessful)
//
//            System.out.println("复制成功！共复制" + byteCount + "字节");
//
//    }
//
}