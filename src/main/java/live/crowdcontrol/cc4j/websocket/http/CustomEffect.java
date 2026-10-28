package live.crowdcontrol.cc4j.websocket.http;

import live.crowdcontrol.cc4j.websocket.payload.CCName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public final class CustomEffect {
	private final @NotNull CCName name;
	private final int price;
	private final @Nullable String description;
	private final @Nullable List<String> category;
	private final @Nullable String image;
	private final @Nullable CustomEffectDuration duration;
	private final @Nullable CustomEffectQuantity quantity;
	private final @Nullable Map<String, CustomEffectParameter> parameters;
	private final @Nullable Double sessionCooldown;
	private final @Nullable Double userCooldown;
	private final @Nullable Boolean inactive;

	public CustomEffect(
		@NotNull CCName name,
		int price,
		@Nullable String description,
		@Nullable List<String> category,
		@Nullable String image,
		@Nullable CustomEffectDuration duration,
		@Nullable CustomEffectQuantity quantity,
		@Nullable Map<String, CustomEffectParameter> parameters,
		@Nullable Double sessionCooldown,
		@Nullable Double userCooldown,
		@Nullable Boolean inactive
	) {
		if (name == null || price < 1) {
			throw new IllegalArgumentException("Must provide non-null name and ≥1 price");
		}

		this.name = name;
		this.price = price;
		this.description = description;
		this.category = category;
		this.image = image;
		this.duration = duration;
		this.quantity = quantity;
		this.parameters = parameters;
		this.sessionCooldown = sessionCooldown;
		this.userCooldown = userCooldown;
		this.inactive = inactive;
	}

	public @NotNull CCName name() {
		return name;
	}

	public int price() {
		return price;
	}

	public @Nullable String description() {
		return description;
	}

	public @Nullable List<String> category() {
		return category;
	}

	public @Nullable String image() {
		return image;
	}

	public @Nullable CustomEffectDuration duration() {
		return duration;
	}

	public @Nullable CustomEffectQuantity quantity() {
		return quantity;
	}

	public @Nullable Map<String, CustomEffectParameter> parameters() {
		return parameters;
	}

	public @Nullable Double sessionCooldown() {
		return sessionCooldown;
	}

	public @Nullable Double userCooldown() {
		return userCooldown;
	}

	public @Nullable Boolean inactive() {
		return inactive;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		CustomEffect that = (CustomEffect) obj;
		return Objects.equals(this.name, that.name) &&
			this.price == that.price &&
			Objects.equals(this.description, that.description) &&
			Objects.equals(this.category, that.category) &&
			Objects.equals(this.image, that.image) &&
			Objects.equals(this.duration, that.duration) &&
			Objects.equals(this.quantity, that.quantity) &&
			Objects.equals(this.parameters, that.parameters) &&
			Objects.equals(this.sessionCooldown, that.sessionCooldown) &&
			Objects.equals(this.userCooldown, that.userCooldown) &&
			Objects.equals(this.inactive, that.inactive);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, price, description, category, image, duration, quantity, parameters, sessionCooldown, userCooldown, inactive);
	}

	@Override
	public String toString() {
		return "CustomEffect[" +
			"name=" + name + ", " +
			"price=" + price + ", " +
			"description=" + description + ", " +
			"category=" + category + ", " +
			"image=" + image + ", " +
			"duration=" + duration + ", " +
			"quantity=" + quantity + ", " +
			"parameters=" + parameters + ", " +
			"sessionCooldown=" + sessionCooldown + ", " +
			"userCooldown=" + userCooldown + ", " +
			"inactive=" + inactive + ']';
	}
}
