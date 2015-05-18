package co.cheez.cheez.automation.lifecycle;

/**
 * Created by jiho on 5/18/15.
 */
public interface LifecycleObservable {
    void addObserver(LifecycleObserver observer);
    void removeObserver(LifecycleObserver observer);
}
