package dev.ferroloom.fabric.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.resources.Identifier;

/** The ordered elements FerroLoom draws in its HUD layer. */
public final class HudElementRegistryImpl {
	private static final List<Identifier> ORDER = new CopyOnWriteArrayList<>();
	private static final Map<Identifier, HudElement> ELEMENTS = new ConcurrentHashMap<>();

	private HudElementRegistryImpl() {
	}

	private static synchronized void insert(int index, Identifier id, HudElement element) {
		ORDER.remove(id);
		int at = Math.max(0, Math.min(index, ORDER.size()));
		ORDER.add(at, id);
		ELEMENTS.put(id, element);
	}

	public static synchronized void addFirst(Identifier id, HudElement element) {
		insert(0, id, element);
	}

	public static synchronized void addLast(Identifier id, HudElement element) {
		insert(ORDER.size(), id, element);
	}

	public static synchronized void attachElementBefore(Identifier beforeThis, Identifier id,
			HudElement element) {
		int at = ORDER.indexOf(beforeThis);
		insert(at < 0 ? 0 : at, id, element);
	}

	public static synchronized void attachElementAfter(Identifier afterThis, Identifier id,
			HudElement element) {
		int at = ORDER.indexOf(afterThis);
		insert(at < 0 ? ORDER.size() : at + 1, id, element);
	}

	public static synchronized void removeElement(Identifier id) {
		ORDER.remove(id);
		ELEMENTS.remove(id);
	}

	public static synchronized void replaceElement(Identifier id,
			Function<HudElement, HudElement> replacer) {
		HudElement old = ELEMENTS.get(id);
		if (old != null) {
			ELEMENTS.put(id, replacer.apply(old));
		}
	}

	public static boolean hasElements() {
		return !ORDER.isEmpty();
	}

	public static List<HudElement> elements() {
		List<HudElement> out = new ArrayList<>(ORDER.size());
		for (Identifier id : ORDER) {
			HudElement element = ELEMENTS.get(id);
			if (element != null) {
				out.add(element);
			}
		}
		return out;
	}
}