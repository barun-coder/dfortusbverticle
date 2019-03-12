package com.displayfort.dfortusb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.displayfort.dfortusb.receiver.StartService;
import com.displayfort.dfortusb.widgets.FullScreenVideoView;
import com.netcompss.ffmpeg4android.Prefs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;


/**
 * Created by pc on 09/01/2019 10:59.
 * MyApplication
 */
public class PlayAdsFromUsbActivity extends BaseSupportActivity implements SurfaceHolder.Callback {
    private String TAG = "USBCatch";

    String macId;
    private ImageView mDefaultIV;
    private ImageView displayImageView;
    private File[] completFileList;
    private int currentAdvertisementNo = 0;
    private int Orientation = ExifInterface.ORIENTATION_NORMAL;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFERENCE_NAME = "DFADSPreference";
    //    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;
    private FullScreenVideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_main);
        this.sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Orientation = ExifInterface.ORIENTATION_UNDEFINED;
        init();
//        SHowMNT();
    }

    private void init() {
//        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
//        mSurfaceHolder = mSurfaceView.getHolder();
//        mSurfaceHolder.addCallback(this);
//        mSurfaceView.setVisibility(View.INVISIBLE);
        mDefaultIV = (ImageView) findViewById(R.id.default_iv);
        displayImageView = (ImageView) findViewById(R.id.imageView2);
        videoView = findViewById(R.id.videoView);
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(Uri.parse("/storage/emulated/0/ads/nissan.mp4"));
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("VIDEPATH", "onCompletion");
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d("VIDEPATH", "onError");
                return false;
            }
        });
    }

    ///data/user/0/displayfort.nirmit.com.myapplication/files
    private void SHowMNT() {
        showLog("FILEPATH", "\n\n");
        File[] fileList;
        File file = new File("mnt");
        if (file.exists()) {
            showLog("FILEPATH-MNT", "mnt exist");
            fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                showLog("FILEPATH-MNT", i + ":-" + fileList[i].getAbsolutePath());
            }
            showLog("FILEPATH", "MNT over \n");
            File file1 = new File(getApplicationContext().getFilesDir().getPath());
            if (!isNotMobile()) {
                if (Orientation == ExifInterface.ORIENTATION_UNDEFINED) {
                    file1 = new File(Environment.getExternalStorageDirectory() + File.separator + "ads");
                } else {
                    file1 = new File(getApplicationContext().getFilesDir().getPath().replace("files", "img"));
                }
                if (file1.exists()) {
                    showLog("FILEPATH-MNT", file1 + " mnt/usb exist");
                    startFIlePlay(file1);
                }
            } else {
                file1 = new File(file.getAbsoluteFile() + File.separator + "usb");
                fileList = file1.listFiles();
                if (fileList != null && fileList.length > 0) {
                    for (int i = 0; i < fileList.length; i++) {
                        showLog("FILEPATH-MNT", i + ":--" + fileList[i].getAbsolutePath());
                        if (fileList[i].exists()) {
                            startFIlePlay(fileList[i]);
                            return;
                        }
                    }

                } else {
                    file1 = new File("storage");
                    fileList = file1.listFiles();
                    if (fileList != null && fileList.length > 0) {
                        for (int i = 0; i < fileList.length; i++) {
                            if (!fileList[i].getAbsolutePath().contains("emulated") && !fileList[i].getAbsolutePath().contains("self")) {
                                if (fileList[i].exists()) {
                                    startFIlePlay(fileList[i]);
                                    return;
                                }
                            }
                        }
                    }
                }
            }

        }
    }


    ///storage/emulated/0/ads
    private void startFIlePlay(File file) {
        filterName(file);
        completFileList = file.listFiles();
        if (completFileList != null && completFileList.length > 0) {
            Intent intent = new Intent(this, StartService.class);
            intent.putExtra("FILEPATH", file.getAbsolutePath());
            startService(intent);
            showLog("FILEPATH", completFileList.length + " completFileList \n");
            currentAdvertisementNo = 0;
            showCurrentAdvertisements();
        }
    }

    private void filterName(File file) {
        completFileList = file.listFiles();
        if (completFileList != null && completFileList.length > 0) {
            for (File FronFile : completFileList) {
                if (FronFile.getAbsolutePath().contains(" ")) {
                    File from = new File(file, FronFile.getName());
                    File to = new File(file, FronFile.getName().replace(" ", "_"));
                    boolean isRename = from.renameTo(to);
                    showLog("FILEPATH", to.getAbsolutePath() + ":" + isRename);
                }
            }
        }
    }


    private void showCurrentAdvertisements() {
        showCurrentAd();

    }

    private void showCurrentAd() {
        int interval = -1;
        try {
            setTheme(R.style.AppTheme);
            final File file = completFileList[currentAdvertisementNo];
            showLog("FILEPATH", "Run File " + file.getAbsolutePath() + "\n");
            String type = URLConnection.guessContentTypeFromName(file.getName());
            if (type != null) {
                if (type.toLowerCase().contains("gif")) {
                    videoView.setVisibility(View.INVISIBLE);
                    displayImageView.setVisibility(View.VISIBLE);
                    String photoPath = file.getAbsolutePath();
                    Glide.with(this).load(changeOrientation(photoPath)).into(displayImageView);
                    Log.d("ADVERTISEMENT", photoPath.toString() + "");
                    interval = 5000;
                } else if (type.toLowerCase().contains("image")) {
                    videoView.setVisibility(View.INVISIBLE);
                    displayImageView.setVisibility(View.VISIBLE);
                    //displayImageView
                    String photoPath = file.getAbsolutePath();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    final Bitmap b = changeOrientation(photoPath);// BitmapFactory.decodeFile(photoPath, options);
                    displayImageView.setImageBitmap(b);
                    Log.d("ADVERTISEMENT", photoPath.toString() + "");
                    interval = 5000;
                } else if (type.toLowerCase().contains("video")) {
                    displayImageView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.INVISIBLE);
                    File newfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Prefs.FOLDER + file.getName());// new File(getApplicationInfo().dataDir + File.separator + file.getName());
                    boolean isFileFound = newfile.exists();
                    if (!isFileFound) {
                        Log.d(" VIDEPATH", "isFileFound" + isFileFound);
                        interval = -1; // 1 Second
                    } else {
                        Log.d("VIDEPATH", "isFileFound" + isFileFound);
                        if (!PlayAdsFromUsbActivity.hashSet.contains(newfile.getAbsolutePath())) {
                            Log.d("VIDEPATH", "PlayAdsFromUsbActivity.hashSet" + true);
                            videoView.setVisibility(View.VISIBLE);
                            String photoPath = newfile.getAbsolutePath();
                            interval = getMiliseconds(newfile);
                            if (interval != 0) {
                                Log.i("PostActivity", "Video List is " + getFilesDir().getPath());
                                Uri myUri = Uri.parse(photoPath); // initialize Uri here


                                String url = photoPath;
                                try {
//                                    if (mMediaPlayer == null) {
//                                        mMediaPlayer = new MediaPlayer();
//                                        mMediaPlayer.setDisplay(mSurfaceView.getHolder());
//                                    } else {
//                                        mMediaPlayer.release();
//
//                                    }
                                    videoView.setVideoURI(Uri.parse(photoPath));
                                    videoView.start();
                                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mp) {
                                            Log.d("VIDEPATH", "onCompletion");
                                        }
                                    });

                                    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                        @Override
                                        public boolean onError(MediaPlayer mp, int what, int extra) {
                                            Log.d("VIDEPATH", "onError");
                                            return false;
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                interval = -1;
                            }
                        } else {
                            Log.d("VIDEPATH", "PlayAdsFromUsbActivity.hashSet" + false);
                            interval = -1;
                        }
                    }
                    // 1 Second
                }
            }
            if (interval != 0) {
                Log.d("interval", "interval  :  " + interval + " " + file.getName());
                Handler handler = new Handler();
                // final MainActivity MyActivity=this;
                Runnable runnable = new Runnable() {
                    public void run() {
                        /* hit api on stop*/
                        showCurrentAd();
                    }
                };
                handler.postDelayed(runnable, interval);
                if (currentAdvertisementNo < (completFileList.length - 1)) {
                    currentAdvertisementNo++;
                } else {
                    currentAdvertisementNo = 0;
                }
            }
        } catch (Exception e) {
            Log.e("Handeledexception", "E: " + e.getMessage());
            videoView.setVisibility(View.INVISIBLE);
            displayImageView.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            if (currentAdvertisementNo < (completFileList.length - 1)) {
                currentAdvertisementNo++;
            } else {
                currentAdvertisementNo = 0;
            }
            Runnable runnable = new Runnable() {
                public void run() {
                    /* hit api on stop*/
                    showCurrentAd();
                }
            };
            handler.post(runnable);

        }
    }


    public Bitmap changeOrientation(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream outputStream = null;
        int orientation = ExifInterface.ORIENTATION_ROTATE_270;
        Bitmap imageRotate = rotateBitmap(bitmap, orientation);

        return imageRotate;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }


    private int getMiliseconds(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(this, Uri.fromFile(file));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        return (int) timeInMillisec;
    }

    private boolean isNotMobile() {
        Display display = ((Activity) this).getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        float widthInches = metrics.widthPixels / metrics.xdpi;
        float heightInches = metrics.heightPixels / metrics.ydpi;
        double diagonalInches = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));
        return diagonalInches >= 7.0;
    }

    private void showLog(String tag, String deviceName) {
        Log.d(TAG, tag + ":" + deviceName);
//        Toast.makeText(this, "" + deviceName, Toast.LENGTH_SHORT).show();
//        try {
//            textView.append(deviceName + "\n");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onPause() {
        onFinishAPP();
        super.onPause();


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void onFinishAPP() {
        try {
            if (videoView != null && videoView.isPlaying()) {
                videoView.stopPlayback();
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        onFinishAPP();
        super.onStop();

    }

    @Override
    public void onDestroy() {
        onFinishAPP();
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer = new MediaPlayer();
        mSurfaceHolder = holder;
        mMediaPlayer.setDisplay(holder);
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                mMediaPlayer.setDataSource("/sdcard/videokit/out1.mp4");
//            } else {
//            }
//            mMediaPlayer.setLooping(true);
//            mMediaPlayer.prepare();
//            mMediaPlayer.setOnPreparedListener(this);
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                SHowMNT();
            }
        }
    }


}
