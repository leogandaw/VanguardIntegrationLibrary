package id.co.vostra.vanguard.library.modules.vanguard;

public interface ServiceConnectionCallback {

    void onServiceConnected();
    void onServiceConnecting();
    void onServiceDisonnected();
}
