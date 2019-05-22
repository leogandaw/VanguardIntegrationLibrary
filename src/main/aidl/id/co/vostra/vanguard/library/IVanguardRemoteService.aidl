// IVanguardRemoteService.aidl
package id.co.vostra.vanguard.library;

// Declare any non-default types here with import statements

interface IVanguardRemoteService {
    String echo(String name);

    boolean report(String pkg,String log);
    boolean reportLocation(String label);
}
