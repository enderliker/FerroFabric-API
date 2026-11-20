package net.fabricmc.fabric.api.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * A Fabric API event: listeners register against it and the owning module dispatches to them.
 *
 * <p>FerroLoom's minimal implementation keeps the public shape a mod compiled against Fabric API
 * uses ({@code register}), and adds {@link #dispatch(Consumer)} for the module that fires it. A
 * failing listener is logged and the rest still run, so one broken mod cannot stop the others.
 */
public abstract class Event<T> {
    private final List<T> listeners = new CopyOnWriteArrayList<>();

    public void register(T listener) {
        if (listener != null) listeners.add(listener);
    }

    /** Calls {@code invocation} once per listener, in registration order. */
    public final void dispatch(Consumer<T> invocation) {
        for (T listener : List.copyOf(listeners)) {
            try {
                invocation.accept(listener);
            } catch (Throwable failure) {
                System.err.println("[ferrofabric] event listener failed: " + failure);
            }
        }
    }
}