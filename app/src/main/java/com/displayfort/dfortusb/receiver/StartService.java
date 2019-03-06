package com.displayfort.dfortusb.receiver;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.displayfort.dfortusb.PlayAdsFromUsbActivity;
import com.displayfort.dfortusb.R;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.ffmpeg4android.ProgressCalculator;
import com.netcompss.loader.LoadJNI;

import java.io.File;
import java.net.URLConnection;

import tv.danmaku.ijk.media.player.ffmpeg.FFmpegApi;

/**
 * Created by Husain on 14-03-2016.
 */
public class StartService extends Service {
    private String workFolder = null;
    private String demoVideoFolder = null;
    private String demoVideoPath = null;
    private String vkLogPath = null;
    private Context context;
    private File[] completFileList;
    private int i = -1;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        demoVideoFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + Prefs.FOLDER;
        demoVideoPath = demoVideoFolder + "in.mp4";

        Log.i(Prefs.TAG, getString(R.string.app_name) + " version: " + GeneralUtils.getVersionName(getApplicationContext()));
        workFolder = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
        //Log.i(Prefs.TAG, "workFolder: " + workFolder);
        vkLogPath = workFolder + "vk.log";
        int rc = GeneralUtils.isLicenseValid(getApplicationContext(), workFolder);
        Log.i(Prefs.TAG, "License check RC: " + rc);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        if (intent != null) {
            String FilePath = intent.getStringExtra("FILEPATH");
            File mainFile = new File(FilePath);
            completFileList = mainFile.listFiles();
            i = -1;
            StartProcess();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void StartProcess() {
        if (completFileList != null && completFileList.length > i) {
            Log.i(Prefs.TAG, "StartProcess check RC: ");
            File file = completFileList[++i];
            File transposeFile = new File(demoVideoFolder + file.getName());
            try {
                Log.d(Prefs.TAG, "FILE NAME:" + file.getName());
                String type = URLConnection.guessContentTypeFromName(file.getName());
                if (type != null && type.toLowerCase().contains("video") && !transposeFile.exists()) {
                    new TranscdingBackground(file).execute();
                } else {
                    StartProcess();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(Prefs.TAG, "StartProcess END ");
        }
    }


    public class TranscdingBackground extends AsyncTask<String, Integer, Integer> {
        File file;

        public TranscdingBackground(File _file) {
            file = _file;
        }


        @Override
        protected void onPreExecute() {


        }

        protected Integer doInBackground(String... paths) {
            try {
                runThreadForProgress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            PowerManager powerManager = (PowerManager) getSystemService(Activity.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
            Log.d(Prefs.TAG, "Acquire wake lock");
            wakeLock.acquire();
            //getApplicationInfo().dataDir
            File transposeFile = new File(demoVideoFolder + file.getName());
            PlayAdsFromUsbActivity.hashSet.add(transposeFile.getAbsolutePath());
            Log.i(Prefs.TAG, file.getAbsolutePath() + "\n" + transposeFile.getAbsolutePath());
            String commandStr8 = "ffmpeg -y -i " + file.getAbsolutePath().replace(" ", "%20") + " -strict experimental -vf transpose=2 -aspect 16:9 " + transposeFile.getAbsolutePath().replace(" ", "%20"); //-s frame rate
            LoadJNI vk = new LoadJNI();
            try {
                /*  /data/user/0/com.displayfort.dfortusb/blue_ball_v.mp4*/
                Log.i(Prefs.TAG, "=======running eight command=========");
                vk.run(GeneralUtils.utilConvertToComplex(commandStr8), workFolder, getApplicationContext());
                GeneralUtils.copyFileToFolder(vkLogPath, demoVideoFolder);
                Log.i(Prefs.TAG, "=======running FINISH=========");
            } catch (Throwable e) {
                Log.e(Prefs.TAG, "vk run exeption.", e);
                if (transposeFile.exists()) {
                    transposeFile.delete();
                }
            } finally {
                if (wakeLock.isHeld())
                    wakeLock.release();
                else {
                    Log.i(Prefs.TAG, "Wake lock is already released, doing nothing");
                }
            }
            PlayAdsFromUsbActivity.hashSet.remove(transposeFile.getAbsolutePath());
            Log.i(Prefs.TAG, "doInBackground finished");
            return Integer.valueOf(0);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected void onCancelled() {
            Log.i(Prefs.TAG, "onCancelled");
            //progressDialog.dismiss();
            super.onCancelled();
        }


        @Override
        protected void onPostExecute(Integer result) {
            Log.i(Prefs.TAG, "onPostExecute");
            super.onPostExecute(result);
            // finished Toast
            String rc = GeneralUtils.getReturnCodeFromLog(vkLogPath);
            final String status = rc;
            Log.i(Prefs.TAG, " onPostExecute  rc" + rc);
            StartProcess();


        }

    }

    private void runThreadForProgress() {
        new Thread() {
            ProgressCalculator pc = new ProgressCalculator(vkLogPath);

            public void run() {
                Log.d(Prefs.TAG, "Progress update started");
                int progress = -1;
                try {
                    while (true) {
                        sleep(300);
                        progress = pc.calcProgress();
                        if (progress != 0 && progress < 100) {
                            Log.d(Prefs.TAG, "PROGRESS:" + (progress));

                            Log.i(Prefs.TAG, "setting progress notification: " + progress);
                        } else if (progress == 100) {
                            Log.i(Prefs.TAG, "==== progress is 100, exiting Progress update thread");
                            pc.initCalcParamsForNextInter();
                            break;
                        }
                    }

                } catch (Exception e) {
                    Log.e("threadmessage", e.getMessage());
                }
            }
        }.start();
    }

}