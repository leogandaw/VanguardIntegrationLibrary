package id.co.vostra.vanguard.library.modules.vanguard;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import id.co.vostra.vanguard.library.IVanguardRemoteService;
import id.co.vostra.vanguard.library.modules.logcat.Logcat;

import static android.content.ContentValues.TAG;

public class Vanguard {

    private Context context;
    @SuppressLint("StaticFieldLeak")
    private static volatile Vanguard vanguardInstance;
    private ServiceConnection serviceConnection;
    private IVanguardRemoteService service;
    private boolean isBound =false;
    private final int MAX_WAIT_RETRY = 10;


    private Vanguard(){
        if(vanguardInstance!=null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static Vanguard getInstance(){
        if(vanguardInstance==null){
            synchronized (Vanguard.class){
                if(vanguardInstance==null) {
                    vanguardInstance = new Vanguard();
                }

            }
        }
        return vanguardInstance;
    }

//    public static void init(Context context) {
//        Vanguard.getInstance().initialize(context);
//    }

    public boolean isConnected(){
        return isBound;
    }

    public void connect(Context context, final ServiceConnectionCallback serviceConnectionCallback) {
        this.context = context;

        startServiceHost();
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i(TAG, "Service connected to vanguard");
                isBound = true;
                service = IVanguardRemoteService.Stub.asInterface((IBinder) iBinder);
                serviceConnectionCallback.onServiceConnected();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isBound = false;
                serviceConnectionCallback.onServiceDisonnected();
                Log.i(TAG, "Service disconnected from vanguard");
            }
        };

        Intent i = new Intent("id.co.vostra.vanguard.ext.modules.services.ForegroundService");
        i.setPackage("id.co.vostra.vanguard.ext");
        context.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        serviceConnectionCallback.onServiceConnecting();

    }

    private void startServiceHost(){
        final Intent intent=new Intent();
        intent.setAction("id.co.vostra.vanguard.ext.intent.action.RESTART_SERVICE");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setComponent(
                new ComponentName("id.co.vostra.vanguard.ext","id.co.vostra.vanguard.ext.modules.boot.BootReceiver"));
        context.sendBroadcast(intent);
    }

    @SuppressLint("StaticFieldLeak")
    public void reportLog(){

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.i(TAG, "reportLog");
                int retry = 0;
                while (!isBound && retry<MAX_WAIT_RETRY){
                    try {
                        Thread.sleep(3000);
                        retry++;
                        Log.i(TAG, "Waiting connection to Vanguard service..");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (service != null) {
                    try {
                        if(service.report(context.getPackageName(),Logcat.getLog())){
                            Log.i(TAG, "reportLog success");
                        }else{
                            Log.i(TAG, "reportLog failed");
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();

    }

    public boolean sendLocation(String label){

        if(!isBound){
            throw new RuntimeException("Service is not connected to Vanguard");
        }else{
            try {
                return service.reportLocation(label);
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }

    }

    public boolean isComply(){

        if(!isBound){
            throw new RuntimeException("Service is not connected to Vanguard");
        }else{
            try {
                return service.isComply();
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }


    }



}
