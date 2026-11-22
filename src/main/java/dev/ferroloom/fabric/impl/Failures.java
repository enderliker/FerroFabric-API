package dev.ferroloom.fabric.impl;

import dev.ferroloom.api.FerroApi;

/**
 * Runs one Fabric listener so that a throwing listener is logged and the others still run. The
 * modules' invokers otherwise let an exception reach the bridge, which would drop the whole
 * forwarder and leave every Fabric mod without that event.
 */
public final class Failures {
	private Failures() {
	}

	public static void run(String event, Runnable action) {
		try {
			action.run();
		} catch (Throwable failure) {
			FerroApi.log(event + " listener failed: " + failure);
		}
	}
}